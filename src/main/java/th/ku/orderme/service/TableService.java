package th.ku.orderme.service;

import org.springframework.stereotype.Service;
import th.ku.orderme.repository.TableRepository;

@Service
public class TableService {
    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }
}
