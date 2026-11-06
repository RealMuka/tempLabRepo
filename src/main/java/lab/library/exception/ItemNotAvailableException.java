package lab.library.exception;

/** Экземпляр уже выдан читателю и недоступен для операции. */
public class ItemNotAvailableException extends LibraryException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
