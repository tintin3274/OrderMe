package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/create-option-group")
public class CreateOptionGroupController {
    @GetMapping
    public String getPage(){
        return "create_option_group";
    }
}
