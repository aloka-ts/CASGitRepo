package jain;



public class ParameterOutOfRangeException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5660714504363006358L;
	String errorString;
	public static enum PARAM_NAME {
		SERVICE_KEY,DEFAULT
	}
	private PARAM_NAME outOfRangeParam;
	/**
	 * @param errorString
	 */
	public ParameterOutOfRangeException(String errorString,PARAM_NAME outOfRangeParam) {
		super(errorString);
		this.errorString = errorString;
		if(outOfRangeParam!=null){
			this.setOutOfRangeParam(outOfRangeParam);
		}else{
			this.setOutOfRangeParam(PARAM_NAME.DEFAULT);
		}
	}
	/**
	 * 
	 */
	public ParameterOutOfRangeException() {
		super();
		this.errorString="Param out of name";
		this.setOutOfRangeParam(PARAM_NAME.DEFAULT);
	}
	/**
	 * @param outOfRangeParam the outOfRangeParam to set
	 */
	public void setOutOfRangeParam(PARAM_NAME outOfRangeParam) {
		this.outOfRangeParam = outOfRangeParam;
	}
	/**
	 * @return the outOfRangeParam
	 */
	public PARAM_NAME getOutOfRangeParam() {
		return outOfRangeParam;
	}
	
	

}