package com.cherkavi.productconfig.cd.uploader.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class DownloadedFileEvent extends ApplicationEvent{
    private final String branchName;

    public DownloadedFileEvent(File downloadedFile, String branchName) {
        super(downloadedFile);
        this.branchName = branchName;
    }

    @Override
    public File getSource() {
        return (File)super.getSource();
    }

    public String getBranchName() {
        return branchName;
    }

}
