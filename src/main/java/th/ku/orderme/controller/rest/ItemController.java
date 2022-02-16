package th.ku.orderme.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.ku.orderme.dto.AddItemDTO;
import th.ku.orderme.dto.ItemDTO;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.Optional;
import th.ku.orderme.service.ItemService;
import th.ku.orderme.util.FileUploadUtil;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> findAllFood() {
        return itemService.findAllFood();
    }

    @GetMapping("/{id}")
    public Item findItem(@PathVariable int id) {
        return itemService.findById(id);
    }

    @GetMapping("/optional-of-id/{id}")
    public List<Optional> findOptionalOfItem(@PathVariable int id) {
        Item item = itemService.findById(id);
        if(item == null) return null;
        return item.getOptionalList();
    }

    @GetMapping("/category")
    public List<String> findAllFoodCategory() {
        return itemService.findAllFoodCategory();
    }

    @GetMapping("/category/{category}")
    public List<Item> findItemByCategory(@PathVariable String category) {
        return itemService.findItemByCategory(category);
    }

    @PostMapping("/add")
    public Item addItem(@RequestBody AddItemDTO addItemDTO) {
        Item item = convertItemDTOToItem(addItemDTO.getItem());
        item = itemService.addItem(item, addItemDTO.getOptionGroupId());
        return item;
    }

    @PostMapping("/add-with-image")
    public Item addItemWithImage(@RequestParam String addItemDTO, @RequestParam(value = "image", required = false) MultipartFile multipartFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AddItemDTO addItemDTOObj = objectMapper.readValue(addItemDTO, AddItemDTO.class);
            Item item = convertItemDTOToItem(addItemDTOObj.getItem());

            if(multipartFile != null) {
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                item.setImage(fileName);

                String uploadDir = "images";
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }

            item = itemService.addItem(item, addItemDTOObj.getOptionGroupId());
            return item;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Item convertItemDTOToItem(ItemDTO itemDTO) {
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setCategory(itemDTO.getCategory());
        item.setPrice(itemDTO.getPrice());
        item.setQuantity(itemDTO.getQuantity());
        item.setCheckQuantity(itemDTO.isCheckQuantity());
        item.setDisplay(itemDTO.isDisplay());
        return item;
    }
}
