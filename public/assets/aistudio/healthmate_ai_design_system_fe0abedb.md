---
name: "Botanical Clarity"
description: "A calm, trustworthy health and wellness design system built on crisp white surfaces, soft sage-mint containers, and clear, everyday language."
---

## Overview

Botanical Clarity is designed for **HealthMate AI – Your Personal Health & Wellness Guide**, an AI-powered health, skin, hair, and nutrition assistant. The interface feels like a calm, sunlit wellness clinic paired with a helpful everyday companion: uncluttered, reassuring, and easy to scan on a phone.

The design style blends **Minimalism** with **Modern Clinical Warmth**—generous white space, soft botanical green accents, rounded tactile cards, and plain-English guidance. Safety notices and medical disclaimers are clear and calm without causing alarm, while emergency symptoms use distinct high-contrast warning treatments.

## Colors

The palette pairs a deep botanical emerald (`#006445`) primary with soft mint-sage containers (`#C6EBD5`, `#F0F5EF`) over crisp white (`#FFFFFF`) and barely-tinted botanical paper (`#F6FBF5`).

- **Primary & Containers:** Used for key actions (asking the AI assistant, starting a symptom check, logging daily care) and soft sage highlights.
- **Surfaces:** Elevated white cards (`surface-container-lowest`) sit cleanly over soft botanical paper (`surface`) and gentle sage-tinted bands (`surface-container-low`).
- **Error & Urgent Care:** Reserved strictly for urgent medical warnings, emergency symptom alerts, and destructive actions.

```yaml
colors:
  light:
    primary: "#006445"
    on-primary: "#FFFFFF"
    primary-container: "#1A7F5A"
    on-primary-container: "#D3FFE6"
    secondary: "#446554"
    on-secondary: "#FFFFFF"
    secondary-container: "#C6EBD5"
    on-secondary-container: "#4A6B5A"
    tertiary: "#8F3E3E"
    on-tertiary: "#FFFFFF"
    tertiary-container: "#AD5655"
    on-tertiary-container: "#FFF2F1"
    error: "#BA1A1A"
    on-error: "#FFFFFF"
    error-container: "#FFDAD6"
    on-error-container: "#93000A"
    background: "#F6FBF5"
    on-background: "#181D1A"
    surface: "#F6FBF5"
    surface-dim: "#D7DBD6"
    surface-bright: "#F6FBF5"
    surface-container-lowest: "#FFFFFF"
    surface-container-low: "#F0F5EF"
    surface-container: "#EBEFEA"
    surface-container-high: "#E5E9E4"
    surface-container-highest: "#DFE4DE"
    on-surface: "#181D1A"
    surface-variant: "#DAE5DC"
    on-surface-variant: "#3E4943"
    outline: "#6E7A72"
    outline-variant: "#BEC9C1"
    inverse-surface: "#2D322E"
    inverse-on-surface: "#EEF2EC"
    inverse-primary: "#7DD8AD"
    shadow: "#000000"
    scrim: "#000000"
  dark:
    primary: "#7DD8AD"
    on-primary: "#003825"
    primary-container: "#1A7F5A"
    on-primary-container: "#D3FFE6"
    secondary: "#ABCFBA"
    on-secondary: "#163628"
    secondary-container: "#2F5040"
    on-secondary-container: "#9DC1AC"
    tertiary: "#FFB3B0"
    on-tertiary: "#5C181A"
    tertiary-container: "#AD5655"
    on-tertiary-container: "#FFF2F1"
    error: "#FFB4AB"
    on-error: "#690005"
    error-container: "#93000A"
    on-error-container: "#FFDAD6"
    background: "#101412"
    on-background: "#DFE4DE"
    surface: "#101412"
    surface-dim: "#101412"
    surface-bright: "#353A37"
    surface-container-lowest: "#0A0F0C"
    surface-container-low: "#181D1A"
    surface-container: "#1C211E"
    surface-container-high: "#262B28"
    surface-container-highest: "#313632"
    on-surface: "#DFE4DE"
    surface-variant: "#3E4943"
    on-surface-variant: "#BEC9C1"
    outline: "#88938B"
    outline-variant: "#3E4943"
    inverse-surface: "#DFE4DE"
    inverse-on-surface: "#2D322E"
    inverse-primary: "#006C4A"
    shadow: "#000000"
    scrim: "#000000"
```

## Typography

**Alata** brings a warm, approachable geometric voice to display and section headings. **Instrument Sans** handles all interface labels, symptom guidance, and care routines with crisp legibility at small mobile sizes. **Source Code Pro** is reserved for tabular health metrics, water intake counts, and dosage/reference codes.

```yaml
typography:
  display-lg:
    fontFamily: "Alata"
    fontSize: 40px
    fontWeight: 400
    lineHeight: 48px
    letterSpacing: -0.5px
  display-lg-mobile:
    fontFamily: "Alata"
    fontSize: 28px
    fontWeight: 400
    lineHeight: 34px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: "Alata"
    fontSize: 34px
    fontWeight: 400
    lineHeight: 42px
    letterSpacing: -0.25px
  headline-lg-mobile:
    fontFamily: "Alata"
    fontSize: 24px
    fontWeight: 400
    lineHeight: 30px
    letterSpacing: 0px
  headline-md:
    fontFamily: "Alata"
    fontSize: 20px
    fontWeight: 400
    lineHeight: 26px
    letterSpacing: 0px
  title-lg:
    fontFamily: "Instrument Sans"
    fontSize: 18px
    fontWeight: 600
    lineHeight: 24px
    letterSpacing: 0px
  title-md:
    fontFamily: "Instrument Sans"
    fontSize: 16px
    fontWeight: 600
    lineHeight: 22px
    letterSpacing: 0px
  body-lg:
    fontFamily: "Instrument Sans"
    fontSize: 16px
    fontWeight: 400
    lineHeight: 24px
    letterSpacing: 0px
  body-md:
    fontFamily: "Instrument Sans"
    fontSize: 14px
    fontWeight: 400
    lineHeight: 20px
    letterSpacing: 0px
  body-sm:
    fontFamily: "Instrument Sans"
    fontSize: 13px
    fontWeight: 400
    lineHeight: 18px
    letterSpacing: 0px
  label-lg:
    fontFamily: "Instrument Sans"
    fontSize: 14px
    fontWeight: 600
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: "Instrument Sans"
    fontSize: 12px
    fontWeight: 600
    lineHeight: 16px
    letterSpacing: 0.2px
  label-sm:
    fontFamily: "Instrument Sans"
    fontSize: 11px
    fontWeight: 500
    lineHeight: 14px
    letterSpacing: 0.25px
  mono-md:
    fontFamily: "Source Code Pro"
    fontSize: 13px
    fontWeight: 500
    lineHeight: 18px
    letterSpacing: 0px
```

## Layout & Spacing

The layout uses a **Fluid Grid** on mobile (`390px` base, `16px` horizontal page padding, `12px`–`16px` grid gutters) that scales up to a centered **Fixed-Max-Width Grid** (`1200px` max width, `24px` margins) on tablet and desktop. Every spacing step follows a strict `4px` / `8px` rhythm so cards, search bars, and bottom navigation bars align cleanly.

```yaml
spacing:
  gutter-xs: 4px
  gutter-sm: 8px
  gutter-md: 12px
  gutter-lg: 16px
  gutter-xl: 24px
  section: 32px
  page-margin: 16px
```

## Elevation & Depth

Hierarchy relies on **tonal layers** and **low-contrast outlines** rather than heavy drop shadows. White cards (`surface-container-lowest`) rest on the soft botanical background (`surface`) with `1px` `outline-variant` borders at subtle opacity. Floating elements—such as the sticky top search header and bottom navigation bar—use clean solid surfaces (`surface-container-lowest`) with a crisp top or bottom hairline divider, avoiding heavy blur or dramatic shadows.

## Shapes

Friendly, organic rounded corners soften clinical topics without feeling toy-like. Buttons and pills use full rounding (`9999px`), cards and input fields use generous `16px` (`lg`) or `24px` (`xl`) radii, and small badges or inner tiles step down to `12px` (`md`) or `8px` (`DEFAULT`) when nested inside padded containers.

```yaml
rounded:
  sm: 4px
  DEFAULT: 8px
  md: 12px
  lg: 16px
  xl: 24px
  full: 9999px
```

## Motion & Micro-interaction

Motion is calm, natural, and responsive. Button presses, chip selections, and water-intake increments respond within `120ms`, while sheets, photo upload drawers, and AI response cards enter smoothly over `240ms` with gentle deceleration. Nothing bounces or loops continuously.

```yaml
motion:
  state-change:
    duration: 120ms
    delay: 0ms
    timingFunction: ease-out
  surface-enter:
    duration: 240ms
    delay: 0ms
    timingFunction: cubic-bezier(0.2, 0, 0, 1)
  surface-exit:
    duration: 160ms
    delay: 0ms
    timingFunction: cubic-bezier(0.3, 0, 1, 1)
```

## Voice & Content Strategy

- **Tone of voice:** Warm, clear, and reassuring—never alarmist, overly technical, or salesy. Write in simple everyday English ("Gentle skin care for breakouts" rather than "Dermatological sebaceous intervention").
- **Capitalisation:** Sentence case for headings, buttons, navigation labels, and cards.
- **Safety & Boundaries:** Always frame guidance as educational information, never a medical diagnosis or prescription. Clearly highlight red-flag symptoms that need urgent medical care.
- **Button and action labels:** Action-oriented verb phrases (`Check symptoms`, `Upload skin photo`, `Log 1 glass`, `Ask HealthMate`).
- **Numbers & units:** Use `tabular-nums` for water glasses (`6 / 8 glasses`), streaks (`5 days`), and routine step counts.

## Components

Component implementations live as individual HTML sticker sheet files in
`components/`. Each file contains the canonical, styled HTML components with
`<!-- COMPONENT: name -->` comment markers. Read each file before use.

- **Alert** → `@components/alert.html` — `alert-info`
- **Button** → `@components/button.html` — `button-primary`, `button-icon`, `button-with-icon`
- **Card** → `@components/card.html` — `card-elevated`, `card-action`, `card-interactive`, `card-interactive-2`
- **Chip** → `@components/chip.html` — `chip-action`, `chip-input`, `chip-with-icon`
- **Searchbar** → `@components/searchbar.html` — `searchbar-standard`, `searchbar-with-suggestions`
