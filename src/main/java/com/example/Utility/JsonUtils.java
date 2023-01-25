package com.example.Utility;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonUtils {

    private static Gson gson = new Gson();

    public static String mapToJson(Map<String, String> map) {
        return gson.toJson(map);
    }

    public static Map<String, String> jsonToMap(String json) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        return gson.fromJson(json, type);
    }
}

