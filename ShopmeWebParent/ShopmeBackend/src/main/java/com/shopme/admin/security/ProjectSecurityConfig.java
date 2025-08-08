package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {

        @Bean
        public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

            http.authorizeHttpRequests(requests -> requests
                    .requestMatchers("/images/**", "/js/**", "/webjars/**", "/fontawesome/**").permitAll()
                    .requestMatchers("/states/list_by_country/**").hasAnyAuthority("Admin", "Salesperson")
                    .requestMatchers("/users/**","/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
                    .requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
                    .requestMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
                    .requestMatchers("/products/edit/**", "/products/save" , "/products/check_unique").hasAnyAuthority("Admin", "Editor", "Salesperson")
                    .requestMatchers("/products", "/products/", "/products/detail/**", "/products/page/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
                    .requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
                    .requestMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                    .requestMatchers("/products/detail/**", "/customers/detail/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Assistant")
                    .requestMatchers("/customers/**", "/orders/**", "/get_shipping_cost", "/reports/**").hasAnyAuthority("Admin", "Salesperson")
                    .requestMatchers("/order_shipper/update/**").hasAnyAuthority("Shipper")
                    .requestMatchers("/reviews/**").hasAnyAuthority("Admin", "Assistant")
                    .anyRequest().authenticated()
            );

            http.formLogin(flc -> flc.loginPage("/login").usernameParameter("email").permitAll());

            http.logout(LogoutConfigurer::permitAll);

            http.rememberMe(rm -> rm.key("]4v5-Tq,y=N5S?0];En.(:;1LQQq(L").tokenValiditySeconds(7 * 24 * 60 * 60));

           http.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

            return http.build();
        }


        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }


}
