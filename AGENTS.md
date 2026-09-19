# BetterUI — agent notes

A client-side Mixin-based Minecraft mod (durability/FPS/coordinates/keystrokes
HUD overlays + a settings/position-editor screen), shipped separately for
Fabric, Forge, and NeoForge across many Minecraft versions from one Gradle
multi-project build. See `README.md` for the user-facing feature list.

## Module map

- `common:api` — `IBetterUiMod`/`IConfig` marker interfaces only. No logic.
- `common:impl` — loader-agnostic logic: `BetterUiMod` (mod-wide static
  state + init), `Config`/`HudLayout`/`ConfigIo` (Lombok `@Getter@Setter`
  POJOs, JSON-serialized), `ClickPerSecondTracker`, `BuildData`.
- `common:mcoverlay` — vanilla-only HUD overlay/screen classes (`FpsOverlay`,
  `CoordinatesOverlay`, `ArmorDurabilityOverlay`, `KeystrokesOverlay`,
  `BetterUiSettingsScreen`, `BetterUiPositionEditorScreen`, mixins), compiled
  against pre-1.21.8 mappings. Used via `forge:common`/`neoforge:*` (≤1.21.4)
  and `fabric:common-drawcontext` (Fabric ≥1.20.1).
- `common:mcoverlay-v1218` — **byte-identical in intent**, recompiled
  against 1.21.8 mappings because `GuiGraphics.drawString(...)` and
  `GuiGraphics.pose()` changed signature/type in that version. Used only by
  `forge:1218`/`neoforge:1218`. When you touch one copy, check whether the
  other needs the same change.
- `fabric:common` (+ `common-legacy`) — Fabric `DrawContext`/render-API glue
  shared by every Fabric version; `common-drawcontext`/`common-drawcontext-v1218`
  mirror the `mcoverlay`/`mcoverlay-v1218` split for the same 1.21.8-mappings
  reason, Fabric side.
- `forge:common`, `neoforge:common` — shared Forge/NeoForge glue for their
  older, still-mutually-compatible versions.
- `forge:1201/1211/1214/1218`, `neoforge:1204/1211/1214/1218`,
  `fabric:1165/1182/1192/1201/1211/1214/1218` — one module per shipped
  Minecraft version. Module name encodes the version: `1218` → `1.21.8`
  (`"1" + 2-digit minor + patch`).

`application.properties` at the repo root is the single source of truth for
mod id/name/version/vendor — every convention plugin under `build-logic/`
reads it directly (`new Properties().load(...)`, not the version catalog).
Bump the version there, nowhere else.

## Build system

Convention plugins live in `build-logic/src/main/groovy/buildlogic.*.gradle`
and are composed per module (see any module's `plugins {}` block). Loader
versions/deps are centralized in `gradle/libs.versions.toml`.

**Known-good `gradle.properties` settings — do not casually change:**
- `org.gradle.parallel = false`. ForgeGradle's run tasks resolve other
  projects' `runtimeClasspath` outside a registered task input; Gradle
  rejects that under parallel execution ("attempted without an exclusive
  lock"). Flipping this back to `true` reintroduces an intermittent
  `runClient` failure.

**Forge/NeoForge 1218 dev-run classpath gotchas** (1.20.5+ Forge/NeoForge use
JPMS-ish module layering that legacy 1201/1211/1214 don't — these only apply
to the `1218` modules):
- `forge/1218/build.gradle`: `common:api`/`common:impl`/`common:mcoverlay-v1218`
  are `compileOnly`, not `implementation`, and a `mergeDevRunClasses` task
  copies their compiled output into this project's own merged directory
  (`build/sourceSets/main`, per `merge-source-sets` in `gradle.properties`)
  before any `run*` task. FML only ever treats that one directory as "the
  mod file" for dev runs (`minecraft.runs.client.mods{}` has no effect here —
  ForgeGradle 7's client run never reads the token it builds from that
  config); anything else lands as a separate library jar in a different
  module layer, and cross-referencing a Minecraft type from both sides
  throws `VerifyError: Bad type on operand stack`. Using `implementation`
  *and* the merge together instead throws a JPMS split-package
  `ResolutionException` (same package exported by two modules) — pick one.
- `neoforge/1218/build.gradle`: every source set is registered under the
  `"betterui"` mod id via `runs.client.modSources.add('betterui', …)`
  (the *named* overload), not the bare `modSources(...)` varargs. Without
  a name, NeoForge's dev launcher can't find resources that live in another
  project's sourceSet — specifically `betterui.mcoverlay.mixins.json`,
  which lives in `common:mcoverlay-v1218`, not `neoforge:1218`'s own
  sources — and mixin init throws `MixinInitialisationError`. Older
  NeoForge modules (1204/1211/1214) currently use the plain, unnamed
  `modSource project.sourceSets.main` and haven't shown this problem; apply
  the same named-`modSources.add` fix there too if they ever do.
- Forge's run configs pin `minHeapSize`/`maxHeapSize` (`1G`/`2G`) in
  `buildlogic.java-forge-conventions.gradle`; without it G1's default heap
  request can exceed what's actually available and `runClient` dies with
  `insufficient memory for the Java Runtime Environment`.

If `runClient` starts failing again for one of these loaders, check whether
these settings survived — they're easy to lose in a `git reset` since they
were, at various points, only staged locally. See the `run-dev-client` skill
for the full verification procedure.

Mixin config resources follow `betterui.<module-suffix>.mixins.json`
(e.g. `betterui.fabric.v1218.mixins.json`, `betterui.mcoverlay.mixins.json`)
— grep for the existing name before inventing a new one for a new module.

## Adding a feature

See the `add-hud-feature` skill for the full per-module checklist (Config
field → HudLayout → overlay class ×N loader copies → settings screen wiring
→ mixin, if any). The short version: a new on/off HUD element is a `Config`
boolean + `HudLayout` field (`common:impl`), one `ToggleSetting` entry in
`BetterUiMod.toggleSettings()`, and matching overlay/render code duplicated
across every `mcoverlay`/`mcoverlay-v1218`/`fabric:common-drawcontext*` copy
that needs it (see the module map above for which loaders share which copy).

## Release/publish flow

See the `publish-release` skill. Short version: bump
`application.properties`, cut a GitHub release with a `v`-prefixed tag,
`.github/workflows/ci.yml`'s `publish` job (`Kir-Antipov/mc-publish`) does
the rest. `modrinth-id` must be Modrinth's base62 **project ID**, not its
slug (a slug's hyphen isn't valid base62 and fails with `Invalid character
'-' in base62 encoding`) — the ID lives in the project's Settings page.

## Conventions

- Lombok (`@Getter`/`@Setter`/`@Slf4j`/`@UtilityClass`) is used freely in
  `common:impl`; match that style there rather than hand-rolling
  accessors.
- Comments in this codebase are substantial Javadoc explaining *why*
  (a version-mapping quirk, a module-layering constraint, a rejected
  simpler approach) — not restating *what* the code does. Match that bar:
  skip comments on obvious code, write one when a future reader would
  otherwise have to rediscover a non-obvious constraint the hard way (this
  file exists because several of those constraints already cost a full
  session to rediscover once).
- **Never commit or push on your own initiative** — not after finishing a
  fix, not because "the work is done," not even if asked to commit in a
  previous turn of the same conversation. Only `git commit`/`git push` when
  the user invokes the `/auto-commit-push` or `/open-pr` command in that
  turn. Any other request to save work — "commit this", "push it up" — do
  through one of those two commands rather than running `git commit`/
  `git push` directly, so the same rules (attribution, scope) apply
  consistently.
- Git commits: this repo's owner does not want Claude's name/email
  attached to commits (no `Co-Authored-By: Claude` trailer) — commit as
  the configured git user only, every time.
- Prefer the smallest fix that addresses the actual root cause; this is a
  hobby mod with a large build matrix, not a place for speculative
  abstraction — see `ponytail` guidance if that skill is loaded.
