package jain;

public class ListenerNotDeployedException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5660714504363006358L;
	String errorString;
	/**
	 * @param errorString
	 */
	public ListenerNotDeployedException(String errorString) {
		super(errorString);
		this.errorString = errorString;
	}
	/**
	 * 
	 */
	public ListenerNotDeployedException() {
		super();
		this.errorString="Listener not deployed";
	}
	

}