package com.cherkavi.productconfig.cd.uploader.service.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("storage")
public class StorageProperties {

    @NotBlank
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
