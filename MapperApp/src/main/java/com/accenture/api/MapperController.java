package com.accenture.api;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.service.MapperService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/mapperapp/v1")
public class MapperController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperController.class);

    @Autowired
    private MapperService mapperService;

    @GetMapping(value="/getFieldMapping/{partnerName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode mapRequestJSON(@PathVariable("partnerName") String partnerName){
        return mapperService.getMenu(partnerName);
    }

}
