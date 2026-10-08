package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents the weekly timeslot of a Lesson: a day of the week with a start time and an end time.
 * The end time is later than the start time, so a timeslot never runs past midnight.
 * Guarantees: immutable; is valid as declared in {@link #isValidTimeslot(LocalTime, LocalTime)}
 */
public class Timeslot {

    public static final String MESSAGE_INVALID_DAY = "Invalid day. Enter a day from Monday to Sunday.";
    public static final String MESSAGE_INVALID_START_TIME = "Invalid start time. Use the 24-hour format HH:mm.";
    public static final String MESSAGE_INVALID_END_TIME =
            "Invalid end time. Use HH:mm and enter a time later than the start time.";

    /** Exactly two-digit hour, a colon and a two-digit minute, e.g. {@code 09:00}. */
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final DayOfWeek day;
    private final LocalTime start;
    private final LocalTime end;

    /**
     * Constructs a {@code Timeslot}.
     *
     * @param day Day of the week.
     * @param start Start time.
     * @param end End time, which must be later than {@code start}.
     */
    public Timeslot(DayOfWeek day, LocalTime start, LocalTime end) {
        requireNonNull(day);
        requireNonNull(start);
        requireNonNull(end);
        checkArgument(isValidTimeslot(start, end), MESSAGE_INVALID_END_TIME);
        this.day = day;
        this.start = start;
        this.end = end;
    }

    /**
     * Returns true if {@code end} is later than {@code start}.
     */
    public static boolean isValidTimeslot(LocalTime start, LocalTime end) {
        return end.isAfter(start);
    }

    /**
     * Parses a day name such as "Monday". Letter case is ignored.
     * Returns an empty {@code Optional} if {@code test} is not a day of the week.
     */
    public static Optional<DayOfWeek> parseDay(String test) {
        requireNonNull(test);
        String trimmed = test.trim();
        for (DayOfWeek candidate : DayOfWeek.values()) {
            if (dayName(candidate).equalsIgnoreCase(trimmed)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    /**
     * Parses a time written exactly as {@code HH:mm} (24-hour), so "09:00" is accepted and "9:00" is not.
     * Returns an empty {@code Optional} if {@code test} is not in that format.
     */
    public static Optional<LocalTime> parseTime(String test) {
        requireNonNull(test);
        String trimmed = test.trim();
        if (!TIME_PATTERN.matcher(trimmed).matches()) {
            return Optional.empty();
        }
        return Optional.of(LocalTime.parse(trimmed));
    }

    /**
     * Returns the English name of {@code day}, e.g. "Monday".
     */
    public static String dayName(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    /**
     * Returns {@code time} formatted as {@code HH:mm}.
     */
    public static String timeText(LocalTime time) {
        return time.format(TIME_FORMAT);
    }

    /**
     * Returns true if this timeslot overlaps {@code other}, i.e. they are on the same day and each starts
     * before the other ends. A timeslot that starts exactly when another ends does not overlap it.
     */
    public boolean overlaps(Timeslot other) {
        requireNonNull(other);
        return day == other.day && start.isBefore(other.end) && other.start.isBefore(end);
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return dayName(day) + " " + timeText(start) + "-" + timeText(end);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Timeslot otherTimeslot)) {
            return false;
        }

        return day == otherTimeslot.day && start.equals(otherTimeslot.start) && end.equals(otherTimeslot.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, start, end);
    }

}
