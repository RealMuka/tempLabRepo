package lab.library.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Ввод данных с консоли с повторным запросом при ошибках. */
public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();   // NoSuchElementException при конце ввода
    }

    public int readInt(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }

    /** Дата в формате гггг-мм-дд; пустой ввод означает сегодняшнюю дату. */
    public LocalDate readDate(String prompt) {
        while (true) {
            String s = readLine(prompt + " (гггг-мм-дд, Enter = сегодня)");
            if (s.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты.");
            }
        }
    }
}
