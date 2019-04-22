package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;


abstract class PortFinder extends StepBranchAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(PortFinder.class);

    int findFirstOpenPort(int lowRange, int highRange) throws IllegalStateException {
        for (int index=lowRange; index<highRange; index++) {
            try {
                ServerSocket socket = new ServerSocket(index);
                socket.close();
                LOGGER.info("next free port : "+index);
                return index;
            } catch (IOException ex) {
                continue; // try next port
            }
        }
        throw new IllegalStateException(String.format("no free port found into the range %d .. %d", lowRange, highRange));
    }
}
