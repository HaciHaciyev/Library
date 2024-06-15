package core.project.library.application.mappers;

import core.project.library.application.model.OrderDTO;
import core.project.library.application.model.OrderModel;
import core.project.library.domain.entities.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    OrderDTO dtoFrom(Order order);
    OrderModel modelFrom(Order order);
    List<OrderDTO> dtosFrom(List<Order> orders);
    List<OrderModel> modelsFrom(List<Order> orders);
}
