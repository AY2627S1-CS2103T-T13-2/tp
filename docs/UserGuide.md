---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# TuiTracker User Guide

TuiTracker is a **desktop application for private tutors to manage their students, lessons, and monthly payments in one place**. It combines a Command Line Interface (CLI) with a Graphical User Interface (GUI), allowing tutors who type quickly to handle common administrative tasks efficiently while viewing their records clearly.

With TuiTracker, you can view and update student and lesson details, manage enrollments, track monthly payments, and remove records that are no longer needed.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `tuitracker.jar` file from the [TuiTracker releases page](https://github.com/AY2627S1-CS2103T-T13-2/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for TuiTracker.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar tuitracker.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all students.

   * `list lessons` : Lists all lessons.

   * `add s/Daniel Koh c/92223333 slv/Primary 6 l/Art llv/Primary 6 d/Tuesday st/10:00 et/11:00 f/40` : Adds a new student named `Daniel Koh` and enrolls them in a new Art lesson.

   * `edit student 1 c/90001111` : Updates the contact number of the student with ID 1.

   * `mark 1` : Marks the student with ID 1 as paid for the current month.
   * `unmark 1` : Corrects student 1's payment status back to unpaid.

   * `delete student 3` : Deletes the student with ID 3.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

### Command-box shortcuts

The command box keeps successful commands for the current session. Press **Up** and **Down** to move through that
history; text you were drafting is restored when you move past the newest entry. Consecutive duplicate commands are
stored once. Type the start of a command word and press **Tab** to complete it when there is only one match.

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `delete student ID`, replace `ID` with a value such as `12`.

* Items in square brackets are optional.<br>
  For example, `list [students|lessons]` can be used as `list students`, `list lessons` or as just `list`.

* Parameters that start with a prefix, such as `s/` or `l/` in `add`, can be in any order.<br>
  For example, if the command specifies `s/STUDENT l/LESSON`, `l/LESSON s/STUDENT` is also acceptable.

* Command words and prefixes can be typed in any letter case, so `ADD S/1 L/2` is the same as `add s/1 l/2`. Each prefix must have a space before it.

* Extraneous parameters for commands that take no parameters, such as `help` and `exit`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Listing all students or lessons: `list`

Displays either all students or all lessons. Use this command to switch between the two lists or to clear an active filter.

**Format:** `list [students|lessons]`

**Parameters:**

* `students|lessons` — optionally specifies which list to display. The keyword is case-insensitive.
* If the parameter is omitted, TuiTracker displays the list of students.
* Leading and trailing spaces are ignored. Only one view may be specified.

**Examples:**

* `list` — displays all students.
* `list students` — displays all students.
* `list lessons` — displays all lessons.
* `list LESSONS` — displays all lessons; keyword case is ignored.

**Successful listing:**

* The selected list is displayed and any active filter is cleared.
* The result box reports the number of records, for example `Listed 1 student.`, `Listed 3 students.`,
  `Listed 1 lesson.`, or `Listed 4 lessons.`
* If the selected list is empty, the result box displays `No students found. Use the add command to create one.` or `No lessons found. Lessons are created through the add command.`

**Displayed information:**

* The student list shows each student's name, ID, contact number, level, payment status for the current month, and enrolled lesson IDs. Its default order is ascending ID.
* The lesson list shows each lesson's subject, ID, level, weekly timeslot, fee per session, and enrolled students. Its default order is day of the week (Monday first), start time, then ID.
* If you previously used `sort`, `list` keeps that selected order while clearing the active filter. For example, after `sort student name desc`, `list students` still shows students by descending name.

**Notes:**

* `list` and `list students` have the same effect.
* This command changes only the displayed list and filter. It does not modify any student, lesson, enrollment, or payment data.

**Errors:**

* An unknown view or extra text — the app displays `Invalid view. Use: list [students|lessons]`.
* The same view repeated — the app displays `The view can only be specified once.`
* Both `students` and `lessons` — the app displays `Choose either students or lessons, not both.`

### Adding a student, a lesson, or a student to a lesson: `add`

Enrolls a student in a lesson. The student and the lesson can each be **new** (the app creates them) or **existing** (you refer to them by their ID).

You can also add a new student or a new lesson **on its own**, without enrolling anyone, by leaving out the other side. Enroll them later with `add s/ID l/ID`.

Format: `add [s/STUDENT] [l/LESSON] [c/CONTACT slv/STUDENT_LEVEL] [llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE]`

<box type="tip" seamless>

**Tip:** To find an ID, use `list students` or `list lessons` first.
</box>

#### How `s/` and `l/` work

* `s/` is the student. A **whole number**, such as `3`, means the existing student with that ID. Anything else, such as `Daniel Koh`, is the **name of a new student**.
* `l/` is the lesson. A **whole number**, such as `1`, means the existing lesson with that ID. Anything else, such as `Mathematics`, is the **subject of a new lesson**.
* A name or a subject must contain at least one letter, so it can never be mistaken for an ID.

Which other parameters you give depends on what is new. You can give the parameters in any order.

Student | Lesson | What you give
--------|--------|--------------
existing | existing | `s/ID l/ID`
new | existing | `s/NAME c/CONTACT slv/STUDENT_LEVEL l/ID`
existing | new | `s/ID l/SUBJECT llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE`
new | new | all of the above
new | none | `s/NAME c/CONTACT slv/STUDENT_LEVEL`
none | new | `l/SUBJECT llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE`

At least one of `s/` and `l/` is needed. An existing student or lesson given on its own, such as `add s/3`, is an error that asks you for the other side, since there is nothing new to add.

#### Parameters

Parameter | Meaning | Needed when | Rules
----------|---------|-------------|------
`s/` | The student: an ID, or the name of a new student | you enroll a student, or add one | A name has at most 100 characters: letters, numbers, spaces and `.` `,` `'` `-` `(` `)` `&` `@`, with at least one letter. The words `s/o` and `d/o` can be part of a name, such as `Ravi s/o Kumar`.
`l/` | The lesson: an ID, or the subject of a new lesson | you enroll a student in a lesson, or add one | A subject has at most 50 characters: letters, numbers, spaces and `.` `,` `-` `(` `)` `&` `@`, with at least one letter.
`c/` | The student's contact number | the student is new | 3 to 15 digits, at most 25 characters in all. Groups of digits can be separated by one space or hyphen, a group can be in brackets, and a `+` can start the number, such as `+65 (9123) 4567`. Duplicates are compared by the digits only.
`slv/` | The student's level, such as `Primary 6` | the student is new | 1 to 30 characters: letters, numbers, spaces and hyphens, with at least one letter or number.
`llv/` | The lesson's level, such as `Primary 6` | the lesson is new | The same as `slv/`.
`d/` | The day of the week | the lesson is new | `Monday` to `Sunday`, or a short form: `Mon`, `Tue`, `Tues`, `Wed`, `Thu`, `Thur`, `Thurs`, `Fri`, `Sat`, `Sun`. Letter case is ignored.
`st/` | The start time | the lesson is new | In the 24-hour format, such as `9:00`, `09:00` or `14:30`.
`et/` | The end time | the lesson is new | In the same format, and later than the start time on the same day: a lesson cannot run past midnight. To end at midnight, use `23:59`, not `24:00` or `00:00`. To teach past midnight, add two lessons, such as `23:00`-`23:59` and `00:00`-`01:00` the next day.
`f/` | The fee for one session | the lesson is new | 0 or more, with at most seven digits before the decimal point and at most two decimal places, such as `50` or `45.50`. A fee of `0` is accepted for a free lesson, and the result ends with a note asking you to check it.

Using a parameter that does not belong, such as `c/` with an existing student, is an error.

#### How duplicates are detected

* A new student is a duplicate of an existing one when both the name and the contact number match. Names are compared ignoring letter case and extra spaces, so `alice  TAN` matches `Alice Tan`. Contact numbers are compared by their digits only. A duplicate is an error that gives the existing student's ID.
* A new student with the same name as an existing student but a different contact number is added, and the result ends with a line such as `Note: another student named Alice Tan already exists (Student ID: 1).` Check that this is really a different person.
* A new lesson is a duplicate of an existing one when the subject, the level and the day and times all match. Subjects and levels are compared ignoring letter case and extra spaces. A duplicate is an error that gives the existing lesson's ID.

#### Examples

Each one is followed by the result you should see.

1. `add s/Daniel Koh c/92223333 slv/Primary 6 l/1`: Enrolled Daniel Koh [Student ID: 4] in Mathematics [Lesson ID: 1]. Payment status: Unpaid.
1. `add s/3 l/Art llv/Primary 4 d/Tuesday st/10:00 et/11:00 f/40`: Enrolled Chloe Ng [Student ID: 3] in Art [Lesson ID: 5]. Payment status: Unpaid.
1. `add s/Eve Ong c/90001111 slv/Primary 4 l/Drama llv/Primary 4 d/Wednesday st/10:00 et/11:00 f/35`: Enrolled Eve Ong [Student ID: 5] in Drama [Lesson ID: 6]. Payment status: Unpaid.
1. `add s/4 l/2`: Enrolled Daniel Koh [Student ID: 4] in Science [Lesson ID: 2]. Payment status: Unpaid.
1. `add s/2 l/Physics llv/Secondary 2 d/Friday st/14:00 et/15:00 f/55`: Enrolled Bryan Lim [Student ID: 2] in Physics [Lesson ID: 7]. Payment status: Paid.
1. `add s/1 l/4`: Enrolled Alice Tan [Student ID: 1] in English [Lesson ID: 4]. Payment status: Unpaid. The result ends with `Note: Alice Tan's level (Primary 6) differs from the lesson's level (Primary 4).` A student can join a lesson of another level, but the note asks you to check that this is intended.
1. `add s/Nora Lim c/90000000 slv/Primary 6`: Added student Nora Lim [Student ID: 6]. Nora is not enrolled in any lesson yet, so the student list shows `Lesson IDs: none`.
1. `add l/Mathematics llv/Primary 6 d/Friday st/15:00 et/16:00 f/50`: Added lesson Mathematics [Lesson ID: 8]. No student is enrolled yet.
1. `add s/6 l/8`: Enrolled Nora Lim [Student ID: 6] in Mathematics [Lesson ID: 8]. Payment status: Unpaid.

The result always gives the new ID, so you can use it even if an active filter hides the new record.

#### Possible sources of errors

Each of these starts from the sample data. The command is shown first, then the message you see.

Command | Message
--------|--------
`add s/1 l/1` | The student is already enrolled in this lesson.
`add s/1 l/99` | No lesson found with ID 99.
`add s/99 l/1` | No student found with ID 99.
`add s/Alice Tan c/91234567 slv/Primary 6 l/1` | A student with the same name and contact already exists (Student ID: 1). Use the existing student ID.
`add s/1 l/Mathematics llv/Primary 6 d/Monday st/15:00 et/16:30 f/60` | An identical lesson already exists (Lesson ID: 1). Use the existing lesson ID.
`add s/1 l/History llv/Primary 6 d/Monday st/16:00 et/17:00 f/35` | Lesson clashes with lesson 1 (Mathematics, Monday 15:00-16:30).
`add s/Daniel Koh slv/Primary 6 l/1` | Missing required parameter: c/. s/Daniel Koh was read as a new student.
`add s/1 l/2 extra words` | Missing required parameter: llv/, d/, st/, et/, f/. l/2 extra words was read as a new lesson. To use an existing lesson, give only its ID, such as l/2.
`add s/Daniel Koh c/92223333slv/Primary 6 l/1` | slv/ was found inside the value of c/. Separate parameters with a space.
`add s/1 l/2 x/value` | Unknown parameter: x/. Use: add s/STUDENT l/LESSON [c/CONTACT slv/STUDENT_LEVEL] [llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE]
`add s/0 l/1` | s/0 is neither an ID nor a valid name. An ID is a positive whole number, such as s/3.
`add s/2147483648 l/1` | ID is too large. No student or lesson has an ID above 2147483647.
`add s/1 l/Art llv/Primary 6 d/Friday st/17:00 et/16:00 f/40` | Invalid end time. Use the 24-hour format, such as 9:00 or 14:30, and enter a time later than the start time.
`add s/1 l/Art llv/Primary 6 d/Friday st/23:00 et/01:00 f/40` | Invalid end time. A lesson must end later on the same day it starts, so it cannot run past midnight. To teach past midnight, split it into two lessons, such as 23:00-23:59 and 00:00-01:00 the next day.
`add s/1 l/1 c/91234567` | c/ and slv/ can only be used when adding a new student.
`add s/1 l/1 d/Monday` | llv/, d/, st/, et/ and f/ can only be used when adding a new lesson.
`add s/1 s/2 l/1` | A parameter can only be specified once.
`add s/1` | Alice Tan [Student ID: 1] already exists. To enroll them, add l/ with a lesson ID or the details of a new lesson.
`add l/1` | Mathematics [Lesson ID: 1] already exists. To enroll a student in it, add s/ with a student ID or the details of a new student.
`add c/90000000 slv/Primary 6` | Missing required parameter: s/.
`add s/Daniel Koh c/12 slv/Primary 6 l/1` | Invalid contact. Use 3 to 15 digits, at most 25 characters in all. Groups of digits can be separated by one space or hyphen, a group can be in brackets, and a + can start the number, such as +65 (9123) 4567.
`add s/1 l/History llv/Primary 6 d/Monday st/9:60 et/10:00 f/35` | Invalid start time. Use the 24-hour format, such as 9:00 or 14:30.
`add` | Invalid command format. Use: add [s/STUDENT] [l/LESSON] [c/CONTACT slv/STUDENT_LEVEL] [llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE]

### Editing a student or lesson: `edit`

Updates one or more details of an existing student or lesson. Details that are not included in the command remain unchanged.

**Format:**

* `edit student ID [s/NAME] [c/CONTACT] [slv/STUDENT_LEVEL]`
* `edit lesson ID [l/SUBJECT] [llv/LESSON_LEVEL] [d/DAY] [st/START_TIME] [et/END_TIME] [f/FEE]`

<box type="tip" seamless>

**Tip:** To find an ID, use `list students` or `list lessons` first.
</box>

#### Parameters

Parameter | Meaning | Used when | Rules
----------|---------|-----------|------
`student\|lesson` | The type of record to edit | always | Must be exactly `student` or `lesson`. Letter case is ignored.
`ID` | The existing record's ID | always | A positive whole number. Leading zeros are allowed and ignored, so `0012` refers to ID `12`.
`s/` | The student's name | editing a student | At most 100 characters: letters, numbers, spaces and `.` `,` `'` `-` `(` `)` `&` `@`, with at least one letter. The words `s/o` and `d/o` can be part of a name, such as `Ravi s/o Kumar`.
`c/` | The student's contact number | editing a student | 3 to 15 digits, at most 25 characters in all. Groups of digits can be separated by one space or hyphen, a group can be in brackets, and a `+` can start the number, such as `+65 (9123) 4567`.
`slv/` | The student's level, such as `Primary 6` | editing a student | 1 to 30 characters: letters, numbers, spaces and hyphens, with at least one letter or number.
`l/` | The lesson's subject | editing a lesson | At most 50 characters: letters, numbers, spaces and `.` `,` `-` `(` `)` `&` `@`, with at least one letter.
`llv/` | The lesson's level, such as `Primary 6` | editing a lesson | The same rules as `slv/`.
`d/` | The day of the week | editing a lesson | `Monday` to `Sunday`, or a short form: `Mon`, `Tue`, `Tues`, `Wed`, `Thu`, `Thur`, `Thurs`, `Fri`, `Sat`, `Sun`. Letter case is ignored.
`st/` | The start time | editing a lesson | In the 24-hour format, such as `9:00`, `09:00` or `14:30`.
`et/` | The end time | editing a lesson | In the same format, and later than the final start time on the same day.
`f/` | The fee for one session | editing a lesson | 0 or more, with at most seven digits before the decimal point and at most two decimal places, such as `50` or `45.50`.

Give at least one editable field after the ID. You can give the field parameters in any order, but each parameter can appear only once. The command word, record type and prefixes ignore letter case. For a lesson, you may give `st/`, `et/`, or both. If you give only one, the other keeps its existing value. The resulting end time must still be later than the start time.

#### Examples

Each one starts from the sample data and is followed by the result you should see.

1. `edit student 1 c/90001111`: Edited student: Alice Tan (ID: 1).
1. `edit student 1 s/Alice Lee slv/Primary 6`: Edited student: Alice Lee (ID: 1).
1. `edit lesson 1 f/55`: Edited lesson: Mathematics | Primary 6 | Monday 15:00-16:30 (ID: 1).
1. `edit lesson 1 d/Friday st/14:00 et/15:30`: Edited lesson: Mathematics | Primary 6 | Friday 14:00-15:30 (ID: 1).
1. `edit student 1 slv/Primary 4`: Edited student: Alice Tan (ID: 1). The result also includes `Note: Alice Tan's level (Primary 4) differs from lesson 1's level (Primary 6).` and a corresponding note for lesson 2.
1. `edit lesson 1 f/0`: Edited lesson: Mathematics | Primary 6 | Monday 15:00-16:30 (ID: 1). The result ends with `Note: the fee is 0. Check that this lesson is meant to be free.`
1. `edit student 1 s/Alice Tan`: No changes were made.

#### Notes

* The record's ID never changes.
* Editing a student preserves their payment status and lesson enrollments.
* Editing a lesson preserves its student enrollments.
* Student duplicates are compared using the name without regard to letter case or extra spaces, and using only the digits of the contact number. Lesson duplicates have the same subject and level without regard to letter case or extra spaces, and the same final day, start time, and end time.
* An edit that results in the same name as another student but a different contact number goes ahead with a note naming that student and ID.
* Changing a student's level goes ahead even if it differs from their enrolled lessons. The result adds one note per mismatching lesson, including its ID. Changing a lesson's level similarly adds one note per affected student.
* Changing a lesson fee to `0` goes ahead with a note asking you to check that it is meant to be free.
* A change in letter case still counts as an edit, including for a student's or lesson's level.
* No data is changed if the command fails validation.
* If the supplied values already match the stored record, TuiTracker reports that no changes were made and does not
  rewrite the data file.

#### Possible sources of errors

Each of these starts from the sample data. The command is shown first, then the message you see.

Command | Message
--------|--------
`edit teacher 1 s/Alice` | Invalid command format. Use: edit student ID [s/NAME] [c/CONTACT] [slv/STUDENT_LEVEL] OR edit lesson ID [l/SUBJECT] [llv/LESSON_LEVEL] [d/DAY] [st/START_TIME] [et/END_TIME] [f/FEE]
`edit student 1` | At least one field to edit must be provided.
`edit student 1 c/90001111 c/92223333` | A parameter can only be specified once.
`edit student 1 s/Alicia x/value` | Unknown parameter: x/. Use: edit student ID [s/NAME] [c/CONTACT] [slv/STUDENT_LEVEL] OR edit lesson ID [l/SUBJECT] [llv/LESSON_LEVEL] [d/DAY] [st/START_TIME] [et/END_TIME] [f/FEE]
`edit student 1 c/92345678slv/Secondary 1` | slv/ was found inside the value of c/. Separate parameters with a space.
`edit student 1 l/English` | Only s/, c/ and slv/ can be used when editing a student.
`edit lesson 1 c/90001111` | Only l/, llv/, d/, st/, et/ and f/ can be used when editing a lesson.
`edit student 0 c/90001111` | ID must be a positive whole number.
`edit lesson 99 f/55` | No lesson found with ID 99.
`edit student 1 s/bryan lim c/9876-5432` | Another student with the same name and contact already exists (Student ID: 2).
`edit lesson 2 l/mathematics d/Mon st/15:00 et/16:30` | Another identical lesson already exists (Lesson ID: 1).
`edit lesson 2 d/Mon st/16:00 et/17:00` | Lesson clashes with lesson 1 (Mathematics, Monday 15:00-16:30).
`edit lesson 1 st/17:00` | Invalid end time. Use the 24-hour format, such as 9:00 or 14:30, and enter a time later than the start time.
`edit lesson 1 et/24:00` | A lesson cannot end at 24:00 or 00:00. To end at midnight, use 23:59.
`edit lesson 1 st/23:00 et/00:59` | Invalid end time. A lesson must end later on the same day it starts, so it cannot run past midnight. To teach past midnight, split it into two lessons, such as 23:00-23:59 and 00:00-01:00 the next day.

Invalid names, contacts, levels, subjects, days, times and fees follow the rules in the parameter table above.

### Deleting a student or a lesson: `delete`

Deletes a student or lesson using its ID. Use this command when you want to remove a record from the app.

**Format:** `delete student|lesson ID`

**Parameters:**

* `student|lesson` — must be exactly `student` or `lesson` (case-insensitive). Abbreviations and repeating the keyword are not allowed.
* `ID` — the record's positive whole-number ID, up to `2147483647`. Leading zeros are allowed and ignored (for example, `0012` refers to ID `12`).
* Leading and trailing spaces are ignored. No other text may follow the ID.

**Examples:**

* `delete student 3` — deletes the student with ID 3.
* `delete lesson 2` — deletes the lesson with ID 2.
* `delete STUDENT 0012` — deletes student 12; keyword case and leading zeros are ignored.

**Successful deletion:**

* The result box confirms the deleted record, for example `Deleted student: Alice Tan (ID: 12)`. If the deleted student was selected in the schedule, the result also says `The schedule now shows all lessons.`
* Deleting a lesson confirms its subject, level, timeslot and ID, for example `Deleted lesson: Mathematics | Primary 6 | Monday 15:00-16:30 (ID: 7)`.

**Notes:**

* Deleting a student also removes all of that student's enrollments. Deleting a lesson removes all enrollments tied to that lesson. Other student and lesson records remain.
* Deleting a record removes it from the displayed list when it is shown. An active filter stays in place; if it refers to the deleted record, its filtered list becomes empty until you change or clear the filter.
* The ID must be a positive whole number. For example, `delete student 0` and `delete student -1` are invalid.

**Errors:**

* Missing or unknown kind, an abbreviation, a repeated kind, a missing or malformed ID, or extra text after the ID — the app displays `Invalid command format. Use: delete student|lesson ID`.
* An ID above `2147483647` — the app displays `ID is too large. No student or lesson has an ID above 2147483647.`
* No record of the requested kind has that ID — the app displays `No student found with ID 999.` or `No lesson found with ID 999.` (using the requested ID). An ID belonging only to the other kind does not match. No data is changed on failure.

### Marking a student as paid: `mark`

Marks one or more students, or all students enrolled in a lesson, as paid for the current month. Use this command after receiving payment for the current month.

**Format:** `mark ID [MORE_IDS]...` or `mark lesson LESSON_ID`

**Parameters:**

* `ID` — one or more student positive whole-number IDs separated by spaces, up to `2147483647`. Leading zeros are allowed and ignored (for example, `0012` refers to ID `12`). Duplicate IDs are merged.
* `lesson LESSON_ID` — the keyword `lesson` followed by a lesson's positive whole-number ID, marking all students enrolled in that lesson as paid.
* Leading and trailing spaces are ignored.

**Examples:**

* `mark 1` — marks student 1 as paid for the current month.
* `mark 1 2 3` — marks students 1, 2, and 3 as paid for the current month.
* `mark lesson 1` — marks all students enrolled in lesson 1 as paid for the current month.

**Successful marking:**

* For a single student: `Marked Alice Tan as paid for the current month.`
* For multiple students: `Marked Alice Tan, Bob Tan as paid for the current month.`
* If some students were already marked as paid: `Marked Alice Tan as paid for the current month. (Already paid: Bob Tan)`
* The payment status of the marked students for the current month is updated to paid.

**Notes:**

* The command records payment only for the current month.
* If a single student is already marked as paid, the command fails and displays an error message.
* When specifying multiple students or a lesson, if some students are already paid, the command marks the remaining unpaid students and informs you of those already paid. If all specified students or lesson students are already paid, no change is made and an error message is displayed.
* Student IDs and lesson IDs are separate.
* The ID must be a positive whole number. For example, `mark 0` and `mark -1` are invalid.

**Errors:**

* A missing ID or invalid format — the app displays `Invalid command format. Use: mark ID [MORE_IDS]... or mark lesson LESSON_ID`.
* A malformed or non-positive ID — the app displays `ID must be a positive whole number.`
* An ID above `2147483647` — the app displays `ID is too large. No student or lesson has an ID above 2147483647.`
* No student or lesson has that ID — the app displays `No student found with ID 999.` or `No lesson found with ID 999.` (using the requested ID).
* No students are enrolled in the lesson — the app displays `No students are enrolled in Lesson 1.`
* All specified students are already marked as paid — the app displays `Alice Tan is already marked as paid for the current month.` or `All specified students are already marked as paid for the current month.` or `All students in Lesson 1 are already marked as paid for the current month.`

No data is changed on failure.

### Correcting a payment to unpaid: `unmark`

Marks one or more students, or all students enrolled in a lesson, as unpaid for the current month. Use this command to correct accidental payment entries.

**Format:** `unmark ID [MORE_IDS]...` or `unmark lesson LESSON_ID`

**Parameters:**

* `ID` — one or more student positive whole-number IDs separated by spaces, up to `2147483647`. Leading zeros are allowed and ignored (for example, `0012` refers to ID `12`). Duplicate IDs are merged.
* `lesson LESSON_ID` — the keyword `lesson` followed by a lesson's positive whole-number ID, marking all students enrolled in that lesson as unpaid.
* Leading and trailing spaces are ignored.

**Examples:**

* `unmark 1` — marks student 1 as unpaid for the current month.
* `unmark 1 2 3` — marks students 1, 2, and 3 as unpaid for the current month.
* `unmark lesson 1` — marks all students enrolled in lesson 1 as unpaid for the current month.

**Successful unmarking:**

* For a single student: `Marked Alice Tan as unpaid for the current month.`
* For multiple students: `Marked Alice Tan, Bob Tan as unpaid for the current month.`
* If some students were already marked as unpaid: `Marked Alice Tan as unpaid for the current month. (Already unpaid: Bob Tan)`

The change is saved immediately and is retained after restarting TuiTracker. The student's details and lesson enrollments are not changed.

### Showing a student's lessons, a lesson's students, lessons by day, subject, level or fee, or students by payment status or level: `filter`

Shows only the lessons that one student is enrolled in, only the students enrolled in one lesson, only the lessons on one day of the week, only the lessons with a given subject, level, or both, only the lessons with a given fee, only the students who are paid or unpaid for the current month, or only the students at a given level.

Format: `filter student|lesson ID`, `filter day DAY`, `filter paid|unpaid`, `filter [l/SUBJECT] [llv/LESSON_LEVEL]`, `filter slv/STUDENT_LEVEL` or `filter f/FEE`

* Shows a student's lessons: `filter student ID`.
* Shows a lesson's students: `filter lesson ID`.
* Shows a day's lessons: `filter day DAY`.
* Shows students by payment status for the current month: `filter paid` or `filter unpaid`.
* Shows lessons by subject, level, or both: `filter l/SUBJECT`, `filter llv/LESSON_LEVEL` or `filter l/SUBJECT llv/LESSON_LEVEL`. At least one of `l/` and `llv/` must be given, and they can be in any order. When both are given, only lessons matching both are shown.
* Shows students at a given level: `filter slv/STUDENT_LEVEL`. This matches the student's own level, not the level of any lesson they are enrolled in.
* Shows lessons at a given fee: `filter f/FEE`. `FEE` must match the lesson's fee exactly, e.g. `f/50` and `f/50.00` both match a lesson with fee `$50.00`.
* `SUBJECT` and `LESSON_LEVEL` must match the lesson's subject and level exactly; letter case is ignored. For example, `l/mathematics` matches `Mathematics` but `l/Math` does not. The same applies to `STUDENT_LEVEL`.
* The `ID` refers to the student ID or lesson ID shown in the displayed list, not its position in the list.
* The specified `ID` must belong to an existing student or lesson.
* `DAY` is the full day name, e.g. `Monday`; letter case is ignored.
* Filtering replaces any earlier filter; only one filter can be active at a time.
* Filtering by a student switches the displayed list to **Lessons**; filtering by a lesson, by payment status, or by student level, switches it to **Students**; filtering by a day, subject, level or fee also switches it to **Lessons**.
* To clear the filter and show every student and lesson again, use `list`.

Examples:
* `filter student 1` shows only the lessons that the student with ID `1` is enrolled in.
* `filter lesson 2` shows only the students enrolled in the lesson with ID `2`.
* `filter day Monday` shows only the lessons that take place on Monday.
* `filter paid` shows only the students who have paid for the current month.
* `filter unpaid` shows only the students who have not paid for the current month.
* `filter l/Mathematics` shows only the Mathematics lessons.
* `filter llv/Primary 6` shows only the Primary 6 lessons.
* `filter l/Mathematics llv/Primary 6` shows only the Primary 6 Mathematics lessons.
* `filter slv/Primary 6` shows only the students at Primary 6.
* `filter f/50` shows only the lessons with fee `$50.00`.

If a filter is incomplete or mixes incompatible forms, TuiTracker explains the specific problem and includes a
valid example. For instance, `filter student` suggests `filter student 1`, while combining `day` with `l/` explains
that keyword and prefix filters cannot be mixed. The same restriction applies to `slv/` and `f/`: neither can be
combined with `student`, `lesson`, `day`, `l/`, `llv/`, or with each other.

### Sorting students and lessons: `sort`

Sorts the currently displayed student or lesson records without changing saved data. The direction defaults to
ascending and can be written explicitly as `asc` or `desc`.

**Format:** `sort student id|name|level|payment|paid [asc|desc]` or `sort lesson id|subject|level|fee|time [asc|desc]`

* For students, you can sort by `id`, `name`, `level`, or payment status (`payment` / `paid`).
* Sorting students by payment status in ascending order (default) places **unpaid** students at the top, making it easy for tutors to see who has yet to pay for the current month. Using `desc` places paid students at the top.
* For lessons, you can sort by `id`, `subject`, `level`, `fee`, or `time` (chronological weekday and start-time order).

Examples:

* `sort student payment` or `sort student paid` sorts students with unpaid students at the top.
* `sort student payment desc` shows paid students before unpaid students.
* `sort student name` sorts students alphabetically by name.
* `sort student level` sorts students by educational level.
* `sort lesson fee desc` shows the highest-fee lessons first.
* `sort lesson time` restores chronological weekday and start-time order.
* `sort lesson id` sorts lessons by their identifier.

### Viewing the weekly schedule: `schedule`

Shows all recurring lessons in a Monday-to-Sunday weekly timeline. Each day runs from 00:00 to 23:59 at the same time scale. Lesson bars span their actual start and end times and show the time and subject; taller bars also show the level and a student summary. Short lessons have a thin duration mark beside a clickable label. Select a bar or label to open a scrollable details window with a separate card for each lesson, including its ID, fee, and enrolled students. A day with no lessons shows `No lessons`; a lesson with no students shows `No students` in its details.

**Format:** `schedule [student ID]`

Use `schedule` to show all lessons, or `schedule student 1` to show only the lessons attended by student `1`. The `student` keyword ignores letter case. The ID must be a positive whole number belonging to an existing student. An empty schedule for an existing student still shows all seven days with `No lessons`. Neither form changes an active list filter; entering plain `schedule` removes any earlier student selection from the schedule.

If you delete the student whose schedule is displayed, the schedule switches to showing all lessons.

The schedule repeats each week because lessons are stored by weekday and time, without calendar dates. The current weekday is outlined. Scroll horizontally to see all seven day columns, and scroll within a day to move through its 24-hour timeline. Lessons whose labels would overlap are grouped into one clickable bar or marker; the details window keeps every lesson separate. A group does not necessarily mean a timetable clash. Actual overlapping times are highlighted, with a warning naming the affected day. Use `list` or `filter` to switch back to a student or lesson list.

The schedule can be explored entirely from the keyboard:

| Key | Action |
|-----|--------|
| `F6` | Move focus from the command box to Monday in the schedule. |
| `Left` / `Right` | Move to the previous or next day. |
| `Up` / `Down` | Move between that day's lesson bars or markers. On an empty day, scroll by one hour. |
| `Enter` / `Space` | From a day heading, select its first lesson; from a lesson, open its details. |
| `Page Up` / `Page Down`, `Home` / `End` | Scroll the focused day through its 24-hour timeline. |
| `Tab` | Move through the details window's scrollable cards and Close button. |
| `Esc` | Close the details window; press it again to return to the command box. |

When focus moves to a day or lesson, the schedule scrolls to keep it visible. The focused heading or lesson is highlighted.

If there are no lessons, the result box shows `No lessons are scheduled for this week.` An ID with no matching student shows `No student found with ID 999.` (using the entered ID). A missing ID or extra text shows `Invalid command format. Use: schedule [student ID]`; an unknown keyword shows `Unknown parameter. Use: schedule [student ID]`. Invalid IDs use the same ID error messages as other commands.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

TuiTracker automatically saves data after each data-changing command. You do not need to save manually. 

If the data file cannot be loaded at startup, data-changing commands are blocked until you repair or replace the file and restart TuiTracker.

### Editing the data file

TuiTracker data is saved automatically as a JSON file `[JAR file location]/data/tuitracker.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, TuiTracker warns you at the next launch and opens in read-only recovery mode. It shows an empty list, but leaves the invalid file untouched and blocks data-changing commands. Back up the file, repair or replace it, then restart TuiTracker to resume editing.<br>
Furthermore, certain edits can cause TuiTracker to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous TuiTracker home folder.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add [s/STUDENT] [l/LESSON] [c/CONTACT slv/STUDENT_LEVEL] [llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE]` <br> e.g., `add s/Daniel Koh c/92223333 slv/Primary 6 l/1`, `add s/Nora Lim c/90000000 slv/Primary 6`, `add l/Mathematics llv/Primary 6 d/Friday st/15:00 et/16:00 f/50`
**Edit**   | `edit student ID [s/NAME] [c/CONTACT] [slv/STUDENT_LEVEL]` <br> `edit lesson ID [l/SUBJECT] [llv/LESSON_LEVEL] [d/DAY] [st/START_TIME] [et/END_TIME] [f/FEE]`
**Delete** | `delete student\|lesson ID` <br> `delete student 3`, `delete lesson 2`
**Filter** | `filter student\|lesson ID`, `filter day DAY`, `filter paid\|unpaid`, `filter [l/SUBJECT] [llv/LESSON_LEVEL]`, `filter slv/STUDENT_LEVEL` or `filter f/FEE` <br> e.g., `filter student 1`, `filter lesson 2`, `filter day Monday`, `filter paid`, `filter l/Mathematics llv/Primary 6`, `filter slv/Primary 6`, `filter f/50`
**List**   | `list [students\|lessons]` <br> e.g., `list`, `list students`, `list lessons`
**Mark**   | `mark ID [MORE_IDS]...` or `mark lesson LESSON_ID` <br> e.g., `mark 1`, `mark 1 2 3`, `mark lesson 1`
**Unmark** | `unmark ID [MORE_IDS]...` or `unmark lesson LESSON_ID` <br> e.g., `unmark 1`, `unmark 1 2 3`, `unmark lesson 1`
**Schedule** | `schedule [student ID]` <br> e.g., `schedule`, `schedule student 1`; shows recurring lessons from Monday to Sunday
**Sort** | `sort student id\|name\|level\|payment\|paid [asc\|desc]` or `sort lesson id\|subject\|level\|fee\|time [asc\|desc]` <br> e.g., `sort student payment`, `sort lesson fee desc`
**Help**   | `help`
**Exit**   | `exit`
