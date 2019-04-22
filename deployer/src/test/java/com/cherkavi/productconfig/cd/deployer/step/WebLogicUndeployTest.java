package com.cherkavi.productconfig.cd.deployer.step;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WebLogicUndeployTest extends StepTest {

    @Test
    public void tryUndeploy(){
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(WebLogicUndeploy.PARAM_APPLICATION_NAME, "fixnet-gui-2018.02.00.00-SPRINT-7");
        parameters.put(WebLogicUndeploy.PARAM_WEBLOGIC_HOST_AND_PORT, "vldn265:7001");
        parameters.put(WebLogicUndeploy.PARAM_WEBLOGIC_LOGIN, "weblogic");
        parameters.put(WebLogicUndeploy.PARAM_WEBLOGIC_PASSW, "weblogic1");

        // when
        executeWithParameters(WebLogicUndeploy.class, parameters);

        // then
        // check logic
    }

    /*
    {
        "item": {
        "description": "[Deployer:149026]remove application fixnet-gui-2018.02.00.00-SPRINT-7 on pportal_group.",
                "deploymentName": "fixnet-gui-2018.02.00.00-SPRINT-7",
                "targets": [{
            "status": "completed",
                    "errors": [],
            "name": "pportal_group",
                    "type": "cluster"
        }],
        "beginTime": 1530780063697,
                "endTime": 1530780064283,
                "status": "completed",
                "operation": "remove",
                "name": "ADTR-16",
                "id": "16",
                "type": "deployment"
    },
        "messages": [{
        "message": "Undeployed the application 'fixnet-gui-2018.02.00.00-SPRINT-7'.",
                "severity": "SUCCESS"
    }],
        "links": [{
        "rel": "job",
                "uri": "http:\/\/vldn265:7001\/management\/wls\/latest\/jobs\/deployment\/id\/16"
    }]
    }
    */

}