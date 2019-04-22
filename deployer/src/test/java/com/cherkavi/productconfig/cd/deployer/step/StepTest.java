package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepContext;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public abstract class StepTest {

    protected void executeWithParameters(Class<? extends StepContext.Step> clazz, Map<String, Object> additionalParameters){
        executeWithParameters(clazz, null, additionalParameters);
    }

    protected void executeWithParameters(Class<? extends StepContext.Step> clazz, String branchName, Map<String, Object> additionalParameters){
        Map<String, Object> parameters =new HashMap<>();
        parameters.put("branch", (branchName==null)?"test":branchName);
        parameters.putAll(additionalParameters);
        try {
            clazz.newInstance().execute(parameters);
        } catch (InstantiationException|IllegalAccessException e) { }
    }

}