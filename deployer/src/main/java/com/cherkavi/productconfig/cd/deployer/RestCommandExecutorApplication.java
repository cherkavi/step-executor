package com.cherkavi.productconfig.cd.deployer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestCommandExecutorApplication {
	/** input parameters should be specified ( example for VM param):
	 * - Dsteps={filename with json objects}
	 * - DfailSteps={filename with json objects to signal about failing}
	*/
	public static void main(String[] args) {
		SpringApplication.run(RestCommandExecutorApplication.class, args);
	}
}
