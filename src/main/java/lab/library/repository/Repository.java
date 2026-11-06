package lab.library.repository;

import lab.library.exception.InvalidDataException;
import lab.library.model.Identifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Собственный обобщённый класс: хранилище объектов с уникальными идентификаторами.
 */
public class Repository<T extends Identifiable> {
    private final Map<Integer, T> storage = new LinkedHashMap<>();

    public void add(T entity) {
        if (storage.containsKey(entity.getId())) {
            throw new InvalidDataException("Объект с id=" + entity.getId() + " уже существует");
        }
        storage.put(entity.getId(), entity);
    }

    public boolean remove(int id) {
        return storage.remove(id) != null;
    }

    public Optional<T> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<T> find(Predicate<? super T> condition) {
        List<T> result = new ArrayList<>();
        for (T entity : storage.values()) {
            if (condition.test(entity)) {
                result.add(entity);
            }
        }
        return result;
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public int nextId() {
        return storage.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    public int size() {
        return storage.size();
    }

    public void replaceAll(Collection<? extends T> entities) {
        storage.clear();
        entities.forEach(this::add);
    }
}
