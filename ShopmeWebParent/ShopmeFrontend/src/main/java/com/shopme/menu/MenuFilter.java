package com.shopme.menu;

import com.shopme.common.entity.menu.Menu;
import com.shopme.common.entity.menu.MenuType;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class MenuFilter implements Filter {
    private final MenuService service;

    @Autowired
    public MenuFilter(MenuService service) {
        this.service = service;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String url = servletRequest.getRequestURL().toString();

        if(url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
            filterChain.doFilter(request, response);
        }

        List<Menu> listHeaderMenus = service.listMenusByMenuType(MenuType.HEADER);
        List<Menu> listFooterMenus = service.listMenusByMenuType(MenuType.FOOTER);

        request.setAttribute("listHeaderMenus", listHeaderMenus);
        request.setAttribute("listFooterMenus", listFooterMenus);

        filterChain.doFilter(request, response);
    }
}
