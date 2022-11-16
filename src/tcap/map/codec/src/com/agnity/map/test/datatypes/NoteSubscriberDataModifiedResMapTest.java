package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agnity.map.asngenerated.NoteSubscriberDataModifiedRes;
import com.agnity.map.asngenerated.SendRoutingInfoRes;
import com.agnity.map.datatypes.NoteSubscriberDataModifiedResMap;
import com.agnity.map.datatypes.SendRoutingInfoResMap;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.parser.ParseAsnToUserType;

public class NoteSubscriberDataModifiedResMapTest {

	@Test
	public void test() {
		
		byte[] atmdata = new byte[] {};
		
		NoteSubscriberDataModifiedRes asnresp = null;

		Throwable e = null;
		try{
			asnresp = (NoteSubscriberDataModifiedRes) MapOperationsCoding.
				decodeOperationsForOpCode(atmdata, MapOpCodes.MAP_NOTE_SUBSCRIBER_DATA_MODIFIED, false);
		}
		catch(Exception ex){
			System.out.println("ex1 = "+ex);
			e = ex;
			ex.printStackTrace();
		}
		
		NoteSubscriberDataModifiedResMap userobj = null;
		
		try{
		//	userobj = ParseAsnToUserType.(asnresp);
		}
		catch(Exception ex){
			e = ex;
			System.out.println("ex2 = "+ex);
			e.printStackTrace();
		}
		
		assertNull(e);
		assertNotNull(userobj);
		System.out.println("=================================");
		System.out.println(userobj);
		System.out.println("=================================");	}

}
