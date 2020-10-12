package com.accenture.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MappingUtil {

    public Map<String,Object> createJsonResponse(JSONObject inputJson, Map mapping, Map responseMap, boolean innerFlag, String parent) {

        Iterator<?> keys = inputJson.keys();
        while (keys.hasNext()) {
            String nextKey = (String) keys.next();
            if (inputJson.get(nextKey) instanceof JSONObject) {
                JSONObject jsonObject = inputJson.getJSONObject(nextKey);
                Map<String, Object> innerMap = new HashMap<>();
                jsonObject.keys().forEachRemaining(key -> {
                    if (mapping.containsKey(nextKey + "." + key)) {
                        Object value = jsonObject.get(key);
                        innerMap.put(mapping.get(nextKey + "." + key).toString(), value);
                    }
                });
                responseMap.put(mapping.get(nextKey), innerMap);
            } else if (inputJson.get(nextKey) instanceof JSONArray) {
                List<Map<String, Object>> jsonList = new ArrayList<>();
                JSONArray jsonarray = inputJson.getJSONArray(nextKey);
                for (int i = 0; i < jsonarray.length(); i++) {
                    String jsonarrayString = jsonarray.get(i).toString();
                    JSONObject innerJSON = new JSONObject(jsonarrayString);
                    Map<String, Object> innerMap = new HashMap<>();
                    if (innerJSON.length() > 0) {
                        innerMap = createJsonResponse(innerJSON, mapping, innerMap, true, nextKey + ".");
                    }
                    jsonList.add(innerMap);
                }
                responseMap.put(mapping.get(nextKey), jsonList);
            } else {
                if (innerFlag == true) {
                    if (mapping.containsKey(parent + nextKey)) {
                        responseMap.put(mapping.get(parent + nextKey).toString(), inputJson.get(nextKey).toString());
                    }
                } else {
                    if (mapping.containsKey(nextKey)) {
                        responseMap.put(mapping.get(nextKey).toString(), inputJson.get(nextKey).toString());
                    }
                }
            }
        }

      return responseMap;
    }

}
