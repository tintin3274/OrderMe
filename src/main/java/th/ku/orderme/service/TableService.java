package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import th.ku.orderme.dto.TableDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Table;
import th.ku.orderme.model.Token;
import th.ku.orderme.repository.TableRepository;
import th.ku.orderme.util.ConstantUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;
    private final TokenService tokenService;
    private final SimpMessagingTemplate template;

    public Table findById(int id) {
        return tableRepository.findById(id).orElse(null);
    }

    public List<Table> findAll() {
        return tableRepository.findAll();
    }

    public Table createTable(int id) {
        Table table = new Table(id, true, null);
        return tableRepository.saveAndFlush(table);
    }

    public String deleteTable(int id) {
        if(tableRepository.existsById(id)) {
            tableRepository.deleteById(id);
            return "SUCCESS: Delete table id - " + id;
        }
        return "FAIL: Not found table id - " + id;
    }

    public List<Integer> getAllId() {
        return tableRepository.getAllId();
    }

    public boolean tableIsAvailable(int id) {
        Table table = findById(id);
        if(table == null) return false;
        return table.isAvailable();
    }

    public Table mappingTableToBill(int id, Bill bill) {
        Table table = findById(id);
        if(table == null) return null;
        if(table.isAvailable()) {
            table.setAvailable(false);
            table.setBill(bill);
            return tableRepository.saveAndFlush(table);
        }
        return null;
    }

    public boolean clearTableOfBill(int billId) {
        Table table = tableRepository.findByBill_Id(billId);
        if(table == null) return false;
        table.setAvailable(true);
        table.setBill(null);
        table = tableRepository.saveAndFlush(table);
        template.convertAndSend("/topic/table/update", converterTableToTableDTO(table));
        return true;
    }

    public List<TableDTO> findAllTableDTO() {
        return converterTableListToTableDTOList(findAll());
    }

    private TableDTO converterTableToTableDTO(Table table) {
        TableDTO tableDTO = new TableDTO();
        tableDTO.setId(table.getId());
        tableDTO.setAvailable(table.isAvailable());
        if(table.getBill() != null) {
            tableDTO.setBillId(table.getBill().getId());
            if(table.getBill().getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
                tableDTO.setPaid(true);
            }
            Token token = tokenService.findByBillId(table.getBill().getId());
            if(token != null) {
                tableDTO.setToken(token.getId());
            }
        }
        return tableDTO;
    }

    private List<TableDTO> converterTableListToTableDTOList(List<Table> tableList) {
        List<TableDTO> tableDTOList = new ArrayList<>();
        for(Table table : tableList) {
            tableDTOList.add(converterTableToTableDTO(table));
        }
        return tableDTOList;
    }

    public List<Integer> getAllBillId() {
        List<Integer> billIds = tableRepository.getAllBillId();
        billIds.removeAll(Collections.singleton(null));
        return billIds;
    }
}
