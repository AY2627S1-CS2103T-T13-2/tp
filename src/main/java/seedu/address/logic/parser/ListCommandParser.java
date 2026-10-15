package seedu.address.logic.parser;

import java.util.Arrays;
import java.util.Locale;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ListView;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ListCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ListCommand(ListView.STUDENTS);
        }

        String[] arguments = trimmedArgs.split("\\s+");
        if (arguments.length > 1) {
            boolean containsOnlyViews = Arrays.stream(arguments).allMatch(ListCommandParser::isView);
            String message = containsOnlyViews
                    ? ListCommand.MESSAGE_VIEW_GIVEN_TWICE
                    : ListCommand.MESSAGE_INVALID_VIEW;
            throw new ParseException(message);
        }

        return switch (arguments[0].toLowerCase(Locale.ROOT)) {
            case "students" -> new ListCommand(ListView.STUDENTS);
            case "lessons" -> new ListCommand(ListView.LESSONS);
            default -> throw new ParseException(ListCommand.MESSAGE_INVALID_VIEW);
        };
    }

    private static boolean isView(String argument) {
        return argument.equalsIgnoreCase("students") || argument.equalsIgnoreCase("lessons");
    }
}
