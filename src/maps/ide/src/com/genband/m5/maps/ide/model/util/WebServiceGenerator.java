package com.genband.m5.maps.ide.model.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFConstants.OperationType;
import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.Util;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFResource;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.ModelAttribute;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.model.RelationKey;
import com.genband.m5.maps.ide.model.template.CreateJava;
import com.genband.m5.maps.ide.model.template.CreateWS;
import com.genband.m5.maps.ide.model.template.CreateWSInterface;
import com.genband.m5.maps.ide.model.template.ReturnBean;

public class WebServiceGenerator extends CodeGenerator {

	private CPFResource m_cpfResource;

	private ResourceInfo m_resourceInfo;

	private ModelUtil modelUtil;

	private boolean m_one2Many = false; // assuming that list is for One2One
	// only

	private Map<RelationKey, List<CPFAttribute>> m_naryRelation = null;

	private boolean m_primaryKeyExists = false; // assuming primarykey does not
	// exists in the list of
	// selected attributes..

	private boolean m_nestedPrimaryKey;

	private CPFAttribute m_baseAttribute = null;

	private CPFAttribute m_nestedAttribute;

	private List<CPFAttribute> detailsSelectedAttbs;

	private static CPFPlugin LOG = CPFPlugin.getDefault();

	/**
	 * 
	 * Updating web.xml by adding new web service as servlet
	 * 
	 * @param cpfScreen
	 *            for which web service has been created..
	 * 
	 * @param webXmlFile
	 *            file name to be modified
	 * 
	 * @throws Exception
	 * 
	 */

	private static void writeWebXml(CPFScreen cpfScreen, File webXmlFile)
	throws Exception {

		FileReader reader = null;
		boolean serviceAlreadyExists = false;
		boolean mappingAlreadyExists = false;

		LOG.info("Updating webxml file: " + webXmlFile);

		try {

			reader = new FileReader(webXmlFile);

			DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory
			.newInstance();

			DocumentBuilder docBulider = docBuilderFac.newDocumentBuilder();

			Document doc = docBulider.parse(new InputSource(reader));

			// Node root = doc.getDocumentElement();

			NodeList r = doc.getElementsByTagName("web-app");

			Element root = (Element) r.item(0);

			LOG.info("RootElement is : " + root.getNodeName().toString());

			// reeta added
			NodeList nodeList = root.getElementsByTagName("servlet");

			if (nodeList != null) {
				for (int i = 0; i < nodeList.getLength(); i++) {

					Node servletNode = nodeList.item(i);
					if (servletNode != null) {
						NodeList children = servletNode.getChildNodes();
						for (int j = 0; j < children.getLength(); j++) {
							Node node = children.item(j);
							if (node != null) {
								String name = node.getNodeName();
								CPFPlugin.getDefault().log(
										"The Servlet found is "
										+ node.getTextContent());

								if (name.equals("servlet-name")) {
									if (node.getTextContent().equals(
											cpfScreen.getWebServiceInfo()
											.getWebServiceName())) {
										serviceAlreadyExists = true;
										CPFPlugin
										.getDefault()
										.log(
												"WEb Service alredy exits so removing this eelement from the web.xml");
										root.removeChild(servletNode);
										break;
									}
									break; // servlet-name chid found for this
									// servlet node
								}
							}
						}

					}
					if (serviceAlreadyExists) {
						break;
					}
				}

			}

			// reeta added
			nodeList = root.getElementsByTagName("servlet-mapping");

			if (nodeList != null) {
				for (int i = 0; i < nodeList.getLength(); i++) {

					Node servletNode = nodeList.item(i);
					if (servletNode != null) {
						NodeList children = servletNode.getChildNodes();
						for (int j = 0; j < children.getLength(); j++) {
							Node node = children.item(j);
							if (node != null) {
								String name = node.getNodeName();
								CPFPlugin.getDefault().log(
										"The Servlet Mapping found is "
										+ node.getTextContent());
								if (name.equals("servlet-name")) {
									if (node.getTextContent().equals(
											cpfScreen.getWebServiceInfo()
											.getWebServiceName())) {
										mappingAlreadyExists = true;
										CPFPlugin
										.getDefault()
										.log(
												"WEb Service alredy exits so removing this eelement from the web.xml");
										root.removeChild(servletNode);
										break;
									}
									break; // servlet-name chid found for this
									// servlet node
								}
							}
						}
					}
					if (mappingAlreadyExists) {
						break;
					}
				}

			}

			Element child1 = doc.createElement("servlet");

			Element subChild1 = doc.createElement("servlet-name");

			subChild1.setTextContent(cpfScreen.getWebServiceInfo()
					.getWebServiceName());
			child1.appendChild(doc.createTextNode("\n"));
			child1.appendChild(subChild1);
			child1.appendChild(doc.createTextNode("\n"));

			Element subChild2 = doc.createElement("servlet-class");

			subChild2.setTextContent("com.genband.m5.maps.services."

					+ cpfScreen.getWebServiceInfo().getWebServiceName() + "Impl");

			child1.appendChild(subChild2);
			child1.appendChild(doc.createTextNode("\n"));

			Element subChild3 = doc.createElement("load-on-startup");

			subChild3.setTextContent("1");

			child1.appendChild(subChild3);
			child1.appendChild(doc.createTextNode("\n"));

			root.appendChild(doc.createTextNode("\n"));
			root.appendChild(child1);
			root.appendChild(doc.createTextNode("\n"));

			Element child2 = doc.createElement("servlet-mapping");

			subChild1 = doc.createElement("servlet-name");

			subChild1.setTextContent(cpfScreen.getWebServiceInfo()
					.getWebServiceName());

			child2.appendChild(doc.createTextNode("\n"));
			child2.appendChild(subChild1);
			child2.appendChild(doc.createTextNode("\n"));

			subChild2 = doc.createElement("url-pattern");

			String temp = cpfScreen.getWebServiceInfo().getTargetNamespace();

			temp = temp.substring(6, temp.lastIndexOf("/"));

			subChild2.setTextContent(temp);

			child2.appendChild(subChild2);
			child2.appendChild(doc.createTextNode("\n"));

			root.appendChild(doc.createTextNode("\n"));
			root.appendChild(child2);
			root.appendChild(doc.createTextNode("\n"));

			Source source = new DOMSource(doc);

			Result result = new StreamResult(webXmlFile);

			Transformer xformer = TransformerFactory.newInstance()

			.newTransformer();

			xformer.transform(source, result);

			LOG.info("Done updating webxml file: " + webXmlFile);

		} catch (Exception e) {

			LOG.error("Got exception dealing with file I/O and DOM", e);

			throw e;

		}

		finally {

			if (reader != null) {

				try {

					reader.close();

				} catch (IOException e) {

					LOG.error("Got exception while closing reader ...");

				}

			}

		}

	}

	/**
	 * 
	 * Generates the web services and related all beans and updates web.xml if
	 * existed
	 * 
	 * otherwise creates it and edit it for the Portlet passed as an argument to
	 * 
	 * this function.
	 * 
	 */

	@Override
	public IFile generateResource(CPFPortlet p_portlet) throws Exception {

		CPFScreen cpfScreen = null;

		// Code generation starts here

		String projectName = p_portlet.getCurrentProject();

		IFolder sourceFodler = ResourcesPlugin.getWorkspace().getRoot()

		.getProject(projectName).getFolder("src");

		createPackage(sourceFodler);

		try {

			/**
			 * 
			 * Generating web Service for List Screen if supported....
			 * 
			 */

			if (p_portlet.getListScreen() != null

					&& p_portlet.getListScreen().getWebServiceInfo()
					.getWebMethodsMap() != null

					&& p_portlet.getListScreen().getWebServiceInfo()
					.getWebMethodsMap().size() > 0) {

				LOG.info("Started Generating Web Service for Listing Screen");

				this.generateService(p_portlet.getListScreen());

				cpfScreen = p_portlet.getListScreen();

				LOG
				.info("Generating Web Service for Listing Screen is finished");

			} else {

				LOG.info("For list screen Web service is not supported....");

			}

		} finally {

			// Adding list web method in case of removed at the time of service
			// geenration

			LOG.info("Reverting changes done to list screen started....");

			if (m_one2Many && m_naryRelation.size() > 0) {

				cpfScreen.getNestedAttributes().putAll(m_naryRelation);

			}

			LOG.info("Changes have been reverted to list screen...");

		}

		try {

			/**
			 * 
			 * Generating web Service for Details Screen if supported....
			 * 
			 */

			if (p_portlet.getDetailsScreen() != null

					&& p_portlet.getDetailsScreen().getWebServiceInfo()
					.getWebMethodsMap() != null

					&& p_portlet.getDetailsScreen().getWebServiceInfo()
					.getWebMethodsMap().size() > 0) {

				LOG.info("Started Generating Web Service for Details Screen");

				removeGroupBars(p_portlet.getDetailsScreen());

				this.generateService(p_portlet.getDetailsScreen());

				cpfScreen = p_portlet.getDetailsScreen();

				LOG
				.info("Generating Web Service for Details Screen is finished");

			} else {

				LOG.info("For Details screen Web service is not supported....");

			}

		} finally {

			// Deleting primary key from selectedAttributes if added here
			// explicitly...

			if (m_primaryKeyExists) {

				List<CPFAttribute> selectedAttributes = p_portlet
				.getDetailsScreen().getSelectedAttributes();

				selectedAttributes.remove(m_baseAttribute);

			}

			// Adding original list selected Attribute to the cpfScreen list

			if (detailsSelectedAttbs != null

					&& this.detailsSelectedAttbs.size() > 0) {

				p_portlet.getDetailsScreen().setSelectedAttributes(
						this.detailsSelectedAttbs);

			}

		}

		/**
		 * 
		 * Creating or Updating web.xml in WebContent/WEB-INF......
		 * 
		 */

		if (cpfScreen != null) {

			String path = Util.getProjectPath(p_portlet.getCurrentProject());

			path = path.concat(destWebDescriptor);

			File webXmlFile = new File(path);

			// make sure web.xml is created with static content

			createWebXml(p_portlet, webXmlFile);

			if (!webXmlFile.exists()) {

				LOG.error("File does not exist ... " + webXmlFile);

				throw new Exception("File does not exist ... " + webXmlFile);

			}

			// file exists

			writeWebXml(cpfScreen, webXmlFile);

		}

		return null;

	}

	@Override
	public void generateResource(CPFScreen screenCapture, CPFListener listener,

			Object handback) throws Exception {

		// TODO Auto-generated method stub

		throw new UnsupportedOperationException(
		"Not implemented yet ... please come back later ;-)");

	}

	/**
	 * 
	 * This will Generate All java beans and web service Interface and its
	 * implementation
	 * 
	 * @param p_screenCapture
	 *            the input screen for which web service has to be generated
	 * 
	 */

	private void generateService(CPFScreen p_screenCapture) {

		String path = null; // Holds the absolute path of the generating files

		FileWriter file = null;

		String result = null; // HOlds the result string returned by JET
		// intermediate Java code

		modelUtil = ModelUtil.getInstance();

		// Check for existing of list and If it exists check for One2Many
		// relationShip

		Map<CPFConstants.OperationType, String> webMethodsMap = p_screenCapture
		.getWebServiceInfo().getWebMethodsMap();

		if (webMethodsMap.containsKey(CPFConstants.OperationType.LIST)) {

			List<RelationKey> tempRel = getNestedRelations(p_screenCapture);

			if (tempRel != null) {

				// Deleting N-ary relatinos from nestedAttributes so that can
				// continue with creating service

				// for List

				m_one2Many = true;

				m_naryRelation = new HashMap<RelationKey, List<CPFAttribute>>();

				for (RelationKey relationKey : tempRel) {

					m_naryRelation.put(relationKey, p_screenCapture
							.getNestedAttributes().get(relationKey));

					p_screenCapture.getNestedAttributes().remove(relationKey);

				}

				LOG
				.info("As OneToMany relations not Supported for listing deleting nested relations from the list");

			}

		}

		// End of Checking

		// Code generation starts here

		LOG.info("Code Generation for Web Services Started....");

		if (p_screenCapture.getWebServiceInfo().getWebMethodsMap() != null &&

				p_screenCapture.getWebServiceInfo().getWebMethodsMap().size() > 0) {

			m_cpfResource = new CPFResource(p_screenCapture);

			LOG.info("Operation Id got is: " + m_cpfResource.getOperationId());

			// Generating WebService Interface (Java SEI)

			LOG.info("Generation of Web Service Interface Started....");

			CreateWSInterface createWsInt = new CreateWSInterface();

			result = createWsInt.generate(m_cpfResource);

			path = Util.getProjectPath(p_screenCapture.getPortletRef()
					.getCurrentProject());

			path = path + destServices
			+ p_screenCapture.getWebServiceInfo().getWebServiceName()
			+ ".java";

			LOG.info(path);

			try {

				file = new FileWriter(path);

				file.flush();

				file.write(result);

				// file.close();

			} catch (IOException e) {

				LOG.error("Got Exception while opening file...", e);

			} finally {

				if (file != null) {

					try {

						file.close();

					} catch (IOException e) {

						LOG.error("Got exception while closing reader ...", e);

					}

					file = null;

				}

			}

			LOG.info("Web Service Interface has been generated....");

			// End of generating webService interface

			// Generating WebService (SEI Implementation)

			LOG.info("Generation of web service implementation started......");

			CreateWS createWs = new CreateWS();

			result = createWs.generate(m_cpfResource);

			path = Util.getProjectPath(p_screenCapture.getPortletRef()
					.getCurrentProject());

			path = path + destServices
			+ p_screenCapture.getWebServiceInfo().getWebServiceName()
			+ "Impl.java";

			LOG.info(path);

			try {

				file = new FileWriter(path);

				file.flush();

				file.write(result);

				// file.close();

			} catch (IOException e) {

				e.printStackTrace();

			} finally {

				if (file != null) {

					try {

						file.close();

					} catch (IOException e) {

						LOG.error("Got exception while closing reader ...", e);

					}

					file = null;

				}

			}

			LOG.info("web service implementation has been generated......");

			// End of generating WebService

			LOG.info("Beans Generation for Web services started...");

			// Generating Java Bean of Web Service for return details of List

			if (webMethodsMap.containsKey(CPFConstants.OperationType.LIST)) {

				LOG.info("Return Bean Generation started here......");

				ReturnBean returnBean = new ReturnBean();

				result = returnBean.generate(m_cpfResource);

				path = Util.getProjectPath(p_screenCapture.getPortletRef()
						.getCurrentProject());

				path = path + destMessages
				+ p_screenCapture.getBaseEntity().getName()
				+ "Details_"

				+ p_screenCapture.getPortletRef().getPortletId()
				+ ".java";

				LOG.info(path);

				try {

					file = new FileWriter(path);

					file.flush();

					file.write(result);

					// file.close();

				} catch (IOException e) {

					e.printStackTrace();

				} finally {

					if (file != null) {

						try {

							file.close();

						} catch (IOException e) {

							LOG.error("Got exception while closing reader ...",
									e);

						}

						file = null;

					}

				}

				LOG.info("Return Bean has been generated......");

			}

			// End of Generating Java Bean of Web Service for return details of
			// List

			// Creating Java Bean of Web Service for Base Entity

			if (webMethodsMap.containsKey(OperationType.VIEW)) { // If it
				// supports
				// VIEW

				LOG.info("Base Entity Bean Generation started here......");

				// Adding primary key attribute if does not exist in the
				// selected attributes..

				if (!(isPrimaryKeyExists(p_screenCapture
						.getSelectedAttributes()))) {

					this.m_primaryKeyExists = true;

					List<CPFAttribute> selectedAttributes = p_screenCapture
					.getSelectedAttributes();

					CPFAttribute cpfAttribute = new CPFAttribute(modelUtil
							.getPrimaryKey(p_screenCapture.getBaseEntity()));

					selectedAttributes.add(cpfAttribute);

					this.m_baseAttribute = cpfAttribute;

				}

				m_resourceInfo = new ResourceInfo();

				m_resourceInfo.setResourceName(p_screenCapture.getBaseEntity()
						.getName());

				m_cpfResource.setResourceInfo(m_resourceInfo);

				CreateJava create = new CreateJava();

				result = create.generate(m_cpfResource);

				path = Util.getProjectPath(p_screenCapture.getPortletRef()
						.getCurrentProject());

				path = path + destMessages
				+ p_screenCapture.getBaseEntity().getName() + "_"

				+ p_screenCapture.getPortletRef().getPortletId()
				+ ".java";

				try {

					file = new FileWriter(path);

					file.flush();

					file.write(result);

					// file.close();

				} catch (IOException e) {

					e.printStackTrace();

				} finally {

					if (file != null) {

						try {

							file.close();

						} catch (IOException e) {

							LOG.error("Got exception while closing reader ...",
									e);

						}

						file = null;

					}

				}

				LOG.info("Base Entity Bean has been generated......");

				// End of Creating Java Bean of Web Service for Base Entity

				// Generating Java Beans of Web Service for other Selected
				// Entities

				if (p_screenCapture.getNestedAttributes() != null) {

					LOG
					.info("Dependent Entitiy Beans Generation started here......");

					for (Iterator<RelationKey> itrOtherEntities = p_screenCapture
							.getNestedAttributes().keySet()

							.iterator(); itrOtherEntities.hasNext();) {

						m_nestedPrimaryKey = false;

						m_nestedAttribute = null;

						RelationKey relationKey = itrOtherEntities.next(); // Added
						// this
						// line

						LOG.info("Dependent Entitiy "
								+ relationKey.getRelationShipInfo()
								.getPropertyName()

								+ " Bean Generation started here......");

						try {

							ModelEntity otherEntity = relationKey
							.getReferencedEntity();

							List<CPFAttribute> otherEntityAttributes = p_screenCapture
							.getNestedAttributes()

							.get(relationKey);

							if (!(isPrimaryKeyExists(otherEntityAttributes))) {

								CPFAttribute cpfAttribute = new CPFAttribute(
										modelUtil.getPrimaryKey(otherEntity));

								otherEntityAttributes.add(cpfAttribute);

								m_nestedPrimaryKey = true;

								m_nestedAttribute = cpfAttribute;

							}

							// Setting Propertyname as a resource name.....

							String resourceName = relationKey
							.getRelationShipInfo().getPropertyName();

							m_resourceInfo = new ResourceInfo();

							m_resourceInfo.setResourceName(resourceName);

							m_cpfResource.setResourceInfo(m_resourceInfo);

							result = create.generate(m_cpfResource);

							path = Util.getProjectPath(p_screenCapture
									.getPortletRef().getCurrentProject());

							String classname = resourceName.toUpperCase()
							.charAt(0)
							+ resourceName.substring(1);

							path = path + destMessages + classname + "_"

							+ p_screenCapture.getPortletRef().getPortletId()
							+ ".java";

							LOG.info(path);

							try {

								file = new FileWriter(path);

								file.flush();

								file.write(result);

							} catch (IOException e) {

								e.printStackTrace();

							} finally {

								if (file != null) {

									try {

										file.close();

									} catch (IOException e) {

										LOG
										.error(
												"Got exception while closing reader ...",
												e);

									}

									file = null;

								}

							}

						} finally {

							// Removing primary key of related one from list If
							// added explicitly here...

							if (m_nestedPrimaryKey) {

								List<CPFAttribute> otherEntityAttributes = p_screenCapture

								.getNestedAttributes().get(relationKey);

								otherEntityAttributes.remove(m_nestedAttribute);

							}

						}

						LOG.info("Dependent Entitiy "
								+ relationKey.getRelationShipInfo()
								.getPropertyName()

								+ " Bean has been generated......");

					}

					LOG.info("Dependent Entitiy Beans has been generated");

					// End of Generating Java Beans of Web Service for other
					// Selected Entities

				}

			} // End of if (webMethodsMap.containsKey(OperationType.CREATE)
			// ||..)

		} // End of If p_screenCapture.getWebServiceInfo().getWebMethodsMap()
		// != null && ....

		LOG.info("Code Generation for Web Services finished Successfully....");

	}

	/**
	 * 
	 * Finds out wheather the selected other entities has any many relatioin
	 * shipwith Base entity or not
	 * 
	 * @param p_screenCapture
	 *            screen to generate Web Service
	 * 
	 * @return true if many relation exists with base entity false otherwise
	 * 
	 */

	private boolean isManyExists(CPFScreen p_screenCapture) {

		boolean manyExists = false;

		for (Iterator<RelationKey> itr = p_screenCapture.getNestedAttributes()
				.keySet().iterator(); itr.hasNext();) {

			RelationKey relationKey = itr.next();

			RelationShipInfo relationShipInfo = relationKey
			.getRelationShipInfo();

			if (relationShipInfo.getMapping()
					.equals(RelationshipType.OneToMany)

					|| relationShipInfo.getMapping().equals(
							RelationshipType.ManyToMany)) {

				manyExists = true;

				break;

			}

		}

		return manyExists;

	}

	/**
	 * 
	 * Finds out primary Key selcted or not from the wizard
	 * 
	 * @param cpfAttributes
	 *            in which we have to search for a primary Key
	 * 
	 * @return true if primaryKey exists otherwise false
	 * 
	 */

	private boolean isPrimaryKeyExists(List<CPFAttribute> cpfAttributes) {

		boolean primary = false;

		for (Iterator<CPFAttribute> itrCpfAttribute = cpfAttributes.iterator(); itrCpfAttribute
		.hasNext();) {

			ModelAttribute modelAttribute = ((CPFAttribute) itrCpfAttribute
					.next()).getModelAttrib();

			if (modelAttribute.isPK()) {

				primary = true;

				break;

			}

		}

		return primary;

	}

	private List<RelationKey> getNestedRelations(CPFScreen p_screenCapture) {

		List<RelationKey> rel = null;

		for (Iterator<RelationKey> itr = p_screenCapture.getNestedAttributes()
				.keySet().iterator(); itr.hasNext();) {

			RelationKey relationKey = itr.next();

			RelationShipInfo relationShipInfo = relationKey
			.getRelationShipInfo();

			if (relationShipInfo.getMapping()
					.equals(RelationshipType.OneToMany)

					|| relationShipInfo.getMapping().equals(
							RelationshipType.ManyToMany)) {

				if (rel == null) {

					rel = new ArrayList<RelationKey>();

				}

				rel.add(relationKey);

			}

		}

		return rel;

	}

	@Override
	public void createPackage(IFolder src) {

		IFolder com = src.getFolder("com");

		try {

			if (!com.exists())

				com.create(true, true, null);

		} catch (CoreException e) {

			LOG.info("Got exception while creating package structure.." + e);

		}

		// Creating genband folder...

		IFolder genband = com.getFolder("genband");

		try {

			if (!genband.exists())

				genband.create(true, true, null);

		} catch (CoreException e) {

			LOG.info("Got exception while creating package structure.." + e);

		}

		// Creating m5 folder..

		IFolder m5 = genband.getFolder("m5");

		try {

			if (!m5.exists())

				m5.create(true, true, null);

		} catch (CoreException e) {

			LOG.info("Got exception while creating package structure.." + e);

		}

		// Creating maps folder...

		IFolder maps = m5.getFolder("maps");

		try {

			if (!maps.exists())

				maps.create(true, true, null);

		} catch (CoreException e) {

			LOG.info("Got exception while creating package structure.." + e);

		}

		// Creating services Folder....

		IFolder services = maps.getFolder("services");

		try {

			if (!services.exists())

				services.create(true, true, null);

		} catch (CoreException e) {

			LOG.info("Got exception while creating package structure.." + e);

		}

		// Creating messages Folder....

		IFolder messages = maps.getFolder("messages");

		try {

			if (!messages.exists())

				messages.create(true, true, null);

		} catch (CoreException e) {

			LOG.info("Got exception while creating package structure.." + e);

		}

	}

	private void removeGroupBars(CPFScreen cpfScreen) {

		List<Integer> gbPos = null;

		List<CPFAttribute> selectedAttribute = cpfScreen
		.getSelectedAttributes();

		detailsSelectedAttbs = new ArrayList<CPFAttribute>();

		// Adding all selected attributes to the list so that can be added later
		// to the list...

		for (CPFAttribute attribute : selectedAttribute) {

			detailsSelectedAttbs.add(attribute);

			if (attribute.isGroup()) {

				if (gbPos == null) {

					gbPos = new ArrayList<Integer>();

				}

				gbPos.add(attribute.getPosition());

			}

		}

		// Removing group bar items from the list to generate Web Service..

		if (gbPos != null) {

			int i = 0;

			for (Integer pos : gbPos) {

				selectedAttribute.remove(pos - (i++));

			}

		}

	}

}

