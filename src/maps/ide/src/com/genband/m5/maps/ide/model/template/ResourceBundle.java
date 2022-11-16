package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.ide.model.*;
import java.util.*;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.common.CPFConstants;
import java.util.List;

public class ResourceBundle
{
  protected static String nl;
  public static synchronized ResourceBundle create(String lineSeparator)
  {
    nl = lineSeparator;
    ResourceBundle result = new ResourceBundle();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "# Start of labels declaration for ";
  protected final String TEXT_3 = ".xhtml generated at ";
  protected final String TEXT_4 = " ";
  protected final String TEXT_5 = NL;
  protected final String TEXT_6 = "=";
  protected final String TEXT_7 = NL + "# End of labels declaration for ";
  protected final String TEXT_8 = ".xhtml ";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CPFPlugin LOG = CPFPlugin.getDefault();
	CPFScreen cpfScreen = (CPFScreen)argument;
	/*int x = ;*/
	
	if(cpfScreen != null) {
		LOG.info("Adding resource labels related to CPFScren : ");
		List<String> resourceLabels = new ArrayList<String>();
		List<CPFAttribute> selectedAttributes = new ArrayList<CPFAttribute>();
		List<String> resourceLabelsLeft = new ArrayList<String>();
		List<String> middleName = new ArrayList<String>();
		
		LOG.info("Adding base attributes and its middle name");
		for (CPFAttribute cpfAttr : cpfScreen.getSelectedAttributes()) {
			selectedAttributes.add(cpfAttr);
			middleName.add(cpfScreen.getBaseEntity().getName());
		}
			LOG.info("End of Adding base attributes and its middle name");
		
			//Adding related entities selected attributes to the main list if exists....
			if(cpfScreen.getNestedAttributes() != null) {
			Iterator<RelationKey> itrRelationKey = cpfScreen.getNestedAttributes().keySet().iterator();
			LOG.info("Adding attribteus for " + itrRelationKey.toString());
	
			while(itrRelationKey.hasNext()) {
				//Adding all related entity selected attributes to the main list
				RelationKey tempRK = itrRelationKey.next();
				selectedAttributes.addAll(cpfScreen.getNestedAttributes().get(tempRK));
				
				for(int i = 0; i < cpfScreen.getNestedAttributes().get(tempRK).size(); i++) {
					middleName.add(tempRK.getRelationShipInfo().getPropertyName());
					LOG.info("Middle name is : "+tempRK.getRelationShipInfo().getPropertyName());
				}
				
				if(!tempRK.getReferencedEntity().isWeakEntity ()
					&& (tempRK.getRelationShipInfo().getMapping().equals(CPFConstants.RelationshipType.OneToMany)
					|| tempRK.getRelationShipInfo().getMapping().equals(CPFConstants.RelationshipType.ManyToMany))) {
					resourceLabels.add(tempRK.getRelationShipInfo().getPropertyName());
					resourceLabelsLeft.add(tempRK.getRelationShipInfo().getPropertyName());
				}
			}
		}
			//End of adding related entities selected attribtues to the main list
			
			//getting labels by iterating over CpfAttributes.....
			Iterator<CPFAttribute> itrSelectedAttributes = selectedAttributes.iterator();
			Iterator<String> itrMiddle = middleName.iterator();
			LOG.info("Type is : " + cpfScreen.getViewType());
			while(itrSelectedAttributes.hasNext()) {
			String temp = itrSelectedAttributes.next().getLabel();
			resourceLabels.add(temp);
			temp = temp.replaceAll(" ", "_");
			if(cpfScreen.getViewType().equals(CPFConstants.ViewType.LIST)) {
				resourceLabelsLeft.add("L" + cpfScreen.getPortletRef().getPortletId() + "_"
										+ itrMiddle.next() + "_" + temp);
			} else {
				resourceLabelsLeft.add("D" + cpfScreen.getPortletRef().getPortletId() + "_"
										+ itrMiddle.next() + "_" + temp);
			}
		}

    stringBuffer.append(TEXT_2);
    stringBuffer.append( cpfScreen.getJspName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( new Date(System.currentTimeMillis()) );
    stringBuffer.append(TEXT_4);
    			
		//Iterating over resource labels to generate resource properties
		Iterator<String> itrResourceLabels = resourceLabels.iterator();
		Iterator<String> itrRresourceLabelsLeft = resourceLabelsLeft.iterator(); 
		while(itrResourceLabels.hasNext()){
			String resourceLabel = itrResourceLabels.next();
			//String resourceLabelL = resourceLabel;	//This is for left side value
			//resourceLabelL = resourceLabelL.replaceAll(" ", "_");
			String resourceLabelL = itrRresourceLabelsLeft.next();
			resourceLabel = resourceLabel.toUpperCase().charAt(0) + resourceLabel.substring(1);
			LOG.info("Adding \"" + resourceLabel + "\" to the bundle");
	        //TODO rite now only first character is Capital... have to Capitalize left side lable.

    stringBuffer.append(TEXT_5);
    stringBuffer.append( resourceLabelL );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( resourceLabel );
    
		}		//End of while loop i.e iterating over list of selected attributes

    stringBuffer.append(TEXT_7);
    stringBuffer.append( cpfScreen.getJspName() );
    stringBuffer.append(TEXT_8);
    
		LOG.info("Resource Bundle generation finished from template side....");
	}  //End of If 

    stringBuffer.append(TEXT_5);
    stringBuffer.append(TEXT_5);
    return stringBuffer.toString();
  }
}
