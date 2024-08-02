package core.project.library.infrastructure.mappers;

import core.project.library.application.model.CustomerDTO;
import core.project.library.application.model.CustomerModel;
import core.project.library.domain.entities.Customer;
import core.project.library.domain.events.Events;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface CustomerMapper {

    CustomerDTO toDTO(Customer customer);

    CustomerModel toModel(Customer customer);

    List<CustomerDTO> listOfDTO(List<Customer> customers);

    List<CustomerModel> listOfModel(List<Customer> customers);

    default Customer customerFromDTO(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }

        return Customer.create(
                UUID.randomUUID(),
                customerDTO.firstName(),
                customerDTO.lastName(),
                customerDTO.password(),
                customerDTO.email(),
                customerDTO.address(),
                new Events()
        );
    }
}
