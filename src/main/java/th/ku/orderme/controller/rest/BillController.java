package th.ku.orderme.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.BillDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Token;
import th.ku.orderme.service.BillService;
import th.ku.orderme.service.TokenService;

import java.util.List;

@RestController
@RequestMapping("/api/bill")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;
    private final TokenService tokenService;

    @GetMapping("/all")
    public List<Bill> findAll() {
        return billService.findAll();
    }

    @GetMapping("/{id}")
    public BillDTO getBillDTO(@PathVariable int id) {
        return billService.getBillDTO(id);
    }

    @GetMapping("/my-bill")
    public BillDTO getBillDTO(@CookieValue(name = "uid") String uid) {
        Token token = tokenService.findById(uid);
        if(token == null) return null;
        Bill bill = token.getBill();
        if(bill == null) return null;
        return billService.getBillDTO(bill.getId());
    }
}
