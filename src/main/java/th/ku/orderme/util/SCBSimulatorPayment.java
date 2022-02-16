package th.ku.orderme.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.time.Instant;
import java.time.ZoneId;

@Service
public class SCBSimulatorPayment {

    private static String accessToken;
    private static Long accessTokenExpiresAt;

    @Value("${scb.simulator.orderme.apiKey}")
    private String apiKey;

    @Value("${scb.simulator.orderme.apiSecret}")
    private String apiSecret;

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

                System.out.println("accessToken: "+accessToken);
                return accessToken;
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.getAsJsonPrimitive("description").getAsString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
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

    public String generateDeeplink(Double amount) {
        try {
            if(!validateToken()) throw new AuthenticationException("Can't Generate SCB Token");

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{" +
                    "\n\t\"transactionType\": \"PURCHASE\"," +
                    "\n\t\"transactionSubType\": [\"BP\", \"CCFA\"]," +
                    "\n\t\"sessionValidityPeriod\": 900," +
                    "\n\t\"sessionValidUntil\": \"\"," +
                    "\n\t\"billPayment\": {" +
                    "\n\t\t\"paymentAmount\": "+amount+"," +
                    "\n\t\t\"accountTo\": \"164041983738626\"," +
                    "\n\t\t\"ref1\": \""+ref1+"\"," +
                    "\n\t\t\"ref2\": \""+ref2+"\"," +
                    "\n\t\t\"ref3\": \""+ref3Prefix+"\"\n\t}," +
                    "\n\t\"creditCardFullAmount\": {" +
                    "\n\t\t\"merchantId\": \"741195842153552\"," +
                    "\n\t\t\"terminalId\": \"751110402887330\"," +
                    "\n\t\t\"orderReference\": \"12345678\"," +
                    "\n\t\t\"paymentAmount\": "+amount+"\n\t}," +
                    "\n\t\"merchantMetaData\": {" +
                    "\n\t\t\"callbackUrl\": \"http://tintin3274.trueddns.com:40850\"," +
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
            String statusCode = status.getAsJsonPrimitive("code").getAsString();

            if(statusCode.equals("1000")) {
                JsonObject data = jsonResponse.getAsJsonObject("data");
                String deeplinkUrl = data.getAsJsonPrimitive("deeplinkUrl").getAsString();
                return deeplinkUrl;
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.getAsJsonPrimitive("description").getAsString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        return null;
    }

    public String generateQrCode(Double amount) {
        try {
            if(!validateToken()) throw new AuthenticationException("Can't Generate SCB Token");

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{ " +
                    "\r\n\t\"qrType\": \"PPCS\", " +
                    "\r\n\t\"ppType\": \"BILLERID\", " +
                    "\r\n\t\"ppId\": \""+billerId+"\", " +
                    "\r\n\t\"amount\": "+amount+", " +
                    "\r\n\t\"ref1\": \""+ref1+"\", " +
                    "\r\n\t\"ref2\": \""+ref2+"\", " +
                    "\r\n\t\"ref3\": \""+ref3Prefix+"\"," +
                    "\r\n\t\"merchantId\": \""+merchantId+"\"," +
                    "\r\n\t\"terminalId\": \""+terminalId+"\"," +
                    "\r\n\t\"invoice\": \"ORDERMETEST1\"" +
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
                return jsonResponse.toString();
            }
            else {
                throw new Exception("Status code "+statusCode+": "+status.getAsJsonPrimitive("description").getAsString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        return null;
    }
}
