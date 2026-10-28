package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.SortField;

/** Parses sort commands. */
public class SortCommandParser implements Parser<SortCommand> {
    @Override
    public SortCommand parse(String args) throws ParseException {
        String[] parts = args.trim().split("\\s+");
        if (parts.length < 2 || parts.length > 3) {
            throw invalidFormat();
        }
        String entity = parts[0].toLowerCase();
        String fieldName = parts[1].toLowerCase();
        boolean ascending = parts.length == 2 || parts[2].equalsIgnoreCase("asc");
        if (parts.length == 3 && !parts[2].equalsIgnoreCase("asc") && !parts[2].equalsIgnoreCase("desc")) {
            throw invalidFormat();
        }
        if (entity.equals("student") && (fieldName.equals("paid") || fieldName.equals("unpaid"))) {
            fieldName = "payment";
        }
        SortField field;
        try {
            field = SortField.valueOf((entity + "_" + fieldName).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw invalidFormat();
        }
        return new SortCommand(entity, fieldName, field, ascending);
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
