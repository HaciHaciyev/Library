package core.project.library.application.mappers;

import core.project.library.application.model.BookDTO;
import core.project.library.domain.entities.Book;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {
    BookDTO toDTO(Book book);
}
