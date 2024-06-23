package core.project.library.infrastructure.mappers;

import core.project.library.application.model.PublisherDTO;
import core.project.library.application.model.PublisherModel;
import core.project.library.domain.entities.Publisher;
import core.project.library.domain.events.Events;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.boot.actuate.autoconfigure.web.mappings.MappingsEndpointAutoConfiguration;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PublisherMapper {

    PublisherDTO toDTO(Publisher publisher);

    PublisherModel toModel(Publisher publisher);

    List<PublisherDTO> listOfDTO(List<Publisher> publishers);

    List<PublisherModel> listOfModel(List<Publisher> publishers);

    @Mapping(target = "id", source = "publisherDTO")
    @Mapping(target = "events", source = "publisherDTO")
    Publisher publisherFromDTO(PublisherDTO publisherDTO);

    default UUID getUUID(PublisherDTO publisherDTO) {
        return UUID.randomUUID();
    }

    default Events getEvents(PublisherDTO publisherDTO) {
        return new Events();
    }
}
