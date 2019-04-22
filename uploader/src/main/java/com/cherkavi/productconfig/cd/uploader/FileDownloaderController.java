package com.cherkavi.productconfig.cd.uploader;

import com.cherkavi.productconfig.cd.uploader.event.DownloadedFileEvent;
import com.cherkavi.productconfig.cd.uploader.service.jenkins.ArtifactApiParser;
import com.cherkavi.productconfig.cd.uploader.service.storage.FileDownloaderStorage;
import com.cherkavi.productconfig.cd.uploader.service.storage.MutlipartStorage;
import com.cherkavi.productconfig.cd.uploader.service.storage.exception.StorageFileNotFoundException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/downloader")
public class FileDownloaderController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FileDownloaderController.class);

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    FileDownloaderStorage fileDownloaderStorage;

    // curl -X POST -H "Content-Type: multipart/form-data" -H "branchname:feature-OPM-nojira" -F "file=@uploader-local.png" http://127.0.0.1:8080/downloader
    @PostMapping(value = "/")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(
            @RequestHeader("branchname") String branchName,
            @RequestHeader("buildurl") String buildUrl) {
        Executors.newSingleThreadExecutor().submit(()-> processRequest(branchName, buildUrl));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Accepted");
    }

    public void processRequest(String branchName, String buildUrl) {
        eventPublisher.publishEvent(new DownloadedFileEvent(downloadFileByApi(branchName, buildUrl), branchName));
    }

    private File downloadFileByApi(String branchName, String url)  {
        String buildUrl = correctUrlSuffix(url);
        String xmlApi = buildUrl + "api/xml";
        File xmlFile;
        try{
            xmlFile = fileDownloaderStorage.downloadFile(xmlApi, branchName+Integer.toString(new Random().nextInt(9999))+".xml");
        }catch(IllegalArgumentException ex){
            throw new IllegalStateException("can't download Jenkins api file: "+ xmlApi);
        }

        String warFileRelativePath;
        try {
            warFileRelativePath = ArtifactApiParser.findWarFileRelativePath(xmlFile.getAbsolutePath());
        } catch (IOException | SAXException | ParserConfigurationException | IllegalArgumentException e) {
            throw new IllegalStateException("can't parse response from Jenkins API ");
        }finally{
            FileUtils.deleteQuietly(xmlFile);
        }
        String warDownloadUrl = buildUrl + "artifact" + correctUrlPrefix(warFileRelativePath);
        LOGGER.info("start download file: "+warDownloadUrl);
        try{
            File downloadFile = fileDownloaderStorage.downloadFile(warDownloadUrl, branchName);
            LOGGER.info("file downloaded: "+downloadFile+" from "+warDownloadUrl);
            return downloadFile;
        }catch(IllegalArgumentException arg){
            throw new IllegalStateException("can't download war artifact: "+ warDownloadUrl);
        }

    }

    private String correctUrlSuffix(String url){
        if(!url.endsWith("/")){
            url = url+"/";
        }
        return url;
    }

    private String correctUrlPrefix(String url){
        if(!url.startsWith("/")){
            url = "/"+url;
        }
        return url;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleStorageFileNotFound(IllegalStateException exc) {
        return ResponseEntity.badRequest().body(exc.getMessage());
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<String> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exc.getMessage());
    }

}
