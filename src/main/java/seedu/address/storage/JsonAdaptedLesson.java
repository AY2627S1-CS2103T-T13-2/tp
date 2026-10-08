package seedu.address.storage;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.common.Level;
import seedu.address.model.lesson.Fee;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Timeslot;

/**
 * Jackson-friendly version of {@link Lesson}.
 */
class JsonAdaptedLesson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Lesson's %s field is missing!";

    private final Integer id;
    private final String subject;
    private final String level;
    private final String day;
    private final String start;
    private final String end;
    private final String fee;

    /**
     * Constructs a {@code JsonAdaptedLesson} with the given lesson details.
     */
    @JsonCreator
    public JsonAdaptedLesson(@JsonProperty("id") Integer id, @JsonProperty("subject") String subject,
            @JsonProperty("level") String level, @JsonProperty("day") String day,
            @JsonProperty("start") String start, @JsonProperty("end") String end,
            @JsonProperty("fee") String fee) {
        this.id = id;
        this.subject = subject;
        this.level = level;
        this.day = day;
        this.start = start;
        this.end = end;
        this.fee = fee;
    }

    /**
     * Converts a given {@code Lesson} into this class for Jackson use.
     */
    public JsonAdaptedLesson(Lesson source) {
        id = source.getId().value;
        subject = source.getSubject().value;
        level = source.getLevel().value;
        day = Timeslot.dayName(source.getTimeslot().getDay());
        start = Timeslot.timeText(source.getTimeslot().getStart());
        end = Timeslot.timeText(source.getTimeslot().getEnd());
        fee = source.getFee().toPlainString();
    }

    /**
     * Converts this Jackson-friendly adapted lesson object into the model's {@code Lesson} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted lesson.
     */
    public Lesson toModelType() throws IllegalValueException {
        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"));
        }
        if (!LessonId.isValidId(id)) {
            throw new IllegalValueException(LessonId.MESSAGE_CONSTRAINTS);
        }
        final LessonId modelId = new LessonId(id);

        if (subject == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "subject"));
        }
        if (!Subject.isValidSubject(subject)) {
            throw new IllegalValueException(Subject.MESSAGE_CONSTRAINTS);
        }
        final Subject modelSubject = new Subject(subject);

        if (level == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "level"));
        }
        if (!Level.isValidLevel(level)) {
            throw new IllegalValueException(Level.MESSAGE_CONSTRAINTS_LESSON);
        }
        final Level modelLevel = new Level(level);

        if (day == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "day"));
        }
        final DayOfWeek modelDay = Timeslot.parseDay(day)
                .orElseThrow(() -> new IllegalValueException(Timeslot.MESSAGE_INVALID_DAY));

        if (start == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "start"));
        }
        final LocalTime modelStart = Timeslot.parseTime(start)
                .orElseThrow(() -> new IllegalValueException(Timeslot.MESSAGE_INVALID_START_TIME));

        if (end == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "end"));
        }
        final LocalTime modelEnd = Timeslot.parseTime(end)
                .orElseThrow(() -> new IllegalValueException(Timeslot.MESSAGE_INVALID_END_TIME));
        if (!Timeslot.isValidTimeslot(modelStart, modelEnd)) {
            throw new IllegalValueException(Timeslot.MESSAGE_INVALID_END_TIME);
        }

        if (fee == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "fee"));
        }
        if (!Fee.isValidFee(fee)) {
            throw new IllegalValueException(Fee.MESSAGE_CONSTRAINTS);
        }

        return new Lesson(modelId, modelSubject, modelLevel, new Timeslot(modelDay, modelStart, modelEnd),
                new Fee(fee));
    }

}
