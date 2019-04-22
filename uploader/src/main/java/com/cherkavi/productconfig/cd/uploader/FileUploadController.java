package com.cherkavi.productconfig.cd.uploader;

import com.cherkavi.productconfig.cd.uploader.event.ReceiveFileEvent;
import com.cherkavi.productconfig.cd.uploader.service.storage.MutlipartStorage;
import com.cherkavi.productconfig.cd.uploader.service.storage.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/uploader")
public class FileUploadController {

    private final MutlipartStorage mutlipartStorage;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    public FileUploadController(MutlipartStorage mutlipartStorage) {
        this.mutlipartStorage = mutlipartStorage;
    }

    // curl -X GET  http://127.0.0.1:8080/image.png
    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = mutlipartStorage.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    // curl -X POST -H "Content-Type: multipart/form-data" -H "branchname:feature-OPM-nojira" -F "file=@uploader-local.png" http://127.0.0.1:8080/uploader
    @PostMapping(value = "/", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(
            @RequestHeader("branchname") String branchName,
            @RequestParam("file") MultipartFile file) {
        eventPublisher.publishEvent(new ReceiveFileEvent(file, branchName));
        return ResponseEntity.ok().body("OK");
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
