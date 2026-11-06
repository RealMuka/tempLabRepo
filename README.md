# Библиотека — консольное приложение на Java

Лабораторная работа: ООП, коллекции, generics, исключения, файловый ввод-вывод.
Предметная область: учёт изданий (книги, журналы, аудиокниги), читателей, выдач и штрафов за просрочку.

## Запуск

Требуется JDK 17+.

```bash
mvn test                                   # тесты
mvn package                                # сборка target/library-app-1.0.jar
java -jar target/library-app-1.0.jar       # запуск (данные в library-data.txt)
java -jar target/library-app-1.0.jar my.txt   # другой файл данных
```

Без Maven: `javac -encoding UTF-8 -d out $(find src/main -name "*.java")` и `java -cp out lab.library.Main`.
В Windows для корректной кириллицы: `chcp 65001`.

## Структура

| Пакет | Ответственность |
|---|---|
| `model` | предметная область: `LibraryItem` (абстрактный) → `Book`, `Magazine`, `AudioBook`; `Reader`, `Loan`; интерфейсы `Identifiable`, `Loanable` |
| `exception` | собственные исключения: `LibraryException`, `NotFoundException`, `ItemNotAvailableException`, `LoanLimitExceededException`, `InvalidDataException`, `StorageException` |
| `repository` | обобщённый `Repository<T extends Identifiable>` |
| `service` | бизнес-логика `LibraryService`, `LibraryStatistics`, `LibraryData` |
| `storage` | интерфейс `DataStorage`, файловая реализация `FileDataStorage` |
| `ui` | `ConsoleMenu`, `InputReader` |
| `util` | `Validators`, обобщённый метод `CollectionUtils.countBy` |

## Соответствие требованиям

1. Объектная модель — `model` (+ `Loan` связывает `Reader` и `LibraryItem`).
2. Инкапсуляция — закрытые поля, сеттеры с проверками (`Validators`).
3. Наследование — `Book`, `Magazine`, `AudioBook` наследуют `LibraryItem`.
4. Абстракция — `abstract class LibraryItem`.
5. Полиморфизм — перегрузка: `calculateFine(int)` / `calculateFine(LocalDate, LocalDate)`, `search(String)` / `search(int)`; переопределение: `calculateFine`, `getLoanPeriodDays`, `describeExtra` вызываются через ссылку `LibraryItem`.
6. Интерфейсы — `Identifiable`, `Loanable`, `DataStorage`.
7. Коллекции — `List`, `Map`, `LinkedHashMap`, `Optional`, `Predicate`.
8. Generics — `Repository<T>`, `CollectionUtils.countBy<T,K>`.
9. Исключения — try/catch/finally/throw/throws, собственные классы (правила: лимит 3 издания, нельзя выдать выданное, нельзя удалить выданное).
10. Файл — `FileDataStorage` (UTF-8, `library-data.txt`), автозагрузка при старте и автосохранение в `finally`.
11. Обработка данных — расчёт штрафов, просроченные выдачи, статистика.
12. Меню — `ConsoleMenu`.
13–14. Слои разделены; сервис не знает про консоль и файлы (SOLID: SRP, DIP через `DataStorage`).
15. Тесты — `src/test` (JUnit 5, 21 тест).

## Тест-план (ручной)

| Сценарий | Ожидание |
|---|---|
| Добавить книгу с корректными данными | издание появилось в каталоге |
| Название пустое / содержит `\|` / год 1000 / страниц 0 | сообщение об ошибке, объект не создан |
| Нечисловой ввод в числовое поле | повторный запрос |
| Email без `@` | ошибка |
| Выдать издание несуществующему читателю / несуществующее | «не найден(а)» |
| Выдать уже выданное издание | `ItemNotAvailableException` |
| Выдать 4-е издание одному читателю | `LoanLimitExceededException` |
| Вернуть позже срока | начислен штраф по тарифу типа |
| Удалить выданное издание / читателя с долгами | отказ |
| Выход и повторный запуск | данные восстановлены из файла |
| Повреждённый файл данных | сообщение с номером строки, старт с пустой библиотекой |

## Git / GitHub

```bash
git init
git branch -M main
git add .gitignore pom.xml README.md
git commit -m "chore: project skeleton, pom and README"
git add src/main/java/lab/library/exception src/main/java/lab/library/util
git commit -m "feat: custom exceptions and validators"
git add src/main/java/lab/library/model src/main/java/lab/library/repository
git commit -m "feat: domain model (abstract LibraryItem, Book, Magazine, AudioBook) and generic Repository"
git add src/main/java/lab/library/service
git commit -m "feat: library service (issue/return, fines, statistics)"
git add src/main/java/lab/library/storage
git commit -m "feat: file storage"
git add src/main/java/lab/library/ui src/main/java/lab/library/Main.java
git commit -m "feat: console menu and entry point"
git add src/test
git commit -m "test: unit tests for model, repository, service, storage"

# создайте пустой репозиторий на github.com, затем:
git remote add origin https://github.com/<ваш-логин>/library-app.git
git push -u origin main
```
