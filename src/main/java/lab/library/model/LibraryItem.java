package lab.library.model;

import lab.library.util.Validators;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;

/**
 * Абстрактное издание библиотеки: общие данные и поведение для книг, журналов и аудиокниг.
 */
public abstract class LibraryItem implements Identifiable, Loanable {
    private final int id;
    private String title;
    private String author;
    private int year;
    private boolean available = true;

    protected LibraryItem(int id, String title, String author, int year) {
        this.id = Validators.requirePositive("id", id);
        setTitle(title);
        setAuthor(author);
        setYear(year);
    }

    @Override
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Validators.requireText("Название", title);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = Validators.requireText("Автор", author);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = Validators.requireRange("Год издания", year, 1450, Year.now().getValue());
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /** Код типа, используется при сохранении в файл. */
    public abstract String getType();

    /** Название типа для вывода пользователю. */
    public abstract String getDisplayName();

    /** Специфичное для типа числовое значение (страницы, номер выпуска, длительность). */
    public abstract int getExtraValue();

    /** Текстовое описание специфичного поля. */
    public abstract String describeExtra();

    /** Перегрузка: штраф по датам (срок возврата и фактическая дата возврата). */
    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (!returnDate.isAfter(dueDate)) {
            return 0.0;
        }
        return calculateFine((int) ChronoUnit.DAYS.between(dueDate, returnDate));
    }

    @Override
    public String toString() {
        return String.format("[%d] %-10s «%s», %s, %d г., %s | %s",
                id, getDisplayName(), title, author, year, describeExtra(),
                available ? "в наличии" : "выдано");
    }
}
