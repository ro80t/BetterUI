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
  `durabilityHudEnabled` config key — see [Mod config file](#mod-config-file) below.
- **Pause menu "Durability HUD: ON/OFF" button** — toggles the armor durability HUD without
  leaving the game. Pressing it flips `durabilityHudEnabled` and immediately rewrites the config
  file, so the change also survives a restart.

---

## Mod config file

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
the value and rewrites the file immediately — no restart needed.

## License

TODO — no license has been chosen yet. The mod is not licensed for redistribution until one is added.

---

Want to build the mod yourself, add a Minecraft version, or extend a feature? See
[.github/CONTRIBUTING.md](.github/CONTRIBUTING.md).
