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

public class EventFault implements VesBlock {
    private List<Map<String, String>> alarmAdditionalInformation;
    private String alarmCondition;
    private String eventCategory;
    private String eventSeverity;
    private String eventSourceType;
    private int faultFieldsVersion = 2;
    private String specificProblem;
    private String vfStatus;

    public EventFault(String alarmCondition, String eventCategory, String eventSeverity,
                            String eventSourceType, String specificProblem, String vfStatus) {
        this.alarmCondition = alarmCondition;
        this.eventCategory = eventCategory;
        this.eventSeverity = eventSeverity;
        this.eventSourceType = eventSourceType;
        this.specificProblem = specificProblem;
        this.vfStatus = vfStatus;
        this.alarmAdditionalInformation = new ArrayList<>();
    }

    public void addAdditionalValues(String name, String value) {
        HashMap<String, String> newValue = new HashMap<>();
        newValue.put("name", name);
        newValue.put("value", value);
        this.alarmAdditionalInformation.add(newValue);
    }

    public String getName() {
        return "faultFields";
    }

    public Class getType() {
        return EventHeader.class;
    }

	/**
	* Returns value of alarmAdditionalInformation
	* @return
	*/
	public List<Map<String, String>> getAlarmAdditionalInformation() {
		return alarmAdditionalInformation;
	}

	/**
	* Sets new value of alarmAdditionalInformation
	* @param
	*/
	public void setAlarmAdditionalInformation(List<Map<String, String>> alarmAdditionalInformation) {
		this.alarmAdditionalInformation = alarmAdditionalInformation;
	}

	/**
	* Returns value of alarmCondition
	* @return
	*/
	public String getAlarmCondition() {
		return alarmCondition;
	}

	/**
	* Sets new value of alarmCondition
	* @param
	*/
	public void setAlarmCondition(String alarmCondition) {
		this.alarmCondition = alarmCondition;
	}

	/**
	* Returns value of eventCategory
	* @return
	*/
	public String getEventCategory() {
		return eventCategory;
	}

	/**
	* Sets new value of eventCategory
	* @param
	*/
	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}

	/**
	* Returns value of eventSeverity
	* @return
	*/
	public String getEventSeverity() {
		return eventSeverity;
	}

	/**
	* Sets new value of eventSeverity
	* @param
	*/
	public void setEventSeverity(String eventSeverity) {
		this.eventSeverity = eventSeverity;
	}

	/**
	* Returns value of eventSourceType
	* @return
	*/
	public String getEventSourceType() {
		return eventSourceType;
	}

	/**
	* Sets new value of eventSourceType
	* @param
	*/
	public void setEventSourceType(String eventSourceType) {
		this.eventSourceType = eventSourceType;
	}

	/**
	* Returns value of faultFieldsVersion
	* @return
	*/
	public int getFaultFieldsVersion() {
		return faultFieldsVersion;
	}

	/**
	* Sets new value of faultFieldsVersion
	* @param
	*/
	public void setFaultFieldsVersion(int faultFieldsVersion) {
		this.faultFieldsVersion = faultFieldsVersion;
	}

	/**
	* Returns value of specificProblem
	* @return
	*/
	public String getSpecificProblem() {
		return specificProblem;
	}

	/**
	* Sets new value of specificProblem
	* @param
	*/
	public void setSpecificProblem(String specificProblem) {
		this.specificProblem = specificProblem;
	}

	/**
	* Returns value of vfStatus
	* @return
	*/
	public String getVfStatus() {
		return vfStatus;
	}

	/**
	* Sets new value of vfStatus
	* @param
	*/
	public void setVfStatus(String vfStatus) {
		this.vfStatus = vfStatus;
	}
}
