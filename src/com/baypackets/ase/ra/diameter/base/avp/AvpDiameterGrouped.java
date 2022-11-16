package com.baypackets.ase.ra.diameter.base.avp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterFloat32AVP;
import fr.marben.diameter.DiameterFloat64AVP;
import fr.marben.diameter.DiameterGenericAVP;
import fr.marben.diameter.DiameterGroupedAVP;
import fr.marben.diameter.DiameterInteger32AVP;
import fr.marben.diameter.DiameterInteger64AVP;
import fr.marben.diameter.DiameterMessageFactory;
import fr.marben.diameter.DiameterOctetStringAVP;
import fr.marben.diameter.DiameterUnsigned32AVP;
import fr.marben.diameter.DiameterUnsigned64AVP;

public class AvpDiameterGrouped  extends AvpDiameter {

	DiameterGroupedAVP stackObj;
	
	private DiameterMessageFactory diameterMsgFactory;

	public AvpDiameterGrouped(DiameterGroupedAVP stkObj,DiameterMessageFactory diameterMsgFactory){
		super(stkObj);
		this.stackObj=stkObj;
		this.diameterMsgFactory=diameterMsgFactory;
	}

	public DiameterAVP getAvp(String avpName){
		// TODO
		DiameterAVP avp= null;
		List stackList=stackObj.getValue();//AVP(vendorId);
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAVP && (((DiameterAVP)obj).getName() ==avpName)){
				avp=(DiameterAVP)obj;
				break;
			}
		}
		return avp;
	}
	
	public void  addDiameterInteger32AVP(String name, long value){
   	
		DiameterInteger32AVP avpIn=diameterMsgFactory.createInteger32AVP(
				name, stackObj.getVendorName(), (int)value);
		
		 stackObj.add(avpIn);
     }
	
	
	public void addDiameterInteger64AVP(String name, long value) {

		DiameterInteger64AVP avpIn = diameterMsgFactory.createInteger64AVP(
				name,  stackObj.getVendorName(), (int) value);

		stackObj.add(avpIn);
		
	}

	public void addDiameterUnsigned32AVP(String name, long value) {

		DiameterUnsigned32AVP avpIn = diameterMsgFactory.createUnsigned32AVP(
				name,  stackObj.getVendorName(), (int) value);
		stackObj.add(avpIn);

	}
	
	
	public void  addDiameterUnsigned64AVP(String name, BigInteger value){
					
			DiameterUnsigned64AVP avpIn=diameterMsgFactory.createUnsigned64AVP(
					name,stackObj.getVendorName(), value);
			stackObj.add(avpIn);
			//return new CCRequestNumberAvp(stackObj.addCCRequestNumber(value));
	     }
	
	
	public void  addDiameterFloat32AVP(String name, float value){
		
			
			DiameterFloat32AVP avpIn=diameterMsgFactory.createFloat32AVP(
					name,stackObj.getVendorName(), value);
			 stackObj.add(avpIn);
	     }
	
	
	public void  addDiameterFloat64AVP(String name,double value){
	
			DiameterFloat64AVP avpIn=diameterMsgFactory.createFloat64AVP(
					name, stackObj.getVendorName(), value);
			 stackObj.add(avpIn);
			
	     }
	
		public void addDiameterGenericAVP(long avpCode, long vendorId, byte[] value) {
	
			DiameterGenericAVP avpIn = diameterMsgFactory.createGenericAVP(avpCode,
					vendorId, value);
	
			stackObj.add(avpIn);
		}
	
	
		public void  addDiameterOctetStringAVP(String name,byte[] value){
				
	       DiameterOctetStringAVP avpIn=diameterMsgFactory.createOctetStringAVP(name,stackObj.getVendorName(),value);
		   stackObj.add(avpIn);
				
		 }
		
		public void addDiameterOctetStringAVP(String name,String value) {
	
			DiameterOctetStringAVP avpIn = diameterMsgFactory.createOctetStringAVP(
					name, stackObj.getVendorName(), value);
	
			stackObj.add(avpIn);
	
		}
	

	public void removeAvp(DiameterAVP avp){
		stackObj.remove(avp);
	}
	
	public void removeAvp(int index){
		stackObj.remove(index);
	}

	public DiameterAVP getAvp(int index){
		// TODO
		DiameterAVP avp=stackObj.get(index);//AVP(vendorId);
		
		return avp;
	}

	public boolean containsAVP(DiameterAVP avp){
		// TODO
		return stackObj.contains(avp);//AVP(vendorId);
		
	}
	
	public DiameterAVP getAvp(String avpName,String vendorName){
		// TODO
		DiameterAVP avp=stackObj.find(avpName,vendorName);//AVP(vendorId);
		
		return avp;
	}
	
	public DiameterAVP getAvp(String avpName,String vendorName,DiameterAVP currentAVP){
		// TODO
		DiameterAVP avp=stackObj.findNext(avpName,vendorName,currentAVP);//AVP(vendorId);
		
		return avp;
	}

	public int getAvpCount(){
		return stackObj.getValue().size();
	}

	public String getAvpFormat(){
		return stackObj.getInstanceType();
	}

	public java.util.List<DiameterAVP> getAvpList(){
		// TODO 
		return stackObj.getValue();
	}

//	public java.lang.String getData(){
//		return stackObj.get
//	}

	public java.util.List<DiameterAVP> getVendorSpecificAvpSet(){
		
		List containerList= new ArrayList<DiameterAVP>();
		List stackList=stackObj.getValue();//AVP(vendorId);
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAVP && (((DiameterAVP)obj).getVBit())){
				containerList.add(obj);//createContainerAvp((DiameterAVP)obj));
			}
		}
		return containerList;
		
	}

//	public DiameterVendorSpecificAvpSetNE getVendorSpecificAvpSetNe(){
//		return null;
//	}

	public boolean isValid(){
		return true;//stackObj.isValid();
	}

	public java.lang.String toString(){
		return stackObj.toString();
	}

	public void toXML(java.lang.StringBuilder builder, java.lang.String prefix){
		//stackObj.toXML(builder, prefix);
	}

	public void write(java.nio.ByteBuffer otherBuffer){
		//stackObj.write(otherBuffer);
	}

	@Override
	boolean getMBit() {
		// TODO Auto-generated method stub
		return stackObj.getMBit();
	}

	@Override
	boolean getPBit() {
		// TODO Auto-generated method stub
		return stackObj.getPBit();
	}

	@Override
	boolean getVBit() {
		// TODO Auto-generated method stub
		return stackObj.getVBit();
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return stackObj.toString();
	}

}
