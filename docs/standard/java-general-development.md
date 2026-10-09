# Java Engineering Standards

<!-- Version: 0.1.0 | Date: 2026-10-08 -->

## Purpose

This document defines the engineering standard for Java source code.

It covers the language version, the project layout, the coding conventions, the error handling, the testing, and the formatting.

It is written to be executed directly by an AI coding agent and to be read without effort by a human reviewer.

## Scope

A project is in scope when its production code is written in Java and runs on the JVM.

The scope covers the language and the code conventions only.

Build-tool configuration, application frameworks, and persistence technology are out of scope in this document, because each has its own conventions that a different project on the same language may not share.

A project written in Kotlin, Scala, Groovy, or another JVM language is out of scope, because those languages have their own conventions.

An Android application is out of scope, because the Android toolchain and lifecycle follow different conventions.

## How To Use This Standard

This section is the entry point for an agent.

Read it before reading anything else in this document.

### Order Of Operations

Follow these steps in order at the start of every task that touches Java code.

1. Read the project rules file, such as `AGENTS.md`, `README.md`, or `docs/GUIDELINES.md`, because a project rule overrides this standard.
2. Determine the Java version, the build tool, and the test framework using the Agent Intake Protocol below.
3. Confirm the conclusion with the user when the repository is ambiguous or empty.
4. Apply the sections of this standard that match the confirmed project shape.
5. Run every applicable row of the Verification section.
6. Check the result against the Definition of Done before reporting the task complete.

### Precedence

When two rules conflict, apply the first matching source in this list.

| Rank | Source                                    | Example                                         |
|------|-------------------------------------------|-------------------------------------------------|
| 1    | An explicit instruction from the user     | "Keep this project on Java 8"                   |
| 2    | The project rules file                    | `AGENTS.md`, `docs/GUIDELINES.md`               |
| 3    | The existing convention in the repository | TestNG already used for every test              |
| 4    | This standard                             | `Optional` for return values that may be absent |
| 5    | General Java best practice                | Anything the four sources above do not cover    |

Never silently reformat existing code to match this standard.

A whole-repository reformat destroys review history and hides the real change inside noise.

Bring a deviation to the user as a proposal, not as an unrequested edit.

### Non-Negotiable Rules

- **No raw types.** A `List` without a type parameter is a defect, because it bypasses the type system.
- **No checked exceptions in new code.** Wrap in a `RuntimeException` subclass, because checked exceptions force callers to handle failures they cannot meaningfully recover from.
- **No hardcoded secrets.** A secret in a committed file is a security incident.
- **No swallowed exceptions.** A `catch` block that ignores the exception hides a failure, because the program continues in an unknown state.
- **No `System.out.println` for diagnostics.** Diagnostic output goes through a logging facade, because console output cannot be filtered or routed. Writing to `System.out` is allowed where stdout is the program's interface, such as the output of a command-line tool.
- **No `null` returns from public methods.** Return `Optional`, an empty collection, or throw, because a `null` return forces every caller to remember to check.
- **Tests pass where a test suite exists.** A failing test is a defect.

A violation of any of these rules is a defect, not a style preference.

## Agent Intake Protocol

### Detection First

Before asking the user anything, inspect the repository and infer the project shape.

| Signal Found In The Repository               | Inferred Decision        | Confidence |
|----------------------------------------------|--------------------------|------------|
| `pom.xml` at root                            | Maven build              | High       |
| `build.gradle` or `build.gradle.kts`         | Gradle build             | High       |
| `<java.version>` or `<release>` in `pom.xml` | Declared Java version    | High       |
| `maven.compiler.source`/`target` values      | Effective source level   | High       |
| `junit-jupiter` dependency                   | JUnit 5 or 6             | High       |
| `junit:junit` dependency                     | JUnit 4 (legacy)         | High       |
| `testng` dependency                          | TestNG                   | High       |
| `mockito` dependency                         | Mockito                  | High       |
| `assertj` dependency                         | AssertJ                  | High       |
| `jakarta.*` imports                          | Jakarta EE namespace     | High       |
| `javax.*` imports                            | Legacy Java EE namespace | High       |
| `lombok` dependency                          | Lombok in use            | High       |
| No Java files at all                         | New project              | High       |

### Existing Project

When detection is confident, state the conclusion and ask for a single confirmation rather than running a questionnaire.

Example: "I see Java 21 in `pom.xml`, a Maven build, and JUnit 5 for testing. I will follow the existing conventions. Is that correct?"

When detection is ambiguous, ask only the questions that resolve the ambiguity.

### New Project

When the repository is empty or the user declares a new project, ask the following questions.

| Question               | Options                      | Default If Declined |
|------------------------|------------------------------|---------------------|
| Which Java version?    | Java 25 (LTS), Java 21 (LTS) | Java 25 (LTS)       |
| Which test framework?  | JUnit 5, TestNG              | JUnit 5             |
| Which mocking library? | Mockito, EasyMock, none      | Mockito             |

## Documentation

The following are the authoritative sources for Java development.

- [Java Language Specification](https://docs.oracle.com/javase/specs/) - the language definition.
- [OpenJDK Project JDK 25](https://openjdk.org/projects/jdk/25/) - the current LTS release information.
- [OpenJDK Project JDK 21](https://openjdk.org/projects/jdk/21/) - the previous LTS release information.
- [Oracle Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) - release cadence and support lifecycles.
- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html) - the original naming-convention baseline.
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) - the common stricter alternative a project may adopt.
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) - the test framework conventions.
- [TestNG Documentation](https://testng.org/) - the alternative test framework.
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito.Mockito.html) - the mocking library.
- [AssertJ Documentation](https://assertj.github.io/doc/) - fluent assertions.
- [SLF4J Manual](https://www.slf4j.org/manual.html) - the logging facade.

## Language Version

The project Java version is taken from the repository, not from this standard.

A new project targets the current long-term support release unless the project rules file or the environment dictates otherwise.

Long-term support releases and their status:

| Release | General Availability | Status                     |
|---------|----------------------|----------------------------|
| Java 8  | March 2014           | LTS, extended support only |
| Java 11 | September 2018       | LTS, extended support only |
| Java 17 | September 2021       | LTS, supported             |
| Java 21 | September 2023       | LTS, supported             |
| Java 25 | September 2025       | LTS, current               |
| Java 29 | planned Sept 2027    | LTS, upcoming              |

Non-LTS feature releases are supported only until the next feature release, so a non-LTS target in an existing project deserves an upgrade note.

### Feature Availability

Language features apply only where the project's Java version provides them.

| Feature                       | Since   | Use When                                     |
|-------------------------------|---------|----------------------------------------------|
| `var` local type inference    | Java 10 | The inferred type is immediately obvious     |
| Text blocks                   | Java 15 | Multi-line strings such as SQL or JSON       |
| Records                       | Java 16 | DTOs and immutable value types               |
| Sealed classes and interfaces | Java 17 | Domain hierarchies with a closed subtype set |
| Pattern matching for `switch` | Java 21 | Type-driven branching                        |
| Virtual threads               | Java 21 | I/O-bound concurrency                        |

A project on Java 8 or Java 11 uses none of these, and that is correct for its version.

## Project Structure

One `.java` file per public class.

The file name matches the public class name.

All classes live under a single root package derived from the organisation domain.

Split a file when a class exceeds approximately 300 lines.

No circular dependencies between packages, because a package cycle makes the code impossible to build and test in isolation.

## Naming Conventions

| Element                | Convention                                   | Example                          |
|------------------------|----------------------------------------------|----------------------------------|
| Classes and interfaces | `PascalCase`                                 | `EntityService`, `CacheProvider` |
| Methods                | `camelCase`                                  | `loadConfig`, `enqueueWrite`     |
| Variables              | `camelCase`                                  | `currentBatch`, `lockTimeout`    |
| Constants              | `UPPER_SNAKE_CASE`                           | `DEFAULT_BATCH_SIZE`             |
| Packages               | all lowercase, single words, no underscores  | `com.example.service`            |
| Enums                  | `PascalCase` type, `UPPER_SNAKE_CASE` values | `OperationType.ADD`              |
| Source files           | `PascalCase.java`                            | `EntityService.java`             |
| Test files             | `PascalCaseTest.java`                        | `EntityServiceTest.java`         |

## Code Conventions

### Records for Value Types

Use records for data carriers and immutable value types where the Java version permits, because a record provides equality and accessors without boilerplate.

```java
public record BatchResult(
    String op,
    String status,
    int index
) {}
```

On Java versions without records, an immutable class with `final` fields carries the same intent.

### Error Handling

Define domain-specific exceptions extending `RuntimeException`.

Never catch and silently discard an exception, because a swallowed exception hides a failure.

Catch an exception only where the code can act on it meaningfully, and otherwise let it propagate.

```java
public class LockTimeoutException extends RuntimeException {
    private final String resourceId;

    public LockTimeoutException(String resourceId) {
        super("Lock timeout for resource: " + resourceId);
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }
}
```

### Null Handling

Use `Optional<T>` for return values that may be absent.

Never return `null` from a public method, because a `null` return forces every caller to remember to check.

Annotate parameters that accept `null` when the codebase uses a nullability annotation convention.

### Forbidden Patterns

- `var` where the inferred type is not immediately visible, because an explicit type documents intent.
- Raw types such as `List` instead of `List<String>`, because a raw type bypasses the type system.
- Checked exceptions in new code, because checked exceptions force callers to handle failures they cannot meaningfully recover from.
- `System.out.println` for diagnostics, because console output cannot be filtered or routed.
- Hardcoded secrets or credentials in source files, because a secret in a committed file is a security incident.
- `equals` without `hashCode`, because collections then misbehave silently.
- `Date` and `Calendar` in new code, because `java.time` types are immutable and unambiguous.

## Logging

Use SLF4J as the logging facade for libraries and services, because the facade allows the implementation to change without touching application code.

Log levels: `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`.

All log messages are in English.

Do not log sensitive data, because a log file is a lower-trust storage medium than the application itself.

```java
private static final Logger log = LoggerFactory.getLogger(EntityService.class);

log.info("Loading entity: entityId={}", entityId);
log.error("Lock timeout: entityId={}, timeout={}ms", entityId, timeout, ex);
```

A command-line tool may write its payload to `System.out` and diagnostics to `System.err`, because stdout is the interface in that project kind.

## Comments

Code is self-documenting through naming.

Limit comments to the absolute minimum.

- Javadoc on public API classes and methods, with a one-sentence summary.
- A `// REASON:` comment on non-obvious decisions.
- No commented-out code, because version control preserves history.

## Testing

### Framework

Use JUnit 5 for new projects and new test suites.

Where a project already uses JUnit 4, TestNG, or another framework, follow the existing convention, because mixing frameworks doubles the test infrastructure and confuses contributors.

Use Mockito for mocking and AssertJ for fluent assertions where the project adopts them.

### Requirements

Tests cover normal cases, boundary values, and error paths.

A test names the behaviour it verifies, not the method it calls.

### Test Style

Use `@Test`, `@DisplayName`, and `@Nested` for organisation in JUnit 5.

Use `@BeforeEach` and `@AfterEach` for setup and teardown.

```java
@Nested
@DisplayName("BackgroundQueue")
class BackgroundQueueTest {
    @Test
    @DisplayName("batches operations within debounce window")
    void batchesWithinDebounce() {
        // ...
    }
}
```

### Test Doubles

Prefer hand-written fakes over mocks for collaborators with complex state, because a fake captures the contract once instead of in every test.

## Formatting

- 4-space indentation, no tabs.
- 120-character line limit.
- Braces on the same line as the declaration.
- No wildcard imports, because an explicit import documents the dependency.
- One statement per line.

Where the project configures an automated formatter or style checker, its rules take precedence over these defaults.

## Verification

| Check                               | Command Or Method                                      | Applies To     |
|-------------------------------------|--------------------------------------------------------|----------------|
| Build succeeds                      | The project's build command                            | All modules    |
| Tests pass                          | The project's test command                             | All test code  |
| No `System.out.println` diagnostics | Grep for `System.out` in `src/main/`                   | All Java files |
| No hardcoded secrets                | Grep for `password`, `secret`, `apiKey` in `src/main/` | All Java files |
| No raw types                        | Grep or compiler lint for raw generic types            | All Java files |
| No swallowed exceptions             | Grep for empty `catch` blocks                          | All Java files |

## Definition of Done

### Correctness

- Every module compiles without warnings.
- No raw types were introduced.
- No checked exceptions were introduced in new code.
- Tests cover normal cases, boundary values, and error paths.

### Structure

- The project layout matches the confirmed project shape.
- No circular dependency was introduced.
- No class exceeds approximately 300 lines without a justification.

### Quality

- No diagnostic `System.out.println` was introduced.
- No debugging output or commented-out code remains.
- Comments explain reasons, not actions.

### Hygiene

- The build output directory is gitignored.
- No secret, token, or credential was committed.
- Documentation was updated when the change altered the public API or a project rule.

## General Principles

**Single Responsibility.** One class does one thing and its name says which thing.

**Composition Over Inheritance.** Prefer composition, because deep hierarchies couple behaviour that changes for different reasons.

**No Global State.** Avoid static mutable fields, because they make tests order-dependent and concurrency unsafe.

**Fail Fast.** Detect invalid state at the earliest point, because a delayed failure hides the cause.

**Immutability by Default.** Prefer `final` fields and immutable types, because shared mutable state is the root of most concurrency defects.

## Sources

The following authoritative references support the rules in this document.

- [Java Language Specification](https://docs.oracle.com/javase/specs/) - the definitive language definition.
- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html) - the original naming-convention baseline.
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) - the common stricter alternative a project may adopt.
- [Oracle Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html) - LTS releases and support lifecycles.
- [OpenJDK Projects](https://openjdk.org/) - the JDK feature releases and JEPs.
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) - the test framework conventions.
- [TestNG Documentation](https://testng.org/) - the alternative test framework.
- [SLF4J Manual](https://www.slf4j.org/manual.html) - the logging facade contract.
