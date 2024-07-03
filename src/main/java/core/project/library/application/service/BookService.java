package core.project.library.application.service;

import core.project.library.domain.entities.Book;
import core.project.library.domain.value_objects.Description;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.domain.value_objects.Price;
import core.project.library.domain.value_objects.QuantityOnHand;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repository.AuthorRepository;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.repository.PublisherRepository;
import core.project.library.infrastructure.utilities.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
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

    public final Result<Book, NotFoundException> findById(UUID bookId) {
        return bookRepository.findById(bookId);
    }

    public final Result<Book, NotFoundException> findByISBN(ISBN isbn) {
        return bookRepository.findByISBN(isbn);
    }

    public final Result<List<Book>, NotFoundException> listOfBooks(
            Integer pageNumber, Integer pageSize, String title, String category
    ) {
        return bookRepository.listOfBooks(pageNumber, pageSize, title, category);
    }

    public void completelySaveBook(Book book) {
        bookRepository.completelySaveBook(book);
    }

    public void patchBook(UUID bookId, Description description,
                          Price price, QuantityOnHand quantityOnHand) {
        bookRepository.findById(bookId).mapSuccess(foundBook ->  {
            if (StringUtils.hasText(description.description())) {
                foundBook.changeDescription(description.description());
            }
            if (price != null) {
                foundBook.changePrice(price.price());
            }
            if (quantityOnHand != null) {
                foundBook.changeQuantityOnHand(quantityOnHand.quantityOnHand());
            }
            bookRepository.patchBook(foundBook);
            return foundBook;
        });
    }
}
