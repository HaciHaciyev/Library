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

    private final Optional<EntityMapper> entityMapper;

    private final BookService bookService;

    public BookController(Optional<EntityMapper> entityMapper, BookService bookService) {
        if (entityMapper.isEmpty()) log.info("mapper is empty. Now we cannot return a DTO object.");
        this.entityMapper = entityMapper;
        this.bookService = bookService;
    }

    @GetMapping("/getBookById/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable("bookId") UUID bookId) {
        Optional<?> responseBody;
        if (entityMapper.isPresent()) {
            responseBody = Optional.ofNullable(
                    entityMapper.get().toModel(bookService.getBookById(bookId).orElseThrow(NotFoundException::new))
            );
        } else {
            responseBody = Optional.ofNullable(bookService.getBookById(bookId).orElseThrow(NotFoundException::new));
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @GetMapping("/findByName/{title}")
    public ResponseEntity<BookModel> findByName(@PathVariable("title") String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.get().toModel(
                        bookService.findByName(title).orElseThrow(NotFoundException::new)
                ));
    }

    @GetMapping("/page")
    public Page<BookModel> listOfBooks(@RequestParam(required = false) Integer pageNumber,
                                       @RequestParam(required = false) Integer pageSize) {
        return bookService.listOfBooks(pageNumber, pageSize)
                .map(entityMapper.get()::toModel);
    }

    @PostMapping("/saveBook")
    public ResponseEntity saveBook(@RequestBody @Validated BookModel bookModel) {
        // TODO for Nicat. Replace entityMapper.toEntity() with manual code.
        Optional<Book> book = bookService
                .saveBookAndPublisherWithAuthors(entityMapper.get().toEntity(bookModel));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/library/book/getBookById/" + book.get().getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }
}
