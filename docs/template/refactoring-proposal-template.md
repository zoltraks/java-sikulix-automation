# [Short Imperative Title] Refactoring Proposal

Save the proposal in the resolved refactoring directory under the recorded naming convention: `<change-name>-refactoring-proposal.md`, or the fixed name `refactoring-proposal.md` when the convention assigns one refactoring pair per version directory.

## Problem

Describe the code smell, duplication, standard violation, or architectural issue.

Include file paths and short quoted fragments.

State which files the refactoring would touch.

Document the analysis that led to this focus.

## Goal

Describe the desired end state.

What is the new shape of the modules, types, or members involved?

## Findings

List every identified issue in the following structure, ordered by risk level (highest first).

**Issue**

Describe the observed problem.

Include the file path and a short quoted fragment.

**Impact**

Explain the consequences - incorrect behaviour, reliability risk, reduced maintainability, performance degradation, security exposure, testability limitations, or architectural inconsistency.

**Recommendation**

Describe the proposed improvement and the rationale for it.

**Risk Level**

Critical / Major / Minor / Suggestion

**Breaking Change Assessment**

No Breaking Change / Internal Breaking Change / Public API Breaking Change / CLI Contract Breaking Change

**Example**

Before: [code fragment showing the issue] After: [code fragment showing the proposed solution]

---

(Repeat for each finding.)

## Plan

List the steps in execution order.

Each step must leave the codebase buildable and verifiable by the selected procedure, or by task-specific checks when testing is explicitly N/A.

Keep tidy steps such as renames, formatting, and dead-code removal separate from structural steps.

Apply the parallel-change pattern when the refactoring crosses module boundaries: introduce the new shape alongside the old, migrate call sites one at a time, then remove the old shape after every call site has been migrated and the selected checks have passed for at least one full step.

**[Step name]**

Describe what changes and why.

State which findings this step addresses.

## Step Granularity

Each step must be small enough that a regression introduced by the step is obvious from its verification output or task-specific acceptance checks.

Split any step that touches many files or mixes a rename with an extraction.

Tidy steps must come first within a file and must not share a step with structural changes.

## Risk

What could go wrong?

Which areas are most likely to regress?

Which tests cover the affected code?

Identify members with thin coverage that need characterisation tests first.

If no testing procedure is defined, identify the task-specific acceptance checks instead.

## Acceptance Criteria

Concrete conditions for completion.

Include identical observable behaviour and unchanged outputs for the agreed fixtures.

Require a clean run of the selected verification procedure and no regression against coverage thresholds where established.

When testing is N/A, require passing task-specific acceptance checks instead.
