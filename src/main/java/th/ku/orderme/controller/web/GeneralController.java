package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class GeneralController {

    @GetMapping("/")
    public String main() {
        return "index";
    }

    @GetMapping("/payment-fail")
    public String paymentFail() {
        return "payment_fail";
    }

    @GetMapping("/payment")
    public String getPagePayment(){return "payment";}

    @GetMapping("/manage-table")
    public String getPageManageTable(){return "manage_table";}
}
