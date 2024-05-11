package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.BookModel;
import core.project.library.application.model.PublisherDTO;
import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.services.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> getBookById(@PathVariable("bookId") UUID bookId) {
        Optional<?> responseBody;
        responseBody = Optional.ofNullable(
                entityMapper.toModel(bookService.getBookById(bookId).orElseThrow(NotFoundException::new))
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
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
    public ResponseEntity saveBook(@RequestBody @Validated BookModel bookModel) {
        Book bookEntity = Book.from(bookModel);
        Optional<Book> book = bookService
                .saveBookAndPublisherWithAuthors(bookEntity);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/library/book/getBookById/" + book.get().getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

}
