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

    /** A lesson's horizontal track within its day. */
    record PlacedLesson(Lesson lesson, int lane) {
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
     * Assigns simultaneous lessons different lanes so their cards remain visible.
     * A lane becomes available as soon as its previous lesson ends.
     */
    static List<PlacedLesson> placeInLanes(List<Lesson> lessons) {
        requireNonNull(lessons);
        List<Lesson> ordered = new ArrayList<>(lessons);
        ordered.sort(LESSON_ORDER);

        List<LocalTime> laneEndTimes = new ArrayList<>();
        List<PlacedLesson> placements = new ArrayList<>();
        for (Lesson lesson : ordered) {
            LocalTime start = lesson.getTimeslot().getStart();
            int lane = 0;
            while (lane < laneEndTimes.size() && start.isBefore(laneEndTimes.get(lane))) {
                lane++;
            }
            if (lane == laneEndTimes.size()) {
                laneEndTimes.add(lesson.getTimeslot().getEnd());
            } else {
                laneEndTimes.set(lane, lesson.getTimeslot().getEnd());
            }
            placements.add(new PlacedLesson(lesson, lane));
        }
        return placements;
    }
}
