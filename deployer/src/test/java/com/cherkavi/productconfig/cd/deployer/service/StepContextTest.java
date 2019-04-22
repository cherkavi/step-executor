package com.cherkavi.productconfig.cd.deployer.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class StepContextTest {

    @Mock
    ApplicationContext context;

    @Test
    public void context(){
        // given
        StepContext stepContext = StepContext.create(new AbstractMap.SimpleImmutableEntry<>(StepBranchAware.PARAM_BRANCH, "test"))
                .setContext(context);
        Counter counter=new Counter();

        // when
        stepContext.execute(buildParameters(counter), ()->"OK");

        // then
        Assert.assertNotNull(stepContext.getCurrentStep());
        Assert.assertTrue(stepContext.getCurrentStep() instanceof  StepPrint);
        Assert.assertEquals(1, counter.value);
    }

    @Test
    public void contextMultiExecution(){
        // given
        StepContext stepContext = StepContext.create(new AbstractMap.SimpleImmutableEntry<>(StepBranchAware.PARAM_BRANCH, "test"))
                .setContext(context);
        Counter counter=new Counter();

        // when
        stepContext.execute(buildParameters(counter), ()->"OK");

        // then
        Assert.assertNotNull(stepContext.getCurrentStep());
        Assert.assertTrue(stepContext.getCurrentStep() instanceof  StepPrint);
        Assert.assertEquals(1, counter.value);


        // given
        counter = new Counter();

        // when
        List<StepWithParameters> parameters = buildParameters(counter);
        parameters.addAll(buildParameters(counter));
        stepContext.execute(parameters, ()->"OK");

        // then
        Assert.assertNotNull(stepContext.getCurrentStep());
        Assert.assertTrue(stepContext.getCurrentStep() instanceof  StepPrint);
        Assert.assertEquals(2, counter.value);
    }

    private List<StepWithParameters> buildParameters(Counter counter){
        ArrayList<StepWithParameters> returnValue = new ArrayList<StepWithParameters>();
        returnValue.add(new StepWithParameters("com.cherkavi.productconfig.cd.deployer.service.StepPrint", new HashMap<>()));
        Mockito.when(context.getBean(Mockito.anyString())).thenReturn(new StepPrint(counter));
        Mockito.when(context.getBean(StepPrint.class)).thenReturn(new StepPrint(counter));
        return returnValue;
    }
}

class Counter {
    int value;
}