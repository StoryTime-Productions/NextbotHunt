# Nextbot Hunt

A Paper plugin for the StoryTime SMP, inspired by Garry's Mod Nextbot maps: a
single relentless AI-controlled chaser hunts players. Instant elimination on
contact — survive the round timer to win.

## Getting Started (Developers)

```
./gradlew build
```

Run `./gradlew spotlessApply` before committing (or run `set-hooks-path.sh`
once to install the pre-commit hook that does this automatically), then
drop the resulting shaded jar from `build/libs/` into a test server's
`plugins/` folder.

## CI/CD

- **PR checks**: format, lint (Checkstyle), static analysis (SpotBugs),
  unit tests (JUnit 5 + MockBukkit), coverage gate (JaCoCo).
- **Conventional Commits**: enforced via commitlint on every push/PR to
  `main`.
- **Releases**: pushing a `v*` tag builds and opens a draft GitHub Release
  with auto-generated notes for human review before publishing.
