package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
            Sort sort = Sort.by("name");

            if(sortDir == null || sortDir.isEmpty()) sort = sort.ascending();
            else if(sortDir.equals("asc")) sort = sort.ascending();
            else if(sortDir.equals("desc")) sort = sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

            Page<Category> pageCategories = null;
            if(keyword != null && !keyword.isEmpty()) {
                pageCategories = categoryRepository.search(keyword, pageable);
            }
            else {
               pageCategories = categoryRepository.findRootCategories(pageable);
            }

            List<Category> rootCategories = pageCategories.getContent();

            pageInfo.setTotalElements(pageCategories.getTotalElements());
            pageInfo.setTotalPages(pageCategories.getTotalPages());

            if(keyword != null) {
                List<Category> searchResult = pageCategories.getContent();
                for(Category category: searchResult) {
                    category.setHasChildren(category.getChildren().size() > 0);
                }
                return searchResult;
            }else {
                return listHierarchicalCategories(rootCategories, sortDir);
            }

    }

    public List<Category> listHierarchicalCategories(List<Category> rootCategories,String sortDir) {
        List<Category>  listHierarchicalCategories = new ArrayList<>();

        for(Category rootCategory: rootCategories) {
            if(rootCategory.getParent() == null) {
                listHierarchicalCategories.add(Category.copyFull(rootCategory));

                Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

                for (Category subCategory: children) {
                    listHierarchicalCategories.add(Category.copyFull(subCategory, "--" + subCategory.getName()));
                    listSubHierarchicalCategories(subCategory, 1, listHierarchicalCategories, sortDir);
                }
            }
        }

        return listHierarchicalCategories;
    }

    private void listSubHierarchicalCategories(Category parent, int subLevel, List<Category>  listHierarchicalCategories, String sortDir) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = subLevel + 1;
        for(Category subCategory: children) {
            String name = "";
            for(int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            listHierarchicalCategories.add(Category.copyFull(subCategory, name + subCategory.getName()));
            listSubHierarchicalCategories(subCategory, newSubLevel, listHierarchicalCategories, sortDir);
        }
    }

    public Category save(Category category) {
        Category parent = category.getParent();
        if(parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += parent.getId() + "-";
            category.setAllParentIDs(allParentIds);
        }
        return categoryRepository.save(category);
    }


    public List<Category> listCategoriesUsedInForm() {
        Iterable<Category> categories =  (List<Category>)categoryRepository.findRootCategories(Sort.by("name").ascending());
        List<Category> categoriesUsedInForm = new ArrayList<>();

        for(Category category : categories) {
            if(category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyIdAndName(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory: children) {
                    categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), "--" + subCategory.getName()));
                    listSubCategoriesUsedInForm(subCategory, 1, categoriesUsedInForm);
                }
            }
        }

        return categoriesUsedInForm;
    }

    private void listSubCategoriesUsedInForm(Category parent, int subLevel, List<Category> categoriesUsedInForm) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());
        for(Category subCategory : children) {
            String name = "";
            for(int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name + subCategory.getName()));
            listSubCategoriesUsedInForm(subCategory, newSubLevel, categoriesUsedInForm);
        }
    }

    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        }catch (NoSuchElementException exception) {
            throw new CategoryNotFoundException("Could not find any category with id " + id);
        }
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = id == null || id == 0;

        Category categoryByName = categoryRepository.findByName(name);

        if(isCreatingNew) {
            if(categoryByName != null) {
                return "DuplicateName";
            }
            else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if(categoryByAlias != null ){
                    return "DuplicateAlias";
                }
            }
        } else {
            if(categoryByName != null && !categoryByName.getId().equals(id)) {
                return "DuplicateName";
            }

            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if(categoryByAlias != null && !categoryByAlias.getId().equals(id)) {
                return "DuplicateAlias";
            }
        }

        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }


    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                return sortDir.equals("asc" ) ? c1.getName().compareTo(c2.getName()) : c2.getName().compareTo(c1.getName());
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }

    public void updateCategoryEnabledStatus(Integer id, boolean isEnabled) {
            categoryRepository.updateEnableStatus(id, isEnabled);
    }

    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);
        if(countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }

        categoryRepository.deleteById(id);
    }

    public Integer countRootCategories() {
        return listAll().stream().filter(c -> c.getParent() ==null).toList().size();
    }

    public Integer countEnabledCategories() {
        return listAll().stream().filter(Category::isEnabled).toList().size();
    }

    public Integer countDisabledCategories() {
        return listAll().stream().filter(c -> !c.isEnabled()).toList().size();
    }
}
