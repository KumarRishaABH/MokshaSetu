# MokshaSetu 🪔

**Virasat — Digital Legacy, Honoured.** An Android app that helps Indian families plan and settle a loved one's digital legacy — built for Moonshot Avishkar 2026 on the India Stack (Aadhaar · DigiLocker · Account Aggregator · DPDP §14).

## The problem

One death in India triggers three silent crises: assets go unclaimed, credentials die with the person, and grieving families navigate institutions alone. MokshaSetu bridges that gap.

## MVP

| # | Pillar | What it does |
|---|---|---|
| 1 | **Legacy Vault** | Instructions, wishes & asset map — zero-knowledge with threshold/social recovery |
| 2 | **Nominees** | One trusted person per asset; trustee, not owner (DPDP §14) |
| 3 | **Last Wishes** | Text/voice/video messages and unfulfilled wishes for heirs |
| 4 | **Two-Tier Trigger** | Tier-1 protective actions vs. Tier-2 consequential actions with multi-factor proof + waiting period |
| 5 | **Saarthi AI** | Voice-first, grief-aware guide for the Survivor |

## Try the prototype

Open [`docs/prototype.html`](docs/prototype.html) in any browser — a self-contained interactive demo of the full journey (Planner setup → Saarthi outreach over WhatsApp → posthumous claim flow).

## Build the app

```bash
./gradlew assembleDebug        # compile
./gradlew testDebugUnitTest    # unit tests
./gradlew lint                 # lint checks
```

Requires JDK 17+ and Android SDK 37. Open the project in Android Studio for the best experience.

## Tech

- Kotlin + Jetpack Compose (Material 3), single-activity
- Gradle Kotlin DSL with version catalog (`gradle/libs.versions.toml`)
- minSdk 24 · targetSdk 37 · Java 11 compatible

Agents: see [AGENTS.md](AGENTS.md) before contributing.
