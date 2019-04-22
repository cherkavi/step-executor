package com.cherkavi.productconfig.cd.deployer.controller;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import com.cherkavi.productconfig.cd.deployer.service.StepContext;
import com.cherkavi.productconfig.cd.deployer.service.StepWithParameters;
import com.cherkavi.productconfig.cd.deployer.step.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.InvalidParameterException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StepExecutor {
    private final static Logger LOGGER = LoggerFactory.getLogger(StepExecutor.class);
    @Autowired
    ApplicationContext context;

    @Autowired
    FailRecord failRecord;

    @Value("#{JSONReader.parse(JSONReader.readFile('${steps}'))}")
    List<StepWithParameters> steps;

    @Value("#{JSONReader.parse(JSONReader.readFile('${failSteps}'))}")
    List<StepWithParameters> failSteps;

    // curl -X POST -F "branch=feature-OPM-nojira" http://localhost:8080/proceed
    @PostMapping(path="/proceed")
    @ResponseBody
    public String stepExecutor(@RequestParam("branch") String branch){
        StepContext stepContext = StepContext.create(new AbstractMap.SimpleImmutableEntry<>(StepBranchAware.PARAM_BRANCH, branch)).setContext(context);
        try{
            String returnValue = stepContext.execute(steps, () -> "OK");
            LOGGER.info(">>> executed successfully <<< for branch: "+branch);
            return returnValue;
        }catch(RuntimeException ex){
            LOGGER.error("can't execute sequence of steps, issue with: "+stepContext.getCurrentStep()+" message:"+ex.getMessage());
            StepContext failureContext = StepContext.create(new AbstractMap.SimpleImmutableEntry<>(StepBranchAware.PARAM_BRANCH, branch)).setContext(context);
            failureContext.execute(failSteps, () -> "");
            // change status to failure
            throw new RuntimeException(String.format(
                    "execution stopped on step: %s with exception: %s ",
                    stepContext.getCurrentStep(), ex.getMessage()));
        }
    }

    private void setFailRecord(String branch) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("branch", branch);
        failRecord.execute(parameters);
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> exceptionHandling(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

}
