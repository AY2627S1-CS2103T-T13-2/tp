package seedu.address.logic.parser;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new MarkCommand object.
 *
 * <p>This is a stub: the parser is registered in {@code AddressBookParser}, but its behaviour belongs to the
 * owner of this feature.
 */
public class MarkCommandParser implements Parser<MarkCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the MarkCommand
     * and returns a MarkCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public MarkCommand parse(String args) throws ParseException {
        return new MarkCommand();
    }

}
