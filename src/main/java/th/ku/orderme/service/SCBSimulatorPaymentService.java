package th.ku.orderme.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Payment;
import th.ku.orderme.repository.PaymentRepository;
import th.ku.orderme.util.ConstantUtil;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SCBSimulatorPaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    private static String accessToken;
    private static Long accessTokenExpiresAt;

    @Value("${scb.simulator.orderme.apiKey}")
    private String apiKey;

    @Value("${scb.simulator.orderme.apiSecret}")
    private String apiSecret;

    @Value("${scb.simulator.orderme.billerId}")
    private String billerId;

    @Value("${scb.simulator.orderme.ref3Prefix}")
    private String ref3Prefix;

    @Value("${scb.simulator.orderme.merchantId}")
    private String merchantId;

    @Value("${scb.simulator.orderme.terminalId}")
    private String terminalId;

    public String generateAccessToken() {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{" +
                    "\r\n\t\"applicationKey\" : \""+apiKey+"\"," +
                    "\r\n\t\"applicationSecret\" : \""+apiSecret+"\"" +
                    "\r\n}");
            Request request = new Request.Builder()
                    .url("https://api-sandbox.partners.scb/partners/sandbox/v1/oauth/token")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("resourceOwnerId", apiKey)
                    .addHeader("requestUId", java.util.UUID.randomUUID().toString())
                    .addHeader("accept-language", "EN")
                    .build();
            Response response = client.newCall(request).execute();

            JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
            JsonObject status = jsonResponse.getAsJsonObject("status");
            String statusCode = status.getAsJsonPrimitive("code").getAsString();

            if(statusCode.equals("1000")) {
                JsonObject data = jsonResponse.getAsJsonObject("data");
                accessToken = data.getAsJsonPrimitive("accessToken").getAsString();
                accessTokenExpiresAt = data.getAsJsonPrimitive("expiresAt").getAsLong();
                return accessToken;
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.getAsJsonPrimitive("description").getAsString());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            accessToken = null;
        }
        return null;
    }

    private boolean validateToken() {
        if(accessToken == null || (accessTokenExpiresAt - Instant.now().atZone(ZoneId.of("Asia/Bangkok")).toEpochSecond()) < 60) {
            return generateAccessToken() != null;
        }
        return true;
    }

    private String crateRef3(String ref1, String ref2) {
        return ref3Prefix + ref1.substring(2, 10) + ref1.substring(16, 20) + ref2.substring(16, 20);
    }

    public String generateDeeplink(Payment payment) {
        try {
            if(!validateToken()) throw new AuthenticationException("Can't Generate SCB Token");

            String ref3 = crateRef3(payment.getRef1(), payment.getRef2());
            payment.setRef3(ref3);

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{" +
                    "\n\t\"transactionType\": \"PURCHASE\"," +
                    "\n\t\"transactionSubType\": [\"BP\", \"CCFA\"]," +
                    "\n\t\"sessionValidityPeriod\": 900," +
                    "\n\t\"sessionValidUntil\": \"\"," +
                    "\n\t\"billPayment\": {" +
                    "\n\t\t\"paymentAmount\": "+payment.getTotal()+"," +
                    "\n\t\t\"accountTo\": \""+billerId+"\"," +
                    "\n\t\t\"ref1\": \""+ payment.getRef1() +"\"," +
                    "\n\t\t\"ref2\": \""+ payment.getRef2() +"\"," +
                    "\n\t\t\"ref3\": \""+payment.getRef3()+"\"\n\t}," +
                    "\n\t\"creditCardFullAmount\": {" +
                    "\n\t\t\"merchantId\": \""+merchantId+"\"," +
                    "\n\t\t\"terminalId\": \""+terminalId+"\"," +
                    "\n\t\t\"orderReference\": \""+payment.getRef1()+"\"," +
                    "\n\t\t\"paymentAmount\": "+payment.getTotal()+"\n\t}," +
                    "\n\t\"merchantMetaData\": {" +
                    "\n\t\t\"callbackUrl\": \"http://tintin3274.trueddns.com:40850/payment/deeplink/success\"," +
                    "\n\t\t\"merchantInfo\": {" +
                    "\n\t\t\t\"name\": \"OrderMe\"\n\t\t}," +
                    "\n\t\t\"extraData\": {}," +
                    "\n\t\t\"paymentInfo\": [\n\t\t\t{" +
                    "\n\t\t\t\t\"type\": \"TEXT_WITH_IMAGE\"," +
                    "\n\t\t\t\t\"title\": \"\"," +
                    "\n\t\t\t\t\"header\": \"\"," +
                    "\n\t\t\t\t\"description\": \"\"," +
                    "\n\t\t\t\t\"imageUrl\": \"\"\n\t\t\t}," +
                    "\n\t\t\t{" +
                    "\n\t\t\t\t\"type\": \"TEXT\"," +
                    "\n\t\t\t\t\"title\": \"\"," +
                    "\n\t\t\t\t\"header\": \"\"," +
                    "\n\t\t\t\t\"description\": \"\"\n\t\t\t}" +
                    "\n\t\t]\n\t}\n}");

            Request request = new Request.Builder()
                    .url("https://api-sandbox.partners.scb/partners/sandbox/v3/deeplink/transactions")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("authorization", "Bearer "+accessToken)
                    .addHeader("resourceOwnerId", apiKey)
                    .addHeader("requestUId", java.util.UUID.randomUUID().toString())
                    .addHeader("channel", "scbeasy")
                    .addHeader("accept-language", "EN")
                    .build();
            Response response = client.newCall(request).execute();

            JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
            JsonObject status = jsonResponse.getAsJsonObject("status");
            String statusCode = status.get("code").getAsString();

            if(statusCode.equals("1000")) {
                payment.setChannel(ConstantUtil.DEEP_LINK);
                payment.setGenerateInfo(jsonResponse.toString());
                save(payment);

                JsonObject data = jsonResponse.getAsJsonObject("data");
                return data.get("deeplinkUrl").getAsString();
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.get("description").getAsString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String generateQrCode(Payment payment) {
        try {
            if(!validateToken()) throw new AuthenticationException("Can't Generate SCB Token");

            String ref3 = crateRef3(payment.getRef1(), payment.getRef2());
            payment.setRef3(ref3);

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{ " +
                    "\r\n\t\"qrType\": \"PPCS\", " +
                    "\r\n\t\"ppType\": \"BILLERID\", " +
                    "\r\n\t\"ppId\": \""+billerId+"\", " +
                    "\r\n\t\"amount\": "+payment.getTotal()+", " +
                    "\r\n\t\"ref1\": \""+payment.getRef1()+"\", " +
                    "\r\n\t\"ref2\": \""+ payment.getRef2() +"\", " +
                    "\r\n\t\"ref3\": \""+payment.getRef3()+"\"," +
                    "\r\n\t\"merchantId\": \""+merchantId+"\"," +
                    "\r\n\t\"terminalId\": \""+terminalId+"\"," +
                    "\r\n\t\"invoice\": \""+payment.getRef1()+"\"" +
                    "\t\r\n}");
            Request request = new Request.Builder()
                    .url("https://api-sandbox.partners.scb/partners/sandbox/v1/payment/qrcode/create")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("authorization", "Bearer "+accessToken)
                    .addHeader("resourceOwnerId", apiKey)
                    .addHeader("requestUId", java.util.UUID.randomUUID().toString())
                    .addHeader("accept-language", "EN")
                    .build();
            Response response = client.newCall(request).execute();

            JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
            JsonObject status = jsonResponse.getAsJsonObject("status");
            String statusCode = status.get("code").getAsString();

            if(statusCode.equals("1000")) {
                payment.setChannel(ConstantUtil.QR_CODE);
                payment.setGenerateInfo(jsonResponse.toString());
                save(payment);

                JsonObject data = jsonResponse.getAsJsonObject("data");
                String qrImage = data.get("qrImage").getAsString();
                return jsonResponse.toString();
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.get("description").getAsString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private void save(Payment payment) {
        LocalDateTime localDateTime = LocalDateTime.now();
        if(payment.getCreatedTimestamp() == null) payment.setCreatedTimestamp(localDateTime);
        payment.setUpdatedTimestamp(localDateTime);
        paymentRepository.saveAndFlush(payment);
    }

    private boolean paymentComplete(String ref1, String channel, String confirmInfo) {
        Payment payment = paymentRepository.findByRef1(ref1);
        if(payment != null && payment.getStatus().equalsIgnoreCase(ConstantUtil.UNPAID)) {
            payment.setStatus(ConstantUtil.PAID);
            payment.setChannel(channel);
            payment.setConfirmInfo(confirmInfo);
            save(payment);
            paymentService.createReceipt(ref1);
            return true;
        }
        return false;
    }

    public boolean slipVerification(String transRef) { // for QR30 Only
        String request = "{\"transactionId\":\""+transRef+"\"}"; // Reuse by simulate confirm for QR30
        return paymentConfirm(request);
    }

    public boolean inquiryTransaction(int billId) {
        try {
            if(!validateToken()) throw new AuthenticationException("Can't Generate SCB Token");

            Payment payment = paymentRepository.findByBill_Id(billId);
            if(payment == null) return false;
            if(payment.getStatus().equalsIgnoreCase(ConstantUtil.PAID)) return true;
            String channel = payment.getChannel();
            if(channel.equalsIgnoreCase(ConstantUtil.QR_CODE) || channel.equalsIgnoreCase(ConstantUtil.DEEP_LINK)) {
                String generateInfo = payment.getGenerateInfo();
                if(generateInfo == null) return false;
                JsonObject json = JsonParser.parseString(generateInfo).getAsJsonObject();
                JsonObject data = json.getAsJsonObject("data");

                if(channel.equalsIgnoreCase(ConstantUtil.QR_CODE)) {
                    // Try QRCS
                    String transRef = "unknown";
                    String qrId = data.get("qrcodeId").getAsString();
                    String requestBody = "{\"transactionId\":\""+transRef+"\",\"qrId\":\""+qrId+"\"}"; // Reuse by simulate confirm for QRCS
                    if(paymentConfirm(requestBody)) return true; // for QRCS Only

                    try {
                        // Try QR30
                        String ref1 = payment.getRef1();
                        String transactionDate = payment.getUpdatedTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        String url = "http://api-sandbox.partners.scb/partners/sandbox/v1/payment/billpayment/inquiry?billerId="+billerId+"&reference1="+ref1+"&transactionDate="+transactionDate+"&eventCode=00300100";

                        Response response = buildRequestConfirmPayment(url);

                        JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
                        JsonObject status = jsonResponse.getAsJsonObject("status");
                        String statusCode = status.get("code").getAsString();

                        if(statusCode.equals("1000")) {
                            return paymentComplete(ref1, ConstantUtil.QR30, jsonResponse.toString());
                        }
                        else {
                            throw new Exception("Status code "+statusCode+": "+status.get("description").getAsString());
                        }

                    }
                    catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }

                else if(channel.equalsIgnoreCase(ConstantUtil.DEEP_LINK)) {
                    String transactionId = data.get("transactionId").getAsString(); // SCB EASY
                    return slipVerification(transactionId);
                }
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean paymentConfirm(String request) {
        try {
            if(!validateToken()) throw new AuthenticationException("Can't Generate SCB Token");
            String url;

            JsonObject jsonRequest = JsonParser.parseString(request).getAsJsonObject();
            String transactionId = jsonRequest.get("transactionId").getAsString();
            if(transactionId.contains("-")) {
                url = "https://api-sandbox.partners.scb/partners/sandbox/v2/transactions/"+transactionId; // SCB EASY
            }
            else {
                if(jsonRequest.has("qrId")) {
                    String qrId = jsonRequest.get("qrId").getAsString();
                    url = "https://api-sandbox.partners.scb/partners/sandbox/v1/payment/qrcode/creditcard/"+qrId; // QRCS
                }
                else {
                    url = "https://api-sandbox.partners.scb/partners/sandbox/v1/payment/billpayment/transactions/"+transactionId+"?sendingBank=014"; // QR30
                }
            }

            Response response = buildRequestConfirmPayment(url);

            JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
            JsonObject status = jsonResponse.getAsJsonObject("status");
            String statusCode = status.get("code").getAsString();

            if(statusCode.equals("1000")) {
                String channel;
                String ref1 = null;
                JsonObject data = jsonResponse.getAsJsonObject("data");

                if(data.has("transactionMethod")) {
                    channel = data.get("transactionMethod").getAsString(); // SCB EASY - BP, CCFA
                    switch (channel) {
                        case ConstantUtil.BP:
                            JsonObject billPayment = data.getAsJsonObject("billPayment");
                            ref1 = billPayment.get("ref1").getAsString();
                            break;
                        case ConstantUtil.CCFA:
                            JsonObject creditCardFullAmount = data.getAsJsonObject("creditCardFullAmount");
                            ref1 = creditCardFullAmount.get("orderReference").getAsString();
                            break;
                    }
                }
                else if(data.has("paymentMethod")) {
                    channel = data.get("paymentMethod").getAsString(); // QRCS
                    ref1 = data.get("invoice").getAsString();
                }
                else {
                    channel = ConstantUtil.QR30; // QR30
                    ref1 = data.get("ref1").getAsString();
                }

                return paymentComplete(ref1, channel, jsonResponse.toString());
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.get("description").getAsString());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private Response buildRequestConfirmPayment(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("authorization", "Bearer "+accessToken)
                .addHeader("resourceOwnerId", apiKey)
                .addHeader("requestUId", java.util.UUID.randomUUID().toString())
                .addHeader("accept-language", "EN")
                .build();
        return client.newCall(request).execute();
    }

    // For QR Code Only
    public String extractQrImageFromResponse(String response) {
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        JsonObject data = jsonResponse.getAsJsonObject("data");
        return data.get("qrImage").getAsString();
    }

    // For Deep Link Only
    public String extractDeepLinkFromResponse(String response) {
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        JsonObject data = jsonResponse.getAsJsonObject("data");
        return data.get("deeplinkUrl").getAsString();
    }

}
