package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.LessonId;

public class StudentCardTest {
    @Test
    public void lessonIdsText_noLessons_saysNone() {
        assertEquals("Lesson IDs: none", StudentCard.lessonIdsText(List.of()));
    }

    @Test
    public void lessonIdsText_oneLesson_isLabelledAsAnId() {
        assertEquals("Lesson IDs: 2", StudentCard.lessonIdsText(List.of(new LessonId(2))));
    }

    @Test
    public void lessonIdsText_severalLessons_separatesTheIdsWithCommas() {
        assertEquals("Lesson IDs: 1, 3, 12",
                StudentCard.lessonIdsText(List.of(new LessonId(1), new LessonId(3), new LessonId(12))));
    }
}
