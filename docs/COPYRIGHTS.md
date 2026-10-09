# Copyrights

Sikulix Automation is licensed under MIT, declared by the project owner during repository preparation.

The license text lives in [`LICENSE.md`](../LICENSE.md) at the project root.

## Fundamental Rules

- All code must be original and not copied from existing solutions.
- Never copy code from other sources without proper licensing and attribution.
- Never accept AI-generated code that resembles or duplicates code from other projects - always verify originality.
- When adding external libraries or dependencies, verify license compatibility before use.
- Document the source and license of any third-party code or libraries used.
- Do not include copyrighted material without explicit permission.
- These rules apply to code, documentation, images, and any other project assets.

## Project License

The project is licensed under MIT, declared by the owner.

The canonical MIT license text lives in [`LICENSE.md`](../LICENSE.md) at the project root.

## Dependency Licensing

Dependencies are declared in `pom.xml`.

Current dependencies and their licenses:

| Dependency                  | Version | License    |
|-----------------------------|---------|------------|
| `com.sikulix:sikulixapi`    | 2.0.5   | MIT        |
| `com.google.code.gson:gson` | 2.9.0   | Apache-2.0 |
| `commons-cli:commons-cli`   | 1.5.0   | Apache-2.0 |

Permitted license classes: MIT, Apache-2.0, BSD-2-Clause, BSD-3-Clause, ISC.

Excluded license classes: GPL-family (GPL, LGPL, AGPL) and proprietary licenses - never add them without explicit owner approval.

The packaged jar bundles dependencies (`jar-with-dependencies`) - a bundled dependency's license must permit redistribution.

## License Review Convention

License compliance is checked whenever a dependency is added or upgraded in `pom.xml`, and at every release.

Record the outcome in the change record that carried the dependency change.

## Asset Licensing

- `media/image/images.png` is a project screenshot - treat images, icons, and UI captures as project assets requiring original creation or compatible licensing.
- Scenario images are screen captures of the user's own desktop - they stay out of the repository unless sanitized.

## Header Convention

No header convention - the existing sources carry no copyright header and new files follow suit.
