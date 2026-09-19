---
name: minecraft-modding
description: General Java/Gradle/Minecraft modding knowledge for Fabric, Forge, and NeoForge, and how IntelliJ fits in. Use for anything touching loader Gradle plugins (Loom/ForgeGradle/NeoGradle), mixins, mappings, multi-version/multi-loader project structure, or an IntelliJ sync/run-config problem in a Minecraft mod project. For BetterUI-specific procedures, prefer run-dev-client / add-hud-feature / publish-release instead — this skill is the general background those build on.
---

# Minecraft modding (Java/Gradle/Fabric/Forge/NeoForge/IntelliJ)

## The three loaders, at a glance

| | Fabric | Forge | NeoForge |
|---|---|---|---|
| Gradle plugin | Fabric Loom | ForgeGradle (FG) | NeoGradle (NG) |
| Mappings | Yarn (or Mojmap) | Mojang official (≥1.17), MCP (older) | Mojang official |
| Mod metadata | `fabric.mod.json` | `META-INF/mods.toml` | `META-INF/neoforge.mods.toml` |
| Dev-run config | `loom { runs { client {...} } }` | `minecraft { runs { client {...} } }` | `runs { client {...} }` |

NeoForge is a fork of Forge — its Gradle DSL and mod-metadata format look
similar but are **not** interchangeable; don't copy a Forge snippet into a
NeoForge build.gradle (or vice versa) assuming it'll just work.

**Mappings are not portable across Minecraft versions.** The same source
file compiled against 1.20.1's mappings and reused unmodified against
1.21.8 can fail at runtime with `NoSuchMethodError`/`VerifyError` even
though it compiles fine standalone, because a method's signature or return
type changed between mapping generations (e.g. `GuiGraphics.drawString`
returning `int` vs `void`). When a multi-version project shares code across
versions, expect either genuine source duplication per mapping generation,
or a compatibility shim layer — not a single copy reused as-is.

## Gradle specifics

- Always use the wrapper (`./gradlew`/`gradlew.bat`), never a global Gradle
  install — loader plugin versions and the Gradle version itself are
  pinned together on purpose; a mismatched Gradle can silently break Loom/
  FG/NG.
- Check the toolchain (`java.toolchain.languageVersion` or equivalent) for
  the actual required JDK — Minecraft ≤1.20.4 wants JDK 17, ≥1.20.5 wants
  JDK 21. A stale Gradle daemon left over from a JDK switch causes
  confusing failures; `./gradlew --stop` clears it.
- **Loom project dependencies**: `project(':other-module')` resolves to
  that module's default consumable artifact — for a Loom project that's
  the `remapJar` output (intermediary-mapped), which is binary-incompatible
  with named-mapped classes you compiled against. Reference
  `project(':other-module').sourceSets.main.output` directly instead when
  you need the raw, named-mapped classes (e.g. sharing code between two
  Fabric version modules at dev time).
- **ForgeGradle/NeoGradle + `org.gradle.parallel = true`**: run tasks can
  resolve another project's `runtimeClasspath` outside a registered task
  input, which Gradle's parallel-execution locking rejects ("attempted
  without an exclusive lock"). If a loader's `runClient` fails this way
  intermittently, parallel builds are the first thing to suspect.
- **1.20.5+ Forge/NeoForge dev-run module layering**: newer FML uses
  JPMS-like module layers per "mod file." If a mod's code is split across
  several Gradle projects pulled in as plain `implementation project(...)`
  dependencies, the dev-run classpath can put them in a *different* module
  layer than the project's own compiled output — any Minecraft type
  referenced from both sides then throws `VerifyError: Bad type on operand
  stack`, even though the shipped, fully-merged production jar works fine.
  Fabric/Loom doesn't have this problem (no such module layering at dev
  time). The fix is always some form of "make the dev-run classpath match
  the shipped jar's single-module shape" — either merge the other
  projects' compiled output into the primary project's own output
  directory before `run*` tasks, or (NeoGradle) register every relevant
  source set under the mod's own id via the run's `modSources`.

## Mixins

- A mixin config JSON (`<modid>.mixins.json` or similar) must be
  *registered* somewhere the loader actually reads at dev-run time — a
  manifest attribute (`MixinConfigs` on Forge), a `[[mixins]]` entry
  (NeoForge's `.mods.toml`), or an array in `fabric.mod.json` — **and**
  the resource file itself must be reachable on the classpath the launcher
  actually assembles for dev. These are two independent failure points:
  registered-but-missing gives a distinct "resource … could not be read"
  error from registered correctly but the wrong sourceSet's output isn't
  on the classpath at all.
- `@Inject`/`@Redirect`/`@ModifyExpressionValue` targets are matched by
  exact method signature for that specific mapping generation — porting a
  mixin to a new Minecraft version means re-checking the target method's
  current signature, not just recompiling and hoping.
- Compatibility-level warnings (e.g. "higher than the maximum level
  supported by this version of mixin") are usually harmless noise from an
  older Mixin library version being more conservative than the actual JVM
  target — don't chase them unless something is actually broken.

## IntelliJ

- After changing anything Gradle-visible (a `build.gradle`, a convention
  plugin under `build-logic/`, `gradle.properties`, the version catalog),
  **reload the Gradle project** (the elephant/refresh icon, or "Reload All
  Gradle Projects"). IntelliJ's project model is a cached snapshot — CLI
  builds pick up changes immediately, the IDE won't until you reload, which
  looks like phantom red squiggles or stale run configs for no reason.
- Loom/FG/NG regenerate `.idea/runConfigurations/*.xml` on sync from each
  module's `runs {}` block. Don't hand-edit a generated run config expecting
  it to stick — change the Gradle `runs {}` block and re-sync instead.
- Settings → Build Tools → Gradle → "Gradle JVM" is IntelliJ's *own*
  setting for running Gradle itself, separate from the project's Java
  toolchain (which `./gradlew` resolves independently via the Foojay
  resolver or similar). A CLI-only build working while IntelliJ's sync
  fails (or vice versa) often traces back to this being set to a
  different/missing JDK than the toolchain expects.
- Lombok-generated methods (`@Getter`/`@Setter`/etc.) showing as
  unresolved in the editor despite compiling from the CLI means the Lombok
  plugin isn't installed or annotation processing isn't enabled (Settings
  → Build → Compiler → Annotation Processors → "Enable annotation
  processing").

## Debugging a build/run failure

Read the *first* exception in the log, not whatever noisy cascade follows
it (a failed mod load typically produces a wall of "Cowardly refusing to
send event ... to a broken mod state" lines that are symptoms, not the
cause). `--info`/`--stacktrace`/`--scan` surface the real command and
classpath when the top-level error message isn't enough to work from.
Verify a fix by actually running the task that failed, not by re-reading
the diff and assuming it's correct — a `BUILD SUCCESSFUL` on a run task can
still hide a mod that crashed after the JVM launched successfully.
