/*
##########################################################################################
#   Objective       : SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#   Propgram		: upgrades the SAS5.5 ase.properties file               
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################
*/

import java.io.*;
import java.util.*;

public class UpgradeProperties {

	private static File oldPropertyFile;
	private static File newPropertyFile;
	private static File updatedPropertyFile;
	private ArrayList remainingProp = new ArrayList();
	private ArrayList changedProp = new ArrayList();
	private Properties oldProperties;
	private Properties newProperties;
	
	
	public static void main(String args[]) {
		if(args.length !=3) {
			System.out.println("Please provide all the properties files.");
			System.exit(0);
		}

		setOldPropertyFile(args[0]);
		setNewPropertyFile(args[1]);	
		setUpdatedPropertyFile(args[2]);	
		UpgradeProperties upgrPro = new UpgradeProperties();
		upgrPro.copyFile();
	}
	
	private void copyFile() {
		System.out.println("copyFile");
		try {
		validate();
		List oldFile = loadFile(oldPropertyFile);
		List newFile = loadFile(newPropertyFile);
		loadProperties();	
		savedProperties(newProperties);
		specificPropertyChange(oldProperties , newProperties);
		overwriteProperties(oldProperties , newProperties);
		updateNameChanged(oldProperties , newProperties);
		writeFile(newFile ,newProperties);
		} catch(Exception ex) {
			System.out.println("Error in copy properties ");
		}
	}

	private static void setOldPropertyFile(String oldFile) {
		System.out.println("setOldPropertyFile");
		oldPropertyFile = new File(oldFile);
	}

	private static void setNewPropertyFile(String newFile) {
		System.out.println("setNewPropertyFile");
		newPropertyFile = new File(newFile);
	}

	private static void setUpdatedPropertyFile(String updatedFile) {
		System.out.println("setUpdatedPropertyFile");
		updatedPropertyFile = new File(updatedFile);
	}
	private void validate() throws Exception {
		System.out.println("validate()");

		if(!oldPropertyFile.canRead()) {
			final String message = "Unable to read from " + oldPropertyFile ;
			throw new IOException(message);
		}

		if(!newPropertyFile.canRead()) {
			final String message = "Unable to read from " + newPropertyFile ;
			throw new IOException(message);
		}

		if(!updatedPropertyFile.exists()) {
			try {
				updatedPropertyFile.createNewFile();
			}
			catch(IOException ex) {
				throw new IOException("could not create updatedPropertyFile");
			}
		}
		System.out.println("leaving validate() ");
	}

	private List loadFile(File file) throws Exception {
		System.out.println("loadFile");

		List data = new ArrayList();
		try {
			BufferedReader bufferReader = new BufferedReader(new FileReader(file));
			String record;
			try {
				while((record = bufferReader.readLine()) != null) {
					record = record.trim();
					data.add(record);
				}	
			}
			catch (Exception ex) {
				throw new Exception("Could not copy data from file"+ file, ex);
			}
			finally {
				bufferReader.close();
			}
		}
		catch (Exception ex) {
				throw new Exception("Could not read file"+ file, ex);
		}
		System.out.println("leaving loadFile");
		return data;
	}

	private void loadProperties() throws Exception{
		System.out.println("loadProperties()");
		oldProperties = new Properties();
		newProperties = new Properties();
		InputStream oldInStream ;	
		InputStream newInStream ;

		try {
			oldInStream = new FileInputStream(oldPropertyFile);	
			newInStream = new FileInputStream(newPropertyFile);	
		
			oldProperties.load(oldInStream);
			newProperties.load(newInStream);
		} catch ( Exception ex) {
			throw new Exception("Unable to load properties from file", ex);
		}
		//finally {
				oldInStream.close();
				newInStream.close();
		//} 
		System.out.println("leaving loadProperties()");
	}
	
	private void savedProperties(Properties newProp) {
		System.out.println("Entering savedProperties");
		Enumeration enumProp = newProp.propertyNames();
		while(enumProp.hasMoreElements()) {
			String key = (String)enumProp.nextElement();
			if(	key.equals("30.1.10")|| 
				key.equals("javax.servlet.sip.supported")||
				key.equals("sas.version") || 
				// Bug BPind 18080
				key.equals("30.3.2") || 
				key.equals("sm.retries") || 
				key.equals("com.baypackets.ase.channel.DataChannelProvider.protocolStack")||
				key.equals("com.baypackets.ase.channel.ControlChannelProvider.protocolStack")) {
					System.out.println("saving Properties"+key);
				// 22 feb
					if ( key.equals("com.baypackets.ase.channel.DataChannelProvider.protocolStack")||
						key.equals("com.baypackets.ase.channel.ControlChannelProvider.protocolStack")) {
						newProp.setProperty(key, "\\");	
					}
					if ( key.equals("sas.version")) {
						newProp.setProperty(key , "5.5");	
					}
					if ( key.equals("sm.retries")) {
						newProp.setProperty(key , "2");	
					}
				changedProp.add(key);	
			} // if ends
		} // while ends
	}

	private void specificPropertyChange(Properties oldProp, Properties newProp) {
		HashMap map = new HashMap();
		HashMap innerMap = new HashMap();

		Enumeration enumProp = newProp.propertyNames();
		while(enumProp.hasMoreElements()) {
			String key = (String)enumProp.nextElement();

			if(key.equals("30.1.58")) {
				System.out.println("percific Properties"+key);
				newProp.setProperty(key,oldProp.getProperty("30.1.24"));	
				changedProp.add(key);
			} else if(key.equals("ft.sas.cluster.name")) {
				System.out.println("specific Properties"+key);
				// TO DO newProp.setProperty(key,oldProp.getProperty());
				changedProp.add(key);
			} // 22 feb else if (key.equals("com.baypackets.ase.channel.DataChannelProvider.protocolStack")) {
			// 	newProp.setProperty(key,oldProp.getProperty("com.baypackets.ase.channel.DataChannelProvider.protocolStack"));	
				//String mulStr = oldProp.getProperty("com.baypackets.ase.channel.DataChannelProvider.protocolStack");
				//map = tokenize(mulStr);
			//	map = tokenize(oldProp.getProperty("com.baypackets.ase.channel.DataChannelProvider.protocolStack"));
			//	map = tokenize(newProp.getProperty("com.baypackets.ase.channel.DataChannelProvider.protocolStack"));
			/* 22 feb } else if (key.equals("com.baypackets.ase.channel.ControlChannelProvider.protocolStack")) {
				newProp.setProperty(key,oldProp.getProperty("com.baypackets.ase.channel.ControlChannelProvider.protocolStack"));
			} */
		}// while ends here	
	}

	/*private HashMap tokenize(String mulStr) {
		HashMap map = new HashMap();
		HashMap innerMap = new HashMap();
				
			System.out.println("PRASHN IS "+mulStr); 
				String[] token = mulStr.split(":");
				for(int i=0;i<token.length;i++) {
					System.out.println("TOKEN IS " +token[i]);
					System.out.println("INDEX IS " + token[i].indexOf("("));
					String name = token[i].substring(0 , token[i].indexOf("("));
					String value = token[i].substring(token[i].indexOf("(")+1 , token[i].indexOf(")"));
					System.out.println("TOKEN KEY IS >> " +name );
					System.out.println("TOKEN VALUE IS >> " + value);
					String[] innerToken = value.split(";");
					for(int y=0;y<innerToken.length;y++) {
						String innerName = innerToken[y].substring(0 , innerToken[y].indexOf("="));
						String innerValue = innerToken[y].substring(innerToken[y].indexOf("=")+1);
						System.out.println("INNER TOKEN IS >> " + innerName);
						System.out.println("INNER TOKEN VALUE IS >> " + innerValue);
						innerMap.put(innerName, innerValue);
					}
					map.put(name , innerMap);	
			}
	
	}*/
	private void overwriteProperties(Properties oldProperties , Properties newProperties ) throws Exception {
		System.out.println("Entering overwriteProperties");
		try {

			Enumeration enumOld = oldProperties.propertyNames();
			while(enumOld.hasMoreElements()) {
				String key = (String)enumOld.nextElement();
				System.out.println("Element is "+ key);
				if(!changedProp.contains(key)) {
					if(newProperties.containsKey(key)) {
						newProperties.setProperty(key , oldProperties.getProperty(key));	
						System.out.println("KEY N VALUE ARE, KEY = "+key + "value = "+oldProperties.getProperty(key));
					} else {
						remainingProp.add(key);
					}	
				}
			}
		}
		catch (Exception ex) {
			throw new Exception("Could not overwrite the properties",ex); 
		}
		System.out.println("leaving overwriteProperties");
	}

	private void updateNameChanged(Properties oldProperties , Properties newProperties ) throws Exception {
		System.out.println("Entering updateNameChanged");
		try {
			Iterator listItr = remainingProp.iterator();
			while(listItr.hasNext()) {
				String propName = (String)listItr.next();	
				
				if(propName.equals("30.1.5")){ 
					System.out.println("mt.container.thread.pool.size");
					newProperties.setProperty("mt.container.thread.pool.size", oldProperties.getProperty(propName));	
				}

				if(propName.equals("30.1.6")){ 
					System.out.println("mt.queue.batch.size");
					newProperties.setProperty("mt.queue.batch.size", oldProperties.getProperty(propName));	
				}

				if(propName.equals("30.1.14")){ 
					System.out.println("sd.use.cache");
					newProperties.setProperty("sd.use.cache", oldProperties.getProperty(propName));	
				}

				if(propName.equals("30.1.20")){ 
					System.out.println("log.measurment.data");
					newProperties.setProperty("log.measurment.data", oldProperties.getProperty(propName));	
				}
				if(propName.equals("30.1.22")){ 
					System.out.println("");
					newProperties.setProperty("sd.hotdeploy", oldProperties.getProperty(propName));	
				}
				if(propName.equals("30.1.23")){ 
					System.out.println("");
					newProperties.setProperty("sd.hotdeploy.poll.interval.sec", oldProperties.getProperty(propName));	
				}
				if(propName.equals("68.4.1")){ 
					System.out.println("");
					newProperties.setProperty("30.1.34", oldProperties.getProperty(propName));	
				}
				if(propName.equals("68.1.3")){ 
					System.out.println("");
					newProperties.setProperty("30.1.35", oldProperties.getProperty(propName));	
				}
				if(propName.equals("TomcatRequired")){ 
					System.out.println("");
					newProperties.setProperty("http.container", oldProperties.getProperty(propName));	
				}
				/*if(propName.equals("default.start.port")){ 
					System.out.println("");
					newProperties.setProperty("com.baypackets.ase.channel.DataChannelProvider.port", oldProperties.getProperty(propName));	
				}*/
				if(propName.equals("log.max.filesize")){ 
					System.out.println("");
					newProperties.setProperty("log.filesize.max.kb", oldProperties.getProperty(propName));	
				}
				if(propName.equals("log.max.backups")){ 
					System.out.println("");
					newProperties.setProperty("log.filecount.max", oldProperties.getProperty(propName));	
				}
				if(propName.equals("pdu.max.filesize")){ 
					System.out.println("");
					newProperties.setProperty("log.pdu.filesize.max.kb", oldProperties.getProperty(propName));	
				}
				if(propName.equals("pdu.max.backups")){ 
					System.out.println("");
					newProperties.setProperty("log.pdu.filecount.max", oldProperties.getProperty(propName));	
				}
				if(propName.equals("trusted.nodes")){ 
					System.out.println("");
					newProperties.setProperty("sip.trusted.nodes", oldProperties.getProperty(propName));	
				}
				if(propName.equals("NonEmsServiceMgmt")){ 
					System.out.println("");
					newProperties.setProperty("sd.nonems.service.mgmt.mechanism", oldProperties.getProperty(propName));	
				}
			}
		}
		catch (Exception ex) {
			throw new Exception("Could not overwrite the properties",ex); 
		}
		System.out.println("Leaving updateNameChanged");
	}

	private void writeFile(List data ,Properties newProp) throws Exception {
		System.out.println("Entering writeFile");
		try {
			Iterator itr = data.iterator();
			FileOutputStream out = new FileOutputStream(updatedPropertyFile);
			PrintStream ps = new PrintStream(out);
			try {
                while(itr.hasNext()) {
                    String line = (String)itr.next();
                    if(!line.startsWith("#")) {
                        if(line.indexOf("=")!= -1) {
                            int index = line.indexOf("=");
                            String propName = line.substring(0,index);
                            if(!( propName.equals("TCP(bind_addr") ||
                                propName.equals("TCPPING(initial_hosts") ||
                                propName.equals("FD(timeout") ||
                                propName.equals("pbcast.NAKACK(down_thread") ||
                                propName.equals("pbcast.STABLE(desired_avg_gossip") ||
                                propName.equals("pbcast.GMS(join_timeout")) ){
                                    String propValue = newProp.getProperty(propName);
                                    line = (propName + "=" + propValue);
                                    System.out.println("PROP.OUT IS"+line);
                            }
                        }
                    }
					else {
						if(line.indexOf("sd.nonems")!=-1) {
							int index = line.indexOf("sd.nonems");
							line = line.substring( index , line.length()); 	
						}
					}
                    ps.println(line);
                }

			}
			catch(Exception ex) {
				throw new Exception("Unable to write to file",ex);
			}
			finally {
				out.close();	
			}
		} catch (Exception ex) {
				throw new Exception("writeFile failed",ex);
		}
		System.out.println("Leaving writeFile");
	}

}
