# Project Guidelines

This file is the index of record for development rules in this repository.

It also owns the development workflow, the testing and verification procedure, document style, and versioning rules under the resolved Minimal file selection.

## Sources of Truth

This ordered list is the only index of active project documentation.

Each entry is authoritative for its topic.

When information overlaps, topic ownership takes priority, then the earlier applicable entry resolves remaining conflicts.

- **Primary Entry Point**: `README.md`, section "Project Rules", is the recorded agent entry route - it directs agents here. `README.md` is also the human introduction and the user-facing usage documentation for the scenario format.
- **Guidelines**: This file (`docs/GUIDELINES.md`) is the central source of truth for development rules and the index of active documentation.
- **Copyright and Licensing**: [`COPYRIGHTS.md`](COPYRIGHTS.md) is authoritative for intellectual-property rules.
- **Engineering Standards**: `docs/standard/` holds one standard per technology stack or category - read the matching standard before writing code:
  - `java-general-development.md` - Java language conventions (read for every code change).
  - `java-maven-development.md` - Maven build and packaging rules (read for build or POM changes).
  - `automation-general-development.md` - automation strategy and discipline, covering both inspection and execution tool models (read before changing scenario steps or the execution engine).
  - Each standard carries a `<!-- Version: ... | Date: ... -->` line after its heading - on every change to a standard, bump the least significant version component and refresh the date.
- **Refactoring**: `docs/REFACTORING.md` is authoritative for the formal refactoring process - proposals and assessments are written per its rules.
- **Templates**: `docs/template/` holds `change-issue-template.md` - the skeleton for issue documents in `docs/issue/` - and the refactoring trio `refactoring-proposal-prompt.md`, `refactoring-proposal-template.md`, `refactoring-assessment-template.md` for the artifacts in `docs/refactoring/` - plus `change-review-template.md` for version reviews in `docs/review/`.
- **Usage contract**: `README.md` documents the step vocabulary and file formats and is authoritative for the user-visible behavior contract until a dedicated reference exists.

Recorded owner assignments for topics without a standalone file:

- Development workflow: this file, "Development Workflow".
- Testing and verification: this file, "Testing and Verification".
- Document style: this file, "Documentation Guidelines".
- Versioning: this file, "Versioning".
- Refactoring: `docs/REFACTORING.md` - routine cleanup happens inside a change - substantial restructuring goes through a proposal in `docs/refactoring/<version>/` and a mandatory assessment in the same directory.
- Deployment: the built jar `target/sikulix-automation.jar` is the artifact - distribution is manual file copy - see "Release Lifecycle".
- Decision records: not established - no `docs/decisions/` directory - record durable rationale inside the issue document that carried the decision.
- Community files (`CONTRIBUTING.md`, `SECURITY.md`): not selected - the license text lives in `LICENSE.md` at the project root (see `COPYRIGHTS.md`).
- `CHANGELOG.md`: created at the first version bump per "Versioning".

## Repository Layout

Resolved mapping of logical roles to actual paths:

| Logical role          | Resolved location                                                                       |
|-----------------------|-----------------------------------------------------------------------------------------|
| Documentation root    | `docs/`                                                                                 |
| Agent entry point     | `README.md` - "Project Rules" routing block                                             |
| Guidelines            | `docs/GUIDELINES.md`                                                                    |
| Issue documents       | `docs/issue/<version>/` - named `<issue-name>.md`                                       |
| Refactoring artifacts | `docs/refactoring/<version>/` - `refactoring-proposal.md` + `refactoring-assessment.md` |
| Version reviews       | `docs/review/<version>/` - named `<review-name>.md`                                     |
| Engineering standards | `docs/standard/`                                                                        |
| Document templates    | `docs/template/`                                                                        |
| Disposable workspace  | `work/` - gitignored, cleanable at any time                                             |
| Sources               | `src/main/java/pl/alyx/robot/sikulix/`                                                  |
| Example image assets  | `media/image/`                                                                          |
| Build output          | `target/` - gitignored, generated                                                       |
| Version file          | `pom.xml` - `<version>` element                                                         |

Directory naming convention: lowercase singular names, version grouping matching `pom.xml` `<version>` (currently `0.4` - the first released version is `0.3`). Version directories are created by their first document - never seed empty version directories with `.gitkeep` files.

Issue documents merge the change-record and plan roles: one document under `docs/issue/<version>/` carries both as sections, and the plan section is filled in when the issue is scheduled for work.

Roles deliberately absent: `change/` and `plan/` (merged into `issue/` by the issue-document convention), `report/`, `archive/`, `decisions/`, `reference/`, `skill/` directories, and `AGENTS.md` - none were selected during preparation.

## General Workflow

- Read order: `README.md` routing block, then this file, then the matching engineering standard, then task-relevant documents.
- Follow the recorded execution and approval policy (Human in the Loop) for every gate.
- Update the relevant sources of truth before writing code - documentation first.
- `README.md` describes user-visible behavior - a change to the step vocabulary or file formats updates it in the same change.

## Documentation Guidelines

This file is the document style owner.

- Write short sentences effectively.
- Use explicit line breaks.
- Put exactly one empty line before and after lists of items.
- Use standard ASCII characters.
- Keep Markdown tables readable as plain text - align columns by padding cell values to the maximum width of the column.
- Keep section names short.
- Do not put qualifiers in section names using parentheses - put qualifiers like "mandatory" as sentences in the section body.
- For process steps, use bold headers separated by empty lines instead of numbered lists.
- File conventions: UTF-8, CRLF in the working tree (`core.autocrlf=true` normalizes to LF on commit).

## Development Principles

- **Clean Code**: Write readable, self-documenting code.
- **Single Responsibility**: Each function does one thing well.
- **Naming**: Consistent patterns matching the existing codebase.
- **Comments**: Avoid comments that merely restate the code.
- **Behavior contract**: Scenario semantics are the product - steps execute in JSON field order is not guaranteed, the engine applies a fixed field order per step (see `logic/Automation.java`) - keep `README.md` honest about it.
- **Real desktop only**: The tool drives the live interactive desktop through SikuliX - verification needs a real session and cannot be headless.

## Development Workflow

### Lifecycle Phase

Initial development toward MVP - a working prototype with no test suite and no CI.

Use lightweight issue documents, and establish basic verification for essential flows.

### Execution Policy

Human in the Loop.

A human approves the gated decisions: scope and plan, documentation reconciliation, implementation review, working-branch integration, production release.

Automated checks may support a human decision but do not constitute approval.

Reserved decisions (human only, under every policy): commit authorship and attribution, which baseline or uncommitted delta ships in a release, and whether the version number bumps.

The agent reports a reserved decision as pending instead of inferring it.

### Change Lifecycle

Every change is classified: **Feature**, **Fix**, **Refactor**, or **Chore**.

Subtype labels (documentation, configuration, dependency) annotate but do not form a second taxonomy.

Every change above trivial size begins with an issue document in `docs/issue/<version>/` built from `docs/template/change-issue-template.md` - intent, description, scope, definition of done, risk, lifecycle phase, affected documentation.

Threshold: one-command fixes and documentation typo corrections may proceed without an issue document - everything else records one.

Every change has an implementation plan before execution - the Implementation Plan section of the issue document, filled in when the issue is scheduled for work.

A change classified **Refactor** additionally follows `docs/REFACTORING.md` - a formal refactoring needs no issue document, because the proposal under `docs/refactoring/<version>/` is the change record carrying intent, analysis, and step plan, and the assessment closes the pair in the same directory.

### Cycle Stages

- **Concept** - optional free-form ideation, distilled into issue documents before planning.
- **Issue Record** - what should change and why, filed under `docs/issue/`.
- **Implementation Plan** - how it will be implemented - a section of the issue document referencing the matching engineering standard.
- **Implementation** - update documentation first, then work in coherent increments, running the incremental loop per increment. If the plan proves wrong, stop, update the plan, continue.
- **Completion Record** - the issue document's Outcome section: implemented changes, plan deviations, checks and outcomes, problems, unresolved limitations, review decision, integration status.
- **Integration** - commit to the working branch under Human in the Loop approval - integration is not a release and not a deployment.

### Cycle Rules

- No time estimates in plans or records - execution is driven by technical requirements and verification results.
- Document-only changes run documentation checks (structure, links, consistency) instead of the source loop.
- Scope and plan approval: create the issue document, then obtain human approval before writing code - for a formal refactoring the proposal is the approval document instead of an issue document.
- Do not update `CHANGELOG.md` during regular work - only on an explicit version bump or changelog request.
- Do not scan `docs/issue/`, `docs/refactoring/`, or `docs/review/` for unrelated history automatically. The current issue document is an active task artifact readable as part of the requested work. Exception: on a version bump or changelog request, read the issue documents of the relevant version for changelog analysis.

### Release Lifecycle

Release preparation runs only on an explicit request.

- Pre-release: assess the accumulated change set - `mvn -T 1C package` must build clean.
- Artifact certification: `target/sikulix-automation.jar` is the shipped artifact - certify it by running `java -jar target/sikulix-automation.jar` against a smoke scenario in `work/` and report what could not be certified (live-desktop steps prove the binary, not the target machine).
- Version review: before the bump, reconcile the recorded work against the real changes - read the issue documents in `docs/issue/<version>/` and any refactoring pairs in `docs/refactoring/<version>/`, compare them with the version-control log for the version period, analyse confirmed, unconfirmed, undocumented, and deviated work, and write the review in `docs/review/<version>/` from `docs/template/change-review-template.md`. The review verdict feeds the bump decision. Review documents carry no author, reviewer, or co-author attribution and no commit-range or item-count bookkeeping rows - Document Information records version, date, and the read-only commands used.
- Versioning: see "Versioning" - bumps are a reserved human decision and include a changelog update.
- Deployment: manual - the jar plus a config file is the deployable unit - there is no managed environment, promotion, or rollback machinery. Record the jar hash and source revision in the release evidence.

### Versioning

The version lives in `pom.xml` (`<version>`), currently `0.4`.

Work groups under the unreleased version `0.4`, which is the `<version>` segment of the issue directory - `0.3` was the first released version.

A bump happens only on an explicit "bump version" request, which also creates or updates `CHANGELOG.md` at the project root (Keep a Changelog conventions: newest first, ISO dates, change types Added, Changed, Deprecated, Removed, Fixed, Security).

## Session Review

"Improve the project" triggers the session review - it is also a standing expectation to run before reporting substantial work done.

Reconstruct what actually happened from the session's conversation history under `%APPDATA%\devin\cli\summaries\history_*.md`, reading across thread boundaries when a summary file split a session.

Identify every wound - what failed, surprised, or needed a workaround - and every assumption that was made, then verify the real state before recording a lesson.

Land each durable lesson as a rule in its owning document: engineering wounds in the matching `docs/standard/` file, workflow wounds in this file, tooling wounds in "Tooling Notes" below, and target-application quirks in the issue that owns the affected scenario. When a lesson has no owning rule yet, add the rule to the owning document first.

This project keeps no lessons index - a learned rule lives only in its owning document, so no `docs/lesson/` directory is created and no findings file is written.

## Testing and Verification

This file owns the testing strategy and the verification procedure.

### Incremental Loop

Run once per coherent increment of work:

**Implement a coherent increment** - change the implementation, keep increments small.

**Format** - no formatter is configured (gap recorded) - match the existing style by hand.

**Run focused tests** - no test framework or `src/test/` exists (gap recorded). Use task-specific acceptance checks: compile and a manual smoke run are the available evidence.

**Static analysis** - `mvn package` runs PMD with the root `ruleset.xml` (dead-code rules: unused locals, parameters, fields, private methods). Violations print as warnings and do not fail the build - widening the ruleset or making it fail is a recorded change.

**Run static checks** - `mvn -T 1C package` is the compile gate - warnings count as diagnostics to resolve.

**Resolve diagnostics and repeat** - fix failures and warnings within the agreed scope until the increment is clean.

### Final Verification

`mvn -T 1C package` completes with zero errors.

Any source, configuration, test, dependency, generated-input, or toolchain change after a clean run requires another run of the affected checks.

Functional smoke: `java -jar target/sikulix-automation.jar work/config.json` drives the real interactive desktop - confirm in advance which window the scenario types into.

### Tooling Notes

- Maven and the JDK may not be on PATH - locate the installations and prefix shell commands with `export PATH="<maven>/bin:$PATH"`, or invoke `mvn.cmd` by full path (the build targets Java 8 regardless of the JDK running Maven).
- `work/` is gitignored, so the file tools refuse paths inside it - write scratch files with a single-quoted heredoc (`cat > work/file <<'EOF'`) instead.
- A failed exact-match edit means the anchor was assumed, not confirmed - re-read the file and never retry blind.
- `find` across the drive root is slow - probe known install locations (SDK directories, package-manager shims, Program Files) with `ls` first when hunting a tool.
- Panther flags a `;` even inside a multi-line code span - keep code spans single-line, run `format-table.py` after editing a table, and keep Markdown in CRLF.

### Check Results

Record each check with a status: **Passed**, **Failed**, **Not Run** (with reason), **Unavailable** (with reason), **Not Applicable** (with reason).

When required evidence cannot be obtained, mark the task **Blocked**.

### Security Checks

None configured (gap recorded).

Dependencies are few and declared in `pom.xml` - review them per `COPYRIGHTS.md` when a dependency changes.

Never commit credentials - the scenario format takes no secrets - images and configs may contain real screen captures, so `work/` stays gitignored.

## File Maintenance

- Maintain existing style and naming conventions.
- Preserve line endings and encoding - sources are UTF-8, ASCII-only, CRLF in the working tree.
- Do not change version numbers unless explicitly instructed - a bump request includes the changelog update.
- `.gitignore` already covers `/work`, `/target`, `/out`, `.idea/`, `/test` - keep durable information out of those paths.

## Agent Behavior

- A request naming no specific operation is a standby instruction - load the read order and wait for the task.
- Confirm the repository root and the shell working directory before scripted or bulk file operations.
- `work/` is the disposable workspace for temporary artifacts (scenario files, captures, scratch) - never put durable information there - it may be cleaned at any time.
- The automation acts on the live desktop - announce a run before typing or clicking on the user's screen.

## Knowledge Exploration Boundaries

- `target/` is generated build output - never authoritative.
- `work/` is disposable scratch - never authoritative and never read for history.
- `media/` holds example screenshot assets referenced by `README.md`.
- The current issue document is an active task artifact - read it as part of the requested work, but do not scan unrelated issue history.
- For changelog analysis on a version bump, read the issue documents of the relevant version - the single exception to the restricted-directory rule.

## Memorization Convention

When the user says "memorize" or "remember", the instruction means:
persist the stated rule into the owning document on disk - guidelines,
standards, or templates - so it survives beyond this session. It never
means relying on conversation memory alone.

Route the rule to the authoritative source for its topic:

- Documentation structure or language changes: this file.
- Workflow, testing, style, or versioning rules: the matching section of this file.
- Engineering-standard lessons: the relevant file in `docs/standard/`.
- Licensing or IP rules: `COPYRIGHTS.md`.
- Shape of a recorded-work document: the matching file in `docs/template/` -
  a template change propagates to the seed copy and to future instances.

Do not create a new file only to store a memorized rule.

Confirm that the rule landed in the correct file.

## Model Change / New Conversation Rule

If the model changes or a new session starts, repeat the pre-work self-check before doing any work: re-read this file and the task-relevant engineering standards.

Prior context is not assumed.
