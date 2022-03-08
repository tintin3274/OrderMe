package th.ku.orderme.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import th.ku.orderme.util.CookieUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class CustomerAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().startsWith("/dine-in/")) {
            filterChain.doFilter(request, response);
        }
        else if(request.getServletPath().equalsIgnoreCase("/take-out")) {
            filterChain.doFilter(request, response);
        }
        else {
            Optional<Cookie> cookie = CookieUtil.readCookie(request, "uid");
            if(cookie.isPresent()) {
                String uid = cookie.get().getValue();
                if(!uid.equals("default-uid")) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(authority);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(uid, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            else {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if(authentication != null) {
                    String authorities = authentication.getAuthorities().toString();
                    if(authorities.contains("ROLE_USER")) {
                        request.getSession().invalidate();
                    }
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
