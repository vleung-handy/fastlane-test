package com.handy.portal.data;

import android.content.Context;
import android.content.res.AssetManager;

import java.util.Properties;

public final class PropertiesReader {
    public static Properties getProperties(Context context, String fileName) {
        final Properties properties = new Properties();
        final AssetManager am = context.getAssets();
        try{
            properties.load(am.open(fileName));
        } catch (Exception e) {
            throw new RuntimeException("Error loading properties file: " + fileName);
        }
        return properties;
    }
}
