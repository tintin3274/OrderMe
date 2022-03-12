package th.ku.orderme.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import th.ku.orderme.dto.CartDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Token;
import th.ku.orderme.service.BillService;
import th.ku.orderme.service.OrderService;
import th.ku.orderme.service.TokenService;
import th.ku.orderme.util.ConstantUtil;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class OrderControllerWeb {
    private final BillService billService;
    private final OrderService orderService;
    private final TokenService tokenService;

    @GetMapping("/order")
    public String order(@RequestBody CartDTO cartDTO, @CookieValue(name = "uid") String uid, RedirectAttributes redirectAttributes) {
        Token token = tokenService.findById(uid);
        if(token == null) return "redirect:/";
        Bill bill = token.getBill();
        if(bill == null) {
            bill = billService.createBillTakeOut(uid);
            billService.autoCancelBill(bill.getId());
        }

        String orderDetail = orderService.order(cartDTO, bill);
        if(orderDetail.startsWith("Bill ID:")) {
            redirectAttributes.addFlashAttribute("information", orderDetail);
            if(bill.getType().equalsIgnoreCase(ConstantUtil.TAKE_OUT)) {
                billService.setStatusPaymentBill(bill.getId());
                return "redirect:/payment";
            }
        }
        else {
            redirectAttributes.addFlashAttribute("error", orderDetail);
        }
        return "redirect:/main-menu";
    }
}
