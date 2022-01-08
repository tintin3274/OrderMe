package th.ku.orderme.service;

import org.springframework.stereotype.Service;
import th.ku.orderme.model.Bill;
import th.ku.orderme.repository.BillRepository;

import java.util.List;

@Service
public class BillService {
    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<Bill> findAll() {
        return billRepository.findAll();
    }
}
