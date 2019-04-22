package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import com.cherkavi.productconfig.cd.statusholder.domain.DeployStatus;
import com.cherkavi.productconfig.cd.statusholder.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


abstract class UpdateRecord extends StepBranchAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(UpdateRecord.class);

    @Override
    public void execute(String branchName) {
        String url=getContextParameter("status-holder.url");
        LOGGER.info("attempt to retrieve current status of records");
        ResponseEntity<DeployStatus[]> result = null;
        try{
            result = new RestTemplate().getForEntity(url + "/search/findByBranch?branch="+ branchName, DeployStatus[].class);
        }catch(RuntimeException ex){
            LOGGER.error("can't retrieve current status of records by branch "+branchName+": "+ex.getMessage());
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        for(DeployStatus eachResult: result.getBody()){
            eachResult.setStatus(getStatus());
            try{
                eachResult.setPort(Integer.parseInt(this.getContextParameter(FreePortFinder.OUTPUT_FREE_PORT).toString()));
            }catch(NumberFormatException | NullPointerException re){}
            try{
                restTemplate.put(url + "/" + eachResult.getId(), eachResult);
            }catch(RuntimeException ex){
                LOGGER.error("can't update status of record "+eachResult+": "+ex.getMessage());
            }

            LOGGER.info("updated record: "+eachResult);
        }
    }

    abstract Status getStatus();
}
