package core.project.library.infrastructure.mappers;

import core.project.library.application.model.AuthorDTO;
import core.project.library.application.model.AuthorModel;
import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper
public interface AuthorMapper {

    AuthorDTO toDTO(Author author);

    AuthorModel toModel(Author author);

    List<AuthorDTO> listOfDTO(List<Author> authors);

    List<AuthorModel> listOfModel(List<Author> authors);

    @Mapping(target = "id", source = "authorDTO")
    @Mapping(target = "events", source = "authorDTO")
    Author authorFromDTO(AuthorDTO authorDTO);

    default UUID getUUID(AuthorDTO authorDTO) {
        return UUID.randomUUID();
    }

    default Events getEvents(AuthorDTO authorDTO) {
        return new Events();
    }
}
