package com.genband.ase.alcx.MediaService.MediaServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;

import javax.servlet.sip.SipServletMessage;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.baypackets.ase.msadaptor.InputValidator;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsConfSpec;
import com.baypackets.ase.msadaptor.MsDialogSpec;
import com.baypackets.ase.msadaptor.MsOperationSpec;
import com.baypackets.ase.sbb.ConferenceController;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsCollectSpec;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsPlaySpec;
import com.baypackets.ase.sbb.MsRecordSpec;
import com.baypackets.ase.sbb.MsVarAnnouncement;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.sip.SipServiceContextProvider;

public class _MLAdaptor implements MsAdaptor {
    private static final String[] PLAY_RESULT = new String[]{"play.amt".intern(),"play.end".intern()};
    private static final String[] COLLECT_RESULT = new String[]{"dtmf.digits".intern(), "dtmf.len".intern(), "dtmf.last".intern(), "dtmf.end".intern()};
    private static final String[] RECORD_RESULT = new String[]{"record.recordid".intern(), "record.len".intern(), "record.end".intern()};
    static Logger logger = Logger.getLogger(_MLAdaptor.class.getName());
    public _MLAdaptor(){
    }

    public _MLAdaptor(ServiceContext sContext){
        this.sContext = sContext;
    }

    public String getConnectionId(int connectionType, String externalId){
        return new String("1000");
    }

    /**
     * This method parses the SDP in the given SIP message and sets the host and
     * port attributes of this SBB using the values specified in the connection
     * and media description fields of the SDP.
     */
    public String getConnectionId(SipServletMessage message)  {
        return new String("1000");
    }

    public String getMediaServerURI(MediaServer mediaServer, int connectionType, Object data) {
            return "sip:" + sContext.getAttribute(sContext.getAttribute("MediaType") + "MediaServerUser") + "@" + sContext.getAttribute("MEDIA_SERVER_IP") + ":" + sContext.getAttribute("MEDIA_SERVER_PORT");
    }

    public boolean isMatchingResult(String eventId, String connectionId, String operationId, MsOperationResult result){
     	
     	sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<<<<<<<<<<<<<<<_MLAdaptor.java |isMatchingResult");
            boolean matching = true;
            return matching;
    }

    public MsOperationResult parseMessage(SipServletMessage message)
    {
    	sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MLAdaptor.java |parseMessage |parsing message ="+message.toString());
        String content = null;
        MsOperationResult result=null;
            try{
                    if (message.getContent() instanceof byte[]) {
                    	sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MLAdaptor.java |parseMessage |message instanceof byte[] =");
                             content = new String((byte[])message.getContent());
                         	sContext.log(logger, Level.DEBUG,"[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+ "<<<<<<<<<<<<<<<_MLAdaptor.java |parseMessage |message content ="+content);
                            
                    } else if (message.getContent() != null) {
                            content = message.getContent().toString();
                            sContext.log(logger, Level.DEBUG, "[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<<<<<<<<<<<<<<<_MLAdaptor.java |parseMessage |OTHERWISE message content ="+content);
                       
                    }
                    result= new _MsResult(content, sContext);
            }catch(IOException e){
                    String msg = "Error occurred while parsing message received from media server: " + e.getMessage();
                    System.out.println(msg);
            }catch(MediaServerException e){
                String msg = "Error occurred while parsing message received from media server: " + e.getMessage();
                System.out.println(msg);
             }
            return result;
    }
    public MsOperationResult parseMessage(SipServletMessage message,MsOperationSpec spec) { return null; }

    synchronized protected MsOperationResult parseMessage(String message)
    {
    	  MsOperationResult result=null;
    	try{
            result= new _MsResult(message, sContext);
    	}catch(MediaServerException e){
            String msg = "Error occurred while parsing message received from media server: " + e.getMessage();
            System.out.println(msg);
        }
    	return result;
    }

    protected void generateSpec(Object spec, StringBuffer buffer){}
    protected void generatePlaySpec(MsPlaySpec spec, StringBuffer buffer){}
    private void generateAudioTag(Object uri, StringBuffer buffer){}
    private void generateVarTag(MsVarAnnouncement var, StringBuffer buffer){}
    protected void generateCollectSpec(MsCollectSpec spec, StringBuffer buffer){}
    protected void generateRecordSpec(MsRecordSpec spec, StringBuffer buffer){}
    protected void generateConfSpec(MsConfSpec spec, StringBuffer buffer){}
    protected void generateCreateConfElement(MsConfSpec spec , StringBuffer buffer){}
    protected void generateUpdateConfElement(MsConfSpec spec , StringBuffer buffer){}
    protected void generateUnjoinElement(MsConfSpec spec , StringBuffer buffer){ }
    public void generateMessage(SipServletMessage message, MsOperationSpec[] specs) { }
    public void generateControlMessage(SipServletMessage message) { }

    private ServiceContext sContext;
}
