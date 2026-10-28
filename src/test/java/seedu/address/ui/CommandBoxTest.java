package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommandBoxTest {

    @Test
    public void isBlankCommand_emptyOrWhitespace_returnsTrue() {
        assertTrue(CommandBox.isBlankCommand(""));
        assertTrue(CommandBox.isBlankCommand("   \t"));
    }

    @Test
    public void isBlankCommand_visibleCommand_returnsFalse() {
        assertFalse(CommandBox.isBlankCommand(" list students "));
    }
}
