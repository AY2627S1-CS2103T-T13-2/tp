package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Writes and reads files.
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        requireNonNull(file);
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory.
     */
    public static void createParentDirsOfFile(Path file) throws IOException {
        requireNonNull(file);
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists and reads its contents.
     */
    public static String readFromFile(Path file) throws IOException {
        requireNonNull(file);
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes given string to a file atomically via a temporary file.
     * Will create the file and its parent directories if they do not exist yet.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        requireNonNull(file);
        requireNonNull(content);
        createParentDirsOfFile(file);

        Path parentDir = file.toAbsolutePath().getParent();
        Path tempFile = parentDir != null
                ? Files.createTempFile(parentDir, file.getFileName().toString(), ".tmp")
                : Files.createTempFile(file.getFileName().toString(), ".tmp");

        try {
            Files.write(tempFile, content.getBytes(CHARSET));
            try {
                Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Creates a backup copy of {@code file} with the specified {@code suffix} if the source file exists.
     *
     * @param file the source file to back up.
     * @param suffix the suffix to append to the backup file name.
     * @return an {@code Optional} containing the backup {@code Path}, or empty if {@code file} does not exist.
     * @throws IOException if copying the file fails.
     */
    public static Optional<Path> backupFile(Path file, String suffix) throws IOException {
        requireNonNull(file);
        requireNonNull(suffix);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        createParentDirsOfFile(file);
        Path backupPath = file.resolveSibling(file.getFileName().toString() + suffix);
        Files.copy(file, backupPath, StandardCopyOption.REPLACE_EXISTING);
        return Optional.of(backupPath);
    }

}
