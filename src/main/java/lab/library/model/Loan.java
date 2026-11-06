package lab.library.model;

import lab.library.exception.InvalidDataException;
import lab.library.util.Validators;

import java.time.LocalDate;

/** Факт выдачи издания читателю. */
public class Loan implements Identifiable {
    private final int id;
    private final int itemId;
    private final int readerId;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;

    public Loan(int id, int itemId, int readerId, LocalDate issueDate, LocalDate dueDate,
                LocalDate returnDate, double fine) {
        this.id = Validators.requirePositive("id", id);
        this.itemId = itemId;
        this.readerId = readerId;
        if (issueDate == null || dueDate == null || dueDate.isBefore(issueDate)) {
            throw new InvalidDataException("Некорректные даты выдачи");
        }
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    public Loan(int id, int itemId, int readerId, LocalDate issueDate, LocalDate dueDate) {
        this(id, itemId, readerId, issueDate, dueDate, null, 0.0);
    }

    @Override
    public int getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
    }

    public int getReaderId() {
        return readerId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getFine() {
        return fine;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueDate);
    }

    public void close(LocalDate returnDate, double fine) {
        if (isReturned()) {
            throw new IllegalStateException("Выдача уже закрыта");
        }
        if (returnDate.isBefore(issueDate)) {
            throw new InvalidDataException("Дата возврата раньше даты выдачи");
        }
        this.returnDate = returnDate;
        this.fine = fine;
    }

    @Override
    public String toString() {
        return String.format("[%d] издание=%d, читатель=%d, выдано %s, вернуть до %s, %s",
                id, itemId, readerId, issueDate, dueDate,
                isReturned() ? "возвращено " + returnDate + ", штраф " + String.format("%.2f", fine) : "на руках");
    }
}
