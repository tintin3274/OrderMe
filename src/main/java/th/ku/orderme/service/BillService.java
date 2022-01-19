package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Bill;
import th.ku.orderme.repository.BillRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class BillService {
    private final BillRepository billRepository;

    public List<Bill> findAll() {
        return billRepository.findAll();
    }

    public Bill findById(int id) {
        return billRepository.findById(id).orElse(null);
    }

    public boolean existsById(int id) {
        return billRepository.existsById(id);
    }
}
