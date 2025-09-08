package com.shopme.product;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.shopme.product.ProductSpecs.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public static final int PRODUCTS_PER_PAGE = 12;
    public static final int SEARCH_RESULTS_PER_PAGE = 10;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> listAll(int pageNum, ProductCriteriaDTO productCriteriaDTO) {
        Specification<Product> combinedSpec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        Pageable pageable = PageRequest.of(pageNum -1, PRODUCTS_PER_PAGE);

        if(productCriteriaDTO.getBrand() != null) {
            String[] brandList = productCriteriaDTO.getBrand().split(",");
            List<String> targetList = Arrays.asList(brandList);
            Specification<Product> currentSpecs = matchBrandList(targetList);
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if(productCriteriaDTO.getCategory() != null) {
            String[] categoryList = productCriteriaDTO.getCategory().split(",");
            List<String> targetList = Arrays.asList(categoryList);
            Specification<Product> currentSpecs = matchCategoryList(targetList);
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        try {
            if(productCriteriaDTO.getInStock() != null && Boolean.parseBoolean(productCriteriaDTO.getInStock())) {
                Specification<Product> currentSpecs = isInStock();
                combinedSpec = combinedSpec.and(currentSpecs);
            }

            if(productCriteriaDTO.getOnSales() != null &&  Boolean.parseBoolean(productCriteriaDTO.getOnSales())) {
                Specification<Product> currentSpecs = isOnSale();
                combinedSpec = combinedSpec.and(currentSpecs);
            }

            if(productCriteriaDTO.getRating() != null ) {
               Integer rating =  Integer.parseInt(productCriteriaDTO.getRating());
               Specification<Product> currentSpecs = matchRating(rating);
               combinedSpec = combinedSpec.and(currentSpecs);
            }

        }catch (RuntimeException exception) {
            return productRepository.findAll(pageable);
        }

        return productRepository.findAll(combinedSpec, pageable);
    }

    public Page<Product> listByCategory(int pageNumber, Integer categoryId) {
        String categoryMatch = "-" + categoryId + "-";
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, categoryMatch, pageable);
    }

    public List<Product> listRelatedProductByCategory(Integer categoryId, Integer currentProductId) {
        String categoryMatch = "-" + categoryId + "-";
        return productRepository.listByCategory(categoryId, categoryMatch, currentProductId);
    }

    public Page<Product> listByBrand(int pageNumber, Integer brandId) {
        Pageable pageable = PageRequest.of(pageNumber -1, PRODUCTS_PER_PAGE);
        return productRepository.listByBrand(brandId, pageable);
    }

    public Product get(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if(product == null) {
            throw new ProductNotFoundException("Could not found any product with alias: " + alias);
        }
        return product;
    }

    public Product get(Integer id) throws ProductNotFoundException {
        Optional<Product> savedProduct= productRepository.findById(id);
        if(savedProduct.isEmpty()) {
            throw new ProductNotFoundException("Could not found any product with ID: " + id);
        }
        return savedProduct.get();
    }



    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum -1, SEARCH_RESULTS_PER_PAGE);
         return productRepository.search(keyword, pageable);
    }

}
