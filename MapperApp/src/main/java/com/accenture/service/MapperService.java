package com.accenture.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component public class MapperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperService.class);

    public JsonNode getMenu(String restaurantName) {

        JSONObject jsonSpecMap = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode specNode = null;
        JSONArray joltSpec = new JSONArray();
        try {

            String mappingFileName = "fieldmapping" + restaurantName + ".json";
            String inputFileName = "partnerRequest" + restaurantName + ".json";
            Map<String, String> mapping = new HashMap<>();

            JsonNode fieldMappings = objectMapper.readTree(
                    this.getClass().getClassLoader().getResourceAsStream(mappingFileName));
            mapping = objectMapper.convertValue(fieldMappings, new TypeReference<Map<String, String>>() {

            });

            JSONObject specObject = new JSONObject();
            jsonSpecMap.put("operation", "shift");

            JSONObject jsonDirectNodes = new JSONObject();
            JSONObject jsonStar = new JSONObject();

            for (Map.Entry<String, String> ent : mapping.entrySet()) {
                String[] tokens = ent.getValue().split("\\.");
                if (1 == tokens.length) {
                    jsonDirectNodes.put(ent.getValue(), ent.getKey());
                } else {
                    int depth = tokens.length - 1;
                    //count of dots would be no of tokens-1
                    String lastTokenKey = ent.getKey()
                                             .substring(ent.getKey().lastIndexOf(".") + 1, ent.getKey().length());
                    String lastTokenValue = ent.getValue()
                                               .substring(ent.getValue().lastIndexOf(".") + 1, ent.getValue().length());
                    JSONObject innerJson = new JSONObject();
                    if (jsonDirectNodes.has(tokens[0])) {
                        jsonStar = (JSONObject) jsonDirectNodes.get(tokens[0]);
                        innerJson = fetchInnerNode(depth, jsonStar);
                        innerJson.put(lastTokenValue,
                                (ent.getKey().substring(0, ent.getKey().indexOf('['))) + "[#" + depth + "]."
                                        + lastTokenKey);

                    } else {
                        innerJson.put(lastTokenValue,
                                (ent.getKey().substring(0, ent.getKey().indexOf('['))) + "[#" + depth + "]."
                                        + lastTokenKey);
                        jsonStar = createInnerNode(depth, innerJson, jsonStar);
                    }

                    jsonDirectNodes.put(tokens[0], jsonStar);
                }
            }
            specObject = jsonDirectNodes;
            jsonSpecMap.put("spec", specObject);
            joltSpec.put(jsonSpecMap);
            specNode = objectMapper.readTree(joltSpec.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return specNode;
    }

    private JSONObject createInnerNode(int depth, JSONObject innerJson, JSONObject jsonn) {

        try {
            if (depth == 1) {
                jsonn = jsonn.put("*", innerJson);
            } else {
                jsonn = jsonn.put("*", createInnerNode(depth - 1, innerJson, new JSONObject()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jsonn;
    }

    private JSONObject fetchInnerNode(int depth, JSONObject jsonStar) {

        try {
            if (depth == 1) {
                return (JSONObject) jsonStar.get("*");
            } else {
                return fetchInnerNode(depth - 1, (JSONObject) jsonStar.get("*"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
