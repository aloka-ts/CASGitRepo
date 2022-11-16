package jain;


public class MandatoryParamMissingException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5660714504363006358L;
	String errorString;
	public static enum MISSINGPARAM {
		SERVICE_KEY,ORIG_NUM,TERM_NUM,DEFAULT
	}
	private MISSINGPARAM missingParam;
	/**
	 * @param errorString
	 */
	public MandatoryParamMissingException(String errorString,MISSINGPARAM missingParam) {
		super(errorString);
		this.errorString = errorString;
		if(missingParam!=null){
			this.setMissingParam(missingParam);
		}else{
			this.setMissingParam(MISSINGPARAM.DEFAULT);
		}
	}
	/**
	 * 
	 */
	public MandatoryParamMissingException() {
		super();
		this.errorString="Mandatory Param missing";
		this.setMissingParam(MISSINGPARAM.DEFAULT);
	}
	/**
	 * @param missingParam the missingParam to set
	 */
	public void setMissingParam(MISSINGPARAM missingParam) {
		this.missingParam = missingParam;
	}
	/**
	 * @return the missingParam
	 */
	public MISSINGPARAM getMissingParam() {
		return missingParam;
	}
	

}