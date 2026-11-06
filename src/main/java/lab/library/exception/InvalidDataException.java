package lab.library.exception;

/** Некорректное значение поля (нарушение инкапсулированных проверок). */
public class InvalidDataException extends IllegalArgumentException {
    public InvalidDataException(String message) {
        super(message);
    }
}
