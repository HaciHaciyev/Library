package core.project.library.application.controllers;

import core.project.library.application.mappers.BookMapper;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.services.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/library/book")
public class BookController {

    private final Optional<BookMapper> bookMapper;

    private final BookService bookService;

    public BookController(Optional<BookMapper> bookMapper, BookService bookService) {
        if (bookMapper.isEmpty()) log.info("BookMapper is empty. Now we cannot return a DTO object.");
        this.bookMapper = bookMapper;
        this.bookService = bookService;
    }

    @GetMapping("/getBookById/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable("bookId") String bookId) {
        Optional<?> responseBody;
        if (bookMapper.isPresent()) {
            responseBody = Optional.ofNullable(
                    bookMapper.get().toDTO(bookService.getBookById(bookId).orElseThrow(NotFoundException::new))
            );
        } else {
            responseBody = Optional.ofNullable(bookService.getBookById(bookId).orElseThrow(NotFoundException::new));
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
