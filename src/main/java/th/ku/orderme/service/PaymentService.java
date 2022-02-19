package th.ku.orderme.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import th.ku.orderme.dto.BillDTO;
import th.ku.orderme.dto.PaymentDTO;
import th.ku.orderme.dto.ReceiptDTO;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Payment;
import th.ku.orderme.repository.PaymentRepository;
import th.ku.orderme.util.ConstantUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final Gson gson = new Gson();

    private final PaymentRepository paymentRepository;
    private final BillService billService;

    @Value("${scb.simulator.orderme.ref1Prefix}")
    private String ref1Prefix;

    @Value("${scb.simulator.orderme.ref2Prefix}")
    private String ref2Prefix;

    public Payment payBill(int billId) {
        Bill bill = billService.findById(billId);
        if(bill == null) return null;
        if(!bill.getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) return null;

        BillDTO billDTO = billService.getBillDTO(billId);
        double subTotal = billDTO.getSubTotal();
        if(subTotal <= 0) return null;

        Payment payment = paymentRepository.findByBill_Id(billId);
        if(payment == null) {
            LocalDateTime localDateTime = LocalDateTime.now();
            String ymd = localDateTime.format(dtf);

            payment = new Payment();
            payment.setBill(bill);
            payment.setRef1(ref1Prefix+ymd+bill.getId());
            payment.setRef2(ref2Prefix+ymd+bill.getId());
        }
        else {
            payment.setRef3(null);
            payment.setGenerateInfo(null);
            payment.setConfirmInfo(null);
        }

        df.setRoundingMode(RoundingMode.DOWN);
        double total = Double.parseDouble(df.format(subTotal));
        payment.setStatus(ConstantUtil.UNPAID);
        payment.setTotal(total);

        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setBill(billDTO);
        payment.setReceipt(gson.toJson(receiptDTO));

        return payment;
    }

    public void createReceipt(String ref1) {
        Payment payment = paymentRepository.findByRef1(ref1);
        if(payment == null) return;

        if(payment.getStatus().equalsIgnoreCase(ConstantUtil.PAID) && payment.getBill().getStatus().equalsIgnoreCase(ConstantUtil.PAYMENT)) {
            billService.setStatusCloseBill(payment.getBill().getId());

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setRef1(payment.getRef1());
            paymentDTO.setChannel(payment.getChannel());
            paymentDTO.setCreatedTimestamp(payment.getCreatedTimestamp());
            paymentDTO.setUpdatedTimestamp(payment.getUpdatedTimestamp());
            paymentDTO.setTotal(payment.getTotal());

            ReceiptDTO receiptDTO = gson.fromJson(payment.getReceipt(), ReceiptDTO.class);
            receiptDTO.setPayment(paymentDTO);

            payment.setReceipt(gson.toJson(receiptDTO));
            paymentRepository.saveAndFlush(payment);
        }
    }

    public ReceiptDTO getReceiptDTO(String ref1) {
        Payment payment = paymentRepository.findByRef1(ref1);
        if(payment == null) return null;
        else if(payment.getStatus().equalsIgnoreCase(ConstantUtil.PAID)) {
            return gson.fromJson(gson.toJson(payment.getReceipt()), ReceiptDTO.class);
        }
        return null;
    }
}
