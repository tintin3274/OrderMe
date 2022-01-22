package th.ku.orderme.service;

import org.springframework.stereotype.Service;
import th.ku.orderme.model.Token;
import th.ku.orderme.repository.TokenRepository;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final TokenRepository tokenRepository;

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public boolean tokenIsPresent(String token) {
        return tokenRepository.findById(token).isPresent();
    }

    public Token findById(String id) {
        return tokenRepository.findById(id).orElse(null);
    }
}
