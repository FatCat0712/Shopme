package com.shopme.admin.setting;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.shopme.common.entity.Constants.SUPABASE_URI;

@Component
public class SettingFilter implements Filter {
    private final SettingService settingService;

    @Autowired
    public SettingFilter(SettingService settingService) {
        this.settingService = settingService;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String url = servletRequest.getRequestURL().toString();

        if(url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
            filterChain.doFilter(request, response);
            return;
        }

       GeneralSettingBag generalSettings = settingService.getGeneralSettingBag();

        generalSettings.list().forEach(setting -> {
            request.setAttribute(setting.getKey(), setting.getValue());
        });

        request.setAttribute("SUPABASE_URI", SUPABASE_URI);


        filterChain.doFilter(request, response);

    }
}
