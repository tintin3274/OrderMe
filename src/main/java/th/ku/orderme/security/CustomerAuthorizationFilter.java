package th.ku.orderme.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import th.ku.orderme.service.TokenService;
import th.ku.orderme.util.CookieUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class CustomerAuthorizationFilter extends OncePerRequestFilter {
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().startsWith("/dine-in/")) {
            filterChain.doFilter(request, response);
        }
        else if(request.getServletPath().equalsIgnoreCase("/take-out")) {
            filterChain.doFilter(request, response);
        }
        else {
            if(tokenService == null) {
                ServletContext servletContext = request.getServletContext();
                WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
                assert webApplicationContext != null;
                tokenService = webApplicationContext.getBean(TokenService.class);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Optional<Cookie> cookie = CookieUtil.readCookie(request, "uid");
            if(cookie.isPresent()) {
                if(authentication == null) {
                    String uid = cookie.get().getValue();
                    if(tokenService.tokenIsPresent(uid)) {
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
                        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(authority);
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(uid, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                    else {
                        Cookie c  = CookieUtil.createCookie("uid", null, 0, false, true, "/", request.getServerName());
                        response.addCookie(c);
                        request.getSession().invalidate();
                    }
                }
            }

            else {
                try {
                    if(authentication != null) {
                        String authorities = authentication.getAuthorities().toString();
                        if(authorities.contains("ROLE_USER")) {
                            Cookie c  = CookieUtil.createCookie("uid", null, 0, false, true, "/", request.getServerName());
                            response.addCookie(c);
                            request.getSession().invalidate();
                        }
                    }
                }
                catch (IllegalStateException e) {
                    log.error(e.getMessage());
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
