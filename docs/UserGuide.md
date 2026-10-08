---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AB-3 User Guide

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all students.

   * `list lessons` : Lists all lessons.

   * `add s/Alice Tan c/91234567 slv/Primary 6 l/Mathematics llv/Primary 6 d/Monday st/15:00 et/16:30 f/50.00` : Adds a student named `Alice Tan` to a new Mathematics lesson.

   * `mark 1` : Marks the student with ID 1 as paid for the current month.

   * `delete student 3` : Deletes the student with ID 3.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `delete student ID`, replace `ID` with a value such as `12`.

* Items in square brackets are optional.<br>
  For example, `list [students|lessons]` can be used as `list students` or as just `list`.

* Parameters that start with a prefix, such as `s/` or `l/` in `add`, can be in any order.<br>
  For example, if the command specifies `s/STUDENT l/LESSON`, `l/LESSON s/STUDENT` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help` and `exit`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student to a lesson: `add`

_To be written by the owner of `add`. Specification codes: ADD-1 to ADD-15._

Format: `add s/STUDENT l/LESSON [c/CONTACT slv/STUDENT_LEVEL] [llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE]`

### Deleting a student or a lesson: `delete`

_To be written by the owner of `delete`. Specification codes: DEL-1 to DEL-6._

Format: `delete student|lesson ID`

### Listing students or lessons: `list`

_To be written by the owner of `list`. Specification codes: LST-1 to LST-9._

Format: `list [students|lessons]`

### Marking a student as paid: `mark`

_To be written by the owner of `mark`. Specification codes: MRK-1 to MRK-4._

Format: `mark ID`

### Showing a student's lessons or a lesson's students: `filter`

_To be written by the owner of `filter`. Specification codes: FLT-1 to FLT-9 (a draft that is not yet agreed)._

Format: `filter student|lesson ID`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/tuitracker.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, TuiTracker starts with no students or lessons at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add s/STUDENT l/LESSON [c/CONTACT slv/STUDENT_LEVEL] [llv/LESSON_LEVEL d/DAY st/START_TIME et/END_TIME f/FEE]` <br> _Examples to be added by the owner of `add`._
**Delete** | `delete student\|lesson ID` <br> _Examples to be added by the owner of `delete`._
**Filter** | `filter student\|lesson ID` <br> _Examples to be added by the owner of `filter`._
**List**   | `list [students\|lessons]` <br> _Examples to be added by the owner of `list`._
**Mark**   | `mark ID` <br> _Examples to be added by the owner of `mark`._
**Help**   | `help`
**Exit**   | `exit`
