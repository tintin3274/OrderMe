package th.ku.orderme.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Payment;
import th.ku.orderme.repository.PaymentRepository;
import th.ku.orderme.service.BillService;
import th.ku.orderme.service.PaymentService;
import th.ku.orderme.service.SCBSimulatorPaymentService;
import th.ku.orderme.util.ConstantUtil;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SCBSimulatorPaymentService scbSimulatorPaymentService;
    private final BillService billService;

    @PostMapping("/cash/{billId}")
    public String cash(@PathVariable int billId) {
        Bill bill = billService.findById(billId);
        if(bill.getStatus().equalsIgnoreCase(ConstantUtil.OPEN)) {
            billService.setStatusPaymentBill(bill.getId());
            Payment payment = paymentService.payBill(billId);
            payment.setChannel(ConstantUtil.CASH);
            paymentService.save(payment);
            paymentService.paymentComplete(payment.getRef1(), ConstantUtil.CASH, null);
            return payment.getRef1();
        }
        else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) {
            Payment payment = paymentService.payBill(billId);
            payment.setChannel(ConstantUtil.CASH);
            paymentService.save(payment);
            paymentService.paymentComplete(payment.getRef1(), ConstantUtil.CASH, null);
            return payment.getRef1();
        }
        else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
            Payment payment = paymentRepository.findByBill_Id(bill.getId());
            if(payment.getStatus().equalsIgnoreCase(ConstantUtil.PAID)) {
                return payment.getRef1();
            }
        }
        return "Bill ID Ref1 not found";
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
