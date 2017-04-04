package com.android.efforts.customclass;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by jonat on 04/04/2017.
 */

public class JWTUtils {

    public static String decoded(String JWTEncoded) throws Exception {
        String bodyResult = "";
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
            bodyResult = getJson(split[1]);
        } catch (UnsupportedEncodingException e) {
            //Error
        }

        return bodyResult;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
