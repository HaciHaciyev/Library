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

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing books in the library system.
 *
 * <p>This controller provides endpoints to handle CRUD operations for books,
 * including retrieving book details by ID or title, listing books with pagination,
 * saving new books, updating existing books, and patching book details. Each endpoint
 * interacts with the {@link BookService} to perform the necessary operations and
 * uses the {@link EntityMapper} to convert between entity and model representations.</p>
 *
 * <p>Endpoints:
 * <ul>
 *     <li>GET /library/book/getBookById/{bookId} - Retrieve a book by its ID.</li>
 *     <li>GET /library/book/findByName/{title} - Find a book by its title.</li>
 *     <li>GET /library/book/page - List books with pagination support.</li>
 *     <li>POST /library/book/saveBook - Save a new book.</li>
 *     <li>PUT /library/book/updateBook/{bookId} - Update an existing book.</li>
 *     <li>PATCH /library/book/patchBook/{bookId} - Patch book details.</li>
 * </ul>
 * </p>
 *
 * <p>Each endpoint returns a {@link ResponseEntity} with appropriate HTTP status codes
 * and headers, and in the case of successful operations, the updated or created
 * {@link BookModel}.</p>
 *
 * <p>Exceptions such as {@link NotFoundException} are thrown when the specified
 * book is not found, ensuring proper error handling and response.</p>
 */
@Slf4j
@RestController
@RequestMapping("/library/book")
public class BookController {

    private final EntityMapper entityMapper;

    private final BookService bookService;

    /**
     * Constructs a new {@code BookController} with the given {@code EntityMapper} and {@code BookService}.
     *
     * @param entityMapper the entity mapper
     * @param bookService the book service
     */
    public BookController(EntityMapper entityMapper, BookService bookService) {
        this.entityMapper = entityMapper;
        this.bookService = bookService;
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param bookId the ID of the book
     * @return a {@code ResponseEntity} containing the {@code BookModel} of the found book
     * @throws NotFoundException if the book is not found
     */
    @GetMapping("/getBookById/{bookId}")
    public ResponseEntity<BookModel> getBookById(@PathVariable("bookId") UUID bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        bookService.getBookById(bookId).orElseThrow(NotFoundException::new))
                );
    }

    /**
     * Finds a book by its title.
     *
     * @param title the title of the book
     * @return a {@code ResponseEntity} containing the {@code BookModel} of the found book
     * @throws NotFoundException if the book is not found
     */
    @GetMapping("/findByName/{title}")
    public ResponseEntity<BookModel> findByName(@PathVariable("title") String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        bookService.findByName(title).orElseThrow(NotFoundException::new)
                ));
    }

    /**
     * Retrieves a paginated list of books.
     *
     * @param pageNumber the page number (optional)
     * @param pageSize the page size (optional)
     * @return a {@code Page} of {@code BookModel} objects
     */
    @GetMapping("/page")
    public Page<BookModel> listOfBooks(@RequestParam(required = false) Integer pageNumber,
                                       @RequestParam(required = false) Integer pageSize) {
        return bookService.listOfBooks(pageNumber, pageSize)
                .map(entityMapper::toModel);
    }

    /**
     * Saves a new book.
     *
     * @param bookModel the book model to save
     * @return a {@code ResponseEntity} with the location and the {@code BookModel} of the saved book
     * @throws NotFoundException if the book could not be saved
     */
    @PostMapping("/saveBook")
    public ResponseEntity<BookModel> saveBook(@RequestBody @Validated BookModel bookModel) {
        Book bookEntity = Book.from(bookModel);
        Optional<Book> book = bookService
                .saveBookAndPublisherWithAuthors(bookEntity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(locationHeader(book))
                .body(entityMapper.toModel(
                        book.orElseThrow(NotFoundException::new)
                ));
    }

    /**
     * Updates an existing book.
     *
     * @param bookId the ID of the book to update
     * @param bookModel the updated book model
     * @return a {@code ResponseEntity} with the location and the {@code BookModel} of the updated book
     * @throws NotFoundException if the book could not be updated
     */
    @PutMapping("/updateBook/{bookId}")
    public ResponseEntity<BookModel> updateBook(@PathVariable("bookId") UUID bookId,
                                                @RequestBody @Validated BookModel bookModel) {
        Book bookEntity = Book.from(bookModel);
        Optional<Book> book = bookService
                .updateBook(bookId, bookEntity);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .headers(locationHeader(book))
                .body(entityMapper.toModel(
                        book.orElseThrow(NotFoundException::new)
                ));
    }

    /**
     * Patches an existing book with the given values.
     *
     * @param bookId the ID of the book to patch
     * @param values the map of values to patch
     * @return a {@code ResponseEntity} with the location and the {@code BookModel} of the patched book
     * @throws NotFoundException if the book could not be patched
     */
    @PatchMapping("/patchBook/{bookId}")
    public ResponseEntity<BookModel> patchBook(@PathVariable("bookId") UUID bookId,
                                               @RequestBody Map<String, String> values) {
        Optional<Book> book = bookService
                .patchBook(bookId, values);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .headers(locationHeader(book))
                .body(entityMapper.toModel(
                        book.orElseThrow(NotFoundException::new)
                ));
    }

    /**
     * Creates HTTP headers containing the location of the given book.
     *
     * @param book the book for which to create the location header
     * @return the HTTP headers with the location of the book
     * @throws NotFoundException if the book is not present
     */
    private HttpHeaders locationHeader(Optional<Book> book) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", String.format(
                "/library/book/getBookById/%s",
                book.orElseThrow(NotFoundException::new)
                .getId().toString()));
        return headers;
    }
}
