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

import java.net.HttpURLConnection;

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
            category, //eventCategory
            severity, //event severity
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

        EventHeader header = new EventHeader("other", System.currentTimeMillis() + ":" + message.getTs(),
                                                "other_VOLTHA_KPI");
        EventKpi ev = new EventKpi();
        ev.addAdditionalValues("voltha", json);
        ev.addAdditionalValues("slices", message.getSliceData());
        ev.addAdditionalValues("co_id", Config.getCoId());
        ev.addAdditionalValues("pod_id", Config.getPodId());
        ev.addAdditionalValues("type", message.getType());
        ev.addAdditionalValues("ts", message.getTs());

        logger.info("Sending KPI event");
        List<VesBlock> blocks = new ArrayList<>();
        blocks.add(header);
        blocks.add(ev);
        int code = dispatcher.sendEvent(blocks);logger.info("KPI event http code received: " + code);
        return code;
    }
}
