package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.WebServiceInfo;

public class WebXML
{
  protected static String nl;
  public static synchronized WebXML create(String lineSeparator)
  {
    nl = lineSeparator;
    WebXML result = new WebXML();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "\t<web-app xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:web=\"http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\" id=\"WebApp_ID\" version=\"2.5\">" + NL + "  \t<display-name>";
  protected final String TEXT_2 = "</display-name>";
  protected final String TEXT_3 = NL + " <servlet>" + NL + " \t\t<servlet-name>";
  protected final String TEXT_4 = "</servlet-name>" + NL + "        <servlet-class>com.genband.m5.maps.services.";
  protected final String TEXT_5 = "Impl</servlet-class>" + NL + "        <load-on-startup>1</load-on-startup>" + NL + " </servlet>" + NL + " <servlet-mapping>" + NL + "        <servlet-name>";
  protected final String TEXT_6 = "</servlet-name>" + NL + "        <url-pattern>";
  protected final String TEXT_7 = "</url-pattern>" + NL + " </servlet-mapping>";
  protected final String TEXT_8 = NL + "    <session-config>" + NL + "        <session-timeout>300</session-timeout>" + NL + "    </session-config>" + NL + "</web-app>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CPFScreen cpfScreen = (CPFScreen)argument;
	WebServiceInfo webServiceInfo = cpfScreen.getWebServiceInfo();
	String temp = webServiceInfo.getTargetNamespace();
	temp = temp.substring(6, temp.lastIndexOf("/"));
	boolean initialcall = false;
	//TODO how to get url pattern if not then initialcall false
		//TODO initial call check has to be done....
		//Path is fixed in servlet-class
	if(initialcall) {

    stringBuffer.append(TEXT_1);
    stringBuffer.append( cpfScreen.getBaseEntity().getName() );
    stringBuffer.append(TEXT_2);
    
	}

    stringBuffer.append(TEXT_3);
    stringBuffer.append( webServiceInfo.getWebServiceName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( webServiceInfo.getWebServiceName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( webServiceInfo.getWebServiceName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_7);
    
	if(initialcall) {

    stringBuffer.append(TEXT_8);
    
	}

    return stringBuffer.toString();
  }
}
