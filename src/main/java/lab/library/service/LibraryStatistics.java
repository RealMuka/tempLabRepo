package lab.library.service;

import java.util.Map;

/** Результат обработки данных: сводная статистика библиотеки. */
public record LibraryStatistics(int totalItems, Map<String, Long> itemsByType, long availableItems,
                                long activeLoans, long overdueLoans, double totalFines) {
}
