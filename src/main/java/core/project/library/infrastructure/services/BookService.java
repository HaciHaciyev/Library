package core.project.library.infrastructure.services;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.data_transfer.BookDTO;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.AuthorRepository;
import core.project.library.infrastructure.repositories.BookRepository;
import core.project.library.infrastructure.repositories.PublisherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;

    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BookRepository bookRepository;

    private final PublisherRepository publisherRepository;

    private final AuthorRepository authorRepository;

    public Optional<Book> getBookById(UUID bookId) {
        Optional<UUID> publisherId = bookRepository.getPublisherId(bookId);
        if (publisherId.isEmpty()) {
            return Optional.empty();
        }
        List<Author> authors = authorRepository.getAuthorsByBookId(bookId);
        BookDTO bookDTO = bookRepository.getBookById(bookId).orElseThrow(NotFoundException::new);
        Publisher publisher = publisherRepository.getPublisherById(publisherId.get()).orElseThrow(NotFoundException::new);

        return entityCollectorForBook(bookDTO, publisher, authors);
    }

    public Optional<Book> findByName(String title) {
        BookDTO bookDTO = bookRepository.findByName(title).orElseThrow(NotFoundException::new);
        List<Author> authors = authorRepository.getAuthorsByBookId(bookDTO.id());
        Publisher publisher = publisherRepository.getPublisherById(bookDTO.publisherId()).orElseThrow(NotFoundException::new);

        return entityCollectorForBook(bookDTO, publisher, authors);
    }

    public Page<Book> listOfBooks(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        Page<BookDTO> bookPage = bookRepository.listOfBooks(pageRequest);

        List<Book> listOfBooks = new ArrayList<>();
        for (BookDTO bookDTO : bookPage) {
            List<Author> authors = authorRepository.getAuthorsByBookId(bookDTO.id());
            UUID publisherId = bookRepository.getPublisherId(bookDTO.id()).orElseThrow(InternalError::new);
            Publisher publisher = publisherRepository.getPublisherById(publisherId).orElseThrow();
            listOfBooks.add(entityCollectorForBook(bookDTO, publisher, authors).orElseThrow());
        }
        return new PageImpl<>(listOfBooks);
    }

    public Optional<Book> saveBookAndPublisherWithAuthors(Book book) {
        if (publisherRepository.getPublisherById(book.getId()).isEmpty()) {
            publisherRepository.savePublisher(book.getPublisher());
        }
        Optional<Book> savedBook = bookRepository.saveBook(book);

        Set<Author> authorsForSave = book.getAuthors();
        for (Author authorForSave : authorsForSave) {
            if (authorRepository.getAuthorsByBookId(book.getId()).isEmpty()) {
                Optional<Author> savedAuthor = authorRepository.saveAuthor(authorForSave);
                bookRepository.saveBookAuthor(savedBook.orElseThrow(), savedAuthor.orElseThrow());
            } else {
                Author savedAuthor = authorRepository.getAuthorsByBookId(book.getId()).getFirst();
                bookRepository.saveBookAuthor(savedBook.orElseThrow(), savedAuthor);
            }
        }

        return savedBook;
    }

    public Optional<Book> updateBook(UUID bookId, Book book) {
        bookRepository.updateBook(bookId, book);
        return getBookById(bookId);
    }

    public Optional<Book> patchBook(UUID bookId, Map<String, String> values) {
        bookRepository.patchBook(bookId, values);
        return getBookById(bookId);
    }

    private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null) {
            queryPageSize = DEFAULT_PAGE_SIZE;
        } else {
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        }

        Sort sort = Sort.by(Sort.Order.asc("beerName"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

    private Optional<Book> entityCollectorForBook(BookDTO bookDTO, Publisher publisher, List<Author> authors) {
        return Optional.of(Book.builder()
                .id(bookDTO.id())
                .title(bookDTO.title())
                .description(bookDTO.description())
                .isbn(bookDTO.isbn())
                .price(bookDTO.price())
                .quantityOnHand(bookDTO.quantityOnHand())
                .category(bookDTO.category())
                .events(bookDTO.events())
                .category(bookDTO.category())
                .publisher(publisher)
                .authors(new HashSet<>(authors))
                .build()
        );
    }
}
