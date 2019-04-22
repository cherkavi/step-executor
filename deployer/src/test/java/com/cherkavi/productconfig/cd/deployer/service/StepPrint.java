package com.cherkavi.productconfig.cd.deployer.service;

import java.util.Map;

public class StepPrint implements StepContext.Step {

    private Counter counter;

    public StepPrint(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void execute(Map<String, Object> parameters) {
        this.counter.value++;
    }
}
