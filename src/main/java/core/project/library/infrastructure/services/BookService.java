package core.project.library.infrastructure.services;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Order;
import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.AuthorRepository;
import core.project.library.infrastructure.repositories.BookRepository;
import core.project.library.infrastructure.repositories.OrderRepository;
import core.project.library.infrastructure.repositories.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    private final OrderRepository orderRepository;

    public Optional<Book> getBookById(String bookId) {
        return entityCollectorForBook(
                bookRepository.getBookById(bookId).orElseThrow(NotFoundException::new),
                publisherRepository.getPublisherByBookId(bookId).orElseThrow(NotFoundException::new),
                authorRepository.getAuthorsByBookId(bookId),
                orderRepository.getOrderByBookId(bookId)
        );
    }

    private static Optional<Book> entityCollectorForBook(
            Book book, Publisher publisher,
            List<Optional<Author>> authors, List<Optional<Order>> orders) {

        Set<Author> authorSet = new HashSet<>();
        Set<Order> orderSet = new HashSet<>();
        authors.forEach(author -> authorSet.add(author.orElseThrow(NotFoundException::new)));
        orders.forEach(order -> orderSet.add(order.orElseThrow(NotFoundException::new)));

        return Optional.ofNullable(Book.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .quantityOnHand(book.getQuantityOnHand())
                .category(book.getCategory())
                .events(book.getEvents())
                .publisher(publisher)
                .authors(authorSet)
                .orders(orderSet)
                .build());
    }
}
