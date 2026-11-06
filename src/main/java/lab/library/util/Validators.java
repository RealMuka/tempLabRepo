package lab.library.util;

import lab.library.exception.InvalidDataException;

/** Общие проверки корректности значений (устраняют дублирование в сеттерах). */
public final class Validators {
    private Validators() {
    }

    public static String requireText(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException("Поле «" + field + "» не может быть пустым");
        }
        if (value.contains("|") || value.contains("\n") || value.contains("\r")) {
            throw new InvalidDataException("Поле «" + field + "» содержит недопустимые символы ('|', перевод строки)");
        }
        return value.trim();
    }

    public static int requireRange(String field, int value, int min, int max) {
        if (value < min || value > max) {
            throw new InvalidDataException(
                    "Поле «" + field + "» должно быть в диапазоне " + min + ".." + max + ", получено " + value);
        }
        return value;
    }

    public static int requirePositive(String field, int value) {
        if (value <= 0) {
            throw new InvalidDataException("Поле «" + field + "» должно быть положительным, получено " + value);
        }
        return value;
    }
}
