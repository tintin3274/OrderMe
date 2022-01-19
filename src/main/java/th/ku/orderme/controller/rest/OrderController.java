package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.CartDTO;
import th.ku.orderme.model.Order;
import th.ku.orderme.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/order-me/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @PostMapping
    public String order(@RequestBody CartDTO cartDTO) {
        return orderService.order(cartDTO);
    }


}
