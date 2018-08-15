/*
* Copyright 2018- Cisco
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package ves;

import evel_javalibrary.att.com.*;
import evel_javalibrary.att.com.AgentMain.EVEL_ERR_CODES;
import evel_javalibrary.att.com.EvelFault.EVEL_SEVERITIES;
import evel_javalibrary.att.com.EvelFault.EVEL_SOURCE_TYPES;
import evel_javalibrary.att.com.EvelFault.EVEL_VF_STATUSES;
import evel_javalibrary.att.com.EvelHeader.PRIORITIES;
import evel_javalibrary.att.com.EvelMobileFlow.MOBILE_GTP_PER_FLOW_METRICS;
import evel_javalibrary.att.com.EvelScalingMeasurement.MEASUREMENT_CPU_USE;
import evel_javalibrary.att.com.EvelScalingMeasurement.MEASUREMENT_VNIC_PERFORMANCE;
import evel_javalibrary.att.com.EvelStateChange.EVEL_ENTITY_STATE;
import evel_javalibrary.att.com.EvelThresholdCross.EVEL_ALERT_TYPE;
import evel_javalibrary.att.com.EvelThresholdCross.EVEL_EVENT_ACTION;
import java.net.HttpURLConnection;

import org.apache.log4j.Level;
import config.Config;

import mapper.VesVolthaMapper;
import mapper.VesVolthaMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonSyntaxException;

public class VesAgent {

    private static final Logger logger = LoggerFactory.getLogger("VesAgent");

    private static VesVolthaMapper mapper;

    public static void initVes() {
        logger.info("Initializing VES Agent");
        try {
            mapper = new VesVolthaMapper();
            AgentMain.evel_initialize("http://"+Config.getVesAddress(),
                Integer.parseInt(Config.getVesPort()),
                //"/vendor_event_listener","/example_vnf",
                null,null,
                "will",
                "pill",
                null, null, null,
                //"/home/gokul/newwk/demo/vnfs/VES5.0/evel/sslcerts2/my-keystore.jks", "changeit", "changeit",
                Level.TRACE);
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public static boolean sendToVES(String json) throws JsonSyntaxException {
        VesVolthaMessage message = mapper.parseJson(json);
        String id = message.getId();
        String ldeviceId = message.getLogicalDeviceId();
        String ts = message.getRaisedTS();
        String description = message.getDescription();
        //Type in Voltha needs to be category in VES
        String category = message.getType();
        //Category in VOLTHA needs to be type in VES
        String type = message.getCategory();
        String severity = message.getSeverity();
        String state = message.getState();
        String resourceId = message.getResourceId();

        EVEL_SEVERITIES vesSeverity = mapSeverity(severity);
        EVEL_SOURCE_TYPES vesType = mapType(type);
        EvelFault flt  = new EvelFault(
            "Fault_VOLTHA_" + id,
            ldeviceId + ":" + ts,
            id,
            description,
            EvelHeader.PRIORITIES.EVEL_PRIORITY_HIGH,
            vesSeverity,
            vesType,
            EVEL_VF_STATUSES.EVEL_VF_STATUS_ACTIVE);
        flt.evel_fault_addl_info_add("voltha", json);
        flt.evel_fault_addl_info_add("state", state);
        flt.evel_fault_addl_info_add("co_id", Config.getCoId());
        flt.evel_fault_addl_info_add("pod_id", Config.getPodId());
        flt.evel_fault_addl_info_add("resourceId", resourceId);
        flt.evel_fault_category_set(category);

        logger.info("Sending fault event");
        int code = AgentMain.evel_post_event_immediate(flt);
        logger.info("Fault event http code received: " + code);
        if(code == 0 || code >= HttpURLConnection.HTTP_BAD_REQUEST ) {
            return false;
        } else {
            return true;
        }
    }

    private static EVEL_SEVERITIES mapSeverity(String severity) {
        String severityUpper = severity.toUpperCase();
        switch (severityUpper) {
            case "INDETERMINATE":
                return EVEL_SEVERITIES.EVEL_SEVERITY_NORMAL;
            default:
                return EVEL_SEVERITIES.valueOf("EVEL_SEVERITY_" + severityUpper);
        }
    }

    private static EVEL_SOURCE_TYPES mapType(String type) {
        return EVEL_SOURCE_TYPES.valueOf("EVEL_SOURCE_" + type.toUpperCase());
    }
}
