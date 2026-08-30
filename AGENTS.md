# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project overview

**MokshaSetu** ("bridge to liberation"), shipped under the product name **Virasat**, is an Android app that helps Indian families plan and settle a loved one's digital legacy. It is built for **Moonshot Avishkar 2026** and sits on the India Stack: Aadhaar, DigiLocker, Account Aggregator (DEPA), UDGAM/DEA, and DPDP Act §14 nominee provisions.

Everything is local and mocked. There is no backend, no network calls and no real India Stack integration — the four services in `data/services/` are deliberate simulations with coroutine delays so the demo feels live.

> **Deviation (user-directed, Aug 2026):** Aadhaar OTP verification now checks a hosted registry — FastAPI on Vercel backed by MongoDB Atlas (code in `server/`), reached over HTTPS via OkHttp. Everything else stays local and mocked.

## MVP scope: two journeys behind a role chooser

The app opens on a role chooser that forks into two self-contained journeys. Only build what is listed here.

**Planner journey** — the person alive today, setting things up:

1. **Aadhaar login** — mock UIDAI OTP (any 12 digits, fixed OTP).
2. **Discovery** — mock Account Aggregator fetch animates the planner's bank, demat and insurance accounts in one at a time, with dormant accounts attributed to UDGAM / the DEA Fund. Two discovered assets deliberately have **no nominee**; that unclaimed-assets gap is the product's core insight and the UI must flag it in red.
3. **Assets** — sections per `AssetKind`, one nominee per asset, plus property papers the planner uploads (property cannot be auto-discovered).
4. **Digital vault** — platform credentials with a per-account after-death action: memorialise, delete, or transfer access.
5. **Nominees** — grouped by person, each expanding to the assets and accounts held in trust, with a rupee total.
6. **Last Wishes** — text/voice/video messages and unfulfilled wishes for heirs.

**Nominee journey** — the grieving heir:

1. **Aadhaar login** — proves who the claimant is. They never use the deceased person's credentials.
2. **Death certificate** — registration number, state, deceased name, document picker.
3. **Registry verification** — mock state Civil Registration System check with both a verified and a mismatch path.
4. **Entitlements** — only that nominee's monetary assets, property papers and digital accounts.
5. **Claim** — per-asset and claim-all, behind a confirmation sheet, then an institution-attributed credit and a reference number.
6. **Safety net** — a static page covering the cases where a nominee is unreachable, estranged, deceased or never named.

### Deliberately out of scope

Saarthi AI, the Two-Tier Trigger, WhatsApp outreach, and all cryptography (`Shamir`, `VaultCipher`) were removed in the two-journey rebuild. Do not reintroduce them without an explicit decision. Digital-vault credentials are held in plain process memory, which is acceptable only because every value is fixture data.

## The one guardrail that cannot be broken

**We verify; we never move money or take title.** A claim sends a *verified claim packet* to the institution, and the UI must attribute the credit to that institution by name — "HDFC Bank credited ₹4,82,650 to your account", never "Virasat paid you". A nominee is a trustee, not an owner (DPDP §14), and the UI says so on both the planner and nominee sides.

## Product vocabulary

Use these terms consistently in code, UI strings and docs:

- **Planner** — the person alive today who sets up the vault.
- **Nominee** — the trusted person named against an asset; a trustee, not an owner. Also the role name for the claimant in the second journey.
- **Vault**, **Wish**, **Entitlements**, **Claim packet** — as described above.

## Repo layout

```
app/src/main/java/MoonshotApp/MokshaSetu/
  MainActivity.kt              # single-activity entry point
  data/
    Models.kt                  # domain types, no behaviour
    Fixtures.kt                # all demo data (names, institutions, amounts)
    Entitlements.kt            # pure entitlement filtering, unit-tested
    Money.kt                   # Indian digit grouping for rupees
    DemoRepository.kt          # singleton + mutableStateListOf state holder
    services/                  # AadhaarAuth, AccountDiscovery, DeathRegistry, Settlement
  ui/
    VirasatApp.kt              # Dest sealed class + hand-rolled back stack
    Components.kt              # shared composables
    DomainLabels.kt            # enum to emoji/string mapping
    screens/                   # planner journey + role chooser + safety net
    screens/nominee/           # nominee journey
    theme/                     # colours, typography
docs/prototype.html            # original pitch prototype (see caveat below)
gradle/libs.versions.toml      # dependency version catalog
```

`docs/prototype.html` is the source of truth for the **palette and visual language only**. Its journey is now stale: it still shows Saarthi, the Two-Tier Trigger and the WhatsApp outreach mock. Do not treat it as a spec for behaviour.

## Tech stack rules

- Kotlin + Jetpack Compose (Material 3) only. One `ComponentActivity`; no XML views, no Fragments, no third-party UI kits.
- All dependencies and plugin versions go through `gradle/libs.versions.toml`. Do not hardcode versions in build files or add dependencies without stating why in the PR/commit message.
- Navigation is a hand-rolled `mutableStateListOf<Dest>` back stack in `VirasatApp.kt`. Deliberate: Navigation-Compose is not worth a dependency for 14 destinations.
- State lives in the `DemoRepository` singleton with `mutableStateOf`/`mutableStateListOf`. No ViewModels yet.
- Package names stay under `MoonshotApp.MokshaSetu`.
- minSdk 24, targetSdk/compileSdk 37, Java 11 compatibility — don't raise these casually.

## Commands

Run these before finishing any change:

```bash
./gradlew assembleDebug       # must compile
./gradlew testDebugUnitTest   # unit tests must pass
./gradlew lint                # no new lint errors
```

AGP 9.x resolves a Java 25 toolchain. If Gradle cannot download one, install a JDK 25 or build from Android Studio.

## Design system (palette source: docs/prototype.html)

CSS variables from the prototype define the palette — mirrored in `ui/theme/Color.kt`:

| Token | Value | Use |
|---|---|---|
| `--navy` | `#0f1b3d` | primary surfaces, app bar |
| `--gold` | `#c9a24b` | brand accent, CTAs |
| `--gold-soft` | `#e7cf95` | accent on dark backgrounds |
| `--cream` | `#f7f4ec` | app background |
| `--paper` | `#fffdf8` | cards/sheets |
| `--muted` | `#6b7280` | secondary text |
| `--green` / `--red` | `#1f9d6b` / `#c14b4b` | success/danger states |

Typography: serif display headings (Georgia/`FontFamily.Serif`) over sans body text. The diya motif (`DiyaMark`) is the logo mark.

## Code conventions

- Keep code comment-free and self-documenting; comments only for non-obvious decisions.
- Composables: small, stateless where possible.
- **User-facing copy goes in `res/values/strings.xml`, never hardcoded in a composable.** Fixture *data* — people's names, institution names, amounts, state names — belongs in `data/Fixtures.kt`, not in strings.xml. The MVP launches in English; Hindi and one more language come later via standard resource qualifiers.
- Money is always rendered through `formatRupees()` so grouping stays Indian: `1800000` renders as `₹18,00,000`.
- Keep domain logic pure and outside composables where it can be unit-tested — see `Entitlements.kt`. Mock services take a delay parameter so tests can pass `0`.

## Security guardrails (non-negotiable)

- Never commit `local.properties`, keystores, signing configs, API keys, or tokens.
- Never commit real Aadhaar/DigiLocker/personal data — use obviously fake fixture data (`demo`, `test`, `example.com`). The demo Aadhaar numbers and death-certificate registration number in `Fixtures.kt` are invented.
- Because there is no encryption layer any more, never put a real credential in `Fixtures.kt`, even temporarily.
