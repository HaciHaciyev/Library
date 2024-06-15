package core.project.library.application.mappers;

import core.project.library.application.model.BookDTO;
import core.project.library.application.model.BookModel;
import core.project.library.domain.entities.Book;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {
    BookDTO dtoFrom(Book book);
    BookModel modelFrom(Book book);
    List<BookDTO> dtosFrom(List<Book> books);
    List<BookModel> modelsFrom(List<Book> books);
}
