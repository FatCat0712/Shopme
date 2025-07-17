package com.shopme.admin;

import com.shopme.admin.paging.PagingAndSortingArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(MvcConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("ShopmeWebParent/ShopmeBackend/user-photos","/user-photos/**", registry);
        exposeDirectory("ShopmeWebParent/category-images","/category-images/**", registry);
        exposeDirectory("ShopmeWebParent/brand-logos", "/brand-logos/**", registry);
        exposeDirectory("ShopmeWebParent/product-images", "/product-images/**", registry);
        exposeDirectory("ShopmeWebParent/site-logo", "/site-logo/**", registry);
    }

    private void exposeDirectory(String projectPath, String pathPattern, ResourceHandlerRegistry registry) {
        /*Map project path to system path*/
        String projectAbsolutePath = new File(projectPath).getAbsolutePath();
        Path dirPath = Paths.get(projectAbsolutePath);
        String systemPath = dirPath.toFile().getAbsolutePath();

        registry.addResourceHandler(pathPattern).addResourceLocations("file:/" + systemPath + "/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
    }
}
