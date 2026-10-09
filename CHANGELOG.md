# Changelog

All notable changes to this project are documented in this file.

The format follows Keep a Changelog conventions: newest first, ISO
dates, and the change types Added, Changed, Deprecated, Removed, Fixed,
and Security.

## [0.3] - 2026-10-08

### Added

- `press` step operation sending real key presses with modifiers:
  symbol prefixes `!`/`@`/`^`/`$` and case-insensitive `Name+Key` forms
  such as `Alt+F4`, `Ctrl+Alt+G`, `^!X`. JSON accepts a single spec or
  an array, and script `PRESS` accepts plain, quoted, or multiple
  space-separated values.
- `--ignore` command line flag and `"ignore"` configuration field:
  step errors print and the scenario continues instead of stopping.
- `StringUtility.splitTokens` - whitespace split honoring double-quoted
  values with `""` escapes.
- `logic.ScenarioLoader` - scenario collection, file reading, and
  script parsing extracted from `App`.
- Repository documentation layer: `docs/GUIDELINES.md`,
  `docs/REFACTORING.md`, engineering standards under `docs/standard/`,
  document templates under `docs/template/`, and the `docs/issue/`,
  `docs/refactoring/`, `docs/review/` version-grouped conventions.
- `LICENSE.md`.

### Changed

- `Automation.step()` pipeline extracted into ordered `apply*` effect
  methods with a shared `FindFailed` helper.
- `Step` operation mapping unified into a single `OPERATIONS` table
  driving both `create()` and `toString()`.
- `Flow.reset()` split into `resetState()` and `resolveImagePath()`,
  and the `next()` failure branch flattened.
- `org.sikuli.basics.Settings` fully qualified where it shares a file
  with `structure.Settings`.

### Removed

- `Test.java` and `Single.java` demo classes.
- Dead `Result.delay` field.
- Leftover commented-out code blocks in `Main`, `App`, and `Flow`.

### Fixed

- A missing configuration file now reports
  `Failed to read configuration` instead of exiting silently.
