package core.project.library.infrastructure.services;

import core.project.library.domain.entities.Author;
import core.project.library.domain.entities.Book;
import core.project.library.domain.entities.Publisher;
import core.project.library.infrastructure.exceptions.NotFoundException;
import core.project.library.infrastructure.repositories.AuthorRepository;
import core.project.library.infrastructure.repositories.BookRepository;
import core.project.library.infrastructure.repositories.PublisherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
        if (bookRepository.getPublisherId(bookId).isEmpty()) {
            return Optional.empty();
        }
        Publisher publisher = publisherRepository.getPublisherById(
                bookRepository.getPublisherId(bookId).get()
        ).orElseThrow(NotFoundException::new);
        List<Author> authors = authorRepository.getAuthorsByBookId(bookId);
        Book book = bookRepository.getBookById(bookId).orElseThrow(NotFoundException::new);

        return entityCollectorForBook(book, publisher, authors);
    }

    public Optional<Book> findByName(String title) {
        Book book = bookRepository.findByName(title).orElseThrow(NotFoundException::new);
        Publisher publisher = publisherRepository.getPublisherById(
                bookRepository.getPublisherId(book.getId()).orElseThrow()
        ).orElseThrow(NotFoundException::new);
        List<Author> authors = authorRepository.getAuthorsByBookId(book.getId());

        return entityCollectorForBook(book, publisher, authors);
    }

    public Page<Book> listOfBooks(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        Page<Book> bookPage = bookRepository.listOfBooks(pageRequest);
        for (Book book : bookPage) {
            Optional<Publisher> currentPublisher = publisherRepository.getPublisherById(
                    bookRepository.getPublisherId(book.getId()).orElseThrow()
            );
            book.addPublisher(currentPublisher.orElseThrow());

            List<Author> currentAuthors = authorRepository.getAuthorsByBookId(book.getId());
            currentAuthors.forEach(book::addAuthor);
        }
        return bookPage;
    }

    public Optional<Book> saveBookAndPublisherWithAuthors(Book book) {
        publisherRepository.savePublisher(book.getPublisher());
        Optional<Book> savedBook = bookRepository.saveBook(book);

        Set<Author> authorsForSave = book.getAuthors();
        for (Author authorForSave : authorsForSave) {
            Optional<Author> savedAuthor = authorRepository.saveAuthor(authorForSave);
            bookRepository.saveBookAuthor(savedBook.orElseThrow(), savedAuthor.orElseThrow());
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

    private static Optional<Book> entityCollectorForBook(
            Book book, Publisher publisher, List<Author> authors) {
        Set<Author> authorSet = new HashSet<>(authors);
        book.addPublisher(publisher);
        authorSet.forEach(book::addAuthor);
        return Optional.of(book);
    }
}
