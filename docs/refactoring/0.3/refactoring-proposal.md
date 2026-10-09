# Consolidate Step Engine And Remove Dead Code Refactoring Proposal

## Problem

The step engine and its orchestration layer carry structural debt that has
started to hide real defects, plus dead code left behind by experiments.

`src/main/java/pl/alyx/robot/sikulix/logic/Automation.java` implements
every scenario operation as a 165-line sequential `if` pipeline in
`step()`, and an unreachable branch inside the `jump` block went unnoticed
inside that length.

`src/main/java/pl/alyx/robot/sikulix/structure/Step.java` maps each
operation three times - a field, a `create()` case, and a `toString()`
block - so adding `press` required three coordinated edits.

`src/main/java/pl/alyx/robot/sikulix/App.java` mixes CLI parsing,
configuration loading, scenario collection, and script parsing in one
class.

Dead code sits in production sources: `Test.java` and `Single.java` are
referenced only by a commented-out line in `Main.java` (`//new Test().run();`),
`Result.delay` is never read or written, and five
commented-out code blocks survive in `Main.java`, `App.java`, and
`Flow.java`.

The analysis traced the flow `Main -> App -> Flow -> Automation.step()`
for every operation, and checked each class for indirect references
(none outside the commented hook for `Test`/`Single`).

Files touched: `Automation.java`, `App.java`, `Flow.java`, `Step.java`,
`Result.java`, `Main.java`, and the deletion of `Test.java` and
`Single.java`.

## Goal

A step engine where each operation is added once, in one place, and where
`step()` reads as the ordered pipeline it contractually is.

`App` handles CLI and orchestration only, with scenario loading and
script parsing owned by a dedicated collaborator.

Production sources contain no dead classes, dead fields, or commented-out
code.

All observable behaviour is identical: same step execution order, same
stdout output, same script and JSON parsing, same error text.

## Findings

**Issue**

Dead classes `Test.java` and `Single.java` ship in the production jar.
`src/main/java/pl/alyx/robot/sikulix/Test.java` is a threading demo
(`Single.INSTANCE.increment()`), and `src/main/java/pl/alyx/robot/
sikulix/Single.java` exists only to serve it. The only reference in the
codebase is the comment `//new Test().run();` at `Main.java:7`.

**Impact**

Misleading inventory for every reader, dead weight in the artifact, and
demo code (`System.out`, `Scanner(System.in)`) that violates the logging
and stdin expectations of a CLI tool.

**Recommendation**

Delete both classes and the commented hook in `Main.java`. A grep across
`src/` confirms no other reference, and `Test` is not loaded reflectively
anywhere.

**Risk Level**

Minor

**Breaking Change Assessment**

No Breaking Change - the classes are unreachable.

**Example**

Before: `src/main/java/.../Test.java` (58 lines), `Single.java` (33
lines), `//new Test().run();` in `Main.java`. After: all three gone.

---

**Issue**

`Automation.step()` executes seventeen operations as sequential `if`
blocks over `step` fields, and three operations (`find`, `click`,
`popup`) repeat the same `try`/`catch(FindFailed)` shape. The `jump`
block contains `if (null == jump)` guarded by `isNotWhite(jump)` - code
that can never run and went unnoticed inside the method's length.

**Impact**

Each new operation lengthens the chain, review becomes scanning rather
than reading, and dead or contradictory branches hide among the noise
(the unreachable `jump` assignment is the proof).

**Recommendation**

Extract the conditional effects into named private methods in their
exact current order (`applyPath`, `applyTiming`, `applySimilarity`,
`applyPrint`, `applyMouse`, `applyFind`-family, `applyType`,
`applyPress`, `applyMessage`, `applyExecute`, `applyCondition`,
`applyPause`), with a shared `FindFailed` attempt helper for the three
screen lookups. Execution order is the contract - do not reorder.

**Risk Level**

Major

**Breaking Change Assessment**

No Breaking Change - method extraction preserves order and output.

**Example**

Before: `String find = this.step.find; if (isNotWhite(find)) { try { screen.find(find); } catch (FindFailed e) { ... return result; } }` repeated for `click` and `popup`. After: `if (!attempt("Find", find)) { return result; }` inside `applyFind`.

---

**Issue**

`Step` records each operation in three places: the field, a `create()`
switch case, and a `toString()` block. `press` needed all three edits.

**Impact**

Operation additions are mechanical but error-prone - a missing
`toString()` branch silently drops the operation from script output, and
there is no compile-time signal that the three places disagree.

**Recommendation**

Introduce a single static operation table in `Step` - keyword to
setter and script-writer - consumed by both `create()` and `toString()`,
with `press` keeping its `JsonElement` handling inside its table entry.
Table iteration order must match the current `toString()` emission
order.

**Risk Level**

Major

**Breaking Change Assessment**

No Breaking Change - the table reproduces existing parsing and
serialization verbatim.

**Example**

Before: `case "PRESS": ... break;` in `create()` plus a separate `PRESS`
block in `toString()`. After: one `Operation` entry `("PRESS",
setter, writer)` drives both.

---

**Issue**

`App` mixes argument parsing (`parseArgs`), configuration loading
(`readConfiguration`), scenario collection (`collectScenarioArray`,
`readScenarioFromFile`), and script parsing (`parseScenarioScript`)
into one 270-line class.

**Impact**

The script parser - the subtlest code in the repo, a multi-line regex -
is untestable in place and buried among lifecycle methods. Scenario
loading rules (extension probing `.json`/`.script`) live far from the
format they feed.

**Recommendation**

Move `collectScenarioArray`, `readScenarioFromFile`, and
`parseScenarioScript` to a dedicated `logic.ScenarioLoader` taking
`Context` for verbose access. `App` keeps `parseArgs`,
`readConfiguration`, `initialize`, `start`, `dump`.

**Risk Level**

Major

**Breaking Change Assessment**

No Breaking Change - same parse results, same verbose messages.

**Example**

Before: `App` owns `parseScenarioScript` with its pattern and `Step`
loop. After: `App` calls `new ScenarioLoader(context).collect()`.

---

**Issue**

`Flow.reset()` mixes three concerns: `State` recreation, SikuliX
`Settings.MinSimilarity`, and `ImagePath` resolution from configuration
and scenario paths plus the `assets` classpath entry. `Flow.next()`
carries a convoluted failure branch that prints `result.error` and then
re-tests `isNotEmpty(result.error)` to decide the stop.

**Impact**

Reset semantics are hard to verify at a glance, and the failure branch
reads as though `ignore` and empty-error cases were afterthoughts.

**Recommendation**

Split `reset()` into `resetState()` and `resolveImagePath()`. Rewrite
the failure branch as a flat sequence: print the error, return `true`
when `ignore`, otherwise return `false` - preserving the current
fall-through behaviour for a failure with empty error text.

**Risk Level**

Minor

**Breaking Change Assessment**

No Breaking Change - identical control flow, clearer shape.

**Example**

Before: nested `if (!result.success) { print error, if (ignore) return true, if (isNotEmpty) return false }`. After: three sequential returns.

---

**Issue**

Five commented-out code blocks remain from earlier experiments:
`Main.java:7` (`//new Test().run();`), `App.java:54-56` (old args
handling), `App.java:75-77` (verbose ImagePath print, now implemented
in `Automation`), `App.java:153-157` (manual array copy loop, replaced
by `toArray`), and `Flow.java:114-117` (ImagePath experiments).

**Impact**

Stale experiments read as intent - the `App` blocks especially look
like temporarily disabled behaviour rather than history.

**Recommendation**

Delete all five blocks. History lives in version control.

**Risk Level**

Minor

**Breaking Change Assessment**

No Breaking Change - comments carry no behaviour.

**Example**

Before: `// if (args.length > 0) { state.configurationFile = args[0]; }`. After: nothing.

---

**Issue**

`pl.alyx.robot.sikulix.structure.Settings` and
`org.sikuli.basics.Settings` are both in scope in `Flow.java` and
`Automation.java`, and `Settings.MinSimilarity` is the SikuliX type while
`context.settings` is the project type.

**Impact**

Two unrelated `Settings` types in one file invites the wrong import and
misread code - `Settings.MinSimilarity` looks like a project constant.

**Recommendation**

Use the fully qualified `org.sikuli.basics.Settings.MinSimilarity` at
both sites and drop the import, keeping the project type imported.

**Risk Level**

Minor

**Breaking Change Assessment**

No Breaking Change.

**Example**

Before: `import org.sikuli.basics.Settings;` then
`Settings.MinSimilarity = 0.7;`. After:
`org.sikuli.basics.Settings.MinSimilarity = 0.7;` with no import.

---

**Issue**

`Result.delay` is declared but never read or written - `context.state
.delay` carries the real delay state (verified across `src/`).

**Impact**

A dead field invites a writer that nothing reads.

**Recommendation**

Delete the field.

**Risk Level**

Suggestion

**Breaking Change Assessment**

No Breaking Change.

**Example**

Before: `public String delay;` in `Result.java`. After: field gone.

---

**Issue**

`App.initialize()` throws `new Exception((String) null)` when the
configuration read fails - a deliberately message-less exception that
`Main` then swallows (`x.getMessage() != null` check) so the failure
prints nothing.

**Impact**

A missing configuration file exits silently, and the operator sees no
reason.

**Recommendation**

Inside the `App` extraction step, give the exception a real message
such as the configuration path - stderr gains one line on a failure
that today produces none. Acceptable internal output improvement, or
defer to an issue if output must stay byte-identical.

**Risk Level**

Suggestion

**Breaking Change Assessment**

No Breaking Change for the success path, while the failure path gains an
error line, which is the intended improvement.

**Example**

Before: `throw new Exception((String) null);`. After:
`throw new Exception("Configuration not readable: " + configurationFile);`

---

**Out of scope - routed to issue documents, not this refactoring**

- `Automation.step()` `jump` handler: `result.jump` is assigned only
  inside `if (null == jump)` guarded by `isNotWhite(jump)` - unreachable,
  so a `jump:` step never navigates. Defect fix required, not a refactor.
- `execute` passes the whole string to `Runtime.exec()` - arguments in
  the value are part of the program name, so `EXECUTE notepad.exe x.txt`
  cannot work as arguments. Behaviour question for an issue document.
- `String.toUpperCase()`/`toLowerCase()` calls without `Locale.ROOT` in
  `Step.create`, `pressKey`, `resolveKey`, and `stringToBoolean` - latent
  Turkish-locale defect, behaviour-visible fix.

## Plan

Tidy steps first, each followed by `mvn -T 1C package`.

**T1 - Remove commented-out code**

Delete the five blocks listed in Finding 6 across `Main.java`,
`App.java`, `Flow.java`. Addresses Finding 6.

**T2 - Remove dead classes and dead field**

Delete `Test.java`, `Single.java`, and `Result.delay`. Re-verify no
reference with a fresh grep before deletion. Addresses Findings 1, 8.

**T3 - Disambiguate the two Settings types**

Fully qualify `org.sikuli.basics.Settings` at `Automation.java` and
`Flow.java` use sites and drop the imports. Addresses Finding 7.

**S1 - Split Flow reset and next**

`reset()` becomes `resetState()` plus `resolveImagePath()`, and the `next()`
failure branch becomes three sequential returns. Addresses Finding 5.

**S2 - Extract step-effect methods in Automation**

Extract the per-operation `apply*` methods in exact execution order and
introduce the shared `FindFailed` attempt helper for `find`, `click`,
`popup`. Addresses Finding 2.

**S3 - Unify the Step operation mapping**

Introduce the keyword table driving `create()` and `toString()`, and
`press` keeps its `JsonElement` special case inside its entry.
Addresses Finding 3.

**S4 - Extract scenario loading from App**

Move scenario collection, file reading, and script parsing into
`logic.ScenarioLoader`, and optionally resolve Finding 9's message-less
exception there. Addresses Findings 4, 9.

## Step Granularity

Each step leaves the codebase compiling through `mvn -T 1C package` and
changes one concern, so a regression maps to the step that caused it.

T1-T3 are tidy steps with no structural change and run first. S1-S4 are
structural, one per file or concern, and never share a step with tidy
work. The `press` `JsonElement` special case stays inside S3 only.

## Risk

The highest-risk step is S2: `Automation.step()` execution order is
observable behaviour (`print` precedes actions, `pause` precedes the
trailing delay, `when` precedes `then`), and reordering changes
scenarios silently. The extracted methods must be invoked in the
current field order, and the smoke scenario checks that order.

S3 risks `toString()` drift if the table emits operations in a
different order than today's output - the round-trip check catches it.

S4 risks losing the `.json`/`.script` extension-probing behaviour or
the verbose "Can't read scenario" messages during the move.

No test framework exists - the task-specific acceptance checks below
replace it. Members with thin coverage are `step()`, `create()`,
`toString()`, and `parseScenarioScript`, all covered by the smoke
scenarios.

## Acceptance Criteria

- `mvn -T 1C package` builds clean after every step with zero errors
  and zero warnings.
- `java -jar target/sikulix-automation.jar work/config.json` behaves
  identically: Notepad opens, `HELLO` is typed, the wait elapses,
  Alt+F4 and Alt+N close the tab without saving.
- `java -jar target/sikulix-automation.jar work/config-play.json`
  behaves identically through the `.script` path.
- A `toString()` round-trip on `work/notepad.script` produces the same
  operations in the same order before and after S3.
- `java -jar ... --ignore` on a scenario with an invalid `PRESS` key
  prints the error and continues, exactly as today.
- No dead code remains: no `Test`/`Single` classes, no `Result.delay`,
  no commented-out blocks.
- Observable stdout for the smoke scenarios is unchanged except the
  optional Finding 9 failure-path message.
