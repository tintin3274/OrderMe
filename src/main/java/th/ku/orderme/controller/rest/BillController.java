package th.ku.orderme.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th.ku.orderme.model.Bill;
import th.ku.orderme.service.BillService;

import java.util.List;

@RestController
@RequestMapping("/api/order-me/bill")
public class BillController {
    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public List<Bill> findAll() {
        return billService.findAll();
    }

}
