package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import th.ku.orderme.util.ConstantUtil;
import th.ku.orderme.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class GeneralController {

    @GetMapping("/")
    public String main(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie1 = CookieUtil.createCookie("uid", null, 0, false, true, "/", request.getServerName());
        Cookie cookie2 = CookieUtil.createCookie("type", null, 0, false, true, "/", request.getServerName());
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        return "index";
    }

    @GetMapping("/payment-fail")
    public String paymentFail() {
        return "payment_fail";
    }

    @GetMapping("/admin/manage-table")
    public String getPageManageTable(){return "manage_table";}
}
