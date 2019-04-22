package com.cherkavi.productconfig.cd.deployer.step;

import com.cherkavi.productconfig.cd.deployer.service.StepBranchAware;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class WebLogicUndeploy extends StepBranchAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebLogicUndeploy.class);
    final static String PARAM_APPLICATION_NAME="app_name";
    final static String PARAM_WEBLOGIC_HOST_AND_PORT="wl_host"; //vldn265:7001
    final static String PARAM_WEBLOGIC_LOGIN="wl_login";
    final static String PARAM_WEBLOGIC_PASSW="wl_passw";

    @Override
    public void execute(String branchName) {
        removeApplicationFromWebLogic(
                this.getContextParameter(PARAM_WEBLOGIC_HOST_AND_PORT),
                this.getContextParameter(PARAM_APPLICATION_NAME),
                this.getContextParameter(PARAM_WEBLOGIC_LOGIN),
                this.getContextParameter(PARAM_WEBLOGIC_PASSW)
        );
    }

    private void removeApplicationFromWebLogic(String host, String appName, String login, String password) {
        ResponseEntity<String> response = deleteRequest(host, appName, login, password);
        JSONObject jsonResponse = new JSONObject(response.getBody());
        if(response.getStatusCode().is4xxClientError()){
            LOGGER.warn(" server response ( NOT FOUND ): "+jsonResponse.get("messages"));
            return;
        }
        if(response.getStatusCode().is2xxSuccessful()){
            LOGGER.info(" server response ( SUCCESSFUL ): "+jsonResponse.get("messages"));
            return;
        }
        LOGGER.info("Application " + appName + " was undeployed with response");
        LOGGER.debug(jsonResponse.toString());
    }

    private ResponseEntity<String> deleteRequest(String host, String appName, String login, String password) {
        return WebLogicUtils.exchange(host, login, password, HttpMethod.DELETE, String.format("/application/id/%s", appName), null);
    }

}
