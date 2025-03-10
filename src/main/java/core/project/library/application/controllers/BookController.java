package core.project.library.application.controllers;

import core.project.library.application.model.BookDTO;
import core.project.library.application.model.BookModel;
import core.project.library.application.service.BookService;
import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import core.project.library.domain.value_objects.ISBN;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.mappers.BookMapper;
import core.project.library.infrastructure.repository.AuthorRepository;
import core.project.library.infrastructure.repository.BookRepository;
import core.project.library.infrastructure.repository.PublisherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/library/book")
public class BookController {

    private final BookMapper bookMapper;

    private final BookService bookService;

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final PublisherRepository publisherRepository;

    @GetMapping("/findById/{bookId}")
    final ResponseEntity<BookModel> findById(@PathVariable("bookId") UUID bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookMapper.toModel(
                        bookService.findById(bookId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, """
                                Book was not found.
                                Make sure the fields you indicate are correct.
                                Otherwise, unfortunately we do not have such data."""))
                        )
                );
    }

    @GetMapping("/findByISBN/{isbn}")
    final ResponseEntity<BookModel> findByISBN(@PathVariable("isbn") String inboundedISBN) {
        ISBN isbn = new ISBN(inboundedISBN);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookMapper.toModel(
                        bookService.findByISBN(isbn)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, """
                                Book was not found.
                                Make sure the fields you indicate are correct.
                                Otherwise, unfortunately we do not have such data."""))
                        )
                );
    }

    @GetMapping("/pageOfBook")
    final ResponseEntity<List<BookModel>> listOfBooks(@RequestParam Integer pageNumber,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam(required = false) String title,
                                                      @RequestParam(required = false) String category) {
        Objects.requireNonNull(pageNumber);
        Objects.requireNonNull(pageSize);

        var books = bookService.listOfBooks(pageNumber, pageSize, title, category);

        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, """
                    We were unable to compile a list of books based on your requests..
                    Make sure the fields you indicate are correct.
                    Otherwise, unfortunately we do not have such data.""");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookMapper.listOfModel(books));
    }

    @PostMapping("/saveBook")
    final ResponseEntity<Void> saveBook(@RequestBody @Valid BookDTO bookDTO,
                                        @RequestParam UUID publisherId,
                                        @RequestParam List<UUID> authorsId) {
        if (bookService.isIsbnExists(Objects.requireNonNull(bookDTO.isbn()))) {
            throw new IllegalArgumentException("ISBN was be used");
        }

        Publisher publisher = publisherRepository
                .findById(publisherId)
                .orElseThrow(() -> new NotFoundException(String.format("Publisher with id %s was not found.", publisherId)));

        Set<Author> authors = new HashSet<>();
        for (UUID authorId : authorsId) {
            authors.add(authorRepository
                    .findById(authorId)
                    .orElseThrow(() -> new NotFoundException(String.format("Author with id %s was not found.", authorId))));
        }

        Book book = Book.create(
                UUID.randomUUID(),
                bookDTO.title(),
                bookDTO.description(),
                bookDTO.isbn(),
                bookDTO.price(),
                bookDTO.quantityOnHand(),
                bookDTO.category(),
                new Events(),
                false,
                publisher,
                authors
        );

        bookService.completelySaveBook(book);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", String.format("/library/book/findById/%s", book.getId().toString()));
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @PatchMapping("/patchBook/{bookId}")
    final ResponseEntity<Void> patchBook(@PathVariable("bookId") UUID bookId,
                                         @RequestParam(required = false) String description,
                                         @RequestParam(required = false) Double price,
                                         @RequestParam(required = false) Integer quantityOnHand) {
        boolean isAllValuesNull = price == null && description == null && quantityOnHand == null;
        if (isAllValuesNull) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        bookService.patchBook(bookId, description, price, quantityOnHand);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/withdrawFromSale/{bookId}")
    final ResponseEntity<Void> withdrawBookFromTheSale(@PathVariable("bookId") UUID bookId) {
        bookService.withdrawBookFromTheSale(bookId);
        return ResponseEntity.noContent().build();
    }
}
