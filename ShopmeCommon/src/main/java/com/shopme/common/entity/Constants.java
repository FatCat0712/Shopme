package com.shopme.common.entity;

public class Constants {
    public static final String SUPABASE_URI;

    static  {
        String projectId = System.getenv("SUPABASE_PROJECT_ID");
        String bucketName = System.getenv("SUPABASE_BUCKET_NAME");
        String pattern = "https://%s.storage.supabase.co/storage/v1/object/public/%s";

        SUPABASE_URI = String.format(pattern, projectId, bucketName);
    }

    public static void main(String[] args) {
        System.out.println(SUPABASE_URI);
    }

}
