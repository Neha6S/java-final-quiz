# Library Management & Dynamic Fine Calculation System

A Java S3 Object-Oriented Programming mini-project using **abstract classes, inheritance,
polymorphism, interfaces, exception handling, Swing event handling, JDBC and MySQL**.

## Features
- Add and list Books, Journals and Multimedia items.
- Search catalog entries by title or author.
- Issue an available item to a member ID.
- Return an issued item and calculate the late fine automatically.
- Fine policy is selected polymorphically from the concrete item type.
- MySQL stores catalog and loan data; JDBC uses prepared statements.

## Fine rules (easy to customize)
- Book: ₹2 per overdue day
- Journal: ₹5 per overdue day
- Multimedia: ₹10 per overdue day

Loan period: 14 days for all types in this starter project.

## Requirements
- JDK 17+
- Maven
- MySQL 8+

## Setup
1. Create a MySQL database and tables by running `src/main/resources/schema.sql`.
2. Set database credentials using environment variables:
   - `LIBRARY_DB_URL` (optional; default `jdbc:mysql://localhost:3306/library_fine_system`)
   - `LIBRARY_DB_USER` (optional; default `root`)
   - `LIBRARY_DB_PASSWORD` (no default; set this to your MySQL password)
3. From this folder, run:
   ```bash
   mvn clean package
   mvn exec:java -Dexec.mainClass="edu.sct.library.ui.LibraryManagementApp"
   ```
   If you don't have the Maven exec plugin configured, use your IDE to run
   `edu.sct.library.ui.LibraryManagementApp` after importing the project as a Maven project.

## First-time use
- Add an item in the **Catalog** tab.
- Use the **Issue / Return** tab to issue it to a member ID.
- Return it using its item ID. The fine is calculated from the loan's due date and return date.
- The app does not currently implement member registration/authentication; member IDs are entered as text.

## OOP concepts demonstrated
- `LibraryItem` is an abstract base class with abstract `calculateLateFine()` and `getItemType()`.
- `Book`, `Journal` and `Multimedia` override these methods.
- `FinePolicy` is an interface used by the fine calculation classes.
- `LibraryService` contains validation and business rules.
- `LibraryException` represents expected application errors.
- `LibraryManagementApp` uses Swing controls and action listeners for event handling.
- `LibraryDAO` uses JDBC prepared statements and transactions.

## Project structure
```
src/main/java/edu/sct/library/
  model/       LibraryItem, Book, Journal, Multimedia, Loan
  policy/      FinePolicy
  exception/   LibraryException
  dao/         Database, LibraryDAO
  service/     LibraryService
  ui/          LibraryManagementApp
src/main/resources/schema.sql
```

## Notes
This is a student project starter, not a production library system. For a real deployment,
add authentication, member records, input hardening, backups and more comprehensive tests.
