package lab.library.ui;

import lab.library.exception.InvalidDataException;
import lab.library.exception.LibraryException;
import lab.library.exception.StorageException;
import lab.library.model.LibraryItem;
import lab.library.model.Loan;
import lab.library.model.Reader;
import lab.library.service.LibraryService;
import lab.library.service.LibraryStatistics;
import lab.library.storage.DataStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.NoSuchElementException;

/** Пользовательский интерфейс: только ввод/вывод, вся логика делегируется сервису. */
public class ConsoleMenu {
    private final LibraryService service;
    private final DataStorage storage;
    private final InputReader in;

    public ConsoleMenu(LibraryService service, DataStorage storage, InputReader in) {
        this.service = service;
        this.storage = storage;
        this.in = in;
    }

    public void run() {
        while (true) {
            printMenu();
            try {
                int choice = in.readInt("Выберите пункт");
                if (choice == 0) {
                    return;
                }
                execute(choice);
            } catch (NoSuchElementException e) {
                return; // конец ввода
            } catch (LibraryException | InvalidDataException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (StorageException e) {
                System.out.println("Ошибка файла: " + e.getMessage());
            }
        }
    }

    private void execute(int choice) throws LibraryException, StorageException {
        switch (choice) {
            case 1 -> System.out.println("Добавлено: " + service.addBook(
                    in.readLine("Название"), in.readLine("Автор"), in.readInt("Год"), in.readInt("Страниц")));
            case 2 -> System.out.println("Добавлено: " + service.addMagazine(
                    in.readLine("Название"), in.readLine("Издатель"), in.readInt("Год"), in.readInt("Номер выпуска")));
            case 3 -> System.out.println("Добавлено: " + service.addAudioBook(
                    in.readLine("Название"), in.readLine("Автор"), in.readInt("Год"), in.readInt("Длительность, мин")));
            case 4 -> {
                service.removeItem(in.readInt("id издания"));
                System.out.println("Издание удалено.");
            }
            case 5 -> printAll(service.getAllItems(), "Каталог пуст.");
            case 6 -> printAll(service.search(in.readLine("Часть названия или автора")), "Ничего не найдено.");
            case 7 -> printAll(service.search(in.readInt("Год издания")), "Ничего не найдено.");
            case 8 -> System.out.println("Зарегистрирован: " + service.registerReader(
                    in.readLine("Имя"), in.readLine("Email")));
            case 9 -> printAll(service.getAllReaders(), "Читателей нет.");
            case 10 -> {
                service.removeReader(in.readInt("id читателя"));
                System.out.println("Читатель удалён.");
            }
            case 11 -> System.out.println("Выдано: " + service.issueItem(
                    in.readInt("id читателя"), in.readInt("id издания"), in.readDate("Дата выдачи")));
            case 12 -> {
                Loan loan = service.returnItem(in.readInt("id издания"), in.readDate("Дата возврата"));
                System.out.println("Возвращено: " + loan);
            }
            case 13 -> printAll(service.getOverdueLoans(in.readDate("На дату")), "Просрочек нет.");
            case 14 -> printStatistics(service.getStatistics(in.readDate("На дату")));
            case 15 -> {
                storage.save(service.snapshot());
                System.out.println("Данные сохранены.");
            }
            default -> System.out.println("Нет такого пункта меню.");
        }
    }

    private <T> void printAll(Collection<T> items, String emptyMessage) {
        if (items.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            items.forEach(System.out::println);
        }
    }

    private void printStatistics(LibraryStatistics s) {
        System.out.println("Всего изданий: " + s.totalItems() + " (в наличии: " + s.availableItems() + ")");
        s.itemsByType().forEach((type, count) -> System.out.println("  " + type + ": " + count));
        System.out.println("Выдач на руках: " + s.activeLoans() + ", просрочено: " + s.overdueLoans());
        System.out.printf("Сумма начисленных штрафов: %.2f%n", s.totalFines());
    }

    private void printMenu() {
        System.out.println("""

                ===== БИБЛИОТЕКА =====
                 1. Добавить книгу            9. Список читателей
                 2. Добавить журнал          10. Удалить читателя
                 3. Добавить аудиокнигу      11. Выдать издание
                 4. Удалить издание          12. Принять возврат
                 5. Каталог                  13. Просроченные выдачи
                 6. Поиск по названию/автору 14. Статистика
                 7. Поиск по году            15. Сохранить данные
                 8. Зарегистрировать читателя 0. Выход""");
    }
}
