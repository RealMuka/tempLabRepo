package lab.library;

import lab.library.exception.InvalidDataException;
import lab.library.model.Book;
import lab.library.repository.Repository;
import lab.library.util.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void addFindRemove() {
        Repository<Book> repo = new Repository<>();
        repo.add(new Book(1, "A", "X", 2000, 10));
        repo.add(new Book(2, "B", "Y", 2001, 20));
        assertEquals(2, repo.size());
        assertTrue(repo.findById(1).isPresent());
        assertTrue(repo.findById(99).isEmpty());
        assertEquals(1, repo.find(b -> b.getYear() == 2001).size());
        assertTrue(repo.remove(1));
        assertFalse(repo.remove(1));
        assertEquals(3, repo.nextId());
    }

    @Test
    void duplicateIdRejected() {
        Repository<Book> repo = new Repository<>();
        repo.add(new Book(1, "A", "X", 2000, 10));
        assertThrows(InvalidDataException.class, () -> repo.add(new Book(1, "B", "Y", 2000, 10)));
    }

    @Test
    void genericCountBy() {
        Map<Integer, Long> result = CollectionUtils.countBy(List.of("a", "bb", "cc", "d"), String::length);
        assertEquals(2L, result.get(1));
        assertEquals(2L, result.get(2));
    }
}
