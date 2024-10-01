package core.project.library.infrastructure.mappers;

import core.project.library.application.model.PublisherDTO;
import core.project.library.application.model.PublisherModel;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PublisherMapper {

    PublisherDTO toDTO(Publisher publisher);

    PublisherModel toModel(Publisher publisher);

    List<PublisherDTO> listOfDTO(List<Publisher> publishers);

    List<PublisherModel> listOfModel(List<Publisher> publishers);

    default Publisher publisherFromDTO(PublisherDTO publisherDTO) {
        if (publisherDTO == null) {
            return null;
        }

        return Publisher.create(
                UUID.randomUUID(),
                publisherDTO.publisherName(),
                publisherDTO.address(),
                publisherDTO.phone(),
                publisherDTO.email(),
                new Events()
        );
    }
}
