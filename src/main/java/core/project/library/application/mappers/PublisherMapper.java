package core.project.library.application.mappers;

import core.project.library.application.model.PublisherDTO;
import core.project.library.application.model.PublisherModel;
import core.project.library.domain.entities.Publisher;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PublisherMapper {

    PublisherDTO toDTO(Publisher publisher);

    PublisherModel toModel(Publisher publisher);

    List<PublisherDTO> listOfDTO(List<Publisher> publishers);

    List<PublisherModel> listOfModel(List<Publisher> publishers);
}
