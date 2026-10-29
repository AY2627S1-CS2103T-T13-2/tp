package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.model.ListView;
import seedu.address.model.Model;
import seedu.address.model.SortField;

/** Sorts a student or lesson list without changing stored data. */
public class SortCommand extends Command {
    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = "sort student id|name|level|payment|paid [asc|desc] or "
            + "sort lesson id|subject|level|fee|time [asc|desc]";
    public static final String MESSAGE_SUCCESS = "Sorted %1$s by %2$s in %3$s order.";

    private final String entity;
    private final String fieldName;
    private final SortField field;
    private final boolean ascending;

    /** Creates a command that sorts an entity list by the requested field and direction. */
    public SortCommand(String entity, String fieldName, SortField field, boolean ascending) {
        this.entity = requireNonNull(entity);
        this.fieldName = requireNonNull(fieldName);
        this.field = requireNonNull(field);
        this.ascending = ascending;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.sort(field, ascending);
        model.setListView(entity.equals("student") ? ListView.STUDENTS : ListView.LESSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, entity + "s", fieldName,
                ascending ? "ascending" : "descending"));
    }

    @Override
    public boolean isDataMutating() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof SortCommand command
                && entity.equals(command.entity) && field.equals(command.field) && ascending == command.ascending);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, field, ascending);
    }
}
