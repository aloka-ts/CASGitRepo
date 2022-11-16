package com.genband.m5.maps.mgmt.jmx;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.jmx.adaptor.rmi.RMIAdaptor;
import org.jboss.util.Strings;
import org.jboss.util.propertyeditor.PropertyEditors;

public class JMXUtility {

	private static final Logger log = Logger.getLogger(JMXUtility.class);

	private MBeanServerConnection server;
	private String serverURL;
	private String adapterName;
	public static final String DEFAULT_JNDI_NAME = "jmx/invoker/RMIAdaptor";
	
	private static final JMXUtility jmxAdaptor = new JMXUtility();

	private JMXUtility() {

		initProtocolHandlers();
	}
	 
	public static JMXUtility getInstance () {
		
		return jmxAdaptor;
	}

	private static void initProtocolHandlers() {
		// Include the default JBoss protocol handler package
		String handlerPkgs = System.getProperty("java.protocol.handler.pkgs");
		if (handlerPkgs != null) {
			handlerPkgs += "|org.jboss.net.protocol";
		} else {
			handlerPkgs = "org.jboss.net.protocol";
		}
		System.setProperty("java.protocol.handler.pkgs", handlerPkgs);
	}
	   
	private MBeanServerConnection createMBeanServerConnection()
			throws NamingException, ClassNotFoundException {
		InitialContext ctx;

		Properties props = new Properties(System.getProperties());
		props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jnp.interfaces.NamingContextFactory");
		props.put(Context.URL_PKG_PREFIXES,
				"org.jboss.naming:org.jnp.interfaces");
		
		if (serverURL == null) {
			props.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		} else {
			props.put(Context.PROVIDER_URL, serverURL);
		}

		ctx = new InitialContext(props);
		// if adapter is null, the use the default
		if (adapterName == null) {
			adapterName = DEFAULT_JNDI_NAME;
		}

		Object obj = ctx.lookup(adapterName);	

		 if (!(obj instanceof RMIAdaptor)) {
			throw new ClassCastException(
					"Object not of type: RMIAdaptorImpl, but: "
							+ (obj == null ? "not found" : obj.getClass()
									.getName()));
		}

		ctx.close();
		return (MBeanServerConnection) obj;
	}

	private void connect() throws NamingException, ClassNotFoundException {
		if (server == null) {
			server = createMBeanServerConnection();
		}
	}

	public void invoke(final String name, String opName, List<String> opArgs)
			throws Exception {
		
		log.debug("Invoke " + name);
		

		ObjectName objectName = new ObjectName(name);
		
		connect ();

		// get mbean info for this mbean
		MBeanInfo info = server.getMBeanInfo(objectName);

		// does it even have an operation of this name?
		MBeanOperationInfo[] ops = info.getOperations();
		MBeanOp inputOp = new MBeanOp(opName, opArgs.size());
		MBeanOp matchOp = null;
		ArrayList opList = new ArrayList();
		for (int i = 0; i < ops.length; i++) {
			MBeanOperationInfo opInfo = ops[i];
			MBeanOp op = new MBeanOp(opInfo.getName(), opInfo.getSignature());
			if (inputOp.equals(op) == true) {
				matchOp = op;
				break;
			}
			opList.add(op);
		}

		if (matchOp == null) {
			// If there was not explicit match on type, look for a match on arg count
			throw new Exception(
						"MBean has no such operation named '" + opName
								+ "' with signature compatible with: " + opArgs);

		}

		// convert parameters with PropertyEditor
		int count = matchOp.getArgCount();
		Object[] params = new Object[count];
		for (int i = 0; i < count; i++) {
			String argType = matchOp.getArgType(i);
			PropertyEditor editor = PropertyEditors.getEditor(argType);
			editor.setAsText((String) opArgs.get(i));
			params[i] = editor.getValue();
		}
		log.debug("Using params: " + Strings.join(params, ","));

		// invoke the operation
		Object result = server.invoke(objectName, opName, params, matchOp
				.getSignature());
		log.debug("Raw result: " + result);

		// Translate the result to text
		String resultText = null;

		if (result != null) {
			PropertyEditor editor = PropertyEditors
					.getEditor(result.getClass());
			editor.setValue(result);
			resultText = editor.getAsText();
		} else {
			resultText = "'null'";
		}
		
		if (log.isTraceEnabled()) {
			log.trace("Converted result: " + resultText);
		}
	}
	
	public String get(final String name, String attributeNames) throws Exception {

		if (name == null)
			throw new Exception("Missing object name");

		ObjectName objectName = new ObjectName(name);
		
		log.debug("attribute names: " + attributeNames);

		connect ();

		String [] names = attributeNames.split(",");
		AttributeList attrList = server.getAttributes(objectName, names);
		log.debug("attribute list: " + attrList);

		if (attrList.size() == 0) {
			throw new Exception("No matching attributes");
		} else if (attrList.size() != names.length) {
			log.warn("Not all specified attributes were found");
		}


		Iterator<Object> iter = attrList.iterator();
		StringBuffer values = new StringBuffer();

		while (iter.hasNext()) {
			Attribute attr = (Attribute) iter.next();

			values.append (attr.getName() + "=" + attr.getValue());
			values.append(',');
		}
		values.deleteCharAt(values.length() - 1);
		
		if (log.isTraceEnabled()) {
			log.trace(values);
		}
		
		return values.toString();
	}
	
	
	public void set(final String name, String theAttr, String theVal)
			throws Exception {

		if (name == null)
			throw new Exception("Missing object name");

		ObjectName objectName = new ObjectName(name);

		log.debug("attribute names: " + theAttr + ", value: " + theVal);
		if (null == theAttr
				|| null == theVal) {
			
			throw new Exception("Invalid attribute name-value");
		}

		connect();

		MBeanInfo info = server.getMBeanInfo(objectName);

		MBeanAttributeInfo[] attrs = info.getAttributes();

		MBeanAttributeInfo attr = null;

		boolean found = false;
		for (int i = 0; i < attrs.length; i++) {
			if (attrs[i].getName().equals(theAttr) && attrs[i].isWritable()) {

				found = true;
				attr = attrs[i];
				break;
			}
		}

		if (found == false) {

			throw new Exception("No matching attribute found");
			
		} else {
			Object oVal = convert(theVal, attr.getType());
			Attribute at = new Attribute(theAttr, oVal);
			server.setAttribute(objectName, at);

			// read the attribute back from the server

			Object nat = server.getAttribute(objectName, theAttr);

			if (log.isTraceEnabled()) {
				log.trace(theAttr + "=" + nat);
			}
		}

	}
	
	private Object convert (String val,String oType) throws Exception
	{
		PropertyEditor editor = PropertyEditors.getEditor(oType);
		editor.setAsText(val);
		return editor.getValue();		
	}
}

class MBeanOp {
	private String name;
	private String[] sig;

	public MBeanOp(String name, MBeanParameterInfo[] params) {
		this.name = name;
		int count = params != null ? params.length : 0;
		sig = new String[count];
		for (int n = 0; n < count; n++) {
			MBeanParameterInfo p = params[n];
			sig[n] = p.getType();
		}
	}

	public MBeanOp(String name, int count) {
		this.name = name;
		sig = new String[count];
		for (int n = 0; n < count; n++) {
			sig[n] = String.class.getName();
		}
	}

	public String getName() {
		return name;
	}

	public String[] getSignature() {
		return sig;
	}

	public int getArgCount() {
		return sig.length;
	}

	public String getArgType(int n) {
		return sig[n];
	}

	public boolean equals(Object obj) {
		MBeanOp op = (MBeanOp) obj;
		if (op.name.equals(name) == false || sig.length != op.sig.length)
			return false;
		for (int n = 0; n < sig.length; n++) {
			if (sig[n].equals(op.sig[n]) == false)
				return false;
		}
		return true;
	}

	public int hashCode() {
		int hashCode = name.hashCode();
		for (int n = 0; n < sig.length; n++) {
			hashCode += sig[n].hashCode();
		}
		return hashCode;
	}
}

