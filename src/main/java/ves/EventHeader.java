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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.util.Enumeration;
import java.net.NetworkInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHeader implements VesBlock {
    private transient static String hostname = "";
    private transient static String uuid = "";

    private transient final Logger logger = LoggerFactory.getLogger("EventHeader");

    private String domain;
    private String eventId;
    private String eventName;
    private long lastEpochMicrosec;
    private String priority;
    private String reportingEntityId;
    private String reportingEntityName;
    private int sequence;
    private String sourceId;
    private String sourceName;
    private long startEpochMicrosec = 0;
    private int version = 3;

    public EventHeader(String domain, String eventId, String eventName) {
        this.domain = domain;
        this.eventId = eventId;
        this.eventName = eventName;
        this.priority = "High";
        this.sequence = 1;
        //microseconds are not supported in java 8. So just approximating it.
        this.lastEpochMicrosec = (long)(System.nanoTime()/1000.0);

        setIdAndName();
    }

    private void setIdAndName() {
        if (!EventHeader.hostname.equals("") && !EventHeader.uuid.equals("")) {
            this.reportingEntityId = uuid;
            this.sourceId = uuid;
            this.reportingEntityName = hostname;
            this.sourceName = hostname;
            return;
        }

        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            EventHeader.hostname = addr.getHostName();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Hostname can not be resolved");
        }

        try {
            Enumeration<NetworkInterface> networks =
                NetworkInterface.getNetworkInterfaces();
            while(networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();

                if(hostname.equalsIgnoreCase("")) {
                    Enumeration inetAddrs = network.getInetAddresses();
                    while(inetAddrs.hasMoreElements()){
                        InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                        if (!inetAddr.isLoopbackAddress()) {
                            EventHeader.hostname = inetAddr.getHostAddress();
                            break;
                        }
                    }
                }

                if (mac != null) {
                    EventHeader.uuid = bytesToHex(mac);
                }
            }
        } catch (SocketException e) {
            logger.error(e.toString());
        }

        this.reportingEntityId = uuid;
        this.sourceId = uuid;
        this.reportingEntityName = hostname;
        this.sourceName = hostname;
    }

    private final transient char[] hexArray = "0123456789ABCDEF".toCharArray();
    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String getName() {
        return "commonEventHeader";
    }

    public Class getType() {
        return EventHeader.class;
    }

	/**
	* Returns value of domain
	* @return
	*/
	public String getDomain() {
		return domain;
	}

	/**
	* Sets new value of domain
	* @param
	*/
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	* Returns value of eventId
	* @return
	*/
	public String getEventId() {
		return eventId;
	}

	/**
	* Sets new value of eventId
	* @param
	*/
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	* Returns value of eventName
	* @return
	*/
	public String getEventName() {
		return eventName;
	}

	/**
	* Sets new value of eventName
	* @param
	*/
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	* Returns value of lastEpochMicrosec
	* @return
	*/
	public long getLastEpochMicrosec() {
		return lastEpochMicrosec;
	}

	/**
	* Sets new value of lastEpochMicrosec
	* @param
	*/
	public void setLastEpochMicrosec(long lastEpochMicrosec) {
		this.lastEpochMicrosec = lastEpochMicrosec;
	}

	/**
	* Returns value of priority
	* @return
	*/
	public String getPriority() {
		return priority;
	}

	/**
	* Sets new value of priority
	* @param
	*/
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	* Returns value of reportingEntityId
	* @return
	*/
	public String getReportingEntityId() {
		return reportingEntityId;
	}

	/**
	* Sets new value of reportingEntityId
	* @param
	*/
	public void setReportingEntityId(String reportingEntityId) {
		this.reportingEntityId = reportingEntityId;
	}

	/**
	* Returns value of reportingEntityName
	* @return
	*/
	public String getReportingEntityName() {
		return reportingEntityName;
	}

	/**
	* Sets new value of reportingEntityName
	* @param
	*/
	public void setReportingEntityName(String reportingEntityName) {
		this.reportingEntityName = reportingEntityName;
	}

	/**
	* Returns value of sequence
	* @return
	*/
	public int getSequence() {
		return sequence;
	}

	/**
	* Sets new value of sequence
	* @param
	*/
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	* Returns value of sourceId
	* @return
	*/
	public String getSourceId() {
		return sourceId;
	}

	/**
	* Sets new value of sourceId
	* @param
	*/
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	* Returns value of sourceName
	* @return
	*/
	public String getSourceName() {
		return sourceName;
	}

	/**
	* Sets new value of sourceName
	* @param
	*/
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	* Returns value of startEpochMicrosec
	* @return
	*/
	public long getStartEpochMicrosec() {
		return startEpochMicrosec;
	}

	/**
	* Sets new value of startEpochMicrosec
	* @param
	*/
	public void setStartEpochMicrosec(long startEpochMicrosec) {
		this.startEpochMicrosec = startEpochMicrosec;
	}

	/**
	* Returns value of version
	* @return
	*/
	public int getVersion() {
		return version;
	}

	/**
	* Sets new value of version
	* @param
	*/
	public void setVersion(int version) {
		this.version = version;
	}
}
