package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SCBPaymentConfirmController {

    @RequestMapping("/scb/payment-confirm")
    public void confirm(@RequestBody String payload) {
        System.out.println(payload);
    }
}
