package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class HealthRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun requireActiveUserId(expectedUid: String): String {
        val current = requireUserId()
        check(current == expectedUid) { "Signed-in account changed; refusing cross-user access." }
        return current
    }

    fun observeProfile(userId: String): Flow<UserWellnessProfile?> = flow {
        val uid = requireActiveUserId(userId)
        val docRef = db.collection("users").document(uid)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserWellnessProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else null
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.GET, docRef.path)
                    }
                    throw error
                }
        )
    }

    fun observeBpReadings(userId: String): Flow<List<BpReading>> = flow {
        val uid = requireActiveUserId(userId)
        val path = "users/$uid/bpReadings"
        emitAll(
            db.collection("users").document(uid).collection("bpReadings")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        BpReading::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeRoutineTasks(userId: String): Flow<List<RoutineTask>> = flow {
        val uid = requireActiveUserId(userId)
        val path = "users/$uid/routineTasks"
        emitAll(
            db.collection("users").document(uid).collection("routineTasks")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        RoutineTask::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    fun observeConsultNotes(userId: String): Flow<List<ConsultNote>> = flow {
        val uid = requireActiveUserId(userId)
        val path = "users/$uid/consultNotes"
        emitAll(
            db.collection("users").document(uid).collection("consultNotes")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ConsultNote::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val toLog = firestoreError ?: (error as? Exception)
                    if (toLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(toLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun ensureProfileAndStarterRoutines(userId: String, displayNameRaw: String?): Result<Unit> {
        var path = "users/$userId"
        return try {
            val uid = requireActiveUserId(userId)
            val profileRef = db.collection("users").document(uid)
            path = profileRef.path
            val snapshot = profileRef.get().await()
            if (!snapshot.exists()) {
                val safeName = displayNameRaw?.trim()?.take(100)?.ifBlank { "Wellness Member" }
                    ?: "Wellness Member"
                val profilePayload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "planTier" to "free",
                    "waterGlasses" to 5L,
                    "waterGoal" to 8L,
                    "steps" to 4200L,
                    "stepsGoal" to 8000L,
                    "streakDays" to 5L,
                    "activeCheckIn" to "Feeling steady",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                profileRef.set(profilePayload).await()

                val routinesRef = profileRef.collection("routineTasks")
                val existingTasks = routinesRef.limit(1).get().await()
                if (existingTasks.isEmpty) {
                    val starterTasks = listOf(
                        Triple("Gentle foam cleanser + SPF 50", "8:00 AM · Morning barrier care", "morning") to true,
                        Triple("2 glasses warm water & protein breakfast", "8:30 AM · Steady energy", "morning") to true,
                        Triple("Scalp brush massage (5 mins)", "7:30 PM · Evening circulation", "evening") to false,
                        Triple("Niacinamide + barrier cream", "9:30 PM · Evening repair", "evening") to false,
                        Triple("Omega-3 walnuts & soaked chia", "4:00 PM · Skin & hair nutrition", "nutrition") to true,
                        Triple("Screens off 30 mins before sleep", "10:15 PM · Restful recovery", "evening") to false
                    )
                    val batch = db.batch()
                    starterTasks.forEach { (meta, isDone) ->
                        val docRef = routinesRef.document()
                        batch.set(
                            docRef,
                            mapOf(
                                "userId" to uid,
                                "title" to meta.first,
                                "subtitle" to meta.second,
                                "period" to meta.third,
                                "done" to isDone,
                                "createdAt" to FieldValue.serverTimestamp(),
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                    }
                    batch.commit().await()
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun updateWaterAndSteps(
        userId: String,
        waterGlasses: Long,
        steps: Long
    ): Result<Unit> {
        val path = "users/$userId"
        return try {
            val uid = requireActiveUserId(userId)
            val clampedWater = waterGlasses.coerceIn(0L, 20L)
            val clampedSteps = steps.coerceIn(0L, 100000L)
            db.collection("users").document(uid)
                .update(
                    mapOf(
                        "waterGlasses" to clampedWater,
                        "steps" to clampedSteps,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateCheckIn(userId: String, checkInLabel: String): Result<Unit> {
        val path = "users/$userId"
        return try {
            val uid = requireActiveUserId(userId)
            val safeLabel = checkInLabel.trim().take(64).ifBlank { "Feeling steady" }
            db.collection("users").document(uid)
                .update(
                    mapOf(
                        "activeCheckIn" to safeLabel,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun updatePlanTier(userId: String, planTier: String): Result<Unit> {
        val path = "users/$userId"
        return try {
            val uid = requireActiveUserId(userId)
            require(planTier == "free" || planTier == "plus_49") {
                "Unsupported plan tier"
            }
            db.collection("users").document(uid)
                .update(
                    mapOf(
                        "planTier" to planTier,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun toggleRoutineTask(userId: String, taskId: String, done: Boolean): Result<Unit> {
        val path = "users/$userId/routineTasks/$taskId"
        return try {
            val uid = requireActiveUserId(userId)
            require(taskId.isNotBlank()) { "Task ID must not be blank" }
            db.collection("users").document(uid)
                .collection("routineTasks").document(taskId)
                .update(
                    mapOf(
                        "done" to done,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun addRoutineTask(
        userId: String,
        title: String,
        subtitle: String,
        period: String
    ): Result<Unit> {
        var path = "users/$userId/routineTasks"
        return try {
            val uid = requireActiveUserId(userId)
            val safeTitle = title.trim().take(120)
            require(safeTitle.isNotEmpty()) { "Habit title cannot be empty" }
            val safeSub = subtitle.trim().take(160)
            val safePeriod = if (period in listOf("morning", "evening", "nutrition")) period else "morning"
            val docRef = db.collection("users").document(uid)
                .collection("routineTasks").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "title" to safeTitle,
                    "subtitle" to safeSub,
                    "period" to safePeriod,
                    "done" to false,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteRoutineTask(userId: String, taskId: String): Result<Unit> {
        val path = "users/$userId/routineTasks/$taskId"
        return try {
            val uid = requireActiveUserId(userId)
            require(taskId.isNotBlank()) { "Task ID must not be blank" }
            db.collection("users").document(uid)
                .collection("routineTasks").document(taskId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun addBpReading(
        userId: String,
        systolic: Int,
        diastolic: Int,
        pulse: Int?,
        category: String
    ): Result<Unit> {
        var path = "users/$userId/bpReadings"
        return try {
            val uid = requireActiveUserId(userId)
            require(systolic in 40..300) { "Systolic must be between 40 and 300 mmHg" }
            require(diastolic in 30..200) { "Diastolic must be between 30 and 200 mmHg" }
            if (pulse != null) {
                require(pulse in 30..220) { "Pulse must be between 30 and 220 bpm" }
            }
            val safeCategory = category.trim().take(40).ifBlank { "Recorded" }
            val docRef = db.collection("users").document(uid)
                .collection("bpReadings").document()
            path = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "systolic" to systolic.toLong(),
                "diastolic" to diastolic.toLong(),
                "pulse" to pulse?.toLong(),
                "category" to safeCategory,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun addConsultNote(
        userId: String,
        problemKey: String,
        summary: String,
        durationLabel: String,
        specialist: String,
        doctorName: String
    ): Result<Unit> {
        var path = "users/$userId/consultNotes"
        return try {
            val uid = requireActiveUserId(userId)
            val safeKey = problemKey.trim().take(48).ifBlank { "general" }
            val safeSummary = summary.trim().take(500)
            require(safeSummary.isNotEmpty()) { "Please describe what you are feeling" }
            val safeDuration = durationLabel.trim().take(40).ifBlank { "Today" }
            val safeSpecialist = specialist.trim().take(100).ifBlank { "General Physician" }
            val safeDoctor = doctorName.trim().take(100).ifBlank { "On-Call Doctor" }

            val docRef = db.collection("users").document(uid)
                .collection("consultNotes").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "problemKey" to safeKey,
                    "summary" to safeSummary,
                    "durationLabel" to safeDuration,
                    "specialist" to safeSpecialist,
                    "doctorName" to safeDoctor,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }
}
