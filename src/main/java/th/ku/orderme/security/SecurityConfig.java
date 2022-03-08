package th.ku.orderme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("password")).roles("ADMIN");
            auth.inMemoryAuthentication().withUser("staff").password(passwordEncoder().encode("password")).roles("STAFF");
        }
    }

    @Order(2)
    @Configuration
    public static class ApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

        //TODO Use filter and authenticated
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    //.addFilterBefore(new CustomerAuthorizationFilter(), BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    //.anyRequest().authenticated()
                    .anyRequest().permitAll()

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
                    "/", "/js/**","/css/**","/styles/**", "/webjars/**"
            };

            String[] allow = new String[]{
                    "/dine-in/**", "/take-out", "/scb/payment-confirm",
                    "/generate-token", "/verify-token"
            };

            http
                    .addFilterBefore(new CustomerAuthorizationFilter(), BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasRole("ADMIN")
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
