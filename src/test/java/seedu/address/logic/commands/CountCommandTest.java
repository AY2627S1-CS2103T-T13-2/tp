package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class CountCommandTest {

    @Test
    public void execute_typicalAddressBook_returnsPersonCount() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        String expectedMessage = String.format(CountCommand.MESSAGE_SUCCESS,
                model.getAddressBook().getPersonList().size());

        assertCommandSuccess(new CountCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emptyAddressBook_returnsZero() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        String expectedMessage = String.format(CountCommand.MESSAGE_SUCCESS, 0);

        assertCommandSuccess(new CountCommand(), model, expectedMessage, expectedModel);
    }
}
