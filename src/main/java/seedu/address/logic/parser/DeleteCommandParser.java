package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonId;
import seedu.address.model.student.StudentId;

/** Parses input arguments and creates a {@link DeleteCommand}. */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    private static final Pattern KIND_AND_ID_PATTERN =
            Pattern.compile("(student|lesson)\\s+([0-9]+)", Pattern.CASE_INSENSITIVE);
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    /**
     * Parses the given arguments and returns a DeleteCommand.
     *
     * @throws ParseException if the input does not match the required format
     */
    public DeleteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = KIND_AND_ID_PATTERN.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        int id;
        try {
            id = Integer.parseInt(matcher.group(2));
        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INVALID_FORMAT, e);
        }
        if (id <= 0) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        if (matcher.group(1).equalsIgnoreCase("student")) {
            return new DeleteCommand(new StudentId(id));
        }
        return new DeleteCommand(new LessonId(id));
    }
}
