package com.example.jobnest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.annotation.Order;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        private static final String LOGIN_URL = "/login";
        private static final String ADMIN_LOGIN_URL = "/admin/login";

        private final UserDetailsService userDetailsService;

        private final com.example.jobnest.config.CustomAuthenticationFailureHandler authenticationFailureHandler;
        private final com.example.jobnest.config.CustomAuthenticationSuccessHandler authenticationSuccessHandler;

        @Value("${security.rememberme.admin-key:}")
        private String adminRememberMeKey;

        @Value("${security.rememberme.user-key:}")
        private String userRememberMeKey;

        private final String[] publicUrl = {
                        "/",
                        LOGIN_URL,
                        "/register",
                        "/register/**",
                        "/forgot-password",
                        "/reset-password",
                        "/change-password",
                        "/resources/**",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/webjars/**",
                        "/assets/**"
        };

        public WebSecurityConfig(UserDetailsService userDetailsService,
                        com.example.jobnest.config.CustomAuthenticationFailureHandler authenticationFailureHandler,
                        com.example.jobnest.config.CustomAuthenticationSuccessHandler authenticationSuccessHandler) {
                this.userDetailsService = userDetailsService;
                this.authenticationFailureHandler = authenticationFailureHandler;
                this.authenticationSuccessHandler = authenticationSuccessHandler;
        }

        @Bean
        @Order(1)
        public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .userDetailsService(userDetailsService)
                                .securityMatcher("/admin/**")
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(ADMIN_LOGIN_URL, "/css/**", "/js/**", "/webjars/**")
                                                .permitAll()
                                                .anyRequest().hasAuthority("ROLE_ADMIN"))
                                .formLogin(form -> form
                                                .loginPage(ADMIN_LOGIN_URL)
                                                .loginProcessingUrl(ADMIN_LOGIN_URL)
                                                .successHandler(authenticationSuccessHandler)
                                                .failureHandler(authenticationFailureHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/admin/logout")
                                                .logoutSuccessUrl(ADMIN_LOGIN_URL + "?logout=true")
                                                .permitAll())
                                .rememberMe(remember -> remember
                                                .key(adminRememberMeKey)
                                                .tokenValiditySeconds(86400 * 7)); // 7 days

                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .userDetailsService(userDetailsService)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(publicUrl).permitAll()
                                                .requestMatchers("/jobs/*/apply").hasAuthority("ROLE_JOB SEEKER")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage(LOGIN_URL)
                                                .loginProcessingUrl(LOGIN_URL)
                                                .successHandler(authenticationSuccessHandler)
                                                .failureHandler(authenticationFailureHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .permitAll())
                                .rememberMe(remember -> remember
                                                .key(userRememberMeKey)
                                                .tokenValiditySeconds(86400 * 7)); // 7 days
                // CSRF is enabled by default, so we removed .csrf(disable)

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
