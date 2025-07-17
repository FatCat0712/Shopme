package com.shopme.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class FileUploadUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadUtil.class);

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);


        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try(InputStream inputStream = multipartFile.getInputStream()) {
            if(fileName.startsWith("/site-logo/")) fileName = fileName.replace("/site-logo/", "");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException exception) {
            throw new IOException("Could not save file: " + fileName, exception);
        }
    }

    public static void cleanDir(String dir) throws IOException {
        Path dirPath = Paths.get(dir);
        try {
            Files.list(dirPath).forEach(file -> {
                if(!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        LOG.error("Could not delete file" + file.getFileName());
                    }
                }
            });
        } catch (IOException e) {
            LOG.error("Could not list directory " + dirPath);
        }
    }

    public static void removeDir(String dir) {
        Path dirPath = Paths.get(dir);
        try {
           cleanDir(dir);
            Files.delete(dirPath);
        } catch (IOException e) {
            LOG.error("Could not delete directory " + dirPath);
        }
    }
}
