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
package mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class VesVolthaMessage {
    private String id = "";
    private String logical_device_id = "";
    private String raised_ts = "";
    private String description = "";
    private String type = "";
    private String category = "";
    private String severity = "";
    private String state = "";
    private String resource_id = "";
    private Map<String,String> context;

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getLogicalDeviceId() {
        return logical_device_id;
    }

    public String getRaisedTS() {
        return raised_ts;
    }

    public String getCategory() {
        //Passing type instead of category to map bewteen VES and VOLTHA.
        return category;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getState() {
        return state;
    }

    public Map<String,String> getContext() {
        return context;
    }

    public String getResourceId() {
        return resource_id;
    }


}
