# Version [version] Review

**Status:** Draft | Complete

**Verdict:** Ready | Ready with Conditions | Not Ready

## Document Information

|             |                                                          |
|-------------|----------------------------------------------------------|
| Version     | [version under review, for example 0.3]                  |
| Review Date | [YYYY-MM-DD]                                             |
| Commands    | [read-only commands used, for example git log, git diff] |

## Verdict

State the recommendation first in one or two sentences: whether the version
is ready for a bump and why.

If a verdict cannot be given, state what is missing - an unclear scope or
unfinished work is itself the finding.

## Scope

The release envelope: which changes this review covers and what it leaves
out, including deferred or unscheduled work.

## Recorded Work

One row per issue document and refactoring pair under the version
directory.

| Document                 | Kind        | Status | Result               |
|--------------------------|-------------|--------|----------------------|
| [docs/issue/0.3/name.md] | Issue       | [Done] | [one-line outcome]   |
| [docs/refactoring/0.3/]  | Refactoring | [Done] | [assessment verdict] |

## Repository Analysis

Read the version-control log for the version period and reconcile it
against the recorded work above.

Report in four parts:

- **Confirmed** - recorded work whose changes are present in the log.
- **Unconfirmed** - recorded work with no corresponding change.
- **Undocumented** - changes in the log with no issue document.
- **Deviations** - work that shipped differently from its plan or
  definition of done.

## Findings

| Severity   | Finding         | Evidence               | Action            |
|------------|-----------------|------------------------|-------------------|
| [Critical] | [short finding] | [file or commit range] | [required action] |

Severity classes follow `docs/REFACTORING.md`: Critical, Major, Minor,
Suggestion.

## Verification Evidence

What was checked for this review - build result, scenario smoke runs,
documentation validation - with the command or artifact for each.

## Unable to Verify

Optional. Evidence that could not be produced and why, kept separate from
findings - an unverified area is a deferred risk, not a defect.

## Decision

The go or no-go record for the version bump: who decided, on what date,
under which conditions, and which risks were accepted.

- **Decision:** [go / no-go / go with conditions]
- **Decided by:** [for example "the project owner" - never an agent name]
- **Conditions:** [or none]
- **Accepted risks:** [or none]
