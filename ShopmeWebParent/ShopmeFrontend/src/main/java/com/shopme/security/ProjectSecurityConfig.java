package com.shopme.security;

import com.shopme.security.oauth.CustomerOAuth2Service;
import com.shopme.security.oauth.OAuth2LoginSuccessHandler;
import com.shopme.setting.SettingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class ProjectSecurityConfig {
    private final CustomerOAuth2Service oAuth2Service;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
    private final SettingFilter settingFilter;

    @Autowired
    public ProjectSecurityConfig(
            CustomerOAuth2Service oAuth2Service, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            DatabaseLoginSuccessHandler databaseLoginSuccessHandler, SettingFilter settingFilter
    )
    {
        this.oAuth2Service = oAuth2Service;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.databaseLoginSuccessHandler = databaseLoginSuccessHandler;
        this.settingFilter = settingFilter;
    }

    @Bean
        public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(requests -> requests
                    .requestMatchers(
                            "/account_details", "/update_account_details", "/cart", "/address_book/**", "/checkout",
                            "/place_order", "/process_paypal_order", "/orders/**", "/reviews/**", "/write_review/product/**",
                            "/post_review").authenticated()
                    .anyRequest().permitAll());

            http.formLogin(flc -> flc.loginPage("/login")
                    .usernameParameter("email")
                    .successHandler(databaseLoginSuccessHandler)
            );


            http.oauth2Login(o2lc -> o2lc.loginPage("/login")
                    .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2Service))
                    .successHandler(oAuth2LoginSuccessHandler)
            );

            http.logout(LogoutConfigurer::permitAll);


            http.rememberMe(rm -> rm.key("]4v5-Tq,y=N5S?0];En.(:;1LQQq(L").tokenValiditySeconds(7 * 24 * 60 * 60));

            http.sessionManagement(ssmc -> ssmc.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

            http.addFilterBefore(settingFilter, UsernamePasswordAuthenticationFilter.class);


            return http.build();
        }


        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }




}
