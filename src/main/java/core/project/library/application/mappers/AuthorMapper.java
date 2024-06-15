package core.project.library.application.mappers;

import core.project.library.application.model.AuthorDTO;
import core.project.library.application.model.AuthorModel;
import core.project.library.domain.entities.Author;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AuthorMapper {

    AuthorDTO toDTO(Author author);

    AuthorModel toModel(Author author);

    List<AuthorDTO> listOfDTO(List<Author> authors);

    List<AuthorModel> listOfModel(List<Author> authors);
}
