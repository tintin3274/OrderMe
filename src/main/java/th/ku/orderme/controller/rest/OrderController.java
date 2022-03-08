package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.CartDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Order;
import th.ku.orderme.model.Token;
import th.ku.orderme.service.OrderService;
import th.ku.orderme.service.TokenService;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;
    private final TokenService tokenService;

    public OrderController(OrderService orderService, TokenService tokenService) {
        this.orderService = orderService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @PostMapping("/new")
    public String order(@RequestBody CartDTO cartDTO, @CookieValue(name = "uid") String uid) {
        Token token = tokenService.findById(uid);
        if(token == null) return ("Invalid Token: "+uid);
        Bill bill = token.getBill();
        return orderService.order(cartDTO, bill);
    }
}
