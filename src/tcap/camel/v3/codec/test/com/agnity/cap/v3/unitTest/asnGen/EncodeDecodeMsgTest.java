package com.agnity.cap.v3.unitTest.asnGen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import asnGenerated.v3.ConnectArg;
import asnGenerated.v3.ConnectToResourceArg;
import asnGenerated.v3.ContinueWithArgumentArg;
import asnGenerated.v3.DisconnectForwardConnectionWithArgumentArg;
import asnGenerated.v3.EventReportBCSMArg;
import asnGenerated.v3.InitialDPArg;
import asnGenerated.v3.PlayAnnouncementArg;
import asnGenerated.v3.ReleaseCallArg;
import asnGenerated.v3.RequestReportBCSMEventArg;
import asnGenerated.v3.SpecializedResourceReportArg;

import com.agnity.cap.v3.util.CapFunctions;

import junit.framework.TestCase;

public class EncodeDecodeMsgTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}
	
	private byte[] encode(Object obj){
		byte[] bytes=null;
		try {
			IEncoder ie = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ie.encode(obj, bo);
			bytes = bo.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	private Object decode(byte[] bytes, Class c){
		Object obj = null;
	    try {
			IDecoder id = CoderFactory.getInstance().newDecoder("BER");
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			obj = id.decode(bi, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public void testInitialDpArg(){
		String hexStr = "307780016e8308041019999911414485010a8801008a0704101032547698bb0580038090a39c01029f320864001200141044f4bf34160201008106911032547698a309800764f0f000010000bf35038301119f360700010b010008009f37069110325476989f3806a199991131229f39080211301222201522";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		InitialDPArg idp = (InitialDPArg) this.decode(bytes, InitialDPArg.class);
		byte[] exBytes = this.encode(idp);
		assertTrue(Arrays.equals(bytes,exBytes));
	}
	
	public void testConnectArg(){
		String hexStr = "3019a00a04080410795242026347ae0b0409060413795297309620";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		ConnectArg ca = (ConnectArg) this.decode(bytes, ConnectArg.class);
		byte[] exBytes = this.encode(ca);
		assertTrue(Arrays.equals(bytes,exBytes));
	}
	
	public void testRequestReportBCSMArg(){
		String hexStr = "3043a041300b80010a810101a203800101300b800107810100a203800102300b800106810100a203800102300b800105810100a203800102300b800109810100a203800101";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		RequestReportBCSMEventArg rrb = (RequestReportBCSMEventArg) this.decode(bytes, RequestReportBCSMEventArg.class);
		byte[] exBytes = this.encode(rrb);
		assertTrue(Arrays.equals(bytes,exBytes));
	}
	
	public void testEventReportBCSMArg(){
		String hexStr = "300d800107a303810102a403800100";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		EventReportBCSMArg erb= (EventReportBCSMArg) this.decode(bytes, EventReportBCSMArg.class);
		byte[] exBytes = this.encode(erb);
		assertTrue(Arrays.equals(bytes,exBytes));
	}
	
	public void testReleaseCallArg(){
		String hexStr = "04028090";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		ReleaseCallArg rca = (ReleaseCallArg) this.decode(bytes, ReleaseCallArg.class);
		byte[] exBytes = this.encode(rca);
		assertTrue(Arrays.equals(bytes,exBytes));
	}
	
	public void testConnectToResourceArg(){
		String hexStr = "300683009f320101";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		ConnectToResourceArg ctr = (ConnectToResourceArg) this.decode(bytes, ConnectToResourceArg.class);
		byte[] exBytes = this.encode(ctr);
		assertTrue(Arrays.equals(bytes,exBytes));
	}
	
	public void testPlayAnnouncementArg(){
		String hexStr = "3012a00aa008a00380017f82010a8201ff850101";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		PlayAnnouncementArg pa = (PlayAnnouncementArg) this.decode(bytes, PlayAnnouncementArg.class);
		byte[] exBytes = this.encode(pa);
		assertTrue(Arrays.equals(bytes,exBytes));
		
	}
	
	public void testSpecializedResourceReportArg(){
		
	}
	
	public void testDisconnectForwardConnectionWithArgumentArg(){
		String hexStr = "3003810101";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		DisconnectForwardConnectionWithArgumentArg dfc = (DisconnectForwardConnectionWithArgumentArg) this.decode(bytes, DisconnectForwardConnectionWithArgumentArg.class);
		byte[] exBytes = this.encode(dfc);
		assertTrue(Arrays.equals(bytes,exBytes));
	}
	
	public void testActivityTest(){
		
	}
	
	public void testAssistRequestInstructions(){
		
	}
	
	public void testContinueWithArgumentArg(){
		String hexStr = "3008bf3b05a303800101";
		byte[] bytes = CapFunctions.hexStringToByteArray(hexStr);
		ContinueWithArgumentArg caa = (ContinueWithArgumentArg) this.decode(bytes, ContinueWithArgumentArg.class);
		byte[] exBytes = this.encode(caa);
		assertTrue(Arrays.equals(bytes,exBytes));
	}
	
	public void testPromptAndCollectUserInformation(){
		
	}
	
	public void testEstablishTemporaryConnection(){
		
	}
	
	
}
