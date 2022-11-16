package jain;

public class ASNParsingException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5660714504363006358L;
	String errorString;
	/**
	 * @param errorString
	 */
	public ASNParsingException(String errorString) {
		super(errorString);
		this.errorString = errorString;
	}
	/**
	 * 
	 */
	public ASNParsingException() {
		super();
		this.errorString="ASN parse Failed";
	}
	

}