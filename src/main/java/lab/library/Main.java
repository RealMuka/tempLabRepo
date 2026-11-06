package lab.library;

import lab.library.exception.StorageException;
import lab.library.service.LibraryService;
import lab.library.storage.DataStorage;
import lab.library.storage.FileDataStorage;
import lab.library.ui.ConsoleMenu;
import lab.library.ui.InputReader;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Path file = Path.of(args.length > 0 ? args[0] : "library-data.txt");
        DataStorage storage = new FileDataStorage(file);
        LibraryService service = new LibraryService();

        try {
            service.restore(storage.load());
            System.out.println("Данные загружены из " + file);
        } catch (StorageException e) {
            System.out.println("Не удалось загрузить данные: " + e.getMessage() + ". Начинаем с пустой библиотеки.");
        }

        try {
            new ConsoleMenu(service, storage, new InputReader(new Scanner(System.in))).run();
        } finally {
            try {
                storage.save(service.snapshot());
                System.out.println("Данные сохранены в " + file);
            } catch (StorageException e) {
                System.out.println("Ошибка сохранения: " + e.getMessage());
            }
        }
    }
}
