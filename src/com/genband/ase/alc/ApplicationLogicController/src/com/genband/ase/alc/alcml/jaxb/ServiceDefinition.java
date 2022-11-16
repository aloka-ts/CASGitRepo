package com.genband.ase.alc.alcml.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.ase.startup.AseClassLoader;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.FileUtils;
import com.baypackets.ase.util.exceptions.ZipExtractException;
import com.genband.ase.alc.TelnetInterface.TelnetInterfaceUtils;
import com.genband.ase.alc.alcml.jaxb.xjc.AlcServiceDefinitiontype;
import com.genband.ase.alc.alcml.jaxb.xjc.ApplicationLogicControltype;
import com.genband.ase.alc.alcml.jaxb.xjc.Attributetype;
import com.genband.ase.alc.alcml.jaxb.xjc.Conditiontype;
import com.genband.ase.alc.alcml.jaxb.xjc.Executetype;
import com.genband.ase.alc.alcml.jaxb.xjc.Formtype;
import com.genband.ase.alc.alcml.jaxb.xjc.Includetype;
import com.genband.ase.alc.alcml.jaxb.xjc.Labeltype;
import com.genband.ase.alc.alcml.jaxb.xjc.Lasttype;
import com.genband.ase.alc.alcml.jaxb.xjc.Looptype;
import com.genband.ase.alc.alcml.jaxb.xjc.Nexttype;
import com.genband.ase.alc.alcml.jaxb.xjc.Regextype;
import com.genband.ase.alc.alcml.jaxb.xjc.Servicetype;
import com.genband.ase.alc.asiml.jaxb.ActionImplementationtype;
import com.genband.ase.alc.asiml.jaxb.ApplicationServiceImplementationtype;
import com.genband.ase.alc.asiml.jaxb.ServiceImplementations;
import com.genband.ase.alc.asiml.jaxb.TagValuetype;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
class DefaultServiceContextProvider implements ServiceContextProvider {
	/**
	 * 
	 */
	private static final long serialVersionUID = 12666L;

	public Object getAttribute(String nameSpace, String name) {
		return null;
	}

	public boolean setAttribute(String nameSpace, String name, Object value) {
		return false;
	}

	public boolean setGlobalAttribute(String nameSpace, String name,
			Object value) {
		return false;
	}

	public String DebugDumpContext() {
		return "";
	}
}

class ServiceDefinitionValidationEventHandler implements ValidationEventHandler {
	Logger logger1 = Logger.getLogger(ServiceDefinition.class.getName());
	//logger1.log(Level.DEBUG,"ServiceDefinition::<<<<<<IN  EVENT HANDLERRRRRRRR>>>>>>>");
	//logger1.log
	public boolean handleEvent(ValidationEvent event) {
		hasErrors = true;
		lastError = event;
		logger1.log(Level.DEBUG,
		"ServiceDefinition::<<<<<<IN HANDLE EVENT>>>>>>>"+ event.getMessage());
		return true;
	}
	
	public boolean hasErrors = false;
	public ValidationEvent lastError = null;
}

class ServiceDefinitionUnmarshallerListener extends Unmarshaller.Listener {
	public void afterUnmarshal(Object target, Object parent) {
	}

	public void beforeUnmarshal(Object target, Object parent) {
	}

}

/**
 * Application Logic Control Service Definition This class provides the
 * programatic representation of a ALCML definition. It provides an execution
 * framework for service logic.
 */
public class ServiceDefinition implements Serializable{
	
	/**
	 * 
	 */
	//hpahuja added serial version id to resolve the serailisation issues coming after applying patches
	private static final long serialVersionUID = 12555L;
	public static String UNNAMED = "UNNAMED";
	/*public static int count=0;
	public static long startTime=0;
	public static long endTime=0;
	public static boolean firstTimer=true;
	public static long cumulativeTime = 0;
	public static long hasErrorsCumTime=0;
	public static long recrefileCumTime=0;
	public static long  trnsfrmCumTime=0;
	public static long unmarshCumTime=0;
	public static long parseCumTime=0;*/
	private static Unmarshaller unmarshaller = null;
	private static JAXBContext jc = null;
	private static Unmarshaller getMarsheller(){
		
		logger.log(Level.DEBUG,
				"ServiceDefinition::getMarsheller ");
		try{
			if(jc == null){
				logger.log(Level.DEBUG,
				"ServiceDefinition::getMarsheller :: jc is null so creating a new instance ");
				jc = JAXBContext.newInstance(
					"com.genband.ase.alc.alcml.jaxb.xjc", JAXBLoader);
			}
			if (unmarshaller == null){
				logger.log(Level.DEBUG,
				"ServiceDefinition::getMarsheller :: unmarshaller is null so creating a new instance ");
				unmarshaller = jc.createUnmarshaller();
			}
		}catch (JAXBException e) {
			logger.log(Level.DEBUG,
			"ServiceDefinition::getMarsheller :: JAXBException exception occured while creating unmarshallar "+e.getMessage());
		}
		return unmarshaller;
	}

	/****************************** public interfaces ***************************************/
	/**
	 * executes the ALCML flow represented by this instance.
	 * 
	 * @param context
	 *            the user supplied context for this execution instance.
	 */
	public void execute(ServiceContext context)
			throws ServiceActionExecutionException {
		synchronized (context) {
			context.SetServiceName(Name);
			context.SetServiceNameSpace(NameSpace);
			ServiceAction firstAction = getAction(StartAction);
			if (firstAction == null)
				firstAction = ActionList.getFirst();

			context.HandleFirstAction(firstAction);
		}
	}

	/**
	 * returns a context that was associated with this service definition upon
	 * creation.
	 * 
	 * @return ServiceContextProvider the service context provider that was
	 *         associated with this service on creation.
	 */
	public ServiceContextProvider getServiceContextProvider() {
		synchronized (ServiceDefinitions) {
			return scp;
		}
	}

	public String getIoForm() {
		return alcML.getIoForm();
	}

	/**
	 * returns a service definition given by a name.
	 * 
	 * @param serviceName
	 *            name of service definition provided in original ALCML
	 *            instance.
	 * 
	 * @return ServiceDefinition associated with serviceName.
	 */
	public static ServiceDefinition getServiceDefinition(String serviceName) {
		return getServiceDefinition(UNNAMED, serviceName);
	}

	public static void addServiceDefinitionListener(
			ServiceDefinitionListener sdl) {
		synchronized (ServiceDefinitionListeners) {
			ServiceDefinitionListeners.add(sdl);
		}
	}

	public static Collection<ServiceDefinition> getServiceDefinitionForNamespace(
			String nameSpace) {
		synchronized (ServiceDefinitions) {
			if (nameSpace == null)
				return null;

			TreeMap<String, ServiceDefinition> sdMap = ServiceDefinitions
					.get(nameSpace);
			if (sdMap == null) {
				logger.log(Level.WARN,
						"ServiceDefinition::getServiceDefinitionForNamespace("
								+ nameSpace + ") namespace not found.");
				return null;
			}

			return sdMap.values();
		}
	}

	public static List<Object> getAlcMapping(String nameSpace) {
		synchronized (ServiceDefinitions) {
			if (nameSpace == null)
				return null;
			return AlcMapping.get(nameSpace);
		}
	}

	public static Formtype getFormDefinition(String nameSpace,
			String serviceName) {
		synchronized (ServiceDefinitions) {
			if (serviceName == null)
				return null;

			TreeMap<String, Formtype> formDef = FormDefinitions.get(nameSpace);
			if (formDef == null) {
				logger.log(Level.WARN, "ServiceDefinition::getFormDefinition("
						+ nameSpace + ", " + serviceName + ") form not found.");
				return null;
			}

			return formDef.get(serviceName);
		}
	}

	public static ServiceDefinition getServiceDefinition(String nameSpace,
			String serviceName) {
		synchronized (ServiceDefinitions) {
			if (__debug == true) {
				try {
					Initialize(scp);
				} catch (Exception e) {
					logger.log(Level.WARN,
							"ServiceDefinition::getServiceDefinition("
									+ serviceName
									+ ") Invalid Service definitions", e);
				}

			}
			if (serviceName == null)
				return null;

			TreeMap<String, ServiceDefinition> sdMap = ServiceDefinitions
					.get(nameSpace);
			if (sdMap == null) {
				logger
						.log(Level.WARN,
								"ServiceDefinition::getServiceDefinition("
										+ nameSpace + ", " + serviceName
										+ ") namespace not found.");
				return null;
			}

			return sdMap.get(serviceName);
		}
	}

	public static Map<String, String> getServiceDefinitionURL() {
		return namespaceMap;
	}

	/**
	 * creates a service definition from a ALCML instance.
	 * 
	 * @param arg
	 *            file location of valid ALCML instance.
	 */
	public static ServiceDefinition CreateALCMLDefinition(String nameSpace,
			URL arg,boolean onlyALCSvcDef, boolean isServerRestartReadFromFile ) throws ServiceCreationException {
		synchronized (ServiceDefinitions) {
			 java.util.Date time = new java.util.Date();
			/*if(firstTimer){
				startTime=System.currentTimeMillis();
				logger.log(Level.DEBUG," FIRST TIMER is false so initilize start time startTime ="+startTime);				
				firstTimer=false;
			}*/
			InputStream is = null;			
			logger.log(Level.DEBUG,
					"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### START ##### METHOD CreateALCMLDefinition START :::::"+new java.sql.Time(time.getTime()) );
			
			logger.log(Level.DEBUG,	"CreateALCMLDefinition<<<<<<<< >>>>>>>>>>CreateALCMLDefinition  called with nameSpace ="+nameSpace +
					"arg= "+ arg.toString()+"onlyALCSvcDef= "+onlyALCSvcDef +"onlyALCSvcDef = "+onlyALCSvcDef);
			try {
				is = arg.openStream();
				namespaceMap.put(nameSpace, arg.toString());
			} catch (IOException e) {
				logger.log(Level.WARN,
						"ServiceDefinition::CreateALCMLDefinition ", e);
				throw new ServiceCreationException(
						"CreateALCMLDefinition without stream failed for " + arg + " "
								+ e.getMessage() + " " + e.getCause()+" Stack Trace "+e.getStackTrace());
			}
			/*count=count+1;
			logger.log(Level.WARN,
					"CreateALCMLDefinition COUNTER ="+count);*/
			return CreateALCMLDefinition(nameSpace, is, arg,onlyALCSvcDef,isServerRestartReadFromFile);
	
		}
	}

	/**
	 * creates a service definition from a ALCML instance.
	 * 
	 * @param arg
	 *            InputStream of valid ALCML instance.
	 */
	public static ServiceDefinition CreateALCMLDefinition(String nameSpace,
			InputStream arg, URL baseURL,boolean onlyALCSvcDef,boolean isServerRestartReadFromFile) throws ServiceCreationException {
		synchronized (ServiceDefinitions) {
			// java.util.Date time = new java.util.Date();
			if(isReloadClass){
			ReloadClasses();
			isReloadClass=false;
			}
			JAXBElement poElement = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Document d = null;
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				/*logger.log(Level.DEBUG,
						"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### START ##### time db.parse:::::"+new java.sql.Time(time.getTime()) );*/
				long startparseTime = System.currentTimeMillis();
				d = db.parse(arg);	
				/*parseCumTime = parseCumTime + (System.currentTimeMillis() - startparseTime);
				logger.log(Level.DEBUG,
						"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>> #### END ####  time db.parse::parseCumTime= "+parseCumTime);*/
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty("indent", "yes");
				long startTrnsTime = System.currentTimeMillis();
				/*logger.log(Level.DEBUG,
						"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### START #####transformer.transform:::::"+new java.sql.Time(time.getTime()) );*/
				transformer.transform(new DOMSource(d), new StreamResult(baos));
				/*trnsfrmCumTime = trnsfrmCumTime + (System.currentTimeMillis() - startTrnsTime);
				logger.log(Level.DEBUG,
						"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>> #### END #### transformer.transform:::::"+trnsfrmCumTime);*/
			} catch (Exception e) {
				logger.log(Level.WARN,
						"ServiceDefinition::CreateALCMLDefinition ", e);
				throw new ServiceCreationException(
						"CreateALCMLDefinition with stream failed for " + arg + " "
								+ e.getMessage() + " " + e.getCause()+" Stack Trace "+e.getStackTrace());
			}

			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(baos
						.toByteArray());
				logger.log(Level.WARN,
				"ServiceDefinition::CreateALCMLDefinition :: converting input stream to BAOS");
				/*byte currentXMLBytes[] = arg.toString().getBytes();
				 ByteArrayInputStream bais = new 
					ByteArrayInputStream(currentXMLBytes);*/
				
				// Class clazz = Class.forName("javax.xml.bind.JAXBContext",
				// true, JAXBContextLoader);
				// JAXBContext jc = (JAXBContext) clazz.getMethod("newInstance",
				// new Class[] {String.class, ClassLoader.class}).invoke(null,
				// "com.genband.ase.alc.alcml.jaxb.xjc", JAXBLoader);

				 //hpahuja moving the unmarshaling code to getmarshalar block
//				JAXBContext jc = JAXBContext.newInstance(
//						"com.genband.ase.alc.alcml.jaxb.xjc", JAXBLoader);
//				Unmarshaller unmarshaller = jc.createUnmarshaller();
				 logger.log(Level.WARN,
							"ServiceDefinition::CreateALCMLDefinition :: calling getMarsheller");				
				unmarshaller = getMarsheller();
				unmarshaller.setListener(new ServiceDefinitionUnmarshallerListener());
				ServiceDefinitionValidationEventHandler evHandler = new ServiceDefinitionValidationEventHandler();
				unmarshaller.setEventHandler(evHandler);
				/*logger.log(Level.DEBUG,
						"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### START ##### unmarshal(bais):::::"+new java.sql.Time(time.getTime()) );*/
				//long startUnmarshTime =System.currentTimeMillis();
				logger.log(Level.DEBUG,"Unmarshalling the Byte array input stream bais"); 
				poElement = (JAXBElement) unmarshaller
						.unmarshal(bais);
				/*unmarshCumTime = unmarshCumTime + (System.currentTimeMillis() - startUnmarshTime);
				logger.log(Level.DEBUG,
						"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### END ##### unmarshal(bais):unmarshCumTime::::"+unmarshCumTime );
				*/
				/*
				 * lets read from file the Service object
				 */
				

				 boolean isReadSuccess =false;
				// hpahuja|fix for the service definitions not completely getting generated in case of server restart and if the file doesn't exist
				 //Read only if there is a server restart and if file
				// exist.If the file doesn't exist then reset the value of the
				// isServerRestartReadFromFile to false so that the service definitions are not read from
				// file.|Start
				if (isServerRestartReadFromFile
						&& !serviceDefinitionFileExist()) {
					logger
							.log(Level.DEBUG,
									"Check if Service Def file exists before Reading service definition from file");
					isServerRestartReadFromFile = false;
					logger.log(Level.DEBUG,
							"value of isServerRestartReadFromFile = "
									+ isServerRestartReadFromFile);
				}
				// hpahuja| Read only if there is a server restart and if file
				// exist.If the file doesn't exist then reset the value of the
				// isServerRestartReadFromFile to false so that the service definitions are not read from
				// file.|End
				if (isServerRestartReadFromFile) {
					// on Server restart case read from file donot create
					// bindings again
					logger.log(Level.DEBUG,
							"Reading service definition from file");
					isReadSuccess = readApplicationLogicControl();

				}
                 if(isReadSuccess){
                	 /*
                	  * Maps has read successfully lets check if the entry exists in map for this service
                	  */
                	 logger.log(Level.DEBUG,"Service definition READ successfully from file"); 
                	 TreeMap<String, ServiceDefinition> sdMap = ServiceDefinitions
 					.get(nameSpace);
                	 if(sdMap!=null && sdMap.lastEntry()!=null)
                	 return  sdMap.lastEntry().getValue();
//                	 else
//                		 return null;
                	 
                	 if (logger.isEnabledFor(Level.DEBUG))
         				logger
         						.debug("No Entry in ServiceData files for  "+nameSpace+" It may be fist time deployment from SAS IDE");
                 }
                 
                 /*
                  *  ends here reading  
                  */
          
				 
				ApplicationLogicControltype returnVal  = (ApplicationLogicControltype)poElement.getValue();
				
				Iterator inclusions = returnVal.getServiceOrIncludeOrForm()
						.listIterator();
				while (inclusions.hasNext()) {
					Object obj = inclusions.next();
					if (obj instanceof Includetype) {
						logger.log(Level.INFO,
								"ServiceDefinition::CreateALCMLDefinition for include type");
						Includetype inclusion = (Includetype) obj;	
						
						CreateALCMLDefinition(nameSpace, new URL(baseURL,
								inclusion.getServiceLocation()),onlyALCSvcDef,isServerRestartReadFromFile);
					}
				}
				logger.log(Level.DEBUG,"EVHANDLER.hasErrors="+evHandler.hasErrors);
				boolean stillReplacing = true;
				while (evHandler.hasErrors && stillReplacing) {
					long hasErrorsStartTime = System.currentTimeMillis();	
					
					stillReplacing = false;
					LinkedList<String> ll = new LinkedList<String>();
					ServiceDefinition.getNodes(ll, d);
					logger.log(Level.DEBUG,
					"ServiceDefinition::CreateALCMLDefinition IN evHandler.hasErrors block");
					logger.log(Level.DEBUG,"IN evHandler.hasErrors block :EVHANDLER.hasErrors="+evHandler.hasErrors);
					Iterator iter = ll.iterator();					
					
					while (iter.hasNext()) {
						String elementName = (String) iter.next();
						Servicetype Definition = findMacro(nameSpace,
								elementName, poElement);
						
						if (Definition != null) {
							logger.log(Level.DEBUG,
									"ServiceDefinition::CreateALCMLDefinition <<< IN ITERATING LINKLIST Definition = "+Definition.getName());
							stillReplacing = true;
							NodeList nl = d.getElementsByTagName(elementName);
							logger.log(Level.DEBUG,
							"ServiceDefinition::CreateALCMLDefinition execute element(elementName) "+elementName);
							while (nl != null && nl.getLength() > 0) {
								logger.log(Level.INFO,
								"ServiceDefinition::CreateALCMLDefinition IN creating execute element ");
								Node n = nl.item(0);
								/* create a Macro Element */
								Element newElement = d.createElement("execute");
								/* add Element name as attribute */
								newElement.setAttribute("name", elementName);

								if (n.hasAttributes()) {
									NamedNodeMap nnm = n.getAttributes();
									int attrIndex = 0;
									int attrLength = nnm.getLength();
									while (attrIndex < attrLength) {
										Attr nattr = (Attr) nnm.item(attrIndex);
										Iterator attriter = Definition
												.getAttribute().iterator();
										boolean parmvalidation = false;
										while (attriter.hasNext()) {
											Attributetype adt = (Attributetype) attriter
													.next();
											if (!nattr.getName().equals(
													adt.getName()))
												continue;
											parmvalidation = true;
										}

										if (!parmvalidation) {
											throw new ServiceCreationException(
													"Wrong attributes to macro -- "
															+ nattr.getName());
										}

										Element attrElement = d
												.createElement("attribute");
										attrElement.setAttribute("name", nattr
												.getName());
										logger.log(Level.DEBUG,
										"ServiceDefinition::CreateALCMLDefinition IN creating execute nattr"+nattr);
										attrElement.setAttribute("value", nattr
												.getValue());
										newElement.appendChild(attrElement);
										attrIndex++;
									}

								}
								if (n.hasChildNodes()) {
									NodeList cnl = n.getChildNodes();
									int childIndex = 0;
									int childLength = cnl.getLength();
									while (childIndex < childLength) {
										Node cn = cnl.item(childIndex);
										newElement.appendChild(cn
												.cloneNode(true));
										childIndex++;
									}
								}
								n.getParentNode().replaceChild(newElement, n);
								d.normalizeDocument();
								nl = d.getElementsByTagName(elementName);
							}
						}
					}					
					baos = new ByteArrayOutputStream();
					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer = transformerFactory
							.newTransformer();
					transformer.setOutputProperty("indent", "yes");
					transformer.transform(new DOMSource(d), new StreamResult(
							baos));
				/*//hpahuja print DOM tree | uncomment this code for debugging and priniting the DOM TREE
					logger.log(Level.INFO,
					"ServiceDefinition::CreateALCMLDefinition PRINTING DOM TREE");
					 StringWriter writer = new StringWriter();
					 StreamResult result = new StreamResult(writer);
				       TransformerFactory tf = TransformerFactory.newInstance();
				       Transformer transformer = tf.newTransformer();
				       transformer.transform(new DOMSource(d), result);				      
				       logger.log(Level.INFO,
						"ServiceDefinition::CreateALCMLDefinition PRINTING DOM TREE"+ writer.toString());
				     //hpahuja print DOM tree |END
*/				       
					bais = new ByteArrayInputStream(baos
							.toByteArray());
					//hpahuja moved the code to getmarshalar 
					/*jc = JAXBContext.newInstance(
							"com.genband.ase.alc.alcml.jaxb.xjc", JAXBLoader);
					unmarshaller = jc.createUnmarshaller();*/
					//Unmarshaller unmarshaller = getMarsheller();
					//hpahuja end
					unmarshaller = getMarsheller();
					unmarshaller
							.setListener(new ServiceDefinitionUnmarshallerListener());
					evHandler = new ServiceDefinitionValidationEventHandler();
					unmarshaller.setEventHandler(evHandler);
					/*logger.log(Level.DEBUG,
							"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### START ##### SECOND unmarshal(bais):::::"+new java.sql.Time(time.getTime()) );*/
					poElement = (JAXBElement) unmarshaller
							.unmarshal(bais);
				/*	hasErrorsCumTime = hasErrorsCumTime + (System.currentTimeMillis() - hasErrorsStartTime);
					logger.log(Level.DEBUG,
							"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### END ##### HAS ERROR BLOCK hasErrorsCumTime:::::"+hasErrorsCumTime );
					*/
					
				}
				if (evHandler.hasErrors) {
					logger.log(Level.WARN, baos.toString());
					logger.log(Level.WARN, "LAST ERROR *** "
							+ evHandler.lastError);
					throw new ServiceCreationException(
							"CreateALCMLDefinition failed for "
									+ arg
									+ " found undefined elements \nLAST ERROR *** "
									+ evHandler.lastError);
				}
			} catch (Exception e) {
				logger.log(Level.WARN,
						"ServiceDefinition::CreateALCMLDefinition ", e);
				throw new ServiceCreationException(
						"CreateALCMLDefinition marshal failed for " + arg + " "
								+ e.getMessage() + " " + e.getCause()+" Stack Trace "+e.getStackTrace());
			}

			ApplicationLogicControltype returnVal = (ApplicationLogicControltype)poElement.getValue();

			ServiceDefinition sd = null;
			Object obj=null;
			
			try {
				Iterator i = returnVal.getServiceOrIncludeOrForm()
						.listIterator();
				/*logger.log(Level.DEBUG,
						"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### START ##### Iterating returnVal :::::"+new java.sql.Time(time.getTime()) );*/
				while (i.hasNext()) {
					 obj = i.next();
					if (onlyALCSvcDef == false && obj instanceof Includetype) {
					
					} else if (obj instanceof AlcServiceDefinitiontype) {
						
						logger.log(Level.INFO,
						"ServiceDefinition::CreateALCMLDefinition getPatternOrUrlPatternOrContextParam");
						AlcMapping
								.put(
										nameSpace,
										((AlcServiceDefinitiontype) obj)
												.getPatternOrUrlPatternOrContextParam());
					} else if (onlyALCSvcDef == false && obj instanceof Formtype) {
						Formtype thisform = (Formtype) obj;
						TreeMap<String, Formtype> formDef = FormDefinitions
								.get(nameSpace);
						if (formDef == null) {
							formDef = new TreeMap<String, Formtype>();
							FormDefinitions.put(nameSpace, formDef);
						}
						if (thisform.getName() != null)
							formDef.put(thisform.getName(), thisform);
					} else if(onlyALCSvcDef == false){
						
						long startServTime = System.currentTimeMillis();
						/*logger.log(Level.DEBUG,
								"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### START ##### CREATING SERVICE DEFINITION :::::"+new java.sql.Time(time.getTime()) );*/
						
						sd = new ServiceDefinition(nameSpace, (Servicetype) obj);
						/*logger.log(Level.DEBUG,"creating service DEFINITION cumulativeTime is"+cumulativeTime);						
						cumulativeTime = cumulativeTime + (System.currentTimeMillis() - startServTime);*/
						}
					
					
				}
				/*logger.log(Level.DEBUG,
						"CreateALCMLDefinition<<<<<<<< TIMER>>>>>>>>>>#### END ##### Iterating returnVal :::::"+new java.sql.Time(time.getTime()) );*/
			} catch (ServiceActionCreationException e) {
				logger.log(Level.WARN,
						"ServiceDefinition::CreateALCMLDefinition ", e);
				throw new ServiceCreationException(
						"CreateALCMLDefinition failed for ServiceOrInclude" + arg + " "
								+ e.getMessage() + " " + e.getCause()+" Stack Trace "+ e);
			}

			/* notify listeners */
			synchronized (ServiceDefinitionListeners) {
				Iterator<ServiceDefinitionListener> iter = ServiceDefinitionListeners
						.iterator();
				while (iter.hasNext()) {
					iter.next().ServiceNamespaceAdded(nameSpace);
				}
			}
			/*
			 *  lets write to file reeta i m here creating new file in case we are deploying application in this case baseURL will not be null
			 *  and its not server restart  . as in server restart case we need to read this file . AND AS MEDIA SERVER WILL ASLO WRITE TO SAME FILE WEHN CALL WILL RUN 
			 *  SO CANNT RECREATE FILE AT THAT TIME SO BASEurl CONDITION HAS BEEN PUT HERE . WE CAN REMOVE isServerRestartReadFromFile CONDITION HERE 
			 *  AS THIS CODE WILL NOT BE CALLED IN THAT CASE IF SERVER RESTART CASE IF FILE EXISTS.BECAUSE IT WILL REDA FROM FILE .IT WILL NOT WRITE AGAIN
			 */
		
			boolean recreatefile = baseURL!=null && ! isServerRestartReadFromFile ? true:false;
			logger.debug("<<<<<recreatefile"+recreatefile);
			logger.debug("<<<<<baseURL"+baseURL);
			
			//long recreatefileTime = System.currentTimeMillis();
	//		   logger.log(Level.DEBUG,"Writing tree map to file");
			
			/*
			 * Writing logic for avoiding re-writing of Media service related definitions on SAs restart when first call is received 
			 * Media Service will initialize itself and will write definitons again which are already written
			 */
			String defName =null;
			if(obj instanceof Servicetype)
				defName=((Servicetype)obj).getName();
			
			boolean donotwriteAlreaywritten=false;
			
			TreeMap<String, ServiceDefinition> sdMap = ServiceDefinitions.get(nameSpace);
			
			if (sdMap != null) {
				
				if(defName!=null && defName.equals("MSMLPLAY_COLLECT_COMPLETED")){
					if(sdMap.get("MSMLPLAY_COLLECT_COMPLETED")!=null)						
						donotwriteAlreaywritten=true;
					logger.debug("first condition  "+donotwriteAlreaywritten );
				}
				
				if(defName!=null && defName.equals("MSCMLPLAY_COLLECT_COMPLETED")){
					if(sdMap.get("MSCMLPLAY_COLLECT_COMPLETED")!=null)
						donotwriteAlreaywritten=true;
					logger.debug("second condition  "+donotwriteAlreaywritten );
				}
			
			}
			
			if(logger.isDebugEnabled())
				logger.debug(" Do i need to write ServiceDefinition to file "+donotwriteAlreaywritten + "nameSpace= "+nameSpace);
			
			
			if(donotwriteAlreaywritten){
			  if(logger.isDebugEnabled())
				logger.debug("Map is already having this Media Server definition  "+defName +" so not writing " + ServiceDefinitions );
			
		    }
			/*
			 * ends logic 
			 */
			
			/*
			 * Write to file if not already written 
			 */
			if(!donotwriteAlreaywritten)
		     writeApplicationLogicControl(recreatefile);
			
			
		  /*   recrefileCumTime = recrefileCumTime+ (System.currentTimeMillis() - recreatefileTime);
		     logger.log(Level.DEBUG,"RECREATEFILE Cummulative file "+recrefileCumTime);*/
			/*
			 * Ends here writing 
			 */
		   		/*hpahuja : commented this code as this code was specifically written for timestamping for deployment time issue.
		   		 * count=count-1;
				logger.log(Level.INFO,
				"RECURSIVE CreateALCMLDefinition ###include### decrmented COUNTER="+count);
				if(count==0)
				{
					endTime=System.currentTimeMillis();
					logger.log(Level.DEBUG,
							"CreateALCMLDefinition<<<<<<<< TIMER NEW>>>>>>>>>>#### START ##### END TIME::::: "+endTime);
					long totalTime=endTime-startTime ;
					logger.log(Level.DEBUG,
							"CreateALCMLDefinition<<<<<<<< TIMER NEW >>>>>>>>>>#### START ##### TOTAL TIME::::: "+totalTime);
					logger.log(Level.DEBUG,"total SEVICE TREE MAP CREATION TIME is" + cumulativeTime);
					logger.log(Level.DEBUG,"total HAS errors cumulative time is " + hasErrorsCumTime);
					logger.log(Level.DEBUG,"total RECREATE FILE cumulative time is " + recrefileCumTime);
					logger.log(Level.DEBUG,"total TRANSFORM cumulative time is " + trnsfrmCumTime);
					logger.log(Level.DEBUG,"total UNMARSHAL cumulative time is " + unmarshCumTime);
					logger.log(Level.DEBUG,"total PARSER cumulative time is " + parseCumTime);				
				}*/
			return sd;
		}
	}

	/**
	 * Initialize service definitions. If user provides optional
	 * ServiceContextProvider implementation, This function reads in the
	 * definitions from the /ServiceDefinitionLocator.xml ASIML file Values in
	 * the file that have Tags of "ServiceFile" will be processed and
	 * subsequently available
	 * 
	 * @param scp
	 *            ServiceContextProvider context that governs this application.
	 */
	public static void Initialize(ServiceContextProvider scp)
			throws ServiceCreationException {
		synchronized (ServiceDefinitions) {
			if (scp == null)
				scp = new DefaultServiceContextProvider();

			ReloadClasses();

			// if (tiu == null)
			// tiu = new TelnetInterfaceUtils();

			ServiceDefinition.AddServiceContextProvider(scp);
			{
				InputStream is = scp.getClass().getResourceAsStream(
						"/ServiceDefinitionLocator.xml");
				if (is != null) {
					ApplicationServiceImplementationtype files = ServiceImplementations
							.CreateGSIMLDefinition(is);
					ProcessApplicationServiceImplementation(files);
				}
			}
		}
	}

	/**
	 * Initialize service definitions.
	 */
	public static void Initialize() throws ServiceCreationException {
		Initialize(new DefaultServiceContextProvider());
	}

	/**
	 * Destroys service definitions.
	 */
	public static void Destroy() {
		synchronized (ServiceDefinitions) {
			if (tiu != null)
				tiu.cleanup();

			ServiceDefinitions = new TreeMap<String, TreeMap<String, ServiceDefinition>>();
			tiu = null;
		}
	}

	/**
	 * This method provides a binding from the User space to the ALC. It is used
	 * by the ALCML compiler and should not be accessed otherwise.
	 * 
	 * @param XSDName
	 *            is the literal name of the complex type generated by the ALCML
	 *            compiler in the schema definition.
	 * @param sClass
	 *            is the class that will be responsible for creating
	 *            ServiceActions that correspond to the XSDName.
	 */
	public static void RegisterCreationClass(String XSDName, Class sClass) {
		synchronized (ServiceDefinitions) {
			logger.log(Level.DEBUG, "Class - " + sClass + ", registering "
					+ XSDName);
			registeredUserDefinitions.put(XSDName, sClass);
		}
	}

	static public void removeNameSpace(String nameSpace) {
		synchronized (ServiceDefinitions) {
			TreeMap<String, ServiceDefinition> sdMap = ServiceDefinitions
					.get(nameSpace);
			if (sdMap != null) {
				logger.log(Level.INFO, "Removing name space " + nameSpace);
				ServiceDefinitions.remove(nameSpace);
				/* notify listeners */
				synchronized (ServiceDefinitionListeners) {
					Iterator<ServiceDefinitionListener> iter = ServiceDefinitionListeners
							.iterator();
					while (iter.hasNext()) {
						iter.next().ServiceNamespaceRemoved(nameSpace);
					}
				}
				LocalServiceContextProvider.removeNameSpace(nameSpace);

			} else
				logger.log(Level.INFO, "Name space not removed, not found: "
						+ nameSpace);
		}
	}

	/********************** End of public interfaces ******************************************/

	void ProcessDefinitions(List<Object> XMLActionTypeList,
			List<ServiceAction> serviceActionList)
			throws ServiceActionCreationException {
		synchronized (ServiceDefinitions) {
			Iterator i = XMLActionTypeList.iterator();
			while (i.hasNext()) {
				Object LoopOrAction = i.next();
				List<ServiceAction> sServices = CreateServiceAction(LoopOrAction);
				Iterator sServiceActionIter = sServices.iterator();
				while (sServiceActionIter.hasNext()) {
					ServiceAction sServiceAction = (ServiceAction) sServiceActionIter
							.next();
					serviceActionList.add(sServiceAction);
				}
			}
		}
	}

	ServiceAction getAction(String s) {
		synchronized (ServiceDefinitions) {
			if (s == null)
				return null;
			return ActionDefinitions.get(s);
		}
	}

	static Logger logger = Logger.getLogger(ServiceDefinition.class.getName());

	private static void getNodes(LinkedList<String> ll, Node n) {
		if (!ll.contains(n.getNodeName())) {
			if (n.getNodeType() == Node.ELEMENT_NODE)
				ll.add(n.getLocalName());
		}
		logger.log(Level.DEBUG,
				"ServiceDefinition::IN GET NODES adding Node n ="+n.getLocalName());
		NodeList nl = n.getChildNodes();
		int maxValues = nl.getLength();
		int index = 0;
		while (index < maxValues) {
			Node sn = nl.item(index);
			if (!ll.contains(sn.getNodeName())) {
				if (n.getNodeType() == Node.ELEMENT_NODE)
					logger.log(Level.DEBUG,
							"ServiceDefinition::IN GET NODES adding Node child nodes of n ="+sn.getNodeName());
					ll.add(sn.getNodeName());
			}
			if (sn.hasChildNodes()) {
				getNodes(ll, sn);
			}
			index++;
		}
	}

	private static Servicetype findMacro(String nameSpace, String name,
			JAXBElement po) {
		Servicetype Definition = null;
		ServiceDefinition _def = getServiceDefinition(nameSpace, name);

		if (_def != null)
			Definition = _def.getALCML();

		if (Definition == null) {
			ApplicationLogicControltype defs = (ApplicationLogicControltype)po.getValue();
			Iterator i = defs.getServiceOrIncludeOrForm().listIterator();
			while (i.hasNext()) {
				Object obj = i.next();
				if (obj instanceof Servicetype) {
					Definition = (Servicetype) obj;
					if (Definition.getName().equals(name))
						break;
					Definition = null;
				}
			}
		}
		return Definition;
	}

	private static void ProcessApplicationServiceImplementation(
			ApplicationServiceImplementationtype files)
			throws ServiceCreationException {
		{
			List<ActionImplementationtype> implList = files
					.getActionImplementation();
			Iterator implI = implList.iterator();
			while (implI.hasNext()) {
				ActionImplementationtype currentInstance = (ActionImplementationtype) implI
						.next();
				List<TagValuetype> contextList = currentInstance.getContext();
				Iterator i = contextList.iterator();
				while (i.hasNext()) {
					TagValuetype tv = (TagValuetype) i.next();
					logger.log(Level.DEBUG,
							"ProcessApplicationServiceImplementation "
									+ tv.getTag() + " " + tv.getValue());
					if (tv.getTag().equals("DebugReload")) {
						__debug = false;
					} else if (tv.getTag().equals("ServiceFile")) {
						continue;
					} else {
						ServiceDefinition.RegisterServiceAlias(tv.getTag(), tv
								.getValue());
					}
				}
			}
		}
		{
			List<ActionImplementationtype> implList = files
					.getActionImplementation();
			Iterator implI = implList.iterator();
			while (implI.hasNext()) {
				ActionImplementationtype currentInstance = (ActionImplementationtype) implI
						.next();
				List<TagValuetype> contextList = currentInstance.getContext();
				Iterator i = contextList.iterator();
				while (i.hasNext()) {
					TagValuetype tv = (TagValuetype) i.next();
					if (tv.getTag().equals("ServiceFile")) {
						try {
							ServiceDefinition.CreateALCMLDefinition(UNNAMED,
									new URL(tv.getValue()),false,false);
						} catch (MalformedURLException mue) {
							throw new ServiceCreationException(
									"CreateALCMLDefinition ActionImpl failed for "
											+ tv.getValue() + " "
											+ mue.getMessage() + " "
											+ mue.getCause()+" Stack Trace "+mue.getStackTrace());
						}
					}
				}
			}
		}
	}

	private static void RegisterServiceAlias(String alias, String ClassName) {
		if (ClassAlias == null) {
			ClassAlias = new TreeMap<String, String>();
		}
		ClassAlias.put(alias, ClassName);
	}

	private static String getClassName(String serviceName) {
		return ClassAlias.get(serviceName);
	}

	private static void AddServiceContextProvider(ServiceContextProvider scp) {
		ServiceDefinition.scp = scp;
	}

	private List<ServiceAction> CreateServiceAction(Object XMLActionType)
			throws ServiceActionCreationException {
		ServiceAction sAction = null;
		LinkedList<ServiceAction> actionList = new LinkedList<ServiceAction>();
             logger.log(Level.INFO,"  CreateServiceAction for "+XMLActionType);   
		synchronized (registeredUserDefinitions) {
			 logger.log(Level.INFO," CreateServiceAction for inside1 : "+XMLActionType +" IsInstance of ServiceAction " + (XMLActionType
instanceof ServiceAction));
			if (registeredUserDefinitions.get(XMLActionType.getClass()
					.getName()) != null || ( XMLActionType instanceof ServiceAction)) {
				Class sClass = registeredUserDefinitions.get(XMLActionType
						.getClass().getName());
				try{
 //                                   System.out.println(" CreateServiceAction for inside 2:"+XMLActionType +" IsInstance of ServiceAction " + (XMLActionType //instanceof ServiceAction)); 
                                       if(sClass== null)
                                        sAction = (ServiceAction)XMLActionType;   
                                       else   
					sAction = (ServiceAction) sClass.newInstance();
				
                                   	sAction.Create(this, XMLActionType, actionList);
				} catch (Exception e) {
					logger.log(Level.WARN,
							"ServiceDefinition::CreateServiceAction ", e);
					throw new ServiceActionCreationException(XMLActionType
							.getClass().getName()
							+ ":: Sorry Could not create user service action object"+e);
				}
			} else if (XMLActionType instanceof String) {
				sAction = new ServiceActionNextAction();
				sAction.Create(this, XMLActionType, actionList);
			} else if (XMLActionType instanceof Looptype) {
				sAction = new ServiceActionLoop();
				sAction.Create(this, XMLActionType, actionList);
			} else if (XMLActionType instanceof Conditiontype) {
				Conditiontype ct = (Conditiontype) XMLActionType;
				if (ct.getIf() != null) {
					sAction = new ServiceActionCondition();
					sAction.Create(this, XMLActionType, actionList);
				}
				if (ct.getOnInput() != null) {
					sAction = new ServiceActionSwitch();
					sAction.Create(this, XMLActionType, actionList);
				}
			} else if (XMLActionType instanceof Regextype) {
				sAction = new ServiceActionRegex();
				sAction.Create(this, XMLActionType, actionList);
			} else if (XMLActionType instanceof Labeltype) {
				sAction = new NoOperation(true);
				sAction.Create(this, XMLActionType, actionList);
			} else if (XMLActionType instanceof Nexttype) {
				sAction = new ServiceLoopEvent(ServiceLoopEvent.Continue);
				sAction.Create(this, XMLActionType, actionList);
			} else if (XMLActionType instanceof Lasttype) {
				sAction = new ServiceLoopEvent(ServiceLoopEvent.Break);
				sAction.Create(this, XMLActionType, actionList);
			} else if (XMLActionType instanceof Executetype) {
				sAction = new ServiceMacroExecution();
				sAction.Create(this, XMLActionType, actionList);
			} else {
				throw new ServiceActionCreationException(XMLActionType
						.getClass().getName()
						+ ":: Could not create service action object");
			}
		}
		return actionList;
	}

	private void AddActionsToDefinition(List<ServiceAction> ActionList) {
		Iterator i = ActionList.iterator();
		int index = 0;
		while (i.hasNext()) {

			ServiceAction sAction = (ServiceAction) i.next();
			if (sAction.getLabel() != null) {
				logger.log(Level.INFO,
						"AddActionsToDefinition Adding NAMED action "
								+ sAction.getLabel() + " to definitions.");
				ActionDefinitions.put(sAction.getLabel(), sAction);
			}

			if (this.ActionList.size() > 0)
				this.ActionList.getLast().setNextAction(sAction);
			this.ActionList.add(sAction);
		}
	}

	private ServiceDefinition(String nameSpace, Servicetype alcML)
			throws ServiceActionCreationException {
		synchronized (ServiceDefinitions) {
			logger.log(Level.DEBUG,"ServiceDefinition CONST::creating ServiceDefinition for "+ alcML.getName()+" START ACTION ="+ alcML.getStartAction() +" NameSpace= "+NameSpace);
			this.alcML = alcML;
			this.Name = alcML.getName();
			this.NameSpace = nameSpace;
			this.StartAction = alcML.getStartAction();
			LinkedList<ServiceAction> sList = new LinkedList<ServiceAction>();
			ProcessDefinitions(alcML.getConditionOrLoopOrRegex(), sList);
			sList.add(ServiceActionBlock.ServiceComplete);
			AddActionsToDefinition(sList);

			{
				Iterator i = sList.iterator();
				while (i.hasNext()) {
					Object objAction = i.next();
					logger.log(Level.INFO, "Action added -- "
							+ objAction.getClass().getSimpleName() + "["
							+ objAction.hashCode() + "]");
				}
			}
			TreeMap<String, ServiceDefinition> sdMap = ServiceDefinitions
					.get(nameSpace);
			if (sdMap == null) {
				logger.log(Level.INFO, "Creating service definition namespace "
						+ nameSpace);
				sdMap = new TreeMap<String, ServiceDefinition>();
				ServiceDefinitions.put(nameSpace, sdMap);
			}
			sdMap.put(this.Name, this);
		}
	}

	public List<Attributetype> getAttribute() {
		return alcML.getAttribute();
	}

	private Servicetype getALCML() {
		return alcML;
	}

	private static void ReloadClasses() {
		logger.log(Level.DEBUG, "IN ReloadClasses START");
		AseClassLoader myLoader = (AseClassLoader) ServiceDefinition.class
				.getClassLoader();
		
		File alcJarsDir = new File(Constants.ASE_HOME, "alcjars/");
		logger.log(Level.DEBUG, "IN ReloadClasses ::iterating jars");
		File[] files = alcJarsDir.listFiles();
		try {
			LinkedList<File> matchingJars = new LinkedList<File>();
			for (File file : files) {
				if (file.isFile()) {
					Pattern p = Pattern.compile(".*\\.jar");
					Matcher m = p.matcher(file.toURL().toString());
					if (m.find()) {
						matchingJars.add(file);
					}
				}
			}

			int length = matchingJars.size();
			int counter, index;
			File temp;
			logger.log(Level.DEBUG, "IN ReloadClasses::setting matching jars");
			for (counter = 0; counter < length - 1; counter++) {
				for (index = 0; index < length - 1 - counter; index++) {
					if (matchingJars.get(index).lastModified() > matchingJars
							.get(index + 1).lastModified()) {
						temp = matchingJars.get(index);
						matchingJars.set(index, matchingJars.get(index + 1));
						matchingJars.set(index + 1, temp);
					}
				}
			}

			LinkedList<Long> matchingTimes = new LinkedList<Long>();
			for (File file : matchingJars) {
				matchingTimes.add(file.lastModified());
			}

			if (!savedJars.equals(matchingJars)
					|| !savedTimes.equals(matchingTimes)) {
				File unpackedAlcJarsDir = new File(Constants.ASE_HOME,
						"alcjars/unpacked/");
				logger.log(Level.DEBUG, "IN ReloadClasses::unpackedAlcJarsDir");
				if (unpackedAlcJarsDir.exists()) {
					try {
						FileUtils.delete(unpackedAlcJarsDir);
					} catch (com.baypackets.ase.util.exceptions.FileDeleteException fde) {
						logger.log(Level.DEBUG, "error deleting directory "
								+ unpackedAlcJarsDir.toURL().toString(), fde);
					}
				}
				unpackedAlcJarsDir.mkdir();
				//hpahuja
				logger.log(Level.DEBUG, "IN ReloadClasses::declaring streams");
				InputStream tempStream =null;
				ZipInputStream zipStream =null;
				
				for (File file : matchingJars) {
					try {
						 tempStream = file.toURI().toURL()
								.openStream();
						 zipStream = new ZipInputStream(
								tempStream);
						logger.log(Level.DEBUG, "ReloadClasses -- Unpacked  "
								+ file.toURL().toString());
						FileUtils.extract(zipStream, unpackedAlcJarsDir);
					} catch (IOException ioe) {
						logger.log(Level.DEBUG, "ReloadClasses:: io exception "
								+ file.toURL().toString(), ioe);
					} catch (ZipExtractException zee) {
						logger.log(Level.DEBUG,
								"ReloadClasses:: ZipExtractException "
										+ file.toURL().toString(), zee);
					}//hpahuja
					finally{
						logger.log(Level.DEBUG, "IN ReloadClasses::Finally closing streams");
						zipStream.close();
						tempStream.close();									
					}
				}

				mkWritable(unpackedAlcJarsDir);

				logger
						.log(Level.DEBUG,
								"Creating new JAXB Class Loader for file with object factory");

				myLoader.removeLoader((AseClassLoader)JAXBLoader);
				AseClassLoader aseJAXBLoader = myLoader.createLoaderExtension();
				aseJAXBLoader.addRepository(unpackedAlcJarsDir.toURI().toURL());
				JAXBLoader = aseJAXBLoader;
				JAXBContextLoader = new AseClassLoader(myLoader.getURLs());

				savedJars = matchingJars;
				savedTimes = matchingTimes;
			}
		} catch (MalformedURLException mue) {
			logger.log(Level.DEBUG, "MalformedURLException "
					+ alcJarsDir.toString(), mue);
		} catch (IOException ioe) {
			logger.log(Level.DEBUG, "ReloadClasses:: io exception "
					+ alcJarsDir.toString(), ioe);
		}
		logger.log(Level.DEBUG, "IN ReloadClasses END");
	}

	private static void mkWritable(File file) {
		try {
			file.setWritable(true, false);
			logger.log(Level.DEBUG, "ReloadClasses -- setWritable  "
					+ file.toURL().toString());
			if (file.isDirectory()) {
				File[] chmodFiles = file.listFiles();
				for (File subfile : chmodFiles) {
					logger.log(Level.DEBUG, "ReloadClasses -- setWritable  "
							+ subfile.toURL().toString());
					if (subfile.isDirectory())
						mkWritable(subfile);
					else
						subfile.setWritable(true, false);

				}
			}
		} catch (MalformedURLException mue) {
			logger.log(Level.DEBUG, "MalformedURLException " + file.toString(),
					mue);
		} catch (IOException ioe) {
			logger.log(Level.DEBUG, "ReloadClasses:: io exception "
					+ file.toString(), ioe);
		}
	}

	/* Instance Attributes */
	public String Name = null;
	public String NameSpace = null;
	 private LinkedList<String> ClassesIllNeed = new LinkedList<String>();
	//transient  rremoved from below attribute by reeta for writing to file
	 private String StartAction = null;
	//transient  rremoved from below attribute by reeta for writing to file
	 private TreeMap<String, ServiceAction> ActionDefinitions = new TreeMap<String, ServiceAction>();
	//transient  rremoved from below attribute by reeta for writing to file
	 private LinkedList<ServiceAction> ActionList = new LinkedList<ServiceAction>();
	//transient  rremoved from below attribute by reeta for writing to file
	 private Servicetype alcML = null;

	/* Class Attributes */
	transient private static TreeMap<String, Class> registeredUserDefinitions = new TreeMap<String, Class>();
	transient private static TreeMap<String, TreeMap<String, ServiceDefinition>> ServiceDefinitions = new TreeMap<String, TreeMap<String, ServiceDefinition>>();
	transient private static TreeMap<String, TreeMap<String, Formtype>> FormDefinitions = new TreeMap<String, TreeMap<String, Formtype>>();
	transient private static TreeMap<String, List<Object>> AlcMapping = new TreeMap<String, List<Object>>();
	transient private static TreeMap<String, String> namespaceMap = new TreeMap<String, String>();
	transient private static boolean __debug = false;
	transient private static TreeMap<String, String> ClassAlias = null;
	transient private static ServiceContextProvider scp = null;
	transient private static LinkedList<ServiceDefinitionListener> ServiceDefinitionListeners = new LinkedList<ServiceDefinitionListener>();
	transient private static LinkedList<File> savedJars = new LinkedList<File>();
	transient private static LinkedList<Long> savedTimes = new LinkedList<Long>();
	transient private static ClassLoader JAXBLoader = null;
	transient private static AseClassLoader JAXBContextLoader = null;
	transient private static TelnetInterfaceUtils tiu = null;
	transient private static boolean isReloadClass = Boolean.TRUE;

	public static boolean isReloadClass() {
		return isReloadClass;
	}

	public static void setReloadClass(boolean isReloadClass) {
		ServiceDefinition.isReloadClass = isReloadClass;
	}

	public static ClassLoader getJAXBLoader() {
		return JAXBLoader;
	}

	public static void setJAXBLoader(ClassLoader jAxbLoader) {
		JAXBLoader = jAxbLoader;
	}
	
	public static void writeApplicationLogicControl(boolean recreateFile) {
		
		 String filename =
             com.baypackets.ase.util.Constants.ASE_HOME + "/alcjars/alc_deploy_tmp/" ;
		 
		ObjectOutputStream outputStream = null;

		try {
			if (logger.isEnabledFor(Level.DEBUG))
			logger.debug("write the Service object to file :" + filename
					+ FILE_SUFIX);
			// Construct the LineNumberReader object

			File objF = new File(filename + FILE_SUFIX);
			if (!objF.exists())
			{
				logger.debug("file object doesn't exist so Recreating File");
				objF.createNewFile();
			}
			else if(recreateFile){
				if (logger.isEnabledFor(Level.DEBUG))
				logger.debug("Recreating File");
				objF.delete();
				objF.createNewFile();
			}

			outputStream = new ObjectOutputStream(new FileOutputStream(filename
					+ FILE_SUFIX));

			if (logger.isEnabledFor(Level.DEBUG))
			logger.debug("Writing Service Definition " + ServiceDefinitions
					+ " , " + " Service Definition Listeners "
					+ ServiceDefinitionListeners + " , " + " ALC Mappings  :"
					+ AlcMapping + " to " + filename + FILE_SUFIX);
			outputStream.writeObject(ServiceDefinitions);
			outputStream.writeObject(ServiceDefinitionListeners);
			// outputStream.writeObject(FormDefinitions);
			outputStream.writeObject(AlcMapping);

		} catch (FileNotFoundException ex) {
			logger.error("Error writing  the Service object to file :" + ex);
		} catch (IOException ex) {
			logger.error("Error writing  the Service object to file :" + ex);
		} finally {
			// Close the ObjectOutputStream
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	  
	public static boolean readApplicationLogicControl() {

		
		String filename =
            com.baypackets.ase.util.Constants.ASE_HOME + "/alcjars/alc_deploy_tmp/" ;
		
		ObjectInputStream outinputStream = null;

		try {

			if (logger.isEnabledFor(Level.DEBUG))
			logger.debug("read the Service object from  file :" + filename
					+ FILE_SUFIX);
			// Construct the LineNumberReader object

			File objF = new File(filename + FILE_SUFIX);
			if (!objF.exists()) {
				if (logger.isEnabledFor(Level.DEBUG))
				logger
						.debug("No file Exists seems to be first time deployment:"
								+ filename + FILE_SUFIX);
				return false;
			}

			outinputStream = new ObjectInputStream(new FileInputStream(filename
					+ FILE_SUFIX));
			Object obj = null;

			int i = 0;
			if (logger.isEnabledFor(Level.DEBUG))
			logger.debug("Reading Service objects  from  :" + filename
					+ FILE_SUFIX);

			while ((obj = outinputStream.readObject()) != null) {

				if (logger.isEnabledFor(Level.DEBUG))
				logger.debug("Reading object " + obj);

				if (obj instanceof LinkedList<?>) {
					ServiceDefinitionListeners = (LinkedList) obj;
					
					if (logger.isEnabledFor(Level.DEBUG))
					logger.debug("Service Definition Listeners "
							+ ServiceDefinitionListeners + " are fetched  :");

				}
				if (obj instanceof TreeMap<?, ?>) {
					TreeMap tm = (TreeMap) obj;
					Entry<?, ?> e = tm.firstEntry();

					if (e != null && e.getValue() instanceof TreeMap) {
						ServiceDefinitions = tm;
						
						if (logger.isEnabledFor(Level.DEBUG))
						logger.debug("Service Definition  "
								+ ServiceDefinitions + " are fetched   :");

					}
					if (e != null && e.getValue() instanceof List) {
						AlcMapping = tm;
						if (logger.isEnabledFor(Level.DEBUG))
						logger.debug("ALC Mappings  " + AlcMapping
								+ " are fetched  :");

					}

				}
				i++;
				
				if (logger.isEnabledFor(Level.DEBUG))
				logger.debug("Read the " + i + " objects");
				if (i == 3)
					return true;
			}

		} catch (ClassNotFoundException ex) {
			logger.error("Error Reading the Service object ffrom file :" + ex);
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			logger.error("Error Reading the Service object ffrom file :" + ex);
			ex.printStackTrace();
		} catch (IOException ex) {
			logger.error("Error Reading the Service object ffrom file :" + ex);
			ex.printStackTrace();
		} finally {
			// Close the ObjectOutputStream
			try {
				if (outinputStream != null) {
					outinputStream.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	public static boolean serviceDefinitionFileExist(){

		String filename =
            com.baypackets.ase.util.Constants.ASE_HOME + "/alcjars/alc_deploy_tmp/" ;
		
		boolean isfileExist =true;
		ObjectInputStream outinputStream = null;

		
			if (logger.isEnabledFor(Level.DEBUG))
			logger.debug("read the Service object from  file :" + filename
					+ FILE_SUFIX);
			
			File objF = new File(filename + FILE_SUFIX);
			if (!objF.exists()) {
				if (logger.isEnabledFor(Level.DEBUG))
				logger
						.debug("No service Definition file Exists :"
								+ filename + FILE_SUFIX);
				isfileExist=false;
			}
			return isfileExist;
//	/	}
		
		
	}
	  private static final String FILE_SUFIX="ServiceDefinitions_Objects.ser";
	
}

