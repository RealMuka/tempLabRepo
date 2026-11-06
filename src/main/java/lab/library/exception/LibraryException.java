package lab.library.exception;

/** Базовое проверяемое исключение для нарушений правил предметной области. */
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
}
