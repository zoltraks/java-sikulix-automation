# Java and Maven Engineering Standards

<!-- Version: 0.1.1 | Date: 2026-10-08 -->

## Purpose

This document defines the engineering standard for Java projects built with Apache Maven.

It covers the POM, the directory layout, the dependency management, the packaging, the multi-module structure, and the build commands.

It is written to be executed directly by an AI coding agent and to be read without effort by a human reviewer.

## Scope

A project is in scope when its build is driven by a `pom.xml` file and Maven.

The scope covers build configuration and packaging only.

Language conventions and framework behaviour are out of scope in this document, because they follow rules independent of the build tool.

A project built with Gradle or another build tool is out of scope, because build configuration conventions differ per build tool.

## How To Use This Standard

This section is the entry point for an agent.

Read it before reading anything else in this document.

### Order Of Operations

Follow these steps in order at the start of every task that touches the Maven build.

1. Read the project rules file, such as `AGENTS.md`, `README.md`, or `docs/GUIDELINES.md`, because a project rule overrides this standard.
2. Determine the Maven version, the Java version, and the project shape using the Agent Intake Protocol below.
3. Confirm the conclusion with the user when the repository is ambiguous or empty.
4. Apply the sections of this standard that match the confirmed project shape.
5. Run every applicable row of the Verification section.
6. Check the result against the Definition of Done before reporting the task complete.

### Precedence

When two rules conflict, apply the first matching source in this list.

| Rank | Source                                    | Example                                             |
|------|-------------------------------------------|-----------------------------------------------------|
| 1    | An explicit instruction from the user     | "Keep the shade plugin, do not switch to assembly"  |
| 2    | The project rules file                    | `AGENTS.md`, `docs/GUIDELINES.md`                   |
| 3    | The existing convention in the repository | `dependencyManagement` already centralises versions |
| 4    | This standard                             | Versions pinned in `properties`                     |
| 5    | General Maven best practice               | Anything the four sources above do not cover        |

Never silently restructure an existing build to match this standard.

A build restructure can break packaging, CI, and released artifacts at once.

Bring a deviation to the user as a proposal, not as an unrequested edit.

### Non-Negotiable Rules

- **Every build succeeds from a clean checkout.** A build that needs a manual step that is not documented is a defect.
- **Dependency versions are pinned.** A versionless or range dependency resolves differently over time and is a defect.
- **`target/` is gitignored.** Committed build output is a defect.
- **No snapshots in a released build.** A `-SNAPSHOT` dependency in a tagged release cannot be rebuilt identically.
- **`mvn package` is the packaging entry point.** A custom script that bypasses the lifecycle is a defect, because it hides the real build steps.

A violation of any of these rules is a defect, not a style preference.

## Agent Intake Protocol

### Detection First

Before asking the user anything, inspect the repository and infer the build shape.

| Signal Found In The Repository                | Inferred Decision                   | Confidence |
|-----------------------------------------------|-------------------------------------|------------|
| `<packaging>pom</packaging>` with `<modules>` | Multi-module aggregator build       | High       |
| `.mvn/wrapper/` present                       | Maven Wrapper is the entry point    | High       |
| `maven-assembly-plugin` configured            | Fat jar via assembly descriptor     | High       |
| `maven-shade-plugin` configured               | Shaded fat jar                      | High       |
| `spring-boot-maven-plugin` configured         | Framework repackaging in use        | High       |
| `maven.compiler.source`/`target` properties   | Effective source level              | High       |
| `<release>` on `maven-compiler-plugin`        | Compiler release flag in use        | High       |
| `dependencyManagement` section                | Centralised version pinning         | High       |
| `maven-wrapper.properties`                    | Pinned Maven version                | High       |
| `settings.xml` committed                      | Project-specific Maven settings     | High       |
| `frontend-maven-plugin` configured            | Non-Java toolchain bridged by Maven | High       |

### Existing Project

When detection is confident, state the conclusion and ask for a single confirmation rather than running a questionnaire.

Example: "I see a multi-module Maven build with a root aggregator `pom.xml`, Java 21, and the Maven Wrapper. I will follow the existing build conventions. Is that correct?"

When detection is ambiguous, ask only the questions that resolve the ambiguity.

### New Project

When the repository is empty or the user declares a new project, ask the following questions.

| Question                       | Options                                  | Default If Declined     |
|--------------------------------|------------------------------------------|-------------------------|
| Single module or multi-module? | Single, multi-module                     | Single                  |
| Fat jar or plain jar?          | Assembly/shade fat jar, plain jar, war   | Plain jar for libraries |
| Maven Wrapper?                 | Include wrapper, rely on installed Maven | Include wrapper         |

## Documentation

The following are the authoritative sources for Maven.

- [Apache Maven Documentation](https://maven.apache.org/guides/) - the official guides.
- [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) - the conventional project structure.
- [Maven Lifecycle Reference](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) - phases and goals.
- [Maven Dependency Mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html) - scopes, transitive resolution, and `dependencyManagement`.
- [Maven POM Reference](https://maven.apache.org/pom.html) - the full POM element reference.
- [Maven Wrapper](https://maven.apache.org/tools/wrapper/) - the pinned-version launcher.

## The POM

### Coordinates

Every artifact declares `groupId`, `artifactId`, and `version`.

The `groupId` follows reverse-domain organisation naming.

The `artifactId` is lowercase with hyphens.

Versions are literal values or properties, never open ranges.

### Properties

Shared values live in `<properties>`: the Java version, encoding, and dependency versions used in more than one declaration.

`project.build.sourceEncoding` is `UTF-8`, because platform-default encoding makes a build machine-dependent.

### Hygiene

A POM explains itself through element names - a comment narrating what a plugin does, such as "build an executable JAR", rots against the configuration it describes and is removed.

The exception is provenance: a comment that is only a repository-index URL above a dependency, for example `<!-- https://mvnrepository.com/artifact/group/artifact -->`, records where the artifact was resolved from and is kept.

### Java Version

Declare the Java level once, in properties, and reference it from the compiler configuration.

Prefer `<release>` on `maven-compiler-plugin` over `source`/`target` pairs on JDK 9 or newer, because `release` also pins the platform API instead of only the language level.

```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.release>${java.version}</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### Dependency Management

Versions for dependencies used by more than one module live in the parent's `dependencyManagement` section, because scattered versions drift apart over time.

A bill-of-materials import with `<scope>import</scope>` pins a whole technology family's versions at once.

### Scopes

Use the narrowest scope that satisfies the need.

| Scope      | Use For                                  |
|------------|------------------------------------------|
| `compile`  | Default: needed to compile and run       |
| `provided` | Supplied by the runtime environment      |
| `runtime`  | Needed at runtime but not compile time   |
| `test`     | Test code only                           |
| `import`   | BOM import inside `dependencyManagement` |

## Directory Layout

```
src/
  main/
    java/          production sources
    resources/     classpath resources
  test/
    java/          test sources
    resources/     test resources
target/            build output, gitignored
```

Sources live only under these directories.

A non-standard `sourceDirectory` needs a reason recorded in the project rules file.

## Multi-Module Builds

A multi-module build declares an aggregator POM with `<packaging>pom</packaging>` and a `<modules>` list.

Each module keeps its own `artifactId` and may declare the aggregator or a dedicated parent as `<parent>`.

Shared versions and plugin configuration live in the parent, because each module otherwise repeats them.

Module dependencies go through declared Maven dependencies, never through relative-path hacks, because the reactor resolves them only from declarations.

## Plugins

Plugin versions are pinned in `pluginManagement` or directly on the plugin declaration, because an unpinned plugin resolves to whatever version was latest at build time.

Common plugins and their roles:

| Plugin                    | Role                                       |
|---------------------------|--------------------------------------------|
| `maven-compiler-plugin`   | Source level, `release`, annotation paths  |
| `maven-surefire-plugin`   | Unit test execution                        |
| `maven-failsafe-plugin`   | Integration test execution                 |
| `maven-jar-plugin`        | JAR creation and manifest `Main-Class`     |
| `maven-assembly-plugin`   | `jar-with-dependencies` fat jar            |
| `maven-shade-plugin`      | Fat jar with class relocation              |
| `maven-enforcer-plugin`   | Build rules such as Java/Maven minimums    |
| `maven-checkstyle-plugin` | Style enforcement                          |
| `versions-maven-plugin`   | Dependency version reporting and updates   |
| `frontend-maven-plugin`   | Bridges non-Java toolchains into the build |

## Packaging

### Plain JAR

A library or application whose dependencies ship separately uses a plain JAR.

`maven-jar-plugin` writes the manifest, and an executable JAR declares `Main-Class`.

### Fat JAR

A runnable artifact that bundles its dependencies uses `maven-assembly-plugin` with `jar-with-dependencies` or `maven-shade-plugin`.

Assembly unpacks every dependency into the output JAR, which is simple but can collide on same-named files such as `META-INF` entries.

Shade merges dependencies and supports relocation and transformers, which resolves the collisions and is the safer choice for larger artifacts.

Framework-specific repackaging is configured through that framework's own plugin and replaces these options.

### War

A servlet-container deployment uses `<packaging>war</packaging>` and `maven-war-plugin`.

Embedded-container deployments do not need a war.

## POM Archetypes

Two shapes recur in practice: the standalone utility jar and the application backend paired with a separately built frontend.

### Standalone Utility Jar

A self-contained command-line tool declares the compiler level directly, fixes the artifact name, and bundles dependencies into one runnable jar.

```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.1.0</version>
            <configuration>
                <finalName>my-tool</finalName>
                <archive>
                    <manifest>
                        <mainClass>com.example.tool.Main</mainClass>
                    </manifest>
                </archive>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>3.1.1</version>
            <configuration>
                <finalName>my-tool</finalName>
                <appendAssemblyId>false</appendAssemblyId>
                <archive>
                    <manifest>
                        <mainClass>com.example.tool.Main</mainClass>
                    </manifest>
                </archive>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id>
                    <phase>package</phase>
                    <goals><goal>single</goal></goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

On Java 8 set `source`/`target` explicitly, because the `release` flag does not cover targets below 9.

`finalName` fixes the artifact name so scripts and documentation reference it without a version suffix, and `appendAssemblyId` disabled keeps the fat jar at that name.

### Application Backend With Separate Frontend

A web application on a recent Java level uses the framework parent for managed versions and its repackaging plugin for the executable jar.

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.3</version>
    <relativePath/>
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

An empty `<relativePath/>` tells Maven the parent is a repository artifact, not a sibling file.

The framework parent manages dependency and plugin versions, so its starters carry no `<version>` element.

The separate frontend module is a `pom`-packaged leaf that installs the toolchain and runs it inside the Maven lifecycle.

```xml
<packaging>pom</packaging>

<build>
    <plugins>
        <plugin>
            <groupId>com.github.eirslett</groupId>
            <artifactId>frontend-maven-plugin</artifactId>
            <version>1.12.1</version>
            <configuration>
                <nodeVersion>v18.16.0</nodeVersion>
                <npmVersion>9.5.1</npmVersion>
            </configuration>
            <executions>
                <execution>
                    <id>install-node-and-npm</id>
                    <goals><goal>install-node-and-npm</goal></goals>
                </execution>
                <execution>
                    <id>npm-build</id>
                    <goals><goal>npm</goal></goals>
                    <phase>prepare-package</phase>
                    <configuration><arguments>run build</arguments></configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

The backend embeds the built frontend by copying its output into `static/` during `prepare-package`, so one jar serves both.

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-frontend-build</id>
            <goals><goal>copy-resources</goal></goals>
            <phase>prepare-package</phase>
            <configuration>
                <outputDirectory>target/classes/static</outputDirectory>
                <overwrite>true</overwrite>
                <resources>
                    <resource><directory>../my-frontend/build</directory></resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

A relative-path resource copy couples the modules' directory layout, so record the layout assumption in the project rules and prefer an aggregator build when the coupling must be explicit.

## Maven Wrapper

A project that includes `.mvn/wrapper/` builds through `mvnw`, which downloads the pinned Maven version and makes the build reproducible on machines without Maven installed.

The wrapper script is used for every build command where present, because a different Maven version can resolve plugins differently.

## Profiles and Settings

Maven profiles in `pom.xml` hold environment-specific build variants.

Machine-local values such as credentials and repository mirrors live in `settings.xml` under the user's `.m2` directory, never in the POM, because committed credentials are a security incident.

The local repository `~/.m2/repository` is a cache and never a delivery mechanism.

## Versioning

A build under development carries `-SNAPSHOT`, and a released build carries a fixed version.

Dependency versions in use are checked with `versions:display-dependency-updates` before updating, and updates are applied deliberately, never by range.

## Common Commands

| Command                    | Purpose                                   |
|----------------------------|-------------------------------------------|
| `mvn package`              | Compile, test, and package                |
| `mvn install`              | Package and install to the local repo     |
| `mvn test`                 | Run unit tests                            |
| `mvn test -Dtest=NameTest` | Run one test class                        |
| `mvn dependency:tree`      | Inspect the resolved dependency graph     |
| `mvn checkstyle:check`     | Style check when the plugin is configured |
| `mvn enforcer:enforce`     | Enforcer rules when configured            |
| `mvn -T 1C package`        | Parallel build, one thread per core       |
| `./mvnw package`           | Wrapper-based build where present         |

## Verification

A build wires a source-level static analyzer, because `javac` lint flags do not cover unused declarations at all - an unused local is invisible outside an IDE. A reporting analyzer such as PMD (`maven-pmd-plugin` with a focused ruleset covering unused locals, parameters, fields, and private methods) binds to the package lifecycle so findings print during `mvn package`. The analyzer reports - whether it also fails the build (`failOnViolation`) is a project decision recorded in the project's own rules.

| Check                             | Command Or Method                                   | Applies To     |
|-----------------------------------|-----------------------------------------------------|----------------|
| Build succeeds                    | `mvn package` or `./mvnw package`                   | All modules    |
| Dependency graph resolves         | `mvn dependency:tree`                               | All modules    |
| No unpinned plugin versions       | Inspect plugin declarations in `pom.xml`            | All POMs       |
| `target/` ignored                 | `target/` present in `.gitignore`                   | Every module   |
| No snapshots in release artifacts | Grep for `SNAPSHOT` outside `version` of the module | Release builds |
| Encoding declared                 | `project.build.sourceEncoding` set to `UTF-8`       | All POMs       |
| Static analysis runs              | Analyzer findings print in the build output         | All modules    |

## Definition of Done

### Correctness

- `mvn package` succeeds from a clean checkout.
- The dependency graph resolves without conflicts reported by `dependency:tree`.
- Every dependency and plugin version is pinned.

### Structure

- The directory layout matches the standard layout or a recorded deviation.
- Multi-module builds declare an aggregator and resolve modules through dependencies.

### Quality

- Encoding is `UTF-8` everywhere.
- No machine-local paths or credentials appear in the POM.

### Hygiene

- `target/` is gitignored in every module.
- No `-SNAPSHOT` dependency was introduced into a release build.

## General Principles

**Declared Not Scripted.** Build behaviour lives in the POM and its plugins, because a custom script hides steps the lifecycle already models.

**Pin Everything.** A version left floating today resolves differently tomorrow.

**Aggregator Over Sibling Paths.** Modules reference each other through coordinates, because the reactor orders builds only through declared dependencies.

**Local Is Cached, Remote Is Published.** The `.m2` repository is a local cache and never the delivery mechanism for shared artifacts.

## Sources

The following authoritative references support the rules in this document.

- [Apache Maven Documentation](https://maven.apache.org/guides/) - the official guides.
- [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) - the conventional project structure.
- [Maven Lifecycle Reference](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) - phases and goals.
- [Maven Dependency Mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html) - scopes, transitive resolution, and `dependencyManagement`.
- [Maven POM Reference](https://maven.apache.org/pom.html) - the full POM element reference.
- [Maven Assembly Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/) - `jar-with-dependencies` packaging.
- [Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/) - relocated fat-jar packaging.
- [Maven Enforcer Plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/) - build-rule enforcement.
- [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) - style enforcement.
- [Versions Maven Plugin](https://www.mojohaus.org/versions/versions-maven-plugin/) - dependency version reporting and updates.
- [Maven Wrapper](https://maven.apache.org/tools/wrapper/) - the pinned-version launcher.
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/) - vulnerability scanning for dependencies.
