package com.genband.tcap.parser;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.dialogue.BeginReqEvent;
import jain.protocol.ss7.tcap.dialogue.ContinueReqEvent;
import jain.protocol.ss7.tcap.dialogue.EndReqEvent;
import jain.protocol.ss7.tcap.dialogue.UnidirectionalReqEvent;
import jain.protocol.ss7.tcap.dialogue.UserAbortReqEvent;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class is used for encoding of dialogues
 */
public class DialogueReqFactory
{
	private static Logger logger = Logger.getLogger(TcapParser.class);	 

	public static List<Byte> encodeDialogue(DialogueReqEvent dlg, List<Byte> list, boolean relay) throws MandatoryParameterNotSetException, ParameterNotSetException
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeDialogue(): Enter encodeDialogue() relay " +relay );
		}
		if(dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN) {
			
			if (logger.isInfoEnabled()) {
				logger.info("encodeDialogue():encode  PRIMITIVE_BEGIN " );
			}
			BeginReqEvent beginDlg = (BeginReqEvent)dlg;
//			
//
//			if (relay) {
//				list.add((byte) TagsConstant.MRS_RELAY);
//				byte rlay = (byte) 1;
////				byte[] relLen = TcapUtil
////						.encodeLength(TagsConstant.MRS_RELAY_LENGTH);
////				for (int i = 0; i < relLen.length; i++) {
////					list.add(relLen[i]);
////				}
//				list.add(rlay);
//			}else{
//				list.add((byte) TagsConstant.MRS_RELAY);
//				byte rlay = (byte) 0;
////				byte[] relLen = TcapUtil
////						.encodeLength(TagsConstant.MRS_RELAY_LENGTH);
////				for (int i = 0; i < relLen.length; i++) {
////					list.add(relLen[i]);
////				}
//				list.add(rlay);
//			}
			
			//add encoding of Permisson in ANSI 
			if(beginDlg.isAllowedPermissionPresent()){
				list.add((byte)TagsConstant.ALLOWED_PERMISSION);
				list.add( (byte)((beginDlg.isAllowedPermission())? 1: 0) );
			}
			
			//adding orig_sua
			byte[] origAdd = TcapUtil.encodeSCCPUserAdd(beginDlg.getOriginatingAddress(),true);
			list.add((byte)TagsConstant.ORIG_SUA);
			byte[] origAddLen = TcapUtil.encodeLength(origAdd.length);
			for(int i=0; i<origAddLen.length; i++)
				list.add(origAddLen[i]);
			for(int i=0; i<origAdd.length; i++)
				list.add(origAdd[i]);
			
			//adding dest_sua
			byte[] destAdd = TcapUtil.encodeSCCPUserAdd(beginDlg.getDestinationAddress(),true);
			list.add((byte)TagsConstant.DEST_SUA);
			byte[] destAddLen = TcapUtil.encodeLength(destAdd.length);
			for(int i=0; i<destAddLen.length; i++)
				list.add(destAddLen[i]);
			for(int i=0; i<destAdd.length; i++)
				list.add(destAdd[i]);
			
			//adding encoding for ACN if present
			if(beginDlg.isDialoguePortionPresent() && beginDlg.getDialoguePortion().isAppContextNamePresent()){
				byte[] acn= beginDlg.getDialoguePortion().getAppContextName();
				list.add((byte)TagsConstant.APP_CONTEXT_NAME);
				
				byte[] acnLen = TcapUtil.encodeLength(acn.length);
				for(int i=0; i<acnLen.length; i++){
					list.add(acnLen[i]);
				}
				for(int i=0; i<acn.length; i++){
					list.add(acn[i]);
				}
			}
			
		}
		else if(dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_CONTINUE) {
			//add encoding of Permisson in ANSI 
			
			if (logger.isInfoEnabled()) {
				logger.info("encodeDialogue():encode  PRIMITIVE_CONTINUE " );
			}
			ContinueReqEvent continueDlg = (ContinueReqEvent)dlg;
			
			if (continueDlg.isOriginatingAddressPresent()) {
				
				if (logger.isInfoEnabled()) {
					logger.info("encodeDialogue():encode Orig SUA " );
				}
				// adding orig_sua
				byte[] origAdd = TcapUtil.encodeSCCPUserAdd(continueDlg
						.getOriginatingAddress(),true);
				list.add((byte) TagsConstant.ORIG_SUA);
				byte[] origAddLen = TcapUtil.encodeLength(origAdd.length);
				for (int i = 0; i < origAddLen.length; i++)
					list.add(origAddLen[i]);
				for (int i = 0; i < origAdd.length; i++)
					list.add(origAdd[i]);
			}
			
			if (continueDlg.isDestinationAddressPresent()) {
				
				if (logger.isInfoEnabled()) {
					logger.info("encodeDialogue():encode Dest SUA " );
				}
				// adding dest_sua
				byte[] destAdd = TcapUtil.encodeSCCPUserAdd(continueDlg
						.getDestinationAddress(),true);
				list.add((byte) TagsConstant.DEST_SUA);
				byte[] destAddLen = TcapUtil.encodeLength(destAdd.length);
				for (int i = 0; i < destAddLen.length; i++)
					list.add(destAddLen[i]);
				for (int i = 0; i < destAdd.length; i++)
					list.add(destAdd[i]);
			}
			
			if(continueDlg.isAllowedPermissionPresent()){
				list.add((byte)TagsConstant.ALLOWED_PERMISSION);
				list.add( (byte)((continueDlg.isAllowedPermission())? 1: 0) );
			}
		}
		else if(dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_END) {
			//no specific params
			
			if (logger.isInfoEnabled()) {
				logger.info("encodeDialogue():encode  PRIMITIVE_END " );
			}
			EndReqEvent endDlg = (EndReqEvent) dlg;

			if (endDlg.getOriginatingAddress() != null) {
				// adding orig_sua
				byte[] origAdd = TcapUtil.encodeSCCPUserAdd(endDlg
						.getOriginatingAddress(),true);
				list.add((byte) TagsConstant.ORIG_SUA);
				byte[] origAddLen = TcapUtil.encodeLength(origAdd.length);
				for (int i = 0; i < origAddLen.length; i++)
					list.add(origAddLen[i]);
				for (int i = 0; i < origAdd.length; i++)
					list.add(origAdd[i]);
			}

			if (endDlg.getDestinationAddress() != null) {
				// adding dest_sua
				byte[] destAdd = TcapUtil.encodeSCCPUserAdd(endDlg
						.getDestinationAddress(),true);
				list.add((byte) TagsConstant.DEST_SUA);
				byte[] destAddLen = TcapUtil.encodeLength(destAdd.length);
				for (int i = 0; i < destAddLen.length; i++)
					list.add(destAddLen[i]);
				for (int i = 0; i < destAdd.length; i++)
					list.add(destAdd[i]);
			}
		}
		else if(dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_UNIDIRECTIONAL) {
			UnidirectionalReqEvent uniDlg = (UnidirectionalReqEvent)dlg;
			//adding orig_sua
			byte[] origAdd = TcapUtil.encodeSCCPUserAdd(uniDlg.getOriginatingAddress(),true);
			list.add((byte)TagsConstant.ORIG_SUA);
			byte[] origAddLen = TcapUtil.encodeLength(origAdd.length);
			for(int i=0; i<origAddLen.length; i++)
				list.add(origAddLen[i]);
			for(int i=0; i<origAdd.length; i++)
				list.add(origAdd[i]);
			
			//adding dest_sua
			byte[] destAdd = TcapUtil.encodeSCCPUserAdd(uniDlg.getDestinationAddress(),true);
			list.add((byte)TagsConstant.DEST_SUA);
			byte[] destAddLen = TcapUtil.encodeLength(destAdd.length);
			for(int i=0; i<destAddLen.length; i++)
				list.add(destAddLen[i]);
			for(int i=0; i<destAdd.length; i++)
				list.add(destAdd[i]);
		
		}
		else if(dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_USER_ABORT) {
			UserAbortReqEvent uaDlg = (UserAbortReqEvent)dlg;
			//adding abort reason
			if(uaDlg.isAbortReasonPresent()){
				list.add((byte)TagsConstant.ABORT_CAUSE);
				list.add((byte)uaDlg.getAbortReason());
			}
			//adding abort info
			if(uaDlg.isUserAbortInformationPresent()){
				list.add((byte)TagsConstant.ABORT_INFO);
				byte[] abortLen = TcapUtil.encodeLength(uaDlg.getUserAbortInformation().length);
				for(int i=0; i<abortLen.length; i++)
					list.add(abortLen[i]);	
				byte[] userAbortInfo = uaDlg.getUserAbortInformation();
				for(int i=0; i<userAbortInfo.length; i++)
					list.add(userAbortInfo[i]);
			}
		}
		if (logger.isInfoEnabled()) {	
			logger.info("encodeDialogue(): Exit encodeDialogue()");
		}
		return list;
	}	
}
