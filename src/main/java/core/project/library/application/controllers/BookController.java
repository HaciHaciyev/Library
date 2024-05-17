package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.BookModel;
import core.project.library.domain.entities.Book;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.services.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/library/book")
public class BookController {

    private final EntityMapper entityMapper;

    private final BookService bookService;

    public BookController(EntityMapper entityMapper, BookService bookService) {
        this.entityMapper = entityMapper;
        this.bookService = bookService;
    }

    @GetMapping("/getBookById/{bookId}")
    public ResponseEntity<BookModel> getBookById(@PathVariable("bookId") UUID bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        bookService.getBookById(bookId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByName/{title}")
    public ResponseEntity<BookModel> findByName(@PathVariable("title") String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        bookService.findByName(title).orElseThrow(NotFoundException::new)
                ));
    }

    @GetMapping("/page")
    public Page<BookModel> listOfBooks(@RequestParam(required = false) Integer pageNumber,
                                       @RequestParam(required = false) Integer pageSize) {
        return bookService.listOfBooks(pageNumber, pageSize)
                .map(entityMapper::toModel);
    }

    @PostMapping("/saveBook")
    public ResponseEntity<BookModel> saveBook(@RequestBody @Validated BookModel bookModel) {
        Book bookEntity = Book.from(bookModel);
        Optional<Book> book = bookService
                .saveBookAndPublisherWithAuthors(bookEntity);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", String.format(
                "/library/book/getBookById/%s",
                book.orElseThrow(NotFoundException::new)
                .getId().toString()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(entityMapper.toModel(
                        book.orElseThrow(NotFoundException::new)
                ));
    }

    @PutMapping("/updateBook/{bookId}")
    public ResponseEntity<BookModel> updateBook(@PathVariable("bookId") UUID bookId,
                                                @RequestBody @Validated BookModel bookModel) {
        Book bookEntity = Book.from(bookModel);
        Optional<Book> book = bookService
                .updateBook(bookId, bookEntity);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", String.format(
                "/library/book/getBookById/%s",
                book.orElseThrow(NotFoundException::new)
                .getId().toString()));

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .headers(headers)
                .body(entityMapper.toModel(
                        book.orElseThrow(NotFoundException::new)
                ));
    }
}
