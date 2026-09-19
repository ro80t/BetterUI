---
name: add-hud-feature
description: Add a new on/off HUD overlay or settings-screen toggle to BetterUI, or add a new field to an existing overlay's layout. Use when asked to add a new HUD element, a new config toggle, or extend the position editor / settings screen.
---

# Adding a HUD feature

BetterUI ships the same feature to many loaders/versions from duplicated
per-module copies, not a single shared renderer — there is no shortcut
around touching each copy. Work outward from `common:impl` (the one place
that's genuinely shared) to the render code (duplicated).

## 1. `common:impl` — config surface (shared, edit once)

In `common/impl/.../config/Config.java` (Lombok `@Getter@Setter`, matches
every existing field — copy the pattern exactly, including the Javadoc
explaining what the field does and how it's toggled):

```java
private boolean myFeatureDisplayEnabled = true;

private HudLayout myFeatureLayout = HudLayout.of(anchorX, anchorY, offsetX, offsetY);
```

If it has a position (most overlays do), also add a `resetMyFeatureLayout()`
following the existing `resetFpsLayout()`/etc. pattern, and call it from
`resetHudLayouts()`.

If it needs a settings-screen toggle button, add one `ToggleSetting` entry
to `BetterUiMod.toggleSettings()` in `common/impl/.../BetterUiMod.java` —
every loader's settings screen builds its button list from this, so this is
the only place a new toggle needs registering.

## 2. Overlay/render code — duplicated per module, not shared

Write the actual `render(GuiGraphics context)` (or `DrawContext` for
Fabric) method once, then copy it — with the version-specific API
adjustments each copy already has — into every module that needs it. Use
`AGENTS.md`'s module map to find which copies exist for a given loader
family, and diff two existing sibling classes (e.g.
`common/mcoverlay/.../FpsOverlay.java` vs
`common/mcoverlay-v1218/.../FpsOverlay.java`) to see exactly what changes
between mapping generations before writing the 1218 copy — don't guess at
the API delta.

Typical copy set for a new vanilla-rendered overlay:
- `common/mcoverlay/.../<Feature>Overlay.java`
- `common/mcoverlay-v1218/.../<Feature>Overlay.java` (1.21.8 mapping diffs)
- `fabric/common-drawcontext/.../<Feature>Overlay.java` (Fabric `DrawContext` API)
- `fabric/common-drawcontext-v1218/.../<Feature>Overlay.java`

Wire the render call into wherever the sibling overlays are called from in
each of those modules (a HUD render event/mixin — grep for how an existing
overlay like `FpsOverlay.render(...)` is invoked in that same module and
mirror it).

## 3. Settings/position-editor screens

If the feature needs a row in `BetterUiSettingsScreen` or
`BetterUiPositionEditorScreen`, those are also per-module copies (same set
as the overlay classes above) — add the new row/button to each copy,
following an existing row's construction exactly (label, getter/setter
wired to the new `Config`/`ToggleSetting` from step 1).

## 4. Verify

Compile everything first (`./gradlew build`) before trying a dev-run — a
missing `Config` field surfaces as a compile error in whichever overlay
class references it, across every module that has a copy. Then use the
`run-dev-client` skill to actually launch and eyeball the new element on at
least one Fabric, one Forge, and one NeoForge module (1218 ones exercise
the trickiest dev-run wiring — see that skill's gotcha list).
