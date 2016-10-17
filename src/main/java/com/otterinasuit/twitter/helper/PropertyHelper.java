package com.otterinasuit.twitter.helper;

import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyHelper {
    private static PropertyHelper _instance;
    private Properties prop;
    private long seed;
    private static String path;

    private PropertyHelper(String path){
        this.path = path;
        _instance = this;
        this.prop = readConfig();
    }

    public static PropertyHelper getInstance(String _path){
        if(_instance != null && !StringUtils.isEmpty(path) && !StringUtils.isEmpty(_path)
                && path.equals(_path)) return _instance;
        else return new PropertyHelper(_path);
    }

    public Properties getProperties(){
        return prop;
    }

    /**
     * Read properties from "auth.properties"
     * TODO: move to hdfs
     * @return Properties
     */
    public Properties readConfig(){
        prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(path);

            // load a properties file
            prop.load(input);

            input.close();

            return prop;

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Get score.optionN
     * @param n score.option1, ... n
     * @return
     */
    public String getOptionN(int n){
        return prop.getProperty("score.option"+n, "DefaultScore"+n);
    }
}
