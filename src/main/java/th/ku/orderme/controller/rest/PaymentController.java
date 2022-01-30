package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import th.ku.orderme.service.SCBSimulatorPayment;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final SCBSimulatorPayment scbSimulatorPayment;

    public PaymentController(SCBSimulatorPayment scbSimulatorPayment) {
        this.scbSimulatorPayment = scbSimulatorPayment;
    }

    @GetMapping("/qrcode")
    public String generateQrCode() {
        return scbSimulatorPayment.generateQrCode(50.0);
    }

    @GetMapping("/deeplink")
    public RedirectView generateDeeplink() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(scbSimulatorPayment.generateDeeplink(50.0));
        return redirectView;
    }
}
