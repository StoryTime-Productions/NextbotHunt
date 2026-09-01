# Nextbot Hunt

A Paper plugin for the StoryTime SMP, inspired by Garry's Mod Nextbot maps: a
single relentless AI-controlled chaser hunts players who hide as disguised
props. Instant elimination on contact — survive the round timer to win.

Design docs and implementation plan live in
[`RalphDocs/NextbotHunt`](../../RalphDocs/NextbotHunt) (specs, roadmap,
implementation plan) — not duplicated here.

## Getting Started (Developers)

```
./gradlew build
```

Run `./gradlew spotlessApply` before committing (or run `set-hooks-path.sh`
once to install the pre-commit hook that does this automatically), then
drop the resulting shaded jar from `build/libs/` into a test server's
`plugins/` folder — see `RalphDocs/NextbotHunt/specs/iterative-test-server-deploy.md`
for the scripted local dev-server workflow.

## CI/CD

- **PR checks**: format, lint (Checkstyle), static analysis (SpotBugs),
  unit tests (JUnit 5 + MockBukkit), coverage gate (JaCoCo).
- **Conventional Commits**: enforced via commitlint on every push/PR to
  `main`.
- **Releases**: pushing a `v*` tag builds and opens a draft GitHub Release
  with auto-generated notes for human review before publishing.

See `RalphDocs/NextbotHunt/specs/ci-cd-and-branch-protection.md` for the
full rationale.
