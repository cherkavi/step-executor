package com.cherkavi.productconfig.cd.uploader.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.multipart.MultipartFile;

public class ReceiveFileEvent extends ApplicationEvent{
    private final String branchName;

    public ReceiveFileEvent(MultipartFile file, String branchName) {
        super(file);
        this.branchName = branchName;
    }

    @Override
    public MultipartFile getSource() {
        return (MultipartFile)super.getSource();
    }

    public String getBranchName() {
        return branchName;
    }

}
