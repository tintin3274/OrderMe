package th.ku.orderme.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ku.orderme.dto.BillDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.service.BillService;

import java.util.List;

@RestController
@RequestMapping("/api/bill")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;

    @GetMapping("/all")
    public List<Bill> findAll() {
        return billService.findAll();
    }

    @GetMapping("/{id}")
    public BillDTO getBillDTO(@PathVariable int id) {
        return billService.getBillDTO(id);
    }
}
