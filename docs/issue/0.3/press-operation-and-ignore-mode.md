# Add press key operation and ignore mode

**Type:** Feature

**Status:** Done

## Summary

Add a `press` step that sends real key presses with optional modifiers, and an
`--ignore` mode that prints step errors without stopping the scenario.

## Description

The `type` step types literal text only - `screen.type("#A.F4.")` writes the
characters rather than pressing Alt+F4, so a scenario cannot close an
application or send shortcuts. A dedicated `press` operation is needed that
sends a single key with optional modifiers through SikuliX key APIs.

Two modifier notations are required: symbol prefixes `!` (Shift), `@` (Alt),
`^` (Ctrl), `$` (Win), and `Name+Key` form such as `Alt+A` or `Ctrl+Alt+G`,
case-insensitive, with `control`, `meta`, and `cmd` aliases. A `press` element
accepts one key specification or an array of them. Each element is exactly one
key press, so a multi-character value that is not a known key name is an error.
In script files, `PRESS` accepts plain or double-quoted values and multiple
space-separated values per line.

Separately, a scenario currently stops on the first failed step. An `--ignore`
command line flag and a matching `"ignore"` configuration field make errors
print while the scenario continues at the next step.

## Steps to Reproduce

1. Run a scenario with `{ "type": "#A.F4." }` against a Notepad window with
   unsaved text.
2. Observe the literal text `#A.F4.` typed into the document and Notepad still
   open.

## Expected Result

`{ "press": "Alt+F4" }` sends the Alt+F4 keystroke, `PRESS @F4` does the same
in a script file, and a failed step under `--ignore` prints its error while
the scenario continues.

## Actual Result

`type` writes the encoding text verbatim and no key press occurs. Any step
failure aborts the scenario.

## Environment

Java 8 target, SikuliX API 2.0.5, Windows.

## Remediation

Execute key presses through `Screen.type(String, int)` with a `KeyModifier`
bitmask, resolving key names to `Key.*` constants.

## Scope

Covers `press` in JSON and script scenario formats, modifier parsing, the
named-key table, the `--ignore` / `"ignore"` failure mode, and documentation.

Does not change `type` semantics, other step operations, or SikuliX itself.

## Risk

- `press` field must accept both string and array JSON values without breaking
  Gson binding of `Step`.
- Quoted `PRESS` values in script files require unquoting that other
  operations do not have. The unquoting must not alter their behavior.
- Ignore mode affects every step failure, not only `press`.

## Affected Documentation

- `README.md` - step list, `press` section, command line and configuration
  options, Project Rules wording.

## Implementation Plan

1. Add `StringUtility.splitTokens` - whitespace split honoring double quotes
   with `""` escapes.
2. Add `ignore` to `Settings` and `Configuration`, mirroring `verbose`.
3. Add `JsonElement press` to `Step`, a `PRESS` case in `create()` that builds
   primitives or arrays from tokens, and a `PRESS` line in `toString()`.
4. Add the `press` handler, spec parser, and key-name table to `Automation`,
   executed through `screen.type(key, modifiers)`.
5. Restructure the failure branch in `Flow.next()` so errors always print and
   `ignore` continues the scenario.
6. Add the `-i`/`--ignore` option and configuration wiring in `App`.
7. Update `README.md`: drop "AI" from Project Rules, add `press` to the step
   list with a full section, document `--ignore` and `"ignore"`, fix the
   `type` example key.
8. Update `work/config.json` to close Notepad with `press` steps, and add
   `work/notepad.script` and `work/config-play.json`.

## Definition of Done

- [x] `{"press": "Alt+F4"}` and `{"press": ["Ctrl+Alt+G", "X"]}` parse and
      press the intended keys.
- [x] Symbol forms `^!X`, `!^X`, `@F4`, `$R` produce identical semantics.
- [x] `PRESS A`, `PRESS "A"`, and `PRESS @F4 N` work in script files.
- [x] An invalid press element fails the step. Under `--ignore` the error is
      printed and the scenario continues.
- [x] The Notepad scenario opens, types `HELLO`, closes via Alt+F4 and the
      Don't Save mnemonic in both JSON and script forms.
- [x] `mvn -T 1C package` builds cleanly and `git diff --check` is clean.

## Outcome

All eight plan steps delivered and verified live. `screen.type(key, modifiers)`
presses with the modifier mask held, confirmed by SikuliX logs such as
`( Alt )  TYPE "#F4."`. Single string, JSON array, and space-separated or
quoted script values all resolve. Invalid specs produce `Press failed for`
errors that stop the scenario normally and print-and-continue under
`--ignore`. A lone modifier symbol (`PRESS $`) presses that character since a
single character is always the key itself.

Deviation found during verification: the Windows 11 Notepad save prompt is an
in-window overlay whose Don't Save mnemonic requires `Alt+N`, not a bare `N`.
The scenarios use `"press": "Alt+N"` / `PRESS @N` accordingly.

Known leftover: a `HELLO#A.F4.n` tab in the pre-existing Notepad instance
remains from the earlier `type` experiment. It holds unsaved text in a tab the
user owns and was left untouched.
