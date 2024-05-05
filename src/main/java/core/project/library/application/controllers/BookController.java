package core.project.library.application.controllers;

import core.project.library.application.mappers.BookMapper;
import core.project.library.application.model.BookDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.services.BookService;
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

    private final BookService bookService;

    public BookController(BookMapper bookMapper, BookService bookService) {
        this.bookMapper = bookMapper;
        this.bookService = bookService;
    }

    @GetMapping("/getBookById/{bookId}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable("bookId") String bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookMapper.toDTO(bookService.getBookById(bookId).orElseThrow(NotFoundException::new)));
    }
}
