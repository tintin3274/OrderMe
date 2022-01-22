package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.dto.BillDTO;
import th.ku.orderme.dto.OrderDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Item;
import th.ku.orderme.model.Order;
import th.ku.orderme.model.SelectItem;
import th.ku.orderme.repository.BillRepository;
import th.ku.orderme.repository.SelectItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class BillService {
    private final BillRepository billRepository;
    private final ItemService itemService;
    private final SelectItemRepository selectItemRepository;

    public List<Bill> findAll() {
        return billRepository.findAll();
    }

    public Bill findById(int id) {
        return billRepository.findById(id).orElse(null);
    }

    public boolean existsById(int id) {
        return billRepository.existsById(id);
    }

    public BillDTO getBill(int id) {
        Bill bill = findById(id);
        if(bill == null) return null;

        double total = 0;
        List<OrderDTO> orderDTOList = new ArrayList<>();

        for(Order order : bill.getOrderList()) {
            if(order.getStatus().equals("CANCEL")) continue;

            Item item = order.getItem();
            double price = item.getPrice()*order.getQuantity();

            Map<Integer, Item> itemMap = itemService.getMapAllItemOfItemId(item.getId());
            List<String> option = new ArrayList<>();

            List<SelectItem> selectItemList = selectItemRepository.findSelectItemsBySelectItemId_OrderId(order.getId());
            for(SelectItem selectItem : selectItemList) {
                Item itemOption = itemMap.get(selectItem.getSelectItemId().getItemId());
                option.add(itemOption.getName());
                price += itemOption.getPrice()*order.getQuantity();
            }

            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setName(item.getName());
            orderDTO.setOption(String.join(", ", option));
            orderDTO.setQuantity(order.getQuantity());
            orderDTO.setPrice(price);
            orderDTO.setComment(order.getComment());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setTimestamp(order.getTimestamp());

            total += price;
            orderDTOList.add(orderDTO);
        }

        BillDTO billDTO = new BillDTO();
        billDTO.setId(bill.getId());
        billDTO.setPerson(bill.getPerson());
        billDTO.setType(bill.getType());
        billDTO.setStatus(bill.getStatus());
        billDTO.setTimestamp(bill.getTimestamp());
        billDTO.setOrders(orderDTOList);
        billDTO.setTotal(total);
        return billDTO;
    }
}
