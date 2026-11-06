package lab.library.storage;

import lab.library.exception.StorageException;
import lab.library.model.AudioBook;
import lab.library.model.Book;
import lab.library.model.LibraryItem;
import lab.library.model.Loan;
import lab.library.model.Magazine;
import lab.library.model.Reader;
import lab.library.service.LibraryData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Текстовое хранилище. Формат строк (разделитель '|'):
 * ITEM|тип|id|название|автор|год|доступно|extra
 * READER|id|имя|email
 * LOAN|id|itemId|readerId|выдано|срок|возвращено или '-'|штраф
 */
public class FileDataStorage implements DataStorage {
    private static final String SEP = "|";
    private final Path file;

    public FileDataStorage(Path file) {
        this.file = file;
    }

    @Override
    public void save(LibraryData data) throws StorageException {
        try (BufferedWriter out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (LibraryItem i : data.items()) {
                writeLine(out, "ITEM", i.getType(), i.getId(), i.getTitle(), i.getAuthor(), i.getYear(),
                        i.isAvailable(), i.getExtraValue());
            }
            for (Reader r : data.readers()) {
                writeLine(out, "READER", r.getId(), r.getName(), r.getEmail());
            }
            for (Loan l : data.loans()) {
                writeLine(out, "LOAN", l.getId(), l.getItemId(), l.getReaderId(), l.getIssueDate(),
                        l.getDueDate(), l.isReturned() ? l.getReturnDate() : "-",
                        String.format(Locale.US, "%.2f", l.getFine()));
            }
        } catch (IOException e) {
            throw new StorageException("Не удалось записать файл " + file, e);
        }
    }

    @Override
    public LibraryData load() throws StorageException {
        if (!Files.exists(file)) {
            return LibraryData.empty();
        }
        List<LibraryItem> items = new ArrayList<>();
        List<Reader> readers = new ArrayList<>();
        List<Loan> loans = new ArrayList<>();
        int lineNo = 0;
        try (BufferedReader in = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = in.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split("\\|", -1);
                switch (p[0]) {
                    case "ITEM" -> items.add(parseItem(p));
                    case "READER" -> readers.add(new Reader(Integer.parseInt(p[1]), p[2], p[3]));
                    case "LOAN" -> loans.add(parseLoan(p));
                    default -> throw new StorageException("Неизвестный тип записи: " + p[0]);
                }
            }
        } catch (IOException e) {
            throw new StorageException("Не удалось прочитать файл " + file, e);
        } catch (StorageException e) {
            throw new StorageException("Ошибка в строке " + lineNo + ": " + e.getMessage());
        } catch (RuntimeException e) { // NumberFormat, DateTimeParse, InvalidData, ArrayIndexOutOfBounds
            throw new StorageException("Повреждённые данные в строке " + lineNo + ": " + e.getMessage(), e);
        }
        return new LibraryData(items, readers, loans);
    }

    private LibraryItem parseItem(String[] p) throws StorageException {
        int id = Integer.parseInt(p[2]);
        int year = Integer.parseInt(p[5]);
        int extra = Integer.parseInt(p[7]);
        LibraryItem item = switch (p[1]) {
            case "BOOK" -> new Book(id, p[3], p[4], year, extra);
            case "MAGAZINE" -> new Magazine(id, p[3], p[4], year, extra);
            case "AUDIOBOOK" -> new AudioBook(id, p[3], p[4], year, extra);
            default -> throw new StorageException("Неизвестный тип издания: " + p[1]);
        };
        item.setAvailable(Boolean.parseBoolean(p[6]));
        return item;
    }

    private Loan parseLoan(String[] p) {
        LocalDate returned = "-".equals(p[6]) ? null : LocalDate.parse(p[6]);
        return new Loan(Integer.parseInt(p[1]), Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                LocalDate.parse(p[4]), LocalDate.parse(p[5]), returned, Double.parseDouble(p[7]));
    }

    private void writeLine(BufferedWriter out, Object... fields) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(SEP);
            }
            sb.append(fields[i]);
        }
        out.write(sb.toString());
        out.newLine();
    }
}
