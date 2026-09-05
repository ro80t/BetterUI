# BetterUI

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
- **FPS display** — draws the current FPS as a small number in the top-left corner of the screen.
  Hidden automatically while any screen (inventory, chat, pause menu, ...) is open. Backed by the
  `fpsDisplayEnabled` config key — see [Mod config file](#mod-config-file) below.
- **BetterUI settings screen** — a "BetterUI Settings" button on the pause menu opens a dedicated
  screen with one ON/OFF button per feature (currently Durability Item, Durability Armor, and FPS
  Display). Toggling a button flips its config value and immediately rewrites the config file, so
  the change survives a restart without leaving the game.

---

## Mod config file

Each loader writes/reads `<game config dir>/betterui.json` on startup (Fabric:
`FabricLoader.getConfigDir()`; NeoForge/Forge: `FMLPaths.CONFIGDIR`). A fresh install creates it
with the defaults on first launch:

```json
{
  "durabilityHudEnabled": true,
  "durabilityShowEnabled": true,
  "fpsDisplayEnabled": true
}
```

You can toggle either setting by hand-editing this file (restart required to pick up the change),
or in-game via the "BetterUI Settings" button on the pause menu, which flips the value and
rewrites the file immediately — no restart needed.

## Credits

- [Durability Show](https://www.curseforge.com/minecraft/mc-mods/durability-show) — inspiration for
  this mod's durability-display features (the per-slot number and the bottom-right armor panel),
  with more planned in the same spirit. All due respect to its original author — go check out the
  original if you haven't.
- [Akazukin-Team/Mod-Template-Repository](https://github.com/Akazukin-Team/Mod-Template-Repository)
  and [Akazukin-Team/Base-Plugin](https://github.com/Akazukin-Team/Base-Plugin) — referenced for
  this mod's multi-version, multi-loader project structure. Respect to the Akazukin Team for the
  template.

## License

TODO — no license has been chosen yet. The mod is not licensed for redistribution until one is added.

---

Want to build the mod yourself, add a Minecraft version, or extend a feature? See
[.github/CONTRIBUTING.md](.github/CONTRIBUTING.md).
