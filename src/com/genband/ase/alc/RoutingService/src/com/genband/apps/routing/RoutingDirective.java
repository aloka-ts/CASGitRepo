/*
 * Created on Mar 20, 2007
 *
 */
package com.genband.apps.routing;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 */
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class RoutingDirective implements Serializable {

	public static final int DEFAULT_TIMEOUT = 30;
	public static final String DELIM = ",";

    public static final String ROUTE_SERIAL = "SERIAL".intern();
    public static final String ROUTE_PARALLEL = "PARALLEL".intern();

    public static final String MODE_B2BUA = "B2BUA".intern();
	public static final String MODE_TBCT = "TBCT".intern();
	public static final String MODE_UNATTENDED_TRANSFER = "UNATTENDED_TRANSFER".intern();

    private String type = ROUTE_SERIAL;
    private String mode = MODE_B2BUA;
    private int timeout = DEFAULT_TIMEOUT;
    private List<String> destinations = new LinkedList<String>();

    /**
     *
     */
    public RoutingDirective() {
        super();
    }

    /**
     * @return Returns the action.
     */
    public String getType() {
        return type;
    }
    /**
     * @param action The action to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    public List<String> getDestinations(){
        return this.destinations;
    }

    public void addDestinations(List<String> destinations){
		this.destinations = destinations;
    }

    public void removeDestination(String destination){
        if(destination == null)
            return;

        this.destinations.remove(destination);
    }

    public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setTimeout(String timeout){
		try{
			this.timeout = Integer.parseInt(timeout);
		}catch(NumberFormatException nfe){}
	}

	public String toString(){
    	StringBuffer buffer = new StringBuffer();
		buffer.append("Routing Directive : ");
		buffer.append("mode=");
		buffer.append(mode);
		buffer.append(",type =");
		buffer.append(type);
		buffer.append(",timeout=");
		buffer.append(timeout);
    	buffer.append(",Destinations=");
    	buffer.append(this.destinations);
    	return buffer.toString();
    }

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}
