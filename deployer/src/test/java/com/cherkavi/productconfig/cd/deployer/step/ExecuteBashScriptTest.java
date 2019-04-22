package com.cherkavi.productconfig.cd.deployer.step;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ExecuteBashScriptTest extends StepTest{

    @Test
    public void executeCommandLine(){
        // given
        Instant beforeExecution = Instant.now();
        int secondsDelay = 3;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ExecuteBashScript.PARAM_BASH_SCRIPT,"python -c \"import time; time.sleep("+secondsDelay+");print('done')\"");

        // when
        this.executeWithParameters(ExecuteBashScript.class, parameters);

        // then elapsed time will be more than execution time
        Instant afterExecution = Instant.now();
        Assert.assertTrue(afterExecution.minusSeconds(secondsDelay).isAfter(beforeExecution));
        Assert.assertTrue(afterExecution.minusSeconds(secondsDelay+1).isBefore(beforeExecution));
    }

    @Test
    public void executeCommandLineWithParameter(){
        // given
        Instant beforeExecution = Instant.now();
        int secondsDelay = 3;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("seconds_to_delay", secondsDelay);
        parameters.put(ExecuteBashScript.PARAM_BASH_SCRIPT,"python -c \"import time; time.sleep(%(seconds_to_delay)%);print('done')\"");

        // when
        this.executeWithParameters(ExecuteBashScript.class, parameters);

        // then elapsed time will be more than execution time
        Instant afterExecution = Instant.now();
        Assert.assertTrue(afterExecution.minusSeconds(secondsDelay).isAfter(beforeExecution));
        Assert.assertTrue(afterExecution.minusSeconds(secondsDelay+1).isBefore(beforeExecution));
    }

}