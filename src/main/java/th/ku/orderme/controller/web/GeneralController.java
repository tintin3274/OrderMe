package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class GeneralController {

    @GetMapping("/payment-fail")
    public String paymentFail() {
        return "payment_fail";
    }
}
