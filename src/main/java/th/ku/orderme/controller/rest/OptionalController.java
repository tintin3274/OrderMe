package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ku.orderme.model.Optional;
import th.ku.orderme.service.OptionalService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/optional")
public class OptionalController {
    private final OptionalService optionalService;

    public OptionalController(OptionalService optionalService) {
        this.optionalService = optionalService;
    }

    @GetMapping
    public List<Optional> findAll() {
        return optionalService.findAll();
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
