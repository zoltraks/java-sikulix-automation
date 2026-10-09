# Refactoring

## Objective

Keep this Java automation codebase maintainable as the scenario step
vocabulary and the execution machinery grow. Refactoring exists to hold
structural debt down before it makes the step engine, the parsers, or the
scenario formats hard to extend safely.

## Definition

Refactoring is a behaviour-preserving change to internal structure.

The same input must produce the same output, and the same observable
behaviour must remain.

A change that alters behaviour is a feature or a fix and belongs in an issue
document under `docs/issue/`, not in a refactoring.

- **Tidy** - preparatory edits such as renames, dead-code removal, comment
  fixes, and formatting.
- **Refactor** - structural changes that preserve behaviour: extract, inline,
  move, split, merge.
- **Behaviour Change** - out of scope - route through an issue document,
  including defect fixes found mid-refactor.

Do not mix these in a single step. If a step reveals a required behaviour
change, stop and route it through an issue document.

## General Rules

- Preserve existing behaviour, and fix defects through a separate issue document.
- Prioritise maintainability and clarity over micro-optimisations.
- Every recommendation must include a justification.
- Do not modify a working critical path unless it is the explicit target of
  the proposal.
- Do not bump the version as part of a refactoring.

## Autonomous Analysis

When asked for a proposal, analyse the repository and select the focus and
document the analysis in the proposal.

Trace execution flow, map dependencies, and note side effects across the
modules under review.

Identify duplication - exact, functional, and intentional - and unused code,
including partial refactors and bypassed legacy paths.

Verify that no indirect reference exists before recommending removal of any
member, including dynamic, reflection, and debug-only usage.

## Proposal Document

Create the proposal in `docs/refactoring/<version>/` as
`refactoring-proposal.md`, built from
`docs/template/refactoring-proposal-template.md`.

The filename is fixed and carries no change or refactoring name, because
the version directory already scopes the document.

Required sections: Problem (with file paths, quoted fragments, and the
analysis), Goal, Findings (each with Issue, Impact, Recommendation, Risk
Level, Breaking Change Assessment, and a before/after Example), Plan
(ordered steps that each leave the codebase verifiable), Step Granularity,
Risk, Acceptance Criteria.

Steps execute in order and each leaves the codebase buildable and
verifiable. Keep tidy steps separate from structural steps. Apply the
parallel-change pattern when the refactoring crosses module boundaries.

A version carries at most one formal refactoring, so the fixed filenames
never collide and earlier proposals are never overwritten. A further
refactoring queues to the next version directory.

The proposal is the change record for the refactoring - no issue
document is created for work that has a proposal.

## Areas to Review

Correctness, architecture and design, duplication and maintainability,
readability, performance, resource management, error handling, security,
testability, documentation.

Project-specific areas:

- Step dispatch structure in `Automation.step()`.
- Scenario parsing fidelity for `.json` and `.script` inputs.
- SikuliX API surface compatibility.
- The CLI, scenario file formats, and stdout output contract.

## Severity Classification

- **Critical** - incorrect results, data loss, security vulnerabilities, or
  failure.
- **Major** - significant impact on reliability, maintainability,
  architecture, or performance.
- **Minor** - readability or consistency without technical risk.
- **Suggestion** - optional improvement.

## Project-Specific Constraints

- **Java 8 compatibility.** `pom.xml` pins source and target to 1.8, so no
  language feature or API newer than Java 8 may be introduced.
- **Step vocabulary is the public contract.** The step names and semantics
  documented in `README.md` (`type`, `press`, `mouse`, `find`, `click`,
  `popup`, `message`, `execute`, `when`, `then`, `jump`, `pause`, `delay`,
  `wait`, `similarity`, `path`, `print`, `label`) must keep their meaning.
- **Scenario formats are a contract.** Both `.json` shape and `.script`
  line syntax must keep parsing existing scenario files, including quoted
  values and multi-token `PRESS` lines.
- **Stdout is the interface.** `print` output and diagnostic lines are the
  user-facing interface of a CLI tool, so refactoring must not reformat or
  reroute them.
- **SikuliX API surface.** Only SikuliX API 2.0.5 types and calls are
  available - abstractions must not assume a different API.
- **Execution order and timing.** Steps execute sequentially with
  `pause`/`wait`/`delay` semantics against a live desktop, so restructuring
  must preserve execution order and wait behaviour.
- **Packaging contract.** The artifact stays `sikulix-automation.jar` with
  main class `pl.alyx.robot.sikulix.Main` and the `assets` resource path.
- **Settings behaviour.** `verbose` and `ignore` semantics, including the
  configuration-overrides-CLI precedence, must not change.

## Focus Areas

Refactoring attention is most valuable in the following locations.

- `App` - argument parsing, configuration loading, and orchestration still
  live in one class after scenario loading moved to
  `logic.Loader`.
- `Automation.pressKey()` - the key-specification parser mixes tokenizing,
  modifier resolution, and key-name lookup in one method, and its
  `KEY_NAMES`/`MODIFIER_NAMES` tables are populated by hand.
- `Configuration` - flags and numbers are stored as `String` and parsed
  per call site (`stringToBoolean`, `stringToDouble`) instead of once at
  the boundary.
- `StringUtility` - a growing grab-bag of unrelated helpers - the
  `isEmpty`/`isNotEmpty`/`isWhite`/`isNotWhite` predicate pairs are
  intentional and stay.
- Suspected defect, not a refactor: the `jump` handling in
  `Automation.applyCondition()` assigns `result.jump` only when
  `null == jump`, which is unreachable inside the enclosing non-empty
  check. Route through an issue document.

## Executing a Refactoring

Run the verification baseline before the first step to establish a
known-good state.

The baseline for this project is `mvn -T 1C package` producing
`target/sikulix-automation.jar`, because no automated test suite exists.

Follow the plan step by step and run the verification after each step and do
not proceed until it passes.

If a step reveals an issue not covered by the plan, stop and update the
proposal.

## Testing Requirements

- The repository has no test framework, so the proposal defines task-specific
  baseline checks when testing is N/A, such as running
  `work/config.json` and `work/config-play.json` and comparing observable
  behaviour before and after.
- Where a step is coverable, add a characterisation note in the proposal
  describing how the behaviour was verified.
- Verify no indirect reference exists before removing any member.
- Changes that touch the SikuliX interaction layer describe the manual
  desktop verification performed, because `keyDown`/`type`/`find` paths
  cannot be proven by a build alone.

## Anti-Patterns

- Premature abstraction and speculative generality.
- Mixing renames with extractions, or tidy edits with structural steps.
- Test rewrites that hide a regression.
- Time or duration estimates in proposals or assessments.
- Version bumps as part of a refactoring.

## Assessment

An assessment is mandatory after every formal refactoring: proposal
summary, implementation summary, gap analysis, verification results,
metrics comparison, output fidelity, behaviour smoke check, and a pass or
fail conclusion.

Create the assessment in the same version directory as
`refactoring-assessment.md`, built from
`docs/template/refactoring-assessment-template.md`.

Creating proposals and assessments is a document-only change and does not
run the verification loop.
