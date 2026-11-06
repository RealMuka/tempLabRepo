package lab.library.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/** Собственные обобщённые методы для работы с коллекциями. */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    /** Группирует элементы по ключу и считает количество в каждой группе. */
    public static <T, K> Map<K, Long> countBy(Collection<? extends T> items, Function<? super T, ? extends K> keyFn) {
        Map<K, Long> result = new LinkedHashMap<>();
        for (T item : items) {
            result.merge(keyFn.apply(item), 1L, Long::sum);
        }
        return result;
    }
}
