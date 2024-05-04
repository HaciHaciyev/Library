package core.project.library.application.controllers;

import core.project.library.application.mappers.BookMapper;
import core.project.library.application.model.BookDTO;
import core.project.library.domain.entities.Book;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/library/book")
public class BookController {

    private final BookMapper bookMapper;

    private final BookRepository bookRepository;

    public BookController(BookMapper bookMapper, BookRepository bookRepository) {
        this.bookMapper = bookMapper;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/getBookById/{bookId}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable("bookId") String bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookMapper.toDTO(Book.entityCollectorForBook(
                                bookRepository.getBookById(bookId).orElseThrow(NotFoundException::new),
                                bookRepository.getBookPublisher(bookId).orElseThrow(NotFoundException::new),
                                bookRepository.getBookAuthors(bookId),
                                bookRepository.getBookOrders(bookId)
                                )
                        )
                );
    }
}
