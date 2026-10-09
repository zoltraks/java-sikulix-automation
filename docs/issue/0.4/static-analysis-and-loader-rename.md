# Static Analysis and Loader Rename

**Status:** Done

**Kind:** Engineering

## Intent

Two related improvements reported from IDE diagnostics:

- An unused local variable (`found` in `parseScenarioScript`) was only visible as an IDE warning - the build prints nothing about dead code.
- The class name `ScenarioLoader` is longer than needed inside a package whose purpose is already scenario handling.

## Scope

- Wire a source-level static analyzer into the Maven build so warnings such as unused locals are printed during `mvn package`.
- Remove the unused `found` variable from `parseScenarioScript`.
- Rename `logic.ScenarioLoader` to `logic.Loader` (file, class, and call sites).
- Update project documentation that references the old name where the reference describes current state.

## Plan

1. Remove `boolean found` from `parseScenarioScript`.
2. Rename `ScenarioLoader` to `Loader` across `src/main/java`.
3. Add `maven-pmd-plugin` with a focused ruleset covering unused locals, parameters, fields, and methods - bound to `package` so violations print during the normal build without failing it.
4. Build and run the `work/config.json` smoke scenario.

## Definition of Done

- `mvn -T 1C package` succeeds and the PMD report prints as part of the build output.
- No `ScenarioLoader` references remain in `src/`.
- The `work/config.json` scenario runs unchanged.

## Risk

Low - a rename and a reporting-only plugin. The ruleset is deliberately narrow to avoid flooding the build with style findings unrelated to dead code.

## Lifecycle Phase

Implementation

## Affected Documentation

- `docs/GUIDELINES.md` - verification procedure notes the analyzer.
- `docs/standard/java-maven-development.md` - static-analysis rule (synchronized to the seed).
