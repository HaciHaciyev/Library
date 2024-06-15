package core.project.library.application.controllers;

import core.project.library.application.mappers.BookMapper;
import core.project.library.application.model.BookModel;
import core.project.library.application.service.BookService;
import core.project.library.domain.entities.Book;
import core.project.library.infrastructure.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/library/book")
public class BookController {

    private final BookMapper mapper;

    private final BookService bookService;

    @GetMapping("/findById/{bookId}")
    final ResponseEntity<BookModel> findById(@PathVariable("bookId") UUID bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.modelFrom(
                        bookService.findById(bookId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByTitle/{title}")
    final ResponseEntity<BookModel> findByName(@PathVariable("title") String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.modelFrom(
                        bookService.findByTitle(title).orElseThrow(NotFoundException::new)
                ));
    }

    @GetMapping("/pageOfBook")
    final ResponseEntity<List<BookModel>> listOfBooks(@RequestParam Integer pageNumber,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam(required = false) String category,
                                                      @RequestParam(required = false) String author) {
        Objects.requireNonNull(pageNumber);
        Objects.requireNonNull(pageSize);

        List<Book> books = bookService.listOfBooks(pageNumber, pageSize, category, author);

        if (books.isEmpty()) {
            throw new NotFoundException("Books not found");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.modelsFrom(books));
    }
}
