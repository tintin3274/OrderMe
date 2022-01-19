package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.repository.TableRepository;

@Service
@AllArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

}
