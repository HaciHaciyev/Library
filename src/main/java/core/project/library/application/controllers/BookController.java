package core.project.library.application.controllers;

import core.project.library.application.mappers.EntityMapper;
import core.project.library.application.model.BookModel;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @GetMapping("/findById/{bookId}")
    public final ResponseEntity<BookModel> getBookById(@PathVariable("bookId") UUID bookId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        bookService.findById(bookId).orElseThrow(NotFoundException::new))
                );
    }

    /**
     * Finds a book by its title.
     *
     * @param title the title of the book
     * @return a {@code ResponseEntity} containing the {@code BookModel} of the found book
     * @throws NotFoundException if the book is not found
     */
    @GetMapping("/findByTitle/{title}")
    public final ResponseEntity<BookModel> findByName(@PathVariable("title") String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entityMapper.toModel(
                        bookService.findByTitle(title).orElseThrow(NotFoundException::new)
                ));
    }

    /**
     * Retrieves a paginated list of books.
     *
     * @param pageNumber the page number of the book list to retrieve
     * @param pageSize the number of books per page
     * @param category (optional) the category of books to filter
     * @return a {@code ResponseEntity} containing the paginated list of {@code BookModel}s
     * @throws NotFoundException if no books are found for the specified criteria
     */
    @GetMapping("/pageOfBook/")
    public final ResponseEntity<List<BookModel>> listOfBooks(@RequestParam Integer pageNumber,
                                                       @RequestParam Integer pageSize,
                                                       @RequestParam(required = false) String category) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookService
                        .listOfBooks(pageNumber, pageSize, category)
                        .orElseThrow(NotFoundException::new)
                        .stream().map(entityMapper::toModel).toList());
    }
}
