package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.BookModel;
import core.project.library.application.service.BookService;
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

    private final EntityMapper entityMapper;

    private final BookService bookService;

    @GetMapping("/findById/{bookId}")
    final ResponseEntity<BookModel> findById(@PathVariable("bookId") UUID bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        bookService.findById(bookId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByTitle/{title}")
    final ResponseEntity<BookModel> findByName(@PathVariable("title") String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
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
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService
                        .listOfBooks(pageNumber, pageSize, category, author)
                        .stream().filter(Objects::nonNull)
                        .map(entityMapper::toModel).toList());
    }
}
