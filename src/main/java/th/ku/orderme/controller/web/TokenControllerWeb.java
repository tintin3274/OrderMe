package th.ku.orderme.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import th.ku.orderme.service.TokenService;
import th.ku.orderme.util.ConstantUtil;
import th.ku.orderme.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class TokenControllerWeb {
    private final TokenService tokenService;

    public TokenControllerWeb(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/dine-in/{uid}")
    public String dineIn(@PathVariable String uid, HttpServletRequest request, HttpServletResponse response) {
        if(!tokenService.tokenIsPresent(uid)) return "redirect:/";
        Cookie cookie1 = CookieUtil.createCookie("uid", uid, 5 * 60 * 60, false, true, "/", request.getServerName());
        Cookie cookie2 = CookieUtil.createCookie("type", ConstantUtil.DINE_IN, 5 * 60 * 60, false, true, "/", request.getServerName());
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        return "redirect:/main-menu";
    }

    @GetMapping("/take-out")
    public String takeOut(HttpServletRequest request, HttpServletResponse response) {
        String uid = generateNewToken();
        Cookie cookie1 = CookieUtil.createCookie("uid", uid, 30 * 60, false, true, "/", request.getServerName());
        Cookie cookie2 = CookieUtil.createCookie("type", ConstantUtil.TAKE_OUT, 30 * 60, false, true, "/", request.getServerName());
        response.addCookie(cookie1);
        response.addCookie(cookie2);

        tokenService.newToken(uid);
        tokenService.autoDeleteToken(uid, 30);
        return "redirect:/main-menu";
    }

    @ResponseBody
    @GetMapping("/verify-token")
    public String readCookie(@CookieValue(name = "uid") String uid) {
        return uid;
    }

    @ResponseBody
    @GetMapping("/generate-token")
    public String generateNewToken() {
        return TokenService.generateNewToken();
    }
}
