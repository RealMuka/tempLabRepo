package lab.library.model;

/** Поведение объекта, который можно выдавать читателю. */
public interface Loanable {
    /** Срок выдачи в днях. */
    int getLoanPeriodDays();

    /** Штраф за указанное число дней просрочки. */
    double calculateFine(int overdueDays);
}
