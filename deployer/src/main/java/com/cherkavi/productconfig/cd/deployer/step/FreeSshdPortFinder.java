package com.cherkavi.productconfig.cd.deployer.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

@Component
public class FreeSshdPortFinder extends PortFinder {
    public static String OUTPUT_FREE_PORT_SSHD="free_sshd_port";
    private final static Logger LOGGER = LoggerFactory.getLogger(FreeSshdPortFinder.class);

    @Override
    public void execute(String branchName) {
        LOGGER.info("find next free port for SSHD connection");
        setContextParameter(OUTPUT_FREE_PORT_SSHD, findFirstOpenPort(getContextParameter("low"), getContextParameter("high")));
    }

}
