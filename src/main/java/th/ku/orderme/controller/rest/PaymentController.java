package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ku.orderme.service.SCBSimulatorPayment;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final SCBSimulatorPayment scbSimulatorPayment;

    public PaymentController(SCBSimulatorPayment scbSimulatorPayment) {
        this.scbSimulatorPayment = scbSimulatorPayment;
    }

    @GetMapping
    public void get() {
        scbSimulatorPayment.generateAccessToken();
    }
}
