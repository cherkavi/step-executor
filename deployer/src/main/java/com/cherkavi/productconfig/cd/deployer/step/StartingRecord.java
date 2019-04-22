package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.statusholder.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StartingRecord extends UpdateRecord{
    private final static Logger LOGGER = LoggerFactory.getLogger(StartingRecord.class);
    @Override
    Status getStatus() {
        LOGGER.info("new status: "+Status.STARTING);
        return Status.STARTING;
    }
}
