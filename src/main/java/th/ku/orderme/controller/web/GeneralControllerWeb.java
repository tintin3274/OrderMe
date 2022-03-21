package th.ku.orderme.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.ReceiptDTO;
import th.ku.orderme.service.PaymentService;
import th.ku.orderme.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class GeneralControllerWeb {
    private final PaymentService paymentService;

    @GetMapping("/")
    public String main(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie1 = CookieUtil.createCookie("uid", null, 0, false, true, "/", request.getServerName());
        Cookie cookie2 = CookieUtil.createCookie("type", null, 0, false, true, "/", request.getServerName());
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        return "index";
    }

    @ResponseBody
    @GetMapping("/receipt/{ref1}")
    public ReceiptDTO getReceiptDTO(@PathVariable String ref1) {
        return paymentService.getReceiptDTO(ref1);
    }

    @GetMapping("/main-menu")
    public String getPageMainMenu(@CookieValue(name = "type") String type, Model model){
        model.addAttribute("type", type);
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
