package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Table;
import th.ku.orderme.repository.TableRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

    public Table findById(int id) {
        return tableRepository.findById(id).orElse(null);
    }

    public List<Table> findAll() {
        return tableRepository.findAll();
    }

    public boolean existsById(int id) {
        return tableRepository.existsById(id);
    }

    public boolean tableIsAvailable(int id) {
        if(existsById(id)) {
            Table table = findById(id);
            return table.isAvailable();
        }
        return false;
    }

    public boolean mappingTableToBill(int id, Bill bill) {
        try {
            if(existsById(id)) {
                Table table = findById(id);
                if(table.isAvailable()) {
                    table.setAvailable(false);
                    table.setBill(bill);
                    tableRepository.saveAndFlush(table);
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

}
