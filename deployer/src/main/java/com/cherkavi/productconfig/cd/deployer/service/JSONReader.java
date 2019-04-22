package com.cherkavi.productconfig.cd.deployer.service;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component("JSONReader")
public class JSONReader {
    private final static Logger LOGGER = LoggerFactory.getLogger(JSONReader.class);
    private final static JSONArray EMPTY_RESULT=new JSONArray("[]");

    public JSONArray readFile(String pathToFile){
        File source = new File(pathToFile);
        if(!source.exists()){
            throw new IllegalStateException();
        }
        try {
            return new JSONArray(FileUtils.readFileToString(source, "utf-8"));
        } catch (IOException e) {
            LOGGER.error("can't read JSON array from text file: "+pathToFile);
            return EMPTY_RESULT;
        }
    }

    public List<StepWithParameters> parse(JSONArray sourceArray){
        List<StepWithParameters> returnValue = new ArrayList<>();
        for(int index=0; index<sourceArray.length(); index++){
            Object next = sourceArray.get(index);
            if(!(next instanceof JSONObject)){
                continue;
            }
            JSONObject nextObject = (JSONObject)next;
            returnValue.add(new StepWithParameters((String)nextObject.get("class"), nextObject.getJSONObject("parameters").toMap()));
        }
        return returnValue;
    }

}
