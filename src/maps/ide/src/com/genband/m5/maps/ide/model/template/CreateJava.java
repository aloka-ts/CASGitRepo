package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.ide.model.*;
import com.genband.m5.maps.ide.CPFPlugin;
import java.util.*;
import java.util.List;
import com.genband.m5.maps.ide.model.util.*;
import com.genband.m5.maps.common.*;

public class CreateJava
{
  protected static String nl;
  public static synchronized CreateJava create(String lineSeparator)
  {
    nl = lineSeparator;
    CreateJava result = new CreateJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "package com.genband.m5.maps.messages;" + NL;
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = ";";
  protected final String TEXT_4 = NL + NL + "/**" + NL + "\t\tThis is the model Bean class for ";
  protected final String TEXT_5 = " " + NL + "\t\tcreated by Damodar Reddy T" + NL + "*/" + NL + "" + NL + "public class  ";
  protected final String TEXT_6 = " {" + NL + "\t";
  protected final String TEXT_7 = NL + "\t\t";
  protected final String TEXT_8 = NL + NL + "\t\tpublic ";
  protected final String TEXT_9 = " () {" + NL + "\t\t}" + NL;
  protected final String TEXT_10 = "\t" + NL + "   \t\tpublic ";
  protected final String TEXT_11 = " ";
  protected final String TEXT_12 = " () {" + NL + "   \t\t\t\treturn this.";
  protected final String TEXT_13 = ";" + NL + "   \t\t}\t \t\t ";
  protected final String TEXT_14 = NL + "\t\tpublic ";
  protected final String TEXT_15 = " () {" + NL + "\t\t\treturn this.";
  protected final String TEXT_16 = ";" + NL + "\t\t}";
  protected final String TEXT_17 = "\t" + NL + "\t\tpublic void ";
  protected final String TEXT_18 = " (";
  protected final String TEXT_19 = ") {" + NL + "\t\t\t\tthis.";
  protected final String TEXT_20 = " = ";
  protected final String TEXT_21 = NL + "\t\tpublic void ";
  protected final String TEXT_22 = ") {" + NL + "\t\t\tthis.";
  protected final String TEXT_23 = " Obj) {" + NL + "\t\t" + NL + "\t\t\tint i;";
  protected final String TEXT_24 = NL + "\t\t\t";
  protected final String TEXT_25 = ");";
  protected final String TEXT_26 = NL + "\t\t\tif(Obj.";
  protected final String TEXT_27 = "() != null) {" + NL + "\t\t\t\ti = 0;" + NL + "\t\t\t\tLong[] ";
  protected final String TEXT_28 = " = new Long[Obj.";
  protected final String TEXT_29 = "().size()];" + NL + "\t\t\t\tfor (Iterator<";
  protected final String TEXT_30 = "> itr = Obj.";
  protected final String TEXT_31 = "().iterator (); itr.hasNext ();) {" + NL + "\t\t\t\t\t";
  protected final String TEXT_32 = "[i++] =  new Long(itr.next ().get";
  protected final String TEXT_33 = " ());\t\t//getCountryId() has to replace with some method call" + NL + "\t\t\t\t}" + NL + "\t\t\t\t";
  protected final String TEXT_34 = ");" + NL + "\t\t\t}";
  protected final String TEXT_35 = " () != null)" + NL + "\t\t\t\t";
  protected final String TEXT_36 = " (Obj.";
  protected final String TEXT_37 = " ().get";
  protected final String TEXT_38 = " ());";
  protected final String TEXT_39 = NL + "\t\t\tIterator<";
  protected final String TEXT_40 = "> ";
  protected final String TEXT_41 = "Entity = Obj.get";
  protected final String TEXT_42 = " ().iterator ();";
  protected final String TEXT_43 = " = new HashSet<";
  protected final String TEXT_44 = "> ();";
  protected final String TEXT_45 = " = new ArrayList<";
  protected final String TEXT_46 = " temp = null;" + NL + "\t\t\twhile (";
  protected final String TEXT_47 = "Entity.hasNext ()) {" + NL + "\t\t\t\ttemp = new ";
  protected final String TEXT_48 = "Entity.next ());" + NL + "\t\t\t\t";
  protected final String TEXT_49 = ".add (temp);" + NL + "\t\t\t}";
  protected final String TEXT_50 = NL + "\t\t\tif(Obj.get";
  protected final String TEXT_51 = " () != null) {" + NL + "\t\t\t";
  protected final String TEXT_52 = " = new ";
  protected final String TEXT_53 = " (Obj.get";
  protected final String TEXT_54 = "(";
  protected final String TEXT_55 = NL + "\t\t\t}";
  protected final String TEXT_56 = NL + "\t\t}" + NL + "\t\t" + NL + "\t//Generating Merge Function which will set values for ModelEntity and returns ModelEntity" + NL + "\t    public ";
  protected final String TEXT_57 = " merge() {" + NL + "\t    \t";
  protected final String TEXT_58 = " returnEntity = new ";
  protected final String TEXT_59 = " ();";
  protected final String TEXT_60 = NL + "\t\t\tif(";
  protected final String TEXT_61 = " != null)";
  protected final String TEXT_62 = NL + "\t\t\treturnEntity.";
  protected final String TEXT_63 = NL + "\t\t\t\tif(";
  protected final String TEXT_64 = " != null && ";
  protected final String TEXT_65 = ".length > 0) {";
  protected final String TEXT_66 = NL + "\t\t\t\t\t";
  protected final String TEXT_67 = NL + "\t\t\t\t\tfor (int i = 0; i < ";
  protected final String TEXT_68 = ".length; i++) {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_69 = " ();" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_70 = ".set";
  protected final String TEXT_71 = " (this.";
  protected final String TEXT_72 = "[i]);" + NL + "\t\t\t\t\t\t\t//Setting child reference in Parent entity";
  protected final String TEXT_73 = NL + "\t\t\t\t\t\t";
  protected final String TEXT_74 = "(returnEntity);";
  protected final String TEXT_75 = "\t" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_76 = ".add (";
  protected final String TEXT_77 = ");" + NL + "\t\t\t\t\t}" + NL + "\t\t\t\t\treturnEntity.";
  protected final String TEXT_78 = ");" + NL + "\t\t\t\t}";
  protected final String TEXT_79 = NL + "\t\t\tif(this.";
  protected final String TEXT_80 = " != null) {" + NL + "\t\t\t";
  protected final String TEXT_81 = " ();" + NL + "\t\t\t";
  protected final String TEXT_82 = " (returnEntity);";
  protected final String TEXT_83 = ");" + NL + "\t\t\t}\t\t";
  protected final String TEXT_84 = "\t";
  protected final String TEXT_85 = ") {";
  protected final String TEXT_86 = "<";
  protected final String TEXT_87 = "Entity = new HashSet<";
  protected final String TEXT_88 = "Entity = new ArrayList<";
  protected final String TEXT_89 = "Bean = ";
  protected final String TEXT_90 = ".iterator();" + NL + "\t\t\twhile (";
  protected final String TEXT_91 = "Bean.hasNext ()) {" + NL + "\t\t\t\t";
  protected final String TEXT_92 = " tempEntity = ";
  protected final String TEXT_93 = "Bean.next ().merge ();" + NL + "\t\t\t\t\t//Setting child entity reference in parent entity" + NL + "\t\t\t\ttempEntity.set";
  protected final String TEXT_94 = " (returnEntity);" + NL + "\t\t\t\t";
  protected final String TEXT_95 = "Entity.add (tempEntity);" + NL + "\t\t\t}" + NL + "\t\t\treturnEntity.set";
  protected final String TEXT_96 = "Entity);";
  protected final String TEXT_97 = " = this.";
  protected final String TEXT_98 = ".merge ();;";
  protected final String TEXT_99 = "    " + NL + "\t\t\t//Setting child entity reference in parent entity" + NL + "\t\t\t";
  protected final String TEXT_100 = NL + "\t\t\treturnEntity.set";
  protected final String TEXT_101 = NL + "\t\t\treturn returnEntity;" + NL + "\t    }" + NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
//Varible declaration for the template usage
	CPFPlugin LOG = CPFPlugin.getDefault();
	CPFResource CPFArgument = (CPFResource)argument; 
 	CPFScreen cpfScreen = (CPFScreen)CPFArgument.getCpfScreen();
 	ResourceInfo resourceInfo = (ResourceInfo)CPFArgument.getResourceInfo();
 	String resourceName = resourceInfo.getResourceName();
 	LOG.info("Generating Java Bean for resource : " + resourceName);
 	ModelEntity modelEntity = null;			//which will hold the modelentity name to creat this bean
 	modelEntity = (ModelEntity)cpfScreen.getBaseEntity();
 	ModelEntity baseEntity = cpfScreen.getPortletRef().getBaseEntity();	//which will hold the base entity name while creting this bean
 	LOG.info("Base Entity found for this Web Service is : " + baseEntity.getName());
 	List<CPFAttribute> selectedAttributes = null;	//whihc will hold the selected attributes for this model entity					
 	int operationId = cpfScreen.getPortletRef().getPortletId();		//Holds Operation Id of the method
 	LOG.info("Web Service is generating for the Portlet Id : " + operationId);
	Map<RelationKey, List<CPFAttribute>> nestedAttributes = cpfScreen.getNestedAttributes();
	Map<CPFAttribute, String> attributeSetters = new HashMap<CPFAttribute, String>();		//Holds Setter function names
	Map<CPFAttribute, String> attributeGetters = new HashMap<CPFAttribute, String>();       //Holds Getter function names
	Map<RelationKey, String> otherEntitiesSetters = new HashMap<RelationKey, String>();		//Holds Setter function names for dependent Entities
	Map<RelationKey, String> otherEntitiesGetters = new HashMap<RelationKey, String>();		//Holds Getter function names for dependent Entities
	Map<CPFAttribute, String> attributeDataType = new HashMap<CPFAttribute, String>();		//Holds selected attributes datatype
	Map<RelationKey, String> otherEntitiesDataType = new HashMap<RelationKey, String>(); 	//Holds dependent entities datatype (like collection or not)
	Map<CPFAttribute, String> attributeVariableName = new HashMap<CPFAttribute, String>();	//Holds selected attributes variable name through out this bean
	Map<RelationKey, String> otherEntitiesVariableName = new HashMap<RelationKey, String>(); //Holds denpendent entities variable name through out this bean
	List<String> declarations =  new ArrayList<String> ();
	List<String> imports = new ArrayList<String>();
	imports.add("import java.util.*");
	//imports.add("import com.genband.m5.maps.ide.model.util.ModelUtil");

    
		//Doing process for import packages as well as assigning selected attributes depending upon Base entity or dependednt entities
 	if (resourceName.equals(baseEntity.getName()))     //comparing resourcename with modelBean name to Create bean class for baseEntity
 	{
 		LOG.info("This resource is the baseEntity resource");
 		imports.add("import " + modelEntity.getCanonicalTypeName ());		//Importing Base entity EJB into this java bean
			//For importing packages of EJBs into this java bean
		if (nestedAttributes != null) {
			for (Iterator<RelationKey> itr = nestedAttributes.keySet().iterator(); itr.hasNext();)
			{
				RelationKey relationKey = itr.next();
				imports.add("import " + relationKey.getReferencedEntity().getCanonicalTypeName ());		//Improting dependent EJBs for this base java bean
			} 
		} 
		
		//getting selected attributes for Base entity
		selectedAttributes = (List<CPFAttribute>)cpfScreen.getSelectedAttributes();
	}
	else
	{
		LOG.info("This resource is not a Base entity.. Infact it is a Dependent Entity..");
		Iterator<RelationKey> keys = nestedAttributes.keySet().iterator();
		for (; keys.hasNext(); )
		{
			RelationKey relationKey = keys.next();
			modelEntity = relationKey.getReferencedEntity();
			if(resourceName.equals(relationKey.getRelationShipInfo().getPropertyName()))         //Comparing with the related entities to create bean class if it is the resource name
			{
					//getting selected attributes for Dependent Entity
				selectedAttributes = nestedAttributes.get(relationKey);   
				break;
			}
		}
		if(!imports.contains("import " + modelEntity.getCanonicalTypeName ())) {
			imports.add("import " + modelEntity.getCanonicalTypeName ());  //Importing this specific EJB into this java bean
		}
	}
	if (selectedAttributes == null)     //If Attributes are not selected from that model entity
	{
		LOG.info ("Model Entity "+modelEntity + " deos not exist");
		System.exit(1);
	}
	
	//Imports for Related Strong Entities here....
	for (Iterator<CPFAttribute> itr = selectedAttributes.iterator (); itr.hasNext ();) {
		CPFAttribute cpf = itr.next ();
		if (cpf.getForeignColumn () != null) {
			imports.add("import " + cpf.getModelAttrib ().getForeignEntity ().getCanonicalTypeName ());		//Importing strong entities EJBs into this Java Bean
		}
	}
	
	//Holds the name of this Java Bean
	String className = null;
	if(resourceName.equals(baseEntity.getName())) {
		className = modelEntity.getName()+"_"+operationId;
	} else {
		String temp = resourceName;
		temp = temp.toUpperCase().charAt(0) + temp.substring(1);
		className = temp + "_"+operationId;
	}   	
	LOG.info("The Java Bean name is : " + className);

     
			//Doing Process for Variable declaration here..................................................
	LOG.info("Doing Process for " + modelEntity.getName() + " entity selected attributes...");
	for(Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext(); ) { 
 		CPFAttribute selectedAttribute = (CPFAttribute)attributeItr.next();    //For each selected attribute
 		ModelAttribute modelAttrib = null;
 		String type = null;
		modelAttrib = selectedAttribute.getModelAttrib();
 		if(selectedAttribute.getForeignColumn () == null) {
			type = new String(modelAttrib.getCanonicalTypeName());      //retrieving Attribute data type 
		} else {
			LOG.info("Selected Attribtue " + selectedAttribute.getName() + " is a Foreign Key.");
			RelationShipInfo relationShipInfo = selectedAttribute.getModelAttrib().getRelType();
			LOG.info("RelationShip from its parent is : " + relationShipInfo.getMapping());
			type = relationShipInfo.getSimpleTypeInfo ();
			if(type.contains("<")) {
				//type = type.substring(0, type.lastIndexOf("<")+1) + "Long>";
				type = "java.lang.Long[]";
			} else {
				type = "java.lang.Long";
			}
		}
		
		if (type.equals ("java.sql.Date") || type.equals ("java.sql.Time") || type.equals ("java.sql.Timestamp")) {
			LOG.info("Date Data Type found for this Attribute..");
			type = new String ("java.util.Date");
		} else if (type.equals ("java.sql.Blob")) {
			type = new String ("byte[]");
		} else if (type.equals ("java.sql.Clob")) {
			type= new String ("char[]");
		}
		declarations.add("private " + type + " " + modelAttrib.getName());  //adding declaration
		attributeDataType.put(selectedAttribute, type);						//adding datatype
		attributeVariableName.put(selectedAttribute, modelAttrib.getName());
		String temp = Character.toUpperCase(modelAttrib.getName().charAt(0))+modelAttrib.getName().substring(1);
		String setter = "set" + temp;
		String getter = "get" + temp;
  		attributeSetters.put(selectedAttribute, setter);
		attributeGetters.put(selectedAttribute, getter);	
	}			
			//Generating Varible declarations for Dependent Entities here (Only in case of generating bean for Base Entity)........
	Map<RelationKey, String> otherEntitiesSign = new HashMap<RelationKey, String>();			//Holds return type signature for dependent entities
	Map<RelationKey, String> otherEntitesProperty = new HashMap<RelationKey, String>();			//Holds Property Name of Dependent entities in Base Entity
		
	if (baseEntity.getName().equals(resourceName) && nestedAttributes != null) {
		for (Iterator<RelationKey> itr = nestedAttributes.keySet().iterator(); itr.hasNext(); ) {
			RelationKey relationKey = itr.next();
			RelationShipInfo relationShipInfo = relationKey.getRelationShipInfo();
			LOG.info("Doing Process for " + relationKey.getReferencedEntity() + " entity selected attributes "
						+ "with property name " + relationShipInfo.getPropertyName() + "...");
			String signature = relationShipInfo.getSimpleTypeInfo();
			String temp = relationShipInfo.getPropertyName();	//Added this line
			temp = temp.toUpperCase().charAt(0) + temp.substring(1);	//Added this line
			if(signature.lastIndexOf(">") > 0){
				signature = signature.substring (0, signature.lastIndexOf("<") + 1)
								+ temp + "_"	+	operationId + ">";
			}
			else {
				signature = temp + "_" + operationId;
			}
			otherEntitiesDataType.put(relationKey, signature);
			otherEntitiesSign.put(relationKey, signature);
				//May not required has to check once redundant info
			otherEntitesProperty.put(relationKey, relationShipInfo.getPropertyName());
			String getter = "get" + temp;
			String setter = "set" + temp;
			otherEntitiesGetters.put(relationKey, getter);
			otherEntitiesSetters.put(relationKey, setter);
			String tempName = relationKey.getRelationShipInfo().getPropertyName();
			otherEntitiesVariableName.put(relationKey, tempName);
			declarations.add("private " + signature + " " + otherEntitiesVariableName.get(relationKey));
		}
	}			//End of doing process for Variable declaration here.............................

     
	//Bean Generation Started Here 

    stringBuffer.append(TEXT_1);
    
		//Importing packages here.............................
	LOG.info("Adding Import statements to the class");
	for(Iterator<String> itrImports = imports.iterator(); itrImports.hasNext();) {

    stringBuffer.append(TEXT_2);
    stringBuffer.append( itrImports.next() );
    stringBuffer.append(TEXT_3);
    
	}   //End of Importing packages..............
	LOG.info("class name added...");

    stringBuffer.append(TEXT_4);
    stringBuffer.append( modelEntity.getName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_6);
    
		//variables declaration goes here....
	LOG.info("Variables declaration started...");
	for(Iterator<String> itrDeclarations = declarations.iterator(); itrDeclarations.hasNext();) {

    stringBuffer.append(TEXT_7);
    stringBuffer.append( itrDeclarations.next() );
    stringBuffer.append(TEXT_3);
    
	}   //End of variables declaration........
	LOG.info("Variables declaration ended and default constructor added...");

    stringBuffer.append(TEXT_8);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_9);
    			
	LOG.info("Defining tbe Getters...");			
			//Generating Getter functions here....................................................
	for(Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext(); ) {
		CPFAttribute selectedAttribute = (CPFAttribute)attributeItr.next();    //For each selected attribute
		

    stringBuffer.append(TEXT_10);
    stringBuffer.append( attributeDataType.get(selectedAttribute) );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( attributeGetters.get(selectedAttribute) );
    stringBuffer.append(TEXT_12);
    stringBuffer.append( attributeVariableName.get(selectedAttribute) );
    stringBuffer.append(TEXT_13);
    	
	} 		//In case of Base Entity only this will call
	if (baseEntity.getName().equals(resourceName) && nestedAttributes != null) {
		for (Iterator<RelationKey> itr = nestedAttributes.keySet().iterator(); itr.hasNext(); ) {
			RelationKey relationKey = itr.next();

    stringBuffer.append(TEXT_14);
    stringBuffer.append( otherEntitiesSign.get(relationKey) );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( otherEntitiesGetters.get(relationKey) );
    stringBuffer.append(TEXT_15);
    stringBuffer.append( otherEntitiesVariableName.get(relationKey) );
    stringBuffer.append(TEXT_16);
    
		}
	}
	
 		//Generating Setter functions here.....................................................
 	LOG.info("Defining the setters....");
 	for(Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext(); ) { 
  		CPFAttribute selectedAttribute = (CPFAttribute)attributeItr.next();              //For each selected attribute

    stringBuffer.append(TEXT_17);
    stringBuffer.append( attributeSetters.get(selectedAttribute) );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( attributeDataType.get(selectedAttribute) );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( attributeVariableName.get(selectedAttribute) );
    stringBuffer.append(TEXT_19);
    stringBuffer.append( attributeVariableName.get(selectedAttribute) );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( attributeVariableName.get(selectedAttribute) );
    stringBuffer.append(TEXT_16);
     
 	}
 	if (baseEntity.getName().equals(resourceName) && nestedAttributes != null) {
		for (Iterator<RelationKey> itr = nestedAttributes.keySet().iterator(); itr.hasNext(); ) {
			RelationKey relationKey = itr.next();

    stringBuffer.append(TEXT_21);
    stringBuffer.append( otherEntitiesSetters.get(relationKey) );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( otherEntitiesSign.get(relationKey) );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( otherEntitiesVariableName.get(relationKey) );
    stringBuffer.append(TEXT_22);
    stringBuffer.append( otherEntitiesVariableName.get(relationKey) );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( otherEntitiesVariableName.get(relationKey) );
    stringBuffer.append(TEXT_16);
    
		} 
	}
	
			// Generating Constructor which will take base Entity as argument and set the values for this java bean.......
		LOG.info("Generating Constructor with signature " + modelEntity.getName());	

    stringBuffer.append(TEXT_14);
    stringBuffer.append( className );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( modelEntity.getName() );
    stringBuffer.append(TEXT_23);
    
			//For CPFAtributes setting Bean values
		for (Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext();) {
			CPFAttribute itrCPFAttribute = attributeItr.next();
		if (itrCPFAttribute.getForeignColumn () == null) {
			String setArgument = null;
			if (attributeDataType.get(itrCPFAttribute).equals ("java.sql.Blob")) {
				setArgument = new String ("ModelUtil.mapBlob2ByteArray (Obj." + attributeGetters.get(itrCPFAttribute) + "())");
			} else if (attributeDataType.get(itrCPFAttribute).equals ("java.sql.Clob")) {
				setArgument = new String ("ModelUtil.mapClob2CharArray (Obj." + attributeGetters.get(itrCPFAttribute) + "())");
			}else {
				setArgument = new String ("Obj." + attributeGetters.get(itrCPFAttribute) + "()");
			}

    stringBuffer.append(TEXT_24);
    stringBuffer.append( attributeSetters.get(itrCPFAttribute) );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( setArgument );
    stringBuffer.append(TEXT_25);
    
		} else {
			ModelEntity fkModelEntity = itrCPFAttribute.getForeignColumn ().getEntity ();
			RelationShipInfo relationShipInfo = itrCPFAttribute.getModelAttrib().getRelType();
			String signature = relationShipInfo.getSimpleTypeInfo();
			if (signature.lastIndexOf (">") > 0) {
				String fkPrimayKeyName = fkModelEntity.getPrimaryKey();
				fkPrimayKeyName = fkPrimayKeyName.toUpperCase().charAt(0) + fkPrimayKeyName.substring(1);

    stringBuffer.append(TEXT_26);
    stringBuffer.append( attributeGetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_27);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( attributeGetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_29);
    stringBuffer.append( fkModelEntity.getName() );
    stringBuffer.append(TEXT_30);
    stringBuffer.append( attributeGetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_31);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_32);
    stringBuffer.append( fkPrimayKeyName );
    stringBuffer.append(TEXT_33);
    stringBuffer.append( attributeSetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_34);
    
			} else {
				String getter = fkModelEntity.getPrimaryKey();
				getter = getter.toUpperCase ().charAt (0) + getter.substring (1);

    stringBuffer.append(TEXT_26);
    stringBuffer.append( attributeGetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_35);
    stringBuffer.append( attributeSetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_36);
    stringBuffer.append( attributeGetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_37);
    stringBuffer.append( getter );
    stringBuffer.append(TEXT_38);
    
			}

    
		}

    
		}

    
			////For OtherSelectedEntities setting Bean values
		if (baseEntity.getName ().equals (resourceName) && nestedAttributes != null) {
			LOG.info("For dependenti Entities setting values");
		for (Iterator<RelationKey> itr = nestedAttributes.keySet().iterator (); itr.hasNext (); ) {
			RelationKey relationKey = itr.next();
			ModelEntity itrModelEntity = relationKey.getReferencedEntity();
			String getter = otherEntitesProperty.get (relationKey);
			getter = getter.toUpperCase ().charAt (0)+getter.substring (1);
			String genericType = otherEntitiesSign.get (relationKey);
			if (genericType.lastIndexOf ("<") > 0) {
				genericType = genericType.substring (genericType.lastIndexOf ("<")+1, genericType.lastIndexOf (">"));
			}
		if (otherEntitiesSign.get(relationKey).lastIndexOf(">") > 0) {

    stringBuffer.append(TEXT_39);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_41);
    stringBuffer.append( getter );
    stringBuffer.append(TEXT_42);
    
			if (otherEntitiesSign.get(relationKey).substring (0, otherEntitiesSign.get(relationKey).lastIndexOf("<")).equals ("Set")) {		

    stringBuffer.append(TEXT_24);
    stringBuffer.append( otherEntitiesSign.get(relationKey) );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_43);
    stringBuffer.append( genericType );
    stringBuffer.append(TEXT_44);
    
			} else {

    stringBuffer.append(TEXT_24);
    stringBuffer.append( otherEntitiesSign.get(relationKey) );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_45);
    stringBuffer.append( genericType );
    stringBuffer.append(TEXT_44);
    
			}

    stringBuffer.append(TEXT_24);
    stringBuffer.append( genericType );
    stringBuffer.append(TEXT_46);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_47);
    stringBuffer.append( genericType );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_48);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_49);
    
		} else {

    stringBuffer.append(TEXT_50);
    stringBuffer.append( getter );
    stringBuffer.append(TEXT_51);
    stringBuffer.append( otherEntitiesSign.get(relationKey) );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_52);
    stringBuffer.append( genericType );
    stringBuffer.append(TEXT_53);
    stringBuffer.append( getter );
    stringBuffer.append(TEXT_38);
    
		}

    stringBuffer.append(TEXT_24);
    stringBuffer.append( otherEntitiesSetters.get (relationKey) );
    stringBuffer.append(TEXT_54);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_25);
    
			if (!otherEntitiesSign.get(relationKey).contains("<")) {

    stringBuffer.append(TEXT_55);
    
			}
		}
		}

    stringBuffer.append(TEXT_56);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_57);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_58);
    stringBuffer.append( modelEntity.getName () );
    stringBuffer.append(TEXT_59);
    
		LOG.info("Generating merge function which will return the object : " + modelEntity.getName());
				//For CPFAtribtues setting modelEntity values
	    for (Iterator<CPFAttribute> attributeItr = selectedAttributes.iterator(); attributeItr.hasNext();) {
			CPFAttribute itrCPFAttribute = attributeItr.next();
		if (itrCPFAttribute.getForeignColumn () == null) {
			String cast = itrCPFAttribute.getModelAttrib ().getCanonicalTypeName ();
			String mergeArgument = null;
			if (cast.equals("java.sql.Date") || cast.equals("java.sql.Time") || cast.equals("java.sql.Timestamp")) {
				mergeArgument = new String("new " + cast + "(" + itrCPFAttribute.getModelAttrib ().getName () + ".getTime ())");

    stringBuffer.append(TEXT_60);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_61);
    
			} else if (cast.equals("java.sql.Blob")) {
				mergeArgument= new String ("ModelUtil.mapByteArray2Blob (" + itrCPFAttribute.getModelAttrib ().getName () + ")");
			} else if (cast.equals("java.sql.Clob")) {
				mergeArgument= new String ("ModelUtil.mapCharArray2Clob (" + itrCPFAttribute.getModelAttrib ().getName () + ")");
			}else {
				//Edited on 20th Feb...
				mergeArgument = attributeVariableName.get(itrCPFAttribute);
			}

    stringBuffer.append(TEXT_62);
    stringBuffer.append( attributeSetters.get(itrCPFAttribute) );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( mergeArgument );
    stringBuffer.append(TEXT_25);
    
		} else {
			ModelEntity fkModelEntity = itrCPFAttribute.getForeignColumn ().getEntity ();
			RelationShipInfo relationShipInfo = itrCPFAttribute.getModelAttrib().getRelType();
			RelationShipInfo inverseRelationShipInfo = itrCPFAttribute.getModelAttrib().getInverseRelType();
			String inversePropertyName = null;
			if(inverseRelationShipInfo != null) {
				if(relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToOne) 
					|| relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToMany)) {
					inversePropertyName = new String(inverseRelationShipInfo.getPropertyName());
					inversePropertyName = inversePropertyName.toUpperCase().charAt(0) 
											+ inversePropertyName.substring(1);
				}
			} else {
				LOG.info("Did not get inverse Relation ship between parent and child for Strong relation so exiting..");
				//System.exit(1);
			}
			String signature = relationShipInfo.getSimpleTypeInfo();
			if (signature.lastIndexOf (">") > 0) {	//In case of Many relationship between parent and child entities
				String fkPrimayKeyName = fkModelEntity.getPrimaryKey();
				fkPrimayKeyName = fkPrimayKeyName.toUpperCase().charAt(0) + fkPrimayKeyName.substring(1);
				String varName = itrCPFAttribute.getModelAttrib ().getName () + "List";

    stringBuffer.append(TEXT_63);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_64);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_65);
    
				if (signature.substring (0, signature.lastIndexOf("<")).equals ("Set")) {

    stringBuffer.append(TEXT_66);
    stringBuffer.append( signature );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_43);
    stringBuffer.append( fkModelEntity.getName () );
    stringBuffer.append(TEXT_44);
    
				} else {

    stringBuffer.append(TEXT_66);
    stringBuffer.append( signature );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_45);
    stringBuffer.append( fkModelEntity.getName () );
    stringBuffer.append(TEXT_44);
    
				}

    stringBuffer.append(TEXT_67);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_68);
    stringBuffer.append( fkModelEntity.getName () );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_52);
    stringBuffer.append( fkModelEntity.getName () );
    stringBuffer.append(TEXT_69);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_70);
    stringBuffer.append( fkPrimayKeyName );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_72);
    
							//Added on 6th Mar,08...
						if (relationShipInfo.getMapping().equals(CPFConstants.RelationshipType.OneToMany)) {

    stringBuffer.append(TEXT_73);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_70);
    stringBuffer.append( inversePropertyName );
    stringBuffer.append(TEXT_74);
    
						}

    stringBuffer.append(TEXT_75);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_76);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_77);
    stringBuffer.append( attributeSetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_78);
    
			} else {
				String setter = fkModelEntity.getPrimaryKey();
				setter = setter.toUpperCase().charAt(0)+setter.substring (1);

    stringBuffer.append(TEXT_79);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_80);
    stringBuffer.append( signature  );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_52);
    stringBuffer.append( signature  );
    stringBuffer.append(TEXT_81);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_70);
    stringBuffer.append( setter );
    stringBuffer.append(TEXT_71);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_25);
    
				if(inversePropertyName != null) {

    stringBuffer.append(TEXT_24);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_70);
    stringBuffer.append( inversePropertyName );
    stringBuffer.append(TEXT_82);
    
				}

    stringBuffer.append(TEXT_62);
    stringBuffer.append( attributeSetters.get (itrCPFAttribute) );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( itrCPFAttribute.getModelAttrib ().getName () );
    stringBuffer.append(TEXT_83);
    
			}

    
		}

    
		}

    stringBuffer.append(TEXT_84);
    
			////For OtherSelectedEntities setting modelEntity values
		if (baseEntity.getName ().equals (resourceName) && nestedAttributes != null) {
			LOG.info("For Dependent entities...");
		for (Iterator<RelationKey> itr = nestedAttributes.keySet().iterator (); itr.hasNext (); ) {
			RelationKey relationKey = itr.next();
			RelationShipInfo inverseRelationShipInfo = relationKey.getInverseRelationShipInfo();
			String inversePropertyName = null;
			
			ModelEntity itrModelEntity = relationKey.getReferencedEntity();
			String setter = otherEntitesProperty.get (relationKey);
			setter = setter.toUpperCase ().charAt (0)+setter.substring (1);
			String genericType = otherEntitiesSign.get (relationKey);
			String varName = otherEntitiesVariableName.get(relationKey);
			String ifCondition = new String (varName + "!= null");
			if (genericType.lastIndexOf ("<") > 0) {
				genericType = genericType.substring (genericType.lastIndexOf ("<")+1, genericType.lastIndexOf (">"));
				ifCondition = ifCondition.concat(" && " + varName + ".size() > 0");
			}
			if(inverseRelationShipInfo != null) {
				if(relationKey.getRelationShipInfo().getMapping().equals(CPFConstants.RelationshipType.OneToOne) 
					|| relationKey.getRelationShipInfo().getMapping().equals(CPFConstants.RelationshipType.OneToMany)) {
				inversePropertyName = new String(inverseRelationShipInfo.getPropertyName());
				inversePropertyName = inversePropertyName.toUpperCase().charAt(0) 
										+ inversePropertyName.substring(1);
				}
			}

    stringBuffer.append(TEXT_60);
    stringBuffer.append( ifCondition );
    stringBuffer.append(TEXT_85);
    
		if (otherEntitiesSign.get(relationKey).lastIndexOf(">") > 0) {	

			if (otherEntitiesSign.get(relationKey).substring (0, otherEntitiesSign.get(relationKey).lastIndexOf("<")).equals ("Set")) { 

    stringBuffer.append(TEXT_24);
    stringBuffer.append( otherEntitiesSign.get(relationKey).substring(0,otherEntitiesSign.get(relationKey).lastIndexOf("<") ) );
    stringBuffer.append(TEXT_86);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_87);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_44);
    
			} else {

    stringBuffer.append(TEXT_24);
    stringBuffer.append( otherEntitiesSign.get(relationKey).substring(0,otherEntitiesSign.get(relationKey).lastIndexOf("<") ) );
    stringBuffer.append(TEXT_86);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_40);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_88);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_44);
    
			}

    stringBuffer.append(TEXT_39);
    stringBuffer.append( genericType  );
    stringBuffer.append(TEXT_40);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_89);
    stringBuffer.append( varName );
    stringBuffer.append(TEXT_90);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_91);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_92);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_93);
    stringBuffer.append( inversePropertyName );
    stringBuffer.append(TEXT_94);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_95);
    stringBuffer.append( setter );
    stringBuffer.append(TEXT_54);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_96);
    
		} else {

    stringBuffer.append(TEXT_24);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_52);
    stringBuffer.append( itrModelEntity.getName() );
    stringBuffer.append(TEXT_81);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_97);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_98);
    
			if(inversePropertyName != null) {

    stringBuffer.append(TEXT_99);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_70);
    stringBuffer.append( inversePropertyName );
    stringBuffer.append(TEXT_82);
    
			}

    stringBuffer.append(TEXT_100);
    stringBuffer.append( setter );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( otherEntitesProperty.get (relationKey) );
    stringBuffer.append(TEXT_25);
    
		}

    stringBuffer.append(TEXT_55);
    
		}
		}

    stringBuffer.append(TEXT_101);
    
	LOG.info("Java Bean Generation completed from template Side...");
	//Bean Generation Ended Here...

    return stringBuffer.toString();
  }
}
