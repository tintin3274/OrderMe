package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.repository.TableRepository;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

}
