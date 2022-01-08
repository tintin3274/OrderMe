package th.ku.orderme.service;

import org.springframework.stereotype.Service;
import th.ku.orderme.model.Order;
import th.ku.orderme.repository.OrderRepository;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
