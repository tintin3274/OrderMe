package th.ku.orderme.controller.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.AddOptionalDTO;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.Optional;
import th.ku.orderme.model.Views;
import th.ku.orderme.service.OptionalService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/optional")
@RequiredArgsConstructor
public class OptionalController {
    private final OptionalService optionalService;


    @JsonView(Views.Detail.class)
    @GetMapping("/{id}")
    public Optional findById(@PathVariable int id) {
        return optionalService.findById(id);
    }

    @JsonView(Views.Overall.class)
    @GetMapping("/all")
    public List<Optional> findAll() {
        return optionalService.findAll();
    }

    @GetMapping("/item-of-id/{id}")
    public List<Item> findItemOfOptional(@PathVariable int id) {
        Optional optional = optionalService.findById(id);
        if(optional == null) return null;
        return optional.getItemList();
    }

    @PostMapping("/add")
    public Optional addOptional(@RequestBody AddOptionalDTO addOptionalDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(addOptionalDTO.getOptionGroup());
            Optional optional = mapper.readValue(jsonString, Optional.class);
            optional = optionalService.addOptional(optional, addOptionalDTO.getOptionId());
            return optional;
        }
        catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @PostMapping("/update")
    public Optional updateOptional(@RequestBody AddOptionalDTO addOptionalDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(addOptionalDTO.getOptionGroup());
            Optional optional = mapper.readValue(jsonString, Optional.class);
            optionalService.updateOptional(optional, addOptionalDTO.getOptionId());
            return optional;
        }
        catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public Optional deleteItem(@PathVariable int id) {
        return optionalService.deleteOptional(id);
    }

    @GetMapping("/using")
    public Map<Integer, Integer> optionalUsingCount() {
        return optionalService.optionalUsingCount();
    }

    @GetMapping("/item-using")
    public Map<Integer, Integer> itemOptionUsingCount() {
        return optionalService.itemOptionUsingCount();
    }
}
