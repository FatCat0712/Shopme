package com.shopme.common.entity;

public class Constants {
    public static final String SUPABASE_URI;

    static  {
        String projectId = "vsovwlewklunhjemktxg";
        String bucketName = "shopme-files";
        String pattern = "https://%s.storage.supabase.co/storage/v1/object/public/%s";

        SUPABASE_URI = String.format(pattern, projectId, bucketName);
    }

}
