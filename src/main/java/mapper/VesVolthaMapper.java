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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VesVolthaMapper {

    private final Logger logger = LoggerFactory.getLogger("VesVolthaMapper");

    private Gson gson;

    public VesVolthaMapper() {
        gson = new GsonBuilder().create();
    }

    public VesVolthaMessage parseJson(String json) {
        return gson.fromJson(json, VesVolthaMessage.class);
    }
}
