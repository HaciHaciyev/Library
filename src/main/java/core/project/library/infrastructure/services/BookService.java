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
        return entityCollectorForBook(
                bookRepository.getBookById(bookId).orElseThrow(NotFoundException::new),
                publisherRepository.getPublisherByBookId(bookId).orElseThrow(NotFoundException::new),
                authorRepository.getAuthorsByBookId(bookId)
        );
    }

    public Optional<Book> findByName(String title) {
        Book book = bookRepository.findByName(title).orElseThrow(NotFoundException::new);
        return entityCollectorForBook(book,
                publisherRepository.getPublisherByBookId(book.getId()).orElseThrow(NotFoundException::new),
                authorRepository.getAuthorsByBookId(book.getId())
        );
    }

    public Page<Book> listOfBooks(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        Page<Book> bookPage = bookRepository.listOfBooks(pageRequest);
        bookPage.forEach(book -> {
            book.addPublisher(publisherRepository.getPublisherByBookId(book.getId()).orElseThrow());
            authorRepository.getAuthorsByBookId(book.getId()).forEach(author -> book.addAuthor(author.orElseThrow()));
        });
        return bookPage;
    }

    public Optional<Book> saveBookAndPublisherWithAuthors(Book book) {
        Optional<Book> savedBook = bookRepository.saveBook(book);

        Optional<Publisher> savedPublisher = publisherRepository.savePublisher(book.getPublisher());
        bookRepository.saveBook_Publisher(savedBook.orElseThrow(), savedPublisher.orElseThrow());

        Set<Author> authorsForSave = book.getAuthors();
        for (Author authorForSave : authorsForSave) {
            Optional<Author> savedAuthor = authorRepository.saveAuthor(authorForSave);
            bookRepository.saveBook_Author(savedBook.orElseThrow(), savedAuthor.orElseThrow());
        }

        return savedBook;
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
            Book book, Publisher publisher, List<Optional<Author>> authors) {
        Set<Author> authorSet = new HashSet<>();
        authors.forEach(author -> authorSet.add(author.orElseThrow(NotFoundException::new)));

        book.addPublisher(publisher);
        authorSet.forEach(book::addAuthor);
        return Optional.of(book);
    }
}
