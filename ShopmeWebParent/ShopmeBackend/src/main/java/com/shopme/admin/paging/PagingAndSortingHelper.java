package com.shopme.admin.paging;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

@Getter
public class PagingAndSortingHelper {
    private final ModelAndViewContainer model;
    private final String listName;
    private final String sortField;
    private final String sortDir;
    private final String keyword;


    public PagingAndSortingHelper(ModelAndViewContainer model , String listName, String sortField,String sortDir, String keyword) {
        this.model = model;
        this.listName = listName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyword = keyword;
    }

    public void updateModelAttribute(int pageNum, Page<?> page) {
        List<?> listItems = page.getContent();
        int pageSize = page.getSize();


        long startCount = (long) (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute(listName, listItems);

    }

    public void listEntities(int pageNum, int pageSize, SearchRepository<?, Integer> repo) {
        Sort sort = Sort.by(sortField);
        if(sortDir == null || sortDir.isEmpty()) sort = sort.ascending();
        else if(sortDir.equals("asc")) sort = sort.ascending();
        else if(sortDir.equals("desc"))  sort = sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<?> page;

        if(keyword != null) {
            page = repo.findAll(keyword, pageable);
        }
        else {
            page = repo.findAll(pageable);
        }

        updateModelAttribute(pageNum, page);
    }

    public Pageable createPageable(int pageSize, int pageNum) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ?  sort.ascending() : sort.descending();
        return PageRequest.of(pageNum -1, pageSize, sort);
    }

}
