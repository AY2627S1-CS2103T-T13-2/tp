---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# TuiTracker Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* TuiTracker was built from [AddressBook Level 3 (AB3)](https://github.com/se-edu/addressbook-level3). We adapted its JavaFX app lifecycle, command and component structure, JSON storage pattern, shared utilities, build setup, and parts of the [AB3 Developer Guide](https://github.com/se-edu/addressbook-level3/blob/master/docs/DeveloperGuide.md). We replaced the contact model with students, lessons, enrollments, payment status and a weekly schedule; the feature descriptions and diagrams below describe TuiTracker unless explicitly labelled as a reference design.
* The application uses [JavaFX](https://openjfx.io/) for its interface and [Jackson](https://github.com/FasterXML/jackson) for JSON. The project uses [JUnit 5](https://junit.org/junit5/), [Gradle](https://gradle.org/), [Checkstyle](https://checkstyle.org/), [JaCoCo](https://www.jacoco.org/jacoco/) and the [Shadow plugin](https://gradleup.com/shadow/) for testing and builds. Documentation is written with [MarkBind](https://markbind.org/) and [PlantUML](https://plantuml.com/).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** shows the high-level design of TuiTracker.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (classes [`Main`](https://github.com/AY2627S1-CS2103T-T13-2/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-T13-2/tp/blob/master/src/main/java/seedu/address/MainApp.java)) handles app launch and shutdown.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The JavaFX interface, including the student and lesson lists and weekly schedule.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds students, lessons, enrollments, payment status, and view state in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

Each of the four main components (also shown in the diagram above),

* defines its *API* in an interface with the same name as the component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-T13-2/tp/blob/master/src/main/java/seedu/address/ui/Ui.java).

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

`MainWindow` owns a `CommandBox`, `ResultDisplay`, `StudentListPanel`, `LessonListPanel`, `SchedulePanel` and `StatusBarFooter`. The list panels create `StudentCard` and `LessonCard` instances for displayed records. `MainWindow` and these visible parts extend the abstract `UiPart` class, which handles common FXML loading behavior.

The layouts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-T13-2/tp/blob/master/src/main/resources/view/MainWindow.fxml) defines the main window's placeholders.

The `UI` component,

* executes user commands using the `Logic` component.
* observes the lists and view property exposed by `Logic` so that lists and the schedule refresh after commands.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* displays `Student` and `Lesson` objects from the `Model` component.

### Logic component

**API**: [`Logic.java`](https://github.com/AY2627S1-CS2103T-T13-2/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a student or lesson).
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API**: [`Model.java`](https://github.com/AY2627S1-CS2103T-T13-2/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" alt="Students, lessons and enrollments in the Model component" />


The `Model` component,

* stores a `TuiTracker` containing unique student and lesson lists, enrollments linking their IDs, and counters for the next IDs. An `Enrollment` stores a student ID and a lesson ID rather than direct object references. Deleting a student or lesson also removes its enrollments.
* exposes sorted, filtered observable student and lesson lists. It keeps the selected list view, schedule student and active filters as view state, separate from the saved tracker data.
* stores `UserPrefs` for GUI settings and uses the current month when determining payment status.
* does not depend on UI, Logic or Storage; those components call the model through its interfaces.


### Storage component

**API**: [`Storage.java`](https://github.com/AY2627S1-CS2103T-T13-2/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" alt="JSON tracker and preferences storage classes" />

The `Storage` component,
* reads and writes tracker data and user preferences as separate JSON files.
* is implemented by `StorageManager`, which delegates to `JsonTuiTrackerStorage` and `JsonUserPrefsStorage`.
* converts students, lessons and enrollments through `JsonSerializableTuiTracker` and its adapted record classes, while the model checks that loaded IDs and enrollments are consistent.

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Add feature

The `add` command enrolls a student in a lesson, creating either record if it is new. It can also create a new student or lesson on its own, without an enrollment. The four enrollment combinations and two standalone forms share validation, so the parser works out **what the user asked for** and the command checks that request before changing the model.

#### Parsing the arguments

`AddCommandParser` does not build a `Student` or a `Lesson`. It builds an `AddCommand` that holds a description of each side:

* `AddCommand.StudentRef` is either an `ExistingStudent` (an ID) or a `NewStudent` (a name, a contact and a level).
* `AddCommand.LessonRef` is either an `ExistingLesson` (an ID) or a `NewLesson` (a subject, a level, a timeslot and a fee).

These are `sealed` interfaces implemented by records, so the compiler checks that every case is handled and `equals` comes for free, which the parser tests rely on.

The parser works in this order, and reports the first problem it finds:

1. The command is well formed: no text before the first parameter, and at least one parameter.
2. No parameter is given more than once.
3. No parameter is joined to another value without a space, such as the `slv/` in `c/92223333slv/Primary 6`. Without this check, the joined parameter would be reported as missing.
4. At least one of `s/` and `l/` is given. Whether each supplied value is an ID or a new record's name or subject decides the remaining checks. The command later rejects an existing ID given on its own, because there would be nothing to create.
5. Neither `s/` nor `l/` is a number that is not a valid ID, such as `0`, `-1` or `1.5`, and neither is an ID larger than any record can have. Without this check, such a number would be read as the name of a new student or the subject of a new lesson.
6. No parameter is given that does not belong to the form. For example, `c/` is not allowed with an existing student.
7. No required parameter is missing, for example `c/` and `slv/` for a new student. The message also says which value was read as a new student or lesson, and suggests giving only the ID when the value is an ID followed by more text.
8. Every value is valid, student first and then lesson, using the shared `ParserUtil` methods, which carry the exact error messages.

#### Executing the command

`AddCommand#execute(Model)` checks the whole request **before it creates or changes anything**, in this order, and reports the first problem it finds:

1. The student exists, then the lesson exists (only for an existing student or lesson).
2. An existing student or lesson is not given on its own, with no other record to add or enroll.
3. A new student is not a duplicate, a new lesson is not a duplicate, and the student is not already enrolled in the lesson. Duplicates are found with `Name#isSameName`, `Contact#isSameContact` and `Subject#isSameSubject`, which ignore letter case, extra spaces and (for contacts) everything but the digits. `equals` on these classes stays exact, so that `edit` can still change only the letter case of a value.
4. A new lesson does not clash with an existing lesson.

It then works out the notes for things that are probably mistakes but might be intended, in this order: a new student with the same name as an existing student, a student whose level differs from the lesson's level, and a new lesson whose fee is 0. Notes do not stop the command; each one is added to the result on its own line.

Only then does it create what is new (`Model#addStudent`, `Model#addLesson`). It calls `Model#enroll` when both sides were supplied; a standalone new record has no enrollment. The IDs in the result message come from the objects that the `Model` returns.

The sequence diagram below shows `execute("add s/Daniel Koh c/92223333 slv/Primary 6 l/1")`, a new student joining an existing lesson. The checks that do not apply to this form (the clash check, which is only for a new lesson, and the notes, which are worked out inside `AddCommand`) are left out to keep the diagram readable.

<puml src="diagrams/AddSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `add` Command" />

#### Design considerations

**Aspect: what the parser hands to the command**

* **Alternative 1 (current choice):** a description of each side, either an ID or the details of something new.
  * Pros: Nothing exists until every check has passed. The `Model` generates IDs when it creates a record, so the parser could not build a complete `Student` anyway.
  * Cons: The command needs a little logic to handle each of the two cases for each side.
* **Alternative 2:** the parser builds `Student` and `Lesson` objects.
  * Pros: `AddCommand` would be simpler.
  * Cons: A `Student` needs an ID, which only the `Model` can give. The parser would need placeholder IDs, and a failed command could still use up a real one.

**Aspect: when new records are created**

* **Alternative 1 (current choice):** run every check first, then create.
  * Pros: A command that fails creates nothing and uses no ID, so the ID counters have no gaps from failed commands.
  * Cons: The checks and the creation are separate code that must be kept in step.
* **Alternative 2:** create as the checks go, and undo if one fails.
  * Pros: Each check could sit next to the code that depends on it.
  * Cons: Undoing must also put the ID counters back. `LogicManager` only undoes a command when saving fails, so this would need a second mechanism.

**Aspect: the order of the checks**

The order is deliberate. Duplicates are checked **before** clashes because an identical lesson always overlaps the original: if the clash were checked first, the more helpful message "an identical lesson already exists" could never be shown. Existence is checked first because the other checks need the records. A different level is reported as a note rather than rejected, because a student may attend a lesson of another level on purpose, for example a stronger student in a higher-level class.

#### Testing

`AddCommandParserTest` has one test for each way the parser can fail, and `AddCommandTest` covers each form, each rule and each pair of checks whose order matters.

### Edit feature

The `edit` command changes the specified fields of one existing student or lesson while preserving its ID and related data. `EditCommandParser` creates an `EditCommand` containing either a `StudentEdit` or a `LessonEdit`; fields that were not supplied remain empty in that edit request and retain their existing values during execution. `EditCommand` builds the complete updated record and validates duplicates, the final timeslot and clashes before asking the `Model` to replace the original.

Duplicate students are matched using `Name#isSameName` and `Contact#isSameContact`; duplicate lessons use `Subject#isSameSubject`, level equality and the final timeslot. Edit's private lookup helpers exclude the record being edited, so that changing only formatting or letter case is not mistaken for a duplicate or a clash. A different student/lesson level, a namesake with a different contact and a fee of `0` are allowed but appended to the success message as notes.

The sequence diagram below shows `execute("edit student 1 c/90001111")`, which updates only an existing student's contact number. Less-important method parameters, duplicate checking, note generation, the no-change path and the error paths are omitted to keep the diagram focused. Editing a lesson follows the same overall flow, with validation of its final timeslot, duplicates and clashes before the update.

<puml src="diagrams/EditSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `edit` Command" />

### Delete feature

The `delete student|lesson ID` command removes exactly one record of the requested kind. `DeleteCommandParser` accepts one supported kind (case insensitive) and one positive ID, using the shared ID parsers. An invalid format fails during parsing. `DeleteCommand` looks up the ID only in the requested collection; a missing record raises a kind-specific `CommandException` before any deletion. On success, `Model` removes the record and its enrollments and the result names the deleted student or lesson.

The first sequence diagram gives a component-level view of `delete student 12`. It omits parser and command objects so that the UI, Logic, Model and Storage responsibilities are easy to see.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" alt="Component-level sequence for deleting a student" />

The next diagram expands the command flow. `KIND ID` stands for the requested kind and ID; `student 12` and `lesson 7` illustrate its two branches. The smaller diagrams below expand its parsing, student deletion and lesson deletion references.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Overview of the Delete Command Sequence" />

<puml src="diagrams/DeleteParsingSequenceDiagram.puml" alt="Parsing Sequence for the Delete Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

<puml src="diagrams/DeleteStudentSequenceDiagram.puml" alt="Student Deletion Sequence for the Delete Command" />

<puml src="diagrams/DeleteLessonSequenceDiagram.puml" alt="Lesson Deletion Sequence for the Delete Command" />

The activity diagram summarizes format validation, lookup, deletion, enrollment removal and the error paths. Its decisions use guard labels on the outgoing flows, leaving the diamonds empty.

<puml src="diagrams/DeleteActivityDiagram.puml" alt="Activity Diagram for Deleting a Student or Lesson" />

### List feature

The `list` command clears any active filter and switches the interface to the requested student or lesson list. If no view is supplied, `ListCommandParser` defaults to the student list.

The sequence diagram below shows `execute("list lessons")` when four lessons exist. The invalid-view and empty-list paths are omitted because they do not change the main interaction between the Logic and Model components.

<puml src="diagrams/ListSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `list` Command" />

### Mark feature

The `mark` command records that a student has paid for the current calendar month. `MarkCommandParser` accepts one
positive student ID and constructs a `MarkCommand`. During execution, the command first looks up the student, then
checks that the student is not already marked as paid before asking `Model` to update the payment status. The command
returns a successful `CommandResult` containing the student's name. Invalid IDs, missing students and students who
are already paid take the corresponding error paths and do not change the model.

The sequence diagram below shows the successful execution of `execute("mark 1")` for an existing unpaid student.

<puml src="diagrams/MarkSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `mark` Command" />

### Help feature

The `help` command takes no required arguments. `AddressBookParser` constructs a `HelpCommand` directly because no
command-specific parsing is needed. `HelpCommand#execute(Model)` returns a `CommandResult` whose `showHelp` flag is
set. After `LogicManager` returns that result, `MainWindow` opens the `HelpWindow`, which displays the format of every
available command and a link to the full User Guide.

The sequence diagram below shows the help window being opened by `executeCommand("help")`.

<puml src="diagrams/HelpSequenceDiagram.puml" alt="Interactions Across the UI and Logic Components for the `help` Command" />

### Filter feature

The `filter` command narrows the displayed list to one student's lessons, one lesson's students, lessons matching a day, subject or level, or students with a selected payment status. It changes the displayed view without changing saved data.

#### Parsing the arguments

`FilterCommandParser` accepts `student ID`, `lesson ID`, `day DAY`, the bare `paid` or `unpaid` keyword, or one or both of `l/SUBJECT` and `llv/LESSON_LEVEL`.

1. The `student`, `lesson` and `day` forms require exactly two whitespace-separated tokens. The payment forms require one token. The subject and level forms use their command prefixes and may be combined.
2. An incomplete, unknown, mixed or repeated form raises a `ParseException` with a specific message before the view changes.
3. IDs and days use the shared `ParserUtil` methods. Subject and level values are parsed with the corresponding domain parsers.

The parser builds the corresponding `FilterCommand` directly. A filter selects existing records and never creates new ones.

#### Executing the command

`FilterCommand#execute(Model)` branches on the selected filter kind:

1. For `studentId`/`lessonId`, it first confirms the ID refers to an existing student or lesson, otherwise throws a `CommandException` (`MESSAGE_STUDENT_NOT_FOUND` / `MESSAGE_LESSON_NOT_FOUND`). A `DayOfWeek` needs no such check — unlike an ID, every value the parser can produce already refers to something real, so there is nothing to look up or fail to find.
2. It delegates to the matching `Model` filter method and selects the relevant list: `LESSONS` for student, day, subject and level filters; `STUDENTS` for lesson and payment-status filters.

Inside `ModelManager`, setting one filter clears the earlier filter fields. `refreshFilters()` then rebuilds the predicates for both `FilteredList`s. A day, subject or level filter narrows only lessons; payment status narrows only students. Mutating operations reapply the predicates. Payment changes and the month-boundary refresh also re-evaluate the current payment filter and payment sort.

The sequence diagram below shows `execute("filter student 1")`.

<puml src="diagrams/FilterSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `filter` Command" />

#### Design considerations

**Aspect: how the active filter is stored**

* **Alternative 1 (current choice):** six nullable fields in `ModelManager`, one each for student, lesson, day, subject, level and paid status. The subject and level fields may be set together; every other filter setter clears the previous fields.
  * Pros: Each predicate can read the fields it needs, and `resetFilters()` clears them in one place.
  * Cons: The valid combinations are enforced by the setters rather than the type system, so a new filter setter must clear the previous fields correctly.
* **Alternative 2:** a sealed type representing each filter form, including the combined subject and level form.
  * Pros: The compiler guarantees exactly one state at a time, and scales to more filter kinds without more fields.
  * Cons: More machinery for a feature this small; `ModelManager` would need to unwrap the sealed type everywhere `refreshFilters()` uses it.

**Aspect: whether filtering by day should also narrow the student list**

* **Alternative 1 (current choice):** filtering by day only narrows the lesson list; the student list is left as-is.
  * Pros: Matches what "day" actually describes — a property of a lesson's timeslot, not of a student. No ambiguity about which students would qualify.
  * Cons: Running `filter day MONDAY` then switching to the Students view (e.g. via `list`) shows every student, not just those with a lesson on Monday, which may surprise a user expecting symmetry with `filter student`/`filter lesson`.
* **Alternative 2:** also narrow the student list to students enrolled in at least one lesson on that day.
  * Pros: Keeps both lists consistent with the same day filter.
  * Cons: Requires scanning every student's enrollments against the day filter, adding a second predicate shape (`anyMatch` over enrollments) alongside the simple per-lesson check; was judged unnecessary complexity for the MVP.

**Aspect: when the filter predicate is recomputed**

* **Alternative 1 (current choice):** recompute both predicates from scratch in `refreshFilters()`, called after every mutation.
  * Pros: The filter is always consistent with the latest data; no risk of a stale filtered list after an unrelated `enroll` or `delete`.
  * Cons: Every mutating `Model` method must remember to call `refreshFilters()`.
* **Alternative 2:** filter once when `filter` runs and leave the resulting list static until the next `filter`/`list`.
  * Pros: Slightly less coupling between mutating methods and the filter.
  * Cons: The filtered list would go stale the moment a student is deleted or an enrollment changes, showing outdated data.

**Aspect: clearing the filter when its target is deleted**

`deleteStudent` and `deleteLesson` call `refreshFilters()` without clearing the active filter. If the deleted record was the filter target, the relevant list becomes empty until the user runs another `filter` or `list`. This keeps deletion from changing the user's selected filter.

#### Testing

`FilterCommandParserTest` covers the student, lesson, day, payment, subject and level forms, including malformed arguments. `FilterCommandTest` checks the resulting model and list view, missing IDs and command equality. `ModelManagerTest` checks that payment filters update after payment changes and a month rollover.

### Schedule feature

The `schedule` command shows all recurring lessons, while `schedule student ID` limits the same weekly view to lessons attended by that student. Both forms keep the active list filter unchanged.

Lessons recur by weekday and time, so the schedule has no calendar date or `/week` option. `LogicManager` treats the command as read-only and does not save the tracker when it runs.

`ScheduleCommandParser` accepts either no arguments or exactly two tokens: the case-insensitive keyword `student` and an ID parsed by `ParserUtil.parseStudentId`. It throws a `ParseException` for a missing ID, extra tokens, an unknown keyword, or an invalid ID. The shared ID parser supplies the invalid and oversized ID messages.

`ScheduleCommand#execute` checks `Model#findStudent` before changing view state. A missing student causes a `CommandException` with `MESSAGE_STUDENT_NOT_FOUND`; the existing view and schedule selection remain unchanged. For a valid command, it sets the optional schedule student ID in `ModelManager`, then switches to `ListView.SCHEDULE`. The ID is transient view state and does not change stored students, lessons, enrollments, or list filters. Plain `schedule` clears this schedule selection and shows every lesson.

The first sequence diagram shows parsing and execution for `schedule student 1`. The rendering sequence and activity diagram below cover the panel and error paths separately to keep each diagram readable.

<puml src="diagrams/ScheduleCommandSequenceDiagram.puml" alt="Parsing and executing the student schedule command" />

`SchedulePanel#refresh` starts with all lessons. If a student is selected, it keeps only lessons whose IDs appear in `Model#getLessonIdsOf(StudentId)`, then passes them to the existing weekday layout. This keeps the seven-day timeline, empty-day labels, grouping, details cards, and overlap warnings consistent with the lessons currently shown. A student with no enrollments gets an empty weekly view.

`ScheduleLayout` sorts each day's lessons by start time and ID and detects actual time overlaps. The panel groups bars or markers whose labels would collide, and each group opens a scrollable details dialog with a separate card for every lesson. The timeline uses one fixed scale from 00:00 to 23:59; each day scrolls vertically and the week scrolls horizontally. No lessons are hidden when a list filter is active.

<puml src="diagrams/ScheduleRenderingSequenceDiagram.puml" alt="Building the weekly schedule panel" />

The activity diagram uses blank decision diamonds with guards on their outgoing flows.

<puml src="diagrams/ScheduleActivityDiagram.puml" alt="Activity diagram for showing the weekly schedule" />

#### Keyboard navigation

`MainWindow` captures `F6` when the schedule is visible and focuses Monday's heading. Each day heading is a focusable button, including on empty days. `SchedulePanel` handles arrow keys within its own view: Left/Right chooses a day, Up/Down chooses the previous or next lesson, and Page Up/Page Down and Home/End scroll the focused day. The panel adjusts horizontal and vertical scroll positions when focus moves so the focused control stays visible. Pressing Enter or Space uses the button's normal action to enter a day or open lesson details. Escape returns focus to the command box.

The lesson details dialog starts with its scroll area focused. Its cards can be reached with Tab, Escape closes it, and focus returns to the lesson button that opened it. The key handler is attached to the schedule panel, so it does not consume arrow keys while the user is typing a command. Focused headings, lessons, and detail cards have visible highlights.

`ScheduleCommandParserTest` covers accepted and malformed arguments. `ScheduleCommandTest` checks student selection, empty and missing students, unchanged list filters, and resetting to the full schedule.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a private tutor who manages students, recurring lessons, enrollments and monthly payments
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage tuition records quickly with typed commands while viewing students, lessons and a weekly schedule in one desktop app.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a … | I want to … | So that I can … |
|----------|--------|-------------|-----------------|
| `* * *` | new tutor | see instructions for the available commands | learn how to use the application and check a command when I forget it |
| `* * *` | tutor | enrol a student in a lesson whether the student or lesson is new or existing | set up my records without unnecessary separate steps |
| `* * *` | tutor | be warned when enrolling a student in a lesson at a different level | check whether the mismatch is intentional |
| `* * *` | tutor | be warned when a new lesson overlaps another lesson on the same day | avoid double-booking my teaching time |
| `* * *` | tutor | be prevented from creating duplicate students, lessons, or enrollments | keep my records accurate |
| `* * *` | tutor | have a failed enrollment leave all existing records unchanged | avoid incomplete records |
| `* * *` | tutor | list my students with their contact details, levels, lessons, monthly fees, and payment statuses | review my roster and outstanding payments |
| `* * *` | tutor | list my lessons with their timeslots, fees, and enrolled students | review my teaching schedule |
| `* * *` | tutor | view the weekly schedule for one student | see when that student's lessons take place |
| `* * *` | tutor | delete a student and their associated enrollments | remove a student who no longer attends lessons without leaving obsolete enrollments |
| `* * *` | tutor | delete a lesson and its associated enrollments | remove a cancelled lesson without leaving obsolete enrollments |
| `* * *` | tutor | mark a student as paid for the current month | track which students have paid |
| `* * *` | tutor | have payment statuses reset for a new month | distinguish this month’s unpaid fees from last month’s payments |
| `* *` | tutor | be shown matching existing students or lessons when adding an enrollment | reuse a record instead of creating a duplicate |
| `* *` | tutor | use common aliases for education levels | enter levels quickly without creating accidental mismatches |
| `* *` | tutor | undo an accidental addition | correct a mistake without manually deleting its records |
| `* *` | tutor | change a student’s current-month status from paid back to unpaid | correct a payment recorded by mistake |
| `* *` | tutor | view payment dates and history | check when a student paid in previous months |
| `* *` | tutor | record a payment for a specific month | update an earlier month when a payment is received late |
| `* *` | tutor | see how many students are paid and unpaid | assess outstanding payments at a glance |

### Use cases

For all use cases below, the System is the tuition management application and the Actor is the user.

**Use case: Enrol a student in a lesson**

**MSS**

1.  User enters an add command containing a student and a lesson.
2.  System validates the command format and required parameters.
3.  System retrieves the existing student and lesson, or prepares new records using the supplied details.
4.  System checks whether the student level matches the lesson level and prepares a note if it does not.
5.  System checks that the student, lesson and enrolment are not duplicates.
6.  System checks that a newly created lesson does not clash with another lesson on the same day.
7.  System creates any required student or lesson records and assigns unique IDs.
8.  System enrols the student in the lesson; a new student's payment status starts unpaid, while an existing student's current-month status is retained.
9.  System saves the changes and displays the student and lesson IDs.

Use case ends.

**Extensions**

* 1a. The command has an invalid format or a required parameter is missing.

    * 1a1. System displays an error message.
    * Use case ends.

* 2a. A supplied student or lesson ID does not exist.

    * 2a1. System displays an error message.
    * Use case ends.

* 4a. The student level does not match the lesson level.

    * 4a1. System continues the enrollment and adds a note naming both levels so the user can check the mismatch.

* 5a. The student or lesson is a duplicate.

    * 5a1. System displays an error message instructing the user to use the existing ID.
    * 5a2. System makes no changes.
    * Use case ends.

* 5b. The student is already enrolled in the lesson.

    * 5b1. System reports that the student is already enrolled in this lesson.
    * 5b2. System makes no changes.
    * Use case ends.

* 6a. The new lesson clashes with an existing lesson.

    * 6a1. System displays a clash message naming the existing lesson and its timeslot.
    * 6a2. System makes no changes.
    * Use case ends.

**Use case: View students or lessons**

**MSS**

1.  User enters `list`, `list students`, or `list lessons`.
2.  System validates the requested view.
3.  System clears any active list filter.
4.  System retrieves the requested records.
5.  System shows records in their current sort order (by ID for students and by day and time for lessons by default).
6.  System displays the records and the total count.

    Use case ends.

**Extensions**

* 2a. The user specifies an invalid, repeated or conflicting view.

    * 2a1. System displays an error specific to the invalid or repeated view, or asks the user to choose one view.
    * Use case ends.

* 4a. There are no records of the requested type.

    * 4a1. System displays an empty list and an informative message.
    * Use case ends.

**Use case: Delete a student or lesson**

**MSS**

1. User requests to delete a student or lesson using its kind and ID.
2. System identifies the specified student or lesson.
3. System deletes the specified record and all enrolments associated with it.
4. System confirms the deletion and identifies the deleted record.

    Use case ends.

**Extensions**
* 1a. The command is missing the kind, contains an unknown kind, or contains extra text.
    * 1a1. System displays `Invalid command format. Use: delete student|lesson ID`.
    * Use case ends.

* 1b. The ID is missing, non-numeric, or not a positive integer.
    * 1b1. System displays `Invalid command format. Use: delete student|lesson ID`.
    * Use case ends.

* 2a. No student or lesson exists with the specified kind and ID.
    * 2a1. System displays an error stating that no record of the specified kind exists with the given ID.
    * 2a2. System leaves all data unchanged.
    * Use case ends.

* 2b. The ID exists but belongs to the other kind of record.
    * 2b1. System displays an error message.
    * 2b2. System leaves all data unchanged.
    * Use case ends.


**Use case: Mark a student's monthly payment as paid**

**MSS**

1. User requests to mark a student as paid using the student's permanent ID.
2. System identifies the student corresponding to the specified ID.
3. System marks the student's payment status as paid for the current month.
4. System confirms that the student's payment has been marked as paid.

Use case ends.

**Extensions**

* 1a. The student ID is missing or more than one argument is provided.
    * 1a1. System displays `Invalid command format. Use: mark ID`.
    * Use case ends.

* 1b. The student ID is non-numeric or not positive.
    * 1b1. System displays `ID must be a positive whole number.`
    * Use case ends.

* 2a. No student has the specified ID.
    * 2a1. System displays `No student found with ID ID.`, using the requested ID.
    * Use case ends.

* 3a. The student has already been marked as paid for the current month.
    * 3a1. System leaves the payment status unchanged.
    * 3a2. System informs the user that the student is already paid.
    * Use case ends.

* 3b. The payment status cannot be saved.
    * 3b1. System displays the applicable file-write or insufficient-permissions error and states that no changes were made.
    * 3b2. System leaves the payment status unchanged.
    * Use case ends.

### Non-Functional Requirements

**Environment**

1.  Should work on any _mainstream OS_ that has Java `25` installed, without requiring any other Java version.
2.  Should run without an installer, from a single JAR file of no more than 100MB.
3.  Should not depend on any remote server, i.e., all features should work without an Internet connection.
4.  Should not require the user to install any third-party software other than Java.

**Performance**

5.  Should be able to hold up to 200 students, 50 lessons and 500 enrollments without any command taking more than 1 second to complete on a typical modern laptop.
6.  Should start up and display the main window within 5 seconds on a typical modern laptop.

**Usability**

7.  A user with above average typing speed (>50WPM) for regular English text (i.e. not code, not system admin commands) should be able to accomplish all of the tasks faster using commands than using the mouse.
8.  Every feature should be usable through typed commands alone, without needing the mouse.
9.  Every error message should state what was wrong with the input and, where applicable, the expected format.

**Data**

10. Data should be stored locally in a human-editable text file, without using a DBMS.
11. Data should be saved automatically after every command that changes it, without the user needing to save manually.
12. A command that fails should leave the stored data unchanged.
13. If the data file is missing or cannot be read, the application should still start, without crashing. 
14. Student contact numbers and payment records should be stored only on the user's computer, and should never be sent elsewhere.

**Scope**

15. The application is intended for a single user, and is not required to support multiple users sharing the same data file.

### Glossary

* **Cascading deletion**: The automatic removal of all enrollments tied to a student or lesson when that student or lesson is deleted.
* **CLI (Command Line Interface)**: A way of using the application by typing text commands, rather than clicking with the mouse.
* **Command**: A text instruction that the user types into TuiTracker to perform an action, made up of a command word (e.g., `add`, `list`) followed by any required parameters, e.g., `delete student 12`.
* **Command word**: The first word of a command, which specifies the action to perform, e.g., `add`, `delete`, `list` or `mark`.
* **Contact**: A student's (or their parent or guardian's) contact number. The app accepts 3 to 15 digits with optional grouping punctuation, subject to the User Guide's length limits.
* **Current month**: The calendar month given by the date on the user's computer. Payment statuses apply only to the current month.
* **Data file**: The local, human-editable file in which TuiTracker stores all students, lessons and enrollments.
* **Display index**: The position of a record in a currently displayed list, starting from 1. It can change whenever the displayed list changes. Commands use permanent student and lesson IDs instead.
* **Duplicate lesson**: A lesson with the same subject, level, day, start time and end time as an existing lesson.
* **Duplicate student**: A student with the same name and contact as an existing student.
* **Enrollment**: The link between a student and a lesson that the student attends. A student can be enrolled in many lessons, and a lesson can have many students.
* **Lesson**: A weekly class conducted by the tutor, defined by its subject, level, day, start time, end time and fee.
* **Lesson clash**: The situation where two lessons on the same day have overlapping timeslots, e.g., `15:00-16:30` and `16:00-17:00` on Monday. A lesson that starts exactly when another ends (e.g., `15:00-16:00` and `16:00-17:00`) does not clash with it.
* **Lesson fee**: The amount charged for one session of a lesson: 0 or more, with at most two decimal places. A fee of 0 is for a free lesson.
* **Lesson ID**: A unique positive integer assigned to a lesson when it is created. It never changes, and is used to refer to the lesson in commands.
* **Level**: The education level of a student or lesson, e.g., `Primary 6` or `Secondary 2`. Levels are compared without considering letter case or extra spaces, so `Primary 6` and `primary   6` are the same level, but `P6` and `Primary 6` are different levels.
* **Level matching**: Comparing a student's level with a lesson's level when the student is enrolled. A student can join a lesson of another level, but `add` then adds a note naming both levels.
* **Mainstream OS**: Windows, Linux, Unix, or macOS.
* **Parameter**: A value given after the command word to provide the details a command needs, e.g., the student ID `12` in `delete student 12`. Some parameters are written after a _prefix_.
* **Payment status**: Whether a student has paid for the current month (_Paid_) or not (_Unpaid_). Every student's payment status is reset to _Unpaid_ at the start of each month.
* **Prefix**: A short label ending in `/` that marks which parameter a value belongs to, e.g., `c/` in `c/91234567` marks a contact. Parameters with prefixes can be given in any order.
* **Student**: A person taught by the tutor, defined by their name, contact and level.
* **Student ID**: A unique positive integer assigned to a student when they are created. It never changes, and is used to refer to the student in commands. Not to be confused with a _display index_.
* **Subject**: The subject taught in a lesson, e.g., `Mathematics`.
* **Timeslot**: The day, start time and end time of a lesson, e.g., Monday `15:00-16:30`. Times use the 24-hour format with a one- or two-digit hour (`9:00` or `09:00`), and are stored and shown as `HH:mm`.
* **Tutor**: The user of TuiTracker, i.e., a private tutor who manages their own students, lessons and payments.
* **WPM**: Words per minute, a standard measurement of how fast someone types.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

These steps give testers a path through every current command. For complete syntax and input rules, see the [User Guide](UserGuide.md). Run each section in a fresh empty folder containing only the release `tuitracker.jar`, unless a step says to continue in the same folder. Launch with `java -jar tuitracker.jar` using Java 25. This keeps earlier saved changes from changing the sample IDs and expected results.

On first launch, the sample data has Alice Tan (student ID 1), Bryan Lim (ID 2), Chloe Ng (ID 3), and four lessons: Mathematics (lesson ID 1), Primary 6 Science (ID 2), Secondary 2 Science (ID 3), and English (ID 4). Bryan is paid for the current month. Type each command in the command box and press Enter.

### Launch, help and exit

1. Launch the JAR in a fresh folder. Expected: the Students view shows three sample students, not contacts.
2. Enter `help`. Expected: the help window shows the available command formats. Close it, then press `F1` to open it again.
3. Resize and move the main window, enter `exit`, and relaunch the JAR in the same folder. Expected: the window reopens with the saved size and location when that location is still available.

### Listing and filtering

1. Enter `list lessons`. Expected: all four sample lessons appear, and the result says `Listed 4 lessons.`
2. Enter `filter student 1`. Expected: only Alice's Mathematics and Primary 6 Science lessons appear.
3. Enter `list lessons`. Expected: the filter is cleared and all four lessons appear again.
4. Enter `filter paid`. Expected: the Students view shows Bryan, who is paid in the sample data.
5. Enter `list students`. Expected: the filter is cleared and all three students appear.

### Adding a student and enrolling them

1. Enter `add s/Daniel Koh c/92223333 slv/Primary 6 l/1`. Expected: Daniel is created with student ID 4 and enrolled in Mathematics (lesson ID 1).
2. Enter `list students`. Expected: Daniel appears with his new ID and Mathematics enrollment.
3. Enter `add s/1 l/1`. Expected: the app reports that Alice is already enrolled in lesson 1; no extra enrollment is created.

### Editing a student and a lesson

1. Enter `edit student 1 c/90001111`. Expected: Alice's contact changes while her ID and enrollments stay the same.
2. Enter `edit lesson 1 f/55`, then `list lessons`. Expected: Mathematics keeps lesson ID 1 and its enrollments, but its displayed fee becomes 55.
3. Enter `edit student 1 s/Alice Tan`. Expected: the result says `No changes were made.`

### Deleting a student or lesson

1. Enter `schedule student 1`, then `delete student 1`. Expected: Alice and her enrollments are removed; the schedule switches to all lessons and the result says `The schedule now shows all lessons.`
2. In a fresh folder, enter `delete lesson 4`, then `list lessons`. Expected: English is absent from the lesson list and its enrollments are removed, while Chloe remains a student.

### Marking and correcting payment

1. Enter `mark 1`. Expected: Alice is marked paid for the current month.
2. Enter `unmark 1`. Expected: Alice is marked unpaid for the current month again.

### Sorting

1. Enter `sort lesson fee desc`. Expected: the Secondary 2 Science lesson (ID 3, fee 60) appears first.
2. Enter `sort student name desc`. Expected: the view switches to Students and Chloe appears first.

### Viewing the weekly schedule

1. Enter `schedule student 1`. Expected: the seven-day schedule shows Alice's lessons on Monday and Thursday.
2. Enter `schedule`. Expected: all four lessons appear in the weekly schedule.
3. Enter `schedule student 999`. Expected: `No student found with ID 999.` The displayed schedule stays unchanged.
4. Press `F6`, use Left/Right to choose a day, then Enter or Space to focus or open a lesson. Expected: the focused day or lesson has a visible highlight; Escape returns focus to the command box.

### Saving and loading data

1. In a fresh folder, enter `add s/Nora Lim c/90000000 slv/Primary 6`, then `exit`. Expected: `data/tuitracker.json` is created beside the JAR.
2. Relaunch the JAR in the same folder and enter `list students`. Expected: Nora is still present with student ID 4.
3. Close the app, rename `data/tuitracker.json` to a backup name, and relaunch. Expected: the three sample students appear because the data file is missing. Close the app before restoring the backup.
4. To examine a corrupted file, use a separate disposable folder: create a saved data file, close the app, and remove a closing brace from its JSON. Relaunch. The current app logs a loading warning and starts with an empty tracker. **Do not enter a data-changing command against that file**: it could overwrite the corrupted file.
