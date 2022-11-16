package com.genband.tcap.parser;

import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.JainTcapProvider;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.dialogue.BeginIndEvent;
import jain.protocol.ss7.tcap.dialogue.ContinueIndEvent;
import jain.protocol.ss7.tcap.dialogue.EndIndEvent;
import jain.protocol.ss7.tcap.dialogue.NoticeIndEvent;
import jain.protocol.ss7.tcap.dialogue.ProviderAbortIndEvent;
import jain.protocol.ss7.tcap.dialogue.UnidirectionalIndEvent;
import jain.protocol.ss7.tcap.dialogue.UserAbortIndEvent;


public class DialogueIndFactory {

	public static DialogueIndEvent prepareDlgIndEvent(int dlgType,int ansiType, int dlgId,
					SccpUserAddress origSua, SccpUserAddress destSua, byte qos, int reportCause,
					int abortCause, byte[] abortInfo, boolean compPresent, int appContextType,
					byte[] appCtxName, JainTcapProvider provider) throws TcapContentReaderException {

		DialogueIndEvent dlgevent = null;

		switch (dlgType) {
			case TcapConstants.PRIMITIVE_BEGIN: {
				dlgevent = new BeginIndEvent(provider);
				BeginIndEvent begin = (BeginIndEvent) dlgevent;
				if (origSua != null && destSua != null) {
					begin.setDestinationAddress(destSua);
					begin.setOriginatingAddress(origSua);
				}
				begin.setDialogueId(dlgId);
				begin.setComponentsPresent(compPresent);
				if (qos != -1)
					begin.setQualityOfService(qos);
				if (appCtxName != null) {
					begin.setAppContextName(appCtxName);
				}
				begin.setAppContextType(appContextType);
				if(ansiType == 1){
					begin.setAllowedPermission(true);
				}else if (ansiType == 0){
					begin.setAllowedPermission(false);
				}//end if ansitype
				break;
			}//@End case: BEGIN
			case TcapConstants.PRIMITIVE_CONTINUE: {
				dlgevent = new ContinueIndEvent(provider);
				dlgevent.setDialogueId(dlgId);
				ContinueIndEvent continueInd = (ContinueIndEvent) dlgevent;
				continueInd.setComponentsPresent(compPresent);
				if (qos != -1){
					continueInd.setQualityOfService(qos);
				}
				if(ansiType == 1){
					continueInd.setAllowedPermission(true);
				}else if (ansiType == 0){
					continueInd.setAllowedPermission(false);
				}//end if ansitype
				break;
			}//@End case: CONTINUE
			case TcapConstants.PRIMITIVE_END: {
				dlgevent = new EndIndEvent(provider);
				dlgevent.setDialogueId(dlgId);
				EndIndEvent end = (EndIndEvent) dlgevent;
				end.setComponentsPresent(compPresent);
				if (qos != -1)
					end.setQualityOfService(qos);
				break;
			}//@End case: END
			case TcapConstants.PRIMITIVE_NOTICE: {
				dlgevent = new NoticeIndEvent(provider);
				dlgevent.setDialogueId(dlgId);
				NoticeIndEvent notice = (NoticeIndEvent) dlgevent;
				if (destSua != null && origSua != null) {
					notice.setDestinationAddress(destSua);
					notice.setOriginatingAddress(origSua);
				}
				if (reportCause != -1)
					notice.setReportCause(new byte[] { (byte) reportCause });

				break;
			}
			case TcapConstants.PRIMITIVE_PROVIDER_ABORT: {
				dlgevent = new ProviderAbortIndEvent(provider);
				dlgevent.setDialogueId(dlgId);
				if (qos != -1)
					((ProviderAbortIndEvent) dlgevent).setQualityOfService(qos);
				if (abortCause != -1)
					((ProviderAbortIndEvent) dlgevent).setPAbort(abortCause);
				break;
			}
			case TcapConstants.PRIMITIVE_USER_ABORT: {
				dlgevent = new UserAbortIndEvent(provider);
				dlgevent.setDialogueId(dlgId);
				if (qos != -1)
					((UserAbortIndEvent) dlgevent).setQualityOfService(qos);
				if (abortCause != -1)
					((UserAbortIndEvent) dlgevent).setAbortReason(abortCause);
				if (abortInfo != null)
					((UserAbortIndEvent) dlgevent).setUserAbortInformation(abortInfo);
				break;
			}
			case TcapConstants.PRIMITIVE_UNIDIRECTIONAL: {
				dlgevent = new UnidirectionalIndEvent(provider);
				UnidirectionalIndEvent uni = (UnidirectionalIndEvent) dlgevent;
				uni.setDialogueId(dlgId);
				uni.setComponentsPresent(compPresent);
				if (qos != -1)
					uni.setQualityOfService(qos);
				if (origSua != null && destSua != null) {
					uni.setDestinationAddress(destSua);
					uni.setOriginatingAddress(origSua);
				}
				break;
			}
			default:
				throw new TcapContentReaderException("Unknow dlg type");
		}

		return dlgevent;

	}

}
