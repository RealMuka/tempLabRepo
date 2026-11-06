package lab.library.service;

import lab.library.model.LibraryItem;
import lab.library.model.Loan;
import lab.library.model.Reader;

import java.util.List;

/** Снимок всех данных приложения для сохранения/восстановления. */
public record LibraryData(List<LibraryItem> items, List<Reader> readers, List<Loan> loans) {
    public static LibraryData empty() {
        return new LibraryData(List.of(), List.of(), List.of());
    }
}
