package com.agnity.simulator.handlers.factory;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.sipsubtype.ProvResNode;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.handlers.impl.AckHandler;
import com.agnity.simulator.handlers.impl.Bye2XXHandler;
import com.agnity.simulator.handlers.impl.ByeHandler;
import com.agnity.simulator.handlers.impl.CancelHandler;
import com.agnity.simulator.handlers.impl.CleanUpHandler;
import com.agnity.simulator.handlers.impl.ConHandler;
import com.agnity.simulator.handlers.impl.Default1XXHandler;
import com.agnity.simulator.handlers.impl.DfcHandler;
import com.agnity.simulator.handlers.impl.EncHandler;
import com.agnity.simulator.handlers.impl.EntityReleaseHanlder;
import com.agnity.simulator.handlers.impl.ErbHandler;
import com.agnity.simulator.handlers.impl.EtcHandler;
import com.agnity.simulator.handlers.impl.IdpHandler;
import com.agnity.simulator.handlers.impl.Info2XXHandler;
import com.agnity.simulator.handlers.impl.InfoHandler;
import com.agnity.simulator.handlers.impl.Invite2XXHandler;
import com.agnity.simulator.handlers.impl.InviteHandler;
import com.agnity.simulator.handlers.impl.Prack2XXHandler;
import com.agnity.simulator.handlers.impl.PrackHandler;
import com.agnity.simulator.handlers.impl.ReleaseCallHandler;
import com.agnity.simulator.handlers.impl.Response180Handler;
import com.agnity.simulator.handlers.impl.Response183Handler;
import com.agnity.simulator.handlers.impl.Response3XXHandler;
import com.agnity.simulator.handlers.impl.Response4XXHandler;
import com.agnity.simulator.handlers.impl.Response5XXHandler;
import com.agnity.simulator.handlers.impl.RnceHandler;
import com.agnity.simulator.handlers.impl.RrbeHandler;
import com.agnity.simulator.handlers.impl.SciHandler;
import com.agnity.simulator.handlers.impl.StartHandler;
import com.agnity.simulator.handlers.impl.TcEndHandler;
import com.agnity.simulator.handlers.impl.TcErrorHandler;
import com.agnity.simulator.handlers.impl.TcRejectHandler;
import com.agnity.simulator.handlers.impl.TimerHandler;
import com.agnity.simulator.handlers.impl.UAbortHandler;
import com.agnity.simulator.handlers.impl.Update2XXHandler;
import com.agnity.simulator.handlers.impl.UpdateHandler;
import com.agnity.simulator.handlers.impl.win.AnlyzdHandler;
import com.agnity.simulator.handlers.impl.win.AnlyzdResHandler;
import com.agnity.simulator.handlers.impl.win.CallControlDirHandler;
import com.agnity.simulator.handlers.impl.win.CallControlDirHandlerResp;
import com.agnity.simulator.handlers.impl.win.ConnResHandler;
import com.agnity.simulator.handlers.impl.win.InstructionHandler;
import com.agnity.simulator.handlers.impl.win.InstructionRespHandler;
import com.agnity.simulator.handlers.impl.win.OAnswerHandler;
import com.agnity.simulator.handlers.impl.win.OCalledPartyBusyHandler;
import com.agnity.simulator.handlers.impl.win.OCalledPartyBusyResHandler;
import com.agnity.simulator.handlers.impl.win.ODisconnectHandler;
import com.agnity.simulator.handlers.impl.win.ODisconnectHandlerRes;
import com.agnity.simulator.handlers.impl.win.ONoAnswerHandler;
import com.agnity.simulator.handlers.impl.win.ONoAnswerResHandler;
import com.agnity.simulator.handlers.impl.win.OrigReqRetResultHandler;
import com.agnity.simulator.handlers.impl.win.OrreqHandler;
import com.agnity.simulator.handlers.impl.win.SRFDirectiveHandler;
import com.agnity.simulator.handlers.impl.win.SRFDirectiveRetResHandler;
import com.agnity.simulator.handlers.impl.win.SeizeResHandler;
import com.agnity.simulator.handlers.impl.win.SeizeResRespHandler;
import com.agnity.simulator.handlers.impl.win.TAnswerHandler;
import com.agnity.simulator.handlers.impl.win.TBusyHandler;
import com.agnity.simulator.handlers.impl.win.TBusyResHandler;
import com.agnity.simulator.handlers.impl.win.TDisconnectHandler;
import com.agnity.simulator.handlers.impl.win.TDisconnectRespHandler;
import com.agnity.simulator.handlers.impl.win.TNoAnswerHandler;
import com.agnity.simulator.handlers.impl.win.TNoAnswerResHandler;
import com.agnity.simulator.utils.Constants;

public class HandlerFactory {
	
	private static final int PROV_183_MSG = 183;
	private static final int PROV_180_MSG = 180;

	public static Handler getHandler(Node node){
		String type = node.getType();
		Handler handler=null;
		if(type.equalsIgnoreCase(Constants.START)){
			handler=StartHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.IDP)){
			handler=IdpHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ETC)){
			handler = EtcHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.DFC)){
			handler= DfcHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.CON)){
			handler= ConHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ENC)){
			handler= EncHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ERB)){
			handler= ErbHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.SCI)){
			handler= SciHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.RRBE)){
			handler= RrbeHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.RNCE)){
			handler= RnceHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.U_ABORT)){
			handler= UAbortHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TC_END)){
			handler= TcEndHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TC_ERROR)){
			handler= TcErrorHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TC_REJECT)){
			handler= TcRejectHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ER)){
			handler= EntityReleaseHanlder.getInstance();
		}else if(type.equalsIgnoreCase(Constants.RELEASE_CALL)){
			handler= ReleaseCallHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ORREQ)){
			handler= OrreqHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ORIG_REQ_RET_RESULT)){
			handler= OrigReqRetResultHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ANLYZD)){
			handler= AnlyzdHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ANLYZD_RES)){
			handler= AnlyzdResHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.OANSWER)){
			handler= OAnswerHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ONOANSWER)){
			handler= ONoAnswerHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ONOANSWERRES)){
			handler= ONoAnswerResHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ODISCONNECT)){
			handler= ODisconnectHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ODISCONNECTRES)){
			handler= ODisconnectHandlerRes.getInstance();
		}else if(type.equalsIgnoreCase(Constants.SRFDIRECTIVE)){
			handler= SRFDirectiveHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.SRFDIRECTIVE_RET_RES)){
			handler= SRFDirectiveRetResHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.CONNRES)){
			handler= ConnResHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.SEIZERES)){
			handler= SeizeResHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.SEIZERESRESP)){
			handler= SeizeResRespHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.INSTRUCTIONREQ)){
			handler= InstructionHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.INSTRUCTIONRES)){
			handler= InstructionRespHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.CALLCONTROLDIRREQ)){
			handler= CallControlDirHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.CALLCONTROLDIRRES)){
			handler= CallControlDirHandlerResp.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TANSWER)){
			handler= TAnswerHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TNOANSWER)){
			handler= TNoAnswerHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TNOANSWERRES)){
			handler= TNoAnswerResHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TDISCONNECT)){
			handler= TDisconnectHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TDISCONNECTRES)){
			handler= TDisconnectRespHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TBUSY)){
			handler= TBusyHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.TBUSYRES)){
			handler= TBusyResHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.OCALLEDPARTYBUSY)){
			handler= OCalledPartyBusyHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.OCALLEDPARTYBUSYRES)){
			handler= OCalledPartyBusyResHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.INVITE)){
			handler = InviteHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.BYE)){
			handler = ByeHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.CANCEL)){
			handler = CancelHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.ACK)){
			handler = AckHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.PRACK)){
			handler = PrackHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.UPDATE)){
			handler = UpdateHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.INFO)){
			handler = InfoHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.INVITE_SUCCESS_RES_NODE)){
			handler = Invite2XXHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.BYE_SUCCESS_RES_NODE)){
			handler = Bye2XXHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.PRACK_SUCCESS_RES_NODE)){
			handler = Prack2XXHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.UPDATE_SUCCESS_RES_NODE)){
			handler = Update2XXHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.INFO_SUCCESS_RES_NODE)){
			handler = Info2XXHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.INVITE_REDIRECT_RES_NODE)){
			handler = Response3XXHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.INVITE_PROV_RES_NODE)){
			ProvResNode provNode= (ProvResNode) node;
			int message = Integer.parseInt(provNode.getMessage());
			switch(message){
			case PROV_183_MSG:
				handler = Response183Handler.getInstance();
				break;
			case PROV_180_MSG:
				handler = Response180Handler.getInstance();
				break;
			default:
				handler = Default1XXHandler.getInstance();
				break;			
			}
		}else if(type.equalsIgnoreCase(Constants.CLIENT_ERROR_RES_NODE)){
			handler = Response4XXHandler.getInstance();
		}else if(type.equalsIgnoreCase(Constants.SERVER_ERROR_RES_NODE)){
			handler = Response5XXHandler.getInstance();
		} else if(type.equalsIgnoreCase(Constants.TIMER_NODE)){
			handler=TimerHandler.getInstance();
		} else if(type.equalsIgnoreCase(Constants.CleanUp_NODE)){
				handler=CleanUpHandler.getInstance();
		}
				
		return handler;
	}


}
