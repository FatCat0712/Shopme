package com.shopme.category;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listAll() {
        return categoryRepository.findAllEnabled();
    }

    public List<Category> listRemainingCategories() {
        return categoryRepository.listRemainingCategories();
    }

    public List<Category> listFirst8Categories() {
        return categoryRepository.findTop8ByEnabledTrueOrderByIdAsc();
    }

    public List<Category> listNoChildrenCategories() {
        List<Category> listEnabledCategory = categoryRepository.findAllEnabled();
        return listEnabledCategory.stream().filter(category -> category.getChildren() == null || category.getChildren().isEmpty()).toList();
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category =  categoryRepository.findByAliasEnabled(alias);
        if(category == null) {
            throw new CategoryNotFoundException("Could not find any category with alias: " + alias);
        }
        return category;
    }

    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();

        Category parent = child.getParent();

        while(parent != null) {
          listParents.add(0, parent);
          parent = parent.getParent();
        }

        listParents.add(child);

        return  listParents;
    }

    public List<Category> listCategoriesUsedInForm() {
        Iterable<Category> categories =  (List<Category>)categoryRepository.findRootCategories(Sort.by("name").ascending());
        List<Category> categoriesUsedInForm = new ArrayList<>();

        for(Category category : categories) {
            if(category.getParent() == null) {
                categoriesUsedInForm.add(Category.copyNameAndAlias(category));

                Set<Category> children = sortSubCategories(category.getChildren());

                for (Category subCategory: children) {
                    categoriesUsedInForm.add(Category.copyNameAndAlias("--" + subCategory.getName(), subCategory.getAlias()));
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
            categoriesUsedInForm.add(Category.copyNameAndAlias(name + subCategory.getName(), subCategory.getAlias()));
            listSubCategoriesUsedInForm(subCategory, newSubLevel, categoriesUsedInForm);
        }
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


}
