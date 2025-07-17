package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProductSaveHelper  {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    static void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImageDir = "ShopmeWebParent/product-images/" + product.getId() + "/extras";
        Path extraImageDirPath = Paths.get(extraImageDir);

        try {
            Files.list(extraImageDirPath).forEach(file -> {
                String fileName = file.toFile().getName();
                if(!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                        log.info("Deleted extra image file: " + fileName);
                    } catch (IOException e) {
                        log.error("Could not delete extra image: " + fileName);
                    }
                }
            });
        } catch (IOException e) {
            log.info("Could not list directory: " + extraImageDir);
        }
    }

    static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if(imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images =new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            int id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if(detailNames == null || detailNames.length == 0) return;
        for(int i = 0; i < detailNames.length; i++) {
            String detailName = detailNames[i];
            String detailValue = detailValues[i];
            int id = Integer.parseInt(detailIDs[i]);

            if(id != 0) {
                product.addDetail(id, detailName, detailValue);
            }
            else  if(!detailName.isEmpty() && !detailValue.isEmpty()) {
                product.addDetail(detailName, detailValue);
            }

        }
    }

    static void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts, Product savedproduct) throws IOException {
        if(!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            String uploadDir = "ShopmeWebParent/product-images/" + savedproduct.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if(extraImageMultiparts.length > 0) {
            String uploadDir = "ShopmeWebParent/product-images/" + savedproduct.getId() + "/extras";
            for(MultipartFile multipartFile : extraImageMultiparts) {
                if(multipartFile.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }

    }

    static void setNewExtraImageName(MultipartFile[] extraImageMultipart, Product product) {
        for (MultipartFile multipartFile : extraImageMultipart) {
            if (!multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                if(!product.containsImageName(fileName)){
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if(!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            product .setMainImage(fileName);
        }
    }
}
