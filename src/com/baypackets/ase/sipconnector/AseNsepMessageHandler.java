package com.baypackets.ase.sipconnector;

import org.apache.log4j.*;
import java.util.*;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.baypackets.ase.container.*;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.AseUtils;

public class AseNsepMessageHandler implements AseSipMessageHandler	{
	
	private AseApplicationSession appSession = null;

	public int handleInitialRequest(AseSipServletRequest request,AseSipSession session)
                  				throws AseSipMessageHandlerException	{

 
		int ret = 0;
		return ret;
	}
                                                                                                                             
        public int handleSubsequentRequest(AseSipServletRequest request,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
                                                                                                                             
        public int recvRequest(AseSipServletRequest request,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
                                                                                                                             
        public int sendInitialRequest(AseSipServletRequest request,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
                                                                                                                             
        public int sendSubsequentRequest(AseSipServletRequest request,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
                                                                                                                             
        public int requestPreSend(AseSipServletRequest request,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
                                                                                                                             
        public int requestPostSend(AseSipServletRequest request,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
        
        public int handleResponse(AseSipServletResponse response,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}

	public int recvResponse(AseSipServletResponse response,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
        
        public int sendResponse(AseSipServletResponse response,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
        
        public int responsePreSend(AseSipServletResponse response,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
        
        public int responsePostSend(AseSipServletResponse response,AseSipSession session)
                  				throws AseSipMessageHandlerException	{
		int ret = 0;
		return ret;
	}
        
         /**
          * Helper methods to check the bit codes set within the return value
          */
        public boolean isRetNoop(int ret)	{
		return true;
	}
        public boolean isRetContinue(int ret)	{

		return true;
	}
        public boolean isRetProxy(int ret)	{

		return true;
	}
        public boolean isRetStateUpdate(int ret)	{

		return true;
	}

	private static ListIterator getRPHHeaders(AseSipServletMessage message)	{
		LinkedList strHdrList = new LinkedList();
                DsSipHeaderList hdrList = null;
                DsSipMessage dsMessage = message.getDsMessage();
                try     {
                        hdrList = dsMessage.getHeaders(new DsByteString(Constants.RPH));
                }catch (IllegalArgumentException ex)    {
                        strHdrList.add(dsMessage.getHeader(new DsByteString(Constants.RPH)));
                        return strHdrList.listIterator(0);
                }
                if(null != hdrList)     {
                        ListIterator hdrIter = hdrList.listIterator(0);
			while (hdrIter.hasNext())	{
                        	String hdrVal = ((DsSipHeaderInterface) hdrIter.next()).getValue().toString();
                        	strHdrList.add(hdrVal);
			}
                
                }
		
		return strHdrList.listIterator(0);
	

	}

	private static Vector getRPHHeadersAfterDeepParsing(AseSipServletMessage message)        {
		Vector rphValues = new Vector();
		String rphValue =null;
		String rph = null;
		ListIterator itr  = getRPHHeaders(message);
		while(itr.hasNext())    {
                	rphValue = (String)itr.next();
                        if(m_logger.isEnabledFor(Level.DEBUG))  {
                                m_logger.debug("rphValue: " +rphValue);
                        }
			//If rphValue contains , then use tokenizer
			//else directly add it to vector
			if(rphValue.indexOf(AseStrings.COMMA) != -1)	{
				StringTokenizer stk = new StringTokenizer(rphValue,AseStrings.COMMA);
				while(stk.hasMoreTokens())	{
					rph = stk.nextToken();
					rphValues.add(rph);
				}
			}else	{
				rphValues.add(rphValue);
			}
		}
		
		return rphValues;
	}
	/**
	* This method will look for ets.x, where x can be 0,1,2,3,4
	* in Resource-Priority headers present in a SIP message
	* @return boolean true if priority message
	*/
	public static boolean getMessagePriority(AseSipServletMessage message)	{
		
		String rphValue = null; 
		int priorityValue;
		int counter = 0;
		boolean priorityMsg = false;
		if(AseUtils.getCallPrioritySupport() == 0)	{
			if(m_logger.isEnabledFor(Level.DEBUG))
				m_logger.debug("Call Priority support is disabled so return false");	
			return priorityMsg;
		}
		Vector v = getRPHHeadersAfterDeepParsing(message);
		Iterator itr = v.iterator(); 
		while(itr.hasNext())	{
			rphValue = (String)itr.next();
			if(m_logger.isEnabledFor(Level.DEBUG))	{
                                m_logger.debug("rphValue: " +rphValue);
			}
			if(rphValue.substring(0,4).equalsIgnoreCase("ets."))	{
				counter++;
				try	{
					priorityValue = Integer.parseInt(rphValue.substring(4));
					if(priorityValue >= 0 && priorityValue < 5)	{
						priorityMsg = true;
					}
				}catch(NumberFormatException exp)	{
					if(m_logger.isEnabledFor(Level.ERROR))
						m_logger.error("Invalid value of ets namespace in RPH");	
				}
			}
			//If multiple RPH with ets namespace found then set message priority to false
			//and stop further processing
			if(counter > 1)	{
				priorityMsg = false;
				break;
			}

		}
		if(priorityMsg && (message.getMethod().equalsIgnoreCase(AseStrings.INVITE)))	{
			message.setMessagePriority(priorityMsg);
		}
		return priorityMsg;
	}

	private int genRetContinue()	{
		int ret = 0;
		ret |= CONTINUE;
		return ret;
	}

	private int genRetNoop()	{
		int ret = 0;
                ret |= NOOP;
                return ret;
	}

	transient private static Logger m_logger =
		Logger.getLogger(AseNsepMessageHandler.class);


}
