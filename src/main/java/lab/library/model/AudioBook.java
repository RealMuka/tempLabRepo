package lab.library.model;

import lab.library.util.Validators;

public class AudioBook extends LibraryItem {
    private static final double FINE_PER_DAY = 1.00;
    private int durationMinutes;

    public AudioBook(int id, String title, String author, int year, int durationMinutes) {
        super(id, title, author, year);
        setDurationMinutes(durationMinutes);
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = Validators.requireRange("Длительность (мин)", durationMinutes, 1, 10_000);
    }

    @Override
    public String getType() {
        return "AUDIOBOOK";
    }

    @Override
    public String getDisplayName() {
        return "Аудиокнига";
    }

    @Override
    public int getExtraValue() {
        return durationMinutes;
    }

    @Override
    public String describeExtra() {
        return durationMinutes + " мин.";
    }

    @Override
    public int getLoanPeriodDays() {
        return 14;
    }

    @Override
    public double calculateFine(int overdueDays) {
        return Math.max(0, overdueDays) * FINE_PER_DAY;
    }
}
