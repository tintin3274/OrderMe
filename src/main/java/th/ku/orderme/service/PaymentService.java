package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.dto.BillDTO;
import th.ku.orderme.model.Bill;

@Service
@AllArgsConstructor
public class PaymentService {
    private final BillService billService;

    public void payBill(int billId) {
        Bill bill = billService.findById(billId);
        if(bill == null) return;
        if(!bill.getStatus().equalsIgnoreCase("OPEN")) return;

        BillDTO billDTO = billService.getBill(billId);
    }
}
