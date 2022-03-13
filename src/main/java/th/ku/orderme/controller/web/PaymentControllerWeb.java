package th.ku.orderme.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Payment;
import th.ku.orderme.model.Token;
import th.ku.orderme.repository.PaymentRepository;
import th.ku.orderme.service.BillService;
import th.ku.orderme.service.PaymentService;
import th.ku.orderme.service.SCBSimulatorPaymentService;
import th.ku.orderme.service.TokenService;
import th.ku.orderme.util.ConstantUtil;

import java.text.DecimalFormat;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentControllerWeb {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SCBSimulatorPaymentService scbSimulatorPaymentService;
    private final TokenService tokenService;
    private final BillService billService;

    @GetMapping
    public String payment(@CookieValue(name = "uid") String uid, Model model) {
        Token token = tokenService.findById(uid);
        if(token != null) {
            Bill bill = token.getBill();
            if(bill == null) {
                return "redirect:/main-menu";
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.OPEN)) {
                return "redirect:/main-menu";
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) {
                model.addAttribute("bill", billService.getBillDTO(bill.getId()));
                return "payment";
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
                Payment payment = paymentRepository.findByBill_Id(bill.getId());
                if(payment.getStatus().equalsIgnoreCase(ConstantUtil.PAID)) {
                    return "redirect:/receipt/"+payment.getRef1();
                }
            }
        }
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/cash")
    public String cash() {
        return "Pay at Cashier";
    }

    @GetMapping("/qrcode")
    public String generateQrCode(@CookieValue(name = "uid") String uid, Model model) {
        Token token = tokenService.findById(uid);
        if(token != null) {
            Bill bill = token.getBill();
            if(bill == null) {
                return "redirect:/main-menu";
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) {
                Payment payment = paymentRepository.findByBill_Id(bill.getId());

                if(payment == null || !payment.getChannel().equalsIgnoreCase(ConstantUtil.QR_CODE)) {
                    payment = paymentService.payBill(bill.getId());
                    String response = scbSimulatorPaymentService.generateQrCode(payment);
                    String qrImage = scbSimulatorPaymentService.extractQrImageFromResponse(response);
                    model.addAttribute("qrImage", qrImage);
                }
                else {
                    String qrImage = scbSimulatorPaymentService.extractQrImageFromResponse(payment.getGenerateInfo());
                    model.addAttribute("qrImage", qrImage);
                }

                model.addAttribute("billId", payment.getBill().getId());
                model.addAttribute("ref1", payment.getRef1());
                model.addAttribute("ref2", payment.getRef2());
                model.addAttribute("ref3", payment.getRef3());
                model.addAttribute("total", df.format(payment.getTotal()));
                return "payment_qrcode";
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
                Payment payment = paymentRepository.findByBill_Id(bill.getId());
                if(payment.getStatus().equalsIgnoreCase(ConstantUtil.PAID)) {
                    return "redirect:/receipt/"+payment.getRef1();
                }
            }
        }
        return "redirect:/";
    }

    // for QR30 Only
    @GetMapping("/qrcode/verify/{transRef}")
    public String slipVerification(@PathVariable String transRef) {
        if(scbSimulatorPaymentService.slipVerification(transRef)) {
            return "redirect:/payment/qrcode/success";
        }
        return "redirect:/payment/qrcode";
    }

    @GetMapping("/deeplink")
    public RedirectView generateDeeplink(@CookieValue(name = "uid") String uid) {
        RedirectView redirectView = new RedirectView();
        Token token = tokenService.findById(uid);
        if(token != null) {
            Bill bill = token.getBill();
            if(bill == null) {
                redirectView.setUrl("/main-menu");
                return redirectView;
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) {
                Payment payment = paymentRepository.findByBill_Id(bill.getId());

                if(payment == null || !payment.getChannel().equalsIgnoreCase(ConstantUtil.DEEP_LINK)) {
                    payment = paymentService.payBill(bill.getId());
                    redirectView.setUrl(scbSimulatorPaymentService.generateDeeplink(payment));
                }
                else {
                    redirectView.setUrl(scbSimulatorPaymentService.extractDeepLinkFromResponse(payment.getGenerateInfo()));
                }
                return redirectView;
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
                Payment payment = paymentRepository.findByBill_Id(bill.getId());
                if(payment.getStatus().equalsIgnoreCase(ConstantUtil.PAID)) {
                    redirectView.setUrl("/receipt/"+payment.getRef1());
                    return redirectView;
                }
            }
        }
        redirectView.setUrl("/");
        return redirectView;
    }

    @GetMapping(value = {"/qrcode/success", "/deeplink/success"})
    public String paymentSuccess(@CookieValue(name = "uid") String uid) {
        Token token = tokenService.findById(uid);
        if(token != null) {
            Bill bill = token.getBill();
            if(bill == null) {
                return "redirect:/main-menu";
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
                Payment payment = paymentRepository.findByBill_Id(bill.getId());
                if(payment.getStatus().equalsIgnoreCase(ConstantUtil.PAID)) {
                    return "redirect:/receipt/"+payment.getRef1();
                }
            }
            else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) {
                Payment payment = paymentRepository.findByBill_Id(bill.getId());
                if(scbSimulatorPaymentService.inquiryTransaction(bill.getId())) {
                    return "redirect:/receipt/"+payment.getRef1();
                }
                else {
                    if(payment.getChannel().equalsIgnoreCase(ConstantUtil.QR_CODE)) {
                        return "redirect:/payment/qrcode";
                    }
                    else if(payment.getChannel().equalsIgnoreCase(ConstantUtil.DEEP_LINK)) {
                        return "redirect:/payment/deeplink";
                    }
                }
            }
        }
        return "redirect:/";
    }
}
