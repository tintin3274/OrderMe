package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage_item")
public class ManageItemController {
    @GetMapping
    public String getPage(){
        return "manage_item";
    }
}
