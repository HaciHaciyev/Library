package core.project.library.application.service;

import core.project.library.domain.entities.Book;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.exceptions.Result;
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

    public boolean isIsbnExists(ISBN isbn) {
        return bookRepository.isbnExists(isbn);
    }

    public final Optional<Book> findById(UUID bookId) {
        return bookRepository.findById(bookId);
    }

    public final Optional<Book> findByISBN(ISBN isbn) {
        return bookRepository.findByISBN(isbn);
    }

    public final Result<List<Book>, NotFoundException> listOfBooks(
            Integer pageNumber, Integer pageSize, String category, String author
    ) {
        return bookRepository.listOfBooks(pageNumber, pageSize);
    }

    public void completelySaveBook(Book book) {
        bookRepository.completelySaveBook(book);
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
            bookRepository.patchBook(foundBook);
        }, () -> {
            throw new NotFoundException("Book was`t found for patch");
        });
    }
}
