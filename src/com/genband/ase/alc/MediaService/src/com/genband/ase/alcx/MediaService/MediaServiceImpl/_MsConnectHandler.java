package com.genband.ase.alcx.MediaService.MediaServiceImpl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import javax.servlet.sip.Address;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.EarlyMediaCallback;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.b2b.EarlyMediaConnectHandler;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class _MsConnectHandler extends EarlyMediaConnectHandler implements EarlyMediaCallback {

	Logger logger = Logger.getLogger(_MsConnectHandler.class.getName());

        /**
         * Public Default Constructor used for Externalizing this Object
         */
        public _MsConnectHandler() {
                super();
        }

        public _MsConnectHandler(SipServletRequest request, Address partyB, Address from) {
                super(request, partyB, from);
        }

        public _MsConnectHandler(SipServletRequest incomingReq, Address addressB) {
                super(incomingReq, addressB);
		logger.info("_MsConnectHandler, request, addressB - invoked");
        }

	@Override
	public void sendRequest(SipServletRequest request) throws IOException{
	    logger.info("sendRequest, request - invoked");
            // Do not use application composition for this request.
            super.sendRequest(request);
	}

        public void handleResponse(SipServletResponse response) {
                int responseCode = response.getStatus();
                if(responseCode >=200 && responseCode < 299 && response.getMethod().equals("INVITE")){
                        ((_MsSessionControllerImpl)this.getSBB()).parseSDP(response);
           }

                //Do the default handling as done in the base class.
                super.handleResponse(response);
        }
}
