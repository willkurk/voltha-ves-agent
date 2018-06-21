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

import org.apache.log4j.Level;
import config.Config;

public class VesAgent {


  public static boolean sendToVES(String json) {
    try{

        AgentMain.evel_initialize("http://"+Config.getVesAddress(),
                Integer.parseInt(Config.getVesPort()),
                //  "http://1.2.3.4", 8080,
                //"/vendor_event_listener","/example_vnf",
                null,null,
                "will",
                "pill",
                null, null, null,
                //"/home/gokul/newwk/demo/vnfs/VES5.0/evel/sslcerts2/my-keystore.jks", "changeit", "changeit",
                Level.TRACE);
    } catch( Exception e )
    {
        e.printStackTrace();
    }

    EvelFault flt  = new EvelFault("Fault_VOLTHA_failed", "tbd_event_key_unique_to_source",
            "NIC error", "Hardware failed",
            EvelHeader.PRIORITIES.EVEL_PRIORITY_HIGH,
            EVEL_SEVERITIES.EVEL_SEVERITY_MAJOR,
            EVEL_SOURCE_TYPES.EVEL_SOURCE_CARD,
            EVEL_VF_STATUSES.EVEL_VF_STATUS_ACTIVE);
    //flt.evel_fault_addl_info_add("nichw", "fail");
    //flt.evel_fault_addl_info_add("nicsw", "fail");
    flt.evel_fault_category_set("Communication");
    AgentMain.evel_post_event(flt);

    return true;
  }

}
