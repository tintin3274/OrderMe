package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/main-menu")
public class MainMenuController {
    @GetMapping
    public String getpage(){return "main_menu";}
}
