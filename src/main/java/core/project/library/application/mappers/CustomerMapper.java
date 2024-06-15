package core.project.library.application.mappers;

import core.project.library.application.model.CustomerDTO;
import core.project.library.application.model.CustomerModel;
import core.project.library.domain.entities.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {
    CustomerDTO dtoFrom(Customer customer);
    CustomerModel modelFrom(Customer customer);
    List<CustomerDTO> dtosFrom(List<Customer> customers);
    List<CustomerModel> modelsFrom(List<Customer> customers);
}
