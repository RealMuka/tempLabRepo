package lab.library.exception;

/** Читатель превысил допустимое число одновременно взятых изданий. */
public class LoanLimitExceededException extends LibraryException {
    public LoanLimitExceededException(String readerName, int limit) {
        super("Читатель «" + readerName + "» уже взял максимум изданий (" + limit + ")");
    }
}
