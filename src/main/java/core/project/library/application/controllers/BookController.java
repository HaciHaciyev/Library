package core.project.library.application.controllers;

import core.project.library.domain.entities.Book;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@RestController
@RequestMapping("/library/book")
public class BookController {

    private final Optional<BookRepository> bookRepository;

    public BookController(Optional<BookRepository> bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/getBookById/{bookId}")
    public ResponseEntity<Optional<Book>> getBookById(@PathVariable("bookId") String bookId) {
        if (bookRepository.isEmpty()) {
            throw new RuntimeException("BookRepository dependency doesn`t exists in BookController class.");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        Optional.ofNullable(
                                Book.compound(
                                        bookRepository.get().getBookById(bookId)
                                                .orElseThrow(NotFoundException::new),
                                        bookRepository.get().getBookPublisher(bookId)
                                                .orElseThrow(NotFoundException::new),
                                        bookRepository.get().getBookAuthors(bookId),
                                        bookRepository.get().getBookOrders(bookId)
                                )
                        )
                );
    }
}
