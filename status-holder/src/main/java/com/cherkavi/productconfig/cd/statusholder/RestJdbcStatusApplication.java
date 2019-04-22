package com.cherkavi.productconfig.cd.statusholder;

import com.cherkavi.productconfig.cd.statusholder.domain.DeployStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@SpringBootApplication
public class RestJdbcStatusApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestJdbcStatusApplication.class, args);
	}


	@Bean
	RepositoryRestConfigurerAdapter deployStatusConfiguration(){
		return new RepositoryRestConfigurerAdapter(){
			public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
				config.exposeIdsFor(DeployStatus.class);
			}
		};
	}

}
