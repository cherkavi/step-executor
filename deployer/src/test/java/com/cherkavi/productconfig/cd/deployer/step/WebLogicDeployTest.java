package com.cherkavi.productconfig.cd.deployer.step;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WebLogicDeployTest extends StepTest {

    @Test
    public void tryDeploy(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(WebLogicDeploy.PARAM_APPLICATION_NAME, "fixnet-gui-2018.02.00.00-SPRINT-7");
        parameters.put(WebLogicDeploy.PARAM_WEBLOGIC_HOST_AND_PORT, "vldn265:7001");
        parameters.put(WebLogicDeploy.PARAM_WEBLOGIC_LOGIN, "weblogic");
        parameters.put(WebLogicDeploy.PARAM_WEBLOGIC_PASSW, "weblogic1");
        parameters.put(WebLogicDeploy.PARAM_APPLICATION_PATH, "/opt/oracle/domains/pportal_dev/pportal-dev1/servers/admin_server/upload/fixnet-gui-2018.02.00.00-SPRINT-7/app/fixnet-gui-2018.02.00.00-SPRINT-7.war");

        // when
        executeWithParameters(WebLogicDeploy.class, parameters);

        // then
        // check logic
    }

}

/*
{
    "item": {
        "description": "[Deployer:149026]deploy application fixnet-gui-2018.02.00.00-SPRINT-7 on pportal_group.",
        "deploymentName": "fixnet-gui-2018.02.00.00-SPRINT-7",
        "targets": [{
            "status": "completed",
            "errors": [],
            "name": "pportal_group",
            "type": "cluster"
        }],
        "beginTime": 1530707579898,
        "endTime": 1530707671655,
        "status": "completed",
        "operation": "deploy",
        "name": "ADTR-15",
        "id": "15",
        "type": "deployment"
    },
    "messages": [{
        "message": "Deployed the application 'fixnet-gui-2018.02.00.00-SPRINT-7'.",
        "severity": "SUCCESS"
    }],
    "links": [{
        "rel": "job",
        "uri": "http:\/\/vldn265:7001\/management\/wls\/latest\/jobs\/deployment\/id\/15"
    }]
}svc_pportal_dev@z3-dev1-app-pportal-01:~/jenkins/fixnet-weblogic>

 */