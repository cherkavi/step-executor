package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class JarExtractor extends StepBranchAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(JarExtractor.class);
    private final static String DELIMITER = "/";
    private final static List<String> ZIP_EXTENSION = Arrays.asList("zip", "jar", "war", "ear");

    public static final String PARAM_JAR_PATHS ="array_of_path";
    public static final String PARAM_JAR_EXTRACTED_FOLDER ="jar_files_extracted";


    @Override
    protected List<String> dependsOnParametersByName() {
        return super.dependsOnParametersByName();
    }

    @Override
    public void execute(String branchName) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(new File((String) this.getContextParameter(MoveFile.PARAM_OUTPUT_FILENAME)));
        } catch (IOException e) {
            throw new IllegalStateException("can't read war file: "+this.getContextParameter(MoveFile.PARAM_OUTPUT_FILENAME));
        }

        List<ZipFile> toBeDeleted=new ArrayList<>();
        toBeDeleted.add(zipFile);
        try{
            String outputFolder = determinateOutputFolder();
            this.setContextParameter(PARAM_JAR_EXTRACTED_FOLDER, outputFolder);
            for(String eachPath: ((List<String>)(this.getContextParameter(PARAM_JAR_PATHS)))){
                try {
                    unzipValue(zipFile, eachPath, outputFolder, toBeDeleted);
                } catch (IOException e) {
                    throw new IllegalStateException(MessageFormat.format("can't read data from jar/war/zip file by path:{1} from zip file:{0}", zipFile, eachPath), e);
                }
            }
        }finally{
            toBeDeleted.forEach(IOUtils::closeQuietly);
        }
    }

    private String determinateOutputFolder() {
        String folder = this.getContextParameter(PARAM_JAR_EXTRACTED_FOLDER);
        if(folder!=null){
            return folder;
        }
        try {
            return Files.createTempDirectory(JarExtractor.class.getSimpleName()).toAbsolutePath().toString();
        } catch (IOException e) {
            throw new IllegalStateException("can't create temp folder");
        }
    }

    private static void unzipValue(ZipFile rootFs, String source, String destinationPath, List<ZipFile> toBeDeleted) throws IOException {
        ZipFile currentFileSystem = rootFs;
        String currentPath = null;
        ZipEntry currentZipEntry = null;

        LOGGER.debug("unzip value: "+source);
        for(String eachPart:source.split(DELIMITER)){
            LOGGER.debug("unzip value.part: "+eachPart);
            if(eachPart.equals("")){
                continue;
            }
            if(isWildcard(eachPart)){
                currentPath = (currentPath==null)?"":currentPath+DELIMITER;
                List<ZipEntry> entries = getEntriesFromCurrentFolder(currentFileSystem, currentPath);
                for(ZipEntry entry:entries){
                    String candidate = zipFileName(entry, currentPath);
                    if(matchWildcardString(eachPart, candidate)){
                        currentZipEntry = entry;
                        currentPath = StringUtils.endsWith(currentPath, DELIMITER)?currentPath:currentPath+DELIMITER;
                        break;
                    }
                }
            }else{
                currentPath = (currentPath==null)?eachPart : currentPath + DELIMITER + eachPart;
                currentZipEntry = currentFileSystem.getEntry(currentPath);
                if(currentZipEntry==null){
                    throw new IllegalArgumentException(String.format("path not exists %s on step: %s",source, currentPath));
                }
            }
            if(currentZipEntry==null){
                LOGGER.error("current path was not found: "+currentPath+"  into source: "+source);
                throw new IllegalArgumentException("current path was not found: "+currentPath+"  into source: "+source);
            }
            if(isZipArchive(currentZipEntry)){
                currentFileSystem = getZipFile(currentFileSystem, currentZipEntry);
                toBeDeleted.add(currentFileSystem);
                currentPath = null;
            }
        }

        LOGGER.debug("unzip value. currentFile: "+currentZipEntry +"   of path: "+currentPath);
        if(currentZipEntry.isDirectory()){
            currentPath = currentPath+DELIMITER;
            unpackFolder(destinationPath, currentFileSystem, currentPath);
        }else{
            InputStream zipFileInputStream = currentFileSystem.getInputStream(currentZipEntry);
            if(zipFileInputStream!=null){
                FileUtils.copyInputStreamToFile(zipFileInputStream, new File(destinationPath));
            }else{
                // LINUX Suse issue
                currentZipEntry = currentFileSystem.getEntry(currentPath+DELIMITER);
                if(currentZipEntry!=null && currentZipEntry.isDirectory()){
                    unpackFolder(destinationPath, currentFileSystem, currentPath);
                }
            }
        }
    }

    private static void unpackFolder(String destinationPath, ZipFile currentFileSystem, String currentPath) throws IOException {
        String destinationFolderPath = destinationPath;
        if(!destinationFolderPath.endsWith(DELIMITER)){
            destinationFolderPath = destinationFolderPath + DELIMITER;
        }
        new File(destinationFolderPath).mkdirs();
        for(ZipEntry entry: getEntries(currentFileSystem, currentPath)){
            String destinationFileName = zipFileName(entry, currentPath);
            if(StringUtils.isEmpty(destinationFileName)){
                continue;
            }
            FileUtils.copyInputStreamToFile(currentFileSystem.getInputStream(entry),
                    new File(destinationFolderPath+destinationFileName));
        }
    }

    private static String zipFileName(ZipEntry entry, String currentPath) {
        String innerFilePath = entry.getName();
        if(!innerFilePath.contains(DELIMITER)){
            return innerFilePath;
        }
        innerFilePath = innerFilePath.substring(currentPath.length());
        if(!innerFilePath.contains(DELIMITER)){
            return innerFilePath;
        }
        return StringUtils.substringAfter(innerFilePath, DELIMITER);
    }

    private static List<ZipEntry> getEntriesFromCurrentFolder(ZipFile currentFileSystem, String currentPath) {
        Enumeration<? extends ZipEntry> listOfEntries = currentFileSystem.entries();
        List<ZipEntry> returnValue = filesFromZip(currentPath, listOfEntries);
        return returnValue.stream().filter(entry -> !entry.getName().substring(currentPath.length()).contains(DELIMITER)).collect(Collectors.toList());
    }

    private static List<ZipEntry> filesFromZip(String currentPath, Enumeration<? extends ZipEntry> listOfEntries) {
        List<ZipEntry> returnValue = new ArrayList<>();
        while(listOfEntries.hasMoreElements()){
            ZipEntry entry = listOfEntries.nextElement();
            if(entry.getName().equals(currentPath)){
                continue;
            }
            if(entry.getName().startsWith(currentPath)){
                returnValue.add(entry);
            }
        }
        return returnValue;
    }


    private static List<ZipEntry> getEntries(ZipFile currentFileSystem, String currentPath) {
        Enumeration<? extends ZipEntry> allFiles = currentFileSystem.entries();
        return filesFromZip(currentPath, allFiles);
    }


    private static boolean isWildcard(String eachPart) {
        return eachPart.contains("*")||eachPart.contains("?");
    }

    private static boolean isZipArchive(ZipEntry currentFile) {
        if(currentFile.isDirectory()){
            return false;
        }
        return ZIP_EXTENSION.contains(StringUtils.substringAfterLast(currentFile.getName(),".").toLowerCase());
    }

    private final static ZipFile getZipFile(ZipFile zipFile, ZipEntry entry) throws IOException {
        File tempFile = Files.createTempFile(JarExtractor.class.getName(), "").toFile();
        tempFile.deleteOnExit();
        FileUtils.copyToFile(zipFile.getInputStream(entry), tempFile);
        return new ZipFile(tempFile);
    }

    private static boolean matchWildcardString(String pattern, String input) {
        if (pattern.equals(input)) {
            return true;
        }
        else if(StringUtils.trimToNull(input)==null) {
            if (pattern.startsWith("*")){
                return true;
            }else{
                return false;
            }
        } else if(pattern.length() == 0) {
            return false;
        } else if (pattern.charAt(0) == '?') {
            return matchWildcardString(pattern.charAt(1)+"", input.charAt(1)+"");
        } else if (pattern.charAt(pattern.length() - 1) == '?') {
            return matchWildcardString(
                    pattern.substring(0, pattern.length() - 1),
                    input.substring(0, input.length() - 1));
        } else if (pattern.charAt(0) == '*') {
            if (matchWildcardString(pattern.substring(1), input)) {
                return true;
            } else {
                return matchWildcardString(pattern, input.substring(1));
            }
        } else if (pattern.charAt(pattern.length() - 1) == '*') {
            if (matchWildcardString(pattern.substring(0, pattern.length() - 1), input)) {
                return true;
            } else {
                return matchWildcardString(pattern, input.substring(0, input.length() - 1));
            }
        } else if (pattern.charAt(0) == input.charAt(0)) {
            return matchWildcardString(pattern.substring(1), input.substring(1));
        }
        return false;
    }

}

