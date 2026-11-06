package lab.library.exception;

/** Объект с указанным идентификатором не найден. */
public class NotFoundException extends LibraryException {
    public NotFoundException(String entity, int id) {
        super(entity + " с id=" + id + " не найден(а)");
    }
}
