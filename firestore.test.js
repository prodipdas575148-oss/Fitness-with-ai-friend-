const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validProfilePayload(uid, now = new Date(Date.now() - 5000)) {
  return {
    userId: uid,
    displayName: "Alice Wellness",
    planTier: "free",
    waterGlasses: 5,
    waterGoal: 8,
    steps: 4200,
    stepsGoal: 8000,
    streakDays: 5,
    activeCheckIn: "Feeling steady",
    createdAt: now,
    updatedAt: now,
  };
}

test("Unauthenticated user cannot read or write profiles or subcollections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID))
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("bpReadings").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("routineTasks").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("consultNotes").get()
  );
});

test("User profile: owner create, valid partial updates, and negative boundary checks", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  // Valid create matching HealthRepository.ensureProfileAndStarterRoutines
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID, created))
  );

  // Unlisted top-level /users query must fail
  await assertFails(aliceDb.collection("users").get());

  // Valid partial updates matching HealthRepository
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      waterGlasses: 6,
      steps: 4700,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      activeCheckIn: "Skin calmer",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      planTier: "plus_49",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Shadow update with ghost field must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Tampered future timestamp must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      waterGlasses: 7,
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Invalid planTier enum must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      planTier: "enterprise_free",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Immutable createdAt mutation must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Cross-user isolation: Bob cannot read or modify Alice's profile or subcollections", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminDb = context.firestore();
    const now = new Date(Date.now() - 10000);
    await adminDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID, now));
    await adminDb.collection("users").doc(ALICE_UID).collection("bpReadings").doc("bp_1").set({
      userId: ALICE_UID,
      systolic: 118,
      diastolic: 76,
      pulse: 70,
      category: "Normal",
      createdAt: now,
      updatedAt: now,
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      waterGlasses: 1,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("bpReadings").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("bpReadings").doc("bp_1").get()
  );
});

test("Subcollections: bpReadings, routineTasks, consultNotes mirror Kotlin queries and bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);

  // 1. bpReadings create (with and without optional pulse) & ordered list query
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bpReadings").doc("bp_1").set({
      userId: ALICE_UID,
      systolic: 118,
      diastolic: 76,
      pulse: 72,
      category: "Normal",
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bpReadings").doc("bp_2").set({
      userId: ALICE_UID,
      systolic: 126,
      diastolic: 82,
      category: "Elevated",
      createdAt: created,
      updatedAt: created,
    })
  );
  // Reject @DocumentId "id" key inside body
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("bpReadings").doc("bp_bad").set({
      id: "bp_bad",
      userId: ALICE_UID,
      systolic: 120,
      diastolic: 80,
      category: "Normal",
      createdAt: created,
      updatedAt: created,
    })
  );
  // Reject out-of-bounds systolic
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("bpReadings").doc("bp_oob").set({
      userId: ALICE_UID,
      systolic: 500,
      diastolic: 80,
      category: "Urgent",
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("bpReadings")
      .orderBy("createdAt", "desc")
      .limit(20)
      .get()
  );

  // 2. routineTasks create, toggle done, delete, and ordered list query
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("routineTasks").doc("task_1").set({
      userId: ALICE_UID,
      title: "Gentle cleanser + SPF 50",
      subtitle: "8:00 AM · Morning barrier care",
      period: "morning",
      done: false,
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("routineTasks").doc("task_1").update({
      done: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("routineTasks")
      .orderBy("createdAt", "asc")
      .get()
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("routineTasks").doc("task_1").delete()
  );

  // 3. consultNotes create and ordered list query
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("consultNotes").doc("note_1").set({
      userId: ALICE_UID,
      problemKey: "chest_tightness",
      summary: "Mild chest tightness after stairs today",
      durationLabel: "Today",
      specialist: "Cardiology / Urgent Evaluation",
      doctorName: "Dr. Rajesh Kulkarni, DM",
      createdAt: created,
      updatedAt: created,
    })
  );
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("consultNotes")
      .orderBy("createdAt", "desc")
      .limit(10)
      .get()
  );
});
