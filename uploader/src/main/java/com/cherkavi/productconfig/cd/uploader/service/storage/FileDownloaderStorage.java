package com.cherkavi.productconfig.cd.uploader.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileDownloaderStorage {

    private final Path rootLocation;

    @Autowired
    public FileDownloaderStorage(StorageProperties properties){
        this.rootLocation = Paths.get(properties.getLocation());
    }

    /**
     * download file from remoute URL and save it on the disk
     * @param url remote url with XML
     * @return xml file path
     */
    public File downloadFile(String url, String outputFileName) throws IllegalArgumentException{
        File destinationFile = rootLocation.resolve(outputFileName).toFile();
        try {
            org.apache.commons.io.FileUtils.copyURLToFile(new URL(url), destinationFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("can't read data from remote source: "+url);
        }
        return destinationFile;
    }

}
