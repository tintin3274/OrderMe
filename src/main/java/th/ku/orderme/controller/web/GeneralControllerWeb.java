package th.ku.orderme.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.ReceiptDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Token;
import th.ku.orderme.service.PaymentService;
import th.ku.orderme.service.TokenService;
import th.ku.orderme.util.ConstantUtil;
import th.ku.orderme.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class GeneralControllerWeb {
    private final PaymentService paymentService;
    private final TokenService tokenService;

    @GetMapping("/")
    public String main(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie1 = CookieUtil.createCookie("uid", null, 0, false, true, "/", request.getServerName());
        Cookie cookie2 = CookieUtil.createCookie("type", null, 0, false, true, "/", request.getServerName());
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        return "index";
    }

    @GetMapping("/receipt/{ref1}")
    public String getPageReceipt(@PathVariable String ref1, Model model) {
        ReceiptDTO receiptDTO = paymentService.getReceiptDTO(ref1);
        if(receiptDTO != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
            String date = formatter.format(receiptDTO.getPayment().getUpdatedTimestamp());

            model.addAttribute("billId", receiptDTO.getBill().getBillId());
            model.addAttribute("person", receiptDTO.getBill().getPerson());
            model.addAttribute("type", receiptDTO.getBill().getType());
            model.addAttribute("orders", receiptDTO.getBill().getOrders());
            model.addAttribute("ref1", receiptDTO.getPayment().getRef1());
            model.addAttribute("channel", receiptDTO.getPayment().getChannel());
            model.addAttribute("date", date);
            model.addAttribute("total", receiptDTO.getPayment().getTotal());
            return "receipt";
        }
        return "redirect:/";
    }

    @GetMapping("/main-menu")
    public String getPageMainMenu(HttpServletRequest request, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roles = authentication.getAuthorities().toString();
        if(roles.contains("ROLE_USER")) {
            Optional<Cookie> cookieUid = CookieUtil.readCookie(request, "uid");
            if(cookieUid.isPresent()) {
                String uid = cookieUid.get().getValue();
                Token token = tokenService.findById(uid);
                if(token != null) {
                    Bill bill = token.getBill();
                    if(bill != null) {
                        if(bill.getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) {
                            return "redirect:/payment";
                        }
                        else if(bill.getStatus().equalsIgnoreCase(ConstantUtil.VOID) || bill.getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
                            return "redirect:/";
                        }
                    }
                }
            }
            Optional<Cookie> cookieType = CookieUtil.readCookie(request, "type");
            cookieType.ifPresent(value -> model.addAttribute("type", value.getValue()));
        }
        return "main_menu";
    }

    @GetMapping("/staff/index")
    public String getPageIndexAdmin(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("role", authentication.getAuthorities().toString());
        return "staff_main_menu";
    }

    @GetMapping("/admin/manage-item")
    public String getPageManageItem(){
        return "manage_item";
    }

    @GetMapping("/staff/manage-table")
    public String getPageManageTable(){return "manage_table";}

    @GetMapping("/admin/create-item")
    public String getPageCreateItem(){
        return "create_item";
    }

    @GetMapping("/admin/create-option")
    public String getPageCreateOption(){
        return "create_option";
    }

    @GetMapping("/admin/create-option-group")
    public String getPageCreateOptionGroup(){
        return "create_option_group";
    }

    @GetMapping("/staff/kitchen")
    public String getPageKitchen(){
        return "kitchen_order";
    }

}
