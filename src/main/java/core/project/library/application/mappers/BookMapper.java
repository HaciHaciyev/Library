package core.project.library.application.mappers;

import core.project.library.application.model.BookDTO;
import core.project.library.application.model.BookModel;
import core.project.library.domain.entities.Book;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {

    BookDTO toDTO(Book book);

    BookModel toModel(Book book);

    List<BookDTO> listOfDTO(List<Book> books);

    List<BookModel> listOfModel(List<Book> books);
}
