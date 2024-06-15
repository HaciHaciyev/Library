package core.project.library.application.mappers;

import core.project.library.application.model.CustomerDTO;
import core.project.library.application.model.CustomerModel;
import core.project.library.domain.entities.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {

    CustomerDTO toDTO(Customer customer);

    CustomerModel toModel(Customer customer);

    List<CustomerDTO> listOfDTO(List<Customer> customers);

    List<CustomerModel> listOfModel(List<Customer> customers);
}
