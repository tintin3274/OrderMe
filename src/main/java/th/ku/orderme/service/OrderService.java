package th.ku.orderme.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import th.ku.orderme.dto.CartDTO;
import th.ku.orderme.dto.OrderRequestDTO;
import th.ku.orderme.dto.SelectItemDTO;
import th.ku.orderme.model.*;
import th.ku.orderme.model.Optional;
import th.ku.orderme.repository.OrderRepository;
import th.ku.orderme.repository.SelectItemRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SelectItemRepository selectItemRepository;
    private final BillService billService;
    private final ItemService itemService;
    private final OptionalService optionalService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    public String order(CartDTO cartDTO, Bill bill) {
        try {
            if(!bill.getStatus().equalsIgnoreCase("OPEN")) throw new IllegalArgumentException("Bill is CLOSE");

            if(!validateAndUpdateItemQuantity(cartDTO.getOrderRequests())) throw new IllegalArgumentException("Something wrong! Cannot order");

            StringBuilder stringBuilder = new StringBuilder();
            LocalDateTime timestamp = LocalDateTime.now();
            stringBuilder.append("Bill ID: "+bill.getId()+"\n");
            stringBuilder.append("Timestamp: "+timestamp+"\n");
            stringBuilder.append("--------------------\n");
            double total = 0;
            for(OrderRequestDTO orderRequestDTO : cartDTO.getOrderRequests()) {
                Order order = new Order();
                order.setBill(bill);
                order.setItem(itemService.findById(orderRequestDTO.getItemId()));
                order.setQuantity(orderRequestDTO.getQuantity());
                order.setComment(orderRequestDTO.getComment());
                order.setStatus("ORDER");
                order.setTimestamp(timestamp);
                order = orderRepository.saveAndFlush(order);

                double amount = order.getItem().getPrice()*order.getQuantity();

                Map<Integer,Item> itemMap = itemService.getMapAllItemOfItemId(orderRequestDTO.getItemId());
                List<String> optionalItemName = new ArrayList<>();

                for(SelectItemDTO selectItemDTO : orderRequestDTO.getSelectItems()) {
                    int orderId = order.getId();
                    int optionalId = selectItemDTO.getOptionalId();
                    for(int itemId : selectItemDTO.getItemOptionalId()) {
                        SelectItem.SelectItemId selectItemId = new SelectItem.SelectItemId(orderId, itemId);
                        SelectItem selectItem = new SelectItem();
                        selectItem.setSelectItemId(selectItemId);
                        selectItem.setOptionalId(optionalId);
                        selectItem = selectItemRepository.saveAndFlush(selectItem);

                        amount += itemMap.get(selectItem.getSelectItemId().getItemId()).getPrice()*order.getQuantity();
                        optionalItemName.add(itemMap.get(selectItem.getSelectItemId().getItemId()).getName());
                    }
                }

                stringBuilder.append(order.getQuantity()+"x "+order.getItem().getName()+" "+amount+"\n");
                if(!optionalItemName.isEmpty()) stringBuilder.append("   "+String.join(", ", optionalItemName)+"\n");
                if(order.getComment() != null) stringBuilder.append("   "+order.getComment()+"\n");
                total += amount;
            }
            stringBuilder.append("--------------------\n");
            stringBuilder.append("Total: "+total);
            return stringBuilder.toString();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private boolean validateAndUpdateItemQuantity(List<OrderRequestDTO> orderRequestsDTO){
        try {
            Map<Integer,Integer> itemIdAndQuantity = new HashMap<>();
            Map<Integer,Item> itemMap = new HashMap<>();
            for(OrderRequestDTO orderRequestDTO : orderRequestsDTO) {
                if(orderRequestDTO.getItemId() < 1) return false;
                if(orderRequestDTO.getQuantity() < 1) return false;

                Item item = itemService.findById(orderRequestDTO.getItemId());
                if(item == null) return false;

                itemIdAndQuantity.put(item.getId(), orderRequestDTO.getQuantity());
                itemMap.putAll(itemService.getMapAllItemOfItemId(item.getId()));

                Map<Integer,Set<Integer>> map = itemService.getMapSetItemIdOfOptionalIdOfItemId(item.getId());

                for(SelectItemDTO selectItemDTO : orderRequestDTO.getSelectItems()) {
                    int optionalId = selectItemDTO.getOptionalId();
                    if(optionalId < 0) return false;
                    if(!map.containsKey(optionalId)) return false;
                    Set<Integer> set = map.get(optionalId);

                    Optional optional = optionalService.findById(optionalId);
                    int amountSelectItem = selectItemDTO.getItemOptionalId().size();
                    if(amountSelectItem < optional.getMin()) return false;
                    if(amountSelectItem > optional.getMax()) return false;

                    for(int itemOptionalId : selectItemDTO.getItemOptionalId()) {
                        if(!set.contains(itemOptionalId)) return false;

                        if(itemIdAndQuantity.containsKey(itemOptionalId)) {
                            itemIdAndQuantity.put(itemOptionalId, itemIdAndQuantity.get(itemOptionalId) + orderRequestDTO.getQuantity());
                        }
                        else {
                            itemIdAndQuantity.put(itemOptionalId, orderRequestDTO.getQuantity());
                        }
                    }
                }
            }
            for(int itemId : itemIdAndQuantity.keySet()) {
                Item itemCheck = itemMap.get(itemId);
                if(itemCheck.isCheckQuantity() && itemIdAndQuantity.get(itemId) > itemCheck.getQuantity()) return false;
            }

            for(int itemId : itemIdAndQuantity.keySet()) {
                Item itemCheck = itemMap.get(itemId);
                if(itemCheck.isCheckQuantity()) {
                    itemCheck.setQuantity(itemCheck.getQuantity() - itemIdAndQuantity.get(itemId));
                    itemService.saveAndFlush(itemCheck);
                }
            }
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
