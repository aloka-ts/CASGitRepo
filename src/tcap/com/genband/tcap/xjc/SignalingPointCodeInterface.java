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

public abstract class /*generated*/ SignalingPointCodeInterface
{
	private SignalingPointCode iSignalingPointCodeType = null;
	public SignalingPointCode getSignalingPointCodeInterface() throws TcapContentReaderException
	{
		try
		{
			if (iSignalingPointCodeType == null)
			{
				iSignalingPointCodeType = new SignalingPointCode(this);
				if (getMember() != null)
					iSignalingPointCodeType.setMember(getMember().intValue());
				if (getCluster() != null)
					iSignalingPointCodeType.setCluster(getCluster().intValue());
				if (getZone() != null)
					iSignalingPointCodeType.setZone(getZone().intValue());
			}
			return iSignalingPointCodeType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public SignalingPointCodeType produceJAXB(SignalingPointCode iSignalingPointCode) throws TcapContentWriterException
	{
		try
		{
			SignalingPointCodeType iSignalingPointCodeType = new SignalingPointCodeType();
			if (iSignalingPointCode.isSpcPresent())
				iSignalingPointCodeType.setSpc(iSignalingPointCode.getSpc());
			return iSignalingPointCodeType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract BigInteger getMember();
	public abstract BigInteger getCluster();
	public abstract BigInteger getZone();
	public abstract void _setMember(BigInteger val);
	public abstract void _setCluster(BigInteger val);
	public abstract void _setZone(BigInteger val);
	public void setSpc(int[] pc) { _setMember(BigInteger.valueOf(pc[0]));_setCluster(BigInteger.valueOf(pc[1]));_setZone(BigInteger.valueOf(pc[2])); }
}
