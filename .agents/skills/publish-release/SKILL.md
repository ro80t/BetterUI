---
name: publish-release
description: Cut a new BetterUI release across every loader/version and publish to CurseForge/Modrinth, or diagnose a failed publish CI run. Use when asked to bump the version, cut a release, or fix a mc-publish/CurseForge/Modrinth CI failure.
---

# Publishing a release

## Flow

1. Bump `version` in `application.properties` at the repo root — the only
   place it's defined; every loader's build.gradle reads it from there.
2. Commit, push, then cut a GitHub release with a `v`-prefixed tag (e.g.
   `v0.1.2`) matching the bumped version. `.github/workflows/ci.yml`'s
   `release: types: [published]` trigger picks it up.
3. The `build` job builds every loader/version and uploads the jars as a
   workflow artifact; `upload-assets` attaches them to the GitHub release;
   `publish` runs `Kir-Antipov/mc-publish` once per loader/version matrix
   entry, uploading to both CurseForge and Modrinth.

## `mc-publish` gotchas already hit in this repo

- **`modrinth-id` must be the Modrinth **project ID** (8-char base62,
  e.g. `LZBaPnjw`), not the project's slug.** mc-publish passes it straight
  through as `project_id` in the version-create request body, which
  Modrinth validates as strict base62 there (unlike URL path params, which
  accept either id or slug). A slug's hyphen isn't valid base62, and fails
  with `HttpError: 400 ... Invalid character '-' in base62 encoding`. Find
  the real ID on the project's Settings page (or `...` menu → Copy ID) —
  the public `GET /v2/project/<slug>` API 404s for unlisted/draft projects,
  so don't rely on it to double-check.
- **`github.event.release.tag_name` is `v0.1.2`, not `0.1.2`.** Don't feed
  it straight into `name`/`version` if you want the published version
  string to read without the `v`. This repo strips it in a step before the
  publish step:
  ```yaml
  - id: version
    env:
      TAG: ${{ github.event.release.tag_name }}
    run: echo "version=${TAG#v}" >> "$GITHUB_OUTPUT"
  ```
  then references `${{ steps.version.outputs.version }}` in the `mc-publish`
  `name`/`version` inputs.
- Per-platform input overrides exist (`curseforge-name`, `modrinth-name`,
  `curseforge-version`, …) and take priority over the generic `name`/
  `version` inputs for that one platform. Passing an explicit empty string
  (`""`) for a `*-name` override tells that platform to derive the display
  name from the uploaded file itself instead of using any custom name.

## Diagnosing a new publish failure

Read the actual `HttpError` body in the Action log first — CurseForge and
Modrinth both return a `description` field naming the specific invalid
field, which is almost always more precise than the generic 400. Don't
guess at a fix without it.
