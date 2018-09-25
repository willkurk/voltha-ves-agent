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

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class EventKpi implements VesBlock {
    private List<Map<String, String>> nameValuePairs;
    private int otherFieldsVersion = 1;

    public EventKpi() {
        nameValuePairs = new ArrayList<>();
    }

    public void addAdditionalValues(String name, String value) {
        HashMap<String, String> newValue = new HashMap<>();
        newValue.put("name", name);
        newValue.put("value", value);
        this.nameValuePairs.add(newValue);
    }

    public String getName() {
        return "otherFields";
    }

    public Class getType() {
        return EventKpi.class;
    }
}
