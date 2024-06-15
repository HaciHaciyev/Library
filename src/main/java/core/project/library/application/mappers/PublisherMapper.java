package core.project.library.application.mappers;

import core.project.library.application.model.PublisherDTO;
import core.project.library.application.model.PublisherModel;
import core.project.library.domain.entities.Publisher;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PublisherMapper {
    PublisherDTO dtoFrom(Publisher publisher);
    PublisherModel modelFrom(Publisher publisher);
    List<PublisherDTO> dtosFrom(List<Publisher> publishers);
    List<PublisherModel> modelsFrom(List<Publisher> publishers);
}
