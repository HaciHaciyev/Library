package core.project.library.infrastructure.mappers;

import core.project.library.application.model.AuthorDTO;
import core.project.library.application.model.AuthorModel;
import core.project.library.domain.entities.Author;
import core.project.library.domain.events.Events;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface AuthorMapper {

    AuthorDTO toDTO(Author author);

    AuthorModel toModel(Author author);

    List<AuthorDTO> listOfDTO(List<Author> authors);

    List<AuthorModel> listOfModel(List<Author> authors);

    default Author authorFromDTO(AuthorDTO authorDTO) {
        if (authorDTO == null) {
            return null;
        }

        return Author.create(
                UUID.randomUUID(),
                authorDTO.firstName(),
                authorDTO.lastName(),
                authorDTO.email(),
                authorDTO.address(),
                new Events()
        );
    }
}
