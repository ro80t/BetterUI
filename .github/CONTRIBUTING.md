# Contributing to BetterUI

## Project layout

This repository follows a **single-branch, multi-version, multi-loader** layout, based on
[Akazukin-Team/Mod-Template-Repository](https://github.com/Akazukin-Team/Mod-Template-Repository)
and [Akazukin-Team/Base-Plugin](https://github.com/Akazukin-Team/Base-Plugin).

```
build-logic/            Gradle convention plugins shared by every module
common/
  api/                   Loader-agnostic interfaces (IBetterUiMod, IConfig, ...)
  impl/                  Loader-agnostic implementation (BetterUiMod, BuildData, Config)
fabric/
  common/                Fabric entrypoint + version-detection Mixin, shared by every Fabric version
  1201/                  Fabric module for Minecraft 1.20.1
  1218/                  Fabric module for Minecraft 1.21.8
neoforge/
  common/                NeoForge module for Minecraft 1.21.8 (ScreenEvent, no raw Mixin needed)
forge/
  common/                (Modern) Forge module for Minecraft 1.21.8 (Mixin + ScreenEvent)
```

Every module is built from the **same branch**. Each `fabric/<version>` (and, in the future,
`neoforge/<version>` / `forge/<version>`) module is its own Gradle subproject with its own
Minecraft/mappings/loader dependency versions, producing its own version-specific mod jar.
`fabric/common` holds the code that does not depend on a specific Minecraft version (currently:
detecting which Minecraft version is running at launch and picking the right per-version compat
layer — see `com.ro80t.betterui.compat.fabric.v1218.mixin.minecraft.MixinMain`).

Every loader (including NeoForge) has real, verified-compiling Mixin wiring — see the "durability
show" feature in the README, implemented as a Mixin on all four modules. The pause-menu button is
instead implemented the way that's idiomatic per loader: Fabric and Forge both use a Mixin injected
into the vanilla pause-menu screen class, while NeoForge uses its own `ScreenEvent.Init.Post`
(simpler, and doesn't need raw Mixin for something this small). Both approaches are real, working
examples — swap in your own feature in the same spot.

### Adding another Minecraft version

1. Copy `fabric/1218` to `fabric/<newversion>` and update its `build.gradle` dependency versions
   (Minecraft, Yarn, Fabric Loader, Fabric API — see `gradle/libs.versions.toml`).
2. Rename the Java packages (`com.ro80t.betterui.compat.fabric.v<newversion>`) and the mixin/access
   widener resource file names (`betterui.fabric.v<newversion>.*`).
3. Register the new module in the root `settings.gradle`.
4. Port (or reimplement) the concrete UI mixins for the new version's mappings — Mixin targets
   are tied to a specific Minecraft version's class/method names, so this step cannot be
   automated away.

### About very old versions (e.g. 1.7.10)

Fabric Loader only supports Minecraft 1.14 and newer, and NeoForge only supports 1.20.1 and
newer. The `forge/common` module here uses **modern** Forge (ForgeGradle 7, official mappings,
Java 21) and only goes back as far as Forge itself keeps building — it does not reach 1.7.10
either. Minecraft 1.7.10 predates all three of these toolchains — Mixin support there requires
the legacy Forge/ForgeGradle 2.x toolchain (MCP mappings, Java 8 only), which is a fundamentally
different build system from the Loom / NeoForge Gradle / ForgeGradle 7 used here. It is not wired
into this repository; it would need to be added as a separate, independent module/toolchain if
truly required.

---

## Prerequisites

- JDK 21 (the Gradle build will attempt to auto-provision one via the
  [Foojay toolchain resolver](https://github.com/gradle/foojay-toolchains) if none is found)
- Git

## Building

```bash
./gradlew build
```

## Running the client for development

```bash
./gradlew :fabric:1218:runClient
./gradlew :fabric:1201:runClient
./gradlew :neoforge:common:runClient
./gradlew :forge:common:runClient
```

### IntelliJ IDEA

Open the repository root as a Gradle project (`File > Open`, pick the folder with `settings.gradle`).
IntelliJ will pick up the pinned JDK 21 automatically from `gradle/gradle-daemon-jvm.properties`.
Four ready-made run/debug configurations are checked in under `.idea/runConfigurations/` and will
show up in the run configuration dropdown after the Gradle sync finishes:

- **Fabric 1.20.1 Client**
- **Fabric 1.21.8 Client**
- **NeoForge Client**
- **Forge Client**

Each just runs that module's `runClient` Gradle task, so **Debug** works out of the box too.

---

## Build-time configuration

- `application.properties` — mod name/id/version/vendor metadata shared by every module, expanded
  into `fabric.mod.json` / `neoforge.mods.toml` / `mods.toml` (Forge) at build time.
- `gradle/libs.versions.toml` — dependency version catalog (Minecraft/Yarn/Loader/Fabric API/
  NeoForge/Loom versions, etc). Forge's own Minecraft/mappings version is pinned directly in
  `forge/common/build.gradle` and `build-logic/.../buildlogic.java-forge-conventions.gradle`
  instead, since ForgeGradle's version-catalog integration is limited.

The mod is MIT-licensed (see [LICENSE](../LICENSE)). Before publishing, add real mod icon artwork
at `common/impl/src/main/resources/icon.png` (currently a 1x1 placeholder).

## Extending the mod config

The loading/saving logic lives in `com.ro80t.betterui.impl.config.ConfigIo` (loader-agnostic, in
`common/impl`). To add a new on/off setting:

1. Add a boolean field (with a default) to `com.ro80t.betterui.impl.config.Config`.
2. Add a `BetterUiMod.ToggleSetting` entry for it in `BetterUiMod.toggleSettings()`, pointing at
   the new field's getter/setter.

Every loader's BetterUI settings screen (opened via the "BetterUI Settings" pause-menu button)
builds its button list from `toggleSettings()` automatically, so no per-loader UI code needs to
change. The screen itself is a small vanilla `Screen` subclass named `BetterUiSettingsScreen`:
shared across Forge/NeoForge via `common:mcoverlay` (+ `common:mcoverlay-v1218` for 1218), and kept
as a separate copy per Fabric version (matching how `MixinGameMenuScreen`, which opens it, is
already per-version) since the `ButtonWidget`/`Text` construction API differs across Fabric eras.
