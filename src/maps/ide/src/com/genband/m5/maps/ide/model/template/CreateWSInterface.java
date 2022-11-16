package com.genband.m5.maps.ide.model.template;

import java.util.*;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.model.*;;

public class CreateWSInterface
{
  protected static String nl;
  public static synchronized CreateWSInterface create(String lineSeparator)
  {
    nl = lineSeparator;
    CreateWSInterface result = new CreateWSInterface();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "package com.genband.m5.maps.services;" + NL + "" + NL + "import javax.jws.*;" + NL + "import javax.jws.WebService;" + NL + "import javax.jws.soap.SOAPBinding;" + NL + "import javax.naming.*;" + NL + "import java.util.ArrayList;";
  protected final String TEXT_2 = NL + "import com.genband.m5.maps.messages.";
  protected final String TEXT_3 = "_";
  protected final String TEXT_4 = ";";
  protected final String TEXT_5 = "Details_";
  protected final String TEXT_6 = NL + "import com.genband.m5.maps.common.*;" + NL + "import javax.xml.ws.*;" + NL + "" + NL + "@WebService (name = \"";
  protected final String TEXT_7 = "\", targetNamespace = \"";
  protected final String TEXT_8 = "\")" + NL + "@SOAPBinding (style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)" + NL + "public interface ";
  protected final String TEXT_9 = " {   ";
  protected final String TEXT_10 = NL + "\t\t@WebMethod";
  protected final String TEXT_11 = NL + "\t\t@WebResult (name = \"";
  protected final String TEXT_12 = "\")";
  protected final String TEXT_13 = NL + "\t\t@WebResult (name = \"result";
  protected final String TEXT_14 = NL + "\t\tpublic ";
  protected final String TEXT_15 = " ";
  protected final String TEXT_16 = " (";
  protected final String TEXT_17 = " ) throws WSException;";
  protected final String TEXT_18 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     	
	CPFPlugin LOG = CPFPlugin.getDefault();
	LOG.info("SEI generation started in Tempalte.....");
	CPFResource CPFArgument = (CPFResource)argument; 
 	CPFScreen cpfScreen = (CPFScreen)CPFArgument.getCpfScreen();
 	ModelEntity baseEntity = cpfScreen.getPortletRef().getBaseEntity();
 	WebServiceInfo webServiceInfo = cpfScreen.getWebServiceInfo();
 	String webServiceName = new String();
 	String rootEntityName = baseEntity.getName();
 	int operationId = cpfScreen.getPortletRef().getPortletId();
 	if(cpfScreen.getWebServiceRef () == null) {
 		webServiceName = webServiceInfo.getWebServiceName();
 	}
 	else {
 	}
 	Map<CPFConstants.OperationType, String> webMethodsMap = webServiceInfo.getWebMethodsMap();
 	Map<CPFConstants.OperationType, String[]> webParams = webServiceInfo.getWebParams();    //Holds WebParam names for WebPorts
 	Map<CPFConstants.OperationType, String> webResults = webServiceInfo.getWebResults();

    
	if(webMethodsMap.keySet().contains(CPFConstants.OperationType.VIEW)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append( baseEntity.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_4);
    
	}

    
	if(webMethodsMap.keySet().contains(CPFConstants.OperationType.LIST)) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append( baseEntity.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_4);
    
	}

    stringBuffer.append(TEXT_6);
    stringBuffer.append( webServiceName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append( webServiceInfo.getTargetNamespace () );
    stringBuffer.append(TEXT_8);
    stringBuffer.append( webServiceName );
    stringBuffer.append(TEXT_9);
    	
		//Started Generating web Method for each selected operation....
	for(Iterator<CPFConstants.OperationType> keys = webMethodsMap.keySet().iterator(); keys.hasNext(); ) { 
		CPFConstants.OperationType operationType = (CPFConstants.OperationType) keys.next();
		String methodArguments = null;
		String returnType = new String();
		boolean returnValue = false;
		String[] webParamNames = null;	
		if(webParams != null) {
			webParamNames = webParams.get (operationType);
		}
		if(webParamNames != null) {
			methodArguments = new String ("@WebParam(name = \"" + webParamNames[0] + "\") ");
		} else {
			methodArguments = new String();
		}
		switch (operationType) {
			case CREATE :
				returnType = "void";
				methodArguments = methodArguments.concat (rootEntityName + "_" + operationId+" ");
				methodArguments = methodArguments.concat(rootEntityName + operationId);  
				break;
			case MODIFY :
				returnType = "void";
				methodArguments = methodArguments.concat (rootEntityName + "_" + operationId + " ");
				methodArguments = methodArguments.concat (rootEntityName + operationId);
				break;
			case DELETE :
				returnType = "void";
				methodArguments = methodArguments.concat ("Long primaryKeyValue");
				break;
			case VIEW :
				returnType = rootEntityName+"_" + operationId;
				returnValue = true;
				methodArguments = methodArguments.concat ("Long primaryKeyValue");
				break;
			case LIST :
				 returnType = rootEntityName+"Details_" + operationId + "[]";
				 returnValue = true;
				 methodArguments = methodArguments.concat ("AuxiliaryDetails auxiliaryDetails");
				 break;
		}

    stringBuffer.append(TEXT_10);
    		
		if(returnValue){
			if(webResults != null) {

    stringBuffer.append(TEXT_11);
    stringBuffer.append( webResults.get(operationType) );
    stringBuffer.append(TEXT_12);
     	
			} else {

    stringBuffer.append(TEXT_13);
    stringBuffer.append( operationType );
    stringBuffer.append(TEXT_12);
    
			}
		}

    stringBuffer.append(TEXT_14);
    stringBuffer.append( returnType );
    stringBuffer.append(TEXT_15);
    stringBuffer.append( webMethodsMap.get(operationType) );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( methodArguments );
    stringBuffer.append(TEXT_17);
     		
	}		//End of For loop
	LOG.info("SEI geenraion finished from tempalte side.....");

    stringBuffer.append(TEXT_18);
    return stringBuffer.toString();
  }
}
