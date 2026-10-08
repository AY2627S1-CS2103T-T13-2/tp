package seedu.address.model.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LevelTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Level(null));
    }

    @Test
    public void constructor_invalidLevel_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Level(""));
    }

    @Test
    public void isValidLevel() {
        // null level
        assertThrows(NullPointerException.class, () -> Level.isValidLevel(null));

        // invalid levels
        assertFalse(Level.isValidLevel("")); // empty string
        assertFalse(Level.isValidLevel("   ")); // spaces only
        assertFalse(Level.isValidLevel("---")); // no letter or number
        assertFalse(Level.isValidLevel("P6!")); // symbol
        assertFalse(Level.isValidLevel("Primary_6")); // underscore
        assertFalse(Level.isValidLevel("a".repeat(31))); // too long

        // valid levels
        assertTrue(Level.isValidLevel("P3"));
        assertTrue(Level.isValidLevel("Primary 3"));
        assertTrue(Level.isValidLevel("Sec 2-A"));
        assertTrue(Level.isValidLevel("1")); // a single number
        assertTrue(Level.isValidLevel("a".repeat(30))); // longest allowed
        assertTrue(Level.isValidLevel("  Primary 3  ")); // surrounding spaces are ignored
    }

    @Test
    public void isValidLevel_lengthIsMeasuredAfterSpacesAreCollapsed() {
        String thirtyCharactersAfterCollapsing = "a".repeat(15) + "     " + "b".repeat(14);
        assertTrue(Level.isValidLevel(thirtyCharactersAfterCollapsing));
    }

    @Test
    public void constructor_normalisesSpaces() {
        assertEquals("Primary 3", new Level("  Primary    3 ").toString());
    }

    @Test
    public void equals() {
        Level level = new Level("Primary 3");

        assertTrue(level.equals(level)); // same object
        assertTrue(level.equals(new Level("Primary 3"))); // same value
        assertTrue(level.equals(new Level("primary   3"))); // ignores case and extra spaces
        assertTrue(level.equals(new Level("PRIMARY 3")));

        assertFalse(level.equals(null));
        assertFalse(level.equals(5.0f)); // different type
        assertFalse(level.equals(new Level("P3"))); // an abbreviation is a different level
        assertFalse(level.equals(new Level("Primary 4")));
    }

    @Test
    public void hashCode_equalLevelsHaveEqualHashCodes() {
        assertEquals(new Level("Primary 3").hashCode(), new Level("primary   3").hashCode());
        assertNotEquals(new Level("Primary 3").hashCode(), new Level("Primary 4").hashCode());
    }
}
