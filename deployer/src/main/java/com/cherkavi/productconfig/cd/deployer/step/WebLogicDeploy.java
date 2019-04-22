package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class WebLogicDeploy extends StepBranchAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebLogicDeploy.class);
    final static String PARAM_APPLICATION_NAME="app_name";
    final static String PARAM_WEBLOGIC_HOST_AND_PORT="wl_host"; //vldn265:7001
    final static String PARAM_APPLICATION_PATH="war_path_on_remote_host"; //

    final static String PARAM_WEBLOGIC_LOGIN="wl_login";
    final static String PARAM_WEBLOGIC_PASSW="wl_passw";

    @Override
    public void execute(String branchName) {
        deployApplicationToWebLogic(
                this.getContextParameter(PARAM_WEBLOGIC_HOST_AND_PORT),
                this.getContextParameter(PARAM_APPLICATION_NAME),
                this.getContextParameter(PARAM_APPLICATION_PATH),
                this.getContextParameter(PARAM_WEBLOGIC_LOGIN),
                this.getContextParameter(PARAM_WEBLOGIC_PASSW)
        );
    }

    private void deployApplicationToWebLogic(String host, String appName, String warPath, String login, String password) {
        // curl -v --user weblogic:weblogic1 -H X-Requested-By:weblogic -H Accept:application/json -X GET http://vldn265:7001/management/wls/latest/deployments
        ResponseEntity<String> response = WebLogicUtils.exchange(host, login, password, HttpMethod.POST, "/application", getBody(appName, warPath));
        JSONObject jsonResponse = new JSONObject(response.getBody());
        if(response.getStatusCode().is4xxClientError()){
            LOGGER.warn(" can't deploy, reason is: "+jsonResponse.get("messages"));
            throw new IllegalStateException(" can't deploy, reason is: "+jsonResponse.get("messages"));
        }
        if(response.getStatusCode().is2xxSuccessful()){
            LOGGER.info(" server response ( SUCCESSFUL ): "+jsonResponse.get("messages"));
            return;
        }
        LOGGER.info("Application " + appName + " was deployed with response");
        LOGGER.debug(jsonResponse.toString());
    }

    private String getBody(String appName, String warPath) {
        return "{\"name\": \""+appName+"\", \"deploymentPath\": \""+warPath+"\", \"targets\": [\"pportal_group\"]}";
    }
}
