package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public List<Item> findDrink() {
        return itemService.findDrink();
    }
}
