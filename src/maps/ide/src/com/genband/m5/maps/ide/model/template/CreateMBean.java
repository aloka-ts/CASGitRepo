package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.model.*;
import com.genband.m5.maps.ide.CPFPlugin;
import java.util.*;
import java.util.List;
import com.genband.m5.maps.ide.model.util.*;;

public class CreateMBean
{
  protected static String nl;
  public static synchronized CreateMBean create(String lineSeparator)
  {
    nl = lineSeparator;
    CreateMBean result = new CreateMBean();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = NL + NL + "package com.genband.m5.maps.mbeans;" + NL + "import java.util.*;" + NL + "import java.util.List;" + NL + "import com.genband.m5.maps.common.CPFManager;" + NL + "import com.genband.m5.maps.common.PortletFacesUtils;" + NL + "import javax.faces.event.ActionEvent;" + NL + "import com.genband.m5.maps.common.CPFException;" + NL + "import com.genband.m5.maps.common.CPFConstants;" + NL + "import com.genband.m5.maps.common.Criteria;" + NL + "import com.genband.m5.maps.common.User;" + NL + "import com.genband.m5.maps.common.SS_Constants;" + NL + "" + NL + "import javax.faces.application.FacesMessage;" + NL + "import javax.faces.context.ExternalContext;" + NL + "import javax.faces.context.FacesContext;" + NL + "import javax.el.ELContext;" + NL + "import javax.el.ExpressionFactory;" + NL + "import javax.el.ValueExpression;" + NL + "import javax.portlet.PortletRequest;" + NL + "import javax.portlet.PortletSession;" + NL + "" + NL + "import java.io.IOException;" + NL + "import java.io.InputStream;" + NL + "import java.security.Principal;" + NL + "import com.genband.m5.maps.identity.GBUserPrincipal;" + NL + "import java.util.Set;" + NL + "import org.jboss.security.SecurityAssociation;";
  protected final String TEXT_3 = NL + "import ";
  protected final String TEXT_4 = ";";
  protected final String TEXT_5 = NL + NL + "/**" + NL + "\t\tThis is the managed Bean class for ";
  protected final String TEXT_6 = " " + NL + "\t\t@Genband.com" + NL + "*/" + NL + "public class  ";
  protected final String TEXT_7 = " {";
  protected final String TEXT_8 = NL + "\t\t";
  protected final String TEXT_9 = NL + "\t\tprivate static List<SelectItem> ";
  protected final String TEXT_10 = ";\t\t" + NL + "\t\tprivate int ";
  protected final String TEXT_11 = NL + "\t\tprivate Long ";
  protected final String TEXT_12 = NL + "\t\tprivate List<SelectItem> ";
  protected final String TEXT_13 = NL + "\t\tprivate int ";
  protected final String TEXT_14 = NL + "\t\tprivate int mode;" + NL + "\t\t" + NL + "\t\tprivate int uiMode;" + NL + "\t\tprivate Long primarykeyValue;" + NL + "\t\t" + NL + "\t\tprivate int operationId;" + NL + "\t\tprivate String userRole;" + NL + "\t\tprivate List<Boolean> listVisibility;" + NL + "\t\tprivate Criteria criteria;" + NL + "\t\t" + NL + "\t\tprivate String clientId;" + NL + "\t\t" + NL + "\t\tprivate String returnString;" + NL + "//Varibles declaration End..........................................." + NL + "\t\tpublic ";
  protected final String TEXT_15 = " () {" + NL + "\t\t}";
  protected final String TEXT_16 = NL + "\t\t\t//Loading Static data..." + NL + "\t\tstatic {" + NL + "\t\t\tSelectItem temp = null;";
  protected final String TEXT_17 = NL + NL + "\t\t\t";
  protected final String TEXT_18 = " = new ArrayList<SelectItem>();";
  protected final String TEXT_19 = NL + "\t\t\ttemp = new SelectItem();" + NL + "\t\t\ttemp.setValue(\"";
  protected final String TEXT_20 = "\");" + NL + "\t\t\ttemp.setLabel(\"";
  protected final String TEXT_21 = "\");" + NL + "\t\t\t";
  protected final String TEXT_22 = ".add(temp);";
  protected final String TEXT_23 = NL + "\t\t}\t";
  protected final String TEXT_24 = NL + "\t\tpublic ";
  protected final String TEXT_25 = " ";
  protected final String TEXT_26 = " () {" + NL + "\t\t\treturn this.";
  protected final String TEXT_27 = ";" + NL + "\t\t}";
  protected final String TEXT_28 = NL + "\t\tpublic Long get";
  protected final String TEXT_29 = ";" + NL + "\t\t}";
  protected final String TEXT_30 = NL + "\t\tpublic List<SelectItem> get";
  protected final String TEXT_31 = ";" + NL + "\t\t}" + NL + "\t\t";
  protected final String TEXT_32 = NL + "\t\tpublic int get";
  protected final String TEXT_33 = NL + "\t\tpublic List<SelectItem> ";
  protected final String TEXT_34 = ";" + NL + "\t\t} \t\t" + NL + "\t\tpublic int get";
  protected final String TEXT_35 = NL + "\t\tpublic int getMode () {" + NL + "\t\t\treturn this.mode;" + NL + "\t\t}\t\t" + NL + "\t\t" + NL + "\t\tpublic int getUiMode () {" + NL + "" + NL + "\t\t\treturn this.uiMode;" + NL + "" + NL + "\t\t}" + NL + "\t\tpublic Long getPrimarykeyValue () {" + NL + "\t\t\treturn this.primarykeyValue;" + NL + "\t\t}\t\t" + NL + "\t\tpublic String getUserRole() {" + NL + "\t\t\treturn userRole;" + NL + "\t\t}\t\t" + NL + "\t\tpublic List<Boolean> getListVisibility() {" + NL + "\t\t\treturn listVisibility;" + NL + "\t\t}\t\t" + NL + "\t\tpublic Criteria getCriteria() {" + NL + "\t\t\treturn criteria;" + NL + "\t\t}";
  protected final String TEXT_36 = NL + "\t\tpublic void ";
  protected final String TEXT_37 = " (";
  protected final String TEXT_38 = ") {" + NL + "\t\t\tthis.";
  protected final String TEXT_39 = " = ";
  protected final String TEXT_40 = ";" + NL + "\t\t}\t\t ";
  protected final String TEXT_41 = NL + "\t\tpublic void set";
  protected final String TEXT_42 = " (Long ";
  protected final String TEXT_43 = "(List<SelectItem> ";
  protected final String TEXT_44 = " (int ";
  protected final String TEXT_45 = " (List<SelectItem> temp) {" + NL + "\t\t\tthis.";
  protected final String TEXT_46 = ";" + NL + "\t\t}\t\t" + NL + "\t\tpublic void set";
  protected final String TEXT_47 = NL + "\t\tpublic void setMode (int mode) {" + NL + "\t\t\tthis.mode = mode;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void setUiMode (int uiMode) {" + NL + "" + NL + "\t\t\tthis.uiMode = uiMode;" + NL + "" + NL + "\t\t}" + NL + "\t\tpublic void setPrimarykeyValue (Long primarykeyValue) {" + NL + "\t\t\tthis.primarykeyValue = primarykeyValue;" + NL + "\t\t}\t\t\t" + NL + "\t\tpublic void setUserRole(String userRole) {" + NL + "\t\t\tthis.userRole = userRole;" + NL + "\t\t}" + NL + "\t\tpublic void setListVisibility(List<Boolean> listVisibility) {" + NL + "\t\t\tthis.listVisibility = listVisibility;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void setCriteria(Criteria criteria) {" + NL + "\t\t\tthis.criteria = criteria;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic String getClientId() {" + NL + "\t\t\treturn clientId;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\tpublic void setClientId(String clientId) {" + NL + "\t\t\tthis.clientId = clientId;" + NL + "\t\t}" + NL + "\t\t\t//Generating getObject Function which will set values for ModelEntity and returns ModelEntity.  This itself will do process for Distribute Data" + NL + "\t    private ";
  protected final String TEXT_48 = " getObject() {" + NL + "\t    \t";
  protected final String TEXT_49 = " returnEntity = new ";
  protected final String TEXT_50 = " ();";
  protected final String TEXT_51 = NL + "\t\t\tif(";
  protected final String TEXT_52 = " != null)";
  protected final String TEXT_53 = NL + "\t\t\t\treturnEntity.";
  protected final String TEXT_54 = ");";
  protected final String TEXT_55 = NL + "\t\t\t\tthis.";
  protected final String TEXT_56 = " = this.getIndex(this.";
  protected final String TEXT_57 = ".toString(), this.";
  protected final String TEXT_58 = ");";
  protected final String TEXT_59 = " = this.getIndex(CommonUtil.getWrapperForPrimitive(this.";
  protected final String TEXT_60 = ").toString(), this.";
  protected final String TEXT_61 = "\t" + NL + "\t\t\t\tif(this.";
  protected final String TEXT_62 = " != null) {";
  protected final String TEXT_63 = NL + "\t\t\t\t\t";
  protected final String TEXT_64 = " = new HashSet<";
  protected final String TEXT_65 = "> ();";
  protected final String TEXT_66 = " = new ArrayList<";
  protected final String TEXT_67 = NL + "\t\t\t\t\tfor (Iterator<Long> itr = ";
  protected final String TEXT_68 = ".iterator ()" + NL + "\t\t\t\t\t\t\t; itr.hasNext();) {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_69 = " = new ";
  protected final String TEXT_70 = " ();" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_71 = ".set";
  protected final String TEXT_72 = " (itr.next());";
  protected final String TEXT_73 = "\t\t\t\t\t\t" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_74 = " (returnEntity);\t\t//Added this line on 31st Jan for setting child Entity reference in Parent Entity";
  protected final String TEXT_75 = "\t\t " + NL + "\t\t\t\t\t\t";
  protected final String TEXT_76 = ".add (";
  protected final String TEXT_77 = ");" + NL + "\t\t\t\t\t} " + NL + "\t\t\t\t\treturnEntity.";
  protected final String TEXT_78 = ");" + NL + "\t\t\t\t}";
  protected final String TEXT_79 = NL + "\t\t\tif(this.";
  protected final String TEXT_80 = " != -1) {" + NL + "\t\t\t";
  protected final String TEXT_81 = " ();" + NL + "\t\t\t";
  protected final String TEXT_82 = " (this.";
  protected final String TEXT_83 = NL + "\t\t\t";
  protected final String TEXT_84 = " (returnEntity);";
  protected final String TEXT_85 = NL + "\t\t\treturnEntity.";
  protected final String TEXT_86 = ");" + NL + "\t\t\tthis.selected";
  protected final String TEXT_87 = ", this.";
  protected final String TEXT_88 = ");\t\t\t\t\t\t\t\t\t\t" + NL + "\t\t\t} else {" + NL + "\t\t\t\tthis.selected";
  protected final String TEXT_89 = " = -1; " + NL + "\t\t\t}";
  protected final String TEXT_90 = "<";
  protected final String TEXT_91 = "> ";
  protected final String TEXT_92 = "> ();";
  protected final String TEXT_93 = NL + "\t\t\tthis.";
  protected final String TEXT_94 = "), this.";
  protected final String TEXT_95 = ");s";
  protected final String TEXT_96 = "Temp = new Hash";
  protected final String TEXT_97 = "();";
  protected final String TEXT_98 = "Temp = new ArrayList<";
  protected final String TEXT_99 = ">();";
  protected final String TEXT_100 = NL + "\t\t\t\t\tIterator<Long> itr";
  protected final String TEXT_101 = ".iterator();" + NL + "\t\t\t\t\twhile(itr";
  protected final String TEXT_102 = ".hasNext()) {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_103 = " temp = new ";
  protected final String TEXT_104 = "();" + NL + "\t\t\t\t\t\ttemp.set";
  protected final String TEXT_105 = " (itr";
  protected final String TEXT_106 = ".next());";
  protected final String TEXT_107 = "\t\t\t\t\t\t" + NL + "\t\t\t\t\t\ttemp.set";
  protected final String TEXT_108 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_109 = "Temp.add(temp);" + NL + "\t\t\t\t\t}";
  protected final String TEXT_110 = NL + "\t\t\t\tif(this.";
  protected final String TEXT_111 = " != -1) {" + NL + "\t\t\t\t";
  protected final String TEXT_112 = "Temp = new ";
  protected final String TEXT_113 = " ();" + NL + "\t\t\t\t";
  protected final String TEXT_114 = "Temp.set";
  protected final String TEXT_115 = NL + "\t\t\t\t";
  protected final String TEXT_116 = "Temp);" + NL + "\t\t\t\tthis.selected";
  protected final String TEXT_117 = ");\t\t" + NL + "\t\t\t\t} else {" + NL + "\t\t\t\t\tthis.selected";
  protected final String TEXT_118 = " = -1;" + NL + "\t\t\t\t}";
  protected final String TEXT_119 = "(returnEntity);";
  protected final String TEXT_120 = ".add(";
  protected final String TEXT_121 = NL + "\t\t\treturnEntity.set";
  protected final String TEXT_122 = NL + "\t\t\treturn returnEntity;" + NL + "\t    }\t    " + NL + "\t    private ";
  protected final String TEXT_123 = " getBaseObject () {" + NL + "\t\t\t";
  protected final String TEXT_124 = " baseObject = new ";
  protected final String TEXT_125 = " ();" + NL + "\t\t\treturn baseObject;" + NL + "\t\t}\t\t" + NL + "//For getting OperationId for a particular Operation depending upon the user's Role\t" + NL + "\t\tprivate int getOperationId (CPFConstants.OperationType opType) {" + NL + "\t\t\tint operationId = new Integer (-99);" + NL + "\t\t\tFacesContext context = FacesContext.getCurrentInstance();" + NL + "\t\t\tExternalContext exContext = context.getExternalContext();";
  protected final String TEXT_126 = NL + "\t\t\tif(opType.equals(CPFConstants.OperationType.";
  protected final String TEXT_127 = ")) {";
  protected final String TEXT_128 = NL + "\t\t\t\tif (";
  protected final String TEXT_129 = ") {" + NL + "\t\t\t\t\toperationId = ";
  protected final String TEXT_130 = ";" + NL + "\t\t\t\t}" + NL + "\t\t\t}";
  protected final String TEXT_131 = NL + "\t\t\treturn operationId;" + NL + "\t\t}";
  protected final String TEXT_132 = NL + "\t" + NL + "\t\tpublic String saveAction() {" + NL + "\t\t" + NL + "\t\t\treturn this.returnString;" + NL + "\t\t\t" + NL + "\t\t}" + NL + "\t\tpublic String saveAction (ActionEvent e) {" + NL + "\t\t\tSystem.out.println(\"Inside ";
  protected final String TEXT_133 = "\");" + NL + "\t\t\t" + NL + "\t\t\tFacesContext ctx = FacesContext.getCurrentInstance();" + NL + "\t\t\tLocale currentLocal=ctx.getViewRoot().getLocale();" + NL + "\t\t\tString code=currentLocal.getLanguage() + \"_\"+ currentLocal.getCountry();" + NL + "\t\t\tInputStream stream=this.getClass().getClassLoader().getResourceAsStream(\"bundle/resources_\"+code+\".properties\");" + NL + "\t\t\tif(stream==null){" + NL + "\t\t\t\tstream=this.getClass().getClassLoader().getResourceAsStream(\"bundle/resources.properties\");" + NL + "\t\t\t}" + NL + "\t\t\tProperties bundle=new Properties();" + NL + "\t\t    try {" + NL + "\t\t\t\tbundle.load(stream);" + NL + "\t\t\t} catch (IOException ie) {" + NL + "\t\t\t\tie.printStackTrace();" + NL + "\t\t\t}" + NL + "\t\t\tString returnValue = null;" + NL + "\t\t\t";
  protected final String TEXT_134 = " = getObject();" + NL + "\t\t\tOrganization merchantAc = _getMerchantAccount();\t\t//Getting merchant Account from Session Object " + NL + "\t\t\t";
  protected final String TEXT_135 = ".setMerchantAccount(merchantAc);" + NL + "\t\t\tif(this.mode == 0) {" + NL + "\t\t\t\tthis.operationId = getOperationId(CPFConstants.OperationType.CREATE);" + NL + "\t\t\t\t\t\t\t\ttry{" + NL + "\t\t\t\t\treturnValue = CPFManager.save(";
  protected final String TEXT_136 = ", this.operationId);" + NL + "\t\t\t\t} catch (CPFException e1) {" + NL + "\t\t\t\t\tctx.addMessage(e.getComponent().getParent().getParent().getId() + \":save\", new FacesMessage(bundle.getProperty(\"cr_failure\") + e1.getMessage()));" + NL + "\t\t\t\t\tthis.returnString = new String(\"viewDetails\");" + NL + "\t\t\t\t\treturn SS_Constants.ReturnMessage.PROVERROR.toString();" + NL + "\t\t\t\t}" + NL + "\t\t\t} else if(this.mode == 1) {" + NL + "\t\t\t\tthis.operationId = getOperationId(CPFConstants.OperationType.MODIFY);" + NL + "\t\t\t\tthis.fillCriteria(CPFConstants.OperationType.MODIFY);\t\t\t\t" + NL + "\t\t\t\ttry{" + NL + "\t\t\t\t\treturnValue = CPFManager.save(";
  protected final String TEXT_137 = ", this.criteria, this.operationId);" + NL + "\t\t\t\t} catch (CPFException e1) {" + NL + "\t\t\t\t\tctx.addMessage(e.getComponent().getParent().getParent().getId() + \":save\", new FacesMessage(bundle.getProperty(\"mod_failure\") + e1.getMessage()));" + NL + "\t\t\t\t\tthis.returnString = new String(\"viewDetails\");" + NL + "\t\t\t\t\treturn SS_Constants.ReturnMessage.PROVERROR.toString();" + NL + "\t\t\t\t}" + NL + "\t\t\t}" + NL + "\t\t\t" + NL + "\t\t\tif(this.mode == 0) {" + NL + "\t\t\t\tctx.addMessage(this.clientId, new FacesMessage(bundle.getProperty(\"cr_success\")));" + NL + "\t\t\t} else if(this.mode == 1) {" + NL + "\t\t\t\tctx.addMessage(this.clientId, new FacesMessage(bundle.getProperty(\"mod_success\")));" + NL + "\t\t\t}" + NL + "\t\t\tthis.returnString = new String(\"list\");" + NL + "\t\t\tthis.mode = 2;";
  protected final String TEXT_138 = "\t\t" + NL + "\t\t\tELContext elCtx = ctx.getELContext();" + NL + "\t\t\tExpressionFactory exF = ctx.getApplication().getExpressionFactory();" + NL + "\t\t\tValueExpression ve = exF.createValueExpression(elCtx, \"#{listMBean";
  protected final String TEXT_139 = "}\", ListMBean_";
  protected final String TEXT_140 = ".class);" + NL + "\t\t\tListMBean_";
  protected final String TEXT_141 = " res = (ListMBean_";
  protected final String TEXT_142 = ") ve.getValue(elCtx);" + NL + "\t\t\tres.setMode(0);";
  protected final String TEXT_143 = "\t\t\t" + NL + "\t\t\treturn returnValue;" + NL + "\t\t}\t\t" + NL + "\t\tpublic String modifyAction (ActionEvent e) throws Exception {" + NL + "\t\t\tSystem.out.println(\"mod\");" + NL + "\t\t\tObject val = e.getComponent().getAttributes().get(\"pkValue\");" + NL + "\t\t\tthis.primarykeyValue = new Long(val.toString());" + NL + "\t\t\tString returnString = viewAction(null);" + NL + "\t\t\tsetMode(1);" + NL + "\t\t\tthis.uiMode = 1;";
  protected final String TEXT_144 = NL + "\t\t\tSelectItem def = null;";
  protected final String TEXT_145 = NL + "\t\t\tdef = new SelectItem();" + NL + "\t\t\tdef.setLabel(\"No-Selection\");" + NL + "\t\t\tdef.setValue(\"-1\");" + NL + "\t\t\tthis.";
  protected final String TEXT_146 = ".add(0, def);";
  protected final String TEXT_147 = "\t\t" + NL + "\t\t\tFacesContext context = FacesContext.getCurrentInstance();" + NL + "\t\t\tthis.userIsInRole(context);" + NL + "\t\t\tthis.getDetailsVisibility(this.userRole);" + NL + "\t\t\tthis.clientId = e.getComponent().getClientId(context);" + NL + "\t\t\tSystem.out.println(\"In modifyAction: \" + this);" + NL + "\t\t\treturn \"modify\";" + NL + "\t\t\t" + NL + "\t\t}" + NL + "\t\tpublic String addAction (ActionEvent e) throws CPFException {" + NL + "\t\t\tthis.mode = 0;" + NL + "\t\t\tthis.uiMode = 0;";
  protected final String TEXT_148 = " = null;";
  protected final String TEXT_149 = " = 0;";
  protected final String TEXT_150 = " = new String(\"";
  protected final String TEXT_151 = "\");";
  protected final String TEXT_152 = " = new Long(";
  protected final String TEXT_153 = " = new Integer(";
  protected final String TEXT_154 = " " + NL + "\t\t\tthis.";
  protected final String TEXT_155 = " = new Double(";
  protected final String TEXT_156 = " = new Float(";
  protected final String TEXT_157 = NL + "\t\t\ttry {" + NL + "\t\t\t this.";
  protected final String TEXT_158 = " = DateFormat.getDateInstance(DateFormat.SHORT).parse(new String(\"12/03/08\"));" + NL + "\t\t\t} catch (ParseException e) {" + NL + "\t\t\t\te.printStackTrace();" + NL + "\t\t\t}" + NL + "\t\t\tthis.";
  protected final String TEXT_159 = " = new Date(System.currentTimeMillis());";
  protected final String TEXT_160 = " = '";
  protected final String TEXT_161 = "';";
  protected final String TEXT_162 = NL + "\t\t\tFacesContext context = FacesContext.getCurrentInstance();" + NL + "\t\t\tthis.userIsInRole(context);" + NL + "\t\t\tthis.getDetailsVisibility(this.userRole);\t\t" + NL + "\t\t\tthis.clientId = e.getComponent().getClientId(context);\t";
  protected final String TEXT_163 = NL + "\t\t\tSelectItem def = null;" + NL + "\t\t\tPortletUtil portletUtil = new PortletUtil();";
  protected final String TEXT_164 = " = portletUtil.getData(\"";
  protected final String TEXT_165 = "\", \"";
  protected final String TEXT_166 = "\", ";
  protected final String TEXT_167 = " ,\"";
  protected final String TEXT_168 = "\t\t\t" + NL + "\t\t\treturn SS_Constants.ReturnMessage.SUCCESS.toString();" + NL + "\t\t}\t\t" + NL + "\t\tpublic String viewAction (ActionEvent e) throws Exception {" + NL + "\t\t\tSystem.out.println(\"View\");" + NL + "\t\t\tif(e != null) {" + NL + "\t\t\t\tObject val = e.getComponent().getAttributes().get(\"pkValue\");" + NL + "\t\t\t\tthis.primarykeyValue = new Long(val.toString());" + NL + "\t\t\t}" + NL + "\t\t\tObject object = null;" + NL + "\t\t\tif(e == null) {" + NL + "\t\t\t\tthis.operationId = getOperationId(CPFConstants.OperationType.MODIFY);" + NL + "\t\t\t\tthis.fillCriteria(CPFConstants.OperationType.MODIFY);" + NL + "\t\t\t}" + NL + "\t\t\telse {" + NL + "\t\t\t\tthis.operationId = getOperationId(CPFConstants.OperationType.VIEW);" + NL + "\t\t\t\tthis.fillCriteria(CPFConstants.OperationType.VIEW);" + NL + "\t\t\t\t" + NL + "\t\t\t\tFacesContext context = FacesContext.getCurrentInstance();" + NL + "\t\t\t\tthis.userIsInRole(context);" + NL + "\t\t\t\tthis.getDetailsVisibility(this.userRole);" + NL + "\t\t\t}" + NL + "\t\t\ttry {" + NL + "\t\t\t\tobject = CPFManager.getDetails(this.operationId, this.getBaseObject(), this.criteria);" + NL + "\t\t\t} catch (CPFException e1) {" + NL + "\t\t\t\te1.printStackTrace();" + NL + "\t\t\t\treturn SS_Constants.ReturnMessage.PROVERROR.toString();" + NL + "\t\t\t}" + NL + "\t\t\tthis.mode = 2;" + NL + "\t\t\tthis.uiMode = 2;";
  protected final String TEXT_169 = NL + "\t\t\tPortletUtil portletUtil = new PortletUtil();";
  protected final String TEXT_170 = " , \"";
  protected final String TEXT_171 = "\t\t" + NL + "\t\t\tSystem.out.println(\"View : \" );\t\t" + NL + "\t\t\tdistributeData(object);\t\t\t//Setting all member class variables here...." + NL + "\t\t\tSystem.out.println(\"View end: \");" + NL + "\t\t\treturn SS_Constants.ReturnMessage.SUCCESS.toString();" + NL + "\t\t}\t" + NL + "//This will return user's Role depending upon context\t" + NL + "\tprivate void userIsInRole (FacesContext context) throws CPFException{" + NL + "\t\tExternalContext exContext = context.getExternalContext();";
  protected final String TEXT_172 = NL + "\t\tif (exContext.isUserInRole(\"";
  protected final String TEXT_173 = "\")) {" + NL + "\t\t\tthis.setUserRole (\"";
  protected final String TEXT_174 = "\");" + NL + "\t\t} else";
  protected final String TEXT_175 = NL + "\t\t{" + NL + "\t\t\tthrow new CPFException(\"Not Authenticated please contact provider for necessary  privileges\", 4046);" + NL + "\t\t}" + NL + "\t}\t" + NL + "//Start of getting visibility for Details columns\t" + NL + "\tprivate void getDetailsVisibility(String userRole) {" + NL + "\t\tthis.listVisibility = new ArrayList<Boolean>();";
  protected final String TEXT_176 = NL + "\t\tif(userRole.equals(\"";
  protected final String TEXT_177 = "\")) {";
  protected final String TEXT_178 = NL + "\t\t\tthis.listVisibility.add(true);";
  protected final String TEXT_179 = NL + "\t\t\tthis.listVisibility.add(false);";
  protected final String TEXT_180 = NL + "\t\t}";
  protected final String TEXT_181 = NL + "\t}" + NL + "//End of getting visibility for listing columns\t\t" + NL + "\t//This will returns MerchantAccount Object of Current User" + NL + "\tprivate Organization _getMerchantAccount() {";
  protected final String TEXT_182 = NL + "\t\tOrganization o =  new Organization ();" + NL + "\t\to.setOrganizationId (new Long (-1));" + NL + "\t\treturn o;";
  protected final String TEXT_183 = "\t" + NL + "            Set<Principal> s = SecurityAssociation.getSubject().getPrincipals();" + NL + "            for (Principal principal : s) {" + NL + "                        if (principal instanceof GBUserPrincipal) {" + NL + "                              Organization enterprise = ((GBUserPrincipal) principal).getMerchantAccount();" + NL + "                              long enterpriseId = enterprise.getOrganizationId();" + NL + "                              return enterprise;" + NL + "                        }" + NL + "                  }" + NL + "            return null;";
  protected final String TEXT_184 = NL + "\t}\t" + NL + "\t//This will set all the member class values from Object returned by CPFSessionFacade" + NL + "\tprivate void distributeData(Object o) {" + NL + "\t\tthis.resetValues();";
  protected final String TEXT_185 = NL + "\t}\t" + NL + "\tprivate void resetValues() {";
  protected final String TEXT_186 = NL + "\t\tthis.";
  protected final String TEXT_187 = NL + "\t}";
  protected final String TEXT_188 = NL + "\t private int getIndex(Object selected, List<SelectItem> available) {" + NL + "\t \tint i = 0;" + NL + "\t    for(Iterator<SelectItem> itr = available.iterator(); itr.hasNext();) {" + NL + "\t    \tSelectItem temp = itr.next();" + NL + "\t    \tif(temp.getValue().equals(selected)) {" + NL + "\t    \t\tbreak;" + NL + "\t    \t}" + NL + "\t    \ti++;\t" + NL + "\t    }" + NL + "\t    return i;" + NL + "\t }";
  protected final String TEXT_189 = "\t" + NL + "\tprivate void fillCriteria(CPFConstants.OperationType o) {" + NL + "\t\tCriteria c = new Criteria();" + NL + "\t\tc.setBaseEntityName(\"";
  protected final String TEXT_190 = "\");" + NL + "\t\tc.setBasePrimaryKey(\"";
  protected final String TEXT_191 = "\");" + NL + "\t\tc.setBasePrimaryKeyValue(this.getPrimarykeyValue());" + NL + "\t\tLong merchantId = this._getMerchantAccount().getOrganizationId();" + NL + "\t\tc.setWhere(\"";
  protected final String TEXT_192 = ".merchantAccount= (select merchantAccount from Organization merchantAccount where merchantAccount.organizationId=\" + merchantId + \")\");" + NL + "\t\tif(o.equals(CPFConstants.OperationType.MODIFY)) {" + NL + "\t\t\tc.setFields(\"";
  protected final String TEXT_193 = "\");" + NL + "\t\t} else if(o.equals(CPFConstants.OperationType.VIEW)) {" + NL + "\t\t\tc.setFields(\"";
  protected final String TEXT_194 = "\");" + NL + "\t\t}" + NL + "\t\tthis.criteria = c;" + NL + "\t}" + NL + "}";
  protected final String TEXT_195 = "";
  protected final String TEXT_196 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CPFPlugin LOG = CPFPlugin.getDefault();
	CPFResource CPFArgument = (CPFResource)argument; 
 	CPFScreen cpfScreen = (CPFScreen)CPFArgument.getCpfScreen();
 	ModelEntity modelEntity = null;
 	modelEntity = (ModelEntity)cpfScreen.getBaseEntity();
 	LOG.info("Generating Managed Bean for Details Screen of Base Entity : " + modelEntity.getName());
 	List<CPFAttribute> selectedAttributes = cpfScreen.getSelectedAttributes ();
 	int operationId = cpfScreen.getPortletRef().getPortletId();		//Holds Operation Id of the method
 	LOG.info("Generatinf details Managed bean for Portlet Id : " + operationId);
	Map<RelationKey, List<CPFAttribute>> nestedAttributes = cpfScreen.getNestedAttributes();
	List<String> varibleNames = new ArrayList<String>();
	List<String> setters = new ArrayList<String>();
	List<String> getters = new ArrayList<String>();
	List<String> signatures = new ArrayList<String>();
	List<String> declarations = new ArrayList<String>();
	List<String> imports = new ArrayList<String>();
	Map<CPFAttribute, String> attributeSetters = new HashMap<CPFAttribute, String>();		//Holds Setter function names
	Map<CPFAttribute, String> attributeGetters = new HashMap<CPFAttribute, String>();		//Holds Getter function names
	Map<String, String> otherEntitiesSign = new HashMap<String, String>();			//Holds return type signature for dependent entities
	List<List<String>> attrbVisibility = new ArrayList<List<String>>();		//Holds values related to getListVisibility()
	List<String> distributeData = new ArrayList<String>();
	String modifyFields = new String();		//Holds field values for Modify mode 
	String viewFields = new String();		//Holds field values for View mode... Infact both can be same if we donot want attibute name in case of strong relation with foreign entity
		//Holds the map of taggedValue name inside managed bean and its user defined map of code and value values
	Map<String, Map<String, String>> taggedValues = null;
	Map<String, String> tvSelectedName = null;
		//Holds the default values for each attribute if set by user otherwise holds null value
	List<String> defaultValues = new ArrayList<String>();
	List<String> formatTypes = new ArrayList<String>();	//Holds the format types for attributes..
	boolean selectItemImport = true;
	List<String> puVarName = null;	//Holds variable Names for related Strong entities;
	List<String> puSelectedName = null;	//Holds variable names for related entities whihch holds index for selected item..
	Map<String, String[]> puArguments = null;	//Holds arguments to be passed to portlet Util to get SelectItems
	String className = "CreateMBean_" + operationId;
	LOG.info("Generating Class name as : " + className);
	imports.add(modelEntity.getCanonicalTypeName());
	if(!modelEntity.getName().equals("Organization")) {
		imports.add("com.genband.m5.maps.common.entity.Organization");
	}

    
		//doing process for Variable declaration, setters, getters and DataTypes here..................
	LOG.info("Doing process over Base Entity selected Attributes for declaration of them in class and thier setters getters as well Data types..");
	for(Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext(); ) { 
 		CPFAttribute selectedAttribute = (CPFAttribute)attributeItr.next();    //For each selected attribute
		ModelAttribute modelAttrib; 
		modelAttrib = selectedAttribute.getModelAttrib();
		String type = null;      //retrieving Attribute data type
			//not working on group items
		if (selectedAttribute.isGroup () ) {
			LOG.info ("Found a group item. skipping it ... " + selectedAttribute.getName ());
			continue;
		}		
 		if(selectedAttribute.getForeignColumn () == null) {	//For Basic attributes...
 			if(selectedAttribute.getModelAttrib().isFK()) {
 				throw new IllegalArgumentException("You have not selected column to be displayed for this foreign attribute.. "
 													+ "please select it and then try the code geenration..");
 			}
			type = new String(modelAttrib.getCanonicalTypeName());      //retrieving Attribute data type 
			modifyFields = modifyFields.concat(modelAttrib.getName() + ", ");
			viewFields = viewFields.concat(modelAttrib.getName() + ", ");
				//For user defined tagged values...i.e, If user selected any control type other than textbox..
			if(!selectedAttribute.getControlType ().equals(CPFConstants.ControlType.TEXTBOX)
				&& !selectedAttribute.getControlType ().equals(CPFConstants.ControlType.CALENDAR)
				&& !selectedAttribute.getControlType ().equals(CPFConstants.ControlType.CLOCK)
				&& selectedAttribute.getTaggedValues () == null) {
				LOG.info(selectedAttribute.getName() + " attribute is user defined tagged value...");
				throw new IllegalArgumentException ("You have not entered any 'code and value' values into tagged values "
														+ "please enter the pair of values and then try for code generation..");
			} else if(!selectedAttribute.getControlType ().equals(CPFConstants.ControlType.TEXTBOX) 
						&& !selectedAttribute.getControlType ().equals(CPFConstants.ControlType.CALENDAR)
						&& !selectedAttribute.getControlType ().equals(CPFConstants.ControlType.CLOCK)
						&& selectedAttribute.getTaggedValues () != null) {
				LOG.info(selectedAttribute.getName() + " attribute is user defined tagged value...");
				if(selectedAttribute.getTaggedValues ().size() == 0) {
					throw new IllegalArgumentException ("You have not entered any code and value values into tagged values "
														+ "please enter the pair of values and then try for code generation..");
				}
				if(taggedValues == null) {
					taggedValues = new HashMap<String, Map<String, String>>();
					tvSelectedName = new HashMap<String, String>();
					if(!imports.contains("javax.faces.model.SelectItem")) {
						imports.add("javax.faces.model.SelectItem");
					}
					imports.add("com.genband.m5.maps.common.CommonUtil");
				}
				String temp = new String();
				temp = temp.concat("b" + modelAttrib.getName() + "TV");
				Map<String, String> tempTags = new HashMap<String, String>();
				tempTags = selectedAttribute.getTaggedValues();
				for(Iterator<String> itrTags = tempTags.keySet().iterator(); itrTags.hasNext();) {
					String tc = itrTags.next();
					LOG.info("TC is : " + tc);
					if(tc == null || tc.trim().equals("")) {
						throw new IllegalArgumentException ("You have not entered taaged code for a tagged value..");
					}
					if(tempTags.get(tc) == null) {
						tempTags.put(tc, tc);
					}
				}
				taggedValues.put(temp, selectedAttribute.getTaggedValues());
				tvSelectedName.put(temp, "selected" + modelAttrib.getName().toUpperCase().charAt(0)
									+ modelAttrib.getName().substring(1));
			}
		} else {	//For Foreign Column attributes...
				//getting information to call portletUtil from add, modify and view acitons later
			if(selectItemImport) {
				selectItemImport = false;
				imports.add("javax.faces.model.SelectItem");
				imports.add("com.genband.m5.maps.common.PortletUtil");
			}
			LOG.info("Adding import statement : " + selectedAttribute.getForeignColumn().getEntity().getCanonicalTypeName());
			if(!imports.contains(selectedAttribute.getForeignColumn().getEntity().getCanonicalTypeName())) {
				imports.add(selectedAttribute.getForeignColumn().getEntity().getCanonicalTypeName());
			}
			LOG.info("Selected property Name is : " + selectedAttribute.getModelAttrib().getName());
			LOG.info("Selected DSC Name is : " + selectedAttribute.getForeignColumn().getName());
			if(puVarName == null) {
				puVarName = new ArrayList<String>();
				puArguments = new HashMap<String, String[]>();
			}
			RelationShipInfo relInfo = selectedAttribute.getModelAttrib().getRelType();
			String tempPuName = relInfo.getPropertyName();
			if(relInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne)
				|| relInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToOne)) {
				if (puSelectedName == null) {
					puSelectedName = new ArrayList<String> ();
				}
				String te = new String("selected");
				te = te.concat(tempPuName.toUpperCase().charAt(0) + tempPuName.substring(1));
				puSelectedName.add(te);
			}
			tempPuName = tempPuName + selectedAttribute.getForeignColumn().getName().toUpperCase().charAt(0)
							+ selectedAttribute.getForeignColumn().getName().substring(1);
			puVarName.add(tempPuName);
			String[] tempP = new String[6];	//argumetns to be passed is 5 So hardcoded and last but one is for one to many purpose .. 1 for one2One 2 for one2Many
			tempP[0] = new String();
			tempP[0] = modelAttrib.getForeignEntity().getName();
			tempP[1] = new String();
			tempP[1] = modelAttrib.getForeignEntity().getPrimaryKey();
			tempP[2] = new String();
			tempP[2] = selectedAttribute.getForeignColumn().getName();
			tempP[3] = new String();
			if(modelAttrib.getForeignEntity().isStatic()) {
				tempP[3] = "true";
			} else {
				tempP[3] = "false";
			}
			if(relInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToOne)
				|| relInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne)) {
				if(modelAttrib.isRequired()) {
					tempP[4] = "2";
				} else {
					tempP[4] = "1";
				}
			} else {
				tempP[4] = "2";
			}			tempP[5] = new String();			tempP[5] = selectedAttribute.getExtraPredicateOnFK();
			puArguments.put(tempPuName, tempP);
			//End of getting info for portletUtil
			
			RelationShipInfo relationShipInfo = modelAttrib.getRelType();
			type = new String(relationShipInfo.getSimpleTypeInfo ());
			if(type.contains("<")) {
				modifyFields = modifyFields.concat(relationShipInfo.getPropertyName() + "[n], ");
				viewFields = viewFields.concat(relationShipInfo.getPropertyName() + "[n]."
												+ selectedAttribute.getForeignColumn().getName() + ", ");
				type = new String("List<Long>");
			} else {
				modifyFields = modifyFields.concat(relationShipInfo.getPropertyName() + ", ");
				viewFields = viewFields.concat(relationShipInfo.getPropertyName() + "."
												+ selectedAttribute.getForeignColumn().getName() + ", ");
				type = "java.lang.Long";
			}
		} //end if-else selectedAttribute.getForeignColumn () == null
		if(selectedAttribute.getDefaultValue () != null) {
			defaultValues.add(selectedAttribute.getDefaultValue ());
		} else {
			LOG.info("Default value found for the attribute : " + selectedAttribute.getName());
			defaultValues.add(null);
		}		
			//Getting format type information..Added on 29th April..
		if(selectedAttribute.getFormatData() != null) {
			LOG.info("format Type is : " + selectedAttribute.getFormatData().getCategory());
			formatTypes.add(selectedAttribute.getFormatData().getCategory().toString());
		} else {
			LOG.info("For This attribute no format has been set If it is date adding default conversion..");
			if(selectedAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)){
				formatTypes.add(CPFConstants.FormatType.DATE.toString());
			} else if(selectedAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
				if(selectedAttribute.getModelAttrib().getCanonicalTypeName().equals(java.sql.Time.class)) {
					formatTypes.add(CPFConstants.FormatType.TIME.toString());
				} else {
					formatTypes.add(CPFConstants.FormatType.DATE_TIME.toString());
				}
			}else {
				formatTypes.add(null);
			}
		}
		if (type.equals ("java.sql.Date") || type.equals ("java.sql.Time") || type.equals ("java.sql.Timestamp")) {
			LOG.info(selectedAttribute.getName()  + " attribute data type found as Date...");
			type = new String ("java.util.Date");
		} else if (type.equals ("java.sql.Blob")) {
			type = new String("byte[]");
		} else if (type.equals ("java.sql.Clob")) {
			type = new String("char[]");
		}		
		if(selectedAttribute.getRolesException() != null && selectedAttribute.getRolesException().size() > 0) {
			LOG.info("Some roles cannot see this Attribute : " + selectedAttribute.getName() );
 			List<String> rolesList = selectedAttribute.getRolesException().get(CPFConstants.OperationType.VIEW);
 			attrbVisibility.add(rolesList);
 		} else {
 			attrbVisibility.add(null);
 		}		
		String varName = modelAttrib.getName();
		varibleNames.add (varName);
		varName = varName.toUpperCase ().charAt (0) + varName.substring (1);
		String setter = "set" + varName;
		String getter = "get" + varName;
		setters.add (setter);
		getters.add ("get" + varName);
		varName = varName.toLowerCase ().charAt (0) + varName.substring (1);
		signatures.add (type);
  		attributeSetters.put(selectedAttribute, setter);
  		attributeGetters.put(selectedAttribute, getter);
		declarations.add("private " + type + " " + varName);	//Adding declaration..  this may move up so that varName changing to lower case  may ommited
	} //end for process over Base Entity selected Attributes	
			//Generating Varible declarations for Dependent here........
	Set<RelationKey> selectedOtherEntities = null;
	if(nestedAttributes != null) {
		selectedOtherEntities = nestedAttributes.keySet();
		if (selectedOtherEntities != null) {
			LOG.info("Doing process for selected other dependent entities selected attributes....");
			for (Iterator<RelationKey> itr = selectedOtherEntities.iterator(); itr.hasNext(); ) {
				RelationKey relationKey = itr.next();
				String otherModelEntityProp = relationKey.getRelationShipInfo().getPropertyName();
				List<CPFAttribute> selectedOtherAttributes = nestedAttributes.get (relationKey);
				ModelEntity otherModelEntity = relationKey.getReferencedEntity();
				if(!imports.contains(otherModelEntity.getCanonicalTypeName())) {
					imports.add(otherModelEntity.getCanonicalTypeName());
				}		
				RelationShipInfo relationShipInfo = relationKey.getRelationShipInfo();
				String signature = relationShipInfo.getSimpleTypeInfo();
				otherEntitiesSign.put(otherModelEntityProp, signature);
				String tempPurpose = null;
				if(relationShipInfo.getTypeInfo().contains("<")) {	//No need to change getTypeInfo to getSimpleTypeInfo...
					tempPurpose = new String("[n].");
				} else {
					tempPurpose = new String(".");
				}
					//Adding Dependent Entity primaryKey to modifyfields and viewFields here....
				modifyFields = modifyFields.concat(relationShipInfo.getPropertyName() + tempPurpose +
													otherModelEntity.getPrimaryKey () + "," );
				viewFields = viewFields.concat(relationShipInfo.getPropertyName() + tempPurpose +
												otherModelEntity.getPrimaryKey () + ", ");
				for (Iterator<CPFAttribute> itrAttribute = selectedOtherAttributes.iterator(); itrAttribute.hasNext();) {
					CPFAttribute selectedAttribute = (CPFAttribute) itrAttribute.next ();
					ModelAttribute modelAttrib; 
					String type = null;
					String varName = null;
					modelAttrib = selectedAttribute.getModelAttrib();
					varName = modelAttrib.getName();
					if(selectedAttribute.getForeignColumn () == null) {	//For Basic attribtues...
						if(selectedAttribute.getModelAttrib().isFK()) {
 							throw new IllegalArgumentException("You have not selected column to be displayed for this foreign attribute.. "
 																+ "please select it and then try the code geenration.."); 				
 						}
						type = new String(modelAttrib.getCanonicalTypeName());      //retrieving Attribute data type 
						varName = varName.toUpperCase ().charAt (0) + varName.substring (1);
						String temp = otherModelEntityProp;
						temp = temp.toLowerCase().charAt(0) + temp.substring(1);
						varName = temp + varName;
						modifyFields = modifyFields.concat(relationShipInfo.getPropertyName() + tempPurpose +
															modelAttrib.getName() + "," );
						viewFields = viewFields.concat(relationShipInfo.getPropertyName() + tempPurpose +
														modelAttrib.getName() + ", ");
						//For user defined tagged values...i.e, If user selected any control type other than textbox..
						if(!selectedAttribute.getControlType ().equals(CPFConstants.ControlType.TEXTBOX)
							&& !selectedAttribute.getControlType ().equals(CPFConstants.ControlType.CALENDAR)
							&& !selectedAttribute.getControlType ().equals(CPFConstants.ControlType.CLOCK)
							&& selectedAttribute.getTaggedValues () == null) {
								LOG.info(selectedAttribute.getName() + " attribute is user defined tagged value...");
								throw new IllegalArgumentException ("You have not entered any 'code and value' values into tagged values "
																	+ "please enter the pair of values and then try for code generation..");
						} else if(!selectedAttribute.getControlType ().equals(CPFConstants.ControlType.TEXTBOX)
									&& !selectedAttribute.getControlType ().equals(CPFConstants.ControlType.CALENDAR)
									&& !selectedAttribute.getControlType ().equals(CPFConstants.ControlType.CLOCK)
									&& selectedAttribute.getTaggedValues () != null) {
							LOG.info(selectedAttribute.getName() + " attribute is user defined tagged value...");
							if(selectedAttribute.getTaggedValues ().size() == 0) {
								throw new IllegalArgumentException ("You have not entered any code and value values into tagged values "
																	+ "please enter the pair of values and then try for code generation..");
							}
							if(taggedValues == null) {
								taggedValues = new HashMap<String, Map<String, String>>();
								tvSelectedName = new HashMap<String, String>();
								if(!imports.contains("javax.faces.model.SelectItem")) {
									imports.add("javax.faces.model.SelectItem");
								}
								imports.add("com.genband.m5.maps.common.CommonUtil");
							}					
							temp = new String();
							temp = temp.concat("d" + modelAttrib.getName() + "TV");
							taggedValues.put(temp, selectedAttribute.getTaggedValues());
							tvSelectedName.put(temp, "selected" + varName.toUpperCase().charAt(0)
												+ varName.substring(1));
						}	//End of if(!selectedAttribute.getControlType ().equals(CPFConstants.ControlType.TEXTBOX)
					} else {	//For Foreign column attribtues....
							//getting information to call portletUtil from add modify and view acitons later
						if(selectItemImport) {
							selectItemImport = false;
							imports.add("javax.faces.model.SelectItem");
							imports.add("com.genband.m5.maps.common.PortletUtil");
						}				
						if(puVarName == null) {
							puVarName = new ArrayList<String>();
							puArguments = new HashMap<String, String[]>();
						}
						if(!imports.contains(selectedAttribute.getForeignColumn().getEntity().getCanonicalTypeName())) {
							imports.add(selectedAttribute.getForeignColumn().getEntity().getCanonicalTypeName());
						}
						RelationShipInfo relInfo = selectedAttribute.getModelAttrib().getRelType();
						String tempPuName = relInfo.getPropertyName();
						
						if(relInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne)
							|| relInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToOne)) {
							if (puSelectedName == null) {
								puSelectedName = new ArrayList<String> ();
							}
							String te = new String("selected");
							te = te.concat(tempPuName.toUpperCase().charAt(0) + tempPuName.substring(1));
							puSelectedName.add(te);
						}				
						tempPuName = tempPuName.toUpperCase().charAt(0) + tempPuName.substring(1);
						tempPuName = relationShipInfo.getPropertyName()
									+ tempPuName + selectedAttribute.getForeignColumn().getName().toUpperCase().charAt(0)
									+ selectedAttribute.getForeignColumn().getName().substring(1);
						puVarName.add(tempPuName);
						String[] tempP = new String[6];	//argumetns to be passed is 6 So hardcoded and last but one is for one2one or one2many purpose..
						tempP[0] = new String();
						tempP[0] = modelAttrib.getForeignEntity().getName();
						tempP[1] = new String();
						tempP[1] = modelAttrib.getForeignEntity().getPrimaryKey();
						tempP[2] = new String();
						tempP[2] = selectedAttribute.getForeignColumn().getName();
						tempP[3] = new String();
						if(modelAttrib.getForeignEntity().isStatic()) {
							tempP[3] = "true";
						} else {
							tempP[3] = "false";
						}
						if(relInfo.getMapping().equals(CPFConstants.RelationshipType.ManyToOne)
							|| relInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne)) {
							if(modelAttrib.isRequired()) {
								tempP[4] = "2";
							} else {
								tempP[4] = "1";
							}
						} else {
							tempP[4] = "2";
						}
			   			tempP[5] = new String();
			   			 			    		tempP[5] = selectedAttribute.getExtraPredicateOnFK();
						puArguments.put(tempPuName, tempP);
							//End of getting info for portletUtil				
						ModelEntity innerModelEntity = selectedAttribute.getForeignColumn().getEntity();
						imports.add(innerModelEntity.getCanonicalTypeName());				
						RelationShipInfo innerRelationShipInfo = modelAttrib.getRelType();
						type = new String(innerRelationShipInfo.getSimpleTypeInfo ());
						if(type.contains("<")) {
							modifyFields = modifyFields.concat(relationShipInfo.getPropertyName() + tempPurpose 											+ innerRelationShipInfo.getPropertyName() + "[n], ");
							viewFields = viewFields.concat(relationShipInfo.getPropertyName() + tempPurpose 											+ innerRelationShipInfo.getPropertyName() + "[n]." 											+ selectedAttribute.getForeignColumn().getName() + ", ");
							type = type.substring(0, type.lastIndexOf("<")+1) + "Long>";
						} else {
							modifyFields = modifyFields.concat(relationShipInfo.getPropertyName() + tempPurpose 											+ innerRelationShipInfo.getPropertyName() + ", ");
							viewFields = viewFields.concat(relationShipInfo.getPropertyName() + tempPurpose 											+ innerRelationShipInfo.getPropertyName() + "." 											+ selectedAttribute.getForeignColumn().getName() + ", ");
							type = "java.lang.Long";
						}	
					}	//End of if(selectedAttribute.getForeignColumn () == null)
					if (type.equals ("java.sql.Date") || type.equals ("java.sql.Time") || type.equals ("java.sql.Timestamp")) {
						type = new String ("java.util.Date");
					}else if (type.equals ("java.sql.Blob")) {
						type = new String ("Byte[]");
					} else if (type.equals ("java.sql.Clob")) {
						type = new String ("Char[]");
					}			
					if(selectedAttribute.getDefaultValue () != null) {
						defaultValues.add(selectedAttribute.getDefaultValue ());
					} else {
						defaultValues.add(null);
					}
					
						//Getting format type information..Added on 29th April..
					if(selectedAttribute.getFormatData() != null) {
						LOG.info("format Type is : " + selectedAttribute.getFormatData().getCategory());
						formatTypes.add(selectedAttribute.getFormatData().getCategory().toString());
					} else {
						LOG.info("For This attribute no format has been set If it is date adding default conversion..");
						if(selectedAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.DATE)){
							formatTypes.add(CPFConstants.FormatType.DATE.toString());
						} else if(selectedAttribute.getModelAttrib().getDataType().equals(CPFConstants.AttributeDataType.TIMESTAMP)){
							if(selectedAttribute.getModelAttrib().getCanonicalTypeName().equals(java.sql.Time.class)) {
								formatTypes.add(CPFConstants.FormatType.TIME.toString());
							} else {
								formatTypes.add(CPFConstants.FormatType.DATE_TIME.toString());
							}
						}else {		
							formatTypes.add(null);
						}
					}		
					if(selectedAttribute.getRolesException() != null && selectedAttribute.getRolesException().size() > 0) {
						List<String> rolesList = selectedAttribute.getRolesException().get(CPFConstants.OperationType.VIEW);
 						attrbVisibility.add(rolesList);
 					} else {
 						attrbVisibility.add(null);
 					}			
					varibleNames.add (varName);	
					declarations.add("private " + type + " " + varName);	//Adding declaration	
					varName = varName.toUpperCase ().charAt (0) + varName.substring (1);
					setters.add ("set" + varName);
					getters.add ("get" + varName);
					signatures.add (type);
				}	//End of for (Iterator<CPFAttribute> itrAttribute = selectedOtherAttributes.iterator()
			}	//End of for (Iterator<RelationKey> itr = selectedOtherEntities.iterator()
		} //End of if(selectedOtherEntities != null)
	} //End of if(nestedAttributes != null)
	modifyFields = modifyFields.substring(0, modifyFields.lastIndexOf(','));
	viewFields = viewFields.substring(0, viewFields.lastIndexOf(','));
	//End of doing process for Variable declaration, setters, getters and DataTypes here...............

    stringBuffer.append(TEXT_1);
    
	//Bean generation Started Here.....
	LOG.info("adding Imports to the class...");

    stringBuffer.append(TEXT_2);
    
	Iterator<String> itrImports = imports.iterator();
	
	while(itrImports.hasNext()) {

    stringBuffer.append(TEXT_3);
    stringBuffer.append( itrImports.next() );
    stringBuffer.append(TEXT_4);
    
	}

    stringBuffer.append(TEXT_5);
    stringBuffer.append( modelEntity.getName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_7);
    
	LOG.info("Declaring variables inside Class...");
		//Variables declaration started here....
	//general variables declaration started here...
	for(Iterator<String> itrDeclarations = declarations.iterator(); itrDeclarations.hasNext();) {

    stringBuffer.append(TEXT_8);
    stringBuffer.append( itrDeclarations.next() );
    stringBuffer.append(TEXT_4);
    
	}	 
	
		//Declaration for Tagged Values here..... declaring as SelectItem
	if(taggedValues != null) {
		LOG.info("Declaration for tagged values ...");
		Iterator<String> itrTaggedValues = taggedValues.keySet().iterator();
		while (itrTaggedValues.hasNext()) {
			String tvVariableName = itrTaggedValues.next();

    stringBuffer.append(TEXT_9);
    stringBuffer.append( tvVariableName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( tvSelectedName.get(tvVariableName) );
    stringBuffer.append(TEXT_4);
    
		}
	}
		//Generating Dependent Entity primary Keys for Modify have to set primaryKey Value;
			//TODO This works only for OneToOne for OneToMany have to define as a Collection... Have to think once for that
	if (selectedOtherEntities != null) {
		for (Iterator<RelationKey> itr = selectedOtherEntities.iterator(); itr.hasNext(); ) {
			RelationKey rk = itr.next();
			String otherEntityPKName = new String(rk.getRelationShipInfo().getPropertyName()); 
			otherEntityPKName = otherEntityPKName.concat("PKValue");

    stringBuffer.append(TEXT_11);
    stringBuffer.append( otherEntityPKName );
    stringBuffer.append(TEXT_4);
    
		}
	}	//End of Generating Dependent Entity primary Keys for Modify have to set primaryKey Value;
	
		//This is for Strong Related Entites
	if(puVarName != null) {
		Iterator<String> itrPuVarName = puVarName.iterator();
		while(itrPuVarName.hasNext()) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append( itrPuVarName.next() );
    stringBuffer.append(TEXT_4);
    
		}	
	}
		//This is for storing index of selected items for related entities...
	if (puSelectedName != null) {
		Iterator<String> itrPuSelectedName = puSelectedName.iterator();
		while (itrPuSelectedName.hasNext()) {

    stringBuffer.append(TEXT_13);
    stringBuffer.append( itrPuSelectedName.next() );
    stringBuffer.append(TEXT_4);
    
		}
	}
	//End Variables declaration here....

    stringBuffer.append(TEXT_14);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_15);
    		
	if(taggedValues != null) {		//This is for loading Tagged Values Statically
		LOG.info("adding static load for tagged values.....");
		Iterator<String> itrTaggedValues = taggedValues.keySet().iterator();

    stringBuffer.append(TEXT_16);
    
		while(itrTaggedValues.hasNext()) {
			String tagName = itrTaggedValues.next();
			Map<String, String> tagValues = taggedValues.get(tagName);
			Iterator<String> itrTagCode = tagValues.keySet().iterator(); 

    stringBuffer.append(TEXT_17);
    stringBuffer.append( tagName );
    stringBuffer.append(TEXT_18);
    


			while(itrTagCode.hasNext()) {


				String tc = itrTagCode.next();


				String tv = tagValues.get(tc);


    stringBuffer.append(TEXT_19);
    stringBuffer.append( tc );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( tv );
    stringBuffer.append(TEXT_21);
    stringBuffer.append( tagName );
    stringBuffer.append(TEXT_22);
    
			}
		}

    stringBuffer.append(TEXT_23);
    
	}	//End of loading Tagged Values Statically

    
		//Generating Getters here.......................
	LOG.info("Defining Getters inside Class.....");
	Iterator<String> itrGetter = getters.iterator ();
	Iterator<String> itrVaribleNames = varibleNames.iterator ();
	Iterator<String> itrSignatures =  signatures.iterator ();
	while (itrGetter.hasNext ()) {

    stringBuffer.append(TEXT_24);
    stringBuffer.append( (String)itrSignatures.next() );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( (String)itrGetter.next () );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( (String)itrVaribleNames.next () );
    stringBuffer.append(TEXT_27);
    
	}	
		//Generating getters for Dependent entities primaryKeys
		//TODO This works only for OneToOne for OneToMany have to define as a Collection... Have to think once for that
	if (selectedOtherEntities != null) {
		for (Iterator<RelationKey> itr = selectedOtherEntities.iterator(); itr.hasNext(); ) {
			RelationKey rk = itr.next();
			String otherEntityPKName = new String(rk.getRelationShipInfo().getPropertyName()); 
			otherEntityPKName = otherEntityPKName.concat("PKValue");

    stringBuffer.append(TEXT_28);
    stringBuffer.append( otherEntityPKName.toUpperCase().charAt(0) + otherEntityPKName.substring(1) );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( otherEntityPKName );
    stringBuffer.append(TEXT_29);
    
		}
	}	
		//For Strong Related Entities
	if(puVarName != null) {
		Iterator<String> itrPuVarName = puVarName.iterator();
		while(itrPuVarName.hasNext()) {
			String tempP = itrPuVarName.next();

    stringBuffer.append(TEXT_30);
    stringBuffer.append( tempP.toUpperCase().charAt(0) + tempP.substring(1) );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( tempP );
    stringBuffer.append(TEXT_31);
    
		}
	}
		//This is also for Related entities..
	if (puSelectedName != null) {
		Iterator<String> itrPuSelectedName = puSelectedName.iterator();
		while (itrPuSelectedName.hasNext()) {
			String tempS = itrPuSelectedName.next();

    stringBuffer.append(TEXT_32);
    stringBuffer.append( tempS.toUpperCase().charAt(0) + tempS.substring(1) );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( tempS );
    stringBuffer.append(TEXT_27);
    
		}
	}
		//For User Defined Tagged Values
	if(taggedValues != null) {	
		Iterator<String> itrTaggedValues = taggedValues.keySet().iterator();
		while (itrTaggedValues.hasNext()) {
			String temp = itrTaggedValues.next();
			String get = temp.toUpperCase().charAt(0) + temp.substring(1);
			get = "get" + get;

    stringBuffer.append(TEXT_33);
    stringBuffer.append( get );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_34);
    stringBuffer.append( tvSelectedName.get(temp).toUpperCase().charAt(0) + tvSelectedName.get(temp).substring(1) );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( tvSelectedName.get(temp) );
    stringBuffer.append(TEXT_27);
    
		}
	}

    stringBuffer.append(TEXT_35);
    
		//Generating Setters here.......................
	LOG.info("Defining Setters inside Class....");
	Iterator<String> itrSetter = setters.iterator ();
	itrVaribleNames = varibleNames.iterator ();
	itrSignatures =  signatures.iterator ();
	while (itrSetter.hasNext ()) {
		String varName = (String)itrVaribleNames.next ();

    stringBuffer.append(TEXT_36);
    stringBuffer.append( (String)itrSetter.next () );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( (String)itrSignatures.next() );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_38);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_40);
    
	}
	
		//Generating setters for Dependent entities primaryKeys
		//TODO This works only for OneToOne for OneToMany have to define as a Collection... Have to think once for that
	if (selectedOtherEntities != null) {
		for (Iterator<RelationKey> itr = selectedOtherEntities.iterator(); itr.hasNext(); ) {
			RelationKey rk = itr.next();
			String otherEntityPKName = new String(rk.getRelationShipInfo().getPropertyName()); 
			otherEntityPKName = otherEntityPKName.concat("PKValue");

    stringBuffer.append(TEXT_41);
    stringBuffer.append( otherEntityPKName.toUpperCase().charAt(0) + otherEntityPKName.substring(1) );
    stringBuffer.append(TEXT_42);
    stringBuffer.append( otherEntityPKName );
    stringBuffer.append(TEXT_38);
    stringBuffer.append( otherEntityPKName );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( otherEntityPKName );
    stringBuffer.append(TEXT_27);
    
		}
	}		
		//For Strong Related Entities
	if(puVarName != null) {
		Iterator<String> itrPuVarName = puVarName.iterator();
		while(itrPuVarName.hasNext()) {
			String tempP = itrPuVarName.next();

    stringBuffer.append(TEXT_41);
    stringBuffer.append( tempP.toUpperCase().charAt(0) + tempP.substring(1) );
    stringBuffer.append(TEXT_43);
    stringBuffer.append( tempP );
    stringBuffer.append(TEXT_38);
    stringBuffer.append( tempP );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( tempP );
    stringBuffer.append(TEXT_27);
    
		}
	}
		//This is also for Related entities..
	if (puSelectedName != null) {
		Iterator<String> itrPuSelectedName = puSelectedName.iterator();
		while (itrPuSelectedName.hasNext()) {
			String tempS = itrPuSelectedName.next();

    stringBuffer.append(TEXT_41);
    stringBuffer.append( tempS.toUpperCase().charAt(0) + tempS.substring(1) );
    stringBuffer.append(TEXT_44);
    stringBuffer.append( tempS );
    stringBuffer.append(TEXT_38);
    stringBuffer.append( tempS );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( tempS );
    stringBuffer.append(TEXT_27);
    
		}
	}
	if(taggedValues != null) {		//For User Defined Tagged Values
		Iterator<String> itrTaggedValues = taggedValues.keySet().iterator();
		while (itrTaggedValues.hasNext()) {
			String temp = itrTaggedValues.next();
			String set = temp.toUpperCase().charAt(0) + temp.substring(1);
			set = "set" + set;

    stringBuffer.append(TEXT_36);
    stringBuffer.append( set );
    stringBuffer.append(TEXT_45);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( tvSelectedName.get(temp).toUpperCase().charAt(0) + tvSelectedName.get(temp).substring(1) );
    stringBuffer.append(TEXT_44);
    stringBuffer.append( tvSelectedName.get(temp) );
    stringBuffer.append(TEXT_38);
    stringBuffer.append( tvSelectedName.get(temp) );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( tvSelectedName.get(temp) );
    stringBuffer.append(TEXT_27);
    
		}
	}

    stringBuffer.append(TEXT_47);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_48);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_49);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_50);
    
		LOG.info("Adding getObject method inside Class.....");
		distributeData.add(modelEntity.getName () + " dataObject = (" + modelEntity.getName() + ")o;" );
		Iterator<String> itrVariableNames = varibleNames.iterator();
				//For CPFAtribtues setting modelEntity values
	    for (Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext();) {
			CPFAttribute itrCPFAttribute = attributeItr.next();			
				//skip group item
			if ( itrCPFAttribute.isGroup () ) {
				LOG.info ("Skipping group item. .. " + itrCPFAttribute.getName ());
				continue;
			}			
			String variableName = itrVariableNames.next();      //getting member class variable name here 
		if (itrCPFAttribute.getForeignColumn () == null) {
			String cast = itrCPFAttribute.getModelAttrib ().getCanonicalTypeName ();
			
			Class classD = null;
			try {
				classD = Class.forName(cast);
			} catch(ClassNotFoundException e) {
				LOG.info("Not a lang data type...");
			}
			String mergeArgument = null;
			if (cast.equals("java.sql.Date") || cast.equals("java.sql.Time") || cast.equals("java.sql.Timestamp")) {
				mergeArgument = new String("new " + cast + "(" + itrCPFAttribute.getModelAttrib ().getName () + ".getTime ())");
			} else if (cast.equals("java.sql.Blob")) {
				mergeArgument= new String ("ModelUtil.mapByteArray2Blob (" + itrCPFAttribute.getModelAttrib ().getName () + ")");
			} else if (cast.equals("java.sql.Clob")) {
				mergeArgument= new String ("ModelUtil.mapCharArray2Clob (" + itrCPFAttribute.getModelAttrib ().getName () + ")");
			}else {
				mergeArgument = itrCPFAttribute.getModelAttrib ().getName ();
			}
			distributeData.add("this." + variableName + " = dataObject."
									+ attributeGetters.get(itrCPFAttribute) + "();");   //For distributeData Method purpose
			if (cast.equals("java.sql.Date") || cast.equals("java.sql.Time") || cast.equals("java.sql.Timestamp")) {

    stringBuffer.append(TEXT_51);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_52);
    
			}

    stringBuffer.append(TEXT_53);
    stringBuffer.append( attributeSetters.get(itrCPFAttribute) );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( mergeArgument );
    stringBuffer.append(TEXT_54);
    
			if(!itrCPFAttribute.getControlType().equals(CPFConstants.ControlType.TEXTBOX)
				&& itrCPFAttribute.getTaggedValues() != null && itrCPFAttribute.getTaggedValues().size() > 0) {
				String selectName = new String("selected");
				selectName = selectName + variableName.toUpperCase().charAt(0) + variableName.substring(1);
				String tvName = new String("b" + itrCPFAttribute.getModelAttrib().getName() + "TV");
				if(classD != null) {
					distributeData.add("this." + selectName + " = this.getIndex(this."
										+ variableName + ".toString(), this." + tvName + ");");

    stringBuffer.append(TEXT_55);
    stringBuffer.append( selectName );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( variableName );
    stringBuffer.append(TEXT_57);
    stringBuffer.append( tvName );
    stringBuffer.append(TEXT_58);
    
				} else {
					distributeData.add("this." + selectName + " = this.getIndex(CommonUtil.getWrapperForPrimitive(this."
										+ variableName + ").toString(), this." + tvName + ");");

    stringBuffer.append(TEXT_55);
    stringBuffer.append( selectName );
    stringBuffer.append(TEXT_59);
    stringBuffer.append( variableName );
    stringBuffer.append(TEXT_60);
    stringBuffer.append( tvName );
    stringBuffer.append(TEXT_58);
    
				}
			}	//End of if(!itrCPFAttribute.getControlType().equals(CPFConstants.ControlType.TEXTBOX) 
		} else {
			ModelEntity fkModelEntity = itrCPFAttribute.getForeignColumn ().getEntity ();
			RelationShipInfo relationShipInfo = itrCPFAttribute.getModelAttrib().getRelType();
			RelationShipInfo inverseRelationShipInfo = itrCPFAttribute.getModelAttrib().getInverseRelType();
			String signature = relationShipInfo.getSimpleTypeInfo();
			String fkPrimaryKeyName = fkModelEntity.getPrimaryKey();
			fkPrimaryKeyName = fkPrimaryKeyName.toUpperCase().charAt(0) + fkPrimaryKeyName.substring(1);
			if (signature.lastIndexOf (">") > 0) {
				String varName = itrCPFAttribute.getModelAttrib ().getName () + "List";
				String inversePropName = null;
				if(inverseRelationShipInfo != null) {
					inversePropName = inverseRelationShipInfo.getPropertyName();
				}
				if(relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToMany)
					&& inversePropName == null) {
					throw new IllegalArgumentException("Have not found inverse Propertyname for "
						+ relationShipInfo.getPropertyName() + "... please check the relatinships..");
				}
				if(inversePropName != null) {
					inversePropName = inversePropName.toUpperCase().charAt(0) + inversePropName.substring(1);
				}
				distributeData.add(signature + " list" + relationShipInfo.getPropertyName()
									+ " = dataObject.get" + relationShipInfo.getPropertyName().toUpperCase().charAt(0) + relationShipInfo.getPropertyName().substring(1) + "();");   //For distributeData method only
				distributeData.add("if (list" + relationShipInfo.getPropertyName() + " != null) {");

    stringBuffer.append(TEXT_61);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_62);
    
				if (signature.substring (0, signature.lastIndexOf("<")).equals ("Set")) {
				
					distributeData.add("this." + variableName + " = new ArrayList<Long>();" );		//For distributeData method only

    stringBuffer.append(TEXT_63);
    stringBuffer.append( signature );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_64);
    stringBuffer.append( fkModelEntity.getName () );
    stringBuffer.append(TEXT_65);
    
				} else {
					distributeData.add("this." + variableName + " = new ArrayList<Long>();" );		//For distributeData method only

    stringBuffer.append(TEXT_63);
    stringBuffer.append( signature );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_66);
    stringBuffer.append( fkModelEntity.getName () );
    stringBuffer.append(TEXT_65);
    
				}
				distributeData.add("Iterator<" + fkModelEntity.getName() + "> itrList" + fkModelEntity.getName()
									 + " = list" + relationShipInfo.getPropertyName()  + ".iterator();");
				distributeData.add("while(itrList" + fkModelEntity.getName() + ".hasNext()) {");
				distributeData.add("	this." + variableName + ".add(" + "itrList"
									+ fkModelEntity.getName() + ".next().get" + fkPrimaryKeyName + "());");
				distributeData.add("}");
				distributeData.add("}");

    stringBuffer.append(TEXT_67);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_68);
    stringBuffer.append( fkModelEntity.getName () );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_69);
    stringBuffer.append( fkModelEntity.getName () );
    stringBuffer.append(TEXT_70);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( fkPrimaryKeyName );
    stringBuffer.append(TEXT_72);
    
					if (relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToMany)) {

    stringBuffer.append(TEXT_73);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( inversePropName );
    stringBuffer.append(TEXT_74);
    
					}

    stringBuffer.append(TEXT_75);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_76);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_77);
    stringBuffer.append( attributeSetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_78);
    
			} else { //Else for if (signature.lastIndexOf (">") > 0) {
					//Adding this If Condition on 31st Jan for the purpose of setting Child reference in parent entity in case Of One to One Relation Only
						//and if the Foreign entity is the Parent one then only
				String inversePropertyName = null;
				if(relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne)) {
					if(!itrCPFAttribute.getModelAttrib().isOwner()) {
						inversePropertyName = inverseRelationShipInfo.getPropertyName();
						inversePropertyName = inversePropertyName.toUpperCase().charAt(0)
												+ inversePropertyName.substring(1);
					}
				}
				String setter = fkModelEntity.getPrimaryKey();
				String capPropName = relationShipInfo.getPropertyName().toUpperCase().charAt(0)
										+ relationShipInfo.getPropertyName().substring(1); 
				setter = setter.toUpperCase().charAt(0)+setter.substring (1);
				distributeData.add(fkModelEntity.getName() + " temp" + relationShipInfo.getPropertyName()
									+ " = dataObject.get" + relationShipInfo.getPropertyName().toUpperCase().charAt(0)
										+ relationShipInfo.getPropertyName().substring(1) + " ();");
				distributeData.add("if (temp" + relationShipInfo.getPropertyName() + " != null) {");
				distributeData.add("	this." + variableName + " = temp" + relationShipInfo.getPropertyName() +
										".get" + fkPrimaryKeyName + "();");
			    distributeData.add("	this.selected" + capPropName + " = getIndex(this."
			    					+ relationShipInfo.getPropertyName() + ", this." + relationShipInfo.getPropertyName()
			    					+ itrCPFAttribute.getForeignColumn().getName().toUpperCase().charAt(0)
			    					+ itrCPFAttribute.getForeignColumn().getName().substring(1) + ");");
				distributeData.add("} else {");
				distributeData.add("	this.selected" + capPropName + " = -1;");
				distributeData.add("}");

    stringBuffer.append(TEXT_79);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_80);
    stringBuffer.append( signature  );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_69);
    stringBuffer.append( signature  );
    stringBuffer.append(TEXT_81);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( setter );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_58);
    
				if(inversePropertyName != null) {	//if condition for the purpose of settingg child reference in Parent entity

    stringBuffer.append(TEXT_83);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( inversePropertyName );
    stringBuffer.append(TEXT_84);
    
				}
			String firstArg = itrCPFAttribute.getModelAttrib ().getName ();
			String secondArg = firstArg + itrCPFAttribute.getForeignColumn().getName().toUpperCase().charAt(0)
								 + itrCPFAttribute.getForeignColumn().getName().substring(1);

    stringBuffer.append(TEXT_85);
    stringBuffer.append( attributeSetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_86);
    stringBuffer.append( attributeSetters.get (itrCPFAttribute).substring(3) );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( firstArg );
    stringBuffer.append(TEXT_87);
    stringBuffer.append( secondArg );
    stringBuffer.append(TEXT_88);
    stringBuffer.append( attributeSetters.get (itrCPFAttribute).substring(3) );
    stringBuffer.append(TEXT_89);
    
			} //End of if (signature.lastIndexOf (">") > 0) {
		}	//End of if (itrCPFAttribute.getForeignColumn () == null) {
		} //End of  for (Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator()

    stringBuffer.append(TEXT_1);
    
			//For OtherSelectedEntities setting modelEntity values
		if (modelEntity.getName ().equals (modelEntity.getName()) && selectedOtherEntities != null) {
			LOG.info("getObject ..for Dependent Entities...");
			boolean c = false;
		for (Iterator<RelationKey> itr = selectedOtherEntities.iterator (); itr.hasNext (); ) {
			c = false;
			RelationKey relationKey = itr.next();
			String tempProp = relationKey.getRelationShipInfo().getPropertyName();
			List<CPFAttribute> cpfAttributes = nestedAttributes.get(relationKey);
			ModelEntity itrModelEntity = relationKey.getReferencedEntity();
			String setter = new String(itrModelEntity.getName ().toLowerCase().charAt(0) + itrModelEntity.getName ().substring(1));
			RelationShipInfo relationShipInfo = relationKey.getRelationShipInfo();
			RelationShipInfo inverseRelationShipInfo = relationKey.getInverseRelationShipInfo();
			String outerPropertyName = relationShipInfo.getPropertyName();		//Holds this ModelEntity property name in base Entity
			setter = outerPropertyName;
			outerPropertyName = outerPropertyName.toUpperCase().charAt(0) + outerPropertyName.substring(1);
			String genericType = new String(otherEntitiesSign.get (tempProp));
			String inversePropertyName = null;
			String depPKN = itrModelEntity.getPrimaryKey();		//Holds the dependent(dep) Primary(P) Key(K) Name(N)				
			String depPKNClass = tempProp + "PKValue";
			LOG.info(tempProp + " Inverse Relation is : " + inverseRelationShipInfo);
			if(inverseRelationShipInfo != null) {
				inversePropertyName = new String(inverseRelationShipInfo.getPropertyName());
				LOG.info("Inverse ProeprtyName : " + inversePropertyName);
				LOG.info("PropertyName is : " + tempProp);
			}
				//Edited this on 19th Feb...
			distributeData.add(genericType + " " + relationShipInfo.getPropertyName()
								+ itrModelEntity.getName() + " = dataObject.get" + outerPropertyName + "();");
								
			distributeData.add("if (" + relationShipInfo.getPropertyName() + itrModelEntity.getName()
								+ " != null) {");
			LOG.info(tempProp + " generic type is : " + genericType);		
			if (genericType.lastIndexOf ("<") > 0) {
				c = true;
				genericType = genericType.substring (genericType.lastIndexOf ("<")+1, genericType.lastIndexOf (">"));
			}

    
			//As we are not supporting explicitly ManyToMany Creation still considered as ManyTomany only but not implented code in that way
				//but assumed as ManyToOne only So iterator did not do on the Input given by the user
					//later Have to implement If we support for Many instances in the UI
		if (c) {
			LOG.info(tempProp + " Sign is : " + otherEntitiesSign.get(tempProp));
			String temp = otherEntitiesSign.get(tempProp).substring(0,otherEntitiesSign.get(tempProp).lastIndexOf("<") );
			
			distributeData.add("if(" + relationShipInfo.getPropertyName()
								+ itrModelEntity.getName() + ".iterator().hasNext()) {");
								
			distributeData.add(itrModelEntity.getName() + " iterate = " + relationShipInfo.getPropertyName()
								+ itrModelEntity.getName() + ".iterator().next();");
			if (temp.equals ("Set")) { 	//Instead of equals we can use contains also 

    stringBuffer.append(TEXT_83);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_90);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_91);
    stringBuffer.append( tempProp );
    stringBuffer.append(TEXT_64);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_92);
    
			} else {

    stringBuffer.append(TEXT_83);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_90);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_91);
    stringBuffer.append( tempProp );
    stringBuffer.append(TEXT_66);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_92);
    


			}


		}

		

		String varName = relationShipInfo.getPropertyName() + genericType;


    stringBuffer.append(TEXT_83);
    stringBuffer.append( genericType );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_69);
    stringBuffer.append( genericType );
    stringBuffer.append(TEXT_81);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( depPKN.toUpperCase().charAt(0) + depPKN.substring(1) );
    stringBuffer.append(TEXT_82);
    stringBuffer.append( depPKNClass );
    stringBuffer.append(TEXT_58);
    
			//For distribute Data
		if(c) {
			distributeData.add("this." + depPKNClass + " = iterate.get" + depPKN.toUpperCase().charAt(0) + depPKN.substring(1) + "();");
		} else {
			distributeData.add("this." + depPKNClass + " = " + relationShipInfo.getPropertyName() + itrModelEntity.getName()
										+ ".get" + depPKN.toUpperCase().charAt(0) + depPKN.substring(1) + "();");
		}
		Iterator<CPFAttribute> itrCpfAttributes = cpfAttributes.iterator();
		while(itrCpfAttributes.hasNext()) {
			CPFAttribute cpfAttribute = itrCpfAttributes.next();
			ModelAttribute modelAttribute = cpfAttribute.getModelAttrib ();
			String variableName = itrVariableNames.next();      //getting member class variable name here 
			if(cpfAttribute.getForeignColumn()== null) {
				setter = relationShipInfo.getPropertyName();	//TODO added this line on 16th Feb...
				String temp = modelAttribute.getName ().toUpperCase().charAt(0) + modelAttribute.getName().substring(1);
				String tempSetter = setter + temp;
				String tempGetter = "get" + temp;
				String cast = cpfAttribute.getModelAttrib ().getCanonicalTypeName ();
				Class classD = null;
				try {
					classD = Class.forName(cast);
				} catch(ClassNotFoundException e) {
					LOG.info("Not a lang data type...");
				}				
				String mergeArgument = null;
				if (cast.equals("java.sql.Date") || cast.equals("java.sql.Time") || cast.equals("java.sql.Timestamp")) {
					mergeArgument = new String("new " + cast + "(" + tempSetter + ".getTime ())");
				} else if (cast.equals("java.sql.Blob")) {
					mergeArgument= new String ("ModelUtil.mapByteArray2Blob (" + tempSetter + ")");
				} else if (cast.equals("java.sql.Clob")) {
					mergeArgument= new String ("ModelUtil.mapCharArray2Clob (" + tempSetter + ")");
				}else {
					mergeArgument = tempSetter;
				}
				if(c) {
					distributeData.add("this." + variableName + " = iterate." + tempGetter + "();");
				} else {
					distributeData.add("this." + variableName + " = " + relationShipInfo.getPropertyName() + itrModelEntity.getName()
										+ "." + tempGetter + "();");
				}

    stringBuffer.append(TEXT_83);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( mergeArgument );
    stringBuffer.append(TEXT_58);
    
				if(!cpfAttribute.getControlType().equals(CPFConstants.ControlType.TEXTBOX)
					&& cpfAttribute.getTaggedValues() != null && cpfAttribute.getTaggedValues().size() > 0) {
					String selectName = new String("selected");
					selectName = selectName + variableName.toUpperCase().charAt(0) + variableName.substring(1);
					String tvName = new String("d" + cpfAttribute.getModelAttrib().getName() + "TV");
					if(classD != null) {
						distributeData.add("this." + selectName + " = this.getIndex(this." 
											+ variableName + ", this." + tvName + ");");

    stringBuffer.append(TEXT_93);
    stringBuffer.append( selectName );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( variableName );
    stringBuffer.append(TEXT_87);
    stringBuffer.append( tvName );
    stringBuffer.append(TEXT_54);
    
					} else {
						distributeData.add("this." + selectName + " = this.getIndex(CommonUtil.getWrapperForPrimitive(this."
											+ variableName + "), this." + tvName + ");");

    stringBuffer.append(TEXT_93);
    stringBuffer.append( selectName );
    stringBuffer.append(TEXT_59);
    stringBuffer.append( variableName );
    stringBuffer.append(TEXT_94);
    stringBuffer.append( tvName );
    stringBuffer.append(TEXT_95);
    
					}
				}
			} else {
				ModelEntity foreignEntity = modelAttribute.getForeignEntity ();
				setter = new String(foreignEntity.getName().toLowerCase().charAt(0)
									+ foreignEntity.getName().substring(1));
				String feName = foreignEntity.getName ().toLowerCase().charAt(0)
									+ foreignEntity.getName ().substring(1);
				String temp = foreignEntity.getPrimaryKey ().toUpperCase().charAt(0)
									+ foreignEntity.getPrimaryKey ().substring(1);
				String tempSetter = modelAttribute.getName();
				RelationShipInfo relationShipInfoF = cpfAttribute.getModelAttrib().getRelType();
				RelationShipInfo inverseRelationShipInfoF = cpfAttribute.getModelAttrib().getInverseRelType();
				String propertyName = relationShipInfoF.getPropertyName ();
				propertyName = propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
				String typeInfo = relationShipInfoF.getSimpleTypeInfo();
				if(c) {
					distributeData.add(typeInfo + " inner = " + "iterate.get"
										+ foreignEntity.getName() + "();");
				} else {
					distributeData.add(typeInfo + " inner = " + relationShipInfo.getPropertyName() + itrModelEntity.getName() + ".get"
										+ propertyName + "();");
										
					distributeData.add("if(inner != null) {");
				}
				if(typeInfo.contains("<")) {
					String inversePropertyNameF = inverseRelationShipInfoF.getPropertyName();
					inversePropertyNameF = inversePropertyNameF.toUpperCase().charAt(0)
											+ inversePropertyNameF.substring(1);
					if(typeInfo.startsWith("Set")) {
						distributeData.add("this." + variableName + " = new HashSet<Long>();");

    stringBuffer.append(TEXT_63);
    stringBuffer.append( typeInfo );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( feName );
    stringBuffer.append(TEXT_96);
    stringBuffer.append( typeInfo );
    stringBuffer.append(TEXT_97);
    
					} else {
						distributeData.add("this." + variableName + " = new ArrayList<Long>();");

    stringBuffer.append(TEXT_63);
    stringBuffer.append( typeInfo );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( feName );
    stringBuffer.append(TEXT_98);
    stringBuffer.append( foreignEntity.getName()  );
    stringBuffer.append(TEXT_99);
    
					}
					distributeData.add("Iterator<" + foreignEntity.getName() + "> temp = inner.iterator();");
					distributeData.add("while(temp.hasNext()) {");
					distributeData.add("	this." + variableName + ".add(temp.next().get" + temp + "());");
					distributeData.add("}");

    stringBuffer.append(TEXT_100);
    stringBuffer.append( tempSetter );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( tempSetter );
    stringBuffer.append(TEXT_101);
    stringBuffer.append( tempSetter );
    stringBuffer.append(TEXT_102);
    stringBuffer.append( foreignEntity.getName() );
    stringBuffer.append(TEXT_103);
    stringBuffer.append( foreignEntity.getName() );
    stringBuffer.append(TEXT_104);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_105);
    stringBuffer.append( tempSetter );
    stringBuffer.append(TEXT_106);
    
					if (relationShipInfoF.getMapping().equals(CPFConstants.RelationshipType.OneToMany)) {

    stringBuffer.append(TEXT_107);
    stringBuffer.append( inversePropertyNameF );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_58);
    
					}

    stringBuffer.append(TEXT_108);
    stringBuffer.append( feName );
    stringBuffer.append(TEXT_109);
    
				} else {
					String inversePropertyNameF = null;
					if(relationShipInfoF.getMapping ().equals(CPFConstants.RelationshipType.OneToOne)) {
						if(!cpfAttribute.getModelAttrib().isOwner()) {
							if(inverseRelationShipInfoF != null) {
								inversePropertyNameF = inverseRelationShipInfoF.getPropertyName();
								inversePropertyNameF = inversePropertyNameF.toUpperCase().charAt(0)
														+ inversePropertyNameF.substring(1);
							} else {
								System.out.println("Did not get parent Entity propertyName in child so exiting..");
								System.exit(1);
							}
						}
					}
					distributeData.add("this." + variableName + " = " + "inner.get" + temp + "();");

    stringBuffer.append(TEXT_110);
    stringBuffer.append( tempSetter );
    stringBuffer.append(TEXT_111);
    stringBuffer.append( foreignEntity.getName () );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( feName );
    stringBuffer.append(TEXT_112);
    stringBuffer.append( foreignEntity.getName () );
    stringBuffer.append(TEXT_113);
    stringBuffer.append( feName );
    stringBuffer.append(TEXT_114);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( tempSetter );
    stringBuffer.append(TEXT_58);
    
					if(inversePropertyNameF != null) {

    stringBuffer.append(TEXT_115);
    stringBuffer.append( feName );
    stringBuffer.append(TEXT_114);
    stringBuffer.append( inversePropertyNameF );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_58);
    
					}
				}
				String firstArg = cpfAttribute.getModelAttrib ().getName ();
				String secondArg = relationShipInfo.getPropertyName() + propertyName + cpfAttribute.getForeignColumn().getName().toUpperCase()
									.charAt(0) + cpfAttribute.getForeignColumn().getName().substring(1);
				distributeData.add("this.selected" + propertyName + " = this.getIndex(this." + firstArg
									+ ", this." + secondArg + ");");
				distributeData.add("} else {");
				distributeData.add("this.selected" + propertyName + " = -1;");
				distributeData.add("}");

    stringBuffer.append(TEXT_115);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( propertyName );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( feName );
    stringBuffer.append(TEXT_116);
    stringBuffer.append( propertyName );
    stringBuffer.append(TEXT_56);
    stringBuffer.append( firstArg );
    stringBuffer.append(TEXT_87);
    stringBuffer.append( secondArg );
    stringBuffer.append(TEXT_117);
    stringBuffer.append( propertyName );
    stringBuffer.append(TEXT_118);
    
			}
		}
				if(c || (relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne)
					&& inversePropertyName != null)) {
					inversePropertyName = inversePropertyName.toUpperCase().charAt(0) +
											inversePropertyName.substring(1);

    stringBuffer.append(TEXT_115);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( inversePropertyName );
    stringBuffer.append(TEXT_119);
    
				}
		if(c) {			
			distributeData.add("}");

    stringBuffer.append(TEXT_83);
    stringBuffer.append( tempProp );
    stringBuffer.append(TEXT_120);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_58);
    
			varName = tempProp;
		}
		distributeData.add("}");
		String setterName = tempProp.toUpperCase().charAt(0) + tempProp.substring(1);

    stringBuffer.append(TEXT_121);
    stringBuffer.append( setterName );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_58);
    
		}
		}

    stringBuffer.append(TEXT_122);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_123);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_124);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_125);
    
			LOG.info("Adding getOperationId method inside Class.....");
			Map<Integer, String[]> operationRoleMap = null;			//which holds roles vs operationIDs
			Iterator<CPFConstants.OperationType> operations = cpfScreen.getMappedRoles().keySet().iterator();
			while(operations.hasNext()) {
				CPFConstants.OperationType operation = operations.next();

    stringBuffer.append(TEXT_126);
    stringBuffer.append( operation );
    stringBuffer.append(TEXT_127);
    
			operationRoleMap = cpfScreen.getOperationRoleMap (operation);
			if(operationRoleMap != null && operationRoleMap.size() != 0) {
			for (Iterator<Integer> keysRole = operationRoleMap.keySet ().iterator (); keysRole.hasNext ();) {
				Integer id = keysRole.next ();
				String[] roles = operationRoleMap.get (id);
				String condition = new String ();
				for (int i = 0; i < roles.length; i++) {
					condition = condition.concat ("exContext.isUserInRole (\"" + roles [i] + "\") ||");
				} 
				condition = condition.substring (0, condition.lastIndexOf (")")+1); 

    stringBuffer.append(TEXT_128);
    stringBuffer.append( condition );
    stringBuffer.append(TEXT_129);
    stringBuffer.append( id.intValue () );
    stringBuffer.append(TEXT_130);
    
			}
			}	//End of If(operationRoleMap != null)
			}	//End of while(operations.hasNext())

    stringBuffer.append(TEXT_131);
    
			//Generating ActionEvents for this Managed Bean
		String passingObject = modelEntity.getName().toLowerCase().charAt(0) + modelEntity.getName().substring(1);
		LOG.info("Adding saveAction method inside Class.....");

    stringBuffer.append(TEXT_132);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_133);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( passingObject );
    stringBuffer.append(TEXT_134);
    stringBuffer.append( passingObject );
    stringBuffer.append(TEXT_135);
    stringBuffer.append( passingObject );
    stringBuffer.append(TEXT_136);
    stringBuffer.append( passingObject );
    stringBuffer.append(TEXT_137);
    
		if(cpfScreen.getPortletRef().getListScreen() != null) {

    stringBuffer.append(TEXT_138);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_139);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_140);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_141);
    stringBuffer.append( operationId );
    stringBuffer.append(TEXT_142);
    
		}

    stringBuffer.append(TEXT_143);
    
		if(puVarName != null) {
			Iterator<String> itrPuVarName = puVarName.iterator();

    stringBuffer.append(TEXT_144);
    
			while(itrPuVarName.hasNext()) {
				String tempP = itrPuVarName.next();
				String[] tempArg = puArguments.get(tempP);
				if(tempArg[4].equals("1")) {

    stringBuffer.append(TEXT_145);
    stringBuffer.append( tempP );
    stringBuffer.append(TEXT_146);
    
				}
			}
		}

    stringBuffer.append(TEXT_147);
    
		LOG.info("adding addAction method inside Class....");
			itrSignatures = signatures.iterator();
			itrVaribleNames = varibleNames.iterator();
			Iterator<String> itrDv = defaultValues.iterator();
			Iterator<String> itrFormatType = formatTypes.iterator();
			while(itrDv.hasNext()) {
				String dv = itrDv.next();
				String sig = itrSignatures.next();
				String varName = itrVaribleNames.next();
				String formatType = itrFormatType.next();
				if(dv == null) {
					Class  c = null; 
					try {
						LOG.info("SIg is : " + sig);
						c = Class.forName(sig);
						LOG.info("Class is : " + c);
					} catch (ClassNotFoundException e) {
						LOG.info("Not a lang data type...");
					}
					if(sig.contains("<") || sig.contains("[") || c != null) {

    stringBuffer.append(TEXT_93);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_148);
    
					} else {

    stringBuffer.append(TEXT_93);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_149);
    
					}
				} else {
					if(sig.equals("java.lang.String")) {

    stringBuffer.append(TEXT_93);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_150);
    stringBuffer.append( dv );
    stringBuffer.append(TEXT_151);
    
					}  else if(sig.equals("java.lang.Long")){

    stringBuffer.append(TEXT_93);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_152);
    stringBuffer.append( dv );
    stringBuffer.append(TEXT_58);
    
					} else if (sig.equals("java.lang.Integer")) {

    stringBuffer.append(TEXT_93);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_153);
    stringBuffer.append( dv );
    stringBuffer.append(TEXT_58);
    
					}else if(sig.equals("java.lang.Double")) {

    stringBuffer.append(TEXT_154);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_155);
    stringBuffer.append( dv );
    stringBuffer.append(TEXT_58);
    
					} else if (sig.equals("java.lang.Float")) {

    stringBuffer.append(TEXT_93);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_156);
    stringBuffer.append( dv );
    stringBuffer.append(TEXT_58);
    
					}else if(sig.equals("java.util.Date")) {
						if(formatType.equals("DATE")) {
						//TODO Date has to do something

    stringBuffer.append(TEXT_157);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_158);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_159);
    
						}
					} else if(sig.equals("java.lang.Character") || sig.equals("char")) {

    stringBuffer.append(TEXT_93);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_160);
    stringBuffer.append( dv );
    stringBuffer.append(TEXT_161);
    
					}else {

    stringBuffer.append(TEXT_93);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_39);
    stringBuffer.append( dv );
    stringBuffer.append(TEXT_4);
    
					}
				}
			}
				//Setting dependent primaryKey values to null
			if(selectedOtherEntities != null) {
				for (Iterator<RelationKey> itr = selectedOtherEntities.iterator(); itr.hasNext(); ) {
					RelationKey rk = itr.next();
					String otherEntityPKName = new String(rk.getRelationShipInfo().getPropertyName()); 
					otherEntityPKName = otherEntityPKName.concat("PKValue");

    stringBuffer.append(TEXT_93);
    stringBuffer.append( otherEntityPKName );
    stringBuffer.append(TEXT_148);
    
				}
			}

    stringBuffer.append(TEXT_162);
    
		if(puVarName != null) {
			Iterator<String> itrPuVarName = puVarName.iterator();

    stringBuffer.append(TEXT_163);
    
			while(itrPuVarName.hasNext()) {
				String tempP = itrPuVarName.next();
				String[] tempArg = puArguments.get(tempP);

    stringBuffer.append(TEXT_93);
    stringBuffer.append( tempP );
    stringBuffer.append(TEXT_164);
    stringBuffer.append( tempArg[0] );
    stringBuffer.append(TEXT_165);
    stringBuffer.append( tempArg[1] );
    stringBuffer.append(TEXT_165);
    stringBuffer.append( tempArg[2] );
    stringBuffer.append(TEXT_166);
    stringBuffer.append( tempArg[3] );
    stringBuffer.append(TEXT_167);
    stringBuffer.append( tempArg[5] );
    stringBuffer.append(TEXT_151);
    
			if(tempArg[4].equals("1")) {

    stringBuffer.append(TEXT_145);
    stringBuffer.append( tempP );
    stringBuffer.append(TEXT_146);
    
			}
			}
		}
		LOG.info("Adding viewAction inside Class...");

    stringBuffer.append(TEXT_168);
    
		if(puVarName != null) {
			Iterator<String> itrPuVarName = puVarName.iterator();

    stringBuffer.append(TEXT_169);
    
			while(itrPuVarName.hasNext()) {
				String tempP = itrPuVarName.next();
				String[] tempArg = puArguments.get(tempP);

    stringBuffer.append(TEXT_93);
    stringBuffer.append( tempP );
    stringBuffer.append(TEXT_164);
    stringBuffer.append( tempArg[0] );
    stringBuffer.append(TEXT_165);
    stringBuffer.append( tempArg[1] );
    stringBuffer.append(TEXT_165);
    stringBuffer.append( tempArg[2] );
    stringBuffer.append(TEXT_166);
    stringBuffer.append( tempArg[3] );
    stringBuffer.append(TEXT_170);
    stringBuffer.append( tempArg[5] );
    stringBuffer.append(TEXT_151);
    
			}
		}

    stringBuffer.append(TEXT_171);
    
		LOG.info("Adding userIsInRole method to Class....");
		List<String> roles = cpfScreen.getMappedRoles().get(CPFConstants.OperationType.VIEW);
		Iterator<String> itrRoles = roles.iterator();
		while(itrRoles.hasNext()) {
			String roleIs = itrRoles.next();

    stringBuffer.append(TEXT_172);
    stringBuffer.append( roleIs );
    stringBuffer.append(TEXT_173);
    stringBuffer.append( roleIs );
    stringBuffer.append(TEXT_174);
    
		}

    stringBuffer.append(TEXT_175);
    
		LOG.info("Adding getDetailsVisibility method to class....");
		itrRoles = roles.iterator();
		while(itrRoles.hasNext()) {
			String roleIs = itrRoles.next();

    stringBuffer.append(TEXT_176);
    stringBuffer.append( roleIs );
    stringBuffer.append(TEXT_177);
    
			Iterator<List<String>> itrAttrbVisibility = attrbVisibility.iterator();
			while(itrAttrbVisibility.hasNext()) {
				List<String> tempRoles = itrAttrbVisibility.next();
				if(tempRoles == null) {

    stringBuffer.append(TEXT_178);
    
				} else if(tempRoles.size() == 0 || !tempRoles.contains(roleIs)) {

    stringBuffer.append(TEXT_178);
    
				} else {

    stringBuffer.append(TEXT_179);
    
				}
			}

    stringBuffer.append(TEXT_180);
    
		}

    stringBuffer.append(TEXT_181);
    
	if(modelEntity.isStatic()) {

    stringBuffer.append(TEXT_182);
    
	} else {

    stringBuffer.append(TEXT_183);
    
	}

    stringBuffer.append(TEXT_184);
    
	Iterator<String> itrDistributeData = distributeData.iterator();
	while(itrDistributeData.hasNext()) {

    stringBuffer.append(TEXT_8);
    stringBuffer.append( itrDistributeData.next() );
    
	}

    stringBuffer.append(TEXT_185);
    
	LOG.info("Adding resetValues method to Class...");
	itrVariableNames = varibleNames.iterator();
	Iterator<String> itrSig = signatures.iterator();
	while(itrVariableNames.hasNext()) {
		Class c = null;
		String sig = itrSig.next();
		try {
			c = Class.forName(sig);
		} catch(ClassNotFoundException e) {
			LOG.info("Not a lang data type...");
		}
		if(sig.contains("<") || sig.contains("[") || c != null) {

    stringBuffer.append(TEXT_186);
    stringBuffer.append( itrVariableNames.next() );
    stringBuffer.append(TEXT_148);
    
		} else {

    stringBuffer.append(TEXT_186);
    stringBuffer.append( itrVariableNames.next() );
    stringBuffer.append(TEXT_149);
    
		}
	}
	LOG.info("Adding fillCriteria method to Class...");

    stringBuffer.append(TEXT_187);
    
		//This is for only Related One2One or Many2One related attributes only... used for getting index from selectItem of selcted item from GUI...
	if(puSelectedName != null || taggedValues != null) {

    stringBuffer.append(TEXT_188);
    
	}

    stringBuffer.append(TEXT_189);
    stringBuffer.append( modelEntity.getName() );
    stringBuffer.append(TEXT_190);
    stringBuffer.append( modelEntity.getPrimaryKey() );
    stringBuffer.append(TEXT_191);
    stringBuffer.append( modelEntity.getName() );
    stringBuffer.append(TEXT_192);
    stringBuffer.append( modifyFields );
    stringBuffer.append(TEXT_193);
    stringBuffer.append( viewFields );
    stringBuffer.append(TEXT_194);
    
		//Bean Generation Ended here.....
	LOG.info("Generating Details Managed Bean finished from Template side...");

    stringBuffer.append(TEXT_195);
    stringBuffer.append(TEXT_196);
    return stringBuffer.toString();
  }
}
