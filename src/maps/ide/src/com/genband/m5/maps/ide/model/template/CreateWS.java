package com.genband.m5.maps.ide.model.template;

import java.util.*;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.model.*;
import com.genband.m5.maps.ide.model.util.*;;

public class CreateWS
{
  protected static String nl;
  public static synchronized CreateWS create(String lineSeparator)
  {
    nl = lineSeparator;
    CreateWS result = new CreateWS();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "package com.genband.m5.maps.services;" + NL + "" + NL + "import javax.jws.*;" + NL + "import javax.jws.WebService;" + NL + "import javax.jws.soap.SOAPBinding;" + NL + "import javax.naming.*;" + NL + "import java.util.List;" + NL + "import java.util.ArrayList;" + NL + "import java.util.Set;" + NL + "import org.jboss.security.SecurityAssociation;" + NL + "import java.security.Principal;" + NL + "import com.genband.m5.maps.identity.GBUserPrincipal;" + NL + "" + NL + "//import com.genband.m5.maps.common.PreProcessCriteria;" + NL + "import com.genband.m5.maps.interfaces.ICPFSessionFacade;" + NL + "import com.genband.m5.maps.common.entity.Organization;";
  protected final String TEXT_2 = NL + "import ";
  protected final String TEXT_3 = ";";
  protected final String TEXT_4 = NL + "import com.genband.m5.maps.messages.";
  protected final String TEXT_5 = "_";
  protected final String TEXT_6 = NL + "import com.genband.m5.maps.common.*;" + NL + "import javax.xml.ws.*;" + NL + "import javax.annotation.Resource;" + NL + "import javax.servlet.http.HttpSession;" + NL + "import javax.servlet.http.HttpServletRequest;" + NL + "import javax.xml.ws.handler.MessageContext;" + NL + "//import com.genband.m5.maps.session.CPFSessionFacade;";
  protected final String TEXT_7 = "Details_";
  protected final String TEXT_8 = NL + NL + "@WebService (name = \"";
  protected final String TEXT_9 = "\", targetNamespace = \"";
  protected final String TEXT_10 = "\")" + NL + "@SOAPBinding (style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)" + NL + "public class ";
  protected final String TEXT_11 = "Impl implements ";
  protected final String TEXT_12 = " {    " + NL + "" + NL + "\t\t@Resource" + NL + " \t\tprivate WebServiceContext wsCtx;" + NL + " \t\t";
  protected final String TEXT_13 = NL + "\t\t@WebMethod";
  protected final String TEXT_14 = NL + "\t\t@WebResult (name = \"";
  protected final String TEXT_15 = "\")";
  protected final String TEXT_16 = NL + "\t\t@WebResult (name = \"result";
  protected final String TEXT_17 = "\t\t\t\t\t//Starting of the Web Method" + NL + "\t\tpublic ";
  protected final String TEXT_18 = " ";
  protected final String TEXT_19 = " (";
  protected final String TEXT_20 = " ) throws WSException {" + NL + "\t\t\tint operationId = get";
  protected final String TEXT_21 = "OperationId ();      //getting operationId for this OperationType" + NL + "\t\t\tOrganization merchantAccount = _getMerchantAccount ();";
  protected final String TEXT_22 = NL + "\t\t\tLong merchantId = merchantAccount.getOrganizationId ();    ";
  protected final String TEXT_23 = NL + "\t\t\t";
  protected final String TEXT_24 = " rootEntity = ";
  protected final String TEXT_25 = ".merge();" + NL + "\t\t\trootEntity.setMerchantAccount (merchantAccount);";
  protected final String TEXT_26 = " rootEntity = new ";
  protected final String TEXT_27 = "();";
  protected final String TEXT_28 = NL + "\t\t\tthis.setPKs2Null(rootEntity);";
  protected final String TEXT_29 = NL + "\t\t\tCriteria criteria = new Criteria();";
  protected final String TEXT_30 = "null;";
  protected final String TEXT_31 = "\t\t" + NL + "\t\t\tcriteria.setBaseEntityName (\"";
  protected final String TEXT_32 = "\");";
  protected final String TEXT_33 = NL + "\t\t\tcriteria.setBasePrimaryKey(\"";
  protected final String TEXT_34 = "\");" + NL + "\t\t\tcriteria.setFields (\"";
  protected final String TEXT_35 = NL + "\t\t\tcriteria.setFrom (\"";
  protected final String TEXT_36 = "\");" + NL + "\t\t\tcriteria.setWhere (\"";
  protected final String TEXT_37 = "\" + merchantId + \")\");";
  protected final String TEXT_38 = NL + "\t\t\tcriteria.setBasePrimaryKeyValue(";
  protected final String TEXT_39 = ");";
  protected final String TEXT_40 = "\t" + NL + "\t\t\t//PreProcessCriteria preProcessCriteria = new PreProcessCriteria ();" + NL + "\t\t\t//criteria.setSearchDetails (preProcessCriteria.preProcess (auxiliaryDetails));" + NL + "\t\t\tthis.preProcess(auxiliaryDetails);" + NL + "\t\t\tcriteria.setSearchDetails (auxiliaryDetails);";
  protected final String TEXT_41 = NL + "\t\t\tboolean logicalDelete = false;";
  protected final String TEXT_42 = NL + "\t\t\tICPFSessionFacade cpfSessionFacade = null;" + NL + "\t\t\ttry {" + NL + "\t\t\t\t\tContext ctx = new InitialContext();" + NL + "\t\t\t\t\tcpfSessionFacade = (ICPFSessionFacade)ctx.lookup(\"maps/LocalCPFSessionFacade\");" + NL + "\t\t\t\t\t//cpfSessionFacade.initialize();" + NL + "\t\t\t} catch (NamingException e) {" + NL + "\t\t\t\t\tthrow new WSException (\"error in lookup sessionfacade\", e);" + NL + "\t\t\t\t\t//e1.printStackTrace();" + NL + "\t\t\t}" + NL + "\t\t\ttry {";
  protected final String TEXT_43 = NL + "\t\t\t\t";
  protected final String TEXT_44 = "cpfSessionFacade.";
  protected final String TEXT_45 = "(";
  protected final String TEXT_46 = ")cpfSessionFacade.";
  protected final String TEXT_47 = "Object (";
  protected final String TEXT_48 = NL + "\t\t\t} catch (CPFException e) {" + NL + "\t\t\t\tthrow new WSException (\"error in operation Method\", e);" + NL + "\t\t\t}";
  protected final String TEXT_49 = " returnDetails = new  ";
  protected final String TEXT_50 = NL + "\t\t\tif(";
  protected final String TEXT_51 = ".size() > auxiliaryDetails.getPageSize()) {" + NL + "\t\t\t\t\t";
  protected final String TEXT_52 = ".remove(auxiliaryDetails.getPageSize());" + NL + "\t\t\t}" + NL + "\t\t\t";
  protected final String TEXT_53 = " returnDetails = new ";
  protected final String TEXT_54 = ".size()];" + NL + "\t\t\tfor (int itr = 0; itr < ";
  protected final String TEXT_55 = ".size(); itr++) {" + NL + "\t\t\t\tObject[] temp = ";
  protected final String TEXT_56 = ".get(itr);" + NL + "\t\t\t\treturnDetails[itr] = new  ";
  protected final String TEXT_57 = "(temp);" + NL + "\t\t\t}";
  protected final String TEXT_58 = NL;
  protected final String TEXT_59 = NL + "\t\t\treturn returnDetails;";
  protected final String TEXT_60 = "\t\t\t\t\t\t\t\t\t\t\t//Closing of the Web Method  and starting of the private method which returns operation ids for this method" + NL + "\t\t}      " + NL + "\t\t" + NL + "\t\tprivate int get";
  protected final String TEXT_61 = "OperationId () {" + NL + "\t\t\tint operationId = new Integer (-99);";
  protected final String TEXT_62 = NL + "\t\t\tif (";
  protected final String TEXT_63 = ") {" + NL + "\t\t\t\toperationId = ";
  protected final String TEXT_64 = ";" + NL + "\t\t\t}";
  protected final String TEXT_65 = NL + "\t\t\t" + NL + "\t\t\treturn operationId;" + NL + "\t\t\t//return -1;" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_66 = NL + "\t\t\t//Mthod which will return Merchant Account for user from his Session" + NL + "\t\tprivate Organization _getMerchantAccount() {" + NL + "\t\t\t//commenting out security ...";
  protected final String TEXT_67 = NL + "\t\tOrganization o =  new Organization ();" + NL + "\t\to.setOrganizationId (new Long (-1));" + NL + "\t\treturn o;";
  protected final String TEXT_68 = NL + "\t\t\t/*MessageContext mc = wsCtx.getMessageContext ();" + NL + "\t\t\tHttpServletRequest request = (HttpServletRequest) mc.get (MessageContext.SERVLET_REQUEST);" + NL + "\t\t\tHttpSession session = request.getSession ();" + NL + "\t\t\tObject obj = session.getAttribute (\"User\");" + NL + "\t\t\tUser user = (User)obj;" + NL + "\t\t\treturn user.getMerchantAccount ();*/" + NL + "            Set<Principal> s = SecurityAssociation.getSubject().getPrincipals();" + NL + "" + NL + "            " + NL + "" + NL + "            for (Principal principal : s) {" + NL + "" + NL + "                  //LOG.debug (\"sub principal: \" + principal.getClass().getName());" + NL + "" + NL + "                        if (principal instanceof GBUserPrincipal) {" + NL + "" + NL + "                              //LOG.debug (\"p: \" + principal);" + NL + "" + NL + "                              " + NL + "" + NL + "                              Organization enterprise = ((GBUserPrincipal) principal).getMerchantAccount();" + NL + "" + NL + "                              long enterpriseId = enterprise.getOrganizationId();" + NL + "" + NL + "                              //LOG.debug(\"enterpriseId = \" + enterpriseId);" + NL + "" + NL + "                              return enterprise;" + NL + "" + NL + "                        }" + NL + "" + NL + " " + NL + "" + NL + "                  }" + NL + "" + NL + "            " + NL + "" + NL + " " + NL + "" + NL + "            " + NL + "" + NL + "            //Organization o = new Organization ();" + NL + "" + NL + "            //o.setOrganizationId (new Long (0));" + NL + "" + NL + "            return null;" + NL;
  protected final String TEXT_69 = NL + "\t\t}" + NL;
  protected final String TEXT_70 = NL + "\t\tprivate void setPKs2Null(";
  protected final String TEXT_71 = " rootEntity) {" + NL + "\t\t\trootEntity.set";
  protected final String TEXT_72 = " (null);";
  protected final String TEXT_73 = NL + "\t\t\trootEntity.get";
  protected final String TEXT_74 = "().set";
  protected final String TEXT_75 = NL + "\t\t}";
  protected final String TEXT_76 = "\t" + NL + "\t\tprivate void preProcess(AuxiliaryDetails auxiliaryDetails) throws WSException {" + NL + "\t\t\tif(auxiliaryDetails.getOrderBy() != null) {" + NL + "\t\t\t\tauxiliaryDetails.setOrderBy(this.SearchAndReplace(auxiliaryDetails.getOrderBy()));" + NL + "\t\t\t}" + NL + "\t\t\tif(auxiliaryDetails.getSearchInfo() != null && auxiliaryDetails.getSearchInfo().getSearchKey() != null) {" + NL + "\t\t\t\tauxiliaryDetails.getSearchInfo().setSearchKey(" + NL + "\t\t\t\t\t\t\t\tthis.SearchAndReplace(auxiliaryDetails.getSearchInfo().getSearchKey()));" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tprivate String SearchAndReplace (String expression) throws WSException {" + NL + "\t\t\tString temp = new String(expression);" + NL + "\t\t\tif(temp.equals(\"primaryKeyValue\")) {" + NL + "\t\t\t\ttemp = new String(\"";
  protected final String TEXT_77 = ".";
  protected final String TEXT_78 = "\");" + NL + "\t\t\t} else" + NL + "\t\t\tif(temp.startsWith(\"";
  protected final String TEXT_79 = "\")" + NL + "\t\t\t\t&& temp.length() > \"";
  protected final String TEXT_80 = "\".length()){";
  protected final String TEXT_81 = NL + "\t\t\t\ttemp = temp.replaceFirst(\"";
  protected final String TEXT_82 = "\", \"";
  protected final String TEXT_83 = ".\");" + NL + "\t\t\t}";
  protected final String TEXT_84 = NL + "\t\t\telse" + NL + "\t\t\tif(temp.startsWith(\"";
  protected final String TEXT_85 = "\") && temp.length() > \"";
  protected final String TEXT_86 = "\".length()) {" + NL + "\t\t\t\ttemp = temp.replaceFirst(\"";
  protected final String TEXT_87 = NL + "\t\t\telse {" + NL + "\t\t\t\tthrow new WSException(\"Not a valid filed please check the field name you have entered..\");" + NL + "\t\t\t}" + NL + "\t\t\tint pos = temp.lastIndexOf(\".\");" + NL + "\t\t\tString te = new String(temp.substring(pos + 1));" + NL + "\t\t\tte = te.toLowerCase().charAt(0) + te.substring(1);" + NL + "\t\t\ttemp = temp.substring(0, pos + 1) + te;" + NL + "\t\t\treturn temp;" + NL + "\t\t}";
  protected final String TEXT_88 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     	
	CPFPlugin LOG = CPFPlugin.getDefault();
	LOG.info("Web Service generaion started from tempalte Side...");
	CPFResource CPFArgument = (CPFResource)argument; 
 	CPFScreen cpfScreen = (CPFScreen)CPFArgument.getCpfScreen();    //which holds the details collected at cpfScreen wizard time
 	ModelEntity baseEntity = cpfScreen.getBaseEntity();			//Which holds BaseEntity name of this web service
 	LOG.info("Generating web service for base Enitty : " + baseEntity.getName());	
 	WebServiceInfo webServiceInfo = cpfScreen.getWebServiceInfo();  //holds information related to generating webserivce like webmethods name adn web service name  
 	String webServiceName = new String();
 	String rootEntityName = baseEntity.getName ();
 	int operationId = cpfScreen.getPortletRef().getPortletId();
 	LOG.info("Generatinf web service for the portlet Id : " + operationId);
 	Map<CPFConstants.OperationType, String[]> webParams = webServiceInfo.getWebParams();    //Holds WebParam names for WebPorts
 	Map<CPFConstants.OperationType, String> webResults = webServiceInfo.getWebResults();
 	if(cpfScreen.getWebServiceRef () == null) {			//checking weather user selected already existed web service or not If not we will get web service name from webServiceInfo
 		webServiceName = webServiceInfo.getWebServiceName();
 	}
 	else {
 		//TODO this release does not supported adding web methods to already existing web services...
 		//Later has to edit this else part ...
 	}
 	LOG.info("Web Service name is : " + webServiceName);
 	Map<CPFConstants.OperationType, String> webMethodsMap = webServiceInfo.getWebMethodsMap();   //holds Web Methods to generate 

    stringBuffer.append(TEXT_1);
    
	if(!baseEntity.getName().equals("Organization")) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append( baseEntity.getCanonicalTypeName() );
    stringBuffer.append(TEXT_3);
    
	}

    
	if(webMethodsMap.keySet().contains(CPFConstants.OperationType.VIEW)) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append( baseEntity.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_3);
    
	}

    stringBuffer.append(TEXT_6);
    
	if(webMethodsMap.keySet().contains(CPFConstants.OperationType.LIST)) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append( baseEntity.getName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_3);
    
	}

    stringBuffer.append(TEXT_8);
    stringBuffer.append( webServiceName );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( webServiceInfo.getTargetNamespace () );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( webServiceName );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( webServiceName );
    stringBuffer.append(TEXT_12);
    		
	Map<Integer, String[]> operationRoleMap = null;			//which holds roles vs operationIDs
	for(Iterator<CPFConstants.OperationType> keys = webMethodsMap.keySet().iterator(); keys.hasNext(); ) { 
		CPFConstants.OperationType operationType = (CPFConstants.OperationType) keys.next();
		LOG.info("Generating web method for the operation : " + operationType.toString());
		String methodArguments = null;						//Arguments to be passed from Client to Port
		String returnType = new String();					//Return Type of Port Method
		String arguments = new String("operationId");       //Arguments to be passeed from Port to SessionBean
		boolean returnValue = false;						//Return Value from the Port Method to be passed or not
		String returning4mSF = new String("");				//Signature of returned varible from Session Bean i.e, CPFSessionFacade
		String variable4mSF = new String("");				//Variable name holding values returned from Session Bean i.e, CPFSesisonFacade
		String primaryKeyName = baseEntity.getPrimaryKey();  
		String fields = new String ();						//Holds criteria fields
		String from = new String ();						//Holds criteria from details
		String where = new String ();						//Holds criteria where details
		operationRoleMap = new HashMap<Integer, String[]> ();
		operationRoleMap = cpfScreen.getOperationRoleMap (operationType);
		String[] webParamNames = null;	//Added this line
		if(webParams != null) {
			webParamNames = webParams.get (operationType);
		} 
		if(webParamNames != null) {
			methodArguments = new String ("@WebParam(name = \"" + webParamNames[0] + "\") ");
		} else {
			LOG.info("DID not enter web param names so not adding web params to the method... default will be set as params...");
			methodArguments = new String();
		}
		switch (operationType) {
			case CREATE :
				returnType = "void";     //which holds return type of this operation
				arguments = arguments.concat (", rootEntity");  
				methodArguments = methodArguments.concat (rootEntityName + "_" + operationId + " ");
				methodArguments = methodArguments.concat(rootEntityName + operationId); 
				break;
			case MODIFY :
				returnType = "void";
				arguments = arguments.concat (", rootEntity, criteria");
				methodArguments = methodArguments.concat (rootEntityName + "_" + operationId + " ");
				methodArguments = methodArguments.concat (rootEntityName + operationId);
				break;
			case DELETE :
				returnType = "void";
				arguments = arguments.concat (", rootEntity, primaryKeyValue, logicalDelete");
				methodArguments = methodArguments.concat ("Long primaryKeyValue");
				break;
			case VIEW :
				returnType = rootEntityName+"_" + operationId;
				returnValue = true;
				arguments = arguments.concat(", new " + rootEntityName + "()");
				arguments = arguments.concat (", criteria"); 
				methodArguments = methodArguments.concat ("Long primaryKeyValue");
				returning4mSF = returning4mSF.concat(rootEntityName);	//Replacing Array Of Objects with Object
				variable4mSF = variable4mSF.concat(" viewDetails = ");
				where = where.concat (rootEntityName + "." + primaryKeyName + "= :PK");
				break;
			case LIST :
				returnType = rootEntityName+"Details_" + operationId + "[]";
				returnValue = true;
				arguments = arguments.concat (", criteria");
				methodArguments = methodArguments.concat ("AuxiliaryDetails auxiliaryDetails");
				returning4mSF = returning4mSF.concat("List<Object []>");
				variable4mSF = variable4mSF.concat(" listDetails = ");
				if (cpfScreen.getExtraListPredicate () != null) {
					where = where.concat (cpfScreen.getExtraListPredicate ());    
				}
				fields = fields.concat (rootEntityName + "." + baseEntity.getPrimaryKey() + ", ");
				break;
		}
	//// Starting filling data for Criteria (from and fields)
				//For BaseEntity
		LOG.info("getting info of selected base entity attributes of from and fields");
		from = from.concat (rootEntityName + " " + rootEntityName);
		List<CPFAttribute> selectedAttributes = cpfScreen.getSelectedAttributes ();
		for (Iterator<CPFAttribute> itrCpfAttribute = selectedAttributes.iterator (); itrCpfAttribute.hasNext ();) {
			CPFAttribute cpfAttribute = (CPFAttribute)itrCpfAttribute.next ();
			if (cpfAttribute.getForeignColumn () == null) {
					//Edited on 26th Feb
				if(operationType.compareTo(CPFConstants.OperationType.LIST) == 0)
					fields = fields.concat (rootEntityName + "."+cpfAttribute.getModelAttrib ().getName () + ", ");
				else 
					fields = fields.concat (cpfAttribute.getModelAttrib ().getName () + ", ");
			} else {
					ModelAttribute innerModelAttribute = cpfAttribute.getForeignColumn ();
					//ModelEntity innerModelEntity = (ModelEntity)cpfAttribute.getModelAttrib ().getForeignEntity ();
					RelationShipInfo relationShipInfo = cpfAttribute.getModelAttrib().getRelType();
					boolean manyRelation = false; 
					if(relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToMany)
						|| relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToMany)) {
						manyRelation = true;
					}
						//Edited on 20th Feb..
					//from = from.concat (" left join " + rootEntityName + "." + relationShipInfo.getPropertyName () + " " + innerModelEntity.getName ());
					from = from.concat (" left join " + rootEntityName + "." + relationShipInfo.getPropertyName () + " " + relationShipInfo.getPropertyName ());
					if (operationType.compareTo(CPFConstants.OperationType.MODIFY) == 0) {
						if (manyRelation) {
							fields = fields.concat (relationShipInfo.getPropertyName () + "[n]" + ","); 
						}else {
							fields = fields.concat (relationShipInfo.getPropertyName () + ",");
						}
					} else {
							//Edited on 26th Feb..
						if (manyRelation) {
							fields = fields.concat (relationShipInfo.getPropertyName () + "[n]." + innerModelAttribute.getName () + ",");
						} else {			
							fields = fields.concat (relationShipInfo.getPropertyName () + "." + innerModelAttribute.getName () + ",");
						}
					}
			}
		}
				//For Other Selected Entities
		Map<RelationKey, List<CPFAttribute>> nestedAttributes = cpfScreen.getNestedAttributes ();
		if(nestedAttributes != null) {
			LOG.info("getting info of selected Dependent entities attributes of from and fields");
		for (Iterator<RelationKey> itrModelEntity = nestedAttributes.keySet().iterator (); itrModelEntity.hasNext ();) {
			RelationKey relationKey = itrModelEntity.next(); 	//Added this line extra on 22nd Jan
			//ModelEntity otherEntity = relationKey.getReferencedEntity();
			RelationShipInfo relationShipInfo = relationKey.getRelationShipInfo();
			boolean manyRelation = false;
			if(relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToMany)
				|| relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToMany)) {
				manyRelation = true;
			}
				//Edited on 20th Feb..
			from = from.concat (" left join " + rootEntityName + "." + relationShipInfo.getPropertyName () + " " 
									+ relationShipInfo.getPropertyName ());
			List<CPFAttribute> selectedOtherAttributes = nestedAttributes.get (relationKey);
			for (Iterator<CPFAttribute> itrOtherCpfAttributes = selectedOtherAttributes.iterator (); itrOtherCpfAttributes.hasNext ();) {
				CPFAttribute cpfAttribute = (CPFAttribute)itrOtherCpfAttributes.next ();
				String propName = relationShipInfo.getPropertyName ();
				if (cpfAttribute.getForeignColumn () == null) {
					if (operationType.compareTo(CPFConstants.OperationType.MODIFY) == 0) {	
						if (manyRelation) {
							fields = fields.concat (relationShipInfo.getPropertyName () + "[n]" + "." + cpfAttribute.getModelAttrib ().getName () + ", ");
						} else {
							fields = fields.concat (relationShipInfo.getPropertyName () + "." + cpfAttribute.getModelAttrib ().getName () + ", ");
						}
					} else {
						//Edited on 20th Feb..
						fields = fields.concat (relationShipInfo.getPropertyName () + "." + cpfAttribute.getModelAttrib ().getName () + ", ");
					}
				} else {
					ModelAttribute innerModelAttribute = cpfAttribute.getForeignColumn ();
					//ModelEntity innerModelEntity = (ModelEntity)cpfAttribute.getModelAttrib ().getForeignEntity ();
					RelationShipInfo innerRelationShipInfo = cpfAttribute.getModelAttrib().getRelType();
					boolean innerManyRealtion = false;
					if(innerRelationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToMany)
						|| innerRelationShipInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToMany)) {
						innerManyRealtion = true;
					}
						//edited on 20th Feb..
					from = from.concat (" left join " + relationShipInfo.getPropertyName () + "." 
											+ innerRelationShipInfo.getPropertyName () + " " 
												+ relationShipInfo.getPropertyName () 
													+ innerRelationShipInfo.getPropertyName ());
					if (operationType.compareTo(CPFConstants.OperationType.MODIFY) == 0) {
						if (manyRelation) {
							fields = fields.concat (propName + "[n].");
						} else {
							fields = fields.concat (propName + ".");
						}
						if (innerManyRealtion) {
							fields = fields.concat (innerRelationShipInfo.getPropertyName () + "[n]" + ",");
						} else {
							fields = fields.concat (innerRelationShipInfo.getPropertyName () + ",");
						}
					} else {
						if (manyRelation) {
							fields = fields.concat (propName + "[n].");
						} else {
							fields = fields.concat (propName + ".");
						}
						if (innerManyRealtion) {
							fields = fields.concat (innerRelationShipInfo.getPropertyName () + "[n]." 
													+ innerModelAttribute.getName () + ",");
						} else {
							fields = fields.concat (innerRelationShipInfo.getPropertyName () + "." 
													+ innerModelAttribute.getName () + ",");
							//fields = fields.concat (relationShipInfo.getPropertyName () 
								//					+ innerRelationShipInfo.getPropertyName () + "." 
									//				+ innerModelAttribute.getName () + ",");
						}
					} 
				}
			}
		}
		}
		fields = fields.substring (0, fields.lastIndexOf (","));
		if (where.equals ("")) {
			where = rootEntityName +  ".merchantAccount" + "= (select merchantAccount from Organization merchantAccount where merchantAccount.organizationId=";
		} else {
			where = where.concat(" and " + rootEntityName +  ".merchantAccount" + "= (select merchantAccount from Organization merchantAccount where merchantAccount.organizationId=");
		}
	/////// Ending of filling data for Criteria (from and fields)
	LOG.info("Web Method geenraiton started...");

    stringBuffer.append(TEXT_13);
    		
		if(returnValue){
			if(webResults != null) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append( webResults.get (operationType) );
    stringBuffer.append(TEXT_15);
     	
			} else {

    stringBuffer.append(TEXT_16);
    stringBuffer.append( operationType );
    stringBuffer.append(TEXT_15);
    
			}
		}

    stringBuffer.append(TEXT_17);
    stringBuffer.append( returnType );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( webMethodsMap.get(operationType) );
    stringBuffer.append(TEXT_19);
    stringBuffer.append( methodArguments );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( webMethodsMap.get(operationType) );
    stringBuffer.append(TEXT_21);
    					//getting merchant account Id in case of LIST and VIEW operation
			if (operationType.compareTo(CPFConstants.OperationType.LIST) == 0 || operationType.compareTo(CPFConstants.OperationType.VIEW) == 0) {

    stringBuffer.append(TEXT_22);
    
			} 

    	
			if ((operationType.compareTo(CPFConstants.OperationType.VIEW) != 0) && (operationType.compareTo(CPFConstants.OperationType.LIST) != 0)) {

    
			if(operationType.compareTo(CPFConstants.OperationType.DELETE) != 0) {

    stringBuffer.append(TEXT_23);
    stringBuffer.append( rootEntityName );
    stringBuffer.append(TEXT_24);
    stringBuffer.append( rootEntityName );
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_25);
    
			} else {

    stringBuffer.append(TEXT_23);
    stringBuffer.append( rootEntityName );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( rootEntityName );
    stringBuffer.append(TEXT_27);
    
			}
			if(operationType.compareTo(CPFConstants.OperationType.CREATE) == 0) {

    stringBuffer.append(TEXT_28);
    
			}
			}

    
			if ((operationType.compareTo(CPFConstants.OperationType.VIEW) == 0) || (operationType.compareTo(CPFConstants.OperationType.MODIFY) == 0) || (operationType.compareTo(CPFConstants.OperationType.LIST) == 0)) {
				String primaryKeyValue = new String("primaryKeyValue");

    stringBuffer.append(TEXT_29);
    
			if(operationType.compareTo(CPFConstants.OperationType.MODIFY) != 0) {

    stringBuffer.append(TEXT_23);
    stringBuffer.append( returning4mSF );
    stringBuffer.append( variable4mSF );
    stringBuffer.append(TEXT_30);
    
			}
			if (operationType.compareTo(CPFConstants.OperationType.MODIFY) == 0) {
				primaryKeyValue = new String(rootEntityName + operationId + ".");
				primaryKeyValue = primaryKeyValue.concat("get" + primaryKeyName.toUpperCase().charAt(0) + primaryKeyName.substring(1));
				primaryKeyValue = primaryKeyValue.concat("()");
			

    stringBuffer.append(TEXT_31);
    stringBuffer.append( rootEntityName );
    stringBuffer.append(TEXT_32);
    
			}

    stringBuffer.append(TEXT_33);
    stringBuffer.append( primaryKeyName );
    stringBuffer.append(TEXT_34);
    stringBuffer.append( fields );
    stringBuffer.append(TEXT_32);
    
			if (operationType.compareTo(CPFConstants.OperationType.LIST) == 0 || operationType.compareTo(CPFConstants.OperationType.VIEW) == 0) {

    stringBuffer.append(TEXT_35);
    stringBuffer.append( from );
    stringBuffer.append(TEXT_36);
    stringBuffer.append( where );
    stringBuffer.append(TEXT_37);
    
			}
			if(operationType.compareTo(CPFConstants.OperationType.LIST) != 0) {

    stringBuffer.append(TEXT_38);
    stringBuffer.append( primaryKeyValue );
    stringBuffer.append(TEXT_39);
    
			} else {
				//Edited on 26th Feb..

    stringBuffer.append(TEXT_40);
    
			}
			}

    
			if(operationType.compareTo(CPFConstants.OperationType.DELETE) == 0) {

    stringBuffer.append(TEXT_41);
    
			}

    stringBuffer.append(TEXT_42);
    
			if(operationType.compareTo(CPFConstants.OperationType.VIEW) != 0) {

    stringBuffer.append(TEXT_43);
    stringBuffer.append( variable4mSF );
    stringBuffer.append(TEXT_44);
    stringBuffer.append( operationType.toString().toLowerCase() );
    stringBuffer.append(TEXT_19);
    stringBuffer.append( arguments );
    stringBuffer.append(TEXT_39);
    
			} else {

    stringBuffer.append(TEXT_43);
    stringBuffer.append( variable4mSF );
    stringBuffer.append(TEXT_45);
    stringBuffer.append( rootEntityName );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( operationType.toString().toLowerCase() );
    stringBuffer.append(TEXT_47);
    stringBuffer.append( arguments );
    stringBuffer.append(TEXT_39);
    
			}

    stringBuffer.append(TEXT_48);
    
		if(operationType.compareTo(CPFConstants.OperationType.VIEW) == 0) {

    stringBuffer.append(TEXT_23);
    stringBuffer.append( returnType );
    stringBuffer.append(TEXT_49);
    stringBuffer.append( returnType );
    stringBuffer.append(TEXT_45);
    stringBuffer.append( variable4mSF.substring(0,variable4mSF.lastIndexOf("=")) );
    stringBuffer.append(TEXT_39);
    
		} else if(operationType.compareTo(CPFConstants.OperationType.LIST) == 0) {

    stringBuffer.append(TEXT_50);
    stringBuffer.append( variable4mSF.substring(0, variable4mSF.lastIndexOf("=")) );
    stringBuffer.append(TEXT_51);
    stringBuffer.append( variable4mSF.substring(0, variable4mSF.lastIndexOf("=")) );
    stringBuffer.append(TEXT_52);
    stringBuffer.append( returnType );
    stringBuffer.append(TEXT_53);
    stringBuffer.append( returnType.substring (0,returnType.lastIndexOf ("[")+1) );
    stringBuffer.append( variable4mSF.substring(0,variable4mSF.lastIndexOf("=")-1) );
    stringBuffer.append(TEXT_54);
    stringBuffer.append( variable4mSF.substring(0,variable4mSF.lastIndexOf("=")-1) );
    stringBuffer.append(TEXT_55);
    stringBuffer.append( variable4mSF.substring(0,variable4mSF.lastIndexOf("=")-1) );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( returnType.substring(0, returnType.lastIndexOf("[")) );
    stringBuffer.append(TEXT_57);
    
		}

    
			if(returnValue) {

    stringBuffer.append(TEXT_58);
    
			if (operationType.compareTo(CPFConstants.OperationType.LIST) == 0) {

    stringBuffer.append(TEXT_59);
    
			} else {

    stringBuffer.append(TEXT_59);
    
			}
 			} 
 			LOG.info("Web Method generation ended...");

    stringBuffer.append(TEXT_60);
    stringBuffer.append( webMethodsMap.get(operationType) );
    stringBuffer.append(TEXT_61);
    
			for (Iterator<Integer> keysRole = operationRoleMap.keySet ().iterator (); keysRole.hasNext ();) {
				Integer id = keysRole.next ();
				String[] roles = operationRoleMap.get (id);
				String condition = new String ();
				for (int i = 0; i < roles.length; i++) {
					condition = condition.concat ("wsCtx.isUserInRole (\"" + roles [i] + "\") ||");
				} 
				condition = condition.substring (0, condition.lastIndexOf (")")+1);

    stringBuffer.append(TEXT_62);
    stringBuffer.append( condition );
    stringBuffer.append(TEXT_63);
    stringBuffer.append( id.intValue () );
    stringBuffer.append(TEXT_64);
    
			}

    stringBuffer.append(TEXT_65);
    
	}  //This is end of for loop of Web Methods and their mappings
	LOG.info("Web Method generation completed from template side.....");

    stringBuffer.append(TEXT_66);
    
	if(baseEntity.isStatic()) {

    stringBuffer.append(TEXT_67);
    
	} else {

    stringBuffer.append(TEXT_68);
    
	}

    stringBuffer.append(TEXT_69);
    
	if(webMethodsMap.keySet().contains(CPFConstants.OperationType.CREATE)) {
		String tempVar = null;
		Map<String, String> dependentPKNames = null;
		if(cpfScreen.getNestedAttributes() != null) {
			dependentPKNames = new HashMap<String, String>();
			Set<RelationKey> nestedRKs = cpfScreen.getNestedAttributes().keySet(); 
			for(Iterator<RelationKey> itrNested = nestedRKs.iterator(); itrNested.hasNext();) {
				RelationKey rk = itrNested.next();
				tempVar = new String();
				tempVar = rk.getReferencedEntity().getPrimaryKey();
				tempVar = tempVar.toUpperCase().charAt(0) + tempVar.substring(1);
				RelationShipInfo ri = rk.getRelationShipInfo();
				dependentPKNames.put(ri.getPropertyName().toUpperCase().charAt(0) + ri.getPropertyName().substring(1)
										, tempVar);
			}
		}
		tempVar = new String();
		tempVar = baseEntity.getPrimaryKey();
		tempVar = tempVar.toUpperCase().charAt(0) + tempVar.substring(1);

    stringBuffer.append(TEXT_70);
    stringBuffer.append( rootEntityName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( tempVar );
    stringBuffer.append(TEXT_72);
    
		if(dependentPKNames != null) {
			for(Iterator<String> itrDpkn = dependentPKNames.keySet().iterator(); itrDpkn.hasNext();){
				String pn = itrDpkn.next();

    stringBuffer.append(TEXT_73);
    stringBuffer.append( pn );
    stringBuffer.append(TEXT_74);
    stringBuffer.append( dependentPKNames.get(pn) );
    stringBuffer.append(TEXT_72);
    
			}
		}

    stringBuffer.append(TEXT_75);
    
	}
	if(webMethodsMap.keySet().contains(CPFConstants.OperationType.LIST)) {

    stringBuffer.append(TEXT_76);
    stringBuffer.append( rootEntityName );
    stringBuffer.append(TEXT_77);
    stringBuffer.append( baseEntity.getPrimaryKey() );
    stringBuffer.append(TEXT_78);
    stringBuffer.append( rootEntityName.toLowerCase().charAt(0) );
    stringBuffer.append( rootEntityName.substring(1) );
    stringBuffer.append(TEXT_79);
    stringBuffer.append( rootEntityName.toLowerCase().charAt(0) );
    stringBuffer.append( rootEntityName.substring(1) );
    stringBuffer.append(TEXT_80);
    
				String tempName = rootEntityName.toLowerCase().charAt(0) + rootEntityName.substring(1);

    stringBuffer.append(TEXT_81);
    stringBuffer.append( tempName );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( rootEntityName );
    stringBuffer.append(TEXT_83);
    
			if(cpfScreen.getNestedAttributes () != null) {
				Set<RelationKey> nested = cpfScreen.getNestedAttributes ().keySet();
				for(Iterator<RelationKey> itrNested = nested.iterator(); itrNested.hasNext();) {
					String relationKeyName = itrNested.next().getRelationShipInfo().getPropertyName();

    stringBuffer.append(TEXT_84);
    stringBuffer.append( relationKeyName );
    stringBuffer.append(TEXT_85);
    stringBuffer.append( relationKeyName );
    stringBuffer.append(TEXT_86);
    stringBuffer.append( relationKeyName );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( relationKeyName );
    stringBuffer.append(TEXT_83);
    
				}	//ENd of for loop
			}	//End if If

    stringBuffer.append(TEXT_87);
    
	}

    stringBuffer.append(TEXT_88);
    stringBuffer.append(TEXT_58);
    return stringBuffer.toString();
  }
}
