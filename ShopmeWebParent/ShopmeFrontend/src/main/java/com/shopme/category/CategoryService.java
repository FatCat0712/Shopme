package com.shopme.category;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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



}
