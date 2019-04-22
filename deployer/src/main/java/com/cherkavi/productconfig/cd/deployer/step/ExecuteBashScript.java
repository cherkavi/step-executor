package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class ExecuteBashScript extends StepBranchAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExecuteBashScript.class);

    static final String PARAM_BASH_SCRIPT="command_line";

    @Override
    public void execute(String branchName) {
        String commandLine = getCommandLine();
        LOGGER.info("Attempt to execute command line script: "+commandLine);
        ProcessResult result;
        try {
            result = new ProcessExecutor().commandSplit(commandLine).start().getFuture().get();
        } catch (IOException e) {
            LOGGER.error("Can't execute command IOException: ", e);
            throw new IllegalStateException("can't access to file");
        } catch (InterruptedException e) {
            LOGGER.error("Can't wait for finish of the process: ", e);
            throw new IllegalStateException("Can't wait for finish of the process");
        } catch (ExecutionException e) {
            LOGGER.error("Can't execute command line: ", e);
            throw new IllegalStateException("Can't execute command line");
        }
        if(result.getExitValue()!=0){
            throw new IllegalStateException("result of command line execution is not 0: "+result.getExitValue());
        }
    }

    protected String getCommandLine() {
        return getContextParameter(PARAM_BASH_SCRIPT);
    }

}
