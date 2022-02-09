package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/option-menu")
public class OptionMenuController {
    @GetMapping
    public String getpage(){return "option_menu";}
}
