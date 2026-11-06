package lab.library.model;

import lab.library.util.Validators;

public class Magazine extends LibraryItem {
    private static final double FINE_PER_DAY = 0.20;
    private int issueNumber;

    public Magazine(int id, String title, String author, int year, int issueNumber) {
        super(id, title, author, year);
        setIssueNumber(issueNumber);
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber = Validators.requireRange("Номер выпуска", issueNumber, 1, 1_000);
    }

    @Override
    public String getType() {
        return "MAGAZINE";
    }

    @Override
    public String getDisplayName() {
        return "Журнал";
    }

    @Override
    public int getExtraValue() {
        return issueNumber;
    }

    @Override
    public String describeExtra() {
        return "выпуск №" + issueNumber;
    }

    @Override
    public int getLoanPeriodDays() {
        return 7;
    }

    @Override
    public double calculateFine(int overdueDays) {
        return Math.max(0, overdueDays) * FINE_PER_DAY;
    }
}
