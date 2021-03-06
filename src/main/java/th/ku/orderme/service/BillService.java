package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import th.ku.orderme.dto.BillDTO;
import th.ku.orderme.dto.OrderDTO;
import th.ku.orderme.dto.UpdateBillOrderStatusMessage;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Order;
import th.ku.orderme.repository.BillRepository;
import th.ku.orderme.util.ConstantUtil;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BillService {
    private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(2, new DaemonThreadFactory());
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final BillRepository billRepository;
    private final TokenService tokenService;
    private final OrderService orderService;
    private final SimpMessagingTemplate template;

    public List<Bill> findAll() {
        return billRepository.findAll();
    }

    public Bill findById(int id) {
        return billRepository.findById(id).orElse(null);
    }

    public Bill createBill(int person) {
        Bill bill = new Bill();
        bill.setPerson(person);
        bill.setType(ConstantUtil.DINE_IN);
        bill.setStatus(ConstantUtil.OPEN);
        bill.setTimestamp(LocalDateTime.now());
        bill = billRepository.saveAndFlush(bill);
        return bill;
    }

    public Bill createBillTakeOut(String tokenId) {
        Bill bill = new Bill();
        bill.setPerson(1);
        bill.setType(ConstantUtil.TAKE_OUT);
        bill.setStatus(ConstantUtil.OPEN);
        bill.setTimestamp(LocalDateTime.now());
        bill = billRepository.saveAndFlush(bill);

        tokenService.mappingTokenToBill(tokenId, bill);
        template.convertAndSend("/topic/take-out/new", bill.getId());
        UpdateBillOrderStatusMessage updateBillOrderStatusMessage = new UpdateBillOrderStatusMessage(bill.getId(), ConstantUtil.PENDING);
        template.convertAndSend("/topic/bill/status/update", updateBillOrderStatusMessage);
        return bill;
    }

    public BillDTO getBillDTO(int id) {
        Bill bill = findById(id);
        if(bill == null) return null;

        double subTotal = 0;
        List<OrderDTO> orderDTOList = new ArrayList<>();

        for(Order order : bill.getOrderList()) {
            if(order.getStatus().equals(ConstantUtil.CANCEL)) continue;
            OrderDTO orderDTO = orderService.convertOrderToOrderDTO(order);
            subTotal += orderDTO.getAmount();
            orderDTOList.add(orderDTO);
        }

        BillDTO billDTO = new BillDTO();
        billDTO.setBillId(bill.getId());
        billDTO.setPerson(bill.getPerson());
        billDTO.setType(bill.getType());
        billDTO.setTimestamp(bill.getTimestamp());
        billDTO.setOrders(orderDTOList);
        billDTO.setSubTotal(Double.parseDouble(df.format(subTotal)));
        return billDTO;
    }

    public void setStatusPaymentBill(int id) {
        Bill bill = findById(id);
        if(bill == null) return;
        if(bill.getStatus().equalsIgnoreCase(ConstantUtil.OPEN)) {
            bill.setStatus(ConstantUtil.PAYMENT);
            billRepository.saveAndFlush(bill);
        }
    }

    public void setStatusCloseBill(int id) {
        Bill bill = findById(id);
        if(bill == null) return;
        bill.setStatus(ConstantUtil.CLOSE);
        billRepository.saveAndFlush(bill);
    }

    public void cancelBill(int id) {
        Bill bill = findById(id);
        if(bill == null) return;
        if(!bill.getStatus().equalsIgnoreCase(ConstantUtil.CLOSE)) {
            bill.setStatus(ConstantUtil.VOID);
            bill = billRepository.saveAndFlush(bill);
            orderService.cancelAllOrderOfBill(bill.getId());
            orderService.checkAllOrderOfBillComplete(bill.getId());
        }
    }

    public void autoCancelBill(int id) {
        Runnable task = () -> cancelBill(id);
        ses.schedule(task, 20, TimeUnit.MINUTES);
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    }
}
