package lab.library;

import lab.library.exception.LibraryException;
import lab.library.exception.StorageException;
import lab.library.service.LibraryData;
import lab.library.service.LibraryService;
import lab.library.storage.FileDataStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FileDataStorageTest {

    @Test
    void saveAndRestoreRoundTrip(@TempDir Path dir) throws Exception {
        FileDataStorage storage = new FileDataStorage(dir.resolve("data.txt"));
        LibraryService s1 = new LibraryService();
        s1.addBook("Война и мир", "Толстой", 1869, 1225);
        s1.addMagazine("Наука", "Редакция", 2024, 5);
        s1.registerReader("Иван", "ivan@mail.ru");
        s1.issueItem(1, 2, LocalDate.of(2026, 10, 1));
        s1.returnItem(2, LocalDate.of(2026, 10, 12));      // с просрочкой
        s1.issueItem(1, 1, LocalDate.of(2026, 10, 2));
        storage.save(s1.snapshot());

        LibraryService s2 = new LibraryService();
        s2.restore(storage.load());
        assertEquals(2, s2.getAllItems().size());
        assertEquals(1, s2.getAllReaders().size());
        assertEquals(2, s2.getAllLoans().size());
        assertFalse(s2.findItem(1).isAvailable());
        assertEquals(s1.getAllLoans().get(0).getFine(), s2.getAllLoans().get(0).getFine(), 1e-9);
        assertEquals("Война и мир", s2.findItem(1).getTitle());
    }

    @Test
    void missingFileGivesEmptyData(@TempDir Path dir) throws StorageException {
        LibraryData data = new FileDataStorage(dir.resolve("none.txt")).load();
        assertTrue(data.items().isEmpty());
    }

    @Test
    void corruptedFileReportsStorageException(@TempDir Path dir) throws IOException {
        Path f = dir.resolve("bad.txt");
        Files.writeString(f, "ITEM|BOOK|1|A|B|не-число|true|10\n");
        assertThrows(StorageException.class, () -> new FileDataStorage(f).load());

        Files.writeString(f, "UNKNOWN|1\n");
        assertThrows(StorageException.class, () -> new FileDataStorage(f).load());

        Files.writeString(f, "ITEM|BOOK|1|A|B|2000|true\n");     // не хватает поля
        assertThrows(StorageException.class, () -> new FileDataStorage(f).load());
    }
}
