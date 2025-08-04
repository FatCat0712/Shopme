package com.shopme.admin;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Slf4j
public class SupabaseS3Util {
    private static final String BUCKET_NAME;
    private static final String SUPABASE_REGION;
    private static final String SUPABASE_ACCESS_KEY_ID;
    private static final String SUPABASE_SECRET_ACCESS_KEY;

    static {
        BUCKET_NAME = System.getenv("SUPABASE_BUCKET_NAME");
        SUPABASE_REGION = System.getenv("SUPABASE_REGION");
        SUPABASE_ACCESS_KEY_ID = System.getenv("SUPABASE_ACCESS_KEY_ID");
        SUPABASE_SECRET_ACCESS_KEY = System.getenv("SUPABASE_SECRET_ACCESS_KEY");

    }

    private static S3Client createClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(SUPABASE_ACCESS_KEY_ID, SUPABASE_SECRET_ACCESS_KEY);
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(SUPABASE_REGION))
                .endpointOverride(URI.create("https://vsovwlewklunhjemktxg.storage.supabase.co/storage/v1/s3"))
                .forcePathStyle(true)
                .build();
    }

    public static List<String> listFolder(String folderName) {
       S3Client client = createClient();
        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME)
                .prefix(folderName)
                .build();
        ListObjectsResponse response = client.listObjects(listRequest);

        List<S3Object> contents  = response.contents();
        ListIterator<S3Object> listIterator = contents.listIterator();

        List<String> listKeys = new ArrayList<>();

        while(listIterator.hasNext()) {
            S3Object object =listIterator.next();
            listKeys.add(object.key());
        }

        return listKeys;
    }

    public static void uploadFile(String folderName, String fileName, InputStream inputStream) {
        S3Client client = createClient();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(folderName + "/" + fileName)
                .contentType("image/png")
                .acl("public-read")
                .build();

        try(inputStream) {
            int contentLength = inputStream.available();
            client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public static void deleteFile(String fileName) {
        S3Client client = createClient();

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .build();

        client.deleteObject(request);
    }

    public static void removeFolder(String folderName) {
        S3Client client = createClient();
        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME)
                .prefix(folderName)
                .build();

        ListObjectsResponse response = client.listObjects(listRequest);

        List<S3Object> contents  = response.contents();

        for (S3Object object : contents) {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(object.key())
                    .build();
            client.deleteObject(request);
        }


    }
}
