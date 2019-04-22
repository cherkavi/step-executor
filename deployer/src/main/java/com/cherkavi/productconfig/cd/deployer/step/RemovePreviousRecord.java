package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import com.cherkavi.productconfig.cd.statusholder.domain.DeployStatus;
import com.cherkavi.productconfig.cd.statusholder.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class RemovePreviousRecord extends StepBranchAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(RemovePreviousRecord.class);

    @Override
    public void execute(String branchName) {
        LOGGER.info("signal to remove previous record from status-holder");
        String url = getContextParameter("status-holder.url");

        ResponseEntity<DeployStatus[]> result = new RestTemplate().getForEntity(url + "/search/findByBranch?branch="+ branchName, DeployStatus[].class);
        for(DeployStatus eachStatus: result.getBody()){
            if(eachStatus.getStatus().equals(Status.NEW)){
                continue;
            }
            try {
                new RestTemplate().delete(new URI(url+"/"+eachStatus.getId()));
            } catch (URISyntaxException e) {
            }
        }
    }

}
