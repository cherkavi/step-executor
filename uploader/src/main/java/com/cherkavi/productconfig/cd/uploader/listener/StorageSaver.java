package com.cherkavi.productconfig.cd.uploader.listener;

import com.cherkavi.productconfig.cd.statusholder.domain.DeployStatus;
import com.cherkavi.productconfig.cd.statusholder.domain.Status;
import com.cherkavi.productconfig.cd.uploader.event.DownloadedFileEvent;
import com.cherkavi.productconfig.cd.uploader.event.ReceiveFileEvent;
import com.cherkavi.productconfig.cd.uploader.service.storage.MutlipartStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class StorageSaver {
    public static final Logger LOGGER = LoggerFactory.getLogger(StorageSaver.class);

    @Autowired
    MutlipartStorage mutlipartStorage;

    @Value("${status-holder.url}")
    String urlStatusHolder;

    @Value("${deployer.url}")
    String urlDeployer;

    @EventListener
    public void saveFile(ReceiveFileEvent event){
        String branchName = event.getBranchName();
        mutlipartStorage.store(event.getSource(), branchName);
        LOGGER.info("new file saved: "+branchName);
        updateStatus(branchName);
        notifyDeployer(branchName);
    }

    @EventListener
    public void processFile(DownloadedFileEvent event){
        String branchName = event.getBranchName();
        updateStatus(branchName);
        notifyDeployer(branchName);
    }

    private void notifyDeployer(String branchName) {
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("branch", branchName);
        ResponseEntity<String> result;
        try{
            result = new RestTemplate().postForEntity(
                urlDeployer + "/proceed",
                new HttpEntity<>(map, new HttpHeaders()),
                String.class);
        }catch(RestClientException re){
            LOGGER.warn("deployer return error: "+re.getMessage());
            throw new IllegalStateException("deployer sent error:" + re.getMessage());
        }
        if(!result.getStatusCode().is2xxSuccessful()){
            LOGGER.warn("deployer return non-poisitive response: "+result.getBody());
            throw new IllegalStateException("deployer sent non positive response:" + result.getBody());
        }
        LOGGER.info("deployer notified: "+result.getBody());
    }

    private void updateStatus(String branchName) {
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        DeployStatus newStatus = new RestTemplate().postForObject(urlStatusHolder,
                new HttpEntity<>(new DeployStatus(Status.NEW, branchName), headers),
                DeployStatus.class);
        LOGGER.info("DB updated: "+newStatus);
    }
}
