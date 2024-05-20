package core.project.library.infrastructure.services;

import core.project.library.domain.entities.*;
import core.project.library.infrastructure.data_transfer.BookDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final BookRepository bookRepository;

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    public Optional<Order> getOrderById(UUID orderId) {
        Order order = orderRepository.getOrderById(orderId).orElseThrow(NotFoundException::new);
        List<BookDTO> bookDTOS = bookRepository.getBooksByOrderId(orderId);
        Customer customer = customerRepository.getCustomerById(orderRepository.getCustomerId(orderId)).orElseThrow(NotFoundException::new);

        List<Book> bookList = new ArrayList<>();
        for (BookDTO bookDTO : bookDTOS) {
            UUID publisherId = bookRepository.getPublisherId(bookDTO.id()).orElseThrow();
            Publisher publisher = publisherRepository.getPublisherById(publisherId).orElseThrow();
            List<Author> authorList = authorRepository.getAuthorsByBookId(bookDTO.id());

            Book book = Book.builder()
                    .id(bookDTO.id())
                    .title(bookDTO.title())
                    .description(bookDTO.description())
                    .isbn(bookDTO.isbn())
                    .price(bookDTO.price())
                    .quantityOnHand(bookDTO.quantityOnHand())
                    .category(bookDTO.category())
                    .events(bookDTO.events())
                    .category(bookDTO.category())
                    .publisher(publisher)
                    .build();
            book.addOrder(order);
            authorList.forEach(book::addAuthor);

            bookList.add(book);
        }

        return entityCollectorForOrder(order, customer, bookList);
    }

    private static Optional<Order> entityCollectorForOrder(Order order, Customer customer, List<Book> bookList) {
        Order resultOrder = Order.builder()
                .id(order.getId())
                .countOfBooks(order.getCountOfBooks())
                .totalPrice(order.getTotalPrice())
                .events(order.getEvents())
                .build();

        customer.addOrder(resultOrder);
        Set<Book> books = new HashSet<>(bookList);
        books.forEach(book -> book.addOrder(resultOrder));

        return Optional.ofNullable(resultOrder);
    }
}
