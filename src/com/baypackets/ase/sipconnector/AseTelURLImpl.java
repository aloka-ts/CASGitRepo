/*
 * @(#)AseTelURLImpl.java        1.0 2004/08/10
 *
 */

package com.baypackets.ase.sipconnector;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.servlet.sip.TelURL;
import javax.servlet.sip.ServletParseException;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipTelephoneSubscriber;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipObject.DsTelURL;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;

/**
 * Class AseTelURLImpl implements the interface javax.servlet.sip.TelURL.
 *
 * @version 	1.0 10 Aug 2004 
 * @author 	Vimal Mishra
 *
 */

public class AseTelURLImpl extends AseURIImpl implements TelURL, Cloneable {

	/**
	 * This class is a wrapper on the DS Stack class 
	 * com.dynamicsoft.DsLibs.DsSipObject.DsTelURL. 
	 * All the methods in this class delegate to corresponding DsTelURL 
	 * methods.
	 *
	 */

	/** Reference to the DsTelURL object. */
	private DsTelURL m_dsTelUrl;

	/** Iterator for TEL URI parameter names. */
	private java.util.Iterator m_parameterIterator;

	/** Logger element */
	private static Logger logger = Logger.getLogger(AseTelURLImpl.class.getName());
	
	private static final String PHONE_CONTEXT = "phone-context";

	/**
	 * Creates a new AseTelURLImpl object from a valid DsTelURL object.
	 *
	 * @param dsTelUrl	A valid DsTelURL object
	 *
	 * @throws IllegalArgumentException	if dsTelUrl is null.
	 *
	 */
	public AseTelURLImpl( DsTelURL dsTelUrl) {
		super( dsTelUrl);
		m_dsTelUrl = dsTelUrl;
	}

	/**
	 * Creates a new AseTelURLImpl object from a valid DsTelURL object.
	 *
	 * @param dsTelUrl	A valid DsTelURL object
	 * @param bImmutable	True if the new object should be immutable
	 *
	 * @throws IllegalArgumentException	if dsTelUrl is null.
	 *
	 */
	public AseTelURLImpl( DsTelURL dsTelUrl, boolean bImmutable) {
		super( dsTelUrl, bImmutable);
		m_dsTelUrl = dsTelUrl;
	}

	/**
	 * Creates a new AseTelURLImpl object from a valid tel URL string.
	 *
	 * @param telUrl	A valid tel URL String object.
	 *
	 * @throws ServletParseException	if telUrl is not a valid tel URL.
	 *
	 */
	public AseTelURLImpl( String telUrl) throws ServletParseException, 
	ServletParseException {

		super(telUrl);

		try{
			m_dsTelUrl = (DsTelURL)m_dsUri;
		} catch( ClassCastException cce){
			logger.error(STR_INVALID_TEL_URL + telUrl, cce);
			throw new ServletParseException( STR_INVALID_TEL_URL);
		}

		/*try{
			m_dsTelUrl = new DsTelURL( telUrl.getBytes() );
			m_dsUri = m_dsTelUrl;
		}catch( DsSipParserException dsspe){
			logger.error(STR_INVALID_TEL_URL + telUrl);
			throw new ServletParseException( STR_INVALID_TEL_URL);
		}*/

	}

	/**
	 * Returns the value associated with specified parameter
	 *
	 * @param key	Name of the parameter
	 *
	 * @throws ServletParseException	if telUrl is not a valid tel URL.
	 *
	 */
	public String getParameter( String key) {
        //bug# BPInd09232
        if(null == key){
            logger.error("getParameter():key is null");
            return null;
        }
		DsByteString bStr = m_dsTelUrl.getTelephoneSubscriber().getParameter( 
			new DsByteString(key) );

		if( bStr != null)
			return bStr.toString();

		return null;

	}

	public java.util.Iterator getParameterNames() {
		/**
		 * No exact replica method was found in DsTelURL.
		 * We are making a list of known params and appending 
		 * a list of "Future Extensions" present in the Tel URL.
		 *
		 The various parameter names that can be queried for are

		 [param name]           [description]

		 isub                 ISDNSubaddress
		 postd                post dial
		 tsp                  Service provider
		 phone-context        Area Specifier
		 extension-name 			future extension name

		 If there are more than one parameter of same type, 
		 then all the parameters will be concatenated but separated by ';'.
		 */

		if( m_parameterIterator != null){
			return m_parameterIterator;
		} 

		// Prepare a new linked list of Param names:-
		LinkedList list = new LinkedList();
		list.add( DsSipConstants.BS_ISUB.toString());
		list.add( DsSipConstants.BS_POSTD.toString());
		list.add( DsSipConstants.BS_TSP.toString());
		list.add( DsSipConstants.BS_PHONE_CONTEXT.toString());

		// Get all the future Extensions:
		Hashtable ht = m_dsTelUrl.getTelephoneSubscriber().getFutureExtensions();
		if( ht == null){
			return list.listIterator();
		}

		Enumeration fpEnum = ht.keys();
		while( fpEnum.hasMoreElements() ){
	
			list.add( 
				( (DsByteString) fpEnum.nextElement()).toString() );
		}

		m_parameterIterator = list.listIterator();

		return m_parameterIterator;
	}

    /**
     * Returns the cloned object of this TelURI. The copy should be mutable.
     */
    public TelURL clone(){
            return new AseTelURLImpl( (DsTelURL)m_dsTelUrl.clone(), false);
    }

	public java.lang.String toString() {
		return m_dsTelUrl.toString();
	}

	public java.lang.String getPhoneNumber() {

		return m_dsTelUrl.getTelephoneSubscriber(
			).getPhoneNumber().toString();
	}

	public boolean isGlobal(){

		return m_dsTelUrl.getTelephoneSubscriber(
			).isGlobal();
	}

	// BugID  6859
	
	/*
	 *Compares the given TelURL with this TelURL.
	 */
	
	public boolean equals(Object url){
		
		if(url == null || m_dsTelUrl == null){
			return false;
		}

		if(!(url instanceof AseTelURLImpl) ){
			return false;
		}

		return m_dsTelUrl.equals( ((AseTelURLImpl)url).m_dsTelUrl );
	}

	
	/*
	 * The hashCode() needs to be override otherwise it will call 
	 * hashCode() implementation of the Object class which will always
	 * return unequal match for different objects
	 */
	
	public int hashCode() {
		if(m_dsTelUrl == null){
			return -1;
		}

		return m_dsTelUrl.toString().hashCode();
	}
	
	//
	
	/**
	 * Used for Unit Testing only..
	 */
	
	public static void main(String[] args) 
		throws Exception {
		
		/**
		 * The idea is to operate on the passed command line arguments.
		 * Each argument shall represent one particular Unit test case.
		 * Following methods shall be tested...
		 *
		 public void setImmutable()
		 public boolean isSipURI()
		 public java.lang.String getScheme()
		 public java.lang.Object clone()
		 public java.lang.String toString()

		 *
		 public AseTelURLImpl( DsTelURL dsTelUrl) {
		 public AseTelURLImpl( DsTelURL dsTelUrl, boolean bImmutable) {
		 public AseTelURLImpl( String telUrl) throws ServletParseException, 
		 public String getParameter( String key) {
		 public java.util.Iterator getParameterNames() {
		 public java.lang.String toString() {
		 public java.lang.String getPhoneNumber() {
		 public boolean isGlobal(){

		 *
		 */

			String strArg1 = null;

			if(args.length >0){
				strArg1 = args[0];
			}

			AseTelURLImpl uri = new AseTelURLImpl( strArg1);

			logResults("\t[ Method ]\t\t[ Return Value ]");
			logResults("\tboolean isSipURI() : " + uri.isSipURI());
			logResults("\tString getScheme() : " + uri.getScheme() );
			logResults("\tObject clone() : " + uri.clone().toString() );
			logResults("\tString toString() : " + uri );

			logResults("\tboolean isGlobal() : " + uri.isGlobal() );
			logResults("\tjava.lang.String getPhoneNumber()  : " + uri.getPhoneNumber() );
			logResults("\tString getParameter( String key)  : " + uri.getParameter("user") );
			logResults("\tjava.util.Iterator getParameterNames()  : " + 
				printIterator( uri.getParameterNames() ) );


		}// main()

	public String getPhoneContext() {
		return this.getParameter(PHONE_CONTEXT);
	}

	public void setPhoneNumber(String phoneNumber) {
		if(phoneNumber == null) {
			logger.error("setPhoneNumber(): Phone Number is null");
			return;
		}
		
		//Check for invalid phone number
		validateBasePhoneNumber(phoneNumber);
		
		if ( phoneNumber.charAt(0) == '+' )
        {
			m_dsTelUrl.getTelephoneSubscriber().setPhoneNumber(new DsByteString(phoneNumber));
	    }
		else
			logger.error("setPhoneNumber(): Phone Number not global");
	}

	public void setPhoneNumber(String phoneNumber, String phoneContext) {
		if(phoneNumber == null) {
			logger.error("setPhoneNumber(): Phone Number is null");
			return;
		}
		
		if(phoneContext == null) {
			logger.error("setPhoneNumber(): Phone context is null");
			return;
		}
		
		//Check for invalid phone number
		validateBasePhoneNumber(phoneNumber);
		
		if ( phoneNumber.charAt(0) == '+' )
			logger.error("setPhoneNumber(): Phone Number not local");
		else
		{
			m_dsTelUrl.getTelephoneSubscriber().setPhoneNumber(new DsByteString(phoneNumber));
			this.setParameter("phone-context", phoneContext);
		}
	}

	public void setParameter(String key, String value) {
		if(null == key){
			logger.error("setParameter():key is null");
			return;
		}

		if(null == value){
			logger.error("setParameter():value is null");
			return;
		}

		m_dsTelUrl.getTelephoneSubscriber().setParameter( 
				new DsByteString(key),new DsByteString(value));
	}

	private void validateBasePhoneNumber(String number){
		StringLexer lexer = new StringLexer(number);

		while (lexer.hasMoreChars()) {
			char ch = 0;
			try {
				ch = lexer.lookAhead(0);
			} catch (ParseException e) {
				logger.error(e);
			}

			if (lexer.isDigit(ch)
					|| lexer.isHexDigit(ch)
					|| ch == '-'  || ch == '.'
					|| ch == '('  || ch == '+'
					|| ch == ')'  || ch == '*'
					|| ch == '#'  ) {
				lexer.consume(1);
			}
			else
				throw new IllegalArgumentException("setPhoneNumber():Phone number Invalid. Unexpected " + ch + " in the phone number");
		}
	}
}

class StringLexer
{

	protected String buffer;
	protected int ptr;
	protected int savedPtr;

	public StringLexer(String buffer)
	{
		this.buffer = buffer;
		ptr = 0;
	}

	public boolean hasMoreChars()
	{
		return ptr < buffer.length();
	}

	public boolean isHexDigit(char ch)
	{
		if(isDigit(ch))
		{
			return true;
		} else
		{
			char ch1 = Character.toUpperCase(ch);
			return ch1 == 'A' || ch1 == 'B' || ch1 == 'C' || ch1 == 'D' ;
		}
	}

	public boolean isAlpha(char ch)
	{
		boolean retval = Character.isUpperCase(ch) || Character.isLowerCase(ch);
		return retval;
	}

	public boolean isDigit(char ch)
	{
		boolean retval = Character.isDigit(ch);
		return retval;
	}

	public char lookAhead() throws ParseException
	{
		return lookAhead(0);
	}

	public char lookAhead(int k) throws ParseException
	{
		if(ptr + k < buffer.length())
		{
			return buffer.charAt(ptr + k);
		} else
		{
			return '\0';
		}
	}

	public void consume()
	{
		ptr = savedPtr;
	}

	public void consume(int k)
	{
		ptr += k;
	}
}