package com.shopme.admin.product;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 5;
    private final ProductRepository repo;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.repo = productRepository;
    }

    public List<Product> listAll() {
        return (List<Product>) repo.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page;

        if(keyword != null && !keyword.isEmpty()) {
            if(categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page =  repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            }else {
                page =  repo.findAll(keyword, pageable);
            }
        }
        else {
            if(categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page =  repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
            }
            else {
                page = repo.findAll(pageable);
            }
        }

        helper.updateModelAttribute(pageNum, page);
    }

    public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page;
        if(keyword != null) {
            page = repo.searchProductsByName(keyword, pageable);
        }
        else {
            page = repo.findAll(pageable);
        }
        helper.updateModelAttribute(pageNum, page);
    }

    public Product save(Product product) {
        if(product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if(product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        }
        else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());

        Product updatedProduct = repo.save(product);
        repo.updateReviewCountAndAverageRating(updatedProduct.getId());
        return  updatedProduct;
    }

    public void savedProductPrice(Product productInForm) throws ProductNotFoundException {
         Optional<Product> product = repo.findById(productInForm.getId());
         if(product.isEmpty()) {
             throw new ProductNotFoundException("Could not find any product with ID " + productInForm.getId());
         }
        Product productInDB = product.get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());
        repo.save(productInDB);
    }

    public String checkName(Integer id, String name) {
        Product product = repo.findByName(name);

        if(product == null) return "OK";

        if(id == null) return "Duplicate";

        return product.getId().equals(id) ? "OK" : "Duplicate";
    }

    public void updateStatus(Integer id, Boolean status) throws ProductNotFoundException {
        Optional<Product> saveProduct = repo.findById(id);
        if(saveProduct.isEmpty()) {
            throw new ProductNotFoundException("Could not find any product with ID " + id );
        }
        repo.updateProductStatus(id, status);

    }

    public void deleteProduct(Integer id) throws ProductNotFoundException {
        Optional<Product> saveProduct = repo.findById(id);
        if(saveProduct.isEmpty()) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
        repo.deleteById(id);
    }

    public Product get(Integer id ) throws ProductNotFoundException {
        Optional<Product> product = repo.findById(id);
        if(product.isEmpty()) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
        return product.get();
    }






}
