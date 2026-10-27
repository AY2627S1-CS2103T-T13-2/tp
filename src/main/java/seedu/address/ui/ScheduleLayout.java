package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;

/** Groups recurring lessons by weekday and identifies overlaps without hiding either lesson. */
final class ScheduleLayout {

    /** The real time span of one lesson or a compact group of nearby lessons. */
    record DisplayGroup(int startMinute, int endMinute, List<Lesson> lessons) {
    }

    private static final Comparator<Lesson> LESSON_ORDER = Comparator
            .comparing((Lesson lesson) -> lesson.getTimeslot().getStart())
            .thenComparing(Lesson::getId);

    private ScheduleLayout() {
    }

    /** Returns all seven days, each with its lessons ordered by start time and then ID. */
    static Map<DayOfWeek, List<Lesson>> groupByDay(List<Lesson> lessons) {
        requireNonNull(lessons);
        Map<DayOfWeek, List<Lesson>> byDay = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            byDay.put(day, new ArrayList<>());
        }
        for (Lesson lesson : lessons) {
            byDay.get(lesson.getTimeslot().getDay()).add(lesson);
        }
        byDay.values().forEach(dayLessons -> dayLessons.sort(LESSON_ORDER));
        return byDay;
    }

    /** Returns the IDs of every lesson that overlaps another lesson on the same day. */
    static Set<LessonId> overlappingLessonIds(Map<DayOfWeek, List<Lesson>> byDay) {
        requireNonNull(byDay);
        Set<LessonId> overlapping = new HashSet<>();
        for (List<Lesson> dayLessons : byDay.values()) {
            for (int first = 0; first < dayLessons.size(); first++) {
                Lesson firstLesson = dayLessons.get(first);
                for (int second = first + 1; second < dayLessons.size(); second++) {
                    Lesson secondLesson = dayLessons.get(second);
                    if (firstLesson.getTimeslot().overlaps(secondLesson.getTimeslot())) {
                        overlapping.add(firstLesson.getId());
                        overlapping.add(secondLesson.getId());
                    }
                }
            }
        }
        return overlapping;
    }

    /**
     * Combines lessons whose bars or short-marker labels would overlap at the schedule's fixed scale.
     * Actual overlaps are still detected independently by {@link #overlappingLessonIds(Map)}.
     */
    static List<DisplayGroup> groupForDisplay(List<Lesson> lessons, int minimumBarMinutes, int markerLabelMinutes) {
        requireNonNull(lessons);
        if (minimumBarMinutes <= 0 || markerLabelMinutes <= 0) {
            throw new IllegalArgumentException("Display durations must be positive");
        }

        List<Lesson> ordered = new ArrayList<>(lessons);
        ordered.sort(LESSON_ORDER);
        List<DisplayGroup> groups = new ArrayList<>();
        List<Lesson> current = new ArrayList<>();
        int start = 0;
        int end = 0;
        for (Lesson lesson : ordered) {
            int lessonStart = minuteOfDay(lesson.getTimeslot().getStart());
            int lessonEnd = minuteOfDay(lesson.getTimeslot().getEnd());
            if (!current.isEmpty() && visibleStart(lessonStart, lessonEnd, minimumBarMinutes, markerLabelMinutes)
                    >= visibleEnd(start, end, minimumBarMinutes, markerLabelMinutes)) {
                groups.add(new DisplayGroup(start, end, List.copyOf(current)));
                current.clear();
            }
            if (current.isEmpty()) {
                start = lessonStart;
                end = lessonEnd;
            } else {
                end = Math.max(end, lessonEnd);
            }
            current.add(lesson);
        }
        if (!current.isEmpty()) {
            groups.add(new DisplayGroup(start, end, List.copyOf(current)));
        }
        return groups;
    }

    private static int minuteOfDay(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    private static double visibleStart(int start, int end, int minimumBarMinutes, int markerLabelMinutes) {
        return end - start < minimumBarMinutes ? (start + end - markerLabelMinutes) / 2.0 : start;
    }

    private static double visibleEnd(int start, int end, int minimumBarMinutes, int markerLabelMinutes) {
        return end - start < minimumBarMinutes ? (start + end + markerLabelMinutes) / 2.0 : end;
    }
}
