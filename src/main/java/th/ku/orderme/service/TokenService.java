package th.ku.orderme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.ku.orderme.model.Bill;
import th.ku.orderme.model.Token;
import th.ku.orderme.repository.TokenRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(2, new DaemonThreadFactory());
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final TokenRepository tokenRepository;

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public boolean tokenIsPresent(String token) {
        return tokenRepository.findById(token).isPresent();
    }

    public Token findById(String id) {
        return tokenRepository.findById(id).orElse(null);
    }

    public void deleteById(String id) {
        if(tokenRepository.existsById(id)) {
            tokenRepository.deleteById(id);
            log.info("TokenService: Delete ID - "+id);
        }
    }

    public void deleteByBillId(int billId) {
        Token token = tokenRepository.findByBill_Id(billId);
        if(token != null) {
            deleteById(token.getId());
        }
    }

    public Token newToken(String id) {
        Token token = new Token();
        token.setId(id);
        token = tokenRepository.saveAndFlush(token);
        log.info("TokenService: New ID - "+id);
        return token;
    }

    public Token mappingNewTokenToBill(Bill bill) {
        try {
            String id = generateNewToken();
            Token token = new Token();
            token.setId(id);
            token.setBill(bill);
            token = tokenRepository.saveAndFlush(token);
            log.info("TokenService: New ID - "+ id);
            log.info("TokenService: Mapping ID to Bill ID - "+ id + " <> "+bill.getId());
            return token;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public Token mappingTokenToBill(String id, Bill bill) {
        Token token = findById(id);
        if(token == null) return null;
        token.setBill(bill);
        token = tokenRepository.saveAndFlush(token);
        log.info("TokenService: Mapping ID to Bill ID - "+ id + " <> "+bill.getId());
        return token;
    }

    public void autoDeleteToken(String id) {
        Runnable task = () -> deleteById(id);
        ses.schedule(task, 30, TimeUnit.MINUTES);
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    }
}
