package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/create-item")
public class CreateItemController {
    @GetMapping
    public String getPage(){
        return "create_item";
    }
}
