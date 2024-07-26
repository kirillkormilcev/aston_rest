package ru.aston.rest_service.utility;

import java.util.Properties;
import java.io.InputStream;

public class PropertiesUtil {
    private static final Properties PROPERTIES_UTIL = new Properties();
    private static final String PROPERTIES_FILE = "application.properties";

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    public static String getProperties(String key) {
        return PROPERTIES_UTIL.getProperty(key);
    }

    private static void loadProperties() {
        try (InputStream inFile = PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            PROPERTIES_UTIL.load(inFile);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }
}