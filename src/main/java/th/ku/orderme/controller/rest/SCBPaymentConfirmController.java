package th.ku.orderme.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ku.orderme.service.SCBSimulatorPaymentService;

@Slf4j
@RestController
public class SCBPaymentConfirmController {

    private final SCBSimulatorPaymentService scbSimulatorPaymentService;

    public SCBPaymentConfirmController(SCBSimulatorPaymentService scbSimulatorPaymentService) {
        this.scbSimulatorPaymentService = scbSimulatorPaymentService;
    }

    @RequestMapping("/scb/payment-confirm")
    public void confirm(@RequestBody String request) {
        log.info(request);
        scbSimulatorPaymentService.paymentConfirm(request);
    }
}
