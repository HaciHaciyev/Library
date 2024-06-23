package core.project.library.infrastructure.mappers;

import core.project.library.application.model.CustomerDTO;
import core.project.library.application.model.CustomerModel;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper
public interface CustomerMapper {

    CustomerDTO toDTO(Customer customer);

    CustomerModel toModel(Customer customer);

    List<CustomerDTO> listOfDTO(List<Customer> customers);

    List<CustomerModel> listOfModel(List<Customer> customers);

    @Mapping(target = "id", source = "customerDTO")
    @Mapping(target = "events", source = "customerDTO")
    Customer customerFromDTO(CustomerDTO customerDTO);

    default UUID getUUID(CustomerDTO customerDTO) {
        return UUID.randomUUID();
    }

    default Events getEvents(CustomerDTO customerDTO) {
        return new Events();
    }
}
