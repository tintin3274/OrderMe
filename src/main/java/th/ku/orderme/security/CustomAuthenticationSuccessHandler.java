package th.ku.orderme.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/manage-item");
                return;
            } else if (grantedAuthority.getAuthority().equals("ROLE_STAFF")) {
                response.sendRedirect("/admin/manage-item");
                return;
            }
        }
    }
}
