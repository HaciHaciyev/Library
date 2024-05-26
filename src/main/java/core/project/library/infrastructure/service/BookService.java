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

    public final Optional<Book> findById(UUID bookId) {
        return bookRepository.findById(bookId);
    }

    public final Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public final Optional<List<Book>> listOfBooks(Integer pageNumber, Integer pageSize,
                                                  String category, String authorLastName) {
        if (category != null && authorLastName == null) {
            return bookRepository.listByCategory(pageNumber, pageSize, category);
        } else if (category == null && authorLastName != null) {
            return bookRepository.listByAuthorLastName(pageNumber, pageSize, authorLastName);
        } else if (category != null) {
            return bookRepository.listByCategoryAndLastName(pageNumber, pageSize, category, authorLastName);
        }else {
            return bookRepository.listOfBooks(pageNumber, pageSize);
        }
    }
}
