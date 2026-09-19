---
name: run-dev-client
description: Launch BetterUI's dev client for a given loader/Minecraft version and diagnose a runClient failure. Use whenever asked to run, test, or debug a launch failure for the Fabric/Forge/NeoForge dev client, or when a runClient error needs root-causing rather than guessing.
---

# Running the dev client

## Task name

`./gradlew :<loader>:<module>:runClient`, e.g.:

```
./gradlew :forge:1218:runClient
./gradlew :neoforge:1214:runClient
./gradlew :fabric:1201:runClient
```

`<module>` is the directory name under that loader (`1218`, `1214`, …), not
the Minecraft version string. See `AGENTS.md`'s module map for the full
list, or `.idea/runConfigurations/*.xml` for the matching IntelliJ configs.

## Before assuming a fresh bug: check known-fragile settings

These have broken and been re-fixed before (see `AGENTS.md` → "Build
system"). If `runClient` fails, check these first — a `git reset`/revert is
usually why they went missing again, not new bit rot:

1. `gradle.properties`: `org.gradle.parallel` must be `false`.
2. `build-logic/.../buildlogic.java-forge-conventions.gradle`: the `client`
   run has `minHeapSize.convention '1G'` / `maxHeapSize.convention '2G'`.
3. `forge/1218/build.gradle`: `common:api`/`common:impl`/
   `common:mcoverlay-v1218` are `compileOnly`, and a `mergeDevRunClasses`
   task exists and is wired as a dependency of every `run*` task.
4. `neoforge/1218/build.gradle`: `runs.client.modSources.add('betterui', …)`
   lists every relevant source set, not the bare unnamed `modSources(...)`.

## Diagnosing a failure from the log, in order

Run in the background and tail the log — the process launches a real
Minecraft client window, so don't block waiting for it to exit on its own;
watch for a terminal log line instead (`BUILD SUCCESSFUL`/`FAILED`, a
crash-report header, or `insufficient memory`).

1. **`Resolution of the configuration '...runtimeClasspath' was attempted
   without an exclusive lock`** → `org.gradle.parallel` got flipped back to
   `true`. Set it to `false`.
2. **`insufficient memory for the Java Runtime Environment` /
   `os::commit_memory ... failed`** → Forge run heap size isn't pinned (see
   #2 above), or you're launching from a sandboxed tool session with its own
   memory ceiling unrelated to the project (check free RAM/pagefile on the
   real host before assuming a project bug — this has happened: the host
   had gigabytes free while the sandboxed child process still failed a
   *smaller* allocation than what later succeeded outside the sandbox).
3. **`VerifyError: Bad type on operand stack` / "Type X is not assignable
   to Y"** (Forge 1218 only) → `common:*` modules are on the dev-run
   classpath as separate library jars instead of merged into
   `forge/1218/build/sourceSets/main`. Check `mergeDevRunClasses` exists,
   runs before `runClient`, and the three dependencies are `compileOnly`.
4. **`java.lang.module.ResolutionException: Modules betterui and X export
   package ... `** (Forge 1218 only) → the opposite mistake: a `common:*`
   module is both `implementation` (→ still resolves onto the classpath as
   its own jar) *and* merged by `mergeDevRunClasses` into the same
   directory, so the same package is claimed by two modules. Switch that
   dependency to `compileOnly`.
5. **`MixinInitialisationError` / "The specified resource
   'betterui...mixins.json' was invalid or could not be read"** (NeoForge
   1218 only) → the mixin config's owning module isn't registered under the
   `"betterui"` mod id in `runs.client.modSources`. Use the named
   `modSources.add('betterui', sourceSet1, sourceSet2, ...)` form, not the
   unnamed one — the unnamed form silently does nothing for the `client`
   run type in this ForgeGradle version (confirmed via `--info`: the
   `source_roots` token it builds is never referenced by the `client` run's
   launch args).

If none of these match, get the *first* exception in the log (not the
"Cowardly refusing to send event ... to a broken mod state" noise that
follows a mod-load failure) and work from there — don't pattern-match past
what's actually in the log.

## Verifying a fix actually worked

`BUILD SUCCESSFUL` alone isn't enough — the Gradle task can succeed even
when the *game* crashed during mod loading (ForgeGradle doesn't fail the
task for a caught mod-loading exception in some configurations). Confirm the
log shows the mod actually initializing (`BetterUI initializing...` /
`BetterUI initialized`) with no `VerifyError`/`ResolutionException`/
`MixinInitialisationError` afterward, and ideally that a world loaded
(`Saving and pausing game...` or similar) before the client closes normally
(`[Render thread/INFO] [minecraft/Minecraft]: Stopping!` is a clean
shutdown, not a crash).
