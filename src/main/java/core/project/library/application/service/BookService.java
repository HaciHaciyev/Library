package core.project.library.application.service;

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

    public final Optional<Book> findById(UUID bookId) {
        return bookRepository.findById(bookId);
    }

    public final Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public final List<Book> listOfBooks(Integer pageNumber, Integer pageSize,
                                                  String category, String author) {
        if (category != null && author == null) {
            return bookRepository.listByCategory(pageNumber, pageSize, category);
        } else if (category == null && author != null) {
            return bookRepository.listByAuthor(pageNumber, pageSize, author);
        } else {
            return bookRepository.listOfBooks(pageNumber, pageSize);
        }
    }
}
