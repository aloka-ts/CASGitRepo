package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.*;
import java.util.*;
import java.util.List;
import com.genband.m5.maps.ide.model.util.*;

public class DetailsXhtml
{
  protected static String nl;
  public static synchronized DetailsXhtml create(String lineSeparator)
  {
    nl = lineSeparator;
    DetailsXhtml result = new DetailsXhtml();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<div" + NL + "   xmlns=\"http://www.w3.org/1999/xhtml\"" + NL + "   xmlns:ui=\"http://java.sun.com/jsf/facelets\"" + NL + "   xmlns:h=\"http://java.sun.com/jsf/html\"" + NL + "   xmlns:f=\"http://java.sun.com/jsf/core\"" + NL + "   xmlns:pfc=\"http://www.jboss.com/portal/facelet/common\"" + NL + "   xmlns:c=\"http://java.sun.com/jstl/core\">" + NL + "\t" + NL + "   <!-- <f:loadBundle basename=\"bundle.resources\" var=\"bundle\"/> -->" + NL + "   <f:view locale=\"#{facesContext.externalContext.request.locale}\">" + NL + "\t" + NL + "<h:form id=\"details";
  protected final String TEXT_2 = "\">" + NL + "\t" + NL + "\t<h:inputHidden id=\"mod\" value=\"#{";
  protected final String TEXT_3 = ".uiMode}\"/>" + NL + "   \t<h:message for=\"save\" style=\"color: Red\"/>" + NL + "\t" + NL + "\t<table width=\"100%\">" + NL + "   \t\t<tbody>";
  protected final String TEXT_4 = NL + "\t\t\t<tr class=\"portlet-section-alternate\">" + NL + "\t\t\t\t<th colspan=\"2\" style=\"color:white; background:#869286\">" + NL + "\t\t\t\t\t<h:outputText  value=\"#{bundle.";
  protected final String TEXT_5 = "}\"/>" + NL + "\t\t\t\t</th>" + NL + "\t\t\t</tr>";
  protected final String TEXT_6 = "  \t\t\t" + NL + "   \t\t\t" + NL + "   \t\t\t<tr class=\"portlet-section-body\">" + NL + "            \t<td>" + NL + "            \t\t#{bundle.";
  protected final String TEXT_7 = "}:";
  protected final String TEXT_8 = NL + "\t\t\t\t\t";
  protected final String TEXT_9 = NL + "            \t</td>" + NL + "            \t<td>" + NL + "\t     \t\t\t<c:choose>";
  protected final String TEXT_10 = NL + "\t\t\t\t\t\t<c:when test='${";
  protected final String TEXT_11 = ".uiMode == \"0\"}'>";
  protected final String TEXT_12 = NL + "\t\t\t\t\t\t\t";
  protected final String TEXT_13 = NL + "\t\t\t\t\t\t\t<h:outputText value=\"*\" style=\"color:red\"/>";
  protected final String TEXT_14 = "            \t\t" + NL + "\t\t\t\t\t\t</c:when>";
  protected final String TEXT_15 = ".uiMode == \"1\"}'>";
  protected final String TEXT_16 = NL + "\t\t\t\t\t\t<c:otherwise>";
  protected final String TEXT_17 = NL + "\t\t\t\t\t\t</c:otherwise>";
  protected final String TEXT_18 = NL + "\t    \t\t</c:choose>" + NL + "\t    \t\t<h:outputText value=\" \"/>" + NL + "\t    \t\t<h:message style=\"color: red\" for=\"";
  protected final String TEXT_19 = "\"/>" + NL + "\t    \t</td>" + NL + "         \t</tr>" + NL;
  protected final String TEXT_20 = "  " + NL + " \t\t\t" + NL + "   \t\t\t<tr class=\"portlet-section-body\">" + NL + "            <td align=\"right\" colspan=\"2\">" + NL + "\t\t\t\t<h:panelGroup> " + NL + "\t\t\t\t\t<c:choose>" + NL + "\t\t\t\t\t\t<c:when test='${";
  protected final String TEXT_21 = ".uiMode == \"1\" || ";
  protected final String TEXT_22 = ".uiMode == \"0\"}'>" + NL + "\t\t\t\t\t\t\t<h:commandButton id=\"save\" action=\"viewDetails\" onclick=\"return change(this, 'details";
  protected final String TEXT_23 = "', 'mod', '2');\" value=\"#{bundle.Save}\"/>" + NL + "\t\t\t\t\t\t</c:when>" + NL + "\t\t\t\t\t</c:choose>" + NL + "\t\t\t\t\t<c:choose>" + NL + "\t\t\t\t\t\t<c:when test='${";
  protected final String TEXT_24 = ".uiMode != \"2\"}'>" + NL + "\t\t\t\t\t\t\t<h:commandButton action=\"list\" value=\"#{bundle.Cancel}\" immediate=\"true\"/>" + NL + "\t\t\t\t\t\t</c:when>" + NL + "\t\t\t\t\t\t<c:otherwise>" + NL + "\t\t\t\t\t\t\t<h:commandButton action=\"#{";
  protected final String TEXT_25 = ".saveAction}\" onclick=\"return change(this, 'details";
  protected final String TEXT_26 = "', 'mod', #{";
  protected final String TEXT_27 = ".mode});\" actionListener=\"#{";
  protected final String TEXT_28 = ".saveAction}\" value=\"#{bundle.Ok}\"/>" + NL + "\t\t\t\t\t\t\t<c:choose>" + NL + "\t\t\t\t\t\t\t\t<c:when test='${";
  protected final String TEXT_29 = ".mode != \"2\"}'>" + NL + "\t\t\t\t\t\t\t\t\t<h:commandButton action=\"viewDetails\" onclick=\"return change(this, 'details";
  protected final String TEXT_30 = ".mode});\" value=\"#{bundle.Cancel}\"/>" + NL + "\t\t\t\t\t\t\t\t</c:when>" + NL + "\t\t\t\t\t\t\t</c:choose>" + NL + "\t\t\t\t\t\t</c:otherwise>" + NL + "\t\t\t\t\t</c:choose>" + NL + "\t      \t\t </h:panelGroup>" + NL + "            </td>" + NL + "        \t</tr>" + NL + "   \t\t" + NL + "   \t\t</tbody>" + NL + "   \t</table>" + NL + "</h:form>" + NL + "</f:view> " + NL + "</div>";
  protected final String TEXT_31 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CPFPlugin LOG = CPFPlugin.getDefault();
	CPFResource CPFArgument = (CPFResource)argument; 
 	CPFScreen cpfScreen = (CPFScreen)CPFArgument.getCpfScreen();
 	ModelEntity modelEntity = null;
 	modelEntity = (ModelEntity)cpfScreen.getBaseEntity();
 	List<CPFAttribute> selectedAttributes = null;
 	int operationId = cpfScreen.getPortletRef().getPortletId();						//Holds Operation Id of the method
 	LOG.info("Generating Details Screen for " + modelEntity.getName() 
 				+ " with PortletId : " + operationId);
	Map<RelationKey, List<CPFAttribute>> nestedAttributes = cpfScreen.getNestedAttributes();
	List<CPFConstants.FormatType> formatType = new ArrayList<CPFConstants.FormatType>(); 	//Holds the formatType for each Attribute
	List<String> pattern = new ArrayList<String>();			//Holds the pattern of Format
	List<String> currencySymbol = new ArrayList<String>();	//Holds the currency code in case of format type is of Currecny 
	List<String> validatorTag = new ArrayList<String>();	//Holds the Tag for validators to be added depending upon its category
	
	selectedAttributes = cpfScreen.getSelectedAttributes();
	List<CPFConstants.OperationType> actionsSupported  = cpfScreen.getActionsSupported ();
	List<String> varibleNames = new ArrayList<String>();	//Holds variable names inside DetailsMBean...
	List<List<String>> attrbVisibility = new ArrayList<List<String>>();		//Holds values related to getListVisibility()
	List<CPFConstants.ControlType> controlTypes =  new ArrayList<CPFConstants.ControlType>();					//Holds controlType to be displayed on browser for each CPFAttribute..
	List<Boolean> required = new ArrayList<Boolean>();						//Holds true if field is mandatory else false...
	List<ValidatorData> validators = new ArrayList<ValidatorData>();		//Holds validation constraints for each CPFAttribute
	List<String> labels = new ArrayList<String>();
	List<String> portletUtilInfo = new ArrayList<String>(); 		//Holds information to be passed to PortletUtil as a String
	List<Integer> nestedRelation = new ArrayList<Integer>(); 	//Holds information between entities if CPFAttribute is actually a foreigncolumn
		// -1-For not a foreign column
		//	0-For One2One
		//	1-For One2Many
	Map<String, String> puiSelectedNames = new HashMap<String, String>(); 
	Map<Integer, Integer> positionMap = new HashMap<Integer, Integer>();	//Holds map between position on html page and position here in the list created..
	List<Boolean> primaryDisabled = new ArrayList<Boolean>();				//This is for disabling primaryKey controls on GUI if user selects the primaryKey...
		//Map<Integer, Integer> == Map<HTMLPos, currentListPos>
	//List<String> puVarName = null;	//Holds variable Names for related Strong entities;
	
	String detailsMBeanName = "createMBean"+operationId;	//Holds the name of DetailsMBean name for this Screen or JSF..

    
	int currentListPos = 0;
	//doing process for Variable names in ManagedBean, their control types, and remaining stuff here..................
	LOG.info("Started process over selected Basic attributes to get their control types, Lable names, mandatory or not,");
	LOG.info("format type and its pattern, and validators for each attribute"); 
	for(Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext(); ) { 
 		CPFAttribute selectedAttribute = (CPFAttribute)attributeItr.next();    //For each selected attribute
		ModelAttribute modelAttrib; 
		modelAttrib = selectedAttribute.getModelAttrib();
		String type = null;      //retrieving Attribute data type
		LOG.info("find pos : " + selectedAttribute.getPosition());
		positionMap.put(selectedAttribute.getPosition(), currentListPos++);
		
		if(selectedAttribute.isGroup()) {
			LOG.info("Found " + selectedAttribute.getName() + " as a Group bar..");
			varibleNames.add(null);
			controlTypes.add(null);
				//Changes for Resource Bundle
			String tempAttr = selectedAttribute.getLabel().replaceAll(" ", "_");
			String leftLabel = "D"+operationId+"_"+modelEntity.getName()+"_"+tempAttr;
			labels.add(leftLabel);
				//Changes Over
			//labels.add(selectedAttribute.getLabel());
			required.add(false);
			portletUtilInfo.add(null);
			nestedRelation.add(new Integer(-1));
			formatType.add(null);
			pattern.add(null);
			currencySymbol.add(null);
			validatorTag.add(null);
			primaryDisabled.add(false);
			continue;
		}
		
 		if(selectedAttribute.getForeignColumn () == null) {	//For Basic Attributes....
 			LOG.info("Found " + selectedAttribute.getName() + " as basic attribtue...");
			type = new String(modelAttrib.getCanonicalTypeName());      //retrieving Attribute data type
			if(selectedAttribute.getControlType().equals(CPFConstants.ControlType.TEXTBOX)
				|| selectedAttribute.getControlType().equals(CPFConstants.ControlType.CALENDAR)
				|| selectedAttribute.getControlType().equals(CPFConstants.ControlType.CLOCK)) {
				portletUtilInfo.add(null);
				controlTypes.add(selectedAttribute.getControlType());
			} else {
				LOG.info("This has user defined tagged values..");
				if(selectedAttribute.getTaggedValues() == null 
					|| selectedAttribute.getTaggedValues().size() == 0) {
					LOG.info(selectedAttribute.getName() + " attribute is user defined tagged value...");
					throw new IllegalArgumentException ("You have not entered any 'code and value' values into tagged values " 
													+ "please enter the pair of values and then try for code generation..");
				}
				String pui = new String();
				pui = pui.concat(detailsMBeanName + ".");
				String t = new String();
				t = t.concat("b" + modelAttrib.getName() + "TV");
				pui = pui.concat(t);
				portletUtilInfo.add(pui);
				if(selectedAttribute.getControlType().equals(CPFConstants.ControlType.DROP_DOWN)
					|| selectedAttribute.getControlType().equals(CPFConstants.ControlType.RADIO)) {
					controlTypes.add(selectedAttribute.getControlType());
				} else {
					LOG.warn("You have not entered proper control type for this tagged values attribute '"
								+ selectedAttribute.getModelAttrib().getName() + "' so keeping its control "
									+ "control type to DROP DOWN and continuing the code geenration");
					controlTypes.add(CPFConstants.ControlType.DROP_DOWN);
				}
			}
			nestedRelation.add(new Integer(-1));
		} else {	//For Foreign Column attributes...
 			LOG.info("Found " + selectedAttribute.getName() + " as Foreign attribtue...");
			RelationShipInfo relationShipInfo = selectedAttribute.getModelAttrib().getRelType();
			type = new String(relationShipInfo.getSimpleTypeInfo ());
			if(type.contains("<")) {
				type = type.substring(0, type.lastIndexOf("<")+1) + "Long>";
				nestedRelation.add(new Integer(1));
			} else {
				type = "Long";
				nestedRelation.add(new Integer(0));
			}
			String tempPuName = relationShipInfo.getPropertyName();
			tempPuName = tempPuName + selectedAttribute.getForeignColumn().getName().toUpperCase().charAt(0)
							+ selectedAttribute.getForeignColumn().getName().substring(1);
			portletUtilInfo.add(detailsMBeanName + "." + tempPuName);
			LOG.info(" and display column as : " + selectedAttribute.getForeignColumn().getName());
			
			if(relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne)
				|| relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToOne)) {
				
				if(selectedAttribute.getControlType().equals(CPFConstants.ControlType.CHECKBOX)) {
					controlTypes.add(CPFConstants.ControlType.DROP_DOWN);
					LOG.warn("As selected Foreign Attribute " + modelAttrib.getName() + " has '" 
								+ relationShipInfo.getMapping().toString()
								+ "' and control type you have selected is not supported for that so keeping the" 
									+ " control type as DROP DOWN");
				} else {
					controlTypes.add(selectedAttribute.getControlType());
				}
				String tempSel = new String(relationShipInfo.getPropertyName());
				tempSel = tempSel + selectedAttribute.getForeignColumn ().getName().toUpperCase().charAt(0)
							+ selectedAttribute.getForeignColumn ().getName().substring(1);
				puiSelectedNames.put(relationShipInfo.getPropertyName(), tempSel);
			} else {
				if(selectedAttribute.getControlType().equals(CPFConstants.ControlType.CHECKBOX)
					|| selectedAttribute.getControlType().equals(CPFConstants.ControlType.LIST)) {
					controlTypes.add(selectedAttribute.getControlType());
				} else {
					controlTypes.add(CPFConstants.ControlType.LIST);
					LOG.warn("As selected Foreign Attribute " + modelAttrib.getName() + " has '" 
								+ relationShipInfo.getMapping().toString()
								+ "' and control type you have selected is not supported for that so keeping the" 
									+ " control type as LIST");
				}
			}
			
		}
		
		if (type.equals ("java.sql.Date") || type.equals ("java.sql.Time") || type.equals ("java.sql.Timestamp")) {
			LOG.info("Found selected attribute type as  " + type);
			type = new String ("java.util.Date");
		} else if (type.equals ("java.sql.Blob")) {
			type = new String("byte[]");
		} else if (type.equals ("java.sql.Clob")) {
			type = new String("char[]");
		}
		
		if(selectedAttribute.getRolesException() != null) {
			LOG.info("Some Roles are excepted to view this Atribute...");
 			List<String> rolesList = selectedAttribute.getRolesException().get(CPFConstants.OperationType.VIEW);
 			attrbVisibility.add(rolesList);
 		} else {
 			attrbVisibility.add(null);
 		}
		
		String varName = modelAttrib.getName();
		varibleNames.add (varName);
		varName = varName.toUpperCase ().charAt (0) + varName.substring (1);
		varName = varName.toLowerCase ().charAt (0) + varName.substring (1);
		required.add(modelAttrib.isRequired());
		//controlTypes.add(selectedAttribute.getControlType());
		validators.add(selectedAttribute.getValidatorData());
			//Changes for Resource Bundle
		String tempAttr = selectedAttribute.getLabel().replaceAll(" ", "_");
		String leftLabel = "D"+operationId+"_"+modelEntity.getName()+"_"+tempAttr;
		labels.add(leftLabel);
			//Changes Over
		//labels.add(selectedAttribute.getLabel());
		if(modelAttrib.isPK()) {
			primaryDisabled.add(true);
		} else {
			primaryDisabled.add(false);
		}
			//Getting format type information..
		if(selectedAttribute.getFormatData() != null) {
			LOG.info("format Type is : " + selectedAttribute.getFormatData().getCategory());
			formatType.add(selectedAttribute.getFormatData().getCategory());
				//14th march..
			String pat = selectedAttribute.getFormatData().getPattern();
			if(selectedAttribute.getFormatData().getCategory().equals(CPFConstants.FormatType.INTEGRAL)
				&& pat.contains(".")) {
				pat = pat.substring(0, pat.indexOf("."));
			}
			//pattern.add(selectedAttribute.getFormatData().getPattern());
			pattern.add(pat);
			currencySymbol.add(selectedAttribute.getFormatData().getCurrencySymbol());
			LOG.info("pattern for varName : " + varName + " " + selectedAttribute.getFormatData().getPattern());
		} else {
			LOG.info("For This attribute no format has been set If it is date adding default conversion..");
			if(selectedAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)){
				formatType.add(CPFConstants.FormatType.DATE);
				pattern.add("default");
			} else if(selectedAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
				if(selectedAttribute.getModelAttrib().getCanonicalTypeName().equals(java.sql.Time.class)) {
					formatType.add(CPFConstants.FormatType.TIME);
				} else {
					formatType.add(CPFConstants.FormatType.DATE_TIME);
				}
				pattern.add("default");
			}else {
				formatType.add(null);
				pattern.add(null);
			}
			currencySymbol.add(null);
		}
		
		if(selectedAttribute.getValidatorData() != null) {
			String temp = new String();
			CPFConstants.ValidatorType valType = selectedAttribute.getValidatorData().getCategory();
			if(valType.equals(CPFConstants.ValidatorType.TEXT)) {
				temp = temp .concat("<f:validateLength minimum=\"");
			} else if(valType.equals(CPFConstants.ValidatorType.INTEGRAL)) {
				temp = temp .concat("<f:validateLong minimum=\"");
			} else if(valType.equals(CPFConstants.ValidatorType.NUMERIC)) {
				temp = temp .concat("<f:validateDoubleRange minimum=\"");
			}
			
			if(valType.equals(CPFConstants.ValidatorType.DATE) 
				|| valType.equals(CPFConstants.ValidatorType.TIME) 
					|| valType.equals(CPFConstants.ValidatorType.DATE_TIME)) {
						validatorTag.add(null);
			} else {
				temp = temp.concat(selectedAttribute.getValidatorData().getMinLimit());
				temp = temp.concat("\" maximum=\"");
				temp = temp.concat(selectedAttribute.getValidatorData().getMaxLimit());
				temp = temp.concat("\"/>");
				validatorTag.add(temp);
			}
		} else {
			LOG.info("For This attribute no Validation has been set and adding default one depending upon data tyep..");
			validatorTag.add(null);
		}
	}
	
			//Doing Process for Dependent here........
	if(nestedAttributes != null) {
	Set<RelationKey> selectedOtherEntities = nestedAttributes.keySet();
	for (Iterator<RelationKey> itr = selectedOtherEntities.iterator(); itr.hasNext(); ) {
		RelationKey relationKey = itr.next();
		String otherModelEntityProp = relationKey.getRelationShipInfo().getPropertyName();
		List<CPFAttribute> selectedOtherAttributes = nestedAttributes.get (relationKey);
		LOG.info("Started process over selected " + otherModelEntityProp + " attributes to get their control types, Lable names, mandatory or not,");
		LOG.info("format type and its pattern, and validators for each attribute"); 
		
		for (Iterator<CPFAttribute> itrAttribute = selectedOtherAttributes.iterator(); itrAttribute.hasNext();) {
			CPFAttribute selectedAttribute = (CPFAttribute) itrAttribute.next ();
			ModelAttribute modelAttrib; 
			String type = null;
			String varName = null;
			modelAttrib = selectedAttribute.getModelAttrib();
			varName = modelAttrib.getName();
			positionMap.put(selectedAttribute.getPosition(), currentListPos++);
			
			if(selectedAttribute.getForeignColumn () == null) {
				LOG.info("Found " + selectedAttribute.getName() + " as basic attribtue...");
				type = new String(modelAttrib.getCanonicalTypeName());      //retrieving Attribute data type 
				varName = varName.toUpperCase ().charAt (0) + varName.substring (1);
				String temp = otherModelEntityProp;
				temp = temp.toLowerCase().charAt(0) + temp.substring(1);
				varName = temp + varName;
					//Edited on 7th March..
				if(selectedAttribute.getControlType().equals(CPFConstants.ControlType.TEXTBOX)
					|| selectedAttribute.getControlType().equals(CPFConstants.ControlType.CALENDAR)
					|| selectedAttribute.getControlType().equals(CPFConstants.ControlType.CLOCK)) {
					portletUtilInfo.add(null);
					controlTypes.add(selectedAttribute.getControlType());
				} else {
					LOG.info("This has user defined tagged values..");
					if(selectedAttribute.getTaggedValues() == null 
						|| selectedAttribute.getTaggedValues().size() == 0) {
						LOG.info(selectedAttribute.getName() + " attribute is user defined tagged value...");
						throw new IllegalArgumentException ("You have not entered any 'code and value' values into tagged values " 
													+ "please enter the pair of values and then try for code generation..");
					}
					String pui = new String();
					pui = pui.concat(detailsMBeanName + ".");
					String t = new String();
					t = t.concat("d" + modelAttrib.getName() + "TV");
					pui = pui.concat(t);
					portletUtilInfo.add(pui);
					//controlTypes.add(selectedAttribute.getControlType());
					if(selectedAttribute.getControlType().equals(CPFConstants.ControlType.DROP_DOWN)
						|| selectedAttribute.getControlType().equals(CPFConstants.ControlType.RADIO)) {
						controlTypes.add(selectedAttribute.getControlType());
					} else {
						LOG.warn("You have not entered proper control type for this tagged values attribute '"
									+ selectedAttribute.getModelAttrib().getName() + "' so keeping its control "
									+ "control type to DROP DOWN and continuing the code geenration");
						controlTypes.add(CPFConstants.ControlType.DROP_DOWN);
					}
				}
				nestedRelation.add(new Integer(-1));
			} else {
 				LOG.info("Found " + selectedAttribute.getName() + " as Foreign attribtue...");
				RelationShipInfo innerRelationShipInfo = modelAttrib.getRelType();
				type = new String(innerRelationShipInfo.getSimpleTypeInfo ());
				if(type.contains("<")) {
					type = type.substring(0, type.lastIndexOf("<")+1) + "Long>";
					nestedRelation.add(new Integer(1));
				} else {
					type = "Long";
					nestedRelation.add(new Integer(0));
				}
				String tempPuName = innerRelationShipInfo.getPropertyName();
				tempPuName = tempPuName.toUpperCase().charAt(0) + tempPuName.substring(1);
				tempPuName = otherModelEntityProp + tempPuName 
								+ selectedAttribute.getForeignColumn().getName().toUpperCase().charAt(0)
								+ selectedAttribute.getForeignColumn().getName().substring(1);
				portletUtilInfo.add(detailsMBeanName + "." + tempPuName);
				LOG.info(" and display column as : " + selectedAttribute.getForeignColumn().getName());
				
				if(innerRelationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne)
					|| innerRelationShipInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToOne)) {
				
					if(selectedAttribute.getControlType().equals(CPFConstants.ControlType.CHECKBOX)) {
						controlTypes.add(CPFConstants.ControlType.DROP_DOWN);
						LOG.warn("As selected Foreign Attribute " + modelAttrib.getName() + " has '" 
									+ innerRelationShipInfo.getMapping().toString()
									+ "' and control type you have selected is not supported for that so keeping the" 
										+ " control type as DROP DOWN");
					} else {
						controlTypes.add(selectedAttribute.getControlType());
					}
					String tempSel = new String(innerRelationShipInfo.getPropertyName());
					tempSel = tempSel + selectedAttribute.getForeignColumn ().getName().toUpperCase().charAt(0)
							+ selectedAttribute.getForeignColumn ().getName().substring(1);
					puiSelectedNames.put(innerRelationShipInfo.getPropertyName(), tempSel);
				} else {
					if(selectedAttribute.getControlType().equals(CPFConstants.ControlType.CHECKBOX)
						|| selectedAttribute.getControlType().equals(CPFConstants.ControlType.LIST)) {
						controlTypes.add(selectedAttribute.getControlType());
					} else {
						controlTypes.add(CPFConstants.ControlType.LIST);
						LOG.warn("As selected Foreign Attribute " + modelAttrib.getName() + " has '" 
									+ innerRelationShipInfo.getMapping().toString()
									+ "' and control type you have selected is not supported for that so keeping the" 
										+ " control type as LIST");
					}
				}
			}
			
			if (type.equals ("java.sql.Date") || type.equals ("java.sql.Time") || type.equals ("java.sql.Timestamp")) {
				LOG.info("Found selected attribute type as  " + type);
				type = new String ("java.util.Date");
			}else if (type.equals ("java.sql.Blob")) {
				type = new String ("Byte[]");
			} else if (type.equals ("java.sql.Clob")) {
				type = new String ("Char[]");
			}
			
			if(selectedAttribute.getRolesException() != null) {
				LOG.info("Some Roles are excepted to view this Atribute...");
				List<String> rolesList = selectedAttribute.getRolesException().get(CPFConstants.OperationType.VIEW);
 				attrbVisibility.add(rolesList);
 			} else {
 				attrbVisibility.add(null);
 			}
 				//Added on 6th March..
			if(modelAttrib.isPK()) {
				primaryDisabled.add(true);
			} else {
				primaryDisabled.add(false);
			}
			varibleNames.add (varName);	
			varName = varName.toUpperCase ().charAt (0) + varName.substring (1);
			required.add(modelAttrib.isRequired());
			//controlTypes.add(selectedAttribute.getControlType());
			validators.add(selectedAttribute.getValidatorData());
			//////////////////////
				//Changes for Resource Bundle
			String depTempAttr = selectedAttribute.getLabel().replaceAll(" ", "_");
			String depLeftLabel = "D"+operationId+"_"+relationKey.getRelationShipInfo().getPropertyName()+"_"+depTempAttr;
			labels.add(depLeftLabel);
				//Changes Over
			//labels.add(selectedAttribute.getLabel());
			if(selectedAttribute.getFormatData() != null) {
				LOG.info("format Type is : " + selectedAttribute.getFormatData().getCategory());
				formatType.add(selectedAttribute.getFormatData().getCategory());
					//14th March..
				String pat = selectedAttribute.getFormatData().getPattern();
				if(selectedAttribute.getFormatData().getCategory().equals(CPFConstants.FormatType.INTEGRAL)
					&& pat.contains(".")) {
					pat = pat.substring(0, pat.indexOf("."));
				}
				//pattern.add(selectedAttribute.getFormatData().getPattern());
				pattern.add(pat);
				currencySymbol.add(selectedAttribute.getFormatData().getCurrencySymbol());
			} else {
				LOG.info("For This attribute no format has been set");
				if(selectedAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)){
					formatType.add(CPFConstants.FormatType.DATE);
					pattern.add("default");
				} else if(selectedAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
					if(selectedAttribute.getModelAttrib().getCanonicalTypeName().equals(java.sql.Time.class)) {
						formatType.add(CPFConstants.FormatType.TIME);
					} else {
						formatType.add(CPFConstants.FormatType.DATE_TIME);
					}
					pattern.add("default");
				}else {
					formatType.add(null);
					pattern.add(null);
				}
				currencySymbol.add(null);
			}
			
			if(selectedAttribute.getValidatorData() != null) {
				String temp = new String();
				CPFConstants.ValidatorType valType = selectedAttribute.getValidatorData().getCategory();
				if(valType.equals(CPFConstants.ValidatorType.TEXT)) {
					temp = temp .concat("<f:validateLength minimum=\"");
				} else if(valType.equals(CPFConstants.ValidatorType.INTEGRAL)) {
					temp = temp .concat("<f:validateLong minimum=\"");
				} else if(valType.equals(CPFConstants.ValidatorType.NUMERIC)) {
					temp = temp .concat("<f:validateDoubleRange minimum=\"");
				}
			
				if(valType.equals(CPFConstants.ValidatorType.DATE) 
					|| valType.equals(CPFConstants.ValidatorType.TIME) 
						|| valType.equals(CPFConstants.ValidatorType.DATE_TIME)) {
							validatorTag.add(null);
				} else {
					temp = temp.concat(selectedAttribute.getValidatorData().getMinLimit());
					temp = temp.concat("\" maximum=\"");
					temp = temp.concat(selectedAttribute.getValidatorData().getMaxLimit());
					temp = temp.concat("\"/>");
					validatorTag.add(temp);
				}
			} else {
				LOG.info("For This attribute no Validation has been set");
				validatorTag.add(null);
			}
		}
	}
	}
	//End of doing process for Variable declaration, setters, getters and DataTypes here...............
	LOG.info("Started Generating XHTML.....");
	LOG.info("Adding DIV tag to the XHTMl.....");

    stringBuffer.append(TEXT_1);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_2);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_3);
    
	LOG.info("form Tag has been added.....");
		//Adding Controls for each selected attribute starts here.......
	LOG.info("Preparing all format tags to append those to XHTML.....");

	LOG.info("Size is : " + positionMap.size() + " " + currentListPos);
	for(int htmlPos = 0; htmlPos < positionMap.size(); htmlPos++) {
		int one2Many = nestedRelation.get(positionMap.get(htmlPos));
		LOG.info("nested relation found for html att at pos " + positionMap.get(htmlPos) + " is : " + one2Many);
		String pui = portletUtilInfo.get(positionMap.get(htmlPos));
		String variableName = varibleNames.get(positionMap.get(htmlPos));
		CPFConstants.ControlType ct = controlTypes.get(positionMap.get(htmlPos));
		String labelName = labels.get(positionMap.get(htmlPos));
		boolean req = required.get(positionMap.get(htmlPos));
		CPFConstants.FormatType format = formatType.get(positionMap.get(htmlPos));
		String pat = pattern.get(positionMap.get(htmlPos));
		String currencySym = currencySymbol.get(positionMap.get(htmlPos));
		String validator = validatorTag.get(positionMap.get(htmlPos));
		boolean disabled = primaryDisabled.get(positionMap.get(htmlPos));
		List<String> htmlCode = new ArrayList<String>();			//For Create and Modify Action
		List<String> viewHtmlCode = new ArrayList<String>();		//For View Action
		String displayExFormat = null;	//This is for only Date and time pattern to display rite now....
		
		String formatTag =  null;
		if(one2Many == -1) {
		if(format != null && !format.equals(CPFConstants.FormatType.TEXT)) {
			formatTag = new String();
			if(format.equals(CPFConstants.FormatType.NUMERIC) || format.equals(CPFConstants.FormatType.CURRENCY)
				|| format.equals(CPFConstants.FormatType.INTEGRAL)) {
				if(format.equals(CPFConstants.FormatType.CURRENCY)) {
					if(currencySym.equals(pat.charAt(0))) {
						pat = pat.substring(1);
					} else if(currencySym.equals(pat.substring(0, 3))) {
						pat = pat.substring(3);
					}
				}
				//if(pat != null) { 
					formatTag = formatTag.concat("	<f:convertNumber pattern=\"");
				//} else {
				//	pat = new String();
				//	formatTag = formatTag.concat("	<f:convertNumber");
				//}
			} else {
				
				if(pat.equals("default")) {
					pat = new String();
					if(format.equals(CPFConstants.FormatType.DATE)) {
						formatTag = formatTag.concat("	<f:convertDateTime type=\"date");
						displayExFormat = new String("Ex : Jan 3, 2005");
					} else if(format.equals(CPFConstants.FormatType.TIME)) {
						formatTag = formatTag.concat("	<f:convertDateTime type=\"time");
						displayExFormat = new String("Ex : 8:03:34 PM");
					} else {
						formatTag = formatTag.concat("	<f:convertDateTime type=\"both");
						displayExFormat = new String("Ex : Jan 3, 2005 8:03:34 PM");
					}
				} else if(pat.equals("Short") || pat.equals("Medium") || pat.equals("Long")
						|| pat.equals("Full")) {
						
					pat = pat.toLowerCase().charAt(0) + pat.substring(1);
					if(format.equals(CPFConstants.FormatType.DATE)) {
						formatTag = formatTag.concat("	<f:convertDateTime dateStyle=\"");
						if(pat.equals("short")) {
							displayExFormat = new String("Ex : 1/3/05 (MM/DD/YY)");
						} else if(pat.equals("medium")){
							displayExFormat = new String("Ex : Jan 3, 2005");
						} else if(pat.equals("long")) {
							 displayExFormat = new String("Ex : January 3, 2005"); 	 
						} else {
							displayExFormat = new String("Ex : Monday, January 3, 2005"); 
						}
					} else if(format.equals(CPFConstants.FormatType.TIME)) {
						formatTag = formatTag.concat("	<f:convertDateTime timeStyle=\"");
						if(pat.equals("short")) {
							displayExFormat = new String("Ex : 8:01 PM ");
						} else if(pat.equals("medium")){
							displayExFormat = new String("Ex : 8:01:51 PM");
						} else {
							displayExFormat = new String("Ex : 8:01:51 PM EET");
						}
					} else {
						formatTag = formatTag.concat("	<f:convertDateTime timeStyle=\"" + pat + "\" ");
						formatTag = formatTag.concat("dateStyle=\"");
						if(pat.equals("short")) {
							displayExFormat = new String("Ex : 1/3/05 8:03 PM");
						}else if(pat.equals("medium")){
							displayExFormat = new String("Ex : Jan 3, 2005 8:03:34 PM");
						} else if(pat.equals("long")) {
							displayExFormat = new String("Ex : January 3, 2005 8:03:34 PM EET");
						} else {
							displayExFormat = new String("Ex : Monday, January 3, 2005 8:03:34 PM EET");
						}
					}
				} else {
					formatTag = formatTag.concat("	<f:convertDateTime pattern=\"");
					displayExFormat = new String("Ex : " + pat);
				}
			}
			formatTag = formatTag.concat(pat + "\"/>");
		}
		} else { //In case of a foreign entity
			formatTag = new String("<f:convertNumber/>");
		}
		
		if(ct != null && (ct.equals(CPFConstants.ControlType.TEXTBOX)
			|| ct.equals(CPFConstants.ControlType.CALENDAR))){
			String temp = new String();
			String viewTemp = new String();
			
			temp = temp.concat("<h:inputText id=\"" + labelName.replaceAll(" ", "") + "\"");
			viewTemp = viewTemp.concat("<h:outputText id=\"" + labelName.replaceAll(" ", "") + "\"");
			
			temp = temp.concat(" value=\"#{" + detailsMBeanName + "." + variableName + "}\"");
			viewTemp = viewTemp.concat(" value=\"#{" + detailsMBeanName + "." + variableName + "}\"");
			
			temp = temp.concat(" styleClass=\"portlet-form-input-field\"");
			if(req) {
				temp = temp.concat(" required=\"true\"");
			}
			
			if(disabled) {
				temp = temp.concat(" disabled=\"true\"");
			}
			
			temp = temp.concat(">");
			viewTemp = viewTemp.concat(">");
			htmlCode.add(temp);
			viewHtmlCode.add(viewTemp);
			
			if(formatTag != null) {
				if(format.equals(CPFConstants.FormatType.DATE)) {
					htmlCode.add(formatTag);
				}
				viewHtmlCode.add(formatTag);
			}
			if(validator != null) {
				htmlCode.add(validator);
			}
			
			temp = new String("</h:inputText>");
			viewTemp = new String("</h:outputText>"); 
			htmlCode.add(temp);
			if(displayExFormat != null) {
				htmlCode.add("<h:outputText value=\"" + displayExFormat + "\"/>");
			}
			viewHtmlCode.add(viewTemp);
		} else if(ct != null && (ct.equals(CPFConstants.ControlType.DROP_DOWN ) || 
					ct.equals(CPFConstants.ControlType.RADIO) || ct.equals(CPFConstants.ControlType.CHECKBOX))) {
			String temp = new String();
			String viewTemp = new String();
			
			if(ct.equals(CPFConstants.ControlType.DROP_DOWN )) {
				temp = temp.concat("<h:selectOneMenu id=\"" + labelName.replaceAll(" ", "") + "\"");
			} else if(ct.equals(CPFConstants.ControlType.RADIO)) {
				temp = temp.concat("<h:selectOneRadio id=\"" + labelName.replaceAll(" ", "") + "\"");
			} else {
				temp = temp.concat("<h:selectManyCheckbox id=\"" + labelName.replaceAll(" ", "") + "\"");
			} 
			viewTemp = viewTemp.concat("<h:outputText id=\"" + labelName.replaceAll(" ", "") + "\"");
			
			temp = temp.concat(" value=\"#{" + detailsMBeanName + "." + variableName + "}\""); 
			//viewTemp = viewTemp.concat(" value=\"#{" + detailsMBeanName + "." + variableName + "}\"");
			//viewTemp = viewTemp.concat(" value=\"#{" + detailsMBeanName + "." + puiSelectedNames.get(variableName)
				//TODO had to edit for check box...
			//if(ct.equals(CPFConstants.ControlType.CHECKBOX)) {
			//} 
			viewTemp = viewTemp.concat(" value=\"#{" + pui
										+ "[" + detailsMBeanName + ".selected" + variableName.toUpperCase().charAt(0)
										+ variableName.substring(1) + "].label" + "}\"");
			temp = temp.concat(" styleClass=\"portlet-form-input-field\"");
			if(req) {
				temp = temp.concat(" required=\"true\"");
			}
			
			temp = temp.concat(">");
			viewTemp = viewTemp.concat(">");
			htmlCode.add(temp);
				//Edited on 9th March..
			if(ct.equals(CPFConstants.ControlType.CHECKBOX)) {	//for check box only..
				viewHtmlCode.add(temp);
			} else {
				viewHtmlCode.add(viewTemp);
			}
			
			temp = new String();
			if(one2Many != -1)	{
				temp = temp.concat("	<f:selectItems value=\"#{" + pui + "}\"/>");
			} else {	
				temp = temp.concat("	<f:selectItems value=\"#{" + pui + "}\"/>");	
			}
			htmlCode.add(temp);
				//Edited on 9th March...
			if(ct.equals(CPFConstants.ControlType.CHECKBOX)) {	//for check box only..
				viewHtmlCode.add(temp);
			} 
			
			if(one2Many != -1 && formatTag != null) {
				htmlCode.add(formatTag);
				//viewHtmlCode.add(formatTag);
			}
			if(validator != null) {
				htmlCode.add(validator);
			}
			
			temp = new String();
			if(ct.equals(CPFConstants.ControlType.DROP_DOWN )) {
	    		temp = temp.concat("</h:selectOneMenu>");
	    	} else if(ct.equals(CPFConstants.ControlType.RADIO)) {
	    		temp = temp.concat("</h:selectOneRadio>");
	    	} else {
	    		temp = temp.concat("</h:selectManyCheckbox>");
	    	}
	    	viewTemp = new String("</h:outputText>"); 
	    	htmlCode.add(temp);
	    		//Edited on 9th March..
	    	if(ct.equals(CPFConstants.ControlType.CHECKBOX)) {	//for check box only..
				viewHtmlCode.add(temp);
			} else {
				viewHtmlCode.add(viewTemp);
			}
		} else if(ct != null && ct.equals(CPFConstants.ControlType.LIST)){
			String temp = new String();
			String viewTemp = new String();
			
			if(one2Many != 1) {
				temp = temp.concat("<h:selectOneListbox id=\"" + labelName.replaceAll(" ", "") + "\"");
				viewTemp = viewTemp.concat("<h:outputText id=\"" + labelName.replaceAll(" ", "") + "\"");
				
				temp = temp.concat(" value=\"#{" + detailsMBeanName + "." + variableName + "}\""); 
				viewTemp = viewTemp.concat(" value=\"#{" + detailsMBeanName + "." + variableName + "}\"");
				
				temp = temp.concat(" styleClass=\"portlet-form-input-field\"");
				if(req) {
					temp = temp.concat(" required=\"true\"");
				}
			
				temp = temp.concat(">");
				viewTemp = viewTemp.concat(">");
				htmlCode.add(temp);
				viewHtmlCode.add(viewTemp);
				
				temp = new String();
				if(one2Many != -1)	{
					temp = temp.concat("	<f:selectItems value=\"#{" + pui + "}\"/>");
				} else {
					temp = temp.concat("	<f:selectItems value=\"#{" + pui + "}\"/>");	//TODO HAS TO EDIT SOMETHING
				}
				htmlCode.add(temp);
				
				if(formatTag != null) {
					htmlCode.add(formatTag);
					viewHtmlCode.add(formatTag);
				}
				if(validator != null) {
					htmlCode.add(validator);
				}
				
				temp = new String();
	    		temp = temp.concat("</h:selectOneListbox>");
	    		htmlCode.add(temp);
	    		viewTemp = new String("</h:outputText>"); 
	    		viewHtmlCode.add(viewTemp);
			} else {
				temp = temp.concat("<h:selectManyListbox id=\"" + labelName.replaceAll(" ", "") + "\"");
				viewTemp = viewTemp.concat("<h:selectManyListbox id=\"" + labelName.replaceAll(" ", "") + "\"");
					
				temp = temp.concat(" value=\"#{" + detailsMBeanName + "." + variableName + "}\"  size=\"4\""); 
				viewTemp = viewTemp.concat(" value=\"#{" + detailsMBeanName + "." + variableName + "}\"  size=\"4\"");
				
				temp = temp.concat(" styleClass=\"portlet-form-input-field\"");
				if(req) {
					temp = temp.concat(" required=\"true\"");
				}
			
				temp = temp.concat(">");
				//viewTemp = viewTemp.concat(" disabled=\"true\" >");
				viewTemp = viewTemp.concat(" style=\"background:#F6F6F6\" >");
				htmlCode.add(temp);
				viewHtmlCode.add(viewTemp);
				
				temp = new String();
				viewTemp = new String();
				temp = temp.concat("	<f:selectItems value=\"#{" + pui + "}\"/>");
				viewTemp = viewTemp.concat("	<f:selectItems value=\"#{" + pui + "}\"/>");
				htmlCode.add(temp);
				viewHtmlCode.add(viewTemp);
				
				if(formatTag != null) {
					htmlCode.add(formatTag);
					viewHtmlCode.add(formatTag);
				}
				if(validator != null) {
					htmlCode.add(validator);
				}
				
				temp = new String();
				viewTemp = new String();
	    		temp = temp.concat("</h:selectManyListbox>");
	    		viewTemp = viewTemp.concat("</h:selectManyListbox>");
	    		htmlCode.add(temp);
	    		viewHtmlCode.add(viewTemp);
						//TODO HAS EDIT SOMETHING
			}
		}
			//This is for Group Bar purpose
		if(ct == null && variableName == null) {

    stringBuffer.append(TEXT_4);
    stringBuffer.append( labelName.replaceAll(" ", "_") );
    stringBuffer.append(TEXT_5);
    
			continue;
		}
		LOG.info("Adding row Entry in XHTML table for Label : " + labelName);

    stringBuffer.append(TEXT_6);
    stringBuffer.append( labelName.replaceAll(" ", "_") );
    stringBuffer.append(TEXT_7);
    
				if(currencySym != null) {

    stringBuffer.append(TEXT_8);
    stringBuffer.append( "  " + currencySym );
    
				}

    stringBuffer.append(TEXT_9);
    
					if(actionsSupported.contains(CPFConstants.OperationType.CREATE)) {

    stringBuffer.append(TEXT_10);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_11);
    
						Iterator<String> temp = htmlCode.iterator();
						while(temp.hasNext()) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append( temp.next() );
    
						}
						if(req) {

    stringBuffer.append(TEXT_13);
    
						}

    stringBuffer.append(TEXT_14);
    
					} 
					if (actionsSupported.contains(CPFConstants.OperationType.MODIFY)) {

    stringBuffer.append(TEXT_10);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_15);
    
						Iterator<String> temp = htmlCode.iterator();
						while(temp.hasNext()) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append( temp.next() );
    
						}
						if(req) {

    stringBuffer.append(TEXT_13);
    
						}

    stringBuffer.append(TEXT_14);
    
					}
					if (actionsSupported.contains(CPFConstants.OperationType.VIEW)) {

    stringBuffer.append(TEXT_16);
    
						Iterator<String> viewTemp = viewHtmlCode.iterator();
						while(viewTemp.hasNext()) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append( viewTemp.next() );
    
						}

    stringBuffer.append(TEXT_17);
    
					}

    stringBuffer.append(TEXT_18);
    stringBuffer.append( labelName.replaceAll(" ", "") );
    stringBuffer.append(TEXT_19);
    
	}
	//Adding Controls for each selected attribute ends here.......
	LOG.info("All controls of selected attribtues have been added.....");

    stringBuffer.append(TEXT_20);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_21);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_22);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_23);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_24);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_27);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_29);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( detailsMBeanName );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(TEXT_31);
    return stringBuffer.toString();
  }
}
