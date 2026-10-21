package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ScheduleCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the no-argument {@code schedule} command. */
public class ScheduleCommandParser implements Parser<ScheduleCommand> {

    @Override
    public ScheduleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.trim().isEmpty()) {
            throw new ParseException(ScheduleCommand.MESSAGE_UNKNOWN_PARAMETER);
        }
        return new ScheduleCommand();
    }
}
