package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    @TempDir
    public Path tempFolder;

    @Test
    public void createIfMissing_fileDoesNotExist_createsFileAndParentDirs() throws IOException {
        Path file = tempFolder.resolve("nested").resolve("dir").resolve("test.txt");
        assertFalse(Files.exists(file));

        FileUtil.createIfMissing(file);
        assertTrue(Files.exists(file));
    }

    @Test
    public void createIfMissing_fileExists_doesNothing() throws IOException {
        Path file = tempFolder.resolve("test.txt");
        Files.writeString(file, "content");
        assertTrue(Files.exists(file));

        FileUtil.createIfMissing(file);
        assertEquals("content", Files.readString(file));
    }

    @Test
    public void createIfMissing_nullFile_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> FileUtil.createIfMissing(null));
    }

    @Test
    public void readFromFile_validFile_returnsContent() throws IOException {
        Path file = tempFolder.resolve("readTest.txt");
        Files.writeString(file, "hello world");

        assertEquals("hello world", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_newFile_createsAndWritesContent() throws IOException {
        Path file = tempFolder.resolve("sub").resolve("newTest.txt");
        FileUtil.writeToFile(file, "atomic write content");

        assertTrue(Files.exists(file));
        assertEquals("atomic write content", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_existingFile_overwritesAtomically() throws IOException {
        Path file = tempFolder.resolve("overwriteTest.txt");
        FileUtil.writeToFile(file, "initial content");
        assertEquals("initial content", FileUtil.readFromFile(file));

        FileUtil.writeToFile(file, "updated content");
        assertEquals("updated content", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(null, "content"));
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFile(tempFolder.resolve("file.txt"), null));
    }

    @Test
    public void backupFile_existingFile_createsBackupCopy() throws IOException {
        Path file = tempFolder.resolve("original.json");
        FileUtil.writeToFile(file, "{\"key\": \"value\"}");

        Optional<Path> backup = FileUtil.backupFile(file, ".bak");
        assertTrue(backup.isPresent());
        assertTrue(Files.exists(backup.get()));
        assertEquals(file.resolveSibling("original.json.bak"), backup.get());
        assertEquals("{\"key\": \"value\"}", FileUtil.readFromFile(backup.get()));
    }

    @Test
    public void backupFile_missingFile_returnsEmpty() throws IOException {
        Path file = tempFolder.resolve("missing.json");
        assertFalse(Files.exists(file));

        Optional<Path> backup = FileUtil.backupFile(file, ".bak");
        assertFalse(backup.isPresent());
    }

    @Test
    public void backupFile_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> FileUtil.backupFile(null, ".bak"));
        assertThrows(NullPointerException.class, () -> FileUtil.backupFile(tempFolder.resolve("file.txt"), null));
    }
}
