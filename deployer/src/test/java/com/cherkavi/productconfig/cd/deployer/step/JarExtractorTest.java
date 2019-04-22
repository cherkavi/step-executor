package com.cherkavi.productconfig.cd.deployer.step;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JarExtractorTest extends StepTest{
    @Test
    public void readTextFile() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempFile(this.getClass().getName(), "").toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/1.txt", destinationFile.getPath());

        // then
        Assert.assertEquals("text file 1.txt", FileUtils.readFileToString(destinationFile, "utf-8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readTextFileNotExists() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempFile(this.getClass().getName(), "").toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/10000000.txt", destinationFile.getPath());

        // then
        // IllegalArgumentException.class
    }

    private void executeUnzip(String source, String pathInsideZip, String destination) {
        Map<String, Object> parameters=new HashMap<>();
        parameters.put(MoveFile.PARAM_OUTPUT_FILENAME, source);
        parameters.put(JarExtractor.PARAM_JAR_PATHS, Arrays.asList(pathInsideZip));
        parameters.put(JarExtractor.PARAM_JAR_EXTRACTED_FOLDER, destination);
        this.executeWithParameters(JarExtractor.class, parameters);
    }

    @Test
    public void readTextFileFromSubArchive() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempFile(this.getClass().getName(), "").toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/3/32.zip/3/31.txt", destinationFile.getPath());

        // then
        Assert.assertEquals("text file 31.txt", FileUtils.readFileToString(destinationFile, "utf-8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readTextFileFromSubArchiveNotFound() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempFile(this.getClass().getName(), "").toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/3/32.zip/3/31000000.txt", destinationFile.getPath());

        // then
        // IllegalArgumentException.class
    }

    @Test
    public void readWildcardTextFile() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempFile(this.getClass().getName(), "").toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/4*.txt", destinationFile.getPath());

        // then
        Assert.assertEquals("text file 45.txt", FileUtils.readFileToString(destinationFile, "utf-8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readWildcardTextFileNotFound() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempFile(this.getClass().getName(), "").toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/6*.txt", destinationFile.getPath());

        // then
        // IllegalArgumentException
    }

    @Test
    public void readWildcardTextFileFromSubArchive() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempFile(this.getClass().getName(), "").toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/3/32.zip/2/4*.txt", destinationFile.getPath());

        // then
        Assert.assertEquals("text file 45.txt", FileUtils.readFileToString(destinationFile, "utf-8"));
    }

    @Test
    public void readFileFromSubfolder() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempFile(this.getClass().getName(), "").toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/3/31.txt", destinationFile.getPath());

        // then
        Assert.assertEquals("text file 31.txt", FileUtils.readFileToString(destinationFile, "utf-8"));
    }

    @Test
    public void readFolder() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempDirectory(this.getClass().getName()).toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/3", destinationFile.getPath());

        //
        List<String> fileList = Stream.of(destinationFile.listFiles()).map(file-> file.getName()).collect(Collectors.toList());
        Assert.assertTrue(fileList.contains("31.txt"));
        Assert.assertTrue(fileList.contains("32.zip"));
    }

    @Test
    public void readFolderFromArchive() throws IOException {
        // given
        File sourceFile = testFile();
        File destinationFile = Files.createTempDirectory(this.getClass().getName()).toFile();
        destinationFile.deleteOnExit();

        // when
        executeUnzip(sourceFile.getPath(), "/3/32.zip/3", destinationFile.getPath());

        //
        List<String> fileList = Stream.of(destinationFile.listFiles()).map(file-> file.getName()).collect(Collectors.toList());
        Assert.assertTrue(fileList.contains("31.txt"));
    }


    private File testFile() {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("example.jar"); // from classpath
        return new File(resource.getPath());
    }

    // @Test
    public void externalTest() throws IOException {
        // given
        File sourceFile = new File("C:\\temp\\fixnet-db\\fixnet-gui-2018.02.00.00-SNAPSHOT.war");
        File destinationFile = new File("C:\\temp\\fixnet-db\\out\\");

        // when
        executeUnzip(sourceFile.getPath(), "/WEB-INF/lib/fixnet-persistence-*.jar/db/migration/2018.01.04.00/ddl", destinationFile.getPath());
        executeUnzip(sourceFile.getPath(),"/WEB-INF/lib/fixnet-persistence-*.jar/db/migration/2018.01.04.00/dml" , destinationFile.getPath());
        executeUnzip(sourceFile.getPath(),"/WEB-INF/lib/fixnet-persistence-*.jar/db/migration/fixnet/2018.02.00.00/ddl" , destinationFile.getPath());
        executeUnzip(sourceFile.getPath(),"/WEB-INF/lib/fixnet-persistence-*.jar/db/migration/fixnet/2018.02.00.00/dml" , destinationFile.getPath());
        executeUnzip(sourceFile.getPath(),"/WEB-INF/lib/fixnet-persistence-*.jar/db/migration/fixnet/2018.02.00.00/post_ddl" , destinationFile.getPath());
        executeUnzip(sourceFile.getPath(),"/WEB-INF/lib/fixnet-persistence-*.jar/db/migration/fixnet/2018.02.00.00/dev" , destinationFile.getPath());


        // executeUnzip(sourceFile.getPath(),"/WEB-INF/lib/fixnet-persistence-*.jar/db/migration/fixnet/2018.02.00.00/dev000000" , destinationFile.getPath());
    }

}