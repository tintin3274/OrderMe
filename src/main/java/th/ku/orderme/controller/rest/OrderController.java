package th.ku.orderme.controller.rest;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.CartDTO;
import th.ku.orderme.dto.UpdateOrderDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Order;
import th.ku.orderme.model.Token;
import th.ku.orderme.model.Views;
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

    @JsonView(Views.Overall.class)
    @GetMapping("/{id}")
    public Order findById(@PathVariable int id) {
        return orderService.findById(id);
    }

    @JsonView(Views.Overall.class)
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    @PostMapping("/update")
    public UpdateOrderDTO sendUpdateOrderDTO(@RequestParam int id, @RequestParam String status) {
        UpdateOrderDTO updateOrderDTO = null;
        switch (status) {
            case ConstantUtil.COOKING: {
                updateOrderDTO = orderService.changeOrderToCooking(id);
                break;
            }
            case ConstantUtil.SERVING: {
                updateOrderDTO = orderService.changeCookingToServing(id);
                break;
            }
            case ConstantUtil.COMPLETE: {
                updateOrderDTO = orderService.changeToComplete(id);
                break;
            }
            case ConstantUtil.CANCEL: {
                updateOrderDTO = orderService.changeToCancel(id);
                break;
            }
        }
        return updateOrderDTO;
    }

    @GetMapping("/doing")
    public List<UpdateOrderDTO> getAllOrderNotPendingAndCancelAndComplete() {
        return orderService.getDoingOrder();
    }
}
