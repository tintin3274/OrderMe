package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import th.ku.orderme.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class GeneralControllerWeb {

    @GetMapping("/")
    public String main(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie1 = CookieUtil.createCookie("uid", null, 0, false, true, "/", request.getServerName());
        Cookie cookie2 = CookieUtil.createCookie("type", null, 0, false, true, "/", request.getServerName());
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        return "index";
    }

    @GetMapping("/main-menu")
    public String getPageMainMenu(){return "main_menu";}

    @GetMapping("/admin/manage-item")
    public String getPageManageItem(){
        return "manage_item";
    }

    @GetMapping("/admin/manage-table")
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

}
