# COS 730 Assignment 2

This repository contains two versions of the 730 Assignment 2:

- `original`: the baseline/original implementation.
- `optimised`: the refactored implementation with clearer service boundaries and evaluation strategy separation.

Both versions let a user capture a research submission, assign available reviewers, and submit reviewer scores.

## Prerequisites

- JDK 17 or later.
- Maven installed and available on your `PATH`.
- An IDE that can open Maven projects, such as IntelliJ IDEA, Eclipse, NetBeans, or VS Code with Java extensions.

## Project Structure

```text
.
|-- original/
|   |-- pom.xml
|   `-- src/main/java/com/cos730/
|       |-- UI.java
|       |-- SubmissionController.java
|       `-- ...
|-- optimised/
|   |-- pom.xml
|   `-- src/main/java/com/cos730/
|       |-- UI.java
|       |-- SubmissionController.java
|       |-- EvaluationService.java
|       |-- ConsensusEvaluationStrategy.java
|       `-- ...
~-- cos730_assignment2.db
```

## Build and Test

Run these commands from the repository root.

### Original Version

```powershell
cd original
mvn compile
```

### Optimised Version

```powershell
cd optimised
mvn compile
```

`mvn test` compiles the selected project and runs the Maven test phase.

## Run the Application

The main class for both versions is:

```text
com.cos730.UI
```

### Run the Original Version with Maven

From the repository root:

```powershell
cd original
mvn exec:java
```

### Run the Optimised Version with Maven

From the repository root:

```powershell
cd optimised
mvn exec:java
```

If Maven needs to download the exec plugin the first time, make sure you are connected to the internet.

## Using Either Version

### Submit a Research Output

1. Enter the research title.
2. Enter the researcher name.
3. Enter keywords.
4. Enter the abstract.
5. Click `Submit Research`.

If validation fails, the UI displays an error message. If validation succeeds, the submission is saved and eligible reviewers are processed.

### Submit a Reviewer Score

1. Enter the submission title.
2. Choose a reviewer from the dropdown.
3. Enter a score between `0` and `100`.
4. Click `Submit Review Score`.

## Database Notes

- Both versions use SQLite through `sqlite-jdbc`.
- Each project also contains its own `cos730_assignment2.db` file.
- The database path in the code is relative: `cos730_assignment2.db`.
- If you run from inside `original`, that version uses `orginal/cos730_assignment2.db`.
- If you run from inside `optimised`, that version uses `optimised/cos730_assignment2.db`.

To reset local data for a version, delete that version's `cos730_assignment2.db` file and rerun the application.

## Troubleshooting

- If `mvn` is not recognized, install Maven and reopen your terminal.
- If the Swing window does not appear, run `UI.java` directly from your IDE.
- If Maven cannot resolve dependencies, check your internet connection and rerun the command.
- If reviewer data looks stale or duplicated, delete the relevant `cos730_assignment2.db` file and rerun the app.
