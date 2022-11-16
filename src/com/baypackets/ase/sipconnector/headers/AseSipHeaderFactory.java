/**
 * AseSipHeaderFactory.java
 * @author Kameswara Rao
 */

package com.baypackets.ase.sipconnector.headers;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderFactory;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipMsgParser;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipTokenListWithParamsParser;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipHeaderParserInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;

/**
 * Provides the construction of user created DsSipHeader Objects.The user first  * registers the headers using the registerHeader method provided in this class.
 * The java user agent will automatically create the new header types when parsi * ng a SIP message
 */
public class AseSipHeaderFactory extends DsSipHeaderFactory
{
	/**
	 * Default constructor
	 */
	public AseSipHeaderFactory()
	{
	}	
	
	/**
	 * Creates a new instance of the header according to the id given
	 * @param the unique header id
	 * @return the header.
	 */
	public DsSipHeader newInstance(int headerId)
	{
		
		if(headerId==AseSipHistoryInfoHeader.m_headerID)
			return new AseSipHistoryInfoHeader();
		else if(headerId==AseSipServiceRouteHeader.m_headerID)
			return new AseSipServiceRouteHeader();
		else if(headerId==AseSipPAssociatedURIHeader.m_headerID)	
			return new AseSipPAssociatedURIHeader();
		else if(headerId==AseSipDiversionHeader.m_headerID)	
			return new AseSipDiversionHeader();
		else
			return super.newInstance( headerId);
			
		
	/*	switch(headerId)
		{
			case AseSipHistoryInfoHeader.m_headerID :
				return new AseSipHistoryInfoHeader();
			case AseSipServiceRouteHeader.m_headerID :
				return new AseSipServiceRouteHeader();
			default	:
				return super.newInstance( headerId);
		}*/
	}
	
	/**
	 * Register's the header with the parser
	 * @param the header name, the short name of the header, singularity of the header,the parser with which the header is associated(this can be user defined or one of the various parsers already available) 
	 * @return the id of the header
	 */
	public static int registerHeader(DsByteString longName, DsByteString compactName, boolean isSingular, DsSipHeaderParserInterface parser)
	{
		/*
		if(longName.compareTo(AseSipHistoryInfoHeader.m_token) == 0 )
			return  DsSipMsgParser.registerHeader(longName, null, false, DsSipTokenListWithParamsParser.getInstance());
		else if(longName.compareTo(AseSipServiceRouteHeader.m_token) == 0 )
				return DsSipMsgParser.registerHeader(longName, null, true , DsSipUriHeaderParser.getInstance());
		     else return 0; //the code should not come here	
			 */
	 	return  DsSipMsgParser.registerHeader(longName,compactName,isSingular,parser );
	}
}
