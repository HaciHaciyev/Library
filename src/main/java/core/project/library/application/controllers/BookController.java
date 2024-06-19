package core.project.library.application.controllers;

import core.project.library.application.model.BookDTO;
import core.project.library.application.model.BookModel;
import core.project.library.application.service.BookService;
import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
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
                        bookService.findById(bookId).orElseThrow(NotFoundException::new))
                );
    }

    @GetMapping("/findByTitle/{title}")
    final ResponseEntity<BookModel> findByName(@PathVariable("title") String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookMapper.toModel(
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
                .body(bookMapper.listOfModel(books));
    }

    @PostMapping("/saveBook")
    final ResponseEntity<Void> saveBook(@RequestBody @Valid BookDTO bookDTO,
                                        @RequestParam UUID publisherId,
                                        @RequestParam List<UUID> authorsId) {
        if (bookService.isIsbnExists(Objects.requireNonNull(bookDTO.isbn()))) {
            throw new IllegalArgumentException("ISBN was be used");
        }

        Publisher publisher = publisherRepository.findById(publisherId).orElseThrow(NotFoundException::new);
        Set<Author> authors = new HashSet<>();
        for (UUID authorId : authorsId) {
            authors.add(authorRepository.findById(authorId).orElseThrow(NotFoundException::new));
        }

        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title(bookDTO.title())
                .description(bookDTO.description())
                .isbn(bookDTO.isbn())
                .price(bookDTO.price())
                .quantityOnHand(bookDTO.quantityOnHand())
                .category(bookDTO.category())
                .events(new Events())
                .publisher(publisher)
                .authors(authors)
                .build();

        bookRepository.completelySaveBook(book);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", String.format("/library/book/findById/%s", book.getId().toString()));
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @PatchMapping("/patchBook/{bookId}")
    final ResponseEntity<Void> updateBook(@PathVariable("bookId") UUID bookId,
                                          @RequestParam(required = false) String description,
                                          @RequestParam(required = false) Double price,
                                          @RequestParam(required = false) Integer quantityOnHand) {
        boolean isAllValuesNull = price == null && description == null && quantityOnHand == null;

        if (isAllValuesNull) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            bookService.patchBook(bookId, description, price, quantityOnHand);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
