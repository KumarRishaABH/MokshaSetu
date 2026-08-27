# MokshaSetu 🪔

**Virasat — Digital Legacy, Honoured.** An Android app that helps Indian families plan and settle a loved one's digital legacy — built for Moonshot Avishkar 2026 on the India Stack (Aadhaar · DigiLocker · Account Aggregator · UDGAM · DPDP §14).

## The problem

One death in India triggers three silent crises: assets go unclaimed because nobody knew they existed, credentials die with the person, and grieving families navigate every institution alone. Virasat closes that gap from both ends.

## Two journeys

The app opens on a role chooser. Everything behind it is local and mocked — no backend, no network calls.

### 🌱 I am planning my legacy

| Step | What happens |
|---|---|
| Aadhaar login | Mock UIDAI OTP — the consent handle for discovery |
| Discovery | A mock Account Aggregator fetch animates your bank, demat and insurance accounts in one by one; dormant money is attributed to UDGAM / the DEA Fund |
| Assets | One nominee per asset, with the ones that have **no nominee** flagged in red |
| Digital vault | Credentials plus a per-account wish: memorialise, delete, or transfer access |
| Nominees | Grouped by person, each showing what they hold in trust and its rupee total |
| Last wishes | Messages and unfulfilled wishes, sealed until death is verified |

### 🕊️ I am a nominee · I have lost someone

| Step | What happens |
|---|---|
| Aadhaar login | You prove who *you* are — you never use the deceased person's password |
| Death certificate | Registration number, state, deceased name, document picker |
| Registry check | Mock state Civil Registration System verification, with a real mismatch path |
| Entitlements | Only your share: monetary assets, property papers, digital accounts |
| One-tap claim | A confirmation sheet, then each institution credits you directly, with a reference number |
| Safety net | What happens when a nominee is unreachable, estranged, deceased or was never named |

## The guardrail

**We verify; we never move money or take title.** A claim sends a verified claim packet — your identity, the state's death record, the nomination on file — to the institution. The institution credits you. Money never passes through Virasat, and a nominee is a trustee, not an owner (DPDP Act 2023 §14).

## Clone & open

### Prerequisites

- Android Studio Ladybug+ (or any IntelliJ IDE with the Android plugin)
- Android SDK 37 installed via SDK Manager
- JDK 25 — AGP 9.x resolves a Java 25 toolchain and will try to download one if it is missing

### Steps

```bash
git clone https://github.com/KumarRishaABH/MokshaSetu.git
cd MokshaSetu
```

**Android Studio (recommended):** `File → Open` → select the cloned `MokshaSetu` folder → let Gradle sync → pick a device/emulator → `Run ▶`.

**Command line / any IDE:**

```bash
./gradlew assembleDebug        # compile
./gradlew testDebugUnitTest    # unit tests
./gradlew lint                 # lint checks
```

An APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

### Demo credentials

Both login screens show the credentials on screen and fill them when tapped:

| | Aadhaar | OTP |
|---|---|---|
| Planner (Anjali Sharma) | `9012 3456 7890` | `123456` |
| Nominee (Rohan Sharma) | `7845 1290 3366` | `123456` |

Death certificate: registration `MH/PUN/2026/0148923`, state Maharashtra. Any other number is rejected, so the mismatch path is demonstrable.

## Tech

- Kotlin + Jetpack Compose (Material 3), single-activity, no third-party UI kits
- Hand-rolled `mutableStateListOf<Dest>` back stack; state in a `DemoRepository` singleton
- Four mock services (`AadhaarAuth`, `AccountDiscovery`, `DeathRegistry`, `Settlement`) with coroutine delays and injectable timings for tests
- Gradle Kotlin DSL with version catalog (`gradle/libs.versions.toml`)
- minSdk 24 · targetSdk 37 · Java 11 compatible

## A note on the prototype

[`docs/prototype.html`](docs/prototype.html) is the original pitch prototype. It remains the reference for the **palette and visual language**, but its journey is stale: it still shows the Saarthi AI guide, the Two-Tier Trigger and the WhatsApp outreach mock, all of which were dropped in the two-journey rebuild.

Agents: see [AGENTS.md](AGENTS.md) before contributing.
