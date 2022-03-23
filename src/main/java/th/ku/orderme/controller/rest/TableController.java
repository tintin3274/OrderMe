package th.ku.orderme.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.dto.TableDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Table;
import th.ku.orderme.model.Token;
import th.ku.orderme.service.BillService;
import th.ku.orderme.service.TableService;
import th.ku.orderme.service.TokenService;

import java.util.List;

@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
public class TableController {
    @Autowired
    private final SimpMessagingTemplate template;
    private final BillService billService;
    private final TableService tableService;
    private final TokenService tokenService;

    @GetMapping("/all")
    @SubscribeMapping("/table/all")
    public List<TableDTO> getAllTableDTOList() {
        return tableService.findAllTableDTO();
    }

    @GetMapping("/all-id")
    public List<Integer> getAllId() {
        return tableService.getAllId();
    }

    @PostMapping("/open")
    public TableDTO openTableDineIn(@RequestParam int id, @RequestParam int person) {
        if(tableService.tableIsAvailable(id) && person >= 1) {
            Bill bill = billService.createBill(person);
            Token token = tokenService.mappingNewTokenToBill(bill);
            Table table  = tableService.mappingTableToBill(id, bill);

            TableDTO tableDTO = new TableDTO();
            tableDTO.setId(table.getId());
            tableDTO.setAvailable(table.isAvailable());
            tableDTO.setBillId(bill.getId());
            tableDTO.setToken(token.getId());
            tableDTO.setPaid(false);
            template.convertAndSend("/topic/table/update", tableDTO);
            return tableDTO;
        }
        return null;
    }

    @PostMapping("/update")
    public TableDTO sendTableDTOUpdate(@RequestBody TableDTO tableDTO) {
        template.convertAndSend("/topic/table/update", tableDTO);
        return tableDTO;
    }

    @GetMapping("/create/{id}")
    public Table createTable(@PathVariable int id) {
        return tableService.createTable(id);
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable int id) {
        return tableService.deleteTable(id);
    }
}
