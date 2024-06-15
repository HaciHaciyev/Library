package core.project.library.application.mappers;

import core.project.library.application.model.OrderDTO;
import core.project.library.application.model.OrderModel;
import core.project.library.domain.entities.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {

    OrderDTO toDTO(Order order);

    OrderModel toModel(Order order);

    List<OrderDTO> listOfDTO(List<Order> orders);

    List<OrderModel> listOfModel(List<Order> orders);
}
