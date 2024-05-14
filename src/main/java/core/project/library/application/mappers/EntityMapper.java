package core.project.library.application.mappers;

import core.project.library.application.model.*;
import core.project.library.domain.entities.*;
import org.mapstruct.Mapper;

@Mapper
public interface EntityMapper {
    AuthorDTO toDTO(Author author);
    AuthorModel toModel(Author author);

    BookDTO toDTO(Book book);
    BookModel toModel(Book book);

    CustomerDTO toDTO(Customer customer);
    CustomerModel toModel(Customer customer);

    OrderDTO toDto(Order order);
    OrderModel toModel(Order order);

    PublisherDTO toDto(Publisher publisher);
    PublisherModel toModel(Publisher publisher);
}
