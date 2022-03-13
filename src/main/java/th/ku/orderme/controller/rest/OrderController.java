package th.ku.orderme.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.CartDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Order;
import th.ku.orderme.model.Token;
import th.ku.orderme.service.BillService;
import th.ku.orderme.service.OrderService;
import th.ku.orderme.service.TokenService;
import th.ku.orderme.util.ConstantUtil;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final BillService billService;
    private final TokenService tokenService;


    @GetMapping("/all")
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @PostMapping("/new")
    public String order(@RequestBody CartDTO cartDTO, @CookieValue(name = "uid") String uid) {
        Token token = tokenService.findById(uid);
        if(token == null) return "Invalid Token: "+uid;
        Bill bill = token.getBill();
        if(bill == null) {
            bill = billService.createBillTakeOut(uid);
            billService.autoCancelBill(bill.getId());
        }

        String orderDetail = orderService.order(cartDTO, bill);
        if(orderDetail.startsWith("Bill ID:")) {
            if(bill.getType().equalsIgnoreCase(ConstantUtil.TAKE_OUT)) {
                billService.setStatusPaymentBill(bill.getId());
            }
        }
        return orderDetail;
    }
}
