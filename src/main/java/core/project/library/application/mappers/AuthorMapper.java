package core.project.library.application.mappers;

import core.project.library.application.model.AuthorDTO;
import core.project.library.application.model.AuthorModel;
import core.project.library.domain.entities.Author;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AuthorMapper {
    AuthorDTO dtoFrom(Author author);
    AuthorModel modelFrom(Author author);
    List<AuthorDTO> dtosFrom(List<Author> authors);
    List<AuthorModel> modelsFrom(List<Author> authors);
}
