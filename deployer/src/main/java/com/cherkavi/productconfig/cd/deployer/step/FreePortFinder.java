package com.cherkavi.productconfig.cd.deployer.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FreePortFinder extends PortFinder{
    public static String OUTPUT_FREE_PORT="free_http_port";
    private final static Logger LOGGER = LoggerFactory.getLogger(FreePortFinder.class);

    @Override
    public void execute(String branchName) {
        LOGGER.info("find next free port");
        setContextParameter(OUTPUT_FREE_PORT, findFirstOpenPort(getContextParameter("low"), getContextParameter("high")));
    }
}
