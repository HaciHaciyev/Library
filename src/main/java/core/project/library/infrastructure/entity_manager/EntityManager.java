package core.project.library.infrastructure.entity_manager;

import core.project.library.domain.entities.Book;
import core.project.library.infrastructure.data_transfer.BookDTO;
import org.mapstruct.Mapper;

@Mapper
public interface EntityManager {

    BookDTO toPersist(Book book);

    Book toDomain(BookDTO bookDTO);
}
