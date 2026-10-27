package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {
    @Test
    public void navigation_preservesDraftAndSkipsConsecutiveDuplicate() {
        CommandHistory history = new CommandHistory();
        history.record("list students");
        history.record("list students");
        history.record("mark 1");

        assertEquals("mark 1", history.previous("filter "));
        assertEquals("list students", history.previous("mark 1"));
        assertEquals("mark 1", history.next("list students"));
        assertEquals("filter ", history.next("mark 1"));
    }

    @Test
    public void completion_uniquePrefixCompletes_ambiguousOrArgumentsUnchanged() {
        List<String> words = List.of("add", "edit", "exit", "filter");
        assertEquals("add ", CommandHistory.completeCommandWord("ad", words));
        assertEquals("e", CommandHistory.completeCommandWord("e", words));
        assertEquals("add s/", CommandHistory.completeCommandWord("add s/", words));
    }
}
