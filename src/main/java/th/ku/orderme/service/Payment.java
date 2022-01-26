package th.ku.orderme.service;

public interface Payment {
    String generateAccessToken();
    String generateDeeplink(Double amount);
    String generateQrCode(Double amount);
}
