package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.ide.model.*;
import java.util.*;
import com.genband.m5.maps.ide.CPFPlugin;
import java.util.List;

public class ReturnBean
{
  protected static String nl;
  public static synchronized ReturnBean create(String lineSeparator)
  {
    nl = lineSeparator;
    ReturnBean result = new ReturnBean();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = NL + NL + "package com.genband.m5.maps.messages;" + NL + "" + NL + "import java.util.*;" + NL + "import com.genband.m5.maps.common.CommonUtil;" + NL + "" + NL + "/**" + NL + "\t\tThis is the model Bean class for ";
  protected final String TEXT_3 = " " + NL + "\t\tcreated by Damodar Reddy T" + NL + "*/" + NL + "" + NL + "public class  ";
  protected final String TEXT_4 = " {" + NL;
  protected final String TEXT_5 = NL + "\t\t";
  protected final String TEXT_6 = ";";
  protected final String TEXT_7 = NL + NL + "\t\tpublic ";
  protected final String TEXT_8 = " () {" + NL + "\t\t}" + NL;
  protected final String TEXT_9 = NL + "\t\tpublic ";
  protected final String TEXT_10 = " ";
  protected final String TEXT_11 = " () {" + NL + "\t\t\treturn this.";
  protected final String TEXT_12 = ";" + NL + "\t\t}" + NL + "\t\t ";
  protected final String TEXT_13 = NL + "\t\tpublic void ";
  protected final String TEXT_14 = " (";
  protected final String TEXT_15 = ") {" + NL + "\t\t\tthis.";
  protected final String TEXT_16 = " = ";
  protected final String TEXT_17 = NL + "\tpublic ";
  protected final String TEXT_18 = " (Object[] object) {" + NL + "\t\tCommonUtil cu = new CommonUtil();" + NL + "\t";
  protected final String TEXT_19 = NL + "\t\tif(object[";
  protected final String TEXT_20 = "] != null)";
  protected final String TEXT_21 = NL + "\t\t\t";
  protected final String TEXT_22 = " = cu.trObjectToPrimitive(object [";
  protected final String TEXT_23 = "], ";
  protected final String TEXT_24 = ");";
  protected final String TEXT_25 = " = (";
  protected final String TEXT_26 = ") object [";
  protected final String TEXT_27 = "];";
  protected final String TEXT_28 = NL + "\t}" + NL + "}";
  protected final String TEXT_29 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     	
	CPFPlugin LOG = CPFPlugin.getDefault();
	CPFResource CPFArgument = (CPFResource)argument; 
 	CPFScreen cpfScreen = (CPFScreen)CPFArgument.getCpfScreen();
 	ModelEntity modelEntity = null;
 	modelEntity = (ModelEntity)cpfScreen.getBaseEntity();
 	ModelEntity baseEntity = cpfScreen.getPortletRef().getBaseEntity();
 	LOG.info("Return Bean details generation started for Base Entity : " + baseEntity.getName());
 	List<CPFAttribute> selectedAttributes = cpfScreen.getSelectedAttributes ();					
 	int operationId = cpfScreen.getPortletRef().getPortletId();   //Holds Operation Id of the method
 	LOG.info("Creating Return Bean details for the portlet Id : " + operationId);
	Map<RelationKey, List<CPFAttribute>> nestedAttributes = cpfScreen.getNestedAttributes();
	List<String> varibleNames = new ArrayList<String>();
	List<String> setters = new ArrayList<String>();
	List<String> getters = new ArrayList<String>();
	List<String> signatures = new ArrayList<String>();
	List<String> declarations = new ArrayList<String>();
	
	String className = baseEntity.getName()+"Details_"+operationId;
	LOG.info("The class name for Return Details bean name is : " + className);

     
	//Adding Base Entity Primary Key to the Return Details
	varibleNames.add("primaryKeyValue");	//Hard coding the variable Name
	setters.add("setPrimaryKeyValue");
	getters.add("getPrimaryKeyValue");
	signatures.add("java.lang.Long");
	declarations.add("private java.lang.Long primaryKeyValue");	//Adding declaration
	
		//doing process for Variable declaration, setters, getters and DataTypes here..................
	LOG.info("Started doing process to get Variable names declaration, their setters , getters and Data Types too.."); 
	for(Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext(); ) { 
 		CPFAttribute selectedAttribute = (CPFAttribute)attributeItr.next();    //For each selected attribute
		ModelAttribute modelAttrib; 
		String temp = null;   //holds model entity name
		if (selectedAttribute.getForeignColumn () == null) {  
			modelAttrib = selectedAttribute.getModelAttrib();
			temp  = baseEntity.getName();		
		}
		else   //If it is a foreign column then get attribute from Foreign column
		{
			LOG.info("selected attribute found as Foreign attribute with name : " + selectedAttribute.getName());
			modelAttrib = selectedAttribute.getForeignColumn ();
			LOG.info("Display column for this foreign attribtue is : " + modelAttrib.getName());
				//Edited on 20th Feb
			temp  = selectedAttribute.getModelAttrib().getName ();
		}
		
		String type = modelAttrib.getCanonicalTypeName();      //retrieving Attribute data type
		if (type.equals ("java.sql.Date") || type.equals ("java.sql.Time") || type.equals ("java.sql.Timestamp")) {
			LOG.info("Date Type attribute found ....");
			type = new String ("java.util.Date");
		} else if (type.equals ("java.sql.Blob")) {
			type = new String("byte[]");
		} else if (type.equals ("java.sql.Clob")) {
			type = new String("char[]");
		}
		String varName = modelAttrib.getName();
		varName = varName.toUpperCase ().charAt (0) + varName.substring (1);
		setters.add ("set" + temp + varName);
		getters.add ("get" + temp + varName);
		temp = temp.toLowerCase ().charAt (0) + temp.substring (1);
		varName = temp + varName.toUpperCase ().charAt (0) + varName.substring (1);
		varibleNames.add (varName);
		signatures.add (type);
		declarations.add("private " + type + " " + varName);	//Adding declaration
	}
			//Generating Varible declarations for Dependent here........
	if (nestedAttributes != null) {
		LOG.info("Doping the above process for Dependent selected attributes");
		Set<RelationKey> selectedOtherEntities = nestedAttributes.keySet();
		for (Iterator<RelationKey> itr = selectedOtherEntities.iterator(); itr.hasNext(); ) {
			RelationKey relationKey = itr.next();
			ModelEntity otherModelEntity = relationKey.getReferencedEntity();
			List<CPFAttribute> selectedOtherAttributes = nestedAttributes.get (relationKey);
			for (Iterator<CPFAttribute> itrAttribute = selectedOtherAttributes.iterator(); itrAttribute.hasNext();) {
				CPFAttribute selectedAttribute = (CPFAttribute) itrAttribute.next ();
				ModelAttribute modelAttrib; 
				String type = null;
				String varName = null;
				String temp = null;
				if (selectedAttribute.getForeignColumn () == null) {
					modelAttrib = selectedAttribute.getModelAttrib(); 
					//temp  = otherModelEntity.getName();			
					temp  = relationKey.getRelationShipInfo().getPropertyName();
				}
				else
				{
					LOG.info("Found this dependent selected attribute as foreign attribute with name : " + selectedAttribute.getName());
					modelAttrib = selectedAttribute.getForeignColumn ();
					LOG.info("The display source column name for this foreign attribtue is : " + modelAttrib.getName());
						//Edited on 20th Feb
					temp  = selectedAttribute.getModelAttrib().getName ();
					temp = temp.toUpperCase().charAt(0) + temp.substring(1);	//Capitalizing the first letter
					temp = otherModelEntity.getName() + temp; 	//prefixing the container entity name
				}
				type = modelAttrib.getCanonicalTypeName();
				if (type.equals ("java.sql.Date") || type.equals ("java.sql.Time") || type.equals ("java.sql.Timestamp")) {
					LOG.info("Found date type attribute for Dependent selected attribute..");
					type = new String ("java.util.Date");
				}else if (type.equals ("java.sql.Blob")) {
					type = new String ("Byte[]");
				} else if (type.equals ("java.sql.Clob")) {
					type = new String ("Char[]");
				}
				varName = modelAttrib.getName();		
				varName = varName.toUpperCase ().charAt (0) + varName.substring (1);
				setters.add ("set" + temp + varName);
				getters.add ("get" + temp + varName);
				temp = temp.toLowerCase ().charAt (0) + temp.substring (1);
				varName = temp + varName.toUpperCase ().charAt (0) + varName.substring (1);
				varibleNames.add (varName);
				signatures.add (type);
				declarations.add("private " + type + " " + varName);	//Adding declaration
			}	//End of for loop iterator over CPFAttribtues...
		}	//End of for loop iterator over selectedotherentities..
	}	//End of if nestedattributes != null...
	//End of doing process for Variable declaration, setters, getters and DataTypes here...............

    stringBuffer.append(TEXT_1);
    
	//Bean generation Started Here.....

    stringBuffer.append(TEXT_2);
    stringBuffer.append( modelEntity.getName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_4);
    
	LOG.info("Declaring variables inside the class....");
		//Variables declaration started here....
	for(Iterator<String> itrDeclarations = declarations.iterator(); itrDeclarations.hasNext();) {

    stringBuffer.append(TEXT_5);
    stringBuffer.append( itrDeclarations.next() );
    stringBuffer.append(TEXT_6);
    
	}	//End Variables declaration here....
	LOG.info("Variables declaration is finished..");

    stringBuffer.append(TEXT_7);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_8);
    
	//Generating Getters here.......................
	LOG.info("Defining Getters in side the class.....");
	Iterator<String> itrGetter = getters.iterator ();
	Iterator<String> itrVaribleNames = varibleNames.iterator ();
	Iterator<String> itrSignatures =  signatures.iterator ();
	while (itrGetter.hasNext ()) {

    stringBuffer.append(TEXT_9);
    stringBuffer.append( (String)itrSignatures.next() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( (String)itrGetter.next () );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( (String)itrVaribleNames.next () );
    stringBuffer.append(TEXT_12);
    
	}

    
	//Generating Setters here.......................
	LOG.info("Defining setters inside the class.....");
	Iterator<String> itrSetter = setters.iterator ();
	itrVaribleNames = varibleNames.iterator ();
	itrSignatures =  signatures.iterator ();
	while (itrSetter.hasNext ()) {
		String varName = (String)itrVaribleNames.next ();

    stringBuffer.append(TEXT_13);
    stringBuffer.append( (String)itrSetter.next () );
    stringBuffer.append(TEXT_14);
    stringBuffer.append( (String)itrSignatures.next() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_15);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_12);
    
	}

    
	//Generating Constructor here.........
	LOG.info("Defining constructor which will set all the values from the object received as an argument..");

    stringBuffer.append(TEXT_17);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_18);
    
		Iterator<String> itrVaribleNames1 = varibleNames.iterator ();
		Iterator<String> itrSignatures1 = signatures.iterator();
		int i = 0;
		while (itrVaribleNames1.hasNext ()) {
			String dType = itrSignatures1.next ();
			String rVarName = itrVaribleNames1.next ();
			Class c = null;
			try {
				c = Class.forName(dType);
			} catch(ClassNotFoundException e) {
				LOG.info("Not a lang data type...");
			}

    stringBuffer.append(TEXT_19);
    stringBuffer.append( i );
    stringBuffer.append(TEXT_20);
    
			if(c == null) {

    stringBuffer.append(TEXT_21);
    stringBuffer.append( rVarName );
    stringBuffer.append(TEXT_22);
    stringBuffer.append( i++ );
    stringBuffer.append(TEXT_23);
    stringBuffer.append( rVarName );
    stringBuffer.append(TEXT_24);
    
			} else {

    stringBuffer.append(TEXT_21);
    stringBuffer.append( rVarName );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( dType );
    stringBuffer.append(TEXT_26);
    stringBuffer.append( i++ );
    stringBuffer.append(TEXT_27);
    
			}
		}
	LOG.info("constructor which will set all the values from the object received as an argument has been declared..");

    stringBuffer.append(TEXT_28);
    
	//Bean Generation Ended here.....
	LOG.info("Return Bean generation is finished from tempalte side....");

    stringBuffer.append(TEXT_29);
    return stringBuffer.toString();
  }
}
