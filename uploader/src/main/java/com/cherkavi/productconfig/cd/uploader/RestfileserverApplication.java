package com.cherkavi.productconfig.cd.uploader;

import com.cherkavi.productconfig.cd.uploader.service.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class RestfileserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestfileserverApplication.class, args);
	}

}
