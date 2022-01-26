package th.ku.orderme.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SCBSimulatorPayment implements Payment {

    @Value("${scb.simulator.orderme.apiKey}")
    private String apiKey;

    @Value("${scb.simulator.orderme.apiSecret}")
    private String apiSecret;

    private String accessToken;

    @Value("${scb.simulator.orderme.billerId}")
    private String billerId;

    @Value("${scb.simulator.orderme.ref1}")
    private String ref1;

    @Value("${scb.simulator.orderme.ref2}")
    private String ref2;

    @Value("${scb.simulator.orderme.ref3Prefix}")
    private String ref3Prefix;

    @Value("${scb.simulator.orderme.merchantId}")
    private String merchantId;

    @Value("${scb.simulator.orderme.terminalId}")
    private String terminalId;


    @Override
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
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.getAsJsonPrimitive("description").getAsString());
            }

            System.out.println(accessToken);

        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    @Override
    public String generateDeeplink(Double amount) {
        return null;
    }

    @Override
    public String generateQrCode(Double amount) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{ " +
                    "\r\n\t\"qrType\": \"PPCS\", " +
                    "\r\n\t\"ppType\": \"BILLERID\", " +
                    "\r\n\t\"ppId\": \""+billerId+"\", " +
                    "\r\n\t\"amount\": \""+amount+"\", " +
                    "\r\n\t\"ref1\": \""+ref1+"\", " +
                    "\r\n\t\"ref2\": \""+ref2+"\", " +
                    "\r\n\t\"ref3\": \""+ref3Prefix+"\"," +
                    "\r\n\t\"merchantId\": \""+merchantId+"\"," +
                    "\r\n\t\"terminalId\": \""+terminalId+"\"," +
                    "\r\n\t\"invoice\": \"ORDERMETEST1\"," +
                    "\r\n\t\"csExtExpiryTime\" : \"60\"" +
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
            String statusCode = status.getAsJsonPrimitive("code").getAsString();

            if(statusCode.equals("1000")) {
                JsonObject data = jsonResponse.getAsJsonObject("data");
                String qrImage = data.getAsJsonPrimitive("qrImage").getAsString();
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.getAsJsonPrimitive("description").getAsString());
            }

        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }
}
