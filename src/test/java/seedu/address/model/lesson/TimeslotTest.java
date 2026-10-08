package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class TimeslotTest {

    private static Timeslot slot(DayOfWeek day, int startHour, int startMinute, int endHour, int endMinute) {
        return new Timeslot(day, LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Timeslot(null, LocalTime.NOON, LocalTime.MAX));
        assertThrows(NullPointerException.class, () -> new Timeslot(DayOfWeek.MONDAY, null, LocalTime.MAX));
        assertThrows(NullPointerException.class, () -> new Timeslot(DayOfWeek.MONDAY, LocalTime.NOON, null));
    }

    @Test
    public void constructor_endNotAfterStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> slot(DayOfWeek.MONDAY, 15, 0, 15, 0)); // zero length
        assertThrows(IllegalArgumentException.class, () -> slot(DayOfWeek.MONDAY, 16, 0, 15, 0)); // negative length
    }

    @Test
    public void parseDay() {
        assertEquals(Optional.of(DayOfWeek.MONDAY), Timeslot.parseDay("Monday"));
        assertEquals(Optional.of(DayOfWeek.MONDAY), Timeslot.parseDay("mOnDaY")); // letter case is ignored
        assertEquals(Optional.of(DayOfWeek.SUNDAY), Timeslot.parseDay(" Sunday ")); // surrounding spaces
        assertEquals(Optional.empty(), Timeslot.parseDay("Mon")); // abbreviation
        assertEquals(Optional.empty(), Timeslot.parseDay("Someday"));
        assertEquals(Optional.empty(), Timeslot.parseDay(""));
        assertThrows(NullPointerException.class, () -> Timeslot.parseDay(null));
    }

    @Test
    public void parseTime_requiresExactlyHhMm() {
        assertEquals(Optional.of(LocalTime.of(9, 5)), Timeslot.parseTime("09:05"));
        assertEquals(Optional.of(LocalTime.of(0, 0)), Timeslot.parseTime("00:00"));
        assertEquals(Optional.of(LocalTime.of(23, 59)), Timeslot.parseTime("23:59"));
        assertEquals(Optional.of(LocalTime.of(15, 0)), Timeslot.parseTime(" 15:00 ")); // surrounding spaces

        assertEquals(Optional.empty(), Timeslot.parseTime("9:00")); // one-digit hour
        assertEquals(Optional.empty(), Timeslot.parseTime("24:00")); // hour out of range
        assertEquals(Optional.empty(), Timeslot.parseTime("12:60")); // minute out of range
        assertEquals(Optional.empty(), Timeslot.parseTime("1500")); // no colon
        assertEquals(Optional.empty(), Timeslot.parseTime("15:00:00")); // seconds
        assertEquals(Optional.empty(), Timeslot.parseTime("3pm"));
        assertEquals(Optional.empty(), Timeslot.parseTime(""));
        assertThrows(NullPointerException.class, () -> Timeslot.parseTime(null));
    }

    @Test
    public void overlaps() {
        Timeslot base = slot(DayOfWeek.MONDAY, 15, 0, 16, 30);

        assertTrue(base.overlaps(base)); // itself
        assertTrue(base.overlaps(slot(DayOfWeek.MONDAY, 16, 0, 17, 0))); // overlaps the end
        assertTrue(base.overlaps(slot(DayOfWeek.MONDAY, 14, 0, 15, 30))); // overlaps the start
        assertTrue(base.overlaps(slot(DayOfWeek.MONDAY, 15, 30, 16, 0))); // inside
        assertTrue(base.overlaps(slot(DayOfWeek.MONDAY, 14, 0, 18, 0))); // contains it

        assertFalse(base.overlaps(slot(DayOfWeek.MONDAY, 16, 30, 17, 30))); // starts exactly when it ends
        assertFalse(base.overlaps(slot(DayOfWeek.MONDAY, 14, 0, 15, 0))); // ends exactly when it starts
        assertFalse(base.overlaps(slot(DayOfWeek.MONDAY, 17, 0, 18, 0))); // later the same day
        assertFalse(base.overlaps(slot(DayOfWeek.TUESDAY, 15, 0, 16, 30))); // same time, other day
        assertThrows(NullPointerException.class, () -> base.overlaps(null));
    }

    @Test
    public void toString_showsDayAndTimes() {
        assertEquals("Monday 15:00-16:30", slot(DayOfWeek.MONDAY, 15, 0, 16, 30).toString());
        assertEquals("Saturday 09:05-10:00", slot(DayOfWeek.SATURDAY, 9, 5, 10, 0).toString());
    }

    @Test
    public void equals() {
        Timeslot base = slot(DayOfWeek.MONDAY, 15, 0, 16, 30);

        assertTrue(base.equals(slot(DayOfWeek.MONDAY, 15, 0, 16, 30)));
        assertTrue(base.equals(base));
        assertFalse(base.equals(null));
        assertFalse(base.equals(5.0f));
        assertFalse(base.equals(slot(DayOfWeek.TUESDAY, 15, 0, 16, 30)));
        assertFalse(base.equals(slot(DayOfWeek.MONDAY, 15, 1, 16, 30)));
        assertFalse(base.equals(slot(DayOfWeek.MONDAY, 15, 0, 16, 31)));
        assertEquals(base.hashCode(), slot(DayOfWeek.MONDAY, 15, 0, 16, 30).hashCode());
    }
}
