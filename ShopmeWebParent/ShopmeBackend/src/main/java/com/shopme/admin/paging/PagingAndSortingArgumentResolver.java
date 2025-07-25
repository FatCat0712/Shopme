package com.shopme.admin.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingArgumentResolver  implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer model, NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        String sortField = request.getParameter("sortField");
        String sortDir = request.getParameter("sortDir");
        String keyword = request.getParameter("keyword");

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);


        PagingAndSortingParam annotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);
        model.addAttribute("moduleURL", annotation.moduleURL());


        return new PagingAndSortingHelper(model, annotation.listName(), sortField, sortDir, keyword);
    }
}
