package com.shopme.admin.product;

import com.shopme.admin.SupabaseS3Util;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.brand.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import static com.shopme.admin.product.ProductSaveHelper.*;

@Controller
public class ProductController {
    private final BrandService brandService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final String defaultURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";

    @Autowired
    public ProductController(BrandService brandService, ProductService productService, CategoryService categoryService) {
        this.brandService = brandService;
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String getProducts() {
        return defaultURL;
    }


    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "pageNum") Integer pageNum,
            @PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            Model model
    ){

        productService.listByPage(pageNum, helper, categoryId);

        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        if(categoryId != null) {
            model.addAttribute("categoryId", categoryId);
        }

        model.addAttribute("listCategories", listCategories);


        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("currentExtraImagesIndex", 0);

        return "products/product_form";
    }



    @PostMapping("/products/save")
    public String saveProduct(
            Product product,
            @RequestParam(name = "fileImage", required = false) MultipartFile mainImageMultipart,
            @RequestParam(name = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
            @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
            @RequestParam(name = "detailNames", required = false) String[] detailNames,
            @RequestParam(name = "detailValues", required = false) String[] detailValues,
            @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
            @RequestParam(name = "imageNames", required = false) String[] imageNames,
            @AuthenticationPrincipal ShopmeUserDetails loggedUser,
            RedirectAttributes redirectAttributes
    ) throws IOException {
            String message;
            if(product.getId() != null) {
                message = String.format("The product with ID %d has been updated", product.getId());
            }
            else {
                message = "The product has been created";
            }

             redirectAttributes.addFlashAttribute("message", message);
            if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
                if(loggedUser.hasRole("Salesperson")) {
                    try {
                        productService.savedProductPrice(product);
                    } catch (ProductNotFoundException e) {
                        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                        return defaultURL;
                    }
                }
            }
            else {
                setMainImageName(mainImageMultipart, product);

                setExistingExtraImageNames(imageIDs, imageNames, product);

                setNewExtraImageName(extraImageMultiparts, product);

                setProductDetails(detailIDs, detailNames, detailValues, product);

                Product savedProduct = productService.save(product);

                saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

                deleteExtraImagesWereRemovedOnForm(product);

            }
             return defaultURL;
    }



    @GetMapping("/products/{id}/enabled/{status}")
    public String enableProduct(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") Boolean status, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            productService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("message", String.format("The product with id %d has been %s successfully",id , status ? "enabled" : "disabled"));
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        }
        return defaultURL;
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            String productExtraImagesDir = "product-images/" + id + "/extras";
            String productMainImageDir =  "product-images/" + id;

            SupabaseS3Util.removeFolder(productExtraImagesDir);
            SupabaseS3Util.removeFolder(productMainImageDir);
            redirectAttributes.addFlashAttribute("message", String.format("The product with id %d has been deleted successfully", id));
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultURL;
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(
            @PathVariable(name = "id") Integer id,
            @AuthenticationPrincipal ShopmeUserDetails loggedInUser,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            int currentExtraImagesCount  = product.getImages().size();

            boolean isReadOnlyForSalesperson = false;
            if(!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Editor")) {
                if(loggedInUser.hasRole("Salesperson")) {
                        isReadOnlyForSalesperson = true;
                }
            }

            model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("currentExtraImagesIndex", currentExtraImagesCount);
            model.addAttribute("pageTitle", String.format("Edit Product (ID: %d)", id));
            return "products/product_form";
        }catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return defaultURL;
        }


    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/product_detail_modal";
        }catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return defaultURL;
        }

    }





}

