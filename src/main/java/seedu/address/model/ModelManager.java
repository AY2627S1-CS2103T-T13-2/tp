package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;
import seedu.address.model.student.Contact;
import seedu.address.model.student.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Represents the in-memory model of the TuiTracker data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    /** Students are shown in ascending order of ID. */
    private static final Comparator<Student> STUDENT_ORDER = Comparator.comparing(Student::getId);

    /** Lessons are shown by day of the week (Monday first), then start time, then ID. */
    private static final Comparator<Lesson> LESSON_ORDER = Comparator
            .comparing((Lesson lesson) -> lesson.getTimeslot().getDay())
            .thenComparing(lesson -> lesson.getTimeslot().getStart())
            .thenComparing(Lesson::getId);

    private final TuiTracker tuiTracker;
    private final UserPrefs userPrefs;
    private final Clock clock;
    private final FilteredList<Student> filteredStudents;
    private final FilteredList<Lesson> filteredLessons;
    private final ObjectProperty<ListView> listView = new SimpleObjectProperty<>(ListView.STUDENTS);
    private Optional<StudentId> scheduleStudentId = Optional.empty();

    /**
     * The filter that is currently active, if any. Only one kind is set at a time: the student, the lesson,
     * the day, or the subject and level (either or both of which may be set).
     */
    private StudentId filteredByStudent;
    private LessonId filteredByLesson;
    private DayOfWeek filteredByDay;
    private Subject filteredBySubject;
    private Level filteredByLevel;

    /**
     * Initializes a ModelManager with the given tuiTracker, userPrefs and clock.
     * The clock decides which month counts as the current month, so tests can fix the date.
     */
    public ModelManager(ReadOnlyTuiTracker tuiTracker, ReadOnlyUserPrefs userPrefs, Clock clock) {
        requireAllNonNull(tuiTracker, userPrefs, clock);

        logger.fine("Initializing with " + tuiTracker.getStudentList().size() + " students, "
                + tuiTracker.getLessonList().size() + " lessons and user prefs " + userPrefs);

        this.tuiTracker = new TuiTracker(tuiTracker);
        this.userPrefs = new UserPrefs(userPrefs);
        this.clock = clock;
        filteredStudents = new FilteredList<>(new SortedList<>(this.tuiTracker.getStudentList(), STUDENT_ORDER));
        filteredLessons = new FilteredList<>(new SortedList<>(this.tuiTracker.getLessonList(), LESSON_ORDER));
    }

    /**
     * Initializes a ModelManager that uses the system clock.
     */
    public ModelManager(ReadOnlyTuiTracker tuiTracker, ReadOnlyUserPrefs userPrefs) {
        this(tuiTracker, userPrefs, Clock.systemDefaultZone());
    }

    public ModelManager() {
        this(new TuiTracker(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== TuiTracker =================================================================================

    @Override
    public void setTuiTracker(ReadOnlyTuiTracker newData) {
        tuiTracker.resetData(newData);
        refreshFilters();
    }

    @Override
    public ReadOnlyTuiTracker getTuiTracker() {
        return tuiTracker;
    }

    @Override
    public Optional<Student> findStudent(StudentId id) {
        return tuiTracker.findStudent(id);
    }

    @Override
    public Optional<Lesson> findLesson(LessonId id) {
        return tuiTracker.findLesson(id);
    }

    @Override
    public Optional<Student> findDuplicateStudent(Name name, Contact contact) {
        return tuiTracker.findDuplicateStudent(name, contact);
    }

    @Override
    public Optional<Student> findStudentWithSameName(Name name) {
        return tuiTracker.findStudentWithSameName(name);
    }

    @Override
    public Optional<Lesson> findIdenticalLesson(Subject subject, Level level, Timeslot timeslot) {
        return tuiTracker.findIdenticalLesson(subject, level, timeslot);
    }

    @Override
    public boolean isEnrolled(StudentId studentId, LessonId lessonId) {
        return tuiTracker.isEnrolled(studentId, lessonId);
    }

    @Override
    public boolean hasClash(Timeslot timeslot) {
        return tuiTracker.hasClash(timeslot);
    }

    @Override
    public Optional<Lesson> findClash(Timeslot timeslot) {
        return tuiTracker.findClash(timeslot);
    }

    @Override
    public List<LessonId> getLessonIdsOf(StudentId studentId) {
        return tuiTracker.getLessonIdsOf(studentId);
    }

    @Override
    public List<Student> getStudentsIn(LessonId lessonId) {
        return tuiTracker.getStudentsIn(lessonId);
    }

    //=========== Payment ====================================================================================

    @Override
    public YearMonth currentMonth() {
        return YearMonth.now(clock);
    }

    @Override
    public boolean isPaid(StudentId studentId) {
        return tuiTracker.findStudent(studentId)
                .map(student -> student.isPaid(currentMonth()))
                .orElse(false);
    }

    @Override
    public Student markPaid(StudentId studentId) {
        return tuiTracker.markPaid(studentId, currentMonth());
    }

    //=========== Create, edit and delete ====================================================================

    @Override
    public Student addStudent(Name name, Contact contact, Level level) {
        Student student = tuiTracker.addStudent(name, contact, level);
        refreshFilters();
        return student;
    }

    @Override
    public Lesson addLesson(Subject subject, Level level, Timeslot timeslot, Fee fee) {
        Lesson lesson = tuiTracker.addLesson(subject, level, timeslot, fee);
        refreshFilters();
        return lesson;
    }

    @Override
    public Student editStudent(StudentId studentId, Name name, Contact contact, Level level) {
        Student student = tuiTracker.editStudent(studentId, name, contact, level);
        refreshFilters();
        return student;
    }

    @Override
    public Lesson editLesson(LessonId lessonId, Subject subject, Level level, Timeslot timeslot, Fee fee) {
        Lesson lesson = tuiTracker.editLesson(lessonId, subject, level, timeslot, fee);
        refreshFilters();
        return lesson;
    }

    @Override
    public void enroll(StudentId studentId, LessonId lessonId) {
        tuiTracker.enroll(studentId, lessonId);
        refreshFilters();
    }

    @Override
    public Student deleteStudent(StudentId studentId) {
        requireNonNull(studentId);
        Student deleted = tuiTracker.deleteStudent(studentId);
        refreshFilters();
        return deleted;
    }

    @Override
    public Lesson deleteLesson(LessonId lessonId) {
        requireNonNull(lessonId);
        Lesson deleted = tuiTracker.deleteLesson(lessonId);
        refreshFilters();
        return deleted;
    }

    //=========== Filter =====================================================================================

    @Override
    public void filterLessonsByStudent(StudentId studentId) {
        requireNonNull(studentId);
        clearFilterFields();
        filteredByStudent = studentId;
        refreshFilters();
    }

    @Override
    public void filterStudentsByLesson(LessonId lessonId) {
        requireNonNull(lessonId);
        clearFilterFields();
        filteredByLesson = lessonId;
        refreshFilters();
    }

    @Override
    public void filterLessonsByDay(DayOfWeek day) {
        requireNonNull(day);
        clearFilterFields();
        filteredByDay = day;
        refreshFilters();
    }

    @Override
    public void filterLessonsBySubjectAndLevel(Subject subject, Level level) {
        checkArgument(subject != null || level != null, "At least one of subject and level must be given.");
        clearFilterFields();
        filteredBySubject = subject;
        filteredByLevel = level;
        refreshFilters();
    }

    @Override
    public void resetFilters() {
        clearFilterFields();
        refreshFilters();
    }

    private void clearFilterFields() {
        filteredByStudent = null;
        filteredByLesson = null;
        filteredByDay = null;
        filteredBySubject = null;
        filteredByLevel = null;
    }

    /**
     * Applies the active filter, if any, to both lists. Setting a new predicate makes the lists re-check every
     * item, which is how a filter stays correct after enrollments change even though the lists themselves did not.
     */
    private void refreshFilters() {
        final LessonId lessonFilter = filteredByLesson;
        final StudentId studentFilter = filteredByStudent;
        final DayOfWeek dayFilter = filteredByDay;
        final Subject subjectFilter = filteredBySubject;
        final Level levelFilter = filteredByLevel;

        Predicate<Student> studentPredicate = lessonFilter == null
                ? student -> true
                : student -> tuiTracker.isEnrolled(student.getId(), lessonFilter);
        Predicate<Lesson> lessonPredicate;
        if (studentFilter != null) {
            lessonPredicate = lesson -> tuiTracker.isEnrolled(studentFilter, lesson.getId());
        } else if (dayFilter != null) {
            lessonPredicate = lesson -> lesson.getTimeslot().getDay() == dayFilter;
        } else if (subjectFilter != null || levelFilter != null) {
            lessonPredicate = lesson -> (subjectFilter == null
                    || lesson.getSubject().value.equalsIgnoreCase(subjectFilter.value))
                    && (levelFilter == null || lesson.getLevel().equals(levelFilter));
        } else {
            lessonPredicate = lesson -> true;
        }

        filteredStudents.setPredicate(studentPredicate);
        filteredLessons.setPredicate(lessonPredicate);
    }

    //=========== View =======================================================================================

    @Override
    public ObservableList<Student> getStudentList() {
        return filteredStudents;
    }

    @Override
    public ObservableList<Lesson> getLessonList() {
        return filteredLessons;
    }

    @Override
    public ListView getListView() {
        return listView.get();
    }

    @Override
    public void setListView(ListView listView) {
        requireNonNull(listView);
        this.listView.set(listView);
    }

    @Override
    public Optional<StudentId> getScheduleStudentId() {
        return scheduleStudentId;
    }

    @Override
    public void setScheduleStudentId(Optional<StudentId> studentId) {
        scheduleStudentId = requireNonNull(studentId);
    }

    @Override
    public ReadOnlyObjectProperty<ListView> listViewProperty() {
        return listView;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return tuiTracker.equals(otherModelManager.tuiTracker)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredStudents.equals(otherModelManager.filteredStudents)
                && filteredLessons.equals(otherModelManager.filteredLessons)
                && scheduleStudentId.equals(otherModelManager.scheduleStudentId)
                && getListView() == otherModelManager.getListView();
    }

    @Override
    public int hashCode() {
        return Objects.hash(tuiTracker, userPrefs, scheduleStudentId, getListView());
    }

}
