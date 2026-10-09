# Step Engine Refactoring Assessment

## Proposal Summary

The proposal at `docs/refactoring/0.3/refactoring-proposal.md` recorded nine
findings and planned three tidy steps (T1-T3) plus four structural steps
(S1-S4): remove commented-out code and dead code, disambiguate the two
`Settings` types, split `Flow.reset()`/`next()`, extract `Automation.step()`
into ordered `apply*` methods, unify `Step`'s three-place operation mapping,
and extract scenario loading from `App` into `logic.ScenarioLoader`.

Three defects were routed out of scope to future issues: the unreachable
`result.jump` assignment inside the `jump` step, `execute`'s missing
argument handling, and `toUpperCase()` without `Locale.ROOT`.

## Implementation Summary

All seven steps ran in plan order, each verified by
`mvn -T 1C package` with zero errors.

T1 removed the five commented-out blocks in `Main.java`, `App.java`
(three sites), and `Flow.java`. T2 deleted `Test.java`, `Single.java`,
and the unread `Result.delay` field. T3 fully qualified
`org.sikuli.basics.Settings` in `Automation` and `Flow` and dropped the
ambiguous imports.

S1 split `Flow.reset()` into `reset()`, `resetState()`, and
`resolveImagePath()`, and flattened the `next()` failure branch to a
single `return context.settings.isIgnore()` - provably identical because
`result.jump` is always null on a failure path.

S2 replaced the 166-line `Automation.step()` pipeline with a dispatcher
calling `applyPath`, `applyTiming`, `applySimilarity`, `applyPrint`,
`applyMouse`, `applyFind`, `applyClick`, `applyPopup`, `applyType`,
`applyPress`, `applyMessage`, `applyExecute`, `applyCondition`, and
`applyPause` in the original order. The three `FindFailed` operations
share a private `attempt` helper, and the routed `jump` defect was kept
verbatim inside `applyCondition`.

S3 replaced the `Step` switch and eighteen `toString()` blocks with a
single `OPERATIONS` table ordered by `toString()` emission order, so
parsing and serialization can no longer drift.

S4 moved `collectScenarioArray`, `readScenarioFromFile`, and
`parseScenarioScript` verbatim into `pl.alyx.robot.sikulix.logic.ScenarioLoader`
and resolved Finding 9: `initialize()` now throws
`new Exception("Failed to read configuration")` instead of a message-less
exception that the `Main` handler silently swallowed.

## Gap Analysis

No findings were dropped and none were added. Two naming choices differ
from the proposal sketches, without changing scope: the `when`/`then`/
`jump` blocks were grouped as one `applyCondition` method rather than
three, since they implement a single conditional-navigation effect, and
the proposal's `ScenarioLoader` sketch used the same moved code so no
gap exists there. The `Flow` failure-branch flattening is simpler than
the proposal's "three sequential returns" phrasing because analysis
showed `result.jump` is unreachable on failure.

## Verification Results

- `mvn -T 1C package` after every step - PASS, zero errors and zero
  warnings.
- JSON smoke `work/config.json` - PASS, Notepad opened, `HELLO` typed,
  tab closed without saving, verbose output identical.
- Script smoke `work/config-play.json` - PASS, same observable run via
  `notepad.script`.
- `--ignore` path - PASS, `Press failed for BOGUSKEY+X` printed and the
  scenario continued to the following `PRINT`.
- Ignore disabled - PASS, the scenario stopped at the error step.
- `git diff --check` - PASS.
- Automated tests - N/A, the repository has no test framework (recorded
  in the proposal as a verification constraint).
- SikuliX `find`/`click`/`popup` image paths - PARTIAL, verified by
  compilation and unchanged control flow only, per the proposal's stated
  limitation.

## Metrics Comparison

| Artifact                     | Before    | After     | Change |
|------------------------------|-----------|-----------|--------|
| `Automation.step()` body     | 166 lines | 34 lines  | -132   |
| `Automation.java` total      | 356 lines | 401 lines | +45    |
| `App.java`                   | 270 lines | 136 lines | -134   |
| `Step.java`                  | 199 lines | 160 lines | -39    |
| `Flow.java`                  | 125 lines | 120 lines | -5     |
| `ScenarioLoader.java`        | -         | 145 lines | new    |
| `Test.java`, `Single.java`   | 41 lines  | deleted   | -41    |
| Dead fields (`Result.delay`) | 1         | 0         | -1     |
| Commented-out code blocks    | 5         | 0         | -5     |

`Automation.java` grew by 45 lines because each extracted method carries
signature and whitespace boilerplate. That growth is the accepted
trade-off recorded in the proposal - a readable dispatcher plus fourteen
focused methods instead of one pipeline.

## Output Fidelity

The smoke runs produce identical stdout to the pre-refactoring
behaviour, including the verbose `Playing scenario` line, `TYPE
"HELLO"` log, and the `( Alt ) TYPE "#F4."` / `N` press sequences.
`Step.toString()` emits operations in the same order as before, so the
`.script` round-trip is unchanged. The one intentional output
difference is the failure path: a missing configuration file now also
prints a stack trace through the `Main` handler, replacing a silent
exit - this is the Finding 9 resolution the proposal's acceptance
criteria allowed.

## Behaviour Smoke Check

Observable behaviour is identical to the baseline for every verified
flow: JSON scenario execution, `.script` scenario execution, `PRESS`
with `Alt+F4`/`Alt+N` modifier specs, `PRINT` output, `EXECUTE`
launching `notepad.exe`, `PAUSE`/`DELAY` timing, `ignore` continuing
after a failed step, and non-ignore stopping at the first failure.
Step order, error text, and failure short-circuiting are preserved.

## Conclusion

PASS. All nine findings were addressed as proposed, verification
criteria met, and the three routed defects remain recorded for future
issues. Recommend writing the `jump`-step issue next, since the
refactored `applyCondition` method makes the unreachable assignment
visually obvious to the next reader.
