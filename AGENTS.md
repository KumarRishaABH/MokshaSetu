# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project overview

**MokshaSetu** ("bridge to liberation") is an Android app that helps Indian families plan and settle a loved one's digital legacy. The product is built for **Moonshot Avishkar 2026** and sits on the India Stack: Aadhaar, DigiLocker, Account Aggregator (DEPA), UDGAM/DEA, and DPDP Act §14 nominee provisions.

The interactive pitch prototype lives at `docs/prototype.html` — open it in a browser to see the intended UX (login, vault, nominees, wishes, Saarthi AI chat, WhatsApp outreach, posthumous claim flow).

## MVP scope (only build what's listed here)

1. **Legacy Vault** — instructions, wishes & asset map. Zero-knowledge + threshold crypto / social recovery; the platform never holds usable credentials alone.
2. **Nominees** — one trusted person per asset; nominee is a trustee, not owner (DPDP §14).
3. **Last Wishes** — text/voice/video messages plus unfulfilled wishes for heirs.
4. **Two-Tier Trigger** — Tier-1 protective actions (fast, reversible, low-proof); Tier-2 consequential actions (DigiLocker certificate + Aadhaar + nominee co-auth + waiting period). We verify; we never move money or take title.
5. **Saarthi AI** — voice-first, grief-aware guide; retrieval-grounded, human-in-the-loop for every consequential step.

Anything outside these five pillars is out of scope until the MVP ships.

## Product vocabulary

Use these terms consistently in code, UI strings, and docs:

- **Planner** — the person alive today who sets up the vault. Proactive, younger, digitally native. Acquired via the free emotional product (wishes/messages).
- **Survivor** — the grieving heir (often 55+, low digital ability). Reached via Saarthi + trusted institutions.
- **Vault**, **Nominee**, **Wish**, **Tier-1/Tier-2 trigger**, **Saarthi** — as defined above.

## Repo layout

```
app/src/main/java/MoonshotApp/MokshaSetu/   # Android app source (Kotlin)
  MainActivity.kt                           # single-activity entry point
  ui/theme/                                 # Compose theme (colors, type)
docs/prototype.html                         # interactive UX prototype (self-contained HTML)
gradle/libs.versions.toml                   # dependency version catalog
```

## Tech stack rules

- Kotlin + Jetpack Compose (Material 3) only. One `ComponentActivity`; no XML views, no Fragments, no third-party UI kits.
- All dependencies and plugin versions go through `gradle/libs.versions.toml`. Do not hardcode versions in build files or add dependencies without stating why in the PR/commit message.
- Package names stay under `MoonshotApp.MokshaSetu`.
- minSdk 24, targetSdk/compileSdk 37, Java 11 compatibility — don't raise these casually.
- Prefer simple, local-first implementations for the MVP (in-memory or DataStore); no backend unless it's required by a pillar above.

## Commands

Run these before finishing any change:

```bash
./gradlew assembleDebug        # must compile
./gradlew testDebugUnitTest    # unit tests must pass
./gradlew lint                 # no new lint errors
```

## Design system (source of truth: docs/prototype.html)

CSS variables from the prototype define the palette — mirror them in `ui/theme/Color.kt`:

| Token | Value | Use |
|---|---|---|
| `--navy` | `#0f1b3d` | primary surfaces, app bar |
| `--gold` | `#c9a24b` | brand accent, CTAs |
| `--gold-soft` | `#e7cf95` | accent on dark backgrounds |
| `--cream` | `#f7f4ec` | app background |
| `--paper` | `#fffdf8` | cards/sheets |
| `--muted` | `#6b7280` | secondary text |
| `--green` / `--red` | `#1f9d6b` / `#c14b4b` | success/danger states |

Typography: serif display headings (Georgia) over sans body text. The diya motif is the logo mark.

## Code conventions

- Keep code comment-free and self-documenting; comments only for non-obvious crypto/security decisions.
- Composables: small, stateless where possible, prefixed with nothing special — follow existing file style.
- User-facing strings go in `res/values/strings.xml`, never hardcoded. The MVP launches in English; Hindi and one more language come later via standard resource qualifiers.

## Security guardrails (non-negotiable)

- Never commit `local.properties`, keystores, signing configs, API keys, or tokens.
- Never commit real Aadhaar/DigiLocker/personal data — use obviously fake fixture data (`demo`, `test`, `example.com`).
- Any code touching vault encryption or the two-tier trigger requires an explanatory note in the commit message about the threat model.
