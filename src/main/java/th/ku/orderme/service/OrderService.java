package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import th.ku.orderme.dto.*;
import th.ku.orderme.model.*;
import th.ku.orderme.model.Optional;
import th.ku.orderme.repository.*;
import th.ku.orderme.util.ConstantUtil;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final SelectItemRepository selectItemRepository;
    private final BillRepository billRepository;
    private final TableRepository tableRepository;
    private final ItemService itemService;
    private final OptionalService optionalService;
    private final TableService tableService;
    private final SimpMessagingTemplate template;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    public String order(CartDTO cartDTO, Bill bill) {
        try {
            if(!bill.getStatus().equalsIgnoreCase(ConstantUtil.OPEN)) throw new IllegalArgumentException("Bill is not OPEN - "+bill.getId());

            if(!validateAndUpdateItemQuantity(cartDTO.getOrderRequests())) throw new IllegalArgumentException("Something wrong! Cannot order");

            StringBuilder stringBuilder = new StringBuilder();
            LocalDateTime timestamp = LocalDateTime.now();
            stringBuilder.append("Bill ID: "+bill.getId()+"\n");
            stringBuilder.append("Timestamp: "+timestamp+"\n");
            stringBuilder.append("--------------------\n");
            double total = 0;
            String status = ConstantUtil.ORDER;
            if(bill.getType().equals(ConstantUtil.TAKE_OUT)) status = ConstantUtil.PENDING;
            for(OrderRequestDTO orderRequestDTO : cartDTO.getOrderRequests()) {
                Order order = new Order();
                order.setBill(bill);
                order.setItem(itemService.findById(orderRequestDTO.getItemId()));
                order.setQuantity(orderRequestDTO.getQuantity());
                order.setComment(orderRequestDTO.getComment());
                order.setStatus(status);
                order.setTimestamp(timestamp);
                order = orderRepository.saveAndFlush(order);

                double amount = order.getItem().getPrice()*order.getQuantity();

                Map<Integer,Item> itemMap = itemService.getMapAllItemOfItemId(orderRequestDTO.getItemId());
                List<String> optionalItemName = new ArrayList<>();

                for(SelectItemDTO selectItemDTO : orderRequestDTO.getSelectItems()) {
                    List<SelectItem> selectItemList = new ArrayList<>();
                    int orderId = order.getId();
                    int optionalId = selectItemDTO.getOptionalId();
                    for(int itemId : selectItemDTO.getItemOptionalId()) {
                        SelectItem.SelectItemId selectItemId = new SelectItem.SelectItemId(orderId, itemId);
                        SelectItem selectItem = new SelectItem();
                        selectItem.setSelectItemId(selectItemId);
                        selectItem.setOptionalId(optionalId);
                        selectItemList.add(selectItem);

                        amount += itemMap.get(selectItem.getSelectItemId().getItemId()).getPrice()*order.getQuantity();
                        optionalItemName.add(itemMap.get(selectItem.getSelectItemId().getItemId()).getName());
                    }
                    selectItemRepository.saveAllAndFlush(selectItemList);
                }

                stringBuilder.append(order.getQuantity()+"x "+order.getItem().getName()+" "+amount+"\n");
                if(!optionalItemName.isEmpty()) stringBuilder.append("   "+String.join(", ", optionalItemName)+"\n");
                if(order.getComment() != null) stringBuilder.append("   "+order.getComment()+"\n");
                total += amount;

                template.convertAndSend("/topic/order/update", getUpdateOrderDTO(order.getId()));
            }
            stringBuilder.append("--------------------\n");
            stringBuilder.append("Total: "+total);

            sendUpdateBillOrderStatusMessage(bill.getId());
            return stringBuilder.toString();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    @Transactional
    public boolean validateAndUpdateItemQuantity(List<OrderRequestDTO> orderRequestsDTO){
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

            List<Item> itemListUpdate = new ArrayList<>();
            for(int itemId : itemIdAndQuantity.keySet()) {
                Item itemCheck = itemMap.get(itemId);
                if(itemCheck.isCheckQuantity()) {
                    if(itemIdAndQuantity.get(itemId) > itemCheck.getQuantity()) return false;
                    itemCheck.setQuantity(itemCheck.getQuantity() - itemIdAndQuantity.get(itemId));
                    itemListUpdate.add(itemCheck);
                }
            }
            if(!itemListUpdate.isEmpty()) {
                itemRepository.saveAllAndFlush(itemListUpdate);
            }
            return true;
        } catch (NullPointerException | ObjectOptimisticLockingFailureException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Transactional
    void cancel(int id) {
        try {
            Order order = findById(id);
            if(order == null) return;
            if(!order.getStatus().equalsIgnoreCase(ConstantUtil.COMPLETE)) {
                order.setStatus(ConstantUtil.CANCEL);
                orderRepository.save(order);

                Item item = order.getItem();
                int quantity = order.getQuantity();

                if(item.isCheckQuantity()) {
                    item.setQuantity(item.getQuantity() + quantity);
                    itemRepository.saveAndFlush(item);
                }

                List<SelectItem> selectItemList = selectItemRepository.findSelectItemsBySelectItemId_OrderId(id);
                for(SelectItem selectItem : selectItemList) {
                    Item itemOption = itemService.findById(selectItem.getSelectItemId().getItemId());
                    if(itemOption.isCheckQuantity()) {
                        itemOption.setQuantity(itemOption.getQuantity() + quantity);
                        itemRepository.saveAndFlush(itemOption);
                    }
                }
            }
        }
        catch (NullPointerException | ObjectOptimisticLockingFailureException e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void cancelAllOrderOfBill(int billId) {
        try {
            HashMap<Integer, Integer> itemsUpdateQuantity = new HashMap<>();
            List<Order> orderList = orderRepository.findAllByBill_Id(billId);
            for(Order order : orderList) {
                if(!order.getStatus().equalsIgnoreCase(ConstantUtil.COMPLETE)) {
                    order.setStatus(ConstantUtil.CANCEL);
                    Item item = order.getItem();
                    int quantity = order.getQuantity();
                    if(item.isCheckQuantity()) {
                        if(itemsUpdateQuantity.containsKey(item.getId())) {
                            itemsUpdateQuantity.put(item.getId(), itemsUpdateQuantity.get(item.getId())+quantity);
                        }
                        else {
                            itemsUpdateQuantity.put(item.getId(), quantity);
                        }
                    }

                    List<SelectItem> selectItemList = selectItemRepository.findSelectItemsBySelectItemId_OrderId(order.getId());
                    for(SelectItem selectItem : selectItemList) {
                        Item itemOption = itemService.findById(selectItem.getSelectItemId().getItemId());
                        if(itemOption.isCheckQuantity()) {
                            if(itemsUpdateQuantity.containsKey(itemOption.getId())) {
                                itemsUpdateQuantity.put(itemOption.getId(), itemsUpdateQuantity.get(itemOption.getId())+quantity);
                            }
                            else {
                                itemsUpdateQuantity.put(itemOption.getId(), quantity);
                            }
                        }
                    }

                    List<Item> itemList = itemRepository.findAllById(itemsUpdateQuantity.keySet());
                    for(Item itemUpdate : itemList) {
                        int addQuantity = itemsUpdateQuantity.get(itemUpdate.getId());
                        itemUpdate.setQuantity(itemUpdate.getQuantity() + addQuantity);
                    }
                    itemRepository.saveAllAndFlush(itemList);
                }
            }
        }
        catch (NullPointerException | ObjectOptimisticLockingFailureException e) {
            log.error(e.getMessage());
        }
    }

    public OrderDTO convertOrderToOrderDTO(Order order) {
        try {
            Item item = order.getItem();
            double price = item.getPrice();

            Map<Integer, Item> itemMap = itemService.getMapAllItemOfItemId(item.getId());
            List<String> option = new ArrayList<>();

            List<SelectItem> selectItemList = selectItemRepository.findSelectItemsBySelectItemId_OrderId(order.getId());
            for(SelectItem selectItem : selectItemList) {
                Item itemOption = itemMap.get(selectItem.getSelectItemId().getItemId());
                option.add(itemOption.getName());
                price += itemOption.getPrice();
            }

            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setName(item.getName());
            orderDTO.setOption(String.join(", ", option));
            orderDTO.setQuantity(order.getQuantity());
            orderDTO.setPrice(price);
            orderDTO.setAmount(price*order.getQuantity());
            orderDTO.setComment(order.getComment());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setTimestamp(order.getTimestamp());
            return orderDTO;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public UpdateOrderDTO getUpdateOrderDTO(int orderId) {
        Order order = findById(orderId);
        if(order == null) return null;
        UpdateOrderDTO updateOrderDTO = new UpdateOrderDTO();
        updateOrderDTO.setBillId(order.getBill().getId());
        updateOrderDTO.setType(order.getBill().getType());
        updateOrderDTO.setOrder(convertOrderToOrderDTO(order));
        updateOrderDTO.setTableId(0);
        if(order.getBill().getType().equalsIgnoreCase(ConstantUtil.DINE_IN)) {
            Table table = tableRepository.findByBill_Id(order.getBill().getId());
            if(table != null) {
                updateOrderDTO.setTableId(table.getId());
            }
        }
        return updateOrderDTO;
    }

    public void changePendingToOrder(Bill bill) {
        if(bill == null) return;
        List<Order> orderList = bill.getOrderList();
        for(Order order : orderList) {
            order.setStatus(ConstantUtil.ORDER);
        }
        orderList = orderRepository.saveAllAndFlush(orderList);

        for(Order order : orderList) {
            template.convertAndSend("/topic/order/update", getUpdateOrderDTO(order.getId()));
        }
    }

    public UpdateOrderDTO changeOrderToCooking(int id) {
        Order order = findById(id);
        if(order == null || !order.getStatus().equalsIgnoreCase(ConstantUtil.ORDER)) return null;
        order.setStatus(ConstantUtil.COOKING);
        orderRepository.saveAndFlush(order);
        UpdateOrderDTO updateOrderDTO = getUpdateOrderDTO(id);
        template.convertAndSend("/topic/order/update", updateOrderDTO);
        sendUpdateBillOrderStatusMessage(order.getBill().getId());
        return updateOrderDTO;
    }

    public UpdateOrderDTO changeCookingToServing(int id) {
        Order order = findById(id);
        if(order == null || !order.getStatus().equalsIgnoreCase(ConstantUtil.COOKING)) return null;
        order.setStatus(ConstantUtil.SERVING);
        orderRepository.saveAndFlush(order);
        UpdateOrderDTO updateOrderDTO = getUpdateOrderDTO(id);
        template.convertAndSend("/topic/order/update", updateOrderDTO);
        sendUpdateBillOrderStatusMessage(order.getBill().getId());
        return updateOrderDTO;
    }

    public UpdateOrderDTO changeToComplete(int id) {
        Order order = findById(id);
        if(order == null) return null;
        String status = order.getStatus();
        if(status.equalsIgnoreCase(ConstantUtil.CANCEL) || status.equalsIgnoreCase(ConstantUtil.PENDING) || status.equalsIgnoreCase(ConstantUtil.COMPLETE)) return null;
        order.setStatus(ConstantUtil.COMPLETE);
        orderRepository.saveAndFlush(order);
        UpdateOrderDTO updateOrderDTO = getUpdateOrderDTO(id);
        template.convertAndSend("/topic/order/update", updateOrderDTO);

        checkAllOrderOfBillComplete(order.getBill().getId());
        return updateOrderDTO;
    }

    public UpdateOrderDTO changeToCancel(int id) {
        Order order = findById(id);
        if(order == null || !order.getStatus().equalsIgnoreCase(ConstantUtil.CANCEL)) return null;
        cancel(id);
        UpdateOrderDTO updateOrderDTO = getUpdateOrderDTO(id);
        template.convertAndSend("/topic/order/update", updateOrderDTO);

        checkAllOrderOfBillComplete(order.getBill().getId());
        return getUpdateOrderDTO(id);
    }

    public List<UpdateOrderDTO> getDoingOrder() {
        List<Integer> orderIds = orderRepository.getAllOrderIdNotPendingAndCancelAndComplete();
        List<UpdateOrderDTO> updateOrderDTOList = new ArrayList<>();
        for(int id : orderIds) {
            updateOrderDTOList.add(getUpdateOrderDTO(id));
        }
        return updateOrderDTOList;
    }

    public boolean allOrderOfBillComplete(int billId) {
        List<Integer> billIds = orderRepository.getAllBillIdOfOrderNotCancelAndComplete();
        return !billIds.contains(billId);
    }

    public void checkAllOrderOfBillComplete(int billId) {
        Bill bill = billRepository.findById(billId).orElse(null);
        if(bill == null) return;
        if(bill.getType().equalsIgnoreCase(ConstantUtil.TAKE_OUT)) {
            if(allOrderOfBillComplete(bill.getId())) {
                template.convertAndSend("/topic/take-out/complete", bill.getId());
            }
            else {
                sendUpdateBillOrderStatusMessage(bill.getId());
            }
        }
        else if(bill.getType().equalsIgnoreCase(ConstantUtil.DINE_IN)) {
            if(bill.getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
                if(allOrderOfBillComplete(bill.getId())) {
                    tableService.clearTableOfBill(bill.getId());
                }
                else {
                    sendUpdateBillOrderStatusMessage(bill.getId());
                }
            }
            else {
                sendUpdateBillOrderStatusMessage(bill.getId());
            }
        }
    }

    public List<Integer> getAllBillIdTakeOutOfOrderNotCancelAndComplete() {
        List<Integer> allBillIdTakeOut = billRepository.getAllIdByTypeEqual(ConstantUtil.TAKE_OUT);
        List<Integer> allBillIdOfOrderProcess = orderRepository.getAllBillIdOfOrderNotCancelAndComplete();
        Set<Integer> result = allBillIdTakeOut.stream()
                .distinct()
                .filter(allBillIdOfOrderProcess::contains)
                .collect(Collectors.toSet());
        List<Integer> resultList = new ArrayList<>(result);
        Collections.sort(resultList);
        return resultList;
    }

    public List<String> getAllOrderStatusOfBillId(int billId) {
        return orderRepository.getAllOrderStatusOfBillId(billId);
    }

    public String getHighPriorityStatusOfAllOrderOfBillId(int billId) {
        List<String> statuses = getAllOrderStatusOfBillId(billId);
        if(statuses.isEmpty()) {
            Bill bill = billRepository.findById(billId).orElse(null);
            if(bill != null) {
                if(bill.getType().equalsIgnoreCase(ConstantUtil.TAKE_OUT)) {
                    return ConstantUtil.PENDING;
                }
            }
            return ConstantUtil.COMPLETE;
        }
        else if(statuses.contains(ConstantUtil.SERVING)) return ConstantUtil.SERVING;
        else if(statuses.contains(ConstantUtil.COOKING)) return ConstantUtil.COOKING;
        else if(statuses.contains(ConstantUtil.ORDER)) return ConstantUtil.ORDER;
        else if(statuses.contains(ConstantUtil.PENDING)) return ConstantUtil.PENDING;
        else return ConstantUtil.COMPLETE;
    }

    public UpdateBillOrderStatusMessage getUpdateBillOrderStatusMessage(int billId) {
        String status = getHighPriorityStatusOfAllOrderOfBillId(billId);
        return new UpdateBillOrderStatusMessage(billId, status);
    }

    public void sendUpdateBillOrderStatusMessage(int billId) {
        template.convertAndSend("/topic/bill/status/update", getUpdateBillOrderStatusMessage(billId));
    }

    public List<UpdateBillOrderStatusMessage> getAllCurrentBillOrderStatus() {
        List<Integer> billIds = new ArrayList<>();
        billIds.addAll(tableService.getAllBillId());
        billIds.addAll(getAllBillIdTakeOutOfOrderNotCancelAndComplete());

        List<UpdateBillOrderStatusMessage> updateBillOrderStatusMessageList = new ArrayList<>();
        for(int billId : billIds) {
            updateBillOrderStatusMessageList.add(getUpdateBillOrderStatusMessage(billId));
        }
        return updateBillOrderStatusMessageList;
    }
}