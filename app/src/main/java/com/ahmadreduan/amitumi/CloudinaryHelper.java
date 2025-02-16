package com.ahmadreduan.amitumi;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.Map;

public class CloudinaryHelper {
    private static final String CLOUD_NAME = "chatappreduan";
    private static final String API_KEY = "918383899597893";
    private static final String API_SECRET = "M8_DqGMQu2Y2xir6j-I8GQCaE4";

    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", CLOUD_NAME,
                    "api_key", API_KEY,
                    "api_secret", API_SECRET
            ));
        }
        return cloudinary;
    }
}
