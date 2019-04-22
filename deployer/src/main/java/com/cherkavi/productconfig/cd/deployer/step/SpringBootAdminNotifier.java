package com.cherkavi.productconfig.cd.deployer.step;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.io.Serializable;
import java.util.*;

@Component
public class SpringBootAdminNotifier extends StepBranchAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(SpringBootAdminNotifier.class);

    @Override
    public void execute(String branchName) {
        String adminUrl=getContextParameter("springbootadmin.url");
        String currentServerUrl=getContextParameter("springbootadmin.currentServer");

        Registration registration = buildRegistration(currentServerUrl, branchName);
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.info("notify SpringAdmin console about new deployment");
        try{
            ResponseEntity response = new RestTemplateBuilder().build()
                    .exchange(getAdminUrl(adminUrl), HttpMethod.POST, buildRequest(registration, headers), String.class);
            if(!response.getStatusCode().is2xxSuccessful()){
                LOGGER.error("can't notify admin console about new element");
            }else{
                LOGGER.info("external admin panel was notified about new element");
            }
        } catch(RestClientException ex){
            LOGGER.error("can't execute request operation ",ex);
        } catch(RuntimeException ex){
            LOGGER.error("can't execute operation due unknown exception ", ex);
        }catch(JsonProcessingException ex){
            LOGGER.error("can't convert value to JSON ", ex);
        }
    }

    private HttpEntity<String> buildRequest(Registration registration, HttpHeaders headers) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return new HttpEntity<>(mapper.writeValueAsString(registration), headers);
    }

    private String getAdminUrl(String adminUrlRoot) {
        return adminUrlRoot +"/instances";
    }

    private Registration buildRegistration(String currentServerUrl, String branchName) {
        Registration returnValue = new Registration();
        returnValue.name="jenkins deploy - "+branchName;
        Map<String, String> metadata = new HashMap<>();
        metadata.put("branch", branchName);
        metadata.put("deploy time", new Date().toString());
        returnValue.metadata = metadata;
        returnValue.serviceUrl= currentServerUrl +":"+this.getContextParameter(FreePortFinder.OUTPUT_FREE_PORT).toString();
        returnValue.managementUrl=returnValue.serviceUrl;
        returnValue.healthUrl=returnValue.managementUrl+"/health";
        return returnValue;
    }

    protected List<String> dependsOnParametersByName(){
        return Arrays.asList(
                FreePortFinder.OUTPUT_FREE_PORT
        );
    }

}


class Registration implements Serializable {
    String name;
    String managementUrl;
    String healthUrl;
    String serviceUrl;
    Map<String, String> metadata;
}