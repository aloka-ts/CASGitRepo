package jain;

import java.io.Serializable;

import com.genband.jain.protocol.ss7.tcap.router.TcapNextAppInfo;

public class ParseError implements Serializable{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private int invokeId;
	private TcapNextAppInfo tcapNextAppInfo;;
	
	public static enum PARSE_ERROR_TYPE {
		ASN_PARSE_FAILURE, ENUM_PARAM_OUT_OF_RANGE, PARAM_OUT_OF_RANGE, MANDATORY_PARAM_MISSING, LISTENER_NOT_REGISTERED_SK, 
		LISTENER_NOT_REGISTERED_APP,LISTENER_NOT_REGISTERED_SUA,CRITICALITY_TYPE,UNKNOWN,NUMBER_NOT_PROVISIONED, TS_NULL_NO_INITAL_MESSAGE
	}

	private PARSE_ERROR_TYPE	errorType;
	private Throwable			cause;

	public ParseError(PARSE_ERROR_TYPE errorType,int invokeId,  Throwable cause) {
		if(errorType == null){
			errorType=PARSE_ERROR_TYPE.UNKNOWN;
		}
		this.setErrorType(errorType);
		this.setCause(cause);
		this.setInvokeId(invokeId);
	}

	/**
	 * @param cause
	 *            the cause to set
	 */
	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	/**
	 * @return the cause
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * @param errorType
	 *            the errorType to set
	 */
	public void setErrorType(PARSE_ERROR_TYPE errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return the errorType
	 */
	public PARSE_ERROR_TYPE getErrorType() {
		return errorType;
	}

	/**
	 * @param invokeId the invokeId to set
	 */
	public void setInvokeId(int invokeId) {
		this.invokeId = invokeId;
	}

	/**
	 * @return the invokeId
	 */
	public int getInvokeId() {
		return invokeId;
	}

	public TcapNextAppInfo getTcapNextAppInfo() {
		return tcapNextAppInfo;
	}

	public void setTcapNextAppInfo(TcapNextAppInfo tcapNextAppInfo) {
		this.tcapNextAppInfo = tcapNextAppInfo;
	}

	
	
}