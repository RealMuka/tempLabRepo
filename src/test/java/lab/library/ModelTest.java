package lab.library;

import lab.library.exception.InvalidDataException;
import lab.library.model.AudioBook;
import lab.library.model.Book;
import lab.library.model.LibraryItem;
import lab.library.model.Magazine;
import lab.library.model.Reader;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void polymorphicFineThroughBaseReference() {
        List<LibraryItem> items = List.of(
                new Book(1, "A", "B", 2000, 100),
                new Magazine(2, "A", "B", 2000, 1),
                new AudioBook(3, "A", "B", 2000, 60));
        assertEquals(5.0, items.get(0).calculateFine(10), 1e-9);
        assertEquals(2.0, items.get(1).calculateFine(10), 1e-9);
        assertEquals(10.0, items.get(2).calculateFine(10), 1e-9);
    }

    @Test
    void overloadedFineByDates() {
        Book b = new Book(1, "A", "B", 2000, 100);
        LocalDate due = LocalDate.of(2026, 1, 10);
        assertEquals(0.0, b.calculateFine(due, due));
        assertEquals(0.0, b.calculateFine(due, due.minusDays(3)));
        assertEquals(1.5, b.calculateFine(due, due.plusDays(3)), 1e-9);
    }

    @Test
    void loanPeriodsDiffer() {
        assertEquals(21, new Book(1, "A", "B", 2000, 1).getLoanPeriodDays());
        assertEquals(7, new Magazine(2, "A", "B", 2000, 1).getLoanPeriodDays());
        assertEquals(14, new AudioBook(3, "A", "B", 2000, 1).getLoanPeriodDays());
    }

    @Test
    void invalidValuesRejected() {
        assertThrows(InvalidDataException.class, () -> new Book(1, "", "B", 2000, 100));
        assertThrows(InvalidDataException.class, () -> new Book(1, "A", null, 2000, 100));
        assertThrows(InvalidDataException.class, () -> new Book(1, "A", "B", 1000, 100));
        assertThrows(InvalidDataException.class, () -> new Book(1, "A", "B", 2100, 100));
        assertThrows(InvalidDataException.class, () -> new Book(1, "A", "B", 2000, 0));
        assertThrows(InvalidDataException.class, () -> new Book(0, "A", "B", 2000, 10));
        assertThrows(InvalidDataException.class, () -> new Book(1, "A|B", "B", 2000, 10));
        assertThrows(InvalidDataException.class, () -> new Magazine(1, "A", "B", 2000, -5));
        assertThrows(InvalidDataException.class, () -> new AudioBook(1, "A", "B", 2000, 0));
    }

    @Test
    void readerEmailValidation() {
        assertDoesNotThrow(() -> new Reader(1, "Иван", "ivan@mail.ru"));
        assertThrows(InvalidDataException.class, () -> new Reader(1, "Иван", "bad-email"));
        assertThrows(InvalidDataException.class, () -> new Reader(1, " ", "ivan@mail.ru"));
    }
}
