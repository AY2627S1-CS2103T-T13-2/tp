package seedu.address.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Stores successful commands and supports shell-style navigation and completion. */
class CommandHistory {
    private final List<String> commands = new ArrayList<>();
    private int cursor;
    private String draft = "";

    void record(String command) {
        if (commands.isEmpty() || !commands.get(commands.size() - 1).equals(command)) {
            commands.add(command);
        }
        cursor = commands.size();
        draft = "";
    }

    String previous(String currentText) {
        if (commands.isEmpty()) {
            return currentText;
        }
        if (cursor == commands.size()) {
            draft = currentText;
        }
        cursor = Math.max(0, cursor - 1);
        return commands.get(cursor);
    }

    String next(String currentText) {
        if (cursor >= commands.size()) {
            return currentText;
        }
        cursor++;
        return cursor == commands.size() ? draft : commands.get(cursor);
    }

    static String completeCommandWord(String input, Collection<String> commandWords) {
        if (input.isBlank() || input.contains(" ")) {
            return input;
        }
        List<String> matches = commandWords.stream().filter(word -> word.startsWith(input.toLowerCase())).toList();
        return matches.size() == 1 ? matches.get(0) + " " : input;
    }
}
