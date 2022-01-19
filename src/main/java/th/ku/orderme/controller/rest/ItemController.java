package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import th.ku.orderme.model.Item;
import th.ku.orderme.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/order-me/item")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> findAllFood() {
        return itemService.findAllFood();
    }

    @PostMapping("/add")
    public Item addItem(@RequestBody Item item) {
        System.out.println(item);
        return item;
    }

//    @GetMapping
//    public RedirectView test() {
//        RedirectView redirectView = new RedirectView();
//        redirectView.setUrl("scbeasysim://purchase/783d69e5-fc45-4076-9f9e-bbbbe7aa4741");
//        return redirectView;
//    }
}
