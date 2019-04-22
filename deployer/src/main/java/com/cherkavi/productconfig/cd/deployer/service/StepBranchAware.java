package com.cherkavi.productconfig.cd.deployer.service;

import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class StepBranchAware implements StepContext.Step {
    private final static Logger LOGGER = LoggerFactory.getLogger(StepBranchAware.class);

    private final static String VAR_DELIMITER_BEGIN="%(";
    private final static String VAR_DELIMITER_END=")%";

    public static final String PARAM_BRANCH = "branch";

    private Map<String, Object> parameters;

    @Override
    public void execute(Map<String, Object> parameters) {
        this.parameters = parameters;
        checkParameters();
        execute(getBranch());
    }

    protected String getBranch() {
        return (String)this.parameters.get(PARAM_BRANCH);
    }

    protected abstract void execute(String branchName);

    protected <T> T setContextParameter(String parameterName, T value) {
        LOGGER.debug("new parameter into context with name: " + parameterName + "   with value: " + value);
        this.parameters.put(parameterName, value);
        return value;
    }

    protected <T> T getContextParameter(String parameterName) {
        T returnValue = (T)this.parameters.get(parameterName);
        if(returnValue == null){
            return null;
        }
        if(returnValue instanceof String){
            StrSubstitutor substitutor = new StrSubstitutor(this.getContextParameters(), VAR_DELIMITER_BEGIN, VAR_DELIMITER_END);
            return (T)substitutor.replace((String)returnValue);
        }
        if(returnValue instanceof List){
            List<Object> returnList = new ArrayList<>();
            StrSubstitutor substitutor = new StrSubstitutor(this.getContextParameters(), VAR_DELIMITER_BEGIN, VAR_DELIMITER_END);
            for(Object each: (List)returnValue){
                if(each instanceof String){
                    returnList.add(substitutor.replace((String)each));
                }else{
                    returnList.add(each);
                }
            }
            return (T)returnList;
        }
        return returnValue;
    }

    protected Map<String, Object> getContextParameters(){
        return this.parameters;
    }

    /**
     * list of parameters that should be present into context to be retrieved during execution
     * @return
     */
    protected List<String> dependsOnParametersByName(){
        return new ArrayList<>(0);
    }

    private void checkParameters() {
        for(String mandatoryParameterName : this.dependsOnParametersByName()){
            if(!this.parameters.containsKey(mandatoryParameterName)){
                LOGGER.error(String.format("can't execute next step %s because mandatory parameters is not present: %s", this.getClass().getSimpleName(), mandatoryParameterName));
                throw new IllegalArgumentException(String.format("can't execute next step %s because mandatory parameters is not present: %s", this.getClass().getSimpleName(), mandatoryParameterName));
            }
        }
    }

}
