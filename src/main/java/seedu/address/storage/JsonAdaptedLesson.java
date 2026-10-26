package seedu.address.storage;

import static seedu.address.storage.JsonFields.validated;

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
        LessonId modelId = validated(required("id", id),
                LessonId::isValidId, LessonId.MESSAGE_CONSTRAINTS, LessonId::new);
        Subject modelSubject = validated(required("subject", subject),
                Subject::isValidSubject, Subject.MESSAGE_CONSTRAINTS, Subject::new);
        Level modelLevel = validated(required("level", level),
                Level::isValidLevel, Level.MESSAGE_CONSTRAINTS_LESSON, Level::new);
        Timeslot modelTimeslot = toTimeslot();
        Fee modelFee = validated(required("fee", fee), Fee::isValidFee, Fee.MESSAGE_CONSTRAINTS, Fee::new);

        return new Lesson(modelId, modelSubject, modelLevel, modelTimeslot, modelFee);
    }

    private Timeslot toTimeslot() throws IllegalValueException {
        DayOfWeek modelDay = Timeslot.parseDay(required("day", day))
                .orElseThrow(() -> new IllegalValueException(Timeslot.MESSAGE_INVALID_DAY));
        LocalTime modelStart = Timeslot.parseTime(required("start", start))
                .orElseThrow(() -> new IllegalValueException(Timeslot.MESSAGE_INVALID_START_TIME));
        LocalTime modelEnd = Timeslot.parseTime(required("end", end))
                .orElseThrow(() -> new IllegalValueException(Timeslot.MESSAGE_INVALID_END_TIME));
        if (!Timeslot.isValidTimeslot(modelStart, modelEnd)) {
            throw new IllegalValueException(Timeslot.MESSAGE_INVALID_END_TIME);
        }
        return new Timeslot(modelDay, modelStart, modelEnd);
    }

    private static <T> T required(String fieldName, T value) throws IllegalValueException {
        return JsonFields.required(value, MISSING_FIELD_MESSAGE_FORMAT, fieldName);
    }

}
