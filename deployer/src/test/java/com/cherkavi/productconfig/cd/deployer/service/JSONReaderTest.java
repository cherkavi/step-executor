package com.cherkavi.productconfig.cd.deployer.service;

import com.cherkavi.productconfig.cd.deployer.step.JarExtractor;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class JSONReaderTest {

    private static int AMOUNT_OF_ELEMENTS=10;

    @Test
    public void readFromFile(){
        // given
        String pathToFile = Thread.currentThread().getContextClassLoader().getResource("step-parameters-01.json").getPath();
        JSONReader reader = new JSONReader();

        // when
        JSONArray array = reader.readFile(pathToFile);

        // then
        Assert.assertNotNull(array);
        Assert.assertTrue(array.length()>0);
        Assert.assertEquals(AMOUNT_OF_ELEMENTS, array.length());
    }


    @Test
    public void readParameters(){
        // given
        String pathToFile = Thread.currentThread().getContextClassLoader().getResource("step-parameters-01.json").getPath();
        JSONReader reader = new JSONReader();
        JSONArray array = reader.readFile(pathToFile);
        String className = "com.cherkavi.productconfig.cd.deployer.step.FreePortFinder";

        // when
        List<StepWithParameters> steps = reader.parse(array);

        // then
        Assert.assertNotNull(steps);
        Assert.assertTrue(steps.size()>0);
        Assert.assertEquals(AMOUNT_OF_ELEMENTS, steps.size());
        StepWithParameters step = StepWithParameters.getByClassName(steps, className);
        Assert.assertNotNull(step);
        Assert.assertNotNull(step.getClassName());
        Assert.assertEquals(className, step.getClassName());
        Assert.assertNotNull(step.getParameters());
        Assert.assertEquals(2, step.getParameters().size());
        Assert.assertNotNull(step.getParameters().get("low"));
        Assert.assertEquals(9000, step.getParameters().get("low"));
        Assert.assertNotNull(step.getParameters().get("high"));
        Assert.assertEquals(9999, step.getParameters().get("high"));
    }

    @Test
    public void readParameterAsArray(){
        // given
        String pathToFile = Thread.currentThread().getContextClassLoader().getResource("step-parameters-01.json").getPath();
        JSONReader reader = new JSONReader();
        JSONArray array = reader.readFile(pathToFile);
        String className = "com.cherkavi.productconfig.cd.deployer.step.JarExtractor";

        // when
        List<StepWithParameters> steps = reader.parse(array);

        // then
        Assert.assertNotNull(steps);
        Assert.assertTrue(steps.size()>0);
        Assert.assertEquals(AMOUNT_OF_ELEMENTS, steps.size());
        StepWithParameters step = StepWithParameters.getByClassName(steps, className);
        Assert.assertNotNull(step);
        Assert.assertNotNull(step.getClassName());
        Assert.assertEquals(className, step.getClassName());
        Assert.assertNotNull(step.getParameters());
        Assert.assertNotNull(step.getParameters().get(JarExtractor.PARAM_JAR_PATHS));
        Assert.assertTrue(step.getParameters().get(JarExtractor.PARAM_JAR_PATHS) instanceof List);
        Assert.assertEquals( 3, ((List)step.getParameters().get(JarExtractor.PARAM_JAR_PATHS)).size());
    }

}