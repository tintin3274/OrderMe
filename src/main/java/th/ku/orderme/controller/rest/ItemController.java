package th.ku.orderme.controller.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.ku.orderme.dto.AddItemDTO;
import th.ku.orderme.dto.ItemDTO;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.Optional;
import th.ku.orderme.model.Views;
import th.ku.orderme.service.ItemService;
import th.ku.orderme.util.FileUploadUtil;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @JsonView(Views.Detail.class)
    @GetMapping("/{id}")
    public Item findById(@PathVariable int id) {
        return itemService.findById(id);
    }

    @JsonView(Views.Overall.class)
    @GetMapping("/all")
    public List<Item> findAll() {
        return itemService.findAll();
    }

    @JsonView(Views.Overall.class)
    @GetMapping("/food")
    public List<Item> findAllFood() {
        return itemService.findAllFood();
    }

    @GetMapping("/optional-of-id/{id}")
    public List<Optional> findOptionalOfItem(@PathVariable int id) {
        Item item = itemService.findById(id);
        if(item == null) return null;
        return item.getOptionalList();
    }

    @GetMapping("/list-category")
    public List<String> findAllFoodCategory() {
        return itemService.findAllFoodCategory();
    }

    @JsonView(Views.Overall.class)
    @GetMapping("/category/{category}")
    public List<Item> findItemByCategory(@PathVariable String category) {
        return itemService.findItemByCategory(category);
    }

    @PostMapping("/add")
    public Item addItem(@RequestBody AddItemDTO addItemDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(addItemDTO.getItem());
            Item item = mapper.readValue(jsonString, Item.class);
            item = itemService.addItem(item, addItemDTO.getOptionGroupId());
            return item;

        }
        catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @PostMapping("/add-with-image")
    public Item addItemWithImage(@RequestParam String addItemDTO, @RequestParam(value = "image", required = false) MultipartFile multipartFile) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AddItemDTO addItemDTOObj = mapper.readValue(addItemDTO, AddItemDTO.class);
            String jsonString = mapper.writeValueAsString(addItemDTOObj.getItem());
            Item item = mapper.readValue(jsonString, Item.class);

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

    @PostMapping("/update")
    public Item updateItem(@RequestBody ItemDTO itemDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(itemDTO);
            Item item = mapper.readValue(jsonString, Item.class);
            item = itemService.updateItem(item);
            return item;
        }
        catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @PostMapping("/update-image")
    public Item updateItemImage(@RequestParam int id, @RequestParam(value = "image") MultipartFile multipartFile) {
        try {
            Item item = itemService.findById(id);
            if(item == null) return null;

            if(multipartFile != null) {
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                item.setImage(fileName);

                String uploadDir = "images";
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }

            item = itemService.updateItem(item);
            return item;
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @PostMapping("/update-optional")
    public Item updateItemOptional(@RequestParam int id, @RequestParam List<Integer> optionGroupId) {
        return itemService.updateItemOptional(id, optionGroupId);
    }

    @DeleteMapping("/{id}")
    public Item deleteItem(@PathVariable int id) {
        return itemService.deleteItem(id);
    }
}
