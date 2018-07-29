package com.apiframework.common.utility;

import com.apiframework.common.logging.FrameWorkLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {

    private static String filePath;
    private static File file;

    /**
     * Method to get propertie from file
     *
     * @param file -->Property File
     * @param key  --> peoperty value
     * @return property value
     */


    public static String getProperty(String file, String key) {
        try {
            file = System.getProperty("user.dir") + "/resources/" + file;
            File config = new File(file);
            FileInputStream input;
            input = new FileInputStream(config.getAbsolutePath());
            Properties prop = new Properties();
            prop.load(input);
            String value = prop.getProperty(key);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * Method to get propertie map from property file
     *
     * @return property map
     */

    public static Map<String, String> getServerConfigMap() {
        try {

            String path = System.getProperty("user.dir") + "/resources/ProjectConfig.properties";
            FrameWorkLogger.logStep("PATH  IS "+path );
            File config = new File(path);
            FileInputStream input;
            input = new FileInputStream(config.getAbsolutePath());
            Properties prop = new Properties();
            prop.load(input);
            Map<String, String> properties = new HashMap<>();
            for (Object key : prop.keySet()) {
                properties.put(key.toString(), prop.getProperty(key.toString()));
            }
            input.close();
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * Method to get propertie map
     *
     * @param Server ExecutionServer
     * @return property map
     */

    public static Map<String, String> getPropertyMap(String Server) {
        try {
            String path = System.getProperty("user.dir") + "/resources/ServerURL/" + Server + "/config.properties";
            FrameWorkLogger.logStep("complete path is "+path);
            File config = new File(path);
            FileInputStream input;
            input = new FileInputStream(config.getAbsolutePath());
            Properties prop = new Properties();
            prop.load(input);
            Map<String, String> properties = new HashMap<>();
            for (Object key : prop.keySet()) {
                properties.put(key.toString(), prop.getProperty(key.toString()));
            }
            input.close();
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    protected static void getBaseURI()
    {


    }

    /**
     * Method to get File Name
     *
     * @return file
     * @Param fileName
     */

    public static File getFileName(String fileName)

    {
        filePath = System.getProperty("user.dir") + "/resources/uploadTestData/" + fileName;
        file = new File(filePath);
        return file;
    }


    /**
     * Method to get File length
     *
     * @return int
     * @Param fileName
     */

    public static long getFileSize(String fileName) throws IOException

    {

        InputStream input = null;
        input = new FileInputStream(Utils.getFileName(fileName));
        long length = ((FileInputStream) input).getChannel().size();
        input.close();

        return length;


    }
}


