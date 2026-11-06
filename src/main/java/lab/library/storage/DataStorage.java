package lab.library.storage;

import lab.library.exception.StorageException;
import lab.library.service.LibraryData;

/** Абстракция хранилища: позволяет заменить файл на БД без изменения бизнес-логики. */
public interface DataStorage {
    void save(LibraryData data) throws StorageException;

    LibraryData load() throws StorageException;
}
