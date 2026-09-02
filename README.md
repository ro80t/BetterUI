# BetterUI

[![Build Status](https://github.com/ro80t/better-ui/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/ro80t/better-ui/actions/workflows/build.yml?query=branch:main)

A client-side Mixin-based Minecraft mod that improves the vanilla UI/UX.

---

## Features

- **Durability show** — draws the remaining durability of a damaged item as a small number in
  the top-left corner of its slot icon (hotbar, inventory, anywhere an item is drawn), colored the
  same as vanilla's own durability bar. Implemented on every loader by injecting into the single
  vanilla method that already draws the stack-count/durability-bar overlay (`DrawContext#drawStackOverlay`
  / `#drawItemInSlot` on Fabric, `GuiGraphics#renderItemDecorations` on NeoForge/Forge), so it
  covers every screen automatically instead of having to hook each screen individually.
- **Armor durability HUD** ([curseforge.com/.../durability-show](https://www.curseforge.com/minecraft/mc-mods/durability-show)-style)
  — a small panel in the bottom-right corner of the screen listing each equipped armor piece's icon
  and remaining durability (`current/max`), colored the same as vanilla's own durability bar. Hidden
  automatically while any screen (inventory, chat, pause menu, ...) is open. Backed by the
  `durabilityHudEnabled` config key — see [Configuration](#configuration) below.
- **Pause menu "Durability HUD: ON/OFF" button** — toggles the armor durability HUD without
  leaving the game. Pressing it flips `durabilityHudEnabled` and immediately rewrites the config
  file (via `BetterUiMod.toggleDurabilityHud()`), so the change also survives a restart.

---

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
show" feature above, implemented as a Mixin on all four modules. The pause-menu button is instead
implemented the way that's idiomatic per loader: Fabric and Forge both use a Mixin injected into
the vanilla pause-menu screen class, while NeoForge uses its own `ScreenEvent.Init.Post` (simpler,
and doesn't need raw Mixin for something this small). Both approaches are real, working examples —
swap in your own feature in the same spot.

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

## Configuration

- `application.properties` — mod name/id/version/vendor metadata shared by every module, expanded
  into `fabric.mod.json` / `neoforge.mods.toml` / `mods.toml` (Forge) at build time.
- `gradle/libs.versions.toml` — dependency version catalog (Minecraft/Yarn/Loader/Fabric API/
  NeoForge/Loom versions, etc). Forge's own Minecraft/mappings version is pinned directly in
  `forge/common/build.gradle` and `build-logic/.../buildlogic.java-forge-conventions.gradle`
  instead, since ForgeGradle's version-catalog integration is limited.

Before publishing, choose a license (see below) and add real mod icon artwork at
`common/impl/src/main/resources/icon.png` (currently a 1x1 placeholder).

### Mod config file

Each loader writes/reads `<game config dir>/betterui.json` on startup (Fabric:
`FabricLoader.getConfigDir()`; NeoForge/Forge: `FMLPaths.CONFIGDIR`). A fresh install creates it
with the defaults on first launch:

```json
{
  "durabilityHudEnabled": true
}
```

You can toggle `durabilityHudEnabled` either by hand-editing this file (restart required to pick
up the change), or in-game via the "Durability HUD: ON/OFF" button on the pause menu, which flips
the value and rewrites the file immediately — no restart needed. The loading/saving logic lives in
`com.ro80t.betterui.impl.config.ConfigIo` (loader-agnostic, in `common/impl`); the in-game toggle
is `BetterUiMod.toggleDurabilityHud()`. Add new fields to `com.ro80t.betterui.impl.config.Config`
and wire up a similar toggle the same way. There's no full in-game settings *screen* (yet) — just
this one button plus the JSON file.

## License

TODO — no license has been chosen yet. The mod is not licensed for redistribution until one is added.
