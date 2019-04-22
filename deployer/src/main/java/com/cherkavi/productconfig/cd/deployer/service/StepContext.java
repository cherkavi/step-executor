package com.cherkavi.productconfig.cd.deployer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * state object with execution of predefined {@class StepWithParameters}
 */
public class StepContext {
    private final static Logger LOGGER = LoggerFactory.getLogger(StepContext.class);
    @FunctionalInterface
    public interface Step {
        void execute(Map<String, Object> parameters);
    }

    private final Map<String, Object> parameters;
    private ApplicationContext context;
    private Step currentStep;

    public StepContext(Map<String, Object> parameters){
        this.parameters = new HashMap<>(parameters);
    }

    public StepContext setContext(ApplicationContext context){
        this.context = context;
        return this;
    }

    public static StepContext create(Map.Entry<String, Object> ... parameters){
        Map<String, Object> contextParameters = new HashMap<>();
        if(parameters!=null && parameters.length>0){
            for(Map.Entry<String, Object> externalParameter : parameters){
                contextParameters.put(externalParameter.getKey(), externalParameter.getValue());
            }
        }
        return new StepContext(contextParameters);
    }

    @FunctionalInterface
    public interface Result<T>{
        T obtain();
    }

    /**
     * execute steps and return result, when reach the end of the steps
     * @param predefinedSteps
     * @param positiveResultExecutor
     * @param <T>
     * @return
     */
    public <T> T execute(List<StepWithParameters> predefinedSteps, Result<T> positiveResultExecutor) {
        predefinedSteps.forEach(step->{
            LOGGER.info("next step: "+step);
            setParametersFromExternalSettings(this.parameters, step);
            StepContext.Step nextStep;
            try{
                nextStep = (StepContext.Step)this.context.getBean(Class.forName(step.getClassName()));
            }catch(BeansException | ClassNotFoundException be){
                LOGGER.error("can't find step object with class: "+step.getClassName());
                throw new IllegalArgumentException("can't find step with class: "+step.getClassName(), be);
            }
            this.currentStep=nextStep;
            nextStep.execute(this.parameters);
        });
        return positiveResultExecutor.obtain();
    }

    private void setParametersFromExternalSettings(Map<String, Object> parameters, StepWithParameters inputParameters) {
        if(inputParameters==null){
            return;
        }
        parameters.putAll(inputParameters.getParameters());
    }

    public Step getCurrentStep() {
        return this.currentStep;
    }

}
