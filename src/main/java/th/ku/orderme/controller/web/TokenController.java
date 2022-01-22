package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.service.TokenService;
import th.ku.orderme.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("token/{uid}")
    public String createCookie(@PathVariable String uid, HttpServletRequest request, HttpServletResponse response) {
        if(!tokenService.tokenIsPresent(uid)) return null;
        Cookie cookie = CookieUtil.createCookie("uid", uid, 12 * 60 * 60, false, true, "/", request.getServerName());
        response.addCookie(cookie);
        return "SUCCESS";
    }

    @GetMapping("verify-token")
    public String readCookie(@CookieValue(name = "uid", defaultValue = "default-uid") String uid) {
        return uid;
    }

    @ResponseBody
    @GetMapping("generate-token")
    public String generateNewToken() {
        return TokenService.generateNewToken();
    }
}
