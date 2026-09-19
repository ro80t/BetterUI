---
description: Commit the current changes and push directly to the current branch
---

Commit and push the current working-tree changes, following the git
workflow in the system prompt (review `git status`/`git diff` first, stage
by explicit path rather than `-A`, double-check nothing suspicious like a
secret is being staged). Write a normal commit message describing the
change — do **not** add a `Co-Authored-By: Claude` trailer or any other
Claude attribution; commit as the repo's configured git user only.

If `$ARGUMENTS` names specific files/paths, stage only those; otherwise use
judgment about what belongs in one commit (see `AGENTS.md` if unsure about
which untracked files, if any, are actually part of this change vs.
unrelated in-progress work that shouldn't be swept in).

After committing, `git push` to the current branch's existing upstream. If
there's no upstream yet, ask before setting one up rather than guessing
where this should go.
