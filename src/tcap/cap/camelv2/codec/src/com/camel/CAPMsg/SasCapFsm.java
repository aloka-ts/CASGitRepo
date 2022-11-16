package com.camel.CAPMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import asnGenerated.EventTypeBCSM;


public class SasCapFsm {

	private static Map<String , SasCapAlwdOp> opAllwdMap = new HashMap<String, SasCapAlwdOp>();
	
	private static Map<EventTypeBCSM.EnumType, SasCapCallStateEnum> stateMap = new HashMap<EventTypeBCSM.EnumType, SasCapCallStateEnum>();

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapFsm.class);	 
	
	public static void loadOpAllwdMap() throws Exception{
		
		logger.info("loadOpAllwdMap:Enter");
		String key = SasCapCallStateEnum.ANALYZED_INFORMATION.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		// preparing object of SasCapAlwdOp
		SasCapAlwdOp obj1 = new SasCapAlwdOp();
		obj1.setNextState(SasCapCallStateEnum.CONNECT_IN_PROGRESS);
		ArrayList<Byte> opCode = new ArrayList<Byte>();
		opCode.add(CAPOpcode.REQUEST_REPORT);
		opCode.add(CAPOpcode.APPLY_CHARGING);
		opCode.add(CAPOpcode.CONNECT);
		opCode.add(CAPOpcode.CONTINUE);
		obj1.setAllwdOpCode(opCode);
		opAllwdMap.put(key, obj1);
		
		String key25 = SasCapCallStateEnum.IVR_DISCONNECTED.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		// preparing object of SasCapAlwdOp
		SasCapAlwdOp obj25 = new SasCapAlwdOp();
		obj25.setNextState(SasCapCallStateEnum.CONNECT_IN_PROGRESS);
		ArrayList<Byte> opCode25 = new ArrayList<Byte>();
		opCode25.add(CAPOpcode.REQUEST_REPORT);
		opCode25.add(CAPOpcode.APPLY_CHARGING);
		opCode25.add(CAPOpcode.CONNECT);
		opCode25.add(CAPOpcode.CONTINUE);
		obj25.setAllwdOpCode(opCode25);
		opAllwdMap.put(key25, obj25);
		
		String key1 = SasCapCallStateEnum.O_BUSY.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		opAllwdMap.put(key1, obj1);		
		String key2 = SasCapCallStateEnum.O_NOANSWER.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		opAllwdMap.put(key2, obj1);		
		String key3 = SasCapCallStateEnum.O_DISCONNECT_LEG2.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		opAllwdMap.put(key3, obj1);			
		String key4 = SasCapCallStateEnum.O_BUSY.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		opAllwdMap.put(key4, obj1);
		String key55 = SasCapCallStateEnum.T_BUSY.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		opAllwdMap.put(key55, obj1);		
		String key56 = SasCapCallStateEnum.T_NOANSWER.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		opAllwdMap.put(key56, obj1);		
		String key57 = SasCapCallStateEnum.T_DISCONNECT_LEG2.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		opAllwdMap.put(key57, obj1);			
		String key58 = SasCapCallStateEnum.T_BUSY.getCode() + "," + SasCapApiEnum.CONNECT.getCode();
		opAllwdMap.put(key58, obj1);
				
		SasCapAlwdOp obj2 = new SasCapAlwdOp();
		obj2.setNextState(null);
		ArrayList<Byte> opCode1 = new ArrayList<Byte>();
		opCode1.add(CAPOpcode.RELEASE_CALL);
		obj2.setAllwdOpCode(opCode1);
		
		String key5 = SasCapCallStateEnum.ANALYZED_INFORMATION.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key5, obj2);		
		String key6 = SasCapCallStateEnum.O_BUSY.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key6, obj2);		
		String key7 = SasCapCallStateEnum.O_NOANSWER.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key7, obj2);		
		String key8 = SasCapCallStateEnum.O_DISCONNECT_LEG2.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key8, obj2);
		String key66 = SasCapCallStateEnum.T_BUSY.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key66, obj2);		
		String key67 = SasCapCallStateEnum.T_NOANSWER.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key67, obj2);		
		String key68 = SasCapCallStateEnum.T_DISCONNECT_LEG2.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key68, obj2);
		String key93 = SasCapCallStateEnum.T_DISCONNECT_LEG1.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key93, obj2);
		String key11 = SasCapCallStateEnum.CONNECTED_ACHRPT.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key11, obj2);
		String key91 = SasCapCallStateEnum.O_DISCONNECT_LEG1.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key91, obj2);
		String key92 = SasCapCallStateEnum.ERROR_STATE.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key92, obj2);
		String key99 = SasCapCallStateEnum.IVR_DISCONNECTED.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key99, obj2);
		String key101 = SasCapCallStateEnum.O_ABANDON.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key101, obj2);
		String key102 = SasCapCallStateEnum.T_ABANDON.getCode() + "," + SasCapApiEnum.RELEASE_CALL.getCode();
		opAllwdMap.put(key102, obj2);
		
		String key10 = SasCapCallStateEnum.CONNECTED_ACHRPT.getCode() + "," + SasCapApiEnum.APPLY_CHARGING.getCode();
		SasCapAlwdOp obj3 = new SasCapAlwdOp();
		obj3.setNextState(SasCapCallStateEnum.CONNECTED_ACHRPT);
		ArrayList<Byte> opCode2 = new ArrayList<Byte>();
		opCode2.add(CAPOpcode.APPLY_CHARGING);
		opCode2.add(CAPOpcode.CONTINUE);
		obj3.setAllwdOpCode(opCode2);
		opAllwdMap.put(key10, obj3);
		String key71 = SasCapCallStateEnum.CONNECTED.getCode() + "," + SasCapApiEnum.APPLY_CHARGING.getCode();
		opAllwdMap.put(key71, obj3);
		String key72 = SasCapCallStateEnum.IVR_DISCONNECTED.getCode() + "," + SasCapApiEnum.APPLY_CHARGING.getCode();
		opAllwdMap.put(key72, obj3);
		
		String key31 = SasCapCallStateEnum.ANALYZED_INFORMATION.getCode() + "," + SasCapApiEnum.CONNECT_IVR.getCode();
		SasCapAlwdOp obj31 = new SasCapAlwdOp();
		obj31.setNextState(SasCapCallStateEnum.USER_INTERACTION_IN_PROGRESS);
		ArrayList<Byte> opCode31 = new ArrayList<Byte>();
		opCode31.add(CAPOpcode.ESTABLISH_TEMP_CONNECTION);
		obj31.setAllwdOpCode(opCode31);
		opAllwdMap.put(key31, obj31);
		
		String key32 = SasCapCallStateEnum.CONNECTED_ACHRPT.getCode() + "," + SasCapApiEnum.FURNISH_CHARGING.getCode();
		SasCapAlwdOp obj32 = new SasCapAlwdOp();
		obj32.setNextState(SasCapCallStateEnum.USER_INTERACTION_IN_PROGRESS);
		ArrayList<Byte> opCode32 = new ArrayList<Byte>();
		opCode32.add(CAPOpcode.FURNISH_CHARGING_INFORMATION);
		obj32.setAllwdOpCode(opCode32);
		opAllwdMap.put(key32, obj32);
		
		String key12 = SasCapCallStateEnum.USER_INTERACTION_COMPLETED.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		SasCapAlwdOp obj4 = new SasCapAlwdOp();
		obj4.setNextState(SasCapCallStateEnum.USER_INTERACTION_IN_PROGRESS);
		ArrayList<Byte> opCode3 = new ArrayList<Byte>();
		opCode3.add(CAPOpcode.PROMPT_COLLECT);
		opCode3.add(CAPOpcode.CONNECT_TO_RESOURCE);
		opCode3.add(CAPOpcode.REQUEST_REPORT);
		obj4.setAllwdOpCode(opCode3);
		opAllwdMap.put(key12, obj4);
		String key13 = SasCapCallStateEnum.O_BUSY.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		opAllwdMap.put(key13, obj4);
		String key14 = SasCapCallStateEnum.O_NOANSWER.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		opAllwdMap.put(key14, obj4);
		String key15 = SasCapCallStateEnum.O_DISCONNECT_LEG2.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		opAllwdMap.put(key15, obj4);
		String key73 = SasCapCallStateEnum.T_BUSY.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		opAllwdMap.put(key73, obj4);
		String key74 = SasCapCallStateEnum.T_NOANSWER.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		opAllwdMap.put(key74, obj4);
		String key75 = SasCapCallStateEnum.T_DISCONNECT_LEG2.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		opAllwdMap.put(key75, obj4);
		String key16 = SasCapCallStateEnum.IVR_DISCONNECTED.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		opAllwdMap.put(key16, obj4);
		String key96 = SasCapCallStateEnum.ANALYZED_INFORMATION.getCode() + "," + SasCapApiEnum.PLAYANDCOLLECT.getCode();
		opAllwdMap.put(key96, obj4);
		
		
		String key17 = SasCapCallStateEnum.ANALYZED_INFORMATION.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		SasCapAlwdOp obj5 = new SasCapAlwdOp();
		obj5.setNextState(SasCapCallStateEnum.USER_INTERACTION_IN_PROGRESS);
		ArrayList<Byte> opCode4 = new ArrayList<Byte>();
		opCode4.add(CAPOpcode.PLAY_ANNOUNCEMENT);
		opCode4.add(CAPOpcode.CONNECT_TO_RESOURCE);
		opCode4.add(CAPOpcode.REQUEST_REPORT);
		obj5.setAllwdOpCode(opCode4);
		opAllwdMap.put(key17, obj5);
		String key18 = SasCapCallStateEnum.O_BUSY.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		opAllwdMap.put(key18, obj5);
		String key19 = SasCapCallStateEnum.O_NOANSWER.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		opAllwdMap.put(key19, obj5);
		String key20 = SasCapCallStateEnum.O_DISCONNECT_LEG2.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		opAllwdMap.put(key20, obj5);
		String key76 = SasCapCallStateEnum.T_BUSY.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		opAllwdMap.put(key76, obj5);
		String key77 = SasCapCallStateEnum.T_NOANSWER.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		opAllwdMap.put(key77, obj5);
		String key78 = SasCapCallStateEnum.T_DISCONNECT_LEG2.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		opAllwdMap.put(key78, obj5);
		String key21 = SasCapCallStateEnum.IVR_DISCONNECTED.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		opAllwdMap.put(key21, obj5);
		String key51 = SasCapCallStateEnum.USER_INTERACTION_COMPLETED.getCode() + "," + SasCapApiEnum.PLAY.getCode();
		opAllwdMap.put(key51, obj5);
		
		
		
		String key22 = SasCapCallStateEnum.USER_INTERACTION_COMPLETED.getCode() + "," + SasCapApiEnum.DISCONNECT_IVR.getCode();
		SasCapAlwdOp obj6 = new SasCapAlwdOp();
		obj6.setNextState(SasCapCallStateEnum.IVR_DISCONNECTED);
		ArrayList<Byte> opCode5 = new ArrayList<Byte>();
		opCode5.add(CAPOpcode.DISCONNECT_FORWARD_CONNECTION);
		obj6.setAllwdOpCode(opCode5);
		opAllwdMap.put(key22, obj6);		
		String key23 = SasCapCallStateEnum.O_ABANDON.getCode() + "," + SasCapApiEnum.DISCONNECT_IVR.getCode();
		opAllwdMap.put(key23, obj6);
		String key24 = SasCapCallStateEnum.T_ABANDON.getCode() + "," + SasCapApiEnum.DISCONNECT_IVR.getCode();
		opAllwdMap.put(key24, obj6);
		
		String key62 = SasCapCallStateEnum.O_ABANDON.getCode() + "," + SasCapApiEnum.TC_END.getCode();
		SasCapAlwdOp obj62 = new SasCapAlwdOp();
		obj62.setNextState(null);
		opAllwdMap.put(key62, obj62);
		String key63 = SasCapCallStateEnum.T_ABANDON.getCode() + "," + SasCapApiEnum.TC_END.getCode();
		opAllwdMap.put(key63, obj62);
		
		logger.debug("loadOpAllwdMap: OpMap:" + opAllwdMap);
	    logger.info("loadOpAllwdMap:Exit");
	}
	
	/*public static void loadStateMap(){
		logger.info("loadStateMap:Enter");
		stateMap.put(EnumType.oAnswer, SasCapCallStateEnum.CONNECTED);
		stateMap.put(EnumType.oAbandon, SasCapCallStateEnum.ORIGDISCONNECTED);
		stateMap.put(EnumType.oCalledPartyBusy, SasCapCallStateEnum.O_BUSY);
		stateMap.put(EnumType.oNoAnswer, SasCapCallStateEnum.O_NOANSWER);
		stateMap.put(EnumType.oDisconnect, SasCapCallStateEnum.TERMDISCONNECTED);
		logger.debug("loadStateMap:StateMap: " + stateMap);
		logger.info("loadStateMap:Exit");
	}*/
	
	public static Map<String, SasCapAlwdOp> getOpAllwdMap() {
		return opAllwdMap;
	}

	public static Map<EventTypeBCSM.EnumType, SasCapCallStateEnum> getStateMap() {
		return stateMap;
	}
	
}
