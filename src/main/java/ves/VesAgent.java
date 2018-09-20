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
import mapper.VesVolthaAlarm;
import mapper.VesVolthaKpi;
import kafka.KafkaConsumerType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonSyntaxException;

import java.util.List;
import java.util.ArrayList;

public class VesAgent {

    private static final Logger logger = LoggerFactory.getLogger("VesAgent");

    private VesVolthaMapper mapper;

    private VesDispatcher dispatcher;

    public VesAgent() {
        logger.info("Initializing VES Agent");
        try {
            mapper = new VesVolthaMapper();
            dispatcher = new VesDispatcher("http://"+Config.getVesAddress(),
                Config.getVesPort());
        } catch(Exception e) {
            logger.error("Failed to initialize VES", e);
            logger.error(e.toString());
        }
    }

    public boolean sendToVES(KafkaConsumerType type, String json) throws JsonSyntaxException {
        int code = 0;

        switch (type) {
            case ALARMS:
                code = sendFault(json);
                break;
            case KPIS:
                code = sendKpi(json);
                break;
        }

        if(code == 0 || code >= HttpURLConnection.HTTP_BAD_REQUEST ) {
            return false;
        } else {
            return true;
        }
    }

    private int sendFault(String json) {
        VesVolthaAlarm message = mapper.parseAlarm(json);

        String id = message.getId();
        String[] idsplit = id.split("\\.");
        String eventType = idsplit[idsplit.length-1];
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

        EventHeader header = new EventHeader("fault", ldeviceId + ":" + ts,
                                                "Fault_VOLTHA_" + eventType);
        EventFault flt  = new EventFault(
            id, //alarm conidition
            severity, //event severity
            category, //eventCategory
            type, //source type
            description, //specificProblem
            "Active" //getVfStatus
            );
        flt.addAdditionalValues("voltha", json);
        flt.addAdditionalValues("state", state);
        flt.addAdditionalValues("co_id", Config.getCoId());
        flt.addAdditionalValues("pod_id", Config.getPodId());
        flt.addAdditionalValues("resourceId", resourceId);

        logger.info("Sending fault event");
        List<VesBlock> blocks = new ArrayList<>();
        blocks.add(header);
        blocks.add(flt);
        int code = dispatcher.sendEvent(blocks);
        logger.info("Fault event http code received: " + code);
        return code;
    }

    private int sendKpi(String json) {
        VesVolthaKpi message = mapper.parseKpi(json);

        EvelOther ev = new EvelOther("measurement_VOLTHA_KPI", "vmname_ip");
        ev.evel_other_field_add("co_id", Config.getCoId());
        ev.evel_other_field_add("pod_id", Config.getPodId());
        ev.evel_other_field_add("type", message.getType());
        ev.evel_other_field_add("ts", message.getTs());
        ev.evel_other_field_add("slices", message.getSliceData());

        ev.evel_other_field_add("voltha", json);

        logger.info("Sending fault event");
        int code = AgentMain.evel_post_event_immediate(ev);
        logger.info("Fault event http code received: " + code);
        return code;
    }

    private EVEL_SEVERITIES mapSeverity(String severity) {
        String severityUpper = severity.toUpperCase();
        switch (severityUpper) {
            case "INDETERMINATE":
                return EVEL_SEVERITIES.EVEL_SEVERITY_NORMAL;
            default:
                return EVEL_SEVERITIES.valueOf("EVEL_SEVERITY_" + severityUpper);
        }
    }

    private EVEL_SOURCE_TYPES getSourceType() {
        return EVEL_SOURCE_TYPES.valueOf("EVEL_SOURCE_OLT");
    }
}
