package core.project.library.infrastructure.repositories;

import core.project.library.domain.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    void getBookById() {
        Optional<Book> optional = bookRepository.getBookById("d4f0aa27-317b-4e00-9462-9a7f0faa7a5e");

        assertThat(optional.orElseThrow()).isNotNull();
    }
}