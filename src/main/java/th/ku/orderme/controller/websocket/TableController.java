package th.ku.orderme.controller.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import th.ku.orderme.dto.TableDTO;
import th.ku.orderme.model.Table;
import th.ku.orderme.service.TableService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TableController {
    private SimpMessagingTemplate template;
    private TableService tableService;

    @SubscribeMapping("/table-init")
    public List<TableDTO> sendTableDTOList() throws Exception {
        return converterTableListToTableDTOList(tableService.findAll());
    }

    @SendTo("/topic/table-update")
    public TableDTO sendTableDTO(TableDTO tableDTO) throws Exception {
        System.out.println(tableDTO);
        return tableDTO;
    }

    @ResponseBody
    @PostMapping("/api/table-update")
    public TableDTO loopTableDTO(TableDTO tableDTO) throws Exception {
        this.template.convertAndSend("/topic/table-update", tableDTO);
        return tableDTO;
    }

    private List<TableDTO> converterTableListToTableDTOList(List<Table> tableList) {
        List<TableDTO> tableDTOList = new ArrayList<>();
        for(Table table : tableList) {
            TableDTO tableDTO = new TableDTO();
            tableDTO.setId(table.getId());
            tableDTO.setAvailable(table.isAvailable());
            tableDTO.setBillId(table.getBill().getId());
            tableDTOList.add(tableDTO);
        }
        return tableDTOList;
    }

}
