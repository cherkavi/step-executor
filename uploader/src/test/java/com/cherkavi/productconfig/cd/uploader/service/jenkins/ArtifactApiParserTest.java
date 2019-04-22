package com.cherkavi.productconfig.cd.uploader.service.jenkins;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


public class ArtifactApiParserTest {

    private final static String JENKINS_XML = "develop.xml";

    @Test
    public void readDataFromFile() throws ParserConfigurationException, SAXException, IOException {
        // given

        String path = Thread.currentThread().getContextClassLoader().getResource(JENKINS_XML).getFile();

        // when
        String result = ArtifactApiParser.findWarFileRelativePath(path);

        // then
        Assert.assertEquals("com/cherkavi/productconfig/opm-gui/2018.06.00.03-SNAPSHOT/opm-gui-2018.06.00.03-SNAPSHOT.war", result);
    }

}