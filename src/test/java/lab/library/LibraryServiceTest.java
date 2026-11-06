package lab.library;

import lab.library.exception.*;
import lab.library.model.Loan;
import lab.library.service.LibraryService;
import lab.library.service.LibraryStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {
    private static final LocalDate D = LocalDate.of(2026, 10, 1);
    private LibraryService service;

    @BeforeEach
    void setUp() {
        service = new LibraryService();
        service.addBook("Война и мир", "Толстой", 1869, 1225);
        service.addMagazine("Наука и жизнь", "Редакция", 2024, 5);
        service.addAudioBook("Мастер и Маргарита", "Булгаков", 1967, 700);
        service.addBook("Идиот", "Достоевский", 1869, 600);
        service.registerReader("Иван", "ivan@mail.ru");
    }

    @Test
    void searchOverloads() {
        assertEquals(1, service.search("толстой").size());
        assertEquals(2, service.search(1869).size());
        assertTrue(service.search("нет такого").isEmpty());
    }

    @Test
    void issueAndReturnWithoutFine() throws LibraryException {
        service.issueItem(1, 1, D);
        assertFalse(service.findItem(1).isAvailable());
        Loan loan = service.returnItem(1, D.plusDays(21));
        assertEquals(0.0, loan.getFine());
        assertTrue(service.findItem(1).isAvailable());
    }

    @Test
    void returnLateCalculatesFine() throws LibraryException {
        service.issueItem(1, 3, D);            // аудиокнига, 14 дней, 1.00 в день
        Loan loan = service.returnItem(3, D.plusDays(14 + 5));
        assertEquals(5.0, loan.getFine(), 1e-9);
    }

    @Test
    void cannotIssueTwice() throws LibraryException {
        service.issueItem(1, 1, D);
        assertThrows(ItemNotAvailableException.class, () -> service.issueItem(1, 1, D));
    }

    @Test
    void loanLimitEnforced() throws LibraryException {
        service.issueItem(1, 1, D);
        service.issueItem(1, 2, D);
        service.issueItem(1, 3, D);
        assertThrows(LoanLimitExceededException.class, () -> service.issueItem(1, 4, D));
    }

    @Test
    void notFoundErrors() {
        assertThrows(NotFoundException.class, () -> service.issueItem(99, 1, D));
        assertThrows(NotFoundException.class, () -> service.issueItem(1, 99, D));
        assertThrows(NotFoundException.class, () -> service.removeItem(99));
        assertThrows(NotFoundException.class, () -> service.removeReader(99));
    }

    @Test
    void returnOfNotIssuedItemFails() {
        assertThrows(LibraryException.class, () -> service.returnItem(1, D));
    }

    @Test
    void returnBeforeIssueDateRejected() throws LibraryException {
        service.issueItem(1, 1, D);
        assertThrows(InvalidDataException.class, () -> service.returnItem(1, D.minusDays(1)));
    }

    @Test
    void cannotRemoveBorrowedItemOrReaderWithLoans() throws LibraryException {
        service.issueItem(1, 1, D);
        assertThrows(ItemNotAvailableException.class, () -> service.removeItem(1));
        assertThrows(LibraryException.class, () -> service.removeReader(1));
        service.returnItem(1, D.plusDays(1));
        assertDoesNotThrow(() -> service.removeItem(1));
        assertDoesNotThrow(() -> service.removeReader(1));
    }

    @Test
    void overdueAndStatistics() throws LibraryException {
        service.issueItem(1, 2, D);                          // журнал, срок 7 дней
        assertTrue(service.getOverdueLoans(D.plusDays(7)).isEmpty());
        assertEquals(1, service.getOverdueLoans(D.plusDays(8)).size());

        LibraryStatistics s = service.getStatistics(D.plusDays(8));
        assertEquals(4, s.totalItems());
        assertEquals(3, s.availableItems());
        assertEquals(1, s.activeLoans());
        assertEquals(1, s.overdueLoans());
        assertEquals(2L, s.itemsByType().get("Книга"));
    }
}
