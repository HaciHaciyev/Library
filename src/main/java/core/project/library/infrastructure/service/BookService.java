package core.project.library.infrastructure.service;

import core.project.library.domain.entities.Book;
import core.project.library.infrastructure.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<Book> findById(UUID bookId) {
        return bookRepository.findById(bookId);
    }

    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public Optional<List<Book>> listOfBooks(Integer pageNumber, Integer pageSize, String category) {
        if (category != null) {
            return bookRepository.listByCategory(pageNumber, pageSize, category);
        } else {
            return bookRepository.listOfBooks(pageNumber, pageSize);
        }
    }
}
