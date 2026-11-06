package lab.library.service;

import lab.library.exception.ItemNotAvailableException;
import lab.library.exception.LibraryException;
import lab.library.exception.LoanLimitExceededException;
import lab.library.exception.NotFoundException;
import lab.library.exception.InvalidDataException;
import lab.library.model.AudioBook;
import lab.library.model.Book;
import lab.library.model.LibraryItem;
import lab.library.model.Loan;
import lab.library.model.Magazine;
import lab.library.model.Reader;
import lab.library.repository.Repository;
import lab.library.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/** Бизнес-логика библиотеки. Не зависит ни от консоли, ни от файлов. */
public class LibraryService {
    public static final int MAX_LOANS_PER_READER = 3;

    private final Repository<LibraryItem> items = new Repository<>();
    private final Repository<Reader> readers = new Repository<>();
    private final Repository<Loan> loans = new Repository<>();

    // ---------- издания ----------

    public Book addBook(String title, String author, int year, int pages) {
        Book book = new Book(items.nextId(), title, author, year, pages);
        items.add(book);
        return book;
    }

    public Magazine addMagazine(String title, String publisher, int year, int issueNumber) {
        Magazine magazine = new Magazine(items.nextId(), title, publisher, year, issueNumber);
        items.add(magazine);
        return magazine;
    }

    public AudioBook addAudioBook(String title, String author, int year, int minutes) {
        AudioBook audioBook = new AudioBook(items.nextId(), title, author, year, minutes);
        items.add(audioBook);
        return audioBook;
    }

    public LibraryItem findItem(int id) throws NotFoundException {
        return items.findById(id).orElseThrow(() -> new NotFoundException("Издание", id));
    }

    public void removeItem(int id) throws LibraryException {
        LibraryItem item = findItem(id);
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("Нельзя удалить издание id=" + id + ": оно сейчас на руках");
        }
        items.remove(id);
    }

    public List<LibraryItem> getAllItems() {
        return items.findAll();
    }

    /** Перегрузка 1: поиск по подстроке в названии или авторе. */
    public List<LibraryItem> search(String query) {
        String q = query.toLowerCase(Locale.ROOT).trim();
        return items.find(i -> i.getTitle().toLowerCase(Locale.ROOT).contains(q)
                || i.getAuthor().toLowerCase(Locale.ROOT).contains(q));
    }

    /** Перегрузка 2: поиск по году издания. */
    public List<LibraryItem> search(int year) {
        return items.find(i -> i.getYear() == year);
    }

    // ---------- читатели ----------

    public Reader registerReader(String name, String email) {
        Reader reader = new Reader(readers.nextId(), name, email);
        readers.add(reader);
        return reader;
    }

    public Reader findReader(int id) throws NotFoundException {
        return readers.findById(id).orElseThrow(() -> new NotFoundException("Читатель", id));
    }

    public void removeReader(int id) throws LibraryException {
        Reader reader = findReader(id);
        if (countActiveLoans(id) > 0) {
            throw new LibraryException("Нельзя удалить читателя «" + reader.getName() + "»: у него есть невозвращённые издания");
        }
        readers.remove(id);
    }

    public List<Reader> getAllReaders() {
        return readers.findAll();
    }

    // ---------- выдача и возврат ----------

    public Loan issueItem(int readerId, int itemId, LocalDate issueDate) throws LibraryException {
        Reader reader = findReader(readerId);
        LibraryItem item = findItem(itemId);
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("Издание id=" + itemId + " уже выдано");
        }
        if (countActiveLoans(readerId) >= MAX_LOANS_PER_READER) {
            throw new LoanLimitExceededException(reader.getName(), MAX_LOANS_PER_READER);
        }
        Loan loan = new Loan(loans.nextId(), itemId, readerId, issueDate,
                issueDate.plusDays(item.getLoanPeriodDays()));
        loans.add(loan);
        item.setAvailable(false);
        return loan;
    }

    /** Возврат издания. Штраф рассчитывается полиморфно через ссылку на LibraryItem. */
    public Loan returnItem(int itemId, LocalDate returnDate) throws LibraryException {
        LibraryItem item = findItem(itemId);
        Loan loan = loans.find(l -> l.getItemId() == itemId && !l.isReturned()).stream().findFirst()
                .orElseThrow(() -> new LibraryException("Издание id=" + itemId + " не числится выданным"));
        if (returnDate.isBefore(loan.getIssueDate())) {
            throw new InvalidDataException("Дата возврата не может быть раньше даты выдачи (" + loan.getIssueDate() + ")");
        }
        loan.close(returnDate, item.calculateFine(loan.getDueDate(), returnDate));
        item.setAvailable(true);
        return loan;
    }

    public List<Loan> getAllLoans() {
        return loans.findAll();
    }

    public List<Loan> getActiveLoans() {
        return loans.find(l -> !l.isReturned());
    }

    public List<Loan> getOverdueLoans(LocalDate today) {
        return loans.find(l -> l.isOverdue(today));
    }

    private long countActiveLoans(int readerId) {
        return loans.find(l -> l.getReaderId() == readerId && !l.isReturned()).size();
    }

    // ---------- обработка данных ----------

    public LibraryStatistics getStatistics(LocalDate today) {
        List<LibraryItem> all = items.findAll();
        double fines = loans.findAll().stream().mapToDouble(Loan::getFine).sum();
        return new LibraryStatistics(
                all.size(),
                CollectionUtils.countBy(all, LibraryItem::getDisplayName),
                all.stream().filter(LibraryItem::isAvailable).count(),
                getActiveLoans().size(),
                getOverdueLoans(today).size(),
                fines);
    }

    // ---------- снимок данных ----------

    public LibraryData snapshot() {
        return new LibraryData(items.findAll(), readers.findAll(), loans.findAll());
    }

    public void restore(LibraryData data) {
        items.replaceAll(data.items());
        readers.replaceAll(data.readers());
        loans.replaceAll(data.loans());
    }
}
