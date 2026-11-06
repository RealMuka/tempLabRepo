package lab.library.model;

import lab.library.util.Validators;

public class Book extends LibraryItem {
    private static final double FINE_PER_DAY = 0.50;
    private int pages;

    public Book(int id, String title, String author, int year, int pages) {
        super(id, title, author, year);
        setPages(pages);
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = Validators.requireRange("Число страниц", pages, 1, 10_000);
    }

    @Override
    public String getType() {
        return "BOOK";
    }

    @Override
    public String getDisplayName() {
        return "Книга";
    }

    @Override
    public int getExtraValue() {
        return pages;
    }

    @Override
    public String describeExtra() {
        return pages + " стр.";
    }

    @Override
    public int getLoanPeriodDays() {
        return 21;
    }

    @Override
    public double calculateFine(int overdueDays) {
        return Math.max(0, overdueDays) * FINE_PER_DAY;
    }
}
