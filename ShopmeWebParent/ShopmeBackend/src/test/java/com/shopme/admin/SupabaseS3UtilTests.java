package com.shopme.admin;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class SupabaseS3UtilTests {
    @Test
    public void testListFolder() {
        String folderName = "product-images/10/";
        List<String> listKeys = SupabaseS3Util.listFolder(folderName);
        listKeys.forEach(System.out::println);
    }

    @Test
    public void testUploadFile() {
        String folderName = "test-upload";
        String fileName = "file.zip";
        String filePath = "D:\\" + fileName;
        try {
            InputStream inputStream = new FileInputStream(filePath);
            SupabaseS3Util.uploadFile(folderName, fileName, inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testDeleteFile() {
        String fileName = "test-upload/file.zip";
        SupabaseS3Util.deleteFile(fileName);
    }

    @Test
    public void testRemoveFolder() {
        String folderName = "test-upload";
        SupabaseS3Util.removeFolder(folderName);
    }


}
