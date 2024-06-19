package core.project.library.application.service;

import core.project.library.domain.entities.Book;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.AuthorRepository;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final PublisherRepository publisherRepository;

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

    public boolean isIsbnExists(ISBN isbn) {
        return bookRepository.isIsbnExists(isbn);
    }

    public void patchBook(UUID bookId, String description,
                          Double price, Integer quantityOnHand) {

        bookRepository.findById(bookId).ifPresentOrElse(foundBook -> {
            if (StringUtils.hasText(description)) {
                foundBook.changeDescription(new Description(description));
            }
            if (price != null) {
                foundBook.changePrice(price);
            }
            if (quantityOnHand != null) {
                foundBook.changeQuantityOnHand(quantityOnHand);
            }
            bookRepository.updateBook(foundBook);
        }, () -> {
            throw new NotFoundException("Book was`t found for patch");
        });
    }
}
