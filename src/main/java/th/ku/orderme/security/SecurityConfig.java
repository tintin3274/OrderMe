package th.ku.orderme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfig {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("password")).roles("ADMIN", "STAFF");
        auth.inMemoryAuthentication().withUser("staff").password(passwordEncoder().encode("password")).roles("STAFF");
    }

    @Order(1)
    @Configuration
    public static class LoginConfiguration extends WebSecurityConfigurerAdapter {
        @Autowired
        private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/login*")

                    .formLogin()
                    .successHandler(customAuthenticationSuccessHandler)

                    .and()
                    .csrf().disable();
        }
    }

    @Order(2)
    @Configuration
    public static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers()
                    .antMatchers("/api/**")
                    .antMatchers("/app/**")
                    .antMatchers("/topic/**")

                    .and()
                    .authorizeRequests()
                    .antMatchers("/app/**").hasRole("STAFF")
                    .antMatchers("/topic/**").hasRole("STAFF")
                    .anyRequest().authenticated()

                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .sessionFixation()
                    .migrateSession()

                    .and()
                    .httpBasic()

                    .and()
                    .csrf().disable();
        }
    }

    @Order(3)
    @Configuration
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            String[] resources = new String[]{
                    "/js/**","/css/**","/styles/**", "/webjars/**", "/material/**"
            };

            String[] allow = new String[]{
                    "/dine-in/**", "/take-out", "/scb/payment-confirm", "/receipt/**"
                    //,"/generate-token", "/verify-token"
                    //,"/api/**", "/app/**", "/topic/**", "/orderme-websocket"
            };

            http
                    .addFilterBefore(new CustomerAuthorizationFilter(), BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/staff/**").hasRole("STAFF")
                    .antMatchers(resources).permitAll()
                    .antMatchers(allow).permitAll()
                    .anyRequest().authenticated()

                    .and()
                    .formLogin()
                    .loginPage("/").permitAll()

                    .and()
                    .csrf().disable();
        }
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
