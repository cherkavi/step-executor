package com.cherkavi.productconfig.cd.deployer.service;

import java.util.List;
import java.util.Map;

public class StepWithParameters {
    private final String className;
    private final Map<String, Object> parameters;

    public StepWithParameters(String className, Map<String, Object> parameters) {
        this.className = className;
        this.parameters = parameters;
    }

    public static StepWithParameters getByClassName(List<StepWithParameters> inputParameters, String name) {
        return inputParameters.stream()
                .filter(p->p.className.equals(name)).findFirst().orElse(null);
    }

    public String getClassName() {
        return className;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "StepWithParameters{" +
                "className='" + className + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}

