/**********************************************************************
 * This class is automatically generated on Mon Dec 10 09:51:48 2007
 * (c) Genband, 2007
 *********************************************************************/

package com.genband.tcap.xjc;
import jain.protocol.ss7.tcap.dialogue.*;
import jain.protocol.ss7.tcap.component.*;
import jain.protocol.ss7.tcap.*;
import jain.protocol.ss7.sccp.*;
import jain.protocol.ss7.sccp.management.*;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.AddressConstants;
import com.genband.tcap.io.TcapContentReaderException;
import com.genband.tcap.io.TcapContentWriterException;
import java.math.BigInteger;
import java.util.List;

public abstract class /*generated*/ NoticeIndEventInterface
{
	private NoticeIndEvent iNoticeIndEventType = null;
	public NoticeIndEvent getNoticeIndEventInterface() throws TcapContentReaderException
	{
		try
		{
			if (iNoticeIndEventType == null)
			{
				iNoticeIndEventType = new NoticeIndEvent(this);
				if (getDestinationAddress() != null)
					iNoticeIndEventType.setDestinationAddress(getDestinationAddress().getSccpUserAddressInterface());
				if (getOriginatingAddress() != null)
					iNoticeIndEventType.setOriginatingAddress(getOriginatingAddress().getSccpUserAddressInterface());
				iNoticeIndEventType.setReportCause(getReportCause());
				if (getDialogueId() != null)
					iNoticeIndEventType.setDialogueId(getDialogueId().intValue());
				if (getDialoguePortion() != null)
					iNoticeIndEventType.setDialoguePortion(getDialoguePortion().getDialoguePortionInterface());
			}
			return iNoticeIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public NoticeIndEventType produceJAXB(NoticeIndEvent iNoticeIndEvent) throws TcapContentWriterException
	{
		try
		{
			NoticeIndEventType iNoticeIndEventType = new NoticeIndEventType();
			if (iNoticeIndEvent.isDestinationAddressPresent())
				iNoticeIndEventType.setDestinationAddress(SccpUserAddressType.produceJAXB(iNoticeIndEvent.getDestinationAddress()));
			if (iNoticeIndEvent.isOriginatingAddressPresent())
				iNoticeIndEventType.setOriginatingAddress(SccpUserAddressType.produceJAXB(iNoticeIndEvent.getOriginatingAddress()));
			iNoticeIndEventType.setReportCause(iNoticeIndEvent.getReportCause());
			if (iNoticeIndEvent.isDialogueIdPresent())
				iNoticeIndEventType.setDialogueId(BigInteger.valueOf(iNoticeIndEvent.getDialogueId()));
			if (iNoticeIndEvent.isDialoguePortionPresent())
				iNoticeIndEventType.setDialoguePortion(DialoguePortionType.produceJAXB(iNoticeIndEvent.getDialoguePortion()));
			return iNoticeIndEventType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract SccpUserAddressType getDestinationAddress();
	public abstract SccpUserAddressType getOriginatingAddress();
	public abstract byte[] getReportCause();
	public abstract BigInteger getDialogueId();
	public abstract DialoguePortionType getDialoguePortion();
}
