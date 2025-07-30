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
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 5;
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper, Integer categoryId) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page;

        if(keyword != null && !keyword.isEmpty()) {
            if(categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page =  productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            }else {
                page =  productRepository.findAll(keyword, pageable);
            }
        }
        else {
            if(categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                page =  productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
            }
            else {
                page = productRepository.findAll(pageable);
            }
        }

        helper.updateModelAttribute(pageNum, page);
    }

    public void searchProducts(int pageNum, PagingAndSortingHelper helper) {
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();
        Page<Product> page = productRepository.searchProductsByName(keyword, pageable);
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

        return productRepository.save(product);

    }

    public void savedProductPrice(Product productInForm) {
        Product productInDB = productRepository.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());

        productRepository.save(productInDB);

    }

    public String checkName(Integer id, String name) {
        Product product = productRepository.findByName(name);

        if(product == null) return "OK";

        if(id == null) return "Duplicate";

        return product.getId().equals(id) ? "OK" : "Duplicate";
    }

    public void updateStatus(Integer id, Boolean status) throws ProductNotFoundException {
        Optional<Product> saveProduct = productRepository.findById(id);
        if(saveProduct.isEmpty()) {
            throw new ProductNotFoundException("Could not find any product with id " + id );
        }
        else {
            productRepository.updateProductStatus(id, status);
        }

    }

    public void deleteProduct(Integer id) throws ProductNotFoundException {
        Optional<Product> saveProduct = productRepository.findById(id);
        if(saveProduct.isEmpty()) {
            throw new ProductNotFoundException("Could not find any product with id " + id);
        }
        else {
            productRepository.deleteById(id);
        }
    }

    public Product get(Integer id ) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).get();
        }catch (NoSuchElementException ex) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }






}
