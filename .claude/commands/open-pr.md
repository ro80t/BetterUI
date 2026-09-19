---
description: Commit the current changes on a new branch, push it, and open a pull request
---

Open a pull request for the current working-tree changes, following the PR
workflow in the system prompt (review the full diff and commit history for
what's actually changing, not just the latest commit).

1. If currently on `master` (or another protected/shared branch), create a
   new branch first — name it after the change, don't reuse `master`.
2. Commit the changes (see the same commit-message rules as
   `/auto-commit-push`: no `Co-Authored-By: Claude` or other Claude
   attribution, commit as the repo's configured git user).
3. Push the branch with `-u`.
4. `gh pr create` with a concise title and a body covering what changed and
   why, plus a test-plan checklist if relevant. Do **not** add a "Generated
   with Claude Code" footer or any other Claude attribution to the PR body
   — keep it as a normal PR from the repo owner.

`$ARGUMENTS`, if given, is context for the PR title/description (e.g. an
issue number to link, or which change to scope the PR to if there are
multiple unrelated changes in the working tree — ask rather than guessing
if that's ambiguous).

Report the PR URL when done.
