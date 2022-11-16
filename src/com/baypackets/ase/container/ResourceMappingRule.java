package com.baypackets.ase.container;

import java.util.ArrayList;

import com.baypackets.ase.dispatcher.Rule;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.spi.container.SasMessage;

public class ResourceMappingRule extends Rule{
	
	private static final long serialVersionUID = -348407242055836701L;
	private String resourceName;
	
	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public ResourceMappingRule() {
		super();
	}

	public boolean evaluate(String[] input, ArrayList list) {
		return (input[0] != null && input[0].equals(resourceName) );
	}

	public String[] getInputData(SasMessage message) {
		String[] data = new String[1];
		if(message instanceof Message){
			data[0] = message.getMessageContext().getObjectName();
		}
		return data;
	}
	
	public ArrayList getInputParameterData(SasMessage message) {
		ArrayList data = new ArrayList(1);
		if(message instanceof Message){
			 data.add(0,message.getMessageContext().getObjectName());
		}
		return data;
	}
}
