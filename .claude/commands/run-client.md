---
description: Launch BetterUI's dev client for one loader/module and verify it actually starts (not just BUILD SUCCESSFUL)
---

Run the dev client for `$ARGUMENTS` (a loader and module, e.g. `forge 1218`
or `fabric 1201` — if not given, ask which one). Follow the `run-dev-client`
skill: run `./gradlew :<loader>:<module>:runClient` in the background, tail
the log, and diagnose any of that skill's known failure signatures rather
than guessing. Confirm success by the criteria in that skill's "Verifying a
fix actually worked" section, not just a `BUILD SUCCESSFUL` line.
