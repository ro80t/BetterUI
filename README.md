# BetterUI

[![Build Status](https://github.com/YOUR_ACCOUNT/better-ui/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/YOUR_ACCOUNT/better-ui/actions/workflows/build.yml?query=branch:main)

A client-side Mixin-based Minecraft mod that improves the vanilla UI/UX.

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
  common/                NeoForge module for Minecraft 1.21.8
```

Every module is built from the **same branch**. Each `fabric/<version>` (and, in the future,
`neoforge/<version>`) module is its own Gradle subproject with its own Minecraft/Yarn/Loader
dependency versions, producing its own version-specific mod jar. `fabric/common` holds the code
that does not depend on a specific Minecraft version (currently: detecting which Minecraft
version is running at launch and picking the right per-version compat layer — see
`org.betterui.compat.fabric.v1218.mixin.minecraft.MixinMain`).

### Adding another Minecraft version

1. Copy `fabric/1218` to `fabric/<newversion>` and update its `build.gradle` dependency versions
   (Minecraft, Yarn, Fabric Loader, Fabric API — see `gradle/libs.versions.toml`).
2. Rename the Java packages (`org.betterui.compat.fabric.v<newversion>`) and the mixin/access
   widener resource file names (`betterui.fabric.v<newversion>.*`).
3. Register the new module in the root `settings.gradle`.
4. Port (or reimplement) the concrete UI mixins for the new version's mappings — Mixin targets
   are tied to a specific Minecraft version's class/method names, so this step cannot be
   automated away.

### About pre-Fabric versions (e.g. 1.7.10)

Fabric Loader only supports Minecraft 1.14 and newer, and NeoForge only supports 1.20.1 and
newer. Minecraft 1.7.10 predates both — Mixin support there requires the legacy
Forge/ForgeGradle 2.x toolchain (MCP mappings, Java 8 only), which is a fundamentally different
build system from Fabric Loom / NeoForge Gradle used here. It is not wired into this repository;
it would need to be added as a separate, independent module/toolchain if truly required.

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
```

---

## Configuration

- `application.properties` — mod name/id/version/vendor metadata shared by every module.
- `gradle/libs.versions.toml` — dependency version catalog (Minecraft/Yarn/Loader/Fabric API/
  NeoForge/Loom versions, etc).

Before publishing, replace the `YOUR_ACCOUNT` placeholders in `application.properties`,
`README.md`, and the loader `fabric.mod.json` / `neoforge.mods.toml` files, choose a license, and
add real mod icon artwork at `common/impl/src/main/resources/icon.png` (currently a 1x1 placeholder).

## License

TODO — no license has been chosen yet. The mod is not licensed for redistribution until one is added.
