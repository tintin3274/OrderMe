package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import th.ku.orderme.model.Payment;
import th.ku.orderme.service.PaymentService;
import th.ku.orderme.service.SCBSimulatorPaymentService;
import th.ku.orderme.util.ConstantUtil;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final SCBSimulatorPaymentService scbSimulatorPaymentService;

    public PaymentController(PaymentService paymentService, SCBSimulatorPaymentService scbSimulatorPaymentService) {
        this.paymentService = paymentService;
        this.scbSimulatorPaymentService = scbSimulatorPaymentService;
    }

    @GetMapping("/cash")
    public String cash() {
        return "Pay at Cashier";
    }

    @GetMapping("/qrcode")
    public String generateQrCode(@RequestParam int billId) {
        Payment payment = paymentService.payBill(billId);
        if(payment == null) return "Something wrong! Cannot pay";
        return scbSimulatorPaymentService.generateQrCode(payment);
    }

    @GetMapping("/deeplink")
    public RedirectView generateDeeplink(@RequestParam int billId) {
        RedirectView redirectView = new RedirectView();
        Payment payment = paymentService.payBill(billId);
        if(payment == null) {
            redirectView.setUrl("/payment-fail");
        }
        else {
            redirectView.setUrl(scbSimulatorPaymentService.generateDeeplink(payment));
        }
        return redirectView;
    }
}
