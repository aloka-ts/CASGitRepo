package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.model.*;
import java.util.*;
import java.util.List;
import com.genband.m5.maps.ide.model.util.*;

public class ListMBean
{
  protected static String nl;
  public static synchronized ListMBean create(String lineSeparator)
  {
    nl = lineSeparator;
    ListMBean result = new ListMBean();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = " ";
  protected final String TEXT_2 = NL + NL + NL + NL + "/**********************************************************************" + NL + "* GENBAND, Inc. Confidential and Proprietary" + NL + "*" + NL + "* This work contains valuable confidential and proprietary" + NL + "* information." + NL + "* Disclosure, use or reproduction without the written authorization of" + NL + "* GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc." + NL + "* is protected by the laws of the United States and other countries." + NL + "* If publication of the work should occur the following notice shall" + NL + "* apply:" + NL + "*" + NL + "* \"Copyright 2008 GENBAND, Inc. All rights reserved.\"" + NL + "**********************************************************************" + NL + "**/" + NL + "" + NL + "/**********************************************************************" + NL + "*" + NL + "* Project: MAPS" + NL + "*" + NL + "* Package: com.genband.m5.maps.mbeans" + NL + "*" + NL + "* File: ";
  protected final String TEXT_3 = ".java" + NL + "*" + NL + "* Desc: Managed Bean for ";
  protected final String TEXT_4 = "." + NL + "*" + NL + "* Author Date Description" + NL + "* ---------------------------------------------------------" + NL + "* Genband ";
  protected final String TEXT_5 = " Initial Creation" + NL + "*" + NL + "**********************************************************************" + NL + "**/" + NL + "" + NL + "package com.genband.m5.maps.mbeans;" + NL;
  protected final String TEXT_6 = NL + "import javax.el.ELContext;" + NL + "import javax.el.ExpressionFactory;" + NL + "import javax.el.ValueExpression;" + NL;
  protected final String TEXT_7 = NL + "import java.util.ArrayList;" + NL + "import java.util.Collection;" + NL + "import java.util.List;" + NL + "import java.util.Map;" + NL + "import java.io.IOException;" + NL + "import java.io.InputStream;" + NL + "import java.util.Properties;" + NL + "import java.util.HashMap;" + NL + "import javax.faces.model.*;" + NL + "import javax.faces.application.FacesMessage;" + NL + "import javax.faces.context.ExternalContext;" + NL + "import javax.faces.context.FacesContext;" + NL + "import javax.faces.event.ActionEvent;" + NL + "import com.genband.m5.maps.common.CPFException;" + NL + "import com.genband.m5.maps.common.AuxiliaryDetails;" + NL + "import com.genband.m5.maps.common.Criteria;" + NL + "import com.genband.m5.maps.common.SS_Constants;" + NL + "import com.genband.m5.maps.common.CPFConstants;" + NL + "import com.genband.m5.maps.common.User;" + NL + "import com.genband.m5.maps.common.AuxiliaryDetails.SearchInfo;" + NL + "import com.genband.m5.maps.common.CPFManager;" + NL + "import com.genband.m5.maps.common.PortletFacesUtils;" + NL + "import javax.portlet.PortletPreferences;" + NL + "import javax.portlet.PortletRequest;" + NL + "import javax.portlet.PortletSession;" + NL + "import java.util.Locale;" + NL + "import java.util.ResourceBundle;" + NL + "import java.util.Set;" + NL + "import org.jboss.security.SecurityAssociation;" + NL + "import java.security.Principal;" + NL + "import com.genband.m5.maps.identity.GBUserPrincipal;" + NL + "import ";
  protected final String TEXT_8 = ";";
  protected final String TEXT_9 = NL + "import ";
  protected final String TEXT_10 = NL + "import com.genband.m5.maps.common.entity.Organization;";
  protected final String TEXT_11 = NL + NL + "public class ";
  protected final String TEXT_12 = " {" + NL + "" + NL + "//Variables Declaration" + NL + "\tprivate boolean canCreate;" + NL + "\t" + NL + "\tprivate boolean canModify;" + NL + "" + NL + "\tprivate boolean canView;" + NL + "" + NL + "\tprivate boolean canDelete;\t" + NL;
  protected final String TEXT_13 = "\t" + NL + "\tprivate String searchKey;" + NL + "" + NL + "\tprivate String searchOperator;" + NL + "" + NL + "\tprivate Object searchValue;\t" + NL + "" + NL + "\tprivate boolean searechCaseSensitive;\t" + NL + "" + NL + "\tprivate Map<String, SelectItemGroup> searchFields = new HashMap<String, SelectItemGroup>();  //Map of Attribute and their supportable search Operators depending upon its DataType\t" + NL + "" + NL + "\tprivate List<SelectItem> attbs;\t" + NL + "" + NL + "\tprivate SelectItemGroup operators;" + NL;
  protected final String TEXT_14 = NL + "\tprivate String primaryKeyName;" + NL;
  protected final String TEXT_15 = "\t" + NL + "\tprivate String orderBy;" + NL + "" + NL + "\tprivate String orderByState = null;\t" + NL + "" + NL + "\tprivate boolean ascending;" + NL;
  protected final String TEXT_16 = NL + "\tprivate int pageSize = ";
  protected final String TEXT_17 = ";" + NL + "" + NL + "\tprivate int rowNumber;\t" + NL + "" + NL + "\tprivate Criteria criteria;\t" + NL + "" + NL + "\tprivate Long primaryKeyValue;\t" + NL + "" + NL + "\tprivate int operationId;\t" + NL + "" + NL + "\tprivate int numberOfRecords;\t" + NL + "" + NL + "\tprivate boolean initialCall = true;\t" + NL + "" + NL + "\tprivate boolean nextDisabled;\t" + NL + "" + NL + "\tprivate boolean previousDisabled;" + NL + "" + NL + "\tprivate String userRole;\t" + NL + "" + NL + "\tprivate List<Boolean> listVisibility;\t" + NL + "" + NL + "\tprivate List<SelectItem> radio;\t" + NL + "" + NL + "\tprivate int mode;\t" + NL + "" + NL + "\tprivate List<Object[]> coll;" + NL + "\t" + NL + "\tprivate Integer delStatus;" + NL + "//End of variables Declaration" + NL + "" + NL + "\t//Default Constructor" + NL + "" + NL + "\tpublic ";
  protected final String TEXT_18 = " () {" + NL + "" + NL + "\t}" + NL + "" + NL + "//Start Of Setters and Getters" + NL + "\tpublic boolean isCanCreate() {" + NL + "\t\treturn canCreate;" + NL + "\t}\t" + NL + "" + NL + "\tpublic void setCanCreate(boolean canCreate) {" + NL + "\t\tthis.canCreate = canCreate;" + NL + "\t}" + NL + "" + NL + "\tpublic boolean isCanModify() {" + NL + "\t\treturn canModify;" + NL + "\t}\t" + NL + "" + NL + "\tpublic void setCanModify(boolean canModify) {" + NL + "\t\tthis.canModify = canModify;" + NL + "\t}\t\t" + NL + "" + NL + "\tpublic boolean isCanView() {" + NL + "\t\treturn canView;" + NL + "\t}\t" + NL + "" + NL + "\tpublic void setCanView(boolean canView) {" + NL + "\t\tthis.canView = canView;" + NL + "\t}\t" + NL + "" + NL + "\tpublic boolean isCanDelete() {" + NL + "\t\treturn canDelete;" + NL + "\t}\t" + NL + "" + NL + "\tpublic void setCanDelete(boolean canDelete) {" + NL + "\t\tthis.canDelete = canDelete;" + NL + "\t}\t" + NL;
  protected final String TEXT_19 = "\t" + NL + "\tpublic String getSearchKey() {" + NL + "\t\treturn searchKey;" + NL + "\t}" + NL + "" + NL + "\tpublic void setSearchKey(String searchKey) {" + NL + "\t\tthis.searchKey = searchKey;" + NL + "\t}" + NL + "" + NL + "\tpublic String getSearchOperator() {" + NL + "\t\treturn searchOperator;" + NL + "\t}" + NL + "" + NL + "\tpublic void setSearchOperator(String searchOperator) {" + NL + "\t\tthis.searchOperator = searchOperator;" + NL + "\t}" + NL + "" + NL + "\tpublic Object getSearchValue() {" + NL + "\t\treturn searchValue;" + NL + "\t}" + NL + "" + NL + "\tpublic void setSearchValue(Object searchValue) {" + NL + "\t\tthis.searchValue = searchValue;" + NL + "\t}" + NL + "" + NL + "\tpublic boolean isSearechCaseSensitive() {" + NL + "\t\treturn searechCaseSensitive;" + NL + "\t}" + NL + "" + NL + "\tpublic void setSearechCaseSensitive(boolean searechCaseSensitive) {" + NL + "\t\tthis.searechCaseSensitive = searechCaseSensitive;" + NL + "\t}" + NL + "" + NL + "\tpublic List<SelectItem> getAttbs() {" + NL + "\t\treturn this.attbs;" + NL + "\t}\t" + NL + "" + NL + "\tpublic void getAttbs(List<SelectItem> attbs) {" + NL + "\t\tthis.attbs = attbs;" + NL + "\t}\t" + NL + "" + NL + "\tpublic SelectItemGroup getOperators() {" + NL + "\t\tthis.operators = getSearchFields().get(this.getSearchKey());" + NL + "\t\treturn operators;" + NL + "\t}" + NL + "" + NL + "\tpublic void setOperators(SelectItemGroup operators) {" + NL + "\t\tthis.operators = operators;" + NL + "\t}" + NL + "" + NL + "\tpublic Map<String, SelectItemGroup> getSearchFields() {" + NL + "\t\treturn searchFields;" + NL + "\t}" + NL + "" + NL + "\tpublic void setSearchFields(Map<String, SelectItemGroup> searchFields) {" + NL + "\t\tthis.searchFields = searchFields;" + NL + "\t}" + NL;
  protected final String TEXT_20 = NL + "\tpublic String getPrimaryKeyName() {" + NL + "\t\treturn primaryKeyName;" + NL + "\t}" + NL + "" + NL + "\tpublic void setPrimaryKeyName(String primaryKeyName) {" + NL + "\t\tthis.primaryKeyName = primaryKeyName;" + NL + "\t}" + NL;
  protected final String TEXT_21 = NL + "\tpublic String getOrderBy() {" + NL + "\t\treturn orderBy;" + NL + "\t}" + NL + "" + NL + "\tpublic void setOrderBy(String orderBy) {" + NL + "\t\tthis.orderBy = orderBy;" + NL + "\t}\t" + NL + "" + NL + "\tpublic String getOrderByState() {" + NL + "\t\treturn orderByState;" + NL + "\t}" + NL + "" + NL + "\tpublic void setOrderByState(String orderByState) {" + NL + "\t\tthis.orderByState = orderByState;" + NL + "\t}" + NL + "" + NL + "\tpublic boolean isAscending() {" + NL + "\t\treturn ascending;" + NL + "\t}" + NL + "" + NL + "\tpublic void setAscending(boolean ascending) {" + NL + "\t\tthis.ascending = ascending;" + NL + "\t}" + NL;
  protected final String TEXT_22 = NL + "\tpublic int getPageSize() {" + NL + "\t\treturn pageSize;" + NL + "\t}" + NL + "" + NL + "\tpublic void setPageSize(int pageSize) {" + NL + "\t\tthis.pageSize = pageSize;" + NL + "\t}" + NL + "" + NL + "\tpublic int getRowNumber() {" + NL + "\t\treturn rowNumber;" + NL + "\t}" + NL + "" + NL + "\tpublic void setRowNumber(int rowNumber) {" + NL + "\t\tthis.rowNumber = rowNumber;" + NL + "\t}" + NL + "" + NL + "\tpublic Criteria getCriteria() {" + NL + "\t\treturn criteria;" + NL + "\t}" + NL + "" + NL + "\tpublic void setCriteria(Criteria criteria) {" + NL + "\t\tthis.criteria = criteria;" + NL + "\t}" + NL + "" + NL + "\tpublic int getOperationId() {" + NL + "\t\treturn operationId;" + NL + "\t}" + NL + "" + NL + "\tpublic void setOperationId(int operationId) {" + NL + "\t\tthis.operationId = operationId;" + NL + "\t}\t" + NL + "" + NL + "\tpublic Long getPrimaryKeyValue() {" + NL + "\t\treturn primaryKeyValue;" + NL + "\t}" + NL + "" + NL + "\tpublic void setPrimaryKeyValue(Long primaryKeyValue) {" + NL + "\t\tthis.primaryKeyValue = primaryKeyValue;" + NL + "\t}\t" + NL + "" + NL + "\tpublic int getNumberOfRecords() {" + NL + "\t\treturn numberOfRecords;" + NL + "\t}" + NL + "" + NL + "\tpublic void setNumberOfRecords(int numberOfRecords) {" + NL + "\t\tthis.numberOfRecords = numberOfRecords;" + NL + "\t}" + NL + "" + NL + "\tpublic boolean isNextDisabled() {" + NL + "\t\treturn nextDisabled;" + NL + "\t}" + NL + "" + NL + "\tpublic void setNextDisabled(boolean nextDisabled) {" + NL + "\t\tthis.nextDisabled = nextDisabled;" + NL + "\t}" + NL + "" + NL + "\tpublic boolean isPreviousDisabled() {" + NL + "\t\tthis.mode = 0;" + NL + "\t\treturn previousDisabled;" + NL + "\t}" + NL + "" + NL + "\tpublic void setPreviousDisabled(boolean previousDisabled) {" + NL + "\t\tthis.previousDisabled = previousDisabled;" + NL + "\t}\t" + NL + "" + NL + "\tpublic String getUserRole() {" + NL + "\t\treturn userRole;" + NL + "\t}" + NL + "" + NL + "\tpublic void setUserRole(String userRole) {" + NL + "\t\tthis.userRole = userRole;" + NL + "\t}\t" + NL + "" + NL + "\tpublic List<Boolean> getListVisibility() {" + NL + "\t\treturn listVisibility;" + NL + "\t}" + NL + "" + NL + "\tpublic void setListVisibility(List<Boolean> listVisibility) {" + NL + "\t\tthis.listVisibility = listVisibility;" + NL + "\t}\t" + NL + "" + NL + "\tpublic List<SelectItem> getRadio() {" + NL + "\t\treturn radio;" + NL + "\t}" + NL + "" + NL + "\tpublic void setRadio(List<SelectItem> radio) {" + NL + "\t\tthis.radio = radio;" + NL + "\t}\t" + NL + "" + NL + "\tpublic int getMode() {" + NL + "\t\treturn mode;" + NL + "\t}" + NL + "" + NL + "\tpublic void setMode(int mode) {" + NL + "\t\tthis.mode = mode;" + NL + "\t}\t" + NL + "" + NL + "\tpublic List<Object[]> getColl() {" + NL + "\t\treturn coll;" + NL + "\t}" + NL + "" + NL + "\tpublic void setColl(List<Object[]> coll) {" + NL + "\t\tthis.coll = coll;" + NL + "\t}" + NL + "\t" + NL + "\tpublic Integer getDelStatus() {" + NL + "\t\treturn delStatus;" + NL + "\t}" + NL + "" + NL + "\tpublic void setDelStatus(Integer delStatus) {" + NL + "\t\tthis.delStatus = delStatus;" + NL + "\t}" + NL + "//End of Setters and Getters" + NL + "" + NL + "" + NL + "\t//For deleting a record (If User does not have permission then this will throw Exception or failure Message)" + NL + "\tpublic String delete (ActionEvent e) {" + NL + "" + NL + "\t\tFacesContext ctx = FacesContext.getCurrentInstance();" + NL + "\t\tLocale currentLocal=ctx.getViewRoot().getLocale();" + NL + "\t\tString code=currentLocal.getLanguage() + \"_\"+ currentLocal.getCountry();" + NL + "\t\tInputStream stream=this.getClass().getClassLoader().getResourceAsStream(\"bundle/resources_\"+code+\".properties\");" + NL + "\t\tif(stream==null){" + NL + "\t\t\tstream=this.getClass().getClassLoader().getResourceAsStream(\"bundle/resources.properties\");" + NL + "\t\t}" + NL + "\t\tProperties bundle=new Properties();" + NL + "\t    try {" + NL + "\t\t\tbundle.load(stream);" + NL + "\t\t} catch (IOException ie) {" + NL + "\t\t\tie.printStackTrace();" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tthis.mode = 0;" + NL + "" + NL + "\t\tthis.operationId = this.getOperationId(CPFConstants.OperationType.DELETE);" + NL + "\t\t" + NL + "\t\tString returnValue = null;" + NL + "" + NL + "\t\ttry{" + NL + "\t\t\treturnValue = CPFManager.delete (this.getBaseObject(), this.primaryKeyValue, this.operationId);" + NL + "\t\t} catch (CPFException e1) {" + NL + "\t\t\tctx.addMessage(e.getComponent().getClientId(ctx), new FacesMessage(bundle.getProperty(\"del_failure\") + e1.getMessage()));" + NL + "\t\t\tthis.delStatus = new Integer(0);" + NL + "\t\t\treturn SS_Constants.ReturnMessage.PROVERROR.toString();" + NL + "\t\t}" + NL + "\t\tctx.addMessage(e.getComponent().getClientId(ctx), new FacesMessage(bundle.getProperty(\"del_success\")));" + NL + "\t\t" + NL + "\t\tthis.operationId = this.getOperationId(CPFConstants.OperationType.LIST);\t" + NL + "" + NL + "\t\tif(this.coll.size() == 1 && this.rowNumber != 0) {" + NL + "" + NL + "\t\t\tthis.rowNumber = this.rowNumber - this.pageSize;" + NL + "" + NL + "\t\t\tthis.criteria.getSearchDetails().setRowNumber(this.rowNumber);" + NL + "" + NL + "\t\t}" + NL + "\t\tthis.delStatus = new Integer(1);" + NL + "\t\tthis.primaryKeyValue = null;" + NL + "" + NL + "\t\treturn returnValue;" + NL + "" + NL + "\t}" + NL + "\t//End of Deleting a record\t" + NL + "" + NL + "\t//Getting result as Collection of Array Objects to display the result on listing page" + NL + "\tpublic Collection<Object[]> getCollec () throws Exception {" + NL + "" + NL + "\t\tList<Object[]> coll = new ArrayList<Object[]>();" + NL;
  protected final String TEXT_23 = NL + "\t\tthis.searechCaseSensitive = false;" + NL + "" + NL + "\t\tthis.searchValue = null;" + NL;
  protected final String TEXT_24 = NL + "\t\tthis.primaryKeyValue = null;" + NL;
  protected final String TEXT_25 = NL + "\t\tif(this.initialCall) {" + NL + "" + NL + "\t\t\tFacesContext ctx = FacesContext.getCurrentInstance();" + NL + "" + NL + "\t\t\t//PortletPreferences pref = PortletFacesUtils.getPortletPreferences(ctx);" + NL + "" + NL + "\t\t\t//String val = pref.getValue(\"pageSize\", \"10\");" + NL + "" + NL + "\t\t\t//this.pageSize = Integer.parseInt(val);" + NL + "" + NL + "\t\t\tthis.userIsInRole(ctx);" + NL + "" + NL + "\t\t\tthis.getListVisibility(this.userRole);" + NL + "" + NL + "\t\t\tthis.setRowNumber(0);" + NL + "" + NL + "\t\t\tthis.criteria = this.getDefaultCriteria();" + NL + "" + NL + "\t\t\tthis.initialCall = false;" + NL;
  protected final String TEXT_26 = NL + "\t\t\tthis.loadAttbsOperators(this.userRole);" + NL;
  protected final String TEXT_27 = NL + "\t\t}" + NL + "" + NL + "\t\t//coll = (List<Object[]>)CPFManager.getResult(this.operationId, this.criteria);" + NL + "" + NL + "\t\tthis.setOperationId(this.getOperationId(CPFConstants.OperationType.LIST));" + NL + "" + NL + "\t\tif(this.mode == 0) {" + NL + "" + NL + "\t\t\tif(this.orderByState == null) {" + NL + "" + NL + "\t\t\t\tthis.ascending = true;" + NL + "" + NL + "\t\t\t\tthis.criteria.getSearchDetails().setAscending(this.ascending);" + NL + "\t\t\t\t";
  protected final String TEXT_28 = NL + "\t\t\t\t\tthis.orderBy = new String(\"";
  protected final String TEXT_29 = ".";
  protected final String TEXT_30 = "\");";
  protected final String TEXT_31 = NL + NL + "\t\t\t\tthis.criteria.getSearchDetails().setOrderBy(this.orderBy);" + NL + "" + NL + "\t\t\t\tthis.orderByState = this.orderBy;" + NL + "" + NL + "\t\t\t}" + NL + "" + NL + "\t\t\tcoll = (List<Object[]>)CPFManager.getResult(this.operationId, this.criteria);" + NL + "" + NL + "\t\t\tthis.mode = 1;" + NL + "" + NL + "\t\t\tthis.coll = coll;" + NL + "" + NL + "\t\t//}\t\t\t" + NL + "" + NL + "\t\tif(this.coll.size() != this.pageSize + 1) {" + NL + "" + NL + "\t\t\tthis.setNextDisabled(true);" + NL + "" + NL + "\t\t} else {" + NL + "" + NL + "\t\t\tthis.setNextDisabled(false);" + NL + "" + NL + "\t\t}\t\t" + NL + "" + NL + "\t\tif(this.coll.size() < this.pageSize + 1) {" + NL + "" + NL + "\t\t\tthis.setNextDisabled(true);" + NL + "" + NL + "\t\t}\t\t" + NL + "" + NL + "\t\tif(this.rowNumber == 0) {" + NL + "" + NL + "\t\t\tthis.setPreviousDisabled(true);" + NL + "" + NL + "\t\t} else {" + NL + "" + NL + "\t\t\tthis.setPreviousDisabled(false);" + NL + "" + NL + "\t\t}\t\t" + NL + "" + NL + "\t\tif(this.coll.size() == this.pageSize + 1) {" + NL + "" + NL + "\t\t\tthis.coll.remove(this.pageSize);" + NL + "" + NL + "\t\t}\t\t" + NL + "" + NL + "\t\tradio = new ArrayList<SelectItem>();" + NL + "" + NL + "\t\tfor (Object[] objects : this.coll) {" + NL + "" + NL + "\t\t\t\tSelectItem item = new SelectItem();" + NL + "" + NL + "\t\t\t\titem.setValue(objects[0]);" + NL + "" + NL + "\t\t\t\titem.setLabel(\"\");" + NL + "" + NL + "\t\t\t\tradio.add(item);" + NL + "" + NL + "\t\t}" + NL + "" + NL + "\t\t}" + NL + "" + NL + "\t\treturn (Collection<Object[]>) this.coll;" + NL + "" + NL + "\t}" + NL + "\t//End of Listing" + NL;
  protected final String TEXT_32 = "\t" + NL + "\t\t//Funtion for Searching details" + NL + "\tpublic String search () throws Exception  {" + NL + "" + NL + "\t\tif(this.searchValue != null && this.searchValue.toString().trim().length() > 0) {\t" + NL + "" + NL + "\t\t\tthis.criteria = this.getDefaultCriteria();" + NL + "" + NL + "\t\t\tAuxiliaryDetails searchDetails = this.criteria.getSearchDetails();" + NL + "" + NL + "\t\t\tSearchInfo searchInfo = new SearchInfo ();" + NL + "" + NL + "\t\t\tCPFConstants.Operators operator = CPFConstants.Operators.valueOf(searchOperator);" + NL + "" + NL + "\t\t\tsearchInfo.setOperator(operator);" + NL + "" + NL + "\t\t\tsearchInfo.setSearchKey(this.searchKey);" + NL + "" + NL + "\t\t\tsearchInfo.setSearchValue(searchValue);" + NL + "" + NL + "\t\t\tsearchInfo.setSearechCaseSensitive(searechCaseSensitive);\t\t" + NL + "" + NL + "\t\t\tsearchDetails.setSearchInfo(searchInfo);" + NL + "" + NL + "\t\t\tsearchDetails.setSearch(true);" + NL + "" + NL + "\t\t\tthis.rowNumber = 0;" + NL + "" + NL + "\t\t\tsearchDetails.setRowNumber(rowNumber);" + NL + "" + NL + "\t\t\tthis.criteria.setSearchDetails(searchDetails);" + NL + "" + NL + "\t\t\tthis.mode = 0;" + NL + "" + NL + "\t\t}\t\t" + NL + "" + NL + "\t\tthis.initialCall = false;" + NL + "" + NL + "\t\treturn SS_Constants.ReturnMessage.SUCCESS.toString();" + NL + "" + NL + "\t}" + NL + "\t//End of searching details\t" + NL + "" + NL + "\tpublic String reset () {" + NL + "" + NL + "\t\tthis.mode = 0;" + NL + "" + NL + "\t\tthis.initialCall = true;" + NL + "" + NL + "\t\tthis.searchValue = null;" + NL + "" + NL + "\t\tthis.orderByState = null;" + NL + "" + NL + "\t\tthis.criteria.reset(this.pageSize);" + NL + "" + NL + "\t\treturn \"SUCCESS\";" + NL + "" + NL + "\t}\t" + NL;
  protected final String TEXT_33 = NL + "\t\t//Function to Sort on one Column" + NL + "\tpublic String sort () throws Exception {" + NL + "" + NL + "\t\tthis.mode = 0;" + NL + "" + NL + "\t\tFacesContext ctx = FacesContext.getCurrentInstance();" + NL + "" + NL + "\t\tExternalContext exCtx = ctx.getExternalContext();" + NL + "" + NL + "\t\tMap<String, String> params = exCtx.getRequestParameterMap();" + NL + "" + NL + "\t\tString val = (String)params.get(\"sortBy\");" + NL + "" + NL + "\t\tSystem.out.println(\"Order By: \" + val);" + NL + "" + NL + "\t\tthis.criteria = this.getDefaultCriteria();" + NL + "" + NL + "\t\tAuxiliaryDetails searchDetails = this.criteria.getSearchDetails();" + NL + "" + NL + "\t\t//this.ascending = true;" + NL + "" + NL + "\t\tthis.orderBy = val;" + NL + "" + NL + "\t\tif(orderBy.equals(this.orderByState)) {" + NL + "" + NL + "\t\t\tthis.ascending = !(this.ascending);\t" + NL + "" + NL + "\t\t} else {" + NL + "" + NL + "\t\t\tthis.ascending = true;" + NL + "" + NL + "\t\t\tthis.orderByState = this.orderBy;" + NL + "" + NL + "\t\t}" + NL + "" + NL + "\t\tsearchDetails.setAscending(this.ascending);" + NL + "" + NL + "\t\tthis.rowNumber = 0;" + NL + "" + NL + "\t\tsearchDetails.setRowNumber(rowNumber);" + NL + "" + NL + "\t\tsearchDetails.setOrderBy(orderBy);" + NL + "" + NL + "\t\tsearchDetails.setSearch(false);" + NL + "" + NL + "\t\tthis.criteria.setSearchDetails(searchDetails);" + NL + "" + NL + "\t\tthis.setCriteria(criteria);" + NL + "" + NL + "\t\tString returnString = \"SUCCESS\";" + NL + "" + NL + "\t\tthis.initialCall = false;" + NL + "" + NL + "\t\treturn returnString;" + NL + "" + NL + "\t}" + NL + "\t//End of Sorting" + NL;
  protected final String TEXT_34 = NL + "\t\t//For viewing list of next records" + NL + "\tpublic String pageChange (ActionEvent e) throws Exception {" + NL + "" + NL + "\t\tthis.mode = 0;" + NL + "" + NL + "\t\tString componentId = e.getComponent().getId();" + NL + "" + NL + "\t\t//this.criteria = this.getDefaultCriteria();" + NL + "" + NL + "\t\tAuxiliaryDetails auxiliaryDetails = this.criteria.getSearchDetails();" + NL + "" + NL + "\t\tif(componentId.equals(SS_Constants.PAGEFLOW_NEXT)) {" + NL + "" + NL + "\t\t\tthis.rowNumber = this.rowNumber + this.pageSize;" + NL + "" + NL + "\t\t} else if(componentId.equals(SS_Constants.PAGEFLOW_PREVIOUS)) {" + NL + "" + NL + "\t\t\tthis.rowNumber = this.rowNumber - this.pageSize;" + NL + "" + NL + "\t\t} " + NL + "" + NL + "\t\tauxiliaryDetails.setRowNumber(rowNumber);" + NL + "" + NL + "\t\tcriteria.setSearchDetails(auxiliaryDetails);" + NL + "" + NL + "\t\tthis.setCriteria(criteria);" + NL + "" + NL + "\t\tthis.initialCall = false;" + NL + "" + NL + "\t\treturn \"SUCCESS\";" + NL + "" + NL + "\t}" + NL + "\t" + NL + "" + NL + "\tprivate ";
  protected final String TEXT_35 = " getBaseObject () {" + NL + "" + NL + "\t\t";
  protected final String TEXT_36 = " baseObject = new ";
  protected final String TEXT_37 = " ();" + NL + "" + NL + "\t\treturn baseObject;" + NL + "" + NL + "\t}" + NL + "" + NL + "\t\t//This will return default Criteria Object " + NL + "\tprivate Criteria getDefaultCriteria () throws Exception {" + NL + "" + NL + "\t\tCriteria c = new Criteria();" + NL + "" + NL + "\t\tString fields = \"";
  protected final String TEXT_38 = "\";" + NL + "" + NL + "\t\tc.setFields(fields);" + NL + "" + NL + "\t\tc.setFrom(\"";
  protected final String TEXT_39 = "\");" + NL + "" + NL + "\t\tOrganization merchantAccount = _getMerchantAccount ();" + NL + "" + NL + "\t\tLong merchantId = merchantAccount.getOrganizationId();" + NL + "" + NL + "\t\tc.setWhere(\"";
  protected final String TEXT_40 = "\" + merchantId + \")\");\t" + NL + "" + NL + "\t\tc.setBaseEntityName(\"";
  protected final String TEXT_41 = "\");" + NL + "" + NL + "\t\tc.setBasePrimaryKey(\"";
  protected final String TEXT_42 = "\");" + NL + "" + NL + "\t\tAuxiliaryDetails auxiliaryDetails = new AuxiliaryDetails();" + NL + "" + NL + "\t\tauxiliaryDetails.setPageSize(pageSize);" + NL + "" + NL + "\t\tauxiliaryDetails.setRowNumber(rowNumber);" + NL + "" + NL + "\t\tauxiliaryDetails.setSearch(false);" + NL + "" + NL + "\t\tc.setSearchDetails(auxiliaryDetails);" + NL + "" + NL + "\t\treturn c;" + NL + "" + NL + "\t}\t" + NL + "" + NL + "\t\t//This will returns MerchantAccount Object of Current User" + NL + "\tprivate Organization _getMerchantAccount() throws Exception {" + NL + "" + NL + "\t\t//commenting out security ...";
  protected final String TEXT_43 = NL + "\t\tOrganization o =  new Organization ();" + NL + "" + NL + "\t\to.setOrganizationId (new Long (-1));" + NL + "" + NL + "\t\treturn o;" + NL;
  protected final String TEXT_44 = NL + "\t\t/*FacesContext ctx = FacesContext.getCurrentInstance();" + NL + "" + NL + "\t\tPortletRequest request = PortletFacesUtils.getPortletRequest(ctx);" + NL + "" + NL + "\t\tPortletSession session = request.getPortletSession();" + NL + "" + NL + "\t\tObject obj = session.getAttribute (\"User\");" + NL + "" + NL + "\t\tUser user = (User)obj;" + NL + "" + NL + "\t\treturn user.getMerchantAccount ();*/" + NL + "            Set<Principal> s = SecurityAssociation.getSubject().getPrincipals();            " + NL + "" + NL + "            for (Principal principal : s) {" + NL + "" + NL + "            \t//LOG.debug (\"sub principal: \" + principal.getClass().getName());" + NL + "" + NL + "                if (principal instanceof GBUserPrincipal) {" + NL + "" + NL + "                \t//LOG.debug (\"p: \" + principal);                              " + NL + "" + NL + "                    Organization enterprise = ((GBUserPrincipal) principal).getMerchantAccount();" + NL + "" + NL + "                    long enterpriseId = enterprise.getOrganizationId();" + NL + "" + NL + "                    //LOG.debug(\"enterpriseId = \" + enterpriseId);" + NL + "" + NL + "                    return enterprise;" + NL + "" + NL + "                }" + NL + "                        " + NL + "            }            " + NL + "" + NL + "            //Organization o = new Organization ();" + NL + "" + NL + "            //o.setOrganizationId (new Long (0));" + NL + "" + NL + "            return null;" + NL + "            ";
  protected final String TEXT_45 = NL + "\t}" + NL + "" + NL + "\t\t//This will return Operation Id depending upon user's Role and OperationType" + NL + "\tprivate int getOperationId (CPFConstants.OperationType opType){" + NL + "\t\tint opId = -99;" + NL + "\t\tFacesContext ctx = FacesContext.getCurrentInstance();" + NL + "\t\tExternalContext exCtx = ctx.getExternalContext();";
  protected final String TEXT_46 = NL + "\t\t\tif(opType.equals(CPFConstants.OperationType.";
  protected final String TEXT_47 = ")) {";
  protected final String TEXT_48 = NL + "\t\t\t\tif(";
  protected final String TEXT_49 = ") {" + NL + "\t\t\t\t\topId = ";
  protected final String TEXT_50 = ";" + NL + "\t\t\t\t}";
  protected final String TEXT_51 = NL + "\t\t\t}";
  protected final String TEXT_52 = NL + "\t\treturn opId;" + NL + "\t\t//return -1;" + NL + "\t}" + NL + "" + NL + "\tprivate void userIsInRole (FacesContext context) throws CPFException {" + NL + "" + NL + "\t\tExternalContext exContext = context.getExternalContext();";
  protected final String TEXT_53 = NL + "\t\t\tif (exContext.isUserInRole(\"";
  protected final String TEXT_54 = "\")) {" + NL + "\t\t\t\tthis.setUserRole(\"";
  protected final String TEXT_55 = "\");" + NL + "\t\t\t} else";
  protected final String TEXT_56 = NL + "\t\t\t{" + NL + "\t\t\t\tthrow new CPFException(\"Not Authenticated please contact provider for necessary  privileges\", 4046);" + NL + "\t\t\t}";
  protected final String TEXT_57 = NL + "\t\tif(";
  protected final String TEXT_58 = ") {" + NL + "\t\t\tthis.canCreate = true;" + NL + "\t\t} else {" + NL + "\t\t\tthis.canCreate = false;" + NL + "\t\t}";
  protected final String TEXT_59 = NL + "\t\t\tthis.canCreate = false;";
  protected final String TEXT_60 = ") {" + NL + "\t\t\tthis.canModify = true;" + NL + "\t\t} else {" + NL + "\t\t\tthis.canModify = false;" + NL + "\t\t}";
  protected final String TEXT_61 = NL + "\t\t\tthis.canModify = false;";
  protected final String TEXT_62 = ") {" + NL + "\t\t\tthis.canView = true;" + NL + "\t\t} else {" + NL + "\t\t\tthis.canView = false;" + NL + "\t\t}";
  protected final String TEXT_63 = NL + "\t\t\tthis.canView = false;";
  protected final String TEXT_64 = ") {" + NL + "\t\t\tthis.canDelete = true;" + NL + "\t\t} else {" + NL + "\t\t\tthis.canDelete = false;" + NL + "\t\t}";
  protected final String TEXT_65 = NL + "\t\t\tthis.canDelete = false;";
  protected final String TEXT_66 = NL + "\t\t//} else";
  protected final String TEXT_67 = NL + "\t\t//{" + NL + "\t\t//\tthrow new CPFException(\"Not Authenticated please contact provider for necessary  privileges\", 4046);" + NL + "\t\t//}" + NL + "\t}" + NL + "" + NL + "//Start of getting visibility for listing columns\t" + NL + "" + NL + "\tprivate void getListVisibility(String userRole) {" + NL + "" + NL + "\t\tthis.listVisibility = new ArrayList<Boolean>();" + NL;
  protected final String TEXT_68 = NL + NL + "\t\tif(userRole.equals(\"";
  protected final String TEXT_69 = "\")) {" + NL;
  protected final String TEXT_70 = NL + NL + "\t\t\tthis.listVisibility.add(true);" + NL;
  protected final String TEXT_71 = NL + NL + "\t\t\tthis.listVisibility.add(false);" + NL;
  protected final String TEXT_72 = NL + NL + "\t\t}" + NL;
  protected final String TEXT_73 = NL + NL + "\t}" + NL + "" + NL + "//End of getting visibility for listing columns" + NL;
  protected final String TEXT_74 = "\t" + NL + "" + NL + "\tprivate void loadAttbsOperators(String userRole) {" + NL + "" + NL + "\t\t\t//Independent of User Role" + NL + "\t\tsearchFields = new HashMap<String, SelectItemGroup>();" + NL + "\t\tMap<String, SelectItemGroup> temp = new HashMap<String, SelectItemGroup>();" + NL + "\t\tSelectItemGroup selectItemGrp = null;" + NL + "\t\tSelectItem[] supportedOperators = null;" + NL + "\t\tselectItemGrp = new SelectItemGroup();" + NL + "\t\tsupportedOperators = new SelectItem[6];" + NL + "\t\tsupportedOperators[0] = new SelectItem();" + NL + "\t\tsupportedOperators[0].setLabel(CPFConstants.Operators.EQUAL.toString());" + NL + "\t\tsupportedOperators[0].setValue(CPFConstants.Operators.EQUAL.toString());" + NL + "\t\tsupportedOperators[1] = new SelectItem();" + NL + "\t\tsupportedOperators[1].setLabel(CPFConstants.Operators.GREATER_THAN.toString());" + NL + "\t\tsupportedOperators[1].setValue(CPFConstants.Operators.GREATER_THAN.toString());" + NL + "\t\tsupportedOperators[2] = new SelectItem();" + NL + "\t\tsupportedOperators[2].setLabel(CPFConstants.Operators.GREATER_THAN_EQUAL.toString());" + NL + "\t\tsupportedOperators[2].setValue(CPFConstants.Operators.GREATER_THAN_EQUAL.toString());" + NL + "\t\tsupportedOperators[3] = new SelectItem();" + NL + "\t\tsupportedOperators[3].setLabel(CPFConstants.Operators.LESS_THAN.toString());" + NL + "\t\tsupportedOperators[3].setValue(CPFConstants.Operators.LESS_THAN.toString());" + NL + "\t\tsupportedOperators[4] = new SelectItem();" + NL + "\t\tsupportedOperators[4].setLabel(CPFConstants.Operators.LESS_THAN_EQUAL.toString());" + NL + "\t\tsupportedOperators[4].setValue(CPFConstants.Operators.LESS_THAN_EQUAL.toString());" + NL + "\t\tsupportedOperators[5] = new SelectItem();" + NL + "\t\tsupportedOperators[5].setLabel(CPFConstants.Operators.NOT_EQUAL.toString());" + NL + "\t\tsupportedOperators[5].setValue(CPFConstants.Operators.NOT_EQUAL.toString());" + NL + "\t\t" + NL + "\t\tselectItemGrp.setSelectItems(supportedOperators);" + NL + "\t\t" + NL + "\t\ttemp.put(\"NUMERIC\", selectItemGrp);" + NL + "\t\t" + NL + "\t\tselectItemGrp = new SelectItemGroup();" + NL + "\t\tsupportedOperators = new SelectItem[3];" + NL + "\t\tsupportedOperators[0] = new SelectItem();" + NL + "\t\tsupportedOperators[0].setLabel(CPFConstants.Operators.CONTAINS.toString());" + NL + "\t\tsupportedOperators[0].setValue(CPFConstants.Operators.CONTAINS.toString());" + NL + "\t\tsupportedOperators[1] = new SelectItem();" + NL + "\t\tsupportedOperators[1].setLabel(CPFConstants.Operators.NOT_CONTAINS.toString());" + NL + "\t\tsupportedOperators[1].setValue(CPFConstants.Operators.NOT_CONTAINS.toString());" + NL + "\t\tsupportedOperators[2] = new SelectItem();" + NL + "\t\tsupportedOperators[2].setLabel(CPFConstants.Operators.EQUAL.toString());" + NL + "\t\tsupportedOperators[2].setValue(CPFConstants.Operators.EQUAL.toString());" + NL + "\t\tselectItemGrp.setSelectItems(supportedOperators);" + NL + "\t\ttemp.put(\"TEXT\", selectItemGrp);" + NL + "\t\t" + NL + "\t\tattbs = new ArrayList<SelectItem>();" + NL + "\t\tFacesContext ctx = FacesContext.getCurrentInstance();" + NL + "\t\tLocale currentLocal=ctx.getViewRoot().getLocale();" + NL + "\t\tString code=currentLocal.getLanguage() + \"_\"+ currentLocal.getCountry();" + NL + "\t\tInputStream stream=this.getClass().getClassLoader().getResourceAsStream(\"bundle/resources_\"+code+\".properties\");" + NL + "\t\tif(stream==null){" + NL + "\t\t\tstream=this.getClass().getClassLoader().getResourceAsStream(\"bundle/resources.properties\");" + NL + "\t\t}" + NL + "\t\tProperties bundle=new Properties();" + NL + "\t    try {" + NL + "\t\t\tbundle.load(stream);" + NL + "\t\t} catch (IOException e) {" + NL + "\t\t\te.printStackTrace();" + NL + "\t\t}";
  protected final String TEXT_75 = NL + NL + "\t\tSelectItem s";
  protected final String TEXT_76 = " = new SelectItem();" + NL + "\t\ts";
  protected final String TEXT_77 = ".setValue(\"";
  protected final String TEXT_78 = "\");" + NL + "\t\ts";
  protected final String TEXT_79 = ".setLabel(bundle.getProperty(\"";
  protected final String TEXT_80 = "\"));" + NL + "\t\tattbs.add(s";
  protected final String TEXT_81 = "); " + NL + "\t\tsearchFields.put(\"";
  protected final String TEXT_82 = "\", temp.get(\"";
  protected final String TEXT_83 = "\"));";
  protected final String TEXT_84 = NL + NL + "\t\t//depends upon User Role";
  protected final String TEXT_85 = "\")) {";
  protected final String TEXT_86 = " " + NL + "" + NL + "\t\t\t\t\tattbs.remove(\"";
  protected final String TEXT_87 = "\");" + NL + "\t\t\t\t\tsearchFields.remove(\"";
  protected final String TEXT_88 = NL + NL + "\t\t\t\t\tthis.searchKey = \"";
  protected final String TEXT_89 = "\";";
  protected final String TEXT_90 = NL + NL + "\t\t}";
  protected final String TEXT_91 = NL + NL + "\t}";
  protected final String TEXT_92 = NL + NL + "\tpublic String r2Parent(ActionEvent e) {" + NL + "\t\tthis.initialCall = true;" + NL + "\t\tthis.searchValue = null;" + NL + "\t\tthis.primaryKeyValue = null;" + NL + "\t\tFacesContext ctx = FacesContext.getCurrentInstance();" + NL + "\t\tELContext elCtx = ctx.getELContext();" + NL + "\t\tExpressionFactory exF = ctx.getApplication().getExpressionFactory();" + NL + "\t\tValueExpression ve = exF.createValueExpression(elCtx, \"#{listMBean";
  protected final String TEXT_93 = "}\", ListMBean_";
  protected final String TEXT_94 = ".class);" + NL + "\t\tListMBean_";
  protected final String TEXT_95 = " res = (ListMBean_";
  protected final String TEXT_96 = ") ve.getValue(elCtx);" + NL + "\t\tres.setMode(0);\t" + NL + "\t\treturn \"SUCCESS\";" + NL + "\t}" + NL + "" + NL + "\tpublic String action(ActionEvent e) {" + NL + "\t" + NL + "\t\tFacesContext ctx = FacesContext.getCurrentInstance();" + NL + "\t\tExternalContext ex = ctx.getExternalContext();" + NL + "\t\tString val1 = ex.getRequestParameterMap().get(\"pkValue\");\t//Getting primaryKey value to which nested list has to display from request parameters" + NL + "\t\tthis.primaryKeyValue = new Long(val1);" + NL + "\t\tthis.mode = 0;" + NL + "\t\treturn \"SUCCESS\";" + NL + "\t}";
  protected final String TEXT_97 = NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CPFPlugin LOG = CPFPlugin.getDefault();

		//CPFResource will come as an argument to this function Generate
	CPFResource CPFArgument = (CPFResource)argument; 

 	CPFScreen cpfScreen = (CPFScreen)CPFArgument.getCpfScreen();

 	ResourceInfo resourceInfo = (ResourceInfo)CPFArgument.getResourceInfo();

 	String resourceName = resourceInfo.getResourceName();

 	ModelEntity modelEntity = null;

 	modelEntity = (ModelEntity)cpfScreen.getBaseEntity();

 	ModelEntity baseEntity = cpfScreen.getPortletRef().getBaseEntity();	

 	List<CPFAttribute> selectedAttributes = null;				

 	int operationId = cpfScreen.getPortletRef().getPortletId();		//Holds Operation Id of the method

	Map<RelationKey, List<CPFAttribute>> nestedAttributes = cpfScreen.getNestedAttributes();

	List<CPFConstants.OperationType> actionsSupported  = cpfScreen.getActionsSupported ();

	String fields = new String();

	String from = new String();

	String where = new String();

	List<List<String>> attrbVisibility = new ArrayList<List<String>>();		//Holds values related to getListVisibility()

		//Holds Text for String and Numeric for other search supported data types and NOTPOSSIBLE otherwise
	List<String> dataTypes = new ArrayList<String>();			//FOR SEARCHING PURPOSE ONLY

		//Holds EntityName.Attribtuename to set this in criteria
	List<String> attbsValues = new ArrayList<String>();			//FOR SEARCHING PURPOSE ONLY
	
	List<String> attbsLabels = new ArrayList<String>();	

		//Holds true if the column represents a many relation Link else false
	List<Boolean> nestedLink = new ArrayList<Boolean>();		//FOR SEARCHING PURPOSE ONLY

	String mainPropertyName = null;	//Holds PropertyName of resource Name in Base Entity if resource Name is not equals to base entity name	

 	LOG.info("generating managed Bean for : " + resourceName); 	

 	selectedAttributes = cpfScreen.getSelectedAttributes ();
 	
 	if(CPFArgument.getOperationId() > 0) {

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

 	} 	

 	/**
 	*Doing process for Seleceted attributes......Getting criteria, attr visibility, data types, etc...
 	**/

 	LOG.info("Started process for Seleceted attributes......Getting criteria, attr visibility, data types, etc... ");

 	Iterator<CPFAttribute> itrCPFAttributes = selectedAttributes.iterator();

 	int counterForTest = 0;

 	while(itrCPFAttributes.hasNext()) {

 		CPFAttribute cpfAttribute = (CPFAttribute)itrCPFAttributes.next();

 		ModelAttribute modelAttribute = cpfAttribute.getModelAttrib();

 		CPFConstants.AttributeDataType dt = null;

 		dt = modelAttribute.getDataType();

 		modelAttribute.getDataType();

 		String temp = new String();
 		
 		String tempLabel =new String();
 		
 		int portletId = cpfScreen.getPortletRef().getPortletId();	
 		
 		String baseEntityName = cpfScreen.getPortletRef().getBaseEntity().getName();
 		
 		tempLabel = tempLabel.concat("L"+portletId+"_");

		if(CPFArgument.getOperationId() > 0 && resourceName.equals(baseEntity.getName())) {
		
			fields = fields.concat(mainPropertyName);
        	temp = temp.concat(mainPropertyName);
			
		} else {		
		
 			fields = fields.concat(resourceName);
        	temp = temp.concat(resourceName);
 			
 		}
        //temp = temp.concat(resourceName);
        
        if(CPFArgument.getOperationId() > 0) {
        
        	tempLabel =tempLabel.concat(mainPropertyName);
        	
        } else {
        
        	tempLabel =tempLabel.concat(baseEntityName);
        	
        }
         
 		fields = fields.concat(".");
 		
        temp = temp.concat(".");
         
        tempLabel = tempLabel.concat("_"); //reeta

 		fields = fields.concat(modelAttribute.getName());

// reeta
        String label=cpfAttribute.getLabel();
        
        if(label != null) {
        
           if(label.indexOf(" ")!=-1){
           
        	label=label.replace(" ","_");
        	
        	}
         	tempLabel = tempLabel.concat(label);
        }
 		temp = temp.concat(modelAttribute.getName());


 		fields = fields.concat(", ");

 		if(cpfAttribute.getRolesException() != null) {

 			List<String> rolesList = cpfAttribute.getRolesException().get(CPFConstants.OperationType.LIST);

 			attrbVisibility.add(rolesList);

 		} else {

 			attrbVisibility.add(null);

 		}

 		LOG.info("Attb Vis counter : " + counterForTest++);

 		if(dt != null && dt.equals(CPFConstants.AttributeDataType.TEXT)) {

 			dataTypes.add("TEXT");

 		} else if(dt != null && (dt.equals(CPFConstants.AttributeDataType.NUMERIC) 

 					|| dt.equals(CPFConstants.AttributeDataType.INTEGRAL) 

 						|| dt.equals(CPFConstants.AttributeDataType.DATE)

 							|| dt.equals(CPFConstants.AttributeDataType.INTERVAL)

 								|| dt.equals(CPFConstants.AttributeDataType.TIMESTAMP))) {

 			dataTypes.add("NUMERIC");	

 		} else {

 			dataTypes.add("NOTPOSSIBLE");

 		}

 		attbsValues.add(temp);
 		attbsLabels.add(tempLabel);
        nestedLink.add(false);

 	}

	fields = fields.substring(0, fields.lastIndexOf(","));

	//if(!resourceName.equals(baseEntity.getName())) {

	if(CPFArgument.getOperationId() > 0) {

		from = from.concat(baseEntity.getName());

		from = from.concat(" ");

		from = from.concat(baseEntity.getName());

		from = from.concat(" left join ");

		from = from.concat(baseEntity.getName() + ".");

		from = from.concat(mainPropertyName);

		from = from.concat(" ");
		
		if (resourceName.equals(baseEntity.getName())) {
			from = from.concat(mainPropertyName);
		} else {
			from = from.concat(resourceName);
		}

	} else {

		from = from.concat(resourceName);

		from = from.concat(" ");

		from = from.concat(resourceName);

	}

	

		//Only in case of generating Managed bean for Base Entity ....

		//Edited on 23rd Feb..

	//if(resourceName.equals(baseEntity.getName ()) && nestedAttributes != null) {

	if(CPFArgument.getOperationId() == 0 && nestedAttributes != null) {

		LOG.info("Doing for dependent entities..");

		Iterator<RelationKey> itrModelEntity = nestedAttributes.keySet().iterator();

		while(itrModelEntity.hasNext()) {

			RelationKey relationKey = itrModelEntity.next();  

			List<CPFAttribute> cpfAttributes = nestedAttributes.get(relationKey);

			RelationShipInfo tempRSI = relationKey.getRelationShipInfo();

			CPFConstants.RelationshipType relation = tempRSI.getMapping(); 

			//ModelEntity tempModelEntity = relationKey.getReferencedEntity();

			if((!relation.equals(CPFConstants.RelationshipType.OneToMany)) && (!relation.equals(CPFConstants.RelationshipType.ManyToMany))) {

				RelationShipInfo relationShipInfo = tempRSI;

				from = from.concat(" left join ");

				from = from.concat(baseEntity.getName());

				from = from.concat(".");

				from = from.concat(relationShipInfo.getPropertyName());

				from = from.concat(" ");

				//from = from.concat(tempModelEntity.getName());

				from = from.concat(relationShipInfo.getPropertyName());

		

				Iterator<CPFAttribute> itrCpfAttributes = cpfAttributes.iterator();

				while(itrCpfAttributes.hasNext()) {

					CPFAttribute otherCpfAttribute = (CPFAttribute)itrCpfAttributes.next();

					ModelAttribute otherModelAttribute = otherCpfAttribute.getModelAttrib();

					CPFConstants.AttributeDataType dt = otherModelAttribute.getDataType();

					String temp = new String();
					String tempLabel= new String();

					fields = fields.concat(", ");

					//fields = fields.concat(tempModelEntity.getName());

					fields = fields.concat(relationShipInfo.getPropertyName());

					temp = temp.concat(relationShipInfo.getPropertyName());

                    //Reeta added
                    int portletId = cpfScreen.getPortletRef().getPortletId();	
 		            tempLabel = tempLabel.concat("L"+portletId+"_");
					tempLabel = tempLabel.concat(relationShipInfo.getPropertyName());

					fields = fields.concat(".");
                    temp = temp.concat("."); 
					//reeta
					tempLabel= tempLabel.concat("_");
					

					fields = fields.concat(otherModelAttribute.getName());

                    //reeta
                     String label=otherCpfAttribute.getLabel();
                     if(label!=null){
                       if(label.indexOf(" ")!=-1)
                      label=label.replace(" " ,"_");
                     }
                     tempLabel = tempLabel.concat(label);
					temp = temp.concat(otherModelAttribute.getName());

                   
                     
					if(otherCpfAttribute.getRolesException() != null) {

						List<String> rolesList = otherCpfAttribute.getRolesException().get(CPFConstants.OperationType.LIST);

 						attrbVisibility.add(rolesList);

 					} else {

 						attrbVisibility.add(null);

 					}

 					LOG.info("Attb Vis counter : " + counterForTest++);

 					if(dt.equals(CPFConstants.AttributeDataType.TEXT)) {

 						dataTypes.add("TEXT");

 					} else if(dt.equals(CPFConstants.AttributeDataType.NUMERIC) 

 								|| dt.equals(CPFConstants.AttributeDataType.INTEGRAL) 

 									|| dt.equals(CPFConstants.AttributeDataType.DATE)

 										|| dt.equals(CPFConstants.AttributeDataType.INTERVAL)

 											|| dt.equals(CPFConstants.AttributeDataType.TIMESTAMP)) {

 						dataTypes.add("NUMERIC");

 					} else {

 						dataTypes.add("NOTPOSSIBLE");

 					}

 					attbsValues.add(temp);
 					attbsLabels.add(tempLabel);
                    nestedLink.add(false);

				}

			} else {

				List<String> rolesList = null;

				attrbVisibility.add(rolesList);	//TODO How to get rolesList in case of nested?..

				String jspName = cpfScreen.getNestedJspNames().get(relationKey).getJspName();

				LOG.info("Attb Vis counter nested relation : " + jspName + " : " + counterForTest++);

				dataTypes.add("NOTPOSSIBLE");

				nestedLink.add(true);

				attbsValues.add("NOTPOSSIBLE");
				
				attbsLabels.add(null);

			}

		}

	}

		//Added cpfScreen.getExtraListPredicate ().trim().length() > 0 on 16th Feb

	if(cpfScreen.getExtraListPredicate () != null && cpfScreen.getExtraListPredicate ().trim().length() > 0) {

		LOG.info("Filter has been defined for this screen...and adding it as where field..");

		where = where.concat(cpfScreen.getExtraListPredicate ());

		where = where.concat(" and ");

	}

	//if(!resourceName.equals(baseEntity.getName())) {

	if(CPFArgument.getOperationId() > 0) {

		where = where.concat(baseEntity.getName());

		where = where.concat(".");

		where = where.concat(baseEntity.getPrimaryKey());

		where = where.concat("=\" + ");

		where = where.concat("this.primaryKeyValue + ");

		where = where.concat(" \" and  ");

	}

	where = where.concat(baseEntity.getName());

	where = where.concat(".merchantAccount= (select merchantAccount from Organization merchantAccount where merchantAccount.organizationId=");

	

	String className = new String();

	

	if(CPFArgument.getOperationId() > 0) {

		className = "ListMBean" + operationId + "_nested" + CPFArgument.getClassName();

	} else {

		className = "ListMBean_"+operationId;

	}

	LOG.info("Adding imports to Class..");


    stringBuffer.append(TEXT_2);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( cpfScreen.getJspName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( new java.util.Date(System.currentTimeMillis()) );
    stringBuffer.append(TEXT_5);
    

	if(CPFArgument.getOperationId() > 0) {


    stringBuffer.append(TEXT_6);
    

	}


    stringBuffer.append(TEXT_7);
    stringBuffer.append( baseEntity.getCanonicalTypeName() );
    stringBuffer.append(TEXT_8);
    

	if(!modelEntity.getName().equals(baseEntity.getName())) {


    stringBuffer.append(TEXT_9);
    stringBuffer.append( modelEntity.getCanonicalTypeName() );
    stringBuffer.append(TEXT_8);
    

	}


    

	if(!baseEntity.getName().equals("Organization")) {


    stringBuffer.append(TEXT_10);
    

	}


    stringBuffer.append(TEXT_11);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_12);
    

	LOG.info("Declaring variables in clss");

	if(actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {


    stringBuffer.append(TEXT_13);
    

	}


    stringBuffer.append(TEXT_14);
    

	//if(actionsSupported.contains(CPFConstants.OperationType.SORT)) {


    stringBuffer.append(TEXT_15);
    

	//}


    stringBuffer.append(TEXT_16);
    stringBuffer.append( cpfScreen.getPreference().getPagination() );
    stringBuffer.append(TEXT_17);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_18);
    

	LOG.info("Defining Getters and Setters...");

	if(actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {


    stringBuffer.append(TEXT_19);
    

	}


    stringBuffer.append(TEXT_20);
    

	if(actionsSupported.contains(CPFConstants.OperationType.SORT)) {


    stringBuffer.append(TEXT_21);
    

	}


    stringBuffer.append(TEXT_22);
    

		if(actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {


    stringBuffer.append(TEXT_23);
    

		}


    

		if(CPFArgument.getOperationId() == 0) {


    stringBuffer.append(TEXT_24);
    

		}


    stringBuffer.append(TEXT_25);
    

	LOG.info("Definign getCollec method in class..");

	if(actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {


    stringBuffer.append(TEXT_26);
    

	}


    stringBuffer.append(TEXT_27);
    
				if(CPFArgument.getOperationId() > 0 && resourceName.equals(baseEntity.getName())) {

    stringBuffer.append(TEXT_28);
    stringBuffer.append( mainPropertyName );
    stringBuffer.append(TEXT_29);
    stringBuffer.append( selectedAttributes.get(1).getModelAttrib().getName() );
    stringBuffer.append(TEXT_30);
    
				} else {

    stringBuffer.append(TEXT_28);
    stringBuffer.append( modelEntity.getName() );
    stringBuffer.append(TEXT_29);
    stringBuffer.append( selectedAttributes.get(1).getModelAttrib().getName() );
    stringBuffer.append(TEXT_30);
    
				}

    stringBuffer.append(TEXT_31);
    

	if(actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {

	LOG.info("Search is supported and defining search method in Class..");


    stringBuffer.append(TEXT_32);
    

	}


    

	LOG.info("Sort is also supported and defining sort method to Class");

	if(actionsSupported.contains(CPFConstants.OperationType.SORT)) {


    stringBuffer.append(TEXT_33);
    

	}

	LOG.info("Adding pageChange method to clas...");


    stringBuffer.append(TEXT_34);
    stringBuffer.append( resourceName );
    stringBuffer.append(TEXT_35);
    stringBuffer.append( resourceName );
    stringBuffer.append(TEXT_36);
    stringBuffer.append( resourceName );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( fields );
    stringBuffer.append(TEXT_38);
    stringBuffer.append( from );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( where );
    stringBuffer.append(TEXT_40);
    stringBuffer.append( modelEntity.getName() );
    stringBuffer.append(TEXT_41);
    stringBuffer.append( modelEntity.getPrimaryKey () );
    stringBuffer.append(TEXT_42);
    

	if(modelEntity.isStatic()) {


    stringBuffer.append(TEXT_43);
    

	} else {


    stringBuffer.append(TEXT_44);
    

	}


    stringBuffer.append(TEXT_45);
    
		LOG.info("Adding getOperaionId method to Class...");
		//Iterator<CPFConstants.OperationType> supportedOpTypes = actionsSupported.iterator();
		Iterator<CPFConstants.OperationType> supportedOpTypes = cpfScreen.getMappedRoles().keySet().iterator();
		while(supportedOpTypes.hasNext()) {
			CPFConstants.OperationType operation = supportedOpTypes.next();
			String operationType = operation.toString();
			//if(operation.equals(CPFConstants.OperationType.LIST) || operation.equals(CPFConstants.OperationType.DELETE)) {

    stringBuffer.append(TEXT_46);
    stringBuffer.append( operationType );
    stringBuffer.append(TEXT_47);
    
				Map<Integer, String[]> mappedRoles = cpfScreen.getOperationRoleMap(operation);
				if(mappedRoles != null) {
				Iterator<Integer> mappedOperationId = mappedRoles.keySet().iterator();
				while(mappedOperationId.hasNext()) {
					Integer opId = mappedOperationId.next();
					String ifCondition = new String();
					String[] roles = mappedRoles.get(opId);
					for (String roleName : roles) {
						ifCondition = ifCondition.concat("exCtx.isUserInRole(\"");
						ifCondition = ifCondition.concat(roleName);
						ifCondition = ifCondition.concat("\") || ");
					}  
					ifCondition = ifCondition.substring(0, ifCondition.lastIndexOf(")")+1);

    stringBuffer.append(TEXT_48);
    stringBuffer.append( ifCondition );
    stringBuffer.append(TEXT_49);
    stringBuffer.append( opId.intValue() );
    stringBuffer.append(TEXT_50);
    
				}
				}

    stringBuffer.append(TEXT_51);
    		
			//}
		}

    stringBuffer.append(TEXT_52);
    
		LOG.info("Adding userUsInRole method to Class...");
		List<String> roles = cpfScreen.getMappedRoles().get(CPFConstants.OperationType.LIST);
		Iterator<String> itrRoles = roles.iterator();
		while(itrRoles.hasNext()) {
			String roleIs = itrRoles.next();

    stringBuffer.append(TEXT_53);
    stringBuffer.append( roleIs );
    stringBuffer.append(TEXT_54);
    stringBuffer.append( roleIs );
    stringBuffer.append(TEXT_55);
    
		}

    stringBuffer.append(TEXT_56);
    
		String ifCond4Role = null;

    
			if(actionsSupported.contains(CPFConstants.OperationType.CREATE)
				&& cpfScreen.getPortletRef().getDetailsScreen().getMappedRoles()
					.get(CPFConstants.OperationType.CREATE) != null) {

				List<String> createRoles = cpfScreen.getPortletRef().getDetailsScreen()
											.getMappedRoles().get(CPFConstants.OperationType.CREATE);
				ifCond4Role = new String();
				for(Iterator<String> itrRole=createRoles.iterator(); itrRole.hasNext();itrRole.hasNext()) {
					ifCond4Role = ifCond4Role.concat("exContext.isUserInRole(");
					ifCond4Role = ifCond4Role.concat("\"" + itrRole.next() + "\")");
					if(itrRole.hasNext()) {
						ifCond4Role = ifCond4Role.concat(" || ");
					}
				}
				//if(createRoles.contains(roleIs)) {

    stringBuffer.append(TEXT_57);
    stringBuffer.append( ifCond4Role );
    stringBuffer.append(TEXT_58);
    
				//}
			} else {	//End of if(actionsSupported.contains(CPFConstants.OperationType.CREATE))

    stringBuffer.append(TEXT_59);
    
			}
			
			if(actionsSupported.contains(CPFConstants.OperationType.MODIFY)
				&& cpfScreen.getPortletRef().getDetailsScreen().getMappedRoles()
					.get(CPFConstants.OperationType.MODIFY) != null) {

				List<String> modifyRoles = cpfScreen.getPortletRef().getDetailsScreen()
											.getMappedRoles().get(CPFConstants.OperationType.MODIFY);
				ifCond4Role = new String();
				for(Iterator<String> itrRole=modifyRoles.iterator(); itrRole.hasNext();itrRole.hasNext()) {
					ifCond4Role = ifCond4Role.concat("exContext.isUserInRole(");
					ifCond4Role = ifCond4Role.concat("\"" + itrRole.next() + "\")");
					if(itrRole.hasNext()) {
						ifCond4Role = ifCond4Role.concat(" || ");
					}
				}
				//if(modifyRoles.contains(roleIs)) {

    stringBuffer.append(TEXT_57);
    stringBuffer.append( ifCond4Role );
    stringBuffer.append(TEXT_60);
    
				//}
			} else {	//End of if(actionsSupported.contains(CPFConstants.OperationType.MODIFY))

    stringBuffer.append(TEXT_61);
    
			}
			if(actionsSupported.contains(CPFConstants.OperationType.VIEW)
				&& cpfScreen.getPortletRef().getDetailsScreen().getMappedRoles()
					.get(CPFConstants.OperationType.VIEW) != null) {

				List<String> viewRoles = cpfScreen.getPortletRef().getDetailsScreen()
											.getMappedRoles().get(CPFConstants.OperationType.VIEW);
				ifCond4Role = new String();
				for(Iterator<String> itrRole=viewRoles.iterator(); itrRole.hasNext();itrRole.hasNext()) {
					ifCond4Role = ifCond4Role.concat("exContext.isUserInRole(");
					ifCond4Role = ifCond4Role.concat("\"" + itrRole.next() + "\")");
					if(itrRole.hasNext()) {
						ifCond4Role = ifCond4Role.concat(" || ");
					}
				}
				//if(viewRoles.contains(roleIs)) {

    stringBuffer.append(TEXT_57);
    stringBuffer.append( ifCond4Role );
    stringBuffer.append(TEXT_62);
    
			//	}
			} else {	//End of if(actionsSupported.contains(CPFConstants.OperationType.VIEW))

    stringBuffer.append(TEXT_63);
    
			}
			if(actionsSupported.contains(CPFConstants.OperationType.DELETE)
				&& cpfScreen.getMappedRoles().get(CPFConstants.OperationType.DELETE) != null) {

				List<String> deleteRoles = cpfScreen.getMappedRoles().get(CPFConstants.OperationType.DELETE);
				ifCond4Role = new String();
				for(Iterator<String> itrRole=deleteRoles.iterator(); itrRole.hasNext();itrRole.hasNext()) {
					ifCond4Role = ifCond4Role.concat("exContext.isUserInRole(");
					ifCond4Role = ifCond4Role.concat("\"" + itrRole.next() + "\")");
					if(itrRole.hasNext()) {
						ifCond4Role = ifCond4Role.concat(" || ");
					}
				}
				//if(deleteRoles.contains(roleIs)) {

    stringBuffer.append(TEXT_57);
    stringBuffer.append( ifCond4Role );
    stringBuffer.append(TEXT_64);
    
				//}
			} else {	//End of if(actionsSupported.contains(CPFConstants.OperationType.DELETE))

    stringBuffer.append(TEXT_65);
    
			}

    stringBuffer.append(TEXT_66);
    
		//}  //End of while();

    stringBuffer.append(TEXT_67);
    

		LOG.info("Adding getListVisibility method to Class...");

		itrRoles = roles.iterator();

		while(itrRoles.hasNext()) {

			String roleIs = itrRoles.next();


    stringBuffer.append(TEXT_68);
    stringBuffer.append( roleIs );
    stringBuffer.append(TEXT_69);
    

			Iterator<List<String>> itrAttrbVisibility = attrbVisibility.iterator();

			while(itrAttrbVisibility.hasNext()) {

				List<String> tempRoles = itrAttrbVisibility.next();

				if(tempRoles == null) {


    stringBuffer.append(TEXT_70);
    

				} else if(tempRoles.size() == 0 || !tempRoles.contains(roleIs)) {


    stringBuffer.append(TEXT_70);
    

				} else {


    stringBuffer.append(TEXT_71);
    

				}

			}


    stringBuffer.append(TEXT_72);
    

		}


    stringBuffer.append(TEXT_73);
    

	if(actionsSupported.contains(CPFConstants.OperationType.SEARCH)) {

	LOG.info("As Search is supporting adding loadAttbsOperato");


    stringBuffer.append(TEXT_74);
    
		Iterator<String> itrAttbsValues = attbsValues.iterator();
		Iterator<String> itrDataTypes = dataTypes.iterator();
	    Iterator<String> itrAttLabels = attbsLabels.iterator();
	    
			//If U donot add it in code generator then u can delete these two lines
		itrDataTypes.next();	//Because added primaryKey extra so omitting that one
		itrAttbsValues.next();	//Because added primaryKey extra so omitting that one
		itrAttLabels.next();

		int i = 0;
		while(itrAttbsValues.hasNext()) {
			String dt = itrDataTypes.next();
			String t = itrAttbsValues.next();
			String l = itrAttLabels.next();
//			String la = t.replace('.', ' ');
			if(!dt.equals("NOTPOSSIBLE")) {

    stringBuffer.append(TEXT_75);
    stringBuffer.append( i );
    stringBuffer.append(TEXT_76);
    stringBuffer.append( i );
    stringBuffer.append(TEXT_77);
    stringBuffer.append( t );
    stringBuffer.append(TEXT_78);
    stringBuffer.append( i );
    stringBuffer.append(TEXT_79);
    stringBuffer.append( l );
    stringBuffer.append(TEXT_80);
    stringBuffer.append( i );
    stringBuffer.append(TEXT_81);
    stringBuffer.append( t );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( dt );
    stringBuffer.append(TEXT_83);
    
			}
			i++;
		}

    stringBuffer.append(TEXT_84);
    
		itrRoles = roles.iterator();
		while(itrRoles.hasNext()) {
			String roleIs = itrRoles.next();
			boolean skBool = true;	//SearchKey Boolean To set SearchKey initially to load in JSF.. First Visible Attribute name is setting

    stringBuffer.append(TEXT_68);
    stringBuffer.append( roleIs );
    stringBuffer.append(TEXT_85);
    
			itrDataTypes = dataTypes.iterator();
			Iterator<Boolean> bool = nestedLink.iterator();
			Iterator<List<String>> itrAttrbVisibility = attrbVisibility.iterator();
			itrAttbsValues = attbsValues.iterator();
			
				//If U donot add it in code generator then u can delete these two lines
			itrDataTypes.next();	//Because added primaryKey extra so omitting that one
			itrAttbsValues.next();	//Because added primaryKey extra so omitting that one
			bool.next();			//Because added primaryKey extra so omitting that one
			itrAttrbVisibility.next();	//Because added primaryKey extra so omitting that one
			
			i = 0;
			while(itrDataTypes.hasNext()) {
				String dataType = itrDataTypes.next();
				String val = itrAttbsValues.next();
				List<String> vis = itrAttrbVisibility.next();
				boolean v = true;
				boolean b = bool.next();
				if(b) {
					continue;
				}
				if(vis != null && vis.contains(roleIs)) {
					v = false;
				}
				if(dataType.equals("NOTPOSSIBLE") || !v) {

    stringBuffer.append(TEXT_86);
    stringBuffer.append( i );
    stringBuffer.append(TEXT_87);
    stringBuffer.append( val );
    stringBuffer.append(TEXT_30);
    
				} else if(skBool){
					skBool = false;

    stringBuffer.append(TEXT_88);
    stringBuffer.append( val );
    stringBuffer.append(TEXT_89);
    
				}
				i++;
			}

    stringBuffer.append(TEXT_90);
    
		}

    stringBuffer.append(TEXT_91);
    
	}		//End of if loop for actionsSupported.contains(CPFConstants.OperationType.SEARCH)
	
		//These two functions are related to Nested Managed Beans only...
	if(CPFArgument.getOperationId() > 0) {
		LOG.info("As this is a nested screen managed bean adding r2Parent and action methods to the class to" +
					" return back to parent and come form parent into this screen ");

    stringBuffer.append(TEXT_92);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_93);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_94);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_95);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_96);
    
	}
	LOG.info("Managed Bean geenration completed from template side.....");

    stringBuffer.append(TEXT_97);
    return stringBuffer.toString();
  }
}
