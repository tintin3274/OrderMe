package th.ku.orderme.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.CartDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Token;
import th.ku.orderme.service.TokenService;
import th.ku.orderme.util.ConstantUtil;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class OrderControllerWeb {
    private final TokenService tokenService;

    @GetMapping("/order")
    public String order(@RequestBody CartDTO cartDTO, @CookieValue(name = "uid") String uid) {
        Token token = tokenService.findById(uid);
        if(token == null) return "redirect:/";
        Bill bill = token.getBill();
        if(bill != null && bill.getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) {
            return "redirect:/payment";
        }
        return "redirect:/main-menu";
    }
}
