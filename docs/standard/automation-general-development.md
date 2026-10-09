# Software Automation Engineering Standards

<!-- Version: 0.1.0 | Date: 2026-10-08 -->

## Purpose

This document defines the engineering standard for software that automates a desktop application a human user is also operating.

It covers tool models, control-channel selection, the read-only contract, cohabiting with a live user, state detection, resilience, machine-generated text, and the calibration workflow.

It is language-agnostic: every rule applies regardless of the implementation stack.

Companion standard documents for this domain cover the Windows platform mechanics underneath these techniques, web applications running inside a browser, and the language-level rules that implement them.

It is written to be executed directly by an AI coding agent and to be read without effort by a human reviewer.

## Scope

A project is in scope when all of the following conditions hold.

- The software drives or inspects an application that a human user may be operating at the same time.
- The software runs on the same desktop session as the target application.
- The interface to the target is whatever the application exposes - an object model, an accessibility tree, a protocol surface, files, or its pixels.

A UI test harness driving the team's own application, a backend integration talking to a service API, and a robotic-process-automation platform are out of scope - they solve different problems with different failure modes.

## How To Use This Standard

### Order Of Operations

- Identify the target application and the control channels it exposes - see Agent Intake.
- Classify the tool model first - inspection or execution - since it decides which contract applies - see Tool Models.
- Calibrate against the real application before writing control code - the Calibration Workflow section.
- Classify every operation against the applicable contract before it ships - Read-Only for inspection tools, Declared Effects for execution tools.
- Apply state detection and resilience patterns - the target belongs to a user, not to the utility.
- Verify against the live application, not a mock.

### Tool Models

Two shapes of desktop automation exist, and each keeps a different contract.

- **Inspection utility** - reads a target the user may be operating. The Read-Only Contract applies: reads are silent, and every mutation sits behind an explicit opt-in that names itself.
- **Execution utility** - runs a declared scenario or command that mutates the target. The scenario is the opt-in: every step is state-changing by design, and nothing may happen beyond what the scenario declares. Stop-on-failure is the default - a continue-on-error mode exists only as an explicit option.

A tool that does both keeps the paths separate - a read feature inside an executor still follows the read-only rules for its own operations.

### Non-Negotiable Rules

- **Read-only by default.** A read path never invokes, clicks, types, or otherwise mutates the target. Every state-changing operation sits behind an explicit opt-in that names itself.
- **Declared effects only.** An execution utility performs exactly what its scenario or command declares - a step name, a value, a target - and introduces no ambient mutation outside the declared sequence.
- **Every tolerated side effect warns.** Launching an app, restoring a window, or switching a view produces a human-readable warning on the run's warning channel.
- **No synthetic input in a read path.** Sending keystrokes or mouse events to a window a user may be typing into is never part of a read operation.
- **Bounded work only.** Every enumeration is capped by a maximum element count, and every wait is capped by a named timeout.
- **Degrade with data, not silence.** When content cannot be reached, return what was obtained plus a warning - a partial answer beats a fabricated or empty one.

A violation of any of these rules is a defect, not a style preference.

## Agent Intake Protocol

Before writing automation code, answer these questions - by inspecting the repository for an existing integration, or by probing the live application for a new one.

| Question                    | Detect By                                                          |
|-----------------------------|--------------------------------------------------------------------|
| Target process and window   | Process names, window titles, window classes                       |
| Available control channels  | Registered COM/ProgID servers, URI schemes, an exposed a11y tree   |
| UI technology               | Native controls, WebView2, Chromium or Gecko browser, custom drawn |
| UI language of exposed text | A tree dump of a representative screen                             |
| Launch path                 | Protocol URI, registered handler, executable location              |
| Shared-desktop constraints  | Whether a user is active and what visible side effects matter      |
| Structural anchors          | Stable automation ids, control-type shapes, repeatable name shapes |

Calibration precedes implementation - never write a provider against assumed tree shapes or invented accessible names.

## Documentation

Consult these sources rather than guessing at platform behavior.

- [Microsoft UI Automation Overview](https://learn.microsoft.com/en-us/windows/win32/winauto/uiauto-uiautomationoverview)
- [Inspect tool](https://learn.microsoft.com/en-us/windows/win32/winauto/inspect-objects) - the reference tree-dump viewer
- [Firefox Accessibility Architecture](https://firefox-source-docs.mozilla.org/accessible/index.html) - remote trees and lazy document creation
- [Chromium Accessibility](https://chromium.googlesource.com/chromium/src/+/HEAD/docs/accessibility/)
- [AT-SPI (GNOME/Linux)](https://gnome.pages.gitlab.gnome.org/at-spi2-core/)
- [Microsoft Active Accessibility (MSAA)](https://learn.microsoft.com/en-us/windows/win32/api/oleacc/)

## Control Channel Selection

Every target application offers some subset of the channels below. Pick the highest row available, and record the reason when dropping a level.

| Channel                          | Fidelity           | Fragility           | Side-Effect Risk | Example                                     |
|----------------------------------|--------------------|---------------------|------------------|---------------------------------------------|
| Application object model / API   | Full data model    | Low - versioned API | Low              | Outlook object model via COM                |
| Accessibility tree               | Rendered UI only   | Medium - DOM shifts | Low for reads    | UIA over new Outlook, Teams, Gmail in a tab |
| Protocol surface                 | Structured, narrow | Medium              | Low              | `msteams:` URI, DevTools protocol           |
| File or data exchange            | Complete records   | Low                 | None for reads   | Export formats, mailbox stores              |
| Synthetic input (keys and mouse) | Whatever user sees | High - focus races  | High             | Last resort only                            |
| Visual capture / OCR             | Pixels             | Very high           | None for reads   | Fallback for inaccessible apps              |

Rules:

- Prefer the semantic channel over the visual one - an object model survives UI redesigns that break a tree walker.
- An accessibility tree exposes what is rendered, not what exists - virtualized lists and lazy trees bound the result set.
- Synthetic input is a last resort and always state-changing - it can never be the silent default.
- A project may legitimately sit on the bottom two rows when no semantic channel exists - visual matching plus synthesized input is then the channel, not a fallback, and the state-detection and calibration discipline below is what makes it safe. Record that choice in the project documentation.
- Channels compose: a mail reader can use the Outlook object model for full fidelity and fall back to UIA when only the new-Outlook window exists.

## The Read-Only Contract

The contract every inspection utility keeps. Execution utilities keep the Declared Effects rule instead - their scenario declares each mutation up front.

Classify each operation before it ships, and wire the classification into the CLI surface.

| Operation                                    | Rule                                                       |
|----------------------------------------------|------------------------------------------------------------|
| Reading names, properties, text              | Always allowed, silent                                     |
| Enumerating elements, walking ancestors      | Always allowed, silent                                     |
| Activating a lazy accessibility tree         | Allowed, silent - no user-visible effect                   |
| Launching the target application             | Allowed with a warning                                     |
| Restoring a minimized window                 | Allowed with a warning                                     |
| Switching a view, folder, or label           | Allowed with a warning - item state must not change        |
| Invoking a control that marks content read   | Opt-in only - explicit flag or entry-id form, plus warning |
| Sending, editing, deleting, flagging content | Out of scope for an inspection utility                     |

- The opt-in is explicit and self-describing: a `--open` flag, or an entry-id form such as `gmail:N` that encodes "open row N".
- The warning goes to the diagnostics channel (stderr or a `warnings` array), never to the payload stream.
- Help text discloses every opt-in that can alter state - a hidden state change is a defect.

## Cohabiting With A Live User

The desktop belongs to the user - the utility is a guest.

- Never take focus, move the pointer, or type in a read path - a user mid-sentence should not notice the run.
- Restoring a minimized window or switching a view is visible - warn, and prefer restoring the previous view before finishing (navigate back after a forced navigation).
- Throttle interactions - spaced invokes and bounded polls keep the app responsive for the user and keep lazy trees building.
- Leave the application as found: same view, same selection, same window state, within what the platform allows.
- Treat every warning-worthy action as a user-visible event - if the user would notice it, say it.

## State Detection

The hardest problem in live-application automation is knowing which screen the target is on.

- **Prefer stable anchors.** Window and document titles update before the accessibility subtree does - a Gmail window title distinguishes list view from conversation reliably while the DOM still serves the old view.
- **Never trust signals that degrade under minimization.** `IsOffscreen` reports every element offscreen for a minimized window - it cannot tell list from conversation, only window state.
- **Structural shapes beat names.** A Gmail message row is identified by its cell layout (a star cell child), not by matching a name prefix that changes with locale.
- **Confirm actions by state, not by return value.** An `Invoke` call returns success even when nothing happened - poll a title or document name for the expected view. When the effect never appears (Firefox never materialized a GCal detail popover), degrade to what is known plus a warning - a silent no-op is a normal outcome.
- **Plan for stale subtrees.** Browsers serve the previous view's elements for tens of seconds after a swap - detect state independently of tree contents and re-resolve rather than cache.
- **Image anchors are the pixel-channel analogue.** For a visual-matching engine the anchor is a captured image region - capture it from the real target at the real DPI and locale, never synthesize or hand-draw it, and prefer unique regions over ambiguous text blocks.
- **Similarity is the tolerance knob.** A match threshold trades missed matches against false positives - change it deliberately per step or per project, never silently.

## Resilience Patterns

- **Bounded polling.** Every wait runs against a named timeout deadline - the poll interval is a constant, never a guess scattered through code.
- **Re-resolve, don't cache.** Elements obtained before a navigation describe the old view - find the document fresh on each poll round.
- **Fault-tolerant reads.** An element can vanish between enumeration and property access - every read goes through a helper that treats a dead element as absent.
- **Graceful degradation.** When a body never materializes, return the row metadata plus a warning instead of a hard error - the caller still gets subject, sender, and date.
- **Per-item failure tokens.** A blocked body lands as `body-access-blocked` on the item plus one summary warning - a whole run does not fail on one protected item.
- **Bounded observation windows.** A collector that watches an event stream - a file-system watcher, a notification listener - runs for a declared duration with an event cap. Buffer overflow, dropped events, and early cancellation degrade to warnings, and the run still emits what it collected.
- **Collapse raw events before emit.** A rename reads as delete-plus-create and a rapid edit storm floods the list - fold the stream into canonical changes before reporting.

## Machine-Generated Text

Accessible names are a wire format - structured, localized, and occasionally mutated by third-party software. Parse them with discipline.

- **Fixture-driven parsing.** Capture real names from the live application (a tree dump into a scratch directory), then copy them verbatim into test cases. A heuristic written against invented strings ships defects - a Teams timestamp misread as a sender and a Gmail date suffix left in place both came from assumed strings.
- **Label tables per locale.** Keep English and localized markers in named tables - adding a language is a table edit, not a logic change.
- **Strip conservatively.** Remove prefixes only where the source shape guarantees decoration. The ABBUC regression stripped a sender name that legitimately began the subject - restricting the strip to quoted/chipped contexts fixed it.
- **Match literal separators.** Gmail separates subject from preview with a non-breaking-space dash - match the exact separator, not generic punctuation.
- **Handle injected content.** Security add-ons prepend markers such as `Avast: Scanned ` to accessible names - strip known injected prefixes and warn that the text was modified.
- **Complete partial dates.** `Sep 29, 8:59 AM (1 day ago)` means: strip the relative suffix, parse the no-year form, apply the current local year.
- **Preserve the raw value.** Keep the untransformed name in a `raw` field - a partially parsed item still reaches the consumer.

## Calibration Workflow

How a new target application goes from unknown to integrated.

- **Dump** the tree with a probe tool (`tree <target> --depth 8 --max 800 --json`) into the scratch directory - for a pixel-channel tool the equivalent is a screenshot capture of the anchor regions into the project's asset directory.
- **Identify anchors** - stable control types, automation ids, repeatable name shapes - and the unreliable signals to avoid - for a pixel channel, image regions that survive theme and locale changes.
- **Pin fixtures** - representative accessible names go verbatim into test cases - for a pixel channel, the captured image assets are the fixtures.
- **Write the parser** as a pure function over names - testable without the application running.
- **Write the provider** against the calibrated structure - resolution, bounded enumeration, contract mapping.
- **Live smoke** against the real application - listing, navigation, read paths, minimized-window behavior.
- **Record quirks** - per-application findings land in a calibration reference document so the next integration does not rediscover them.

## Testing

- Parsers are pure and unit-testable - that is why parsing is separated from tree walking.
- Providers are verified by live smoke runs against the real application - a mocked tree cannot reproduce platform quirks.
- A regression gets a verbatim-fixture test before the fix counts as done.

## Verification

| Check                  | Method                                                                 |
|------------------------|------------------------------------------------------------------------|
| Live smoke             | The changed utility runs against the real application - not a mock     |
| Read-only audit        | The diff contains no invoke/click/send outside an opt-in path          |
| Declared-effects audit | An execution tool's changes mutate only inside declared scenario steps |
| Warning coverage       | Every tolerated side effect appears in the warning channel             |
| Calibration notes      | New target apps or changed DOMs landed in the calibration reference    |

## Definition of Done

- No read path invokes, focuses, types, or mutates - every tolerated side effect warns.
- An execution tool performs only declared steps and stops on failure unless an explicit continue option is engaged.
- Enumeration and polling carry named bounds.
- New parsing heuristics are backed by verbatim captured fixtures.
- The provider degrades to partial data plus warnings rather than failing or fabricating.
- Per-application quirks are recorded in the calibration reference.

## Sources

The following authoritative references support the rules in this document.

- [Microsoft UI Automation Overview](https://learn.microsoft.com/en-us/windows/win32/winauto/uiauto-uiautomationoverview) - the Windows accessibility framework used for desktop automation.
- [UI Automation Security Considerations](https://learn.microsoft.com/en-us/windows/win32/winauto/uiauto-securityoverview) - integrity levels, elevation, and privilege boundaries for automation clients.
- [Inspect Objects](https://learn.microsoft.com/en-us/windows/win32/winauto/inspect-objects) - the SDK tool for examining the UIA tree, control types, and control patterns.
- [Microsoft Active Accessibility](https://learn.microsoft.com/en-us/windows/win32/api/oleacc/) - the legacy MSAA layer for elements without UIA providers.
- [Firefox Accessibility Architecture](https://firefox-source-docs.mozilla.org/accessible/index.html) - how Firefox exposes content to assistive technology.
- [Chromium Accessibility Documentation](https://chromium.googlesource.com/chromium/src/+/HEAD/docs/accessibility/) - how Chromium-based applications expose the accessibility tree.
- [AT-SPI2 Core](https://gnome.pages.gitlab.gnome.org/at-spi2-core/) - the Linux assistive technology service provider interface.
