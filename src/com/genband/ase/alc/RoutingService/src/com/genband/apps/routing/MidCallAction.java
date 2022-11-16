package com.genband.apps.routing;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.B2bSessionController;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.SBBFactory;
import com.baypackets.ase.sbb.impl.SBBOperationContext;

import com.genband.apps.routing.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionClass;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionMethod;
import com.genband.ase.alc.alcml.jaxb.ALCMLMethodParameter;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.sip.SipServiceContextProvider;
/**
 * Mukesh Added the Call id of the initial INVITE request in all logs to separate out logs on the basis of call id.
 */

/**
 * Will be removed when integrated with the RoutingServiceSBB  
 */
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
@ALCMLActionClass( name="MidCall Service")
public class MidCallAction extends BaseALCAction implements SBBEventListener{
	
	private static final Logger logger = Logger.getLogger(MidCallAction.class);
	private static final String NAME = "MidCallAction".intern();
	
	private static final short OP_HOLD = 1;
	private static final short OP_RESYNC = 2;
	private static final short OP_DISCONNECT = 3;
	private static final short OP_DISCONNECT_A = 4;
	private static final short OP_DISCONNECT_B = 5;
	private static final String PARTY_ON_HOLD_A = "PARTY_ON_HOLD_A";
	private static final String PARTY_ON_HOLD_B = "PARTY_ON_HOLD_B";
        private static final String  RECEIVED_FROM = "RECEIVED_FROM".intern();
    //Added to take decision for CPA call flow for AT&T     
    private static final String ATT_CPA_CHECK = "ATT_CPA_CHECK";    
    //BUG id 9069 --added to identify midcall info disconnect to B (ATT Govt Project)
    private static final String ATT_MIDCALL_INFO_DISCONNECT_B = "MIDCALL_INFO_DISCONNECT_B"; 
    private static final String ORIG_INITIAL_REQUEST = "ORIG_INITIAL_REQUEST";
	private String prevSbbName;

	public String getServiceName(){
		return NAME;
	}

	@ALCMLActionMethod( name="hold", isAtomic=false, help="Puts the current call on-hold.\n", asStatic=false)
	public void hold(ServiceContext ctx ,@ALCMLMethodParameter(  name="leg", asAttribute=true, defaultValue="A",
									help="which leg to keep on hold. Values {A or B}\n")
			String leg) {
		
		if(leg.equals("A")) {
		   ctx.setAttribute(SBB.DIRECTION, SBB.DIRECTION_A_TO_B);
		}else if(leg.equals("B")){
			ctx.setAttribute(SBB.DIRECTION, SBB.DIRECTION_B_TO_A);	
	    }
		
		this.execute(ctx, OP_HOLD);
	}
	
	@ALCMLActionMethod( name="resync", isAtomic=false, help="Resync the SDP.\n", asStatic=false)
	public void resync(ServiceContext ctx) {
	    this.execute(ctx, OP_RESYNC);
	}

	@ALCMLActionMethod( name="disconnect", isAtomic=false, help="Disconnect both incoming and outgoing legs.\n", asStatic=false)
	public void disconnect(ServiceContext ctx) {
		this.execute(ctx, OP_DISCONNECT);
	}
	
	@ALCMLActionMethod( name="disconnectA", isAtomic=false, help="Disconnect incoming leg.\n", asStatic=false)
	public void disconnectA(ServiceContext ctx) {
		this.execute(ctx, OP_DISCONNECT_A);
	}
	
	@ALCMLActionMethod( name="disconnectB", isAtomic=false, help="Disconnect outgoing leg.\n", asStatic=false)
	public void disconnectB(ServiceContext ctx) {
		this.execute(ctx, OP_DISCONNECT_B);
	}
	
	public void execute(ServiceContext ctx, short opCode) {
		
		try{
			this.ctx = ctx;
			
			ServletContext servletCtx = (ServletContext)
                                ctx.getAttribute(SipServiceContextProvider.Context);
			SipApplicationSession appSession = (SipApplicationSession)
                                ctx.getAttribute(SipServiceContextProvider.Session);
			SipServletRequest request = (SipServletRequest) 
								ctx.getAttribute(SipServiceContextProvider.InitialRequest); //reeta made  it initial(orig)
			//adding this to check condition for ATT Govt Project CPA flow
			// If thsi value is false then normal behaviour will be executed otherwise CPA call flow (For ATT GOvt Project)
			String origCallID=(String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
			String attCPACheck  = "false";
			if(opCode == OP_RESYNC){
			
				if(ctx.getAttribute(ATT_CPA_CHECK) != null && ctx.getAttribute(ATT_CPA_CHECK).toString().toLowerCase().equals(
				"true")){
					attCPACheck = ((String)ctx.getAttribute(ATT_CPA_CHECK)).toLowerCase();
				}
				if(logger.isDebugEnabled()){
					logger.debug("[CALL-ID]"+origCallID+"[CALL-ID] "+"attCPACheck value is ********::" + attCPACheck);
				}
				if(attCPACheck.equalsIgnoreCase("true")){
					if(appSession.getAttribute(ORIG_INITIAL_REQUEST) == null)
						appSession.setAttribute(ORIG_INITIAL_REQUEST, request);
					if(logger.isDebugEnabled()){
						logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"resuest set is ::::" + (SipServletRequest)appSession.getAttribute(ORIG_INITIAL_REQUEST));
					}
				}	
			}
			if(request == null){
				ctx.ActionFailed("NO original request");
				return;
			}
			
			//Remove the session from the currently associated SBB.... 
			SipSession partyA = null, partyB = null;
			SBB prevSbb =null;
//			prevSbbName = (String) request.getSession().getAttribute(Constants.ATTR_SBB);
//			prevSbbName = prevSbbName == null ? "" : prevSbbName;
//			SBB prevSbb = (SBB)appSession.getAttribute(prevSbbName);
			
			/*
			 * commenting above 3 lines of code and changing code for getting previous SBB .Now previous SBB will be obtainied from Service context .
			 * As request are changing in various scenraio and we canot rely on it.
			 * Service context remains same in a call flow .so each time we does an operation on any of ALC extension. we will save the 
			 * SBB name used in the ServiceContext .So before doing any next action we know which sbb was used previously
			 * in case of Media Server SBB we are going it in mediaSBB.SetServiceContextProvider. this change has been made in routeCallAction
			 * MidCallaction and MediaServiceALCInterfaces.java classes
			 */
			
	         prevSbbName = (String) this.ctx.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
    		
    		if(prevSbbName != null){
    			
    			if(logger.isDebugEnabled())
    			logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+" Previous SBB from context is "+prevSbbName);
    			
    			Object obj = this.ctx.getAttribute(SipServiceContextProvider.Session);
    			
    			if(obj!=null){
    				 appSession=(SipApplicationSession)obj;
    			}
    			prevSbbName = prevSbbName == null ? "" : prevSbbName;
    			 prevSbb = (SBB)this.ctx.getAttribute(prevSbbName); //(SBB)appSession.getAttribute(prevSbbName);
    		}
			
			if(prevSbb != null){
				partyA =  prevSbb.getA();
				partyB =  prevSbb.getB();
			}
			if(logger.isDebugEnabled()){
				logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Prev SBB::" + prevSbb +" Session A:"+ partyA +" SessionB : "+partyB);
			}
			
			if((partyA == null || partyB == null ) 
					&& opCode!=OP_RESYNC 
					&& opCode!=OP_DISCONNECT_A 
					&& opCode!=OP_DISCONNECT_B
					&& opCode!=OP_DISCONNECT){ 
				ctx.ActionFailed("Not able to get session of A OR B");
				return;
			}
			
			/*
			 * Setting appsession as session of A/B on which operation will take place
			 *  always for doing any operation on midcall SBB
			 */
			
			if(partyA !=null)
				appSession =partyA.getApplicationSession();
			else if(partyB !=null )
				appSession =partyB.getApplicationSession();
			
			/*B2bSessionController b2bController = (B2bSessionController)
	        SBBFactory.instance().getSBB(B2bSessionController.class.getName(),
	                                "midCallSBB", appSession, servletCtx);*/
			B2bSessionController b2bController = null;
			SBBFactory sbbFactory = (SBBFactory)appSession.getAttribute("SBBFactory");
			if(sbbFactory != null) {
			  b2bController = (B2bSessionController)sbbFactory.getSBB(B2bSessionController.class.getName(), "midCallSBB",                appSession, servletCtx);
			}
			b2bController.setEventListener(this);
			
			/*
			 * Set the SBB attribute in Service context to know whihc SBB is currently used in this service context
			 */
			
			ctx.setAttribute(SBBOperationContext.ATTRIBUTE_SBB, "midCallSBB");
			ctx.setAttribute("midCallSBB", b2bController);
			
			if(partyA !=null){

				prevSbb.removeA();
				
				if(b2bController.getA() !=null)
				b2bController.removeA(); // adding for FT we need to removeB if it was already there in this b2bcontroller
				
				b2bController.addA(partyA);
				if (logger.isDebugEnabled()) {
					logger
							.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Party A::" + b2bController.getA() == null ? "NULL"
									: b2bController.getA().getId());
				}
			
			}
			
			if (partyB!=null){
			
				prevSbb.removeB();
				
				if(b2bController.getB()!=null)
				b2bController.removeB(); // added for FT we need to removeB if it was already there in this b2bcontroller
			
				b2bController.addB(partyB);

				if (logger.isDebugEnabled()) {
					logger
							.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Party B::" + b2bController.getB() == null ? "NULL"
									: b2bController.getB().getId());
				}
			}
			
			SipServletRequest origReq = (SipServletRequest)
					ctx.getAttribute(SipServiceContextProvider.InitialRequest);
					
			SipSession origSession =origReq.getSession();
			
			switch(opCode){
				case OP_HOLD:
					b2bController.setAttribute(SBB.DIRECTION, (String)ctx.getAttribute(SBB.DIRECTION));
					String direction =(String)ctx.getAttribute(SBB.DIRECTION);
					
					/*
					 * Reeta Setting which party on hold so that these can resynch later on using reshunhc method as parties are removed by different SBB
					 *  before proceeding their own actions so we need to preservce the holded parties so that they can be resynched later on
					 */
					if(direction.equals(SBB.DIRECTION_A_TO_B)){
						
						Object partyOnHoldAObj =ctx.getAttribute(PARTY_ON_HOLD_A);
						if(partyOnHoldAObj== null)
					        ctx.setAttribute(PARTY_ON_HOLD_A, b2bController.getA());
						else {
							
							if(((SipSession)partyOnHoldAObj).equals(b2bController.getA()))
								 ctx.setAttribute(PARTY_ON_HOLD_A, b2bController.getA());
							else
								ctx.setAttribute(PARTY_ON_HOLD_B, b2bController.getA());
							/*
							 * change has been made to incorporate both the jail flow and
							 *  low balance scenarios 
							 */
							// hold is on original A party again e.g low balance scenario
//							if(ctx.getAttribute(PARTY_ON_HOLD_B)!=null)//if (request.getSession().equals(b2bController.getA()))
//							 ctx.setAttribute(PARTY_ON_HOLD_A, b2bController.getA());
//							else // in case the party B was also connected as A e.g jail flow 
//					         ctx.setAttribute(PARTY_ON_HOLD_B, b2bController.getA());
						}
					
					}else if(direction.equals(SBB.DIRECTION_B_TO_A)){
						
						Object partyOnHoldBObj =ctx.getAttribute(PARTY_ON_HOLD_B);
					
						if(partyOnHoldBObj== null)
					        ctx.setAttribute(PARTY_ON_HOLD_B, b2bController.getB());
						else {// in case the party A was also connected as B e.g jail flow 
							
							/*
							 * change has been made to incorporate both the jail flow and
							 *  low balance scenarios 
							 */
							if(((SipSession)partyOnHoldBObj).equals(b2bController.getB()))
								 ctx.setAttribute(PARTY_ON_HOLD_B, b2bController.getB());
							else
								ctx.setAttribute(PARTY_ON_HOLD_A, b2bController.getB());
							
//							if(ctx.getAttribute(PARTY_ON_HOLD_A)!=null)//if (request.getSession().equals(b2bController.getA()))
//								 ctx.setAttribute(PARTY_ON_HOLD_B, b2bController.getB());
//							 else // in case the party B was also connected as A e.g jail flow 
//					             ctx.setAttribute(PARTY_ON_HOLD_A, b2bController.getB());
					        
						}
					}
					
					b2bController.hold();
					break;
				case OP_RESYNC:
					
					/*
					 * As per above decription in hold we need to resynch only holded parties e.g in case of low balance announcemnet
					 * in which firstly party B is put on hold and announm=cemnet is played to A and then A is kept on hold to disconnect media server
					 * and then party b and A has to Resynch for which we need the two parties which were kept on hold
					 */
					Object sessionA = ctx.getAttribute(PARTY_ON_HOLD_A);
					Object sessionB = ctx.getAttribute(PARTY_ON_HOLD_B);
					//This code is added to put  initial A party in sbb because after we need to party to resync but only one party is avialable from previuos sbb ()
					if(attCPACheck.equalsIgnoreCase("true")){
						 if(logger.isDebugEnabled()){
								logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Now going to check the session A and B ");
								logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Session A is ::::" + sessionA);
								logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Session B is :::" +sessionB );
							}
							if(sessionB == null) {
								if(logger.isDebugEnabled()){
									logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Session B is null so adding the session of intialrequest ");
								}
								sessionB = request.getSession();
								b2bController.addB((SipSession)sessionB);
								ctx.setAttribute(PARTY_ON_HOLD_B,null);
							}
					}
					
					if(ctx.getAttribute(PARTY_ON_HOLD_A)!=null){
						
						if(b2bController.getA()!=null)
					       b2bController.removeA();
						
					   b2bController.addA((SipSession)sessionA);
					   ctx.setAttribute(PARTY_ON_HOLD_A,null);
					}
					
					if(attCPACheck.equalsIgnoreCase("false")){
					 if(ctx.getAttribute(PARTY_ON_HOLD_B)!=null){
						 
						 if(b2bController.getB()!=null)
					       b2bController.removeB();
						 
					     b2bController.addB((SipSession)sessionB);
					     ctx.setAttribute(PARTY_ON_HOLD_B,null);
					 }
					} 
					 /*
					  * We need to check the parties we are resynching belongs to same appsession or not .if not then we need to set
					  * SBB name in appsessions of both the sessions so that when the message is recieved from any of sipsessions
					  * pointing to diff appsessions get the same b2bcontroller e.g jailflow because here we donot know which appsession will be used to create 
					  * this b2b sbb so we are blindly setting on both .On one of these it will be already set .so it will get reset 
					  */
					 SipSession sessA =b2bController.getA();
					 SipSession sessB =b2bController.getB();
					 
					 if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Resynching parties : " +sessA + " " +sessB + " ");
						}
					 
					 if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Resynching parties app session : " +sessA.getApplicationSession() + " " +sessB.getApplicationSession() + " ");
						}
					 
					 if((sessA!=null && sessB!=null)
							 &&(sessA.getApplicationSession().getId() != sessB.getApplicationSession().getId())){
						
					  if(logger.isDebugEnabled()){
					   logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"setting current SBB on both the App Sessions: " +sessA.getApplicationSession() + " " +sessB.getApplicationSession());
					  }
						 
						 String b2bname= b2bController.getName();
						 sessA.getApplicationSession().setAttribute(b2bname, b2bController);
						 sessB.getApplicationSession().setAttribute(b2bname, b2bController);
						 
						 if(logger.isDebugEnabled()){
								logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Also set Listener class attribute on other party appsession from which sbb is not created as it will not be set in that");
							}
						 /**
						  * Writing below code because when sbb.setEventListener is called it on a sbb object is sets this attribute in its appsession
						  * but here we can have 2 different app session for A and B for ATT CPA flow kind of scenario but as sbb is created from one session only
						  * so other session also need to have this attribute because of FT in sessiondidActivate the listener object is created from this attribute only
						  */
						 if(!sessA.getApplicationSession().equals(b2bController.getApplicationSession())){	 
							 sessA.getApplicationSession().setAttribute(b2bname + com.baypackets.ase.sbb.util.Constants.SBB_LISTENER_CLASS, 
									 b2bController.getEventListener().getClass().getName());
						 }else  if(!sessB.getApplicationSession().equals(b2bController.getApplicationSession())){
							 sessB.getApplicationSession().setAttribute(b2bname + com.baypackets.ase.sbb.util.Constants.SBB_LISTENER_CLASS, 
									 b2bController.getEventListener().getClass().getName());
						 }
						
						 
					 }else{
						 
						 if(logger.isDebugEnabled()){
							 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"not setting sbb on resynching parties");
						  }
							
					 }
						
					  /*
					   * Chagning the appsession in b2bcontroller as appsession of A as it may be picked up as appsession between B->ivr 
					   *  as further flow will be  between A and ivr so we need to set it. 
					   */
					 if(attCPACheck.equalsIgnoreCase("true")){
						 if(logger.isDebugEnabled()){
							 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"setting check inapp session");
						 }
						 appSession.setAttribute(ATT_CPA_CHECK,attCPACheck);
					  }
					  b2bController.setApplicationSession(sessA.getApplicationSession());
					  b2bController.resync();
					break;
				case OP_DISCONNECT:
								
								/* bug 6142 for jail flow
								 * This will be the case of FT on ringing in jail flow .then A and B both will be null
								 * when jail flow will call disconnect-all on receiving bye from A so we need to
								 * call action completed so that app can call cleanup after this also we can not say it failed
								 * as this is valid scenario
								 */
								if(partyA==null && partyB==null){
									ctx.setAttribute("DISCONNECTED_FROM", "both");
									ctx.ActionCompleted(Constants.ATTR_OKAY);
								}
								/*
								 * handling bye from A in case of jail flow when call is between ive and B party 
								 */
								
								SipSession.State dialogState = origSession.getState();
									//((Integer)origSession.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
								
								boolean isdisconnectOnByeFromB=false;
								if("B".equals(ctx.getAttribute(RECEIVED_FROM))){
									isdisconnectOnByeFromB=true;
								}
								
								/*
								 * if BYE is received from A which is  orig party and the party
								 * from previous SBB is also A then we need to disconnect only B
								 */
						if(partyA!=null && origSession !=null && partyA.equals(origSession)){
									
									/*
									 * Disconnect A as well as A has also not been disconnected both i mean
									 */
									if (dialogState != SipSession.State.TERMINATED
											&& partyB != null
											&& partyB.getState() != SipSession.State.TERMINATED) {// < Constants.STATE_TERMINATED){
												
										if(logger.isDebugEnabled()){
											logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Disconnecting both A and B parties");
										}
										b2bController.disconnect();
								     }else if( partyB !=null&& partyB.getState() != SipSession.State.TERMINATED){
										
										if(logger.isDebugEnabled()){
											logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Disconnecting only partY B as A is already in Terminated state");
										}
										b2bController.disconnectB();
									}if (dialogState != SipSession.State.TERMINATED) {// < Constants.STATE_TERMINATED){
											
										if(logger.isDebugEnabled()){
											logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Disconnecting only partY A as B is already in Terminated state");
										}
										b2bController.disconnectA();
								     }
							} else {
			
								// case here A has sent bye when B was on IVR then anyways
								// we need to disconnect all existing parties
			
								
								 
								if (partyA != null && partyB != null) {
									if (logger.isDebugEnabled()) {
										logger.debug("[CALL-ID]"
												+ origCallID
												+ "[CALL-ID] "
												+ "Disconnecting both the parties of previous SBB as it seems to be 3 party call scenario like jail flow");
									}
			
									if (partyA.getState() != SipSession.State.TERMINATED
											&& partyB.getState() != SipSession.State.TERMINATED) {
			
										if (logger.isDebugEnabled()) {
											logger.debug("[CALL-ID]" + origCallID
													+ "[CALL-ID] "
													+ "Disconnecting both the parties");
										}
										b2bController.disconnect();
									} else if (partyA.getState() != SipSession.State.TERMINATED) {
			
										if (logger.isDebugEnabled()) {
											logger.debug("[CALL-ID]" + origCallID
													+ "[CALL-ID] "
													+ "Disconnecting A party in this SBB");
										}
										b2bController.disconnectA();
									} else if (partyB.getState() != SipSession.State.TERMINATED) {
			
										if (logger.isDebugEnabled()) {
											logger.debug("[CALL-ID]" + origCallID
													+ "[CALL-ID] "
													+ "Disconnecting B party in this SBB");
										}
										b2bController.disconnectB();
									}
			
								} else if (partyA != null ) {
									if ((origSession.getState() != SipSession.State.TERMINATED)) {//isdisconnectOnByeFromB||
										if (logger.isDebugEnabled()) {
											logger.debug("[CALL-ID]"
													+ origCallID
													+ "[CALL-ID] "
													+ "Disconncting partY A as B party seems already disconnected");
			
										}
										origSession.getApplicationSession().setAttribute(
												b2bController.getName(), b2bController);
										b2bController.removeA();
										b2bController.addA(origSession);
										b2bController.disconnectA();
									} else if (b2bController.getA()!=null && b2bController.getA().getState() != SipSession.State.TERMINATED){
			
										b2bController.addB(b2bController.removeA());
										b2bController.disconnectB();
										if (logger.isDebugEnabled()) {
											logger.debug("[CALL-ID]"
													+ origCallID
													+ "[CALL-ID] "
													+ "Disconncting partY A of previous SBB( party b is also disconnected) as it seems to be 3 party call scenario like jail flow");
			
										}
			
									}
			
								} else if (partyB != null) {
			
									if (logger.isDebugEnabled()) {
										logger.debug("[CALL-ID]" + origCallID
												+ "[CALL-ID] " + "Disconnecting partY B ");
									}
									b2bController.disconnectB();
								}
							}
					
					ctx.setAttribute("DISCONNECTED_FROM", "both");
					break;
				case OP_DISCONNECT_A:

					 origSession.setAttribute(com.baypackets.ase.sbb.util.Constants.ATTRIBUTE_INIT_REQUEST,origReq);
					 origSession.getApplicationSession().setAttribute(b2bController.getName(), b2bController);
					 
					if (b2bController.getA() != null) {
	
						if (!origSession.equals(b2bController.getA())&& (origSession.getState() != SipSession.State.TERMINATED)) {
							if (logger.isDebugEnabled())
								logger.debug("[CALL-ID]" + origCallID
										+ "[CALL-ID] "
										+ " add original A partyto disconnect ");
							b2bController.removeA();
							b2bController.addA(origSession);
						}
						b2bController.disconnectA();
						ctx.setAttribute("DISCONNECTED_FROM", "A");
					} else if (origReq != null && (origSession.getState() != SipSession.State.TERMINATED)) {
						if (logger.isDebugEnabled())
							logger.debug("[CALL-ID]"
									+ origCallID
									+ "[CALL-ID] "
									+ " disconnect original A party as there is no A party in this sbb ");
	
						b2bController.addA(origSession);
						b2bController.disconnectA();
						ctx.setAttribute("DISCONNECTED_FROM", "A");
					} else {
						if (logger.isDebugEnabled())
							logger.debug("[CALL-ID]"
									+ origCallID
									+ "[CALL-ID] "
									+ " Can not disconnect A party as there is no session A in this SBB");
						ctx.ActionCompleted(Constants.ATTR_FAILED);
					}
					break;
				case OP_DISCONNECT_B:

					/*The check is added on the basis of flag added during service definition
					 * specifically for disconnect scenario for early state of B. after disconnect (recieving 487)
					 * if disconect is done with b2bcontrller Mid call sbb then the disconnect event is not delivered
					 * since this has no One way dialout operation added during connect to B . Disconnect event will be generated by SAS(in case when B is in early state )when both the connect and disconnect are done by same SBB.--Bug id 9069
					 * */ 
					String MidCallInfoDiscB = (String)ctx.getAttribute(ATT_MIDCALL_INFO_DISCONNECT_B);
					if(logger.isDebugEnabled()){
						logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+" MidCallInfoDiscB value is ::" + MidCallInfoDiscB);
					}
					if(MidCallInfoDiscB != null && MidCallInfoDiscB.equalsIgnoreCase("TRUE")){
						//prevSbb.setAttribute(ATT_MIDCALL_INFO_DISCONNECT_B, MidCallInfoDiscB);
						if(partyB != null){
							if(logger.isDebugEnabled()){
								logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"MidCallInfoDiscB value is "+MidCallInfoDiscB+" PartyB session is "+partyB+" adding back to prev sbb");
							}
							prevSbb.addB(partyB);
						
						if(partyA != null){
							if(logger.isDebugEnabled()){
								logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"MidCallInfoDiscB value is "+MidCallInfoDiscB+" PartyA session is "+partyA+" adding back to prev sbb");
							}
							prevSbb.addA(partyA);
						}
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+" Setting event listener "+this+" to prev sbb "+prevSbb);
						}
							prevSbb.setEventListener(this);
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+" Disconnecting B Using prev sbb "+prevSbb);
						}
							((B2bSessionController)prevSbb).disconnectB();
							ctx.setAttribute("DISCONNECTED_FROM", "B");
						}//End partB null check
					}else {
 
					if (b2bController.getA()!=null &&b2bController.getA() != origSession) {					
										
						if (b2bController.getB() == null) {
							if (logger.isDebugEnabled())
								logger.debug("[CALL-ID]"
										+ origCallID
										+ "[CALL-ID] "
										+ "disconnecting  B party which is A (B-->IVR probably) party looks like ivr was not connected yet !!");
							b2bController.addB(b2bController.removeA());
							b2bController.disconnectB();
						} else {
							if (logger.isDebugEnabled())
								logger.debug("[CALL-ID]"
										+ origCallID
										+ "[CALL-ID] "
										+ "disconnecting  B party which is A (B-->IVR probably) party of current SBB ");
							b2bController.disconnectA();
						}
						ctx.setAttribute("DISCONNECTED_FROM", "B");
						
					} else if (b2bController.getB() != null && (b2bController.getB().getState() != SipSession.State.TERMINATED)) {
						b2bController.disconnectB();
						ctx.setAttribute("DISCONNECTED_FROM", "B");
					} else {
						if (logger.isDebugEnabled())
							logger.debug("[CALL-ID]"
									+ origCallID
									+ "[CALL-ID] "
									+ " Can not disconnect B party as there is no session B in this SBB");
						ctx.ActionCompleted(Constants.ATTR_FAILED);
					}
				}//End IF MidcallInfoDiscB check
					break;
			}
		}catch(Exception e){
			logger.error("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+e.getMessage(), e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"After calling execute..");
		}
	}
	
	public void activate(SBB sbb) {
	}

	public int handleEvent(SBB sbb, SBBEvent event) {
		
		String eventId = event.getEventId();
		Object ctxtObj=sbb.getApplicationSession().getAttribute(SipServiceContextProvider.SERVICE_CONTEXT);
		String origCallID = null;
		if(ctxtObj != null){
			origCallID  = (String)((ServiceContext)ctxtObj).getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
		}
		if(logger.isDebugEnabled()){
			logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"handleEvent() received event ==> " + eventId);
			logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Session A ID :" + (sbb.getA()== null ? "NULL" : sbb.getA().getId() + "AppSession A :"+ sbb.getA().getApplicationSession()));
			logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Session B ID :" + (sbb.getB()== null ? "NULL" : sbb.getB().getId()+ " AppSession B :"+ sbb.getB().getApplicationSession()));
			logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Event Session ID :" + (event.getMessage() == null ? "NULL" : event.getMessage().getSession().getId()));
		}
		
			
		 if (ctx == null) {
			if (ctxtObj != null) {
				/*
				 * taking care of FT
				 */
				if (logger.isDebugEnabled()) {
					logger
							.debug("Seems to be FT case :Service Context is found from AppSession & Servlet Context :"
									+ sbb.getServletContext());
				}
				ctx = (ServiceContext) ctxtObj;
				/*
				 * when BYE is received from A or B after FT . we need to keep same APP_SESSION attribute as
				 * of before FT i.e of between B-->IVR so saving session of B instead of getting it from sbb.
				 */
				
				ctx.setAttribute(SipServiceContextProvider.Context, sbb
						.getServletContext());
				
				if(ctx.getAttribute(SipServiceContextProvider.Session)==null){
				
					/*
					 * This method call is used for FT in jail flow kind of scenario
					 */
					updateSessionsAfterFT(ctx, sbb);
					
					if(ctx.getAttribute(SipServiceContextProvider.Session)==null)
						ctx.setAttribute(SipServiceContextProvider.Session, sbb.getApplicationSession());
				   
				}
				
				
			} else {
				if (logger.isDebugEnabled()) {
					logger
							.debug("Service Context is not found in AppSession so ca not procced further...");
				}
				return SBBEventListener.CONTINUE;

			}
		} else {
			if (ctx.getAttribute(SipServiceContextProvider.Context) == null)
				ctx.setAttribute(SipServiceContextProvider.Context, sbb
						.getServletContext());
		}
		
		try{
	                 SipServletMessage msg =event.getMessage();
                                   ServiceContext sdContext =null; 
                                     if(eventId.equals(SBBEvent.EVENT_SIG_IN_PROGRESS)){
	                                
	                                /*
	                                 *  handling mid call requests like INFO ,UPDATE
	                                 */
	                                
	                                if(logger.isDebugEnabled()){
	                                        logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"SIG in progress is here hey you ..."+msg.getMethod());
	                                }
	                                
	                                /*
	                				 *  Reeta bug id 6149 |Refresh/Hold/UNHOLD invite handling |starts
	                				 *  checks if the request is a refresh/HOLD invite message.then returns continue and let network message handler to handle it
	                				 */
	                				
	                				if(msg instanceof SipServletRequest) {
	                					SipServletRequest reqst = (SipServletRequest) msg;	
	                					
	                					if(msg.getMethod().equalsIgnoreCase(com.baypackets.ase.sbb.util.Constants.METHOD_INVITE))
	                					{
	                						
	                						if(SBBResponseUtil.isRefreshInvite(reqst)){
	                						logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Refresh INVITE >>>> so returning continue");
	                						
	                						return SBBEventListener.CONTINUE;
	                						
	                						}
	                						
	                					    if(SBBResponseUtil.isHoldInvite(reqst)){
	                						logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"HOLD INVITE >>>> so returning continue");
	                						
	                						return SBBEventListener.CONTINUE;
	                						
	                						}
	                					    
	                					    logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"it seems to be un-hold INVITE Request>>>> so returning continue");  
	                						return SBBEventListener.CONTINUE;
	                					}
	                				}
	                				
	                				/*
	                				 * Reeta bug id 6149 |Refresh/hold invite handling |End
	                				 */
	                                
	                                ServiceDefinition sd = ServiceDefinition.getServiceDefinition( ctx.getNameSpace(), "do-" + msg.getMethod().toLowerCase());
	                                if (sd == null)
	                                {
	                                        return SBBEventListener.CONTINUE;
	                                }
	                                else
	                                {
	                                        
	                                	/**
	                					 * sb itself handler 200 ok for bye we need not to send it here
	                					 */
	                					if(msg instanceof SipServletRequest && !msg.getMethod().equalsIgnoreCase(com.baypackets.ase.sbb.util.Constants.METHOD_BYE)) {
										SipServletRequest req = (SipServletRequest) msg;
										req.createResponse(200).send();
				
									}
	                                        
	                                        //= (ServiceContext)msg.getApplicationSession().getAttribute(ALCServiceContext);
									if (ctx == null) {
				
									if (logger.isDebugEnabled()) {
											logger.debug("[CALL-ID]" + origCallID
													+ "[CALL-ID] "
													+ "SIG in progress context is null :)");
										}
										sdContext = new ServiceContext();
				
									} else {
										if (logger.isDebugEnabled()) {
				
											logger.debug("[CALL-ID]" + origCallID
													+ "[CALL-ID] "
													+ "SIG in progress context not null :)");
										}
										sdContext = ctx;
									}

	                                if(logger.isDebugEnabled()){
	                                        logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"SIG in progress lets execute the service context ...");
	                                }
	                                
	                                ServletContext servletCtx = (ServletContext)ctx.getAttribute(SipServiceContextProvider.Context);
	                                if(servletCtx ==null)
	                					servletCtx =sbb.getServletContext();
	                                
	                                SipServiceContextProvider sscp = new SipServiceContextProvider(servletCtx, msg.getApplicationSession(), msg, sdContext);
	                                sdContext.addServiceContextProvider(sscp);
	                                if(sdContext !=null){
	                                	
	                                	SipServletRequest  origReq = (SipServletRequest)
	                							ctx.getAttribute(SipServiceContextProvider.InitialRequest);
//	                                	if (msg.getSession().equals(sbb.getA())) {
//	                						if (msg.getSession().equals(origReq.getSession())) {
//	                							sdContext.setAttribute(RECEIVED_FROM, "A");
//	                						}else{
//	                							sdContext.setAttribute(RECEIVED_FROM, "B");
//	                						}
//	                					} else if (msg.getSession().equals(sbb.getB())) {
	                						if (msg.getSession().equals(origReq.getSession())) {
	                							sdContext.setAttribute(RECEIVED_FROM, "A");
	                						}else{
	                							sdContext.setAttribute(RECEIVED_FROM, "B");
	                						}
//	                					} else {
//	                						sdContext.setAttribute(RECEIVED_FROM, "UNKNOWN");
//	                					}
	                              }
	  
	                                sd.execute(sdContext);
	                                return SBBEventListener.NOOP;

	                                }
	                         } 	

               	if(eventId.equals(SBBEvent.EVENT_HOLD_COMPLETE)	||
					eventId.equals(SBBEvent.EVENT_RESYNC_COMPLETED)||
					eventId.equals(SBBEvent.EVENT_HOLD_FAILED) ||
					eventId.equals(SBBEvent.EVENT_RESYNC_FAILED)){
		             
				if(ctx != null){
					ctx.ActionCompleted(Constants.ATTR_OKAY);
				}
			}
            //Adding Condition to check whether this is midcallinfo disconnect or not 
            //Specific to ATT Govt Project -- BUG 9069   	
			String MidCallInfoDiscB = (String) ctx
					.getAttribute(ATT_MIDCALL_INFO_DISCONNECT_B);
			if (logger.isDebugEnabled()) {
				logger.debug("[CALL-ID]" + origCallID + "[CALL-ID] "
						+ " MidCallInfoDiscB value is ::" + MidCallInfoDiscB);
			}
			if(eventId.equals(SBBEvent.EVENT_DISCONNECTED) ||
					eventId.equals(SBBEvent.EVENT_DISCONNECT_FAILED)){
				if(ctx != null){
					//BUG 9069 -- if Disconnect event recieved during midcall info scenario then no need to execute disconnect handler
					//just continue with next action of service.
					//else normal behaviour as previous. 
					if (MidCallInfoDiscB != null
							&& MidCallInfoDiscB.equalsIgnoreCase("TRUE")) {
						if(logger.isDebugEnabled()){
							logger.debug("[CALL-ID]" + origCallID + "[CALL-ID] "+ "DISCONNECT event recieved during Midcall info disconnect. So Continuing with next action");
						}
						ctx.ActionCompleted(Constants.ATTR_OKAY);
					}
					else{
						String disconnect_handler = (String)ctx.getAttribute(Constants.ATTR_ROUTING_Disconnect_Handler);
						if (disconnect_handler != null)
						{
	
								ServiceDefinition sd = ServiceDefinition.getServiceDefinition(ctx.getNameSpace(), disconnect_handler);
								if (sd != null)
								{
									sdContext = ctx ;
									sd.execute(sdContext);
									return SBBEventListener.NOOP;
							//	}
							}
						}else{
							ctx.ActionCompleted(Constants.ATTR_OKAY);//reeta added it for continuation in case on disconnect handler is not there
						}
					}
				}
			}
			//BUF 9069 --CHANGES are made for midcall info feature in ATT Project
			//Here During Mid call info..we disconnet B and reconnects IVR To B.
			//SO in case B is in EArly state during connect process .. and midcall info is recieved 
			//then we disconnect B (SBB sends CANCEL to B).. so on 487 for Invite onewaydialout handler sends 
			//Connect failed events ...we need to continue our service on this event.
			if (MidCallInfoDiscB != null
					&& MidCallInfoDiscB.equalsIgnoreCase("TRUE")) {
				if (eventId.equals(SBBEvent.EVENT_CONNECT_FAILED)) {
					if (ctx != null) {
						if (logger.isDebugEnabled()) {
							logger
									.debug("[CALL-ID]"
											+ origCallID
											+ "[CALL-ID] "
											+ "CONNECT FAILED envent recieved by MIDCALL ACTION...So continuing with next action");
						}
						ctx.ActionCompleted(Constants.ATTR_OKAY);
					}
				}
			}                   
		}catch(Exception e){
			logger.error("[CALL-ID]"+origCallID +"[CALL-ID] "+e.getMessage(), e);
		}
		return SBBEventListener.CONTINUE;
	}
	
	
//	 private void  validateAppSession(SBB sbb ,SipApplicationSession preSBBAppSession,ServiceContext sContext){
//		 
//		 if(sbb.getA()!= null && preSBBAppSession!=null &&!sbb.getA().getApplicationSession().equals(preSBBAppSession)){
//			 
//			 if(logger.isDebugEnabled()){
//                 logger.debug("[CALL-ID]"+ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"Current SBB and previous SBB are associated with diff AppSessions");
//              }
//			 sbb.setApplicationSession(preSBBAppSession);
//			 sbb.getApplicationSession().setAttribute(SBBOperationContext.ATTRIBUTE_SBB, sbb);
//			 sContext.setAttribute(SipServiceContextProvider.Session, sbb.getA().getApplicationSession());
//
//		 }else{
//			 if(logger.isDebugEnabled()){
//                 logger.debug("Current SBB and previous SBB are associated with same AppSessions " +sbb.getApplicationSession()+" Prev SBB Session : "+preSBBAppSession);
//              }
//		 }
//	}
	 
	 
	       /**
	        * Update Prev SBB and APP_SESSION in jail flow after FT .we need to get APP_SESSION attribute
	        * as app session of B--IVR and prev SBB with sip sessions of A and B 
	        */
	       private void updateSessionsAfterFT(ServiceContext ctx ,SBB sbb){
	    	   
	    	 
	    	   String origCallID = (String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
	    	   if(logger.isDebugEnabled())
          		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Entering updateAppSessionInJailflow() " +sbb);
	    	   
	    	   boolean recvFromA=false;
	    	   boolean recvFromB=false;
	    	   
	    	   SipServletRequest ssr=(SipServletRequest)ctx.getAttribute(SipServiceContextProvider.InitialRequest);
	    	   
	    	   String ORIG_APP_SESSION_ID =ssr.getApplicationSession().getId();
	    	 
	    	   String appSessionID=(String) ctx.getAttribute(SipServiceContextProvider.SessionID);
	    	      	  
	    	   /*
	    	    * Not jail flow BYE as current app session ID is same as orig . in jail flow we update appsession id with of B-IVR
	    	    * after routing call to B--IVR
	    	    */
	    	   if(ORIG_APP_SESSION_ID!=null && appSessionID!=null && ORIG_APP_SESSION_ID.equals(appSessionID)){
	    		   
	    		   /*
	    		    * if orig sessionID  is same as current app session id then its not jail flow as we have updated
	    		    * appsession id of B as attribute in context . so its normal call and we need not to update app session
	    		    * and last SBB object
	    		    */
	    		   if(logger.isDebugEnabled())
	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"BYE Received  is not in jail flow so returning without updating any data.");
	    		   return ;
	    		   
	    	   }
	    	   
               SipSession AfromSBB= sbb.getA();
	    	   
	    	   if(AfromSBB==null)
	    	    AfromSBB = getSessionofBAfterFT(appSessionID, ctx); //sbb.getA();
	    	   
	    	   String appSessionofSBB =AfromSBB.getApplicationSession().getId();
	    	   
	    	   
	    	   /* BYE Received from A in jail flow
	    	    * in jail flow bye received from A as its app session isa same as orig
	    	    * but the latest appsession is not same as orig . so its bye from a in jail flow
	    	    */
	    	   if(appSessionofSBB!=null && appSessionID!=null && appSessionofSBB.equals(ORIG_APP_SESSION_ID)){
	    		   
	    		   if(ORIG_APP_SESSION_ID!=null && appSessionID!=null && !ORIG_APP_SESSION_ID.equals(appSessionID)){
	    			   
	    			   recvFromA=true;
	    			   if(logger.isDebugEnabled())
		            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"BYE Received  from A in jail flow.");
	    			   
	    		   }
	    		   
	    	   }  	   
	    	   
	    	   /* BYE Received from B in jail flow
	    	    * in jail flow bye received from B as its app session is not same as orig
	    	    * and the latest appsession is  same as of B . so its bye from B in jail flow
	    	    */
	    	   if(appSessionofSBB!=null && appSessionID!=null && !appSessionofSBB.equals(ORIG_APP_SESSION_ID)){
	    		   
	    		   if(appSessionofSBB!=null && appSessionID!=null && appSessionofSBB.equals(appSessionID)){
	    			   
	    			   if(logger.isDebugEnabled())
		            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"BYE Received  from B in jail flow.");
	    			   recvFromB=true;
	    			   
	    		   }
	    		   
	    	   }    	   
	    	                
	             /*
	              * We need to update current SBB with sip session of A and B and need to update this updated current SBB in ServiceContext
	              * need to update APP_SESSION attribute in ServiceContext
	              * 
	              */
	             if(recvFromA){
	            	 
	            	 if(logger.isDebugEnabled())
	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"updating data when BYE Recived from A in jail flow.");
	            	 
	            	 
	            	 SipSession sessionBIVR =null;
	            	 SipSession  sessionB=null;
	            	 
	            	  if(sbb.getA()!=null)
	            		  sessionBIVR= sbb.removeA();
	            	  
	            	  if(sbb.getB()!=null)
	            		 sessionB = sbb.removeB();
	            		 
	            		 sbb.addA(AfromSBB);
	            		 
	            		 sessionBIVR= getSessionofBAfterFT(appSessionID, ctx);
	            		 
	            		 if(sessionBIVR!=null)
	            		 sbb.addB(sessionBIVR);		 
	            		 
	            	 /*
	            	  * updating new SBB in both the appsessions as well
	            	  */
	            	 AfromSBB.getApplicationSession().setAttribute("midCallSBB", sbb);
	            	 
	            	 if(sessionBIVR!=null)
	            	 sessionBIVR.getApplicationSession().setAttribute("midCallSBB", sbb);
	            	 
	            	 /*
	            	  * updating new SBB in context
	            	  */
	            	 ctx.setAttribute("midCallSBB", sbb);
	            	 
	            	 if(sessionBIVR!=null)
	            	     ctx.setAttribute(SipServiceContextProvider.Session, sessionBIVR.getApplicationSession());
	            	 
	             }
	             
	             
	             /*
	              * We need to update current SBB with sip session of A and B and need to update this updated current SBB in ServiceContext
	              * need to update APP_SESSION attribute in ServiceContext
	              *  
	              */
	             if(recvFromB){
	            	 
	            	 if(logger.isDebugEnabled())
	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"updating data when BYE Recived from B in jail flow.");
	            	 
	            	 
	            	 if(sbb!=null){
		            	 
		            	 if(logger.isDebugEnabled())
		            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Got  midCallSBB from AppSession B-->IVR: " + sbb);
		            	 
		            	 
		            	 SipSession sessionA =null;
		            	 SipSession  sessionB=null;
		            	 
		            	  if(sbb.getA()!=null)
		            	   sessionA= sbb.removeA();
		            	  
		            	  if(sbb.getB()!=null)
		            		 sessionB = sbb.removeB();
		            		
		            	  
		            	  if(logger.isDebugEnabled())
			            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Got Sip session from prev midCallSBB: A " + sessionA +" B: "+sessionB);
		            	 
		            		 
	            		 sbb.addA(ssr.getSession());
	            		 sbb.addB(AfromSBB);	// this is sessionBIVR	 
	            		 
	            	 }	            	 
	           	   
	            	 /*
	            	  * updating new SBB in both the appsessions as well
	            	  */
	            	 AfromSBB.getApplicationSession().setAttribute("midCallSBB", sbb);
	            	 ssr.getApplicationSession().setAttribute("midCallSBB", sbb);
	            	 
	            	 /*
	            	  * updating new SBB in context
	            	  */
	            	 ctx.setAttribute("midCallSBB",sbb);
	            	 ctx.setAttribute(SipServiceContextProvider.Session, AfromSBB.getApplicationSession());
	             }
	             
	             if(logger.isDebugEnabled())
	          		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Leaving updateAppSessionInJailflow()");
	             
	        }
	       
	       /**
	        * we need to get B session from SBB of B-->IVR in jail flow kind of scenarios
	        * @param appSessionID
	        * @param ctx
	        * @return
	        */
	       private SipSession getSessionofBAfterFT(String appSessionID ,ServiceContext ctx ){
        
	    	   ServletContext sCtxt=(ServletContext)ctx.getAttribute(SipServiceContextProvider.Context);
	    	   String origCallID = (String)ctx.getAttribute(SipServiceContextProvider.ORIG_CALL_ID);
	    	   Map sessionMap = (Map)sCtxt.getAttribute(APP_SESSION_MAP); 
               
	            if (sessionMap == null) {
	            	
	            	 if(logger.isDebugEnabled())
	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Application session map is not available in ServletContext.");
	            } else {
	            	 if(logger.isDebugEnabled())
	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Got application session map from ServletContext.");
	            }
	                 
	            
	            if (appSessionID == null) {
	            	 if(logger.isDebugEnabled())
	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"\"appSessionID\"  is null.");
	            } 
	            
	            SipApplicationSession appSession = sessionMap != null ? (SipApplicationSession)sessionMap.get(appSessionID) : null;
	            
	           
	             SipSession sessionA=null;
	             SipSession sessionBIVR =null;
	             Object ob=null;
	             
	            if (appSession == null) {
	            	 if(logger.isDebugEnabled())
	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"No application session found for ID: " + appSessionID);
	            } else {
	            	 if(logger.isDebugEnabled())
	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Got application session for ID: " + appSessionID);
	            	 
	            	 SBB prevSBBInContext=null;
	 	            
	 	             ob=sCtxt.getAttribute("midCallSBB");
	 	            if(ob!=null){
	 	            	 
	 	            	 if(logger.isDebugEnabled())
	 	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Got  midCallSBB from Context B-->IVR: " + ob);
	 	            	 
	 	            	  prevSBBInContext=(SBB)ob;
	 	            	 
	 	            	  if(prevSBBInContext.getA()!=null)
	 	            	  sessionA= prevSBBInContext.getA();
	 	            	  
	 	            	  if(prevSBBInContext.getB()!=null)
	 	            		 sessionBIVR = prevSBBInContext.getB();
	 	            	  
	 	            	  if(logger.isDebugEnabled())
	 		            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"Got Sip session from prev midCallSBB: A " + sessionA +" B: "+sessionBIVR);
	 	            	 
	 	             }
	            	 
	            	Iterator sipSessions=appSession.getSessions();
         			SipSession sipSession =null;
         			
         			while(sipSessions.hasNext()){
         				
         				sipSession =(SipSession)sipSessions.next();
         				if(sessionBIVR!=null && sipSession.getId().equals(sessionBIVR.getId())){
         					
         				}else{
         					 if(logger.isDebugEnabled())
         	            		 logger.debug("[CALL-ID]"+origCallID +"[CALL-ID] "+"found sip Session of B in jail flow."+ sipSession.getId());
         					sessionA=sipSession;
         					break;
         				}
         				
         			} 
	          }
	            
	            return sessionA;
	            
	            
	       }
		
		public static String APP_SESSION_MAP = "com.baypackets.ase.AppSessionMap";
		
}
