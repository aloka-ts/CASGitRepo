package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.*;
import java.util.*;
import java.util.List;
import com.genband.m5.maps.ide.model.util.*;

public class ListXhtml
{
  protected static String nl;
  public static synchronized ListXhtml create(String lineSeparator)
  {
    nl = lineSeparator;
    ListXhtml result = new ListXhtml();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = " ";
  protected final String TEXT_2 = NL + "<div" + NL + "   xmlns=\"http://www.w3.org/1999/xhtml\"" + NL + "   xmlns:ui=\"http://java.sun.com/jsf/facelets\"" + NL + "   xmlns:h=\"http://java.sun.com/jsf/html\"" + NL + "   xmlns:f=\"http://java.sun.com/jsf/core\"" + NL + "   xmlns:pfc=\"http://www.jboss.com/portal/facelet/common\"" + NL + "   xmlns:c=\"http://java.sun.com/jstl/core\">" + NL + "   " + NL + " <!--  <f:loadBundle basename=\"bundle.resources\" var=\"bundle\"/> -->" + NL + " \t<f:view locale=\"#{facesContext.externalContext.request.locale}\">" + NL + "\t";
  protected final String TEXT_3 = NL + "\t<h:form id=\"SPList";
  protected final String TEXT_4 = "\">" + NL + "\t\t<table width=\"50%\">" + NL + "\t\t\t<h:panelGroup>" + NL + "\t\t\t<tr colspan=\"2\">" + NL + "\t\t\t\t\t<td>" + NL + "\t\t\t\t\t\t<h:outputText value=\"#{bundle.Search}:  \" styleClass=\"portlet-msg-info-m-contentText\" style=\"font-weight: bold\"/>" + NL + "\t\t\t\t\t" + NL + "\t\t\t\t\t\t<h:selectBooleanCheckbox id=\"SC\" value=\"#{";
  protected final String TEXT_5 = ".searechCaseSensitive}\"/>" + NL + "\t\t\t\t\t\t<h:outputLabel for=\"SC\" rendered=\"true\">" + NL + "\t\t\t\t\t\t\t<h:outputText value=\"#{bundle.Case_Sensitive}\"/>" + NL + "\t\t\t\t\t\t</h:outputLabel> " + NL + "\t\t\t\t\t</td>" + NL + "\t\t\t</tr>" + NL + "\t\t\t<tr>" + NL + "\t\t\t\t<td>" + NL + "\t\t\t\t\t<h:selectOneMenu value=\"#{";
  protected final String TEXT_6 = ".searchKey}\" onchange=\"submit()\" immediate=\"true\">" + NL + "\t\t\t\t\t\t<f:selectItems value=\"#{";
  protected final String TEXT_7 = ".attbs}\" />" + NL + "\t\t\t\t\t</h:selectOneMenu>" + NL + "\t\t\t\t" + NL + "\t\t\t\t\t<h:selectOneMenu id=\"Operator\" value=\"#{";
  protected final String TEXT_8 = ".searchOperator}\">" + NL + "\t\t\t\t\t\t<f:selectItem value=\"#{";
  protected final String TEXT_9 = ".operators}\"/>" + NL + "\t\t\t\t\t</h:selectOneMenu>" + NL + "\t\t\t\t" + NL + "\t\t\t\t\t<h:inputText id=\"value\" value=\"#{";
  protected final String TEXT_10 = ".searchValue}\">" + NL + "\t\t\t\t\t\t<c:choose>";
  protected final String TEXT_11 = NL + "\t\t\t\t\t\t\t<c:when test=\"${";
  protected final String TEXT_12 = ".searchKey == '";
  protected final String TEXT_13 = "'}\">";
  protected final String TEXT_14 = NL + "\t\t\t\t\t\t\t\t";
  protected final String TEXT_15 = NL + "\t\t\t\t\t\t\t</c:when>";
  protected final String TEXT_16 = NL + "\t\t\t\t\t\t</c:choose>" + NL + "\t\t\t\t\t</h:inputText>" + NL + "\t\t\t\t" + NL + "\t\t\t\t\t<h:commandButton action=\"#{";
  protected final String TEXT_17 = ".search}\" value=\"#{bundle.Search}\"/>" + NL + "\t\t\t\t</td>" + NL + "\t\t\t</tr>" + NL + "\t\t\t</h:panelGroup>" + NL + "\t\t</table>" + NL + "\t</h:form>";
  protected final String TEXT_18 = "\t" + NL + "\t<h:form id=\"RPList";
  protected final String TEXT_19 = "\">" + NL + "\t\t<c:set var=\"listing\" value=\"${";
  protected final String TEXT_20 = ".collec}\"/>" + NL + "\t\t<c:if test=\"${listing == null}\">" + NL + "\t\t\t<br/>" + NL + "\t\t\t<h:outputText value=\"You Have No Data Rite Now\"/>" + NL + "\t\t</c:if>" + NL + "\t\t<c:if test=\"${listing != null}\">" + NL + "\t\t<br/>" + NL + "\t\t" + NL + "\t\t<!-- for displaying acks for actions taken especially create, modify and delete -->" + NL + "\t\t<c:choose>" + NL + "\t\t\t<c:when test='${";
  protected final String TEXT_21 = ".delStatus == \"1\"}'>" + NL + "\t\t\t\t<h:message for=\"delete\" style=\"color: Blue\"/>" + NL + "\t\t\t</c:when>" + NL + "\t\t\t<c:when test='${";
  protected final String TEXT_22 = ".delStatus == \"0\"}'>" + NL + "\t\t\t\t<h:message for=\"delete\" style=\"color: Red\"/>" + NL + "\t\t\t</c:when>" + NL + "\t\t</c:choose>" + NL + "    \t<h:message for=\"add\" style=\"color: Blue\"/>" + NL + "    \t<h:message for=\"modify\" style=\"color: Blue\"/>" + NL;
  protected final String TEXT_23 = NL + "\t\t<table width=\"100%\">";
  protected final String TEXT_24 = NL + "\t\t\t<thead class=\"portlet-section-header\">";
  protected final String TEXT_25 = NL + "\t\t\t\t<th>" + NL + "\t\t\t\t\t<h:outputText value=\" \"/>" + NL + "\t  \t\t\t</th>";
  protected final String TEXT_26 = NL + "\t\t\t\t<th align=\"center\">" + NL + "\t\t\t\t\t<h:outputText value=\"#{bundle.";
  protected final String TEXT_27 = "}\"/>" + NL + "\t \t\t\t</th>";
  protected final String TEXT_28 = NL + "\t\t\t\t<c:choose>" + NL + "\t\t\t\t  \t<c:when test=\"${";
  protected final String TEXT_29 = ".listVisibility[";
  protected final String TEXT_30 = "]}\">" + NL + "   \t\t\t\t       \t<th>" + NL + "\t\t\t\t\t\t\t<h:commandLink action=\"#{";
  protected final String TEXT_31 = ".sort}\" value=\"#{bundle.";
  protected final String TEXT_32 = "}\">" + NL + "\t\t\t\t\t\t\t\t<f:param name=\"sortBy\" value=\"";
  protected final String TEXT_33 = "\"/>" + NL + "\t\t\t\t\t\t\t</h:commandLink>" + NL + "\t\t\t\t\t\t\t<c:if test=\"${";
  protected final String TEXT_34 = ".orderBy == '";
  protected final String TEXT_35 = "'}\">" + NL + "\t\t\t\t\t\t\t     <h:outputText escape=\"true\" value=\" \"/>" + NL + "\t\t\t\t\t\t\t     <c:choose>" + NL + "\t\t\t\t\t\t\t\t<c:when test=\"${";
  protected final String TEXT_36 = ".ascending == true}\">" + NL + "\t\t\t\t\t\t\t\t\t<h:graphicImage value=\"/images/nested.jpg\" height=\"12\" style=\"border:none\" alt=\"Ascending\"/>" + NL + "\t\t\t\t\t\t\t\t</c:when>\t\t\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t\t\t<c:when test=\"${";
  protected final String TEXT_37 = ".ascending != true}\">" + NL + "\t\t\t\t\t\t\t\t\t<h:graphicImage value=\"/images/parent.jpg\" height=\"12\" style=\"border:none\" alt=\"Descending\"/>" + NL + "\t\t\t\t\t\t\t\t</c:when>" + NL + "\t\t\t\t\t\t\t     </c:choose>" + NL + "\t\t\t\t\t\t\t</c:if>" + NL + "\t \t\t\t\t\t</th>" + NL + "\t \t\t\t\t </c:when>" + NL + "\t \t\t\t</c:choose>";
  protected final String TEXT_38 = NL + "\t\t\t\t<th>" + NL + "\t\t\t\t\t<h:outputText value=\"#{bundle.";
  protected final String TEXT_39 = "}\"/>" + NL + "\t\t\t\t</th>";
  protected final String TEXT_40 = NL + "\t\t\t</thead>";
  protected final String TEXT_41 = NL;
  protected final String TEXT_42 = NL + "\t\t\t<tbody>";
  protected final String TEXT_43 = NL + "\t\t\t\t<c:choose>" + NL + "\t    \t\t\t\t<c:when test=\"${";
  protected final String TEXT_44 = ".listVisibility[0]}\">" + NL + "\t\t\t\t\t\t\t<td rowspan=\"";
  protected final String TEXT_45 = "\">" + NL + "\t\t\t\t\t\t\t<h:selectOneRadio value=\"#{";
  protected final String TEXT_46 = ".primaryKeyValue}\" layout=\"pageDirection\" immediate=\"true\">" + NL + "\t\t\t\t\t\t\t\t<f:selectItems value=\"#{";
  protected final String TEXT_47 = ".radio}\"/>" + NL + "\t\t\t\t\t\t\t</h:selectOneRadio>" + NL + "  \t\t\t\t \t\t\t</td>" + NL + "  \t\t\t\t\t\t</c:when>" + NL + "  \t\t\t\t </c:choose>";
  protected final String TEXT_48 = NL + "  \t\t\t\t <td>" + NL + "\t\t\t\t <c:forEach items=\"#{listing}\" var=\"object\" varStatus=\"status\">" + NL + "         \t\t\t<tr class=\"#{status.index % 2 == 0 ? 'portlet-section-body' : 'portlet-section-alternate'}\">";
  protected final String TEXT_49 = NL + "\t\t\t\t \t\t<c:choose>" + NL + "\t    \t\t\t\t<c:when test=\"${";
  protected final String TEXT_50 = "]}\">";
  protected final String TEXT_51 = NL + "\t\t\t\t\t\t\t<td align=\"center\">" + NL + "\t\t\t\t\t\t\t\t<h:commandLink action=\"";
  protected final String TEXT_52 = "\" actionListener=\"#{";
  protected final String TEXT_53 = ".action}\">" + NL + "\t\t\t\t\t\t\t\t\t<h:graphicImage alt=\"Details\" value=\"/images/nested.jpg\" height=\"15\" style=\"border:none\"/>" + NL + "\t\t\t\t\t\t\t\t\t<f:param name=\"pkValue\" value=\"#{object[0]}\"/>" + NL + "\t\t\t\t\t\t\t\t</h:commandLink>" + NL + "\t    \t\t\t\t\t</td>";
  protected final String TEXT_54 = NL + "\t\t\t\t\t\t\t<td>";
  protected final String TEXT_55 = NL + "\t\t\t\t\t\t\t<c:choose>" + NL + "\t\t\t\t\t\t\t\t<c:when test=\"${";
  protected final String TEXT_56 = ".canView == 'true'}\">" + NL + "\t\t \t\t\t\t\t\t <h:commandLink action=\"add\" actionListener=\"#{";
  protected final String TEXT_57 = ".viewAction}\" >" + NL + "\t\t\t\t\t\t\t\t\t<f:attribute name=\"pkValue\" value=\"#{object[0]}\"/>" + NL + "\t\t\t\t\t\t\t\t\t<h:outputText value=\"#{object[";
  protected final String TEXT_58 = "]}\"/>";
  protected final String TEXT_59 = NL + "\t\t\t\t\t\t\t\t<h:outputText value=\"#{object[";
  protected final String TEXT_60 = NL + "\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_61 = NL + "\t\t\t\t\t\t\t\t</h:commandLink>" + NL + "\t\t\t\t\t\t\t   </c:when>" + NL + "\t\t\t\t\t\t\t   <c:otherwise>" + NL + "\t\t\t\t\t\t\t\t\t<h:outputText value=\"#{object[";
  protected final String TEXT_62 = NL + "\t\t\t\t\t\t\t\t\t\t";
  protected final String TEXT_63 = NL + "\t\t\t\t\t\t\t\t\t</h:outputText>" + NL + "\t\t\t\t\t\t\t   </c:otherwise>" + NL + "\t\t\t\t\t\t\t</c:choose>";
  protected final String TEXT_64 = NL + "\t\t\t\t\t\t\t\t</h:outputText>";
  protected final String TEXT_65 = NL + "            \t\t\t\t</td>";
  protected final String TEXT_66 = ".action}\">" + NL + "\t\t\t\t\t\t\t\t\t<!-- <h:outputText escape=\"true\" value=\"DetailsPage\"/> -->" + NL + "\t\t\t\t\t\t\t\t\t<h:graphicImage alt=\"Details\" value=\"/images/nested.jpg\" height=\"15\" style=\"border:none\"/>" + NL + "\t\t\t\t\t\t\t\t\t<f:param name=\"pkValue\" value=\"#{object[0]}\"/>" + NL + "\t\t\t\t\t\t\t\t</h:commandLink>" + NL + "\t    \t\t\t\t\t</td>";
  protected final String TEXT_67 = NL + "\t\t\t\t\t\t\t<td>" + NL + "\t\t \t\t\t\t\t\t<h:outputText value=\"#{object[";
  protected final String TEXT_68 = NL + "\t\t\t\t\t\t\t\t</h:outputText>" + NL + "            \t\t\t\t</td>";
  protected final String TEXT_69 = NL + "\t\t\t\t\t\t</c:when>" + NL + "\t    \t\t\t\t</c:choose>";
  protected final String TEXT_70 = NL + "         \t\t\t</tr>" + NL + "         \t\t</c:forEach>" + NL + "         \t\t</td>" + NL + "\t\t\t</tbody>";
  protected final String TEXT_71 = NL + "\t\t</table>";
  protected final String TEXT_72 = "\t\t" + NL + "\t\t<table width=\"100%\">" + NL + "\t\t\t<tbody>" + NL + "\t \t\t\t<tr class=\"portlet-section-body\">" + NL + "      \t\t      <td align=\"center\" colspan=\"2\">" + NL + "     \t\t          <h:panelGroup> \t\t\t\t" + NL + "\t\t\t\t\t\t<h:commandLink id=\"PREVIOUS\" action=\"previousPage\" disabled=\"#{";
  protected final String TEXT_73 = ".previousDisabled}\" actionListener=\"#{";
  protected final String TEXT_74 = ".pageChange}\" >" + NL + "\t\t\t\t\t\t\t<h:graphicImage alt=\"Previous\" value=\"/images/prev.jpg\" style=\"border:none\" />" + NL + "\t\t\t\t\t\t\t<h:outputText escape=\"true\" value=\"  \" />" + NL + "\t\t\t\t\t\t</h:commandLink>" + NL + "\t\t\t\t\t\t<h:commandLink id=\"NEXT\" action=\"nextPage\" disabled=\"#{";
  protected final String TEXT_75 = ".nextDisabled}\" actionListener=\"#{";
  protected final String TEXT_76 = ".pageChange}\" >" + NL + "\t\t\t\t\t\t\t<h:graphicImage alt=\"Next\" value=\"/images/next.jpg\" style=\"border:none\" />" + NL + "\t\t\t\t\t\t</h:commandLink>" + NL + "\t     \t\t\t  </h:panelGroup>" + NL + "          \t\t  </td>" + NL + "         \t\t</tr>" + NL + "\t\t\t</tbody>" + NL + "\t\t</table>";
  protected final String TEXT_77 = NL + "\t\t</c:if>";
  protected final String TEXT_78 = NL + "\t\t<h:panelGroup> ";
  protected final String TEXT_79 = NL + "\t\t<!--\t<c:if test=\"${";
  protected final String TEXT_80 = ".canView == 'true'}\"> -->" + NL + "\t\t\t\t<h:commandButton action=\"add\" actionListener=\"#{";
  protected final String TEXT_81 = ".viewAction}\" value=\"#{bundle.View}\" disabled=\"#{!";
  protected final String TEXT_82 = ".canView}\">" + NL + "\t\t\t\t\t<f:attribute name=\"pkValue\" value=\"#{";
  protected final String TEXT_83 = ".primaryKeyValue}\" />" + NL + "\t\t\t\t</h:commandButton>" + NL + "\t\t<!--\t</c:if> -->";
  protected final String TEXT_84 = NL + "\t\t\t<!-- <c:if test=\"${";
  protected final String TEXT_85 = ".canCreate == 'true'}\"> -->" + NL + "\t\t\t\t<h:commandButton id=\"add\" action=\"add\" actionListener=\"#{";
  protected final String TEXT_86 = ".addAction}\" value=\"#{bundle.Add}\" disabled=\"#{!";
  protected final String TEXT_87 = ".canCreate}\" immediate=\"true\"/>" + NL + "\t\t\t<!-- </c:if> -->";
  protected final String TEXT_88 = ".canModify == 'true'}\"> -->" + NL + "\t\t\t\t<h:commandButton id=\"modify\" action=\"add\" actionListener=\"#{";
  protected final String TEXT_89 = ".modifyAction}\" value=\"#{bundle.Modify}\" disabled=\"#{!";
  protected final String TEXT_90 = ".canModify}\">" + NL + "\t\t\t\t\t<f:attribute name=\"pkValue\" value=\"#{";
  protected final String TEXT_91 = ".primaryKeyValue}\" />" + NL + "\t\t\t\t</h:commandButton>" + NL + "\t\t\t<!-- </c:if> -->";
  protected final String TEXT_92 = ".canDelete == 'true'}\"> -->" + NL + "\t\t\t\t<h:commandButton id=\"delete\" action=\"delete\" onclick=\"return delete1();\" actionListener=\"#{";
  protected final String TEXT_93 = ".delete}\" value=\"#{bundle.Delete}\" disabled=\"#{!";
  protected final String TEXT_94 = ".canDelete}\"/>" + NL + "\t\t\t<!-- </c:if> -->";
  protected final String TEXT_95 = NL + "\t\t\t<h:commandLink action=\"resetToMain\" actionListener=\"#{";
  protected final String TEXT_96 = ".r2Parent}\">" + NL + "\t\t\t\t<h:graphicImage height=\"30\" value=\"/images/parent.jpg\" style=\"border:none\" alt=\"Parent List\"/>" + NL + "\t\t\t</h:commandLink>";
  protected final String TEXT_97 = NL + "\t\t\t<c:choose>" + NL + "\t\t\t\t<c:when test=\"${";
  protected final String TEXT_98 = ".criteria.searchDetails.search == 'true'}\">" + NL + "\t\t\t\t\t<h:commandButton  value=\"#{bundle.Reset}\" action=\"#{";
  protected final String TEXT_99 = ".reset}\"/>" + NL + "\t\t\t\t</c:when>" + NL + "\t\t\t</c:choose>";
  protected final String TEXT_100 = NL + "\t\t</h:panelGroup>" + NL + "   \t</h:form>" + NL + "   </f:view>" + NL + "</div>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CPFPlugin LOG = CPFPlugin.getDefault();
	CPFResource CPFArgument = (CPFResource)argument; 
 	CPFScreen cpfScreen = (CPFScreen)CPFArgument.getCpfScreen();
 	ResourceInfo resourceInfo = (ResourceInfo)CPFArgument.getResourceInfo();
 	String resourceName = resourceInfo.getResourceName();
 	LOG.info("Generating list XHTML for : " + resourceName);
 	ModelEntity modelEntity = null;
 	modelEntity = (ModelEntity)cpfScreen.getBaseEntity();
 	ModelEntity baseEntity = cpfScreen.getPortletRef().getBaseEntity();
 	List<CPFAttribute> selectedAttributes = null;					
 	Integer operationId = cpfScreen.getPortletRef().getPortletId();		//Holds Operation Id of the method
 	LOG.info("Generating this for portlet Id : " + operationId);
	Map<RelationKey, List<CPFAttribute>> nestedAttributes = cpfScreen.getNestedAttributes();
	List<CPFConstants.OperationType> actionsSupported  = cpfScreen.getActionsSupported ();
	List<Boolean> nestedLink = new ArrayList<Boolean>();
	Map<RelationKey, String> nestedLinkNames = new HashMap<RelationKey, String>();
	Map<Integer, String> nestedMBeanNames = new HashMap<Integer, String>();		//Holds MBean Names for linking Page and thier Actions in case of OneToMany relation
	List<String> headerNames = new ArrayList<String>();
	
	List<String> formatTag = new ArrayList<String>();		//Holds the JSF code for format tags Null If there is no format selected
	List<String> conversionTag = new ArrayList<String>();	//Holds the tag for conversion in search panel 
	
	List<String> sortValues = null;				//Holds sortBy param values to submit managed bean for sorting as well as for validation in case of Search
	Map<Integer, Integer> positionMap = new HashMap<Integer, Integer>();	//Holds map between position on html page and position here in the list created..
		//Map<Integer, Integer> == Map<HTMLPos, currentListPos>
	Map<Integer, Integer> criteriaPos = new HashMap<Integer, Integer>();
		//Map<Integer, Integer> == Map<HTMLPos, position in Criteria fields>
	
	String listMBeanName = new String("listMBean");	//Holds MBean name for Listing Page
	
	String mainPropertyName = null;
	
	if(CPFArgument.getOperationId() > 0) {
		listMBeanName = listMBeanName.concat(operationId.toString());
		//listMBeanName = listMBeanName.concat("nested" + CPFArgument.getOperationId());
		//listMBeanName = listMBeanName.concat("nested" + CPFArgument.getOperationId());
		listMBeanName = listMBeanName.concat("nested" + CPFArgument.getClassName());
		
		CPFScreen baseScreen = cpfScreen.getPortletRef().getListScreen();
 		Map<RelationKey, CPFScreen> nestedJsp = baseScreen.getNestedJspNames();
 		Iterator<RelationKey> itrRelationKey = nestedJsp.keySet().iterator();
 		while(itrRelationKey.hasNext()) {
 			RelationKey relKey = itrRelationKey.next();
 			CPFScreen thisCpfScreen = nestedJsp.get(relKey);
 			if(thisCpfScreen.getJspName().equals(cpfScreen.getJspName())) {
 				mainPropertyName = new String(relKey.getRelationShipInfo().getPropertyName());
 				LOG.info("The resource is not Base Entity and its property name in Base entiy is : "
 							+ mainPropertyName);
 				break;
 			}
 		}
	} else {
		listMBeanName = listMBeanName.concat(operationId.toString());
	}
	String createMBeanName = new String("createMBean"); 				//Holds MBean name for Details Page
	createMBeanName = createMBeanName.concat(operationId.toString());
		//getting selected attribtues for current resource name	
 	selectedAttributes = cpfScreen.getSelectedAttributes ();
 	
 	if(cpfScreen.getNestedJspNames() != null) {
 		int temp = 1;
 		Iterator<RelationKey> itrNestedJspNames = cpfScreen.getNestedJspNames().keySet().iterator();
 		//Iterator<RelationKey> itrNestedJspNames = cpfScreen.getNestedAttributes().keySet().iterator();
 		while(itrNestedJspNames.hasNext()) {
 			RelationKey tempRK = itrNestedJspNames.next();
 			/*if(tempRK.getRelationShipInfo().getMapping().equals(CPFConstants.RelationshipType.ManyToOne)
					|| tempRK.getRelationShipInfo().getMapping().equals(CPFConstants.RelationshipType.OneToOne)) {
				continue;
			}*/	//Added on 18th April
 			//nestedLinkNames.put(tempRK, "listMBean" + operationId + "nested" + temp++);
 			nestedLinkNames.put(tempRK, "listMBean" + operationId + "nested" + tempRK.getRelationShipInfo().getPropertyName());
 		}	
 	}
 	
 //DOING	process for CPF ATTRIBUTES STARTS HERE............	
		//In case of Sort action is supported....
	LOG.info("Started processing selected Attributes to get information about formatType, headerNames etc...");
	if(actionsSupported.contains(CPFConstants.OperationType.SORT) 
		|| actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {
		sortValues = new ArrayList<String>();
	}
	Iterator<CPFAttribute> itrSelectedAttributes = selectedAttributes.iterator();
	int j = 0;
	int currentListPos = 0;
	int totalNoOfAttbs = selectedAttributes.size();
	int cfp = 0;
	int counterForTest = 0;		//Added on 18th April....
	while(itrSelectedAttributes.hasNext()) {
		CPFAttribute cpfAttribute = itrSelectedAttributes.next();
		LOG.info("X Attb Vis counter : " + counterForTest++);		//Added on 18th Apr
		nestedLink.add(false);
		System.out.println("j: " + j++);
			//Changes for Resource Bundle
		if(currentListPos == 0) {
				headerNames.add(null);
		} else {
				String tempAttr = cpfAttribute.getLabel().replaceAll(" ", "_");
				String leftLabel = null;
				if(CPFArgument.getOperationId() > 0) {
					leftLabel = new String("L"+operationId+"_"+mainPropertyName+"_"+tempAttr);
				} else {
					leftLabel = new String("L"+operationId+"_"+modelEntity.getName()+"_"+tempAttr);
				}
				headerNames.add(leftLabel);
		}
			//Changes Over
		//headerNames.add(cpfAttribute.getLabel());
		LOG.info("find pos : " + cpfAttribute.getPosition());
		if(CPFArgument.getOperationId() > 0) {	//For nested screen
			positionMap.put(currentListPos, currentListPos);
			criteriaPos.put(currentListPos, currentListPos++);
		} else {	//For base entity screen
			positionMap.put(cpfAttribute.getPosition(), currentListPos++);
			criteriaPos.put(cpfAttribute.getPosition(), cfp++);
		}
		if(sortValues != null) {
			String temp = null;
			if(CPFArgument.getOperationId() > 0 && modelEntity.getName().equals(baseEntity.getName())) {
				temp = new String(mainPropertyName);
			} else {
				temp = new String(modelEntity.getName());
			}
			temp = temp.concat(".");
			temp = temp.concat(cpfAttribute.getModelAttrib().getName());
			sortValues.add(temp);
		}
		if(cpfAttribute.getFormatData() != null 
			&& !cpfAttribute.getFormatData().getCategory().equals(CPFConstants.FormatType.TEXT)) {
				CPFConstants.FormatType formatType = cpfAttribute.getFormatData().getCategory();
				String temp = new String(); 
				String convTemp = null;
				if(formatType.equals(CPFConstants.FormatType.NUMERIC) 
					|| formatType.equals(CPFConstants.FormatType.CURRENCY)
						|| formatType.equals(CPFConstants.FormatType.INTEGRAL)) {
						temp = temp.concat("	<f:convertNumber pattern=\"");
							//14th March....
						String tempPat = cpfAttribute.getFormatData().getPattern();
						if(formatType.equals(CPFConstants.FormatType.INTEGRAL) && tempPat.contains(".")) {
							tempPat = tempPat.substring(0, tempPat.indexOf("."));
						}
						temp = temp.concat(tempPat + "\"/>");
						
							//Added this for conversion in search panel....
						if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Long")
							|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("long")) {
							convTemp = new String("		<f:converter converterId=\"javax.faces.Long\"/>");
						} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Integer")
									|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("int")) {
							convTemp = new String("		<f:converter converterId=\"javax.faces.Integer\"/>");
						} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Double") 
									|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("double")) {
							convTemp = new String("		<f:converter converterId=\"javax.faces.Double\"/>");
						} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Float")
									|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("float")) {
							convTemp = new String("		<f:converter converterId=\"javax.faces.Float\"/>");
						} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Boolean")
									|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("boolean")) {
							convTemp = new String("		<f:converter converterId=\"javax.faces.Boolean\"/>");
						} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Byte")
									|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("byte")) {
							convTemp = new String("		<f:converter converterId=\"javax.faces.Byte\"/>");
						} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Character")
									|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("char")) {
							convTemp = new String("		<f:converter converterId=\"javax.faces.Character\"/>");
						} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Short")
									|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("short")) {
							convTemp = new String("		<f:converter converterId=\"javax.faces.Short\"/>");
						}
						//temp = temp.concat(cpfAttribute.getFormatData().getPattern() + "\"/>");
				} else {	//Setting for Date Time and Date_Time
					String tempPat = cpfAttribute.getFormatData().getPattern();
					if(tempPat.equals("Short") || tempPat.equals("Medium") || tempPat.equals("Long")
						|| tempPat.equals("Full")) {
						
						tempPat = tempPat.toLowerCase().charAt(0) + tempPat.substring(1);
						if(formatType.equals(CPFConstants.FormatType.DATE)){
							temp = temp.concat("	<f:convertDateTime ");
							temp = temp.concat("dateStyle=\"");
						} else if(formatType.equals(CPFConstants.FormatType.TIME)) {
								temp = temp.concat("	<f:convertDateTime ");
								temp = temp.concat("timeStyle=\"");
						} else {
								temp = temp.concat("	<f:convertDateTime ");
								temp = temp.concat("timeStyle=\"" + tempPat + "\" ");
								temp = temp.concat("dateStyle=\"");
						}
					} else {
						temp = temp.concat("	<f:convertDateTime pattern=\"");
					}
					temp = temp.concat(tempPat + "\"/>");
					convTemp = new String(temp);
				}
				if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Boolean")
									|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("boolean")) {
					formatTag.add(convTemp);
				} else {
					formatTag.add(temp);
				}
				conversionTag.add(convTemp);
		} else {
			String temp = null;
			String convTemp = null;
			if(cpfAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)){
				temp = new String("		<f:convertDateTime type=\"date\"/>");
				convTemp = new String(temp);
				//formatTag.add(temp);
			} else if(cpfAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
				if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals(java.sql.Time.class)) {
					temp = new String("		<f:convertDateTime type=\"time\"/>");
				} else {
					temp = new String("		<f:convertDateTime type=\"both\"/>");
				}
				convTemp = new String(temp);
				//formatTag.add(temp);
			} else if(cpfAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.NUMERIC)) {
				temp = new String("		<f:convertNumber/>");
					
					//Added this for conversion in search panel....
				if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Long")
					|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("long")) {
					convTemp = new String("		<f:converter converterId=\"javax.faces.Long\"/>");
				} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Integer")
							|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("int")) {
					convTemp = new String("		<f:converter converterId=\"javax.faces.Integer\"/>");
				} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Double") 
							|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("double")) {
					convTemp = new String("		<f:converter converterId=\"javax.faces.Double\"/>");
				} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Float")
							|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("float")) {
					convTemp = new String("		<f:converter converterId=\"javax.faces.Float\"/>");
				} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Boolean")
							|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("boolean")) {
					convTemp = new String("		<f:converter converterId=\"javax.faces.Boolean\"/>");
				} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Byte")
							|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("byte")) {
					convTemp = new String("		<f:converter converterId=\"javax.faces.Byte\"/>");
				} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Character")
							|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("char")) {
					convTemp = new String("		<f:converter converterId=\"javax.faces.Character\"/>");
				} else if(cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Short")
							|| cpfAttribute.getModelAttrib().getCanonicalTypeName().equals("short")) {
					convTemp = new String("		<f:converter converterId=\"javax.faces.Short\"/>");
				}
				//formatTag.add(temp);
			} //else {
				//formatTag.add(null);
				formatTag.add(temp);
				conversionTag.add(convTemp);
			//} 
		}
	}
	
	//if(resourceName.equals(baseEntity.getName()) && nestedAttributes != null) {
	if(CPFArgument.getOperationId() == 0 && nestedAttributes != null) {
		LOG.info("Doingg process for related entities of OneToOne or ManyToOne relationship Entity selected attributes..");
		Iterator<RelationKey> itrOtherEntities = nestedAttributes.keySet().iterator();
		int i = 0;
		while(itrOtherEntities.hasNext()) {
			RelationKey relationKey = itrOtherEntities.next();
			String tempProp = relationKey.getRelationShipInfo().getPropertyName();
			List<CPFAttribute> cpfAttributes = nestedAttributes.get(relationKey);
			ModelEntity tempModelEntity = relationKey.getReferencedEntity();
			CPFConstants.RelationshipType mapping = relationKey.getRelationShipInfo().getMapping();
			if(mapping.equals(CPFConstants.RelationshipType.OneToMany) 
				|| mapping.equals(CPFConstants.RelationshipType.ManyToMany)) {   //IF ONE2MANY RELATIONSHIP EXISTS
				nestedLink.add(true);
				//nestedMBeanNames.put(i++, nestedLinkNames.get(relationKey));
				nestedMBeanNames.put(nestedAttributes.get(relationKey).get(0).getPosition(), nestedLinkNames.get(relationKey));
				headerNames.add(tempProp);
				formatTag.add(null);
				if(sortValues != null) {
					sortValues.add(null);
				}
				conversionTag.add(null);
				LOG.info("find pos : " + nestedAttributes.get(relationKey).get(0).getPosition());
				String jspName = cpfScreen.getNestedJspNames().get(relationKey).getJspName();
				LOG.info("X Attb Vis counter nested relation : " + jspName + " : " + counterForTest++);		//Added on 18th Apr
				positionMap.put(nestedAttributes.get(relationKey).get(0).getPosition(), currentListPos++);
				totalNoOfAttbs += nestedAttributes.get(relationKey).size();
			} else {
				Iterator<CPFAttribute> itrCpfAttributes = cpfAttributes.iterator();
				totalNoOfAttbs += nestedAttributes.get(relationKey).size();
				while(itrCpfAttributes.hasNext()) {
					CPFAttribute otherCpfAttribute = (CPFAttribute)itrCpfAttributes.next();
					LOG.info("X Attb Vis counter : " + counterForTest++);		//Added on 18th Apr
						//Changes for Resource Bundle
					String depTempAttr = otherCpfAttribute.getLabel().replaceAll(" ", "_");
					String depLeftLabel = "L"+operationId+"_"+relationKey.getRelationShipInfo().getPropertyName()+"_"+depTempAttr;
					headerNames.add(depLeftLabel);
						//Changes Over
					//headerNames.add(otherCpfAttribute.getLabel());
					nestedLink.add(false);
					LOG.info("find pos : " + otherCpfAttribute.getPosition());
					positionMap.put(otherCpfAttribute.getPosition(), currentListPos++);
					criteriaPos.put(otherCpfAttribute.getPosition(), cfp++);
					if(sortValues != null) {
						//String temp = new String(tempModelEntity.getName());
						String temp = new String(tempProp);
						temp = temp.concat(".");
						temp = temp.concat(otherCpfAttribute.getModelAttrib().getName());
						sortValues.add(temp);
					}
					if(otherCpfAttribute.getFormatData() != null 
						&& !otherCpfAttribute.getFormatData().getCategory().equals(CPFConstants.FormatType.TEXT)) {
							CPFConstants.FormatType formatType = otherCpfAttribute.getFormatData().getCategory();
							String temp = new String();
							String convTemp = null;
							
							if(formatType.equals(CPFConstants.FormatType.NUMERIC) 
								|| formatType.equals(CPFConstants.FormatType.CURRENCY)
									|| formatType.equals(CPFConstants.FormatType.INTEGRAL)) {
									temp = temp.concat("	<f:convertNumber pattern=\"");
										//14th March....
									String tempPat = otherCpfAttribute.getFormatData().getPattern();
									if(formatType.equals(CPFConstants.FormatType.INTEGRAL) && tempPat.contains(".")) {
										tempPat = tempPat.substring(0, tempPat.indexOf("."));
									}
									temp = temp.concat(tempPat + "\"/>");
									
										//Added this for conversion in search panel....
									if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Long")
										|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("long")) {
										convTemp = new String("		<f:converter converterId=\"javax.faces.Long\"/>");
									} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Integer")
												|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("int")) {
										convTemp = new String("		<f:converter converterId=\"javax.faces.Integer\"/>");
									} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Double") 
												|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("double")) {
										convTemp = new String("		<f:converter converterId=\"javax.faces.Double\"/>");
									} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Float")
												|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("float")) {
										convTemp = new String("		<f:converter converterId=\"javax.faces.Float\"/>");
									} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Boolean")
											|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("boolean")) {
										convTemp = new String("		<f:converter converterId=\"javax.faces.Boolean\"/>");
									} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Byte")
											|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("byte")) {
										convTemp = new String("		<f:converter converterId=\"javax.faces.Byte\"/>");
									} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Character")
											|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("char")) {
										convTemp = new String("		<f:converter converterId=\"javax.faces.Character\"/>");
									} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Short")
											|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("short")) {
										convTemp = new String("		<f:converter converterId=\"javax.faces.Short\"/>");
									}
									//temp = temp.concat(otherCpfAttribute.getFormatData().getPattern() + "\"/>");
							} else {
								String tempPat = otherCpfAttribute.getFormatData().getPattern();
								if(tempPat.equals("Short") || tempPat.equals("Medium") || tempPat.equals("Long")
									|| tempPat.equals("Full")) {
						
									tempPat = tempPat.toLowerCase().charAt(0) + tempPat.substring(1);
									if(formatType.equals(CPFConstants.FormatType.DATE)){
										temp = temp.concat("	<f:convertDateTime ");
										temp = temp.concat("dateStyle=\"");
									} else if(formatType.equals(CPFConstants.FormatType.TIME)) {
										temp = temp.concat("	<f:convertDateTime ");
										temp = temp.concat("timeStyle=\"");
									} else {
										temp = temp.concat("	<f:convertDateTime ");
										temp = temp.concat("timeStyle=\"" + tempPat + "\" ");
										temp = temp.concat("dateStyle=\"");
									}
								} else {
										temp = temp.concat("	<f:convertDateTime pattern=\"");
								}
								temp = temp.concat(tempPat + "\"/>");
							}
							formatTag.add(temp);
							conversionTag.add(convTemp);
					} else {
						String temp = null;
						String convTemp = null;
						
						if(otherCpfAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)){
							temp = new String("		<f:convertDateTime type=\"date\"/>");
							convTemp = new String(temp);
							//formatTag.add(temp);
						} else if(otherCpfAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
							if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals(java.sql.Time.class)) {
								temp = new String("		<f:convertDateTime type=\"time\"/>");
							} else {
								temp = new String("		<f:convertDateTime type=\"both\"/>");
							}
							convTemp = new String(temp);
							//formatTag.add(temp);
						} else  if(otherCpfAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.NUMERIC)) {
							temp = new String("		<f:convertNumber/>");
							
								//Added this for conversion in search panel....
							if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Long")
								|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("long")) {
								convTemp = new String("		<f:converter converterId=\"javax.faces.Long\"/>");
							} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Integer")
										|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("int")) {
								convTemp = new String("		<f:converter converterId=\"javax.faces.Integer\"/>");
							} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Double") 
										|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("double")) {
								convTemp = new String("		<f:converter converterId=\"javax.faces.Double\"/>");
							} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Float")
										|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("float")) {
								convTemp = new String("		<f:converter converterId=\"javax.faces.Float\"/>");
							} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Boolean")
										|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("boolean")) {
								convTemp = new String("		<f:converter converterId=\"javax.faces.Boolean\"/>");
							} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Byte")
										|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("byte")) {
								convTemp = new String("		<f:converter converterId=\"javax.faces.Byte\"/>");
							} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Character")
										|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("char")) {
								convTemp = new String("		<f:converter converterId=\"javax.faces.Character\"/>");
							} else if(otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("java.lang.Short")
										|| otherCpfAttribute.getModelAttrib().getCanonicalTypeName().equals("short")) {
								convTemp = new String("		<f:converter converterId=\"javax.faces.Short\"/>");
							}
							//formatTag.add(temp);
						}// else {
							//formatTag.add(null);
						formatTag.add(temp);
						conversionTag.add(convTemp);
						//}
					}
				}
			}
			
		}
	}
//DOING	process for CPFATTRIBUTES ENDS HERE............	

    
	//Xhtml generation starts here..............
	LOG.info("XHTML generation started inside Tempalte...... adding Div tag to XHTMl..");

    stringBuffer.append(TEXT_2);
    
	//Search Panel starts here....
	LOG.info("Adding Search Panel to XHTMl....");
	if(actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_7);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_8);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_10);
    
				Iterator<String> itrFormatTag = null;
				Iterator<String> itrConvTag = null;	//Added for conversion purpose..
				Iterator<String> itrSortVal = null;
				if(formatTag != null) {
					 itrFormatTag = formatTag.iterator();
					 itrFormatTag.next();	//Skipping the added primary key attribute...
				}
				if(conversionTag != null) {
					itrConvTag = conversionTag.iterator();
					itrConvTag.next();		//Skipping the added primary Key attribute...
				}
				if(sortValues != null) {
					 itrSortVal = sortValues.iterator();
					 itrSortVal.next();		//Skipping the added primary key attribute...
				}
				while(itrSortVal.hasNext()) {
					String sortValue = itrSortVal.next();
					//String format = itrFormatTag.next();
					String format = itrConvTag.next();
					if(sortValue != null) {

    stringBuffer.append(TEXT_11);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_12);
    stringBuffer.append( sortValue );
    stringBuffer.append(TEXT_13);
    
						if(format != null) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append( format );
    
						} else {
						}

    stringBuffer.append(TEXT_15);
    
					}
				}

    stringBuffer.append(TEXT_16);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_17);
    
	}
	//Search Panel Ends here....
	LOG.info("adding search panel finished... adding form Tag to XHTML...");

    stringBuffer.append(TEXT_18);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_19);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_21);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_22);
    
	//Result Table cretaion Starts here.....
	LOG.info("adding Result Table to XHTML....");

    stringBuffer.append(TEXT_23);
    
		//Table Header starts here....
		LOG.info("Adding table Headers to the table..... as well adding sort also if supportable..");

    stringBuffer.append(TEXT_24);
    
			int i = 0;
			int htmlPos = 0;
			if(CPFArgument.getOperationId() == 0) {
				totalNoOfAttbs = totalNoOfAttbs - 1; 
			}
			while(htmlPos < totalNoOfAttbs) {
				if(i == 0) {
					i++;
					if(CPFArgument.getOperationId() > 0) {
						htmlPos++;
					}
					if(actionsSupported.contains(CPFConstants.OperationType.MODIFY)
						|| actionsSupported.contains(CPFConstants.OperationType.DELETE) || 
							actionsSupported.contains(CPFConstants.OperationType.VIEW)) {

    stringBuffer.append(TEXT_25);
    
					}
				} else {
					String header = null;
					boolean nested = false;
					String sortValue = null;
					if(positionMap.get(htmlPos) == null) {
						htmlPos++;
						continue;
					}
					header = headerNames.get(positionMap.get(htmlPos));
					nested = nestedLink.get(positionMap.get(htmlPos));
					if(!nested && sortValues != null
						&& actionsSupported.contains(CPFConstants.OperationType.SORT)) {
						sortValue = sortValues.get(positionMap.get(htmlPos));
					}
					if(nested) {

    stringBuffer.append(TEXT_26);
    stringBuffer.append( header );
    stringBuffer.append(TEXT_27);
    				
					} else {
						if(sortValues != null && actionsSupported.contains(CPFConstants.OperationType.SORT)) {

    stringBuffer.append(TEXT_28);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_29);
    stringBuffer.append( positionMap.get(htmlPos) );
    stringBuffer.append(TEXT_30);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_31);
    stringBuffer.append( header.replaceAll(" ", "_") );
    stringBuffer.append(TEXT_32);
    stringBuffer.append( sortValue );
    stringBuffer.append(TEXT_33);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_34);
    stringBuffer.append( sortValue );
    stringBuffer.append(TEXT_35);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_36);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_37);
    
						} else {

    stringBuffer.append(TEXT_38);
    stringBuffer.append( header.replaceAll(" ", "_") );
    stringBuffer.append(TEXT_39);
    
						}
					}
					htmlPos++;
				}
			}

    stringBuffer.append(TEXT_40);
    
		//Table Header ends here....
		LOG.info("Adding headers to table finished....");

    stringBuffer.append(TEXT_41);
    
		//TABLE BODY CREATION STARTS HERE...............
		LOG.info("adding Table Body to The Table started....");

    stringBuffer.append(TEXT_42);
    
			if(actionsSupported.contains(CPFConstants.OperationType.MODIFY)
				|| actionsSupported.contains(CPFConstants.OperationType.DELETE) || 
					actionsSupported.contains(CPFConstants.OperationType.VIEW)) {
					int pagination = cpfScreen.getPreference().getPagination();
					LOG.info("Adding Radio Buttons to the table for row selection...");

    stringBuffer.append(TEXT_43);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_44);
    stringBuffer.append( pagination + 1 );
    stringBuffer.append(TEXT_45);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_47);
    
			}

    stringBuffer.append(TEXT_48);
    
			i = 0;
			int nestedName = 0;
			htmlPos = 0;
			while(htmlPos < totalNoOfAttbs) {
				//Integer fp = criteriaPos.get(htmlPos);
				if(i == 0) {
					i++;
					if(CPFArgument.getOperationId() > 0) {
						htmlPos++;
					}
					continue;
				}
				if(positionMap.get(htmlPos) == null) {
					htmlPos++;
					continue;
				}
				String format = formatTag.get(positionMap.get(htmlPos));

    stringBuffer.append(TEXT_49);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_29);
    stringBuffer.append( positionMap.get(htmlPos) );
    stringBuffer.append(TEXT_50);
    
				if(i == 1){
					if(nestedLink.get(positionMap.get(htmlPos))) {
						//String nestedMBeanName = nestedMBeanNames.get(nestedName++);
						String nestedMBeanName = nestedMBeanNames.get(htmlPos);
						String nestedNavigatinoName = nestedMBeanName.replace("_", "");

    stringBuffer.append(TEXT_51);
    stringBuffer.append( nestedNavigatinoName );
    stringBuffer.append(TEXT_52);
    stringBuffer.append( nestedMBeanName );
    stringBuffer.append(TEXT_53);
    
					} else {

    stringBuffer.append(TEXT_54);
    
							if(actionsSupported.contains(CPFConstants.OperationType.VIEW)) {

    stringBuffer.append(TEXT_55);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( createMBeanName );
    stringBuffer.append(TEXT_57);
    stringBuffer.append( criteriaPos.get(htmlPos) );
    stringBuffer.append(TEXT_58);
    
							} else {

    stringBuffer.append(TEXT_59);
    stringBuffer.append( criteriaPos.get(htmlPos) );
    stringBuffer.append(TEXT_50);
    
							}		

    
							if(format != null) {

    stringBuffer.append(TEXT_60);
    stringBuffer.append( format );
    
							}
							if(actionsSupported.contains(CPFConstants.OperationType.VIEW)) {

    stringBuffer.append(TEXT_61);
    stringBuffer.append( criteriaPos.get(htmlPos) );
    stringBuffer.append(TEXT_50);
    
							if(format != null) {

    stringBuffer.append(TEXT_62);
    stringBuffer.append( format );
    
							}

    stringBuffer.append(TEXT_63);
    
							} else {

    stringBuffer.append(TEXT_64);
    
							}

    stringBuffer.append(TEXT_65);
    
					}
					i++;
				} else {
					if(nestedLink.get(positionMap.get(htmlPos))) {
						//String nestedMBeanName = nestedMBeanNames.get(nestedName++);
						String nestedMBeanName = nestedMBeanNames.get(htmlPos);
						String nestedNavigatinoName = nestedMBeanName.replace("_", "");

    stringBuffer.append(TEXT_51);
    stringBuffer.append( nestedNavigatinoName );
    stringBuffer.append(TEXT_52);
    stringBuffer.append( nestedMBeanName );
    stringBuffer.append(TEXT_66);
    
					} else {

    stringBuffer.append(TEXT_67);
    stringBuffer.append( criteriaPos.get(htmlPos) );
    stringBuffer.append(TEXT_50);
    
								if(format != null) {

    stringBuffer.append(TEXT_60);
    stringBuffer.append( format );
    
								}

    stringBuffer.append(TEXT_68);
    
					}
				}
				htmlPos++;

    stringBuffer.append(TEXT_69);
    
			}

    stringBuffer.append(TEXT_70);
    
		//TABLE BODY CREATION ENDS HERE...............
		LOG.info("Table Body Creation Completed ....");

    stringBuffer.append(TEXT_71);
    
	//Result Table creation Ends here......

    stringBuffer.append(TEXT_41);
    
	//PageChange panel are coming here............
	LOG.info("Adding page Change panel to the table....");

    stringBuffer.append(TEXT_72);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_73);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_74);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_75);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_76);
    
	//PageChange panel Ends here............

    stringBuffer.append(TEXT_77);
    
	//CMV buttons panel Starts here......

    stringBuffer.append(TEXT_78);
    
		//if(resourceName.equals(baseEntity.getName())) {
		if(CPFArgument.getOperationId() == 0) {

    
		//if(actionsSupported.contains(CPFConstants.OperationType.VIEW)) {
			LOG.info("Adding view button to the table...");

    stringBuffer.append(TEXT_79);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_80);
    stringBuffer.append( createMBeanName );
    stringBuffer.append(TEXT_81);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_83);
    
		//}

    
		//if(actionsSupported.contains(CPFConstants.OperationType.CREATE)) {
			LOG.info("Adding Create button to the table...");

    stringBuffer.append(TEXT_84);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_85);
    stringBuffer.append( createMBeanName );
    stringBuffer.append(TEXT_86);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_87);
    
		//}

    
		//if(actionsSupported.contains(CPFConstants.OperationType.MODIFY)) {
			LOG.info("Adding Modify button to the table...");

    stringBuffer.append(TEXT_84);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_88);
    stringBuffer.append( createMBeanName );
    stringBuffer.append(TEXT_89);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_90);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_91);
    
	//	}

    
		//if(actionsSupported.contains(CPFConstants.OperationType.DELETE)) {
			LOG.info("Adding Delete button to the table...");

    stringBuffer.append(TEXT_84);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_92);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_93);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_94);
    
	//	}
		}
			//Editing on 23rd Feb
		//if(!resourceName.equals(baseEntity.getName ())) {
		if(CPFArgument.getOperationId() > 0) {
			//This only for Nested Screen to return back to the Main List

    stringBuffer.append(TEXT_95);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_96);
    	 
		}
		if(actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {	//Added this line later

    stringBuffer.append(TEXT_97);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_98);
    stringBuffer.append( listMBeanName );
    stringBuffer.append(TEXT_99);
    
		}	//Added this line later
	//CMV buttons panel Ends here......

    stringBuffer.append(TEXT_100);
    
	//Xhtml generation ends here................
	LOG.info("XHTML generation finished from template....");

    return stringBuffer.toString();
  }
}
