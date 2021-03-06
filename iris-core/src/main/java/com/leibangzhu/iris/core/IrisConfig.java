package com.leibangzhu.iris.core;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class IrisConfig {

    private static Map<String,String> configs = new LinkedHashMap<>();
    private static boolean initialized = false;

    private static void load() throws Exception {
        // load from classpath:/iris.properties
        Properties properties = new Properties();
        InputStream in = IrisConfig.class.getClassLoader().getResourceAsStream("iris.properties");
        if (in == null) {
            System.out.println("没有 iris.properties 配置，全部按默认配置处理");
            return;
        }
        properties.load(in);
        for (Map.Entry<Object,Object> entry :  properties.entrySet()){
            configs.put((String) entry.getKey(),(String) entry.getValue());
        }

        // load from application.properties
        // ...
    }

    public static void set(String key, String value) {
        configs.put(key, value);
    }

    public static void set(String key, int value) {
        configs.put(key, String.valueOf(value));
    }



    public static String get(String key){
        if (!initialized){
            try {
                load();
                initialized = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return configs.get(key);
    }

    public static String get(String key, String def) {
        String value = get(key);
        if (value == null || value.isEmpty()) {
            value = def;
        }
        return value;
    }

    public static int get(String key, int def) {
        String value = get(key);
        if (value == null || value.isEmpty()) {
            return def;
        }
        return Integer.parseInt(value);
    }
}
