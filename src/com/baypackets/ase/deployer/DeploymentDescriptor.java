package com.baypackets.ase.deployer;

import java.io.InputStream;

public class DeploymentDescriptor {
	
	public static final short TYPE_SIP_DD = 1;
	public static final short TYPE_WEB_DD = 2;
	public static final short TYPE_SAS_DD = 3;
	public static final short TYPE_CAS_DD = 6;
	public static final short TYPE_RESOURCE_DD = 4;
	public static final short TYPE_SOA_DD = 5;
	
	public static final String STR_SIP_DD = "WEB-INF/sip.xml".intern();
	public static final String STR_WEB_DD = "WEB-INF/web.xml".intern();
	public static final String STR_SAS_DD = "WEB-INF/sas.xml".intern();
	//cas
	public static final String STR_CAS_DD = "WEB-INF/cas.xml".intern();
	public static final String STR_SOA_DD = "WEB-INF/soa.xml".intern();
	public static final String STR_RESOURCE_DD = "resource.xml".intern();

	private short type;
	private InputStream stream;
	
	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}
	
	public String getTypeString(){
		String str = null;
		switch(type){
			case TYPE_SIP_DD:
				str = STR_SIP_DD;
				break;
			case TYPE_WEB_DD:
				str = STR_WEB_DD;
				break;
			case TYPE_SAS_DD:
				str = STR_SAS_DD;
				break;
			case TYPE_RESOURCE_DD:
				str = STR_RESOURCE_DD;
				break;	
			case TYPE_SOA_DD:
				str = STR_SOA_DD;
				break;
			case TYPE_CAS_DD:
				str = STR_CAS_DD;
				break;
		}
		return str;
	}
	
	public void setType(String strType){
		if(strType == null)
			return;
		if(strType.equals(STR_SIP_DD)){
			this.type = TYPE_SIP_DD;
		}else if(strType.equals(STR_WEB_DD)){
			this.type = TYPE_WEB_DD;
		}else if(strType.equals(STR_SAS_DD)){
			this.type = TYPE_SAS_DD;
		}else if(strType.equals(STR_RESOURCE_DD)){
			this.type = TYPE_RESOURCE_DD;
		}else if(strType.equals(STR_SOA_DD)) {
			this.type = TYPE_SOA_DD;
		}else if(strType.equals(STR_CAS_DD)) {
			this.type = TYPE_CAS_DD;
		}
	}
	
	public DeploymentDescriptor() {
		super();
	}

	
}
