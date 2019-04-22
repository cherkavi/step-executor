package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class MoveFile extends StepBranchAware {

    public static String PARAM_OUTPUT_FILENAME = "executable_jar";
    public static String PARAM_UPLOADER_FOLDER_SOURCE = "uploader.folder";
    public static String PARAM_FOLDER_DESTINATION = "storage.location";
    public static String PARAM_FILE_EXTENSION = "artifact.extension";

    private final static Logger LOGGER = LoggerFactory.getLogger(MoveFile.class);

    @Override
    public void execute(String branchName) {
        String folderSource = getContextParameter(PARAM_UPLOADER_FOLDER_SOURCE);
        String folderDestination = getContextParameter(PARAM_FOLDER_DESTINATION);
        String extension = getContextParameter(PARAM_FILE_EXTENSION);

        File destinationFile = getFileWithFolder(folderDestination, branchName, (extension==null)?".jar":"."+extension);
        File sourceFile = getFile(folderSource, branchName);
        LOGGER.info(String.format("moving file from upload storage %s to deploy place %s", folderSource, destinationFile.toString()));
        try {
            if(!destinationFile.getParentFile().exists()){
                destinationFile.getParentFile().mkdirs();
            }
            FileUtils.deleteQuietly(destinationFile);
            FileUtils.moveFile(sourceFile, destinationFile);
        } catch (IOException e) {
            LOGGER.error(String.format("can't move file from source %s to destination %s : %s", folderSource, folderDestination, e.getMessage()));
            throw new IllegalStateException(String.format("can't move file from source %s to destination %s : %s", folderSource, folderDestination, e.getMessage()));
        }
        setContextParameter(PARAM_OUTPUT_FILENAME, destinationFile.getAbsolutePath());
    }

    @Override
    protected List<String> dependsOnParametersByName() {
        return Arrays.asList(PARAM_UPLOADER_FOLDER_SOURCE, PARAM_FOLDER_DESTINATION);
    }

    private String removePrefix(String folderDestination, File destinationFile) {
        return destinationFile.toString().substring(folderDestination.length());
    }

    private File getFile(String folder, String branchName) {
        File directory = new File(folder);
        return directory.toPath().resolve( branchName).toFile();
    }

    private File getFileWithFolder(String folder, String branchName, String suffix) {
        File directory = new File(folder);
        return directory.toPath().resolve( branchName).resolve(branchName+suffix).toFile();
    }

}
