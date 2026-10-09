# Refactoring Proposal Generation Prompt

## Project Analysis Requirements

**MANDATORY TARGETED ANALYSIS:**
- Review the entry point, top-level orchestration, and the modules involved in the suspected duplication or unused code.
- Trace execution flow only across the functions and dependencies that interact with the code under review.
- Map direct and indirect dependencies between the involved modules.
- Document each affected component's role, public surface, and side effects.
- Track data structures and state transitions that cross module boundaries.
- Identify configuration, initialization, and teardown logic that touches the target area.
- Understand performance or timing constraints relevant to the affected modules.
- Capture error handling, edge cases, and debug hooks nearby.

**Analysis must cover:**
- The relevant source files, their structure, and ownership.
- Existing naming patterns used inside those files.
- All module dependencies, imports, and coupling points that influence the change.
- All instances of code duplication (exact, functional, or intentional) inside the reviewed scope.
- All unused or dead code inside the scope, including partial refactors and legacy branches.
- Applied design patterns or architectural decisions that constrain changes.
- Data structures that cross the reviewed boundary.
- Callback mechanisms, event handlers, and debug instrumentation around the scope.

**Identify code duplication carefully:**
- Exact duplicates (copy-paste code)
- Functional duplicates (same logic, different implementation)
- Intentional duplicates (safety-critical, performance-optimized)
- Near-duplicates that could be unified
- Document WHY duplication exists before proposing removal

**Identify unused code thoroughly:**
- Truly dead code (never called, never referenced)
- Incomplete refactoring artifacts (partially removed features)
- Commented-out code that should be removed
- Imports and dependencies that are no longer used
- Functions and variables defined but never accessed
- Legacy code paths that are bypassed
- Distinguish between unused code and code used only in DEBUG mode

## General Instructions

Read project documentation and guidelines for refactoring proposals.
Make the refactoring proposal for the current version.
Save the proposal as PROPOSAL.md in project root.
Forget about previous refactorings.
This refactoring proposal will be used as a guide for AI assisted code generation, not for human programmers.
Create an implementation plan organized in phases, but do not break it into time frames or schedules.
When creating the document, avoid using emoji in section titles.
Do not write conclusions, future considerations, implementation timelines, or files modified summaries.
Do not include implementation safety measures, git workflow strategies, or rollback plans.
Do not mention sources of recommendations (e.g., "from X proposal", "based on Y analysis").
Skip any time-based implementation references (e.g., "Week 2, Day 5", "Days 1-3").
Perform a fresh analysis for each proposal.
Focus on unused or duplicated code as the primary refactoring target.
Try to simplify things for increasing maintainability.
Do not modify the working audio generation pipeline unless the refactor specifically targets its surrounding orchestration; the capture path is functioning correctly.
Proposed changes must be safe and not break anything.
Do not extend file structure more than necessary; keep it as flat as possible.
Current file structure is acceptable and maintainable.
Aim to fit the changes within a single sprint.
All debugging facilities are critical for diagnostics and must not be changed.
Debug mode console logs are intentional and expected - they must not be removed or modified.
Preserve all debug mode logging statements as they are essential for troubleshooting.

## Refactoring Focus Areas

**PRIMARY FOCUS - Unused and Duplicated Code:**
- Identify all unused code and analyze why it exists
- Determine if unused code is from incomplete previous refactorings
- Find all duplicated code and assess whether duplication is intentional
- Some code duplication may be intentional for safety or performance reasons
- Propose removal only when code is genuinely unused and safe to remove
- Propose unification only when duplication provides no benefit
- Prioritize maintainability improvements through code simplification

**Maintainability Enhancement:**
- Simplify complex functions without changing behavior
- Reduce coupling between modules where safe
- Improve code organization for better readability
- Clarify ambiguous or confusing code structures
- Standardize inconsistent patterns
- Enhance code documentation where needed

**Safety-First Approach:**
- Every proposed change must be proven safe before inclusion
- Test critical code paths remain untouched
- Ensure performance-critical sections are not impacted

## Naming Conventions

**Apply naming conventions consistently:**
- Use existing naming patterns found in the project (document these in the proposal)
- If current naming is inconsistent or unclear, propose better conventions
- Ensure all new names follow the established or proposed patterns
- Document any naming convention changes with clear before/after examples
- Maintain consistency across files, functions, variables, and constants
- Proposed naming improvements must enhance code readability and maintainability

## Testing Requirements

**MANDATORY for each affected module:**
- Run existing tests and add new ones covering the updated behavior.
- Ensure tests cover the refactored code paths, error handling, and edge cases directly touched by the change.
- Add regression tests when the refactor addresses a bug or previously failing scenario.

**Test specifications must include:**
- Function signature and expected behavior.
- Test cases with inputs and expected outputs tied to the refactor.
- Assertions that verify correctness.
- Integration points that need verification.
- Performance notes only when relevant to timing-sensitive sections.

**Special testing for code removal:**
- Document why removed code was unused (call graph or reference analysis).
- Verify no indirect references exist.
- Confirm no DEBUG configuration depends on it.
- Demonstrate that removal does not affect existing functionality.

## Document Focus

Focus the document on:
- Technical specifications and executable requirements
- Detailed analysis of unused and duplicated code
- Justification for each proposed change (why it's safe)
- Code structures, function signatures, and expected outcomes
- **Safety constraints (what must NOT be modified)**
- Testing requirements for automated verification with specific test cases for each refactored area
- Clear phase organization without scheduling details
- Explicit marking of protected code areas
- Rationale for keeping or removing duplicated code
- Analysis of incomplete refactorings and their cleanup

## Proposal Quality Requirements

**Each proposed change must include:**
- What code is being changed (exact location and scope)
- Why the change is needed (unused, duplicated, or maintainability)
- Why the change is safe (analysis proving no breakage)
- How to verify the change (specific tests required)
- What must NOT be changed in proximity to the modification

**For code removal proposals:**
- Proof that code is genuinely unused (call graph, reference analysis)
- Explanation of why it exists (incomplete refactoring, legacy, etc.)
- Confirmation that DEBUG mode doesn't use it
- Verification that no future features depend on it

**For code deduplication proposals:**
- Identification of all duplicate instances
- Analysis of whether duplication is intentional
- If intentional, document why and DO NOT propose changes
- If unintentional, show how to safely unify without side effects
- Proof that unification won't affect performance or timing
