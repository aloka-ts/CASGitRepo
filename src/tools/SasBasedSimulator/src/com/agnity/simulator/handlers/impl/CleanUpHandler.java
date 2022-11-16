package com.agnity.simulator.handlers.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.CleanUpNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;

public class CleanUpHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(CleanUpHandler.class);
	private static Handler handler;
	private static final String CDR_AT_SERVER="cdrserveradress";
	private static final String CDR_VALUESTOVERIFY="cdrvalues";
	private static final String USERNAME="username";
	private static final String PASSWORD="password";
	private static final String PATH="path";
	private static final String FILENAME="filename";
	
	
	private static String fileName = "";
	

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (CleanUpHandler.class) {
				if(handler ==null){
					handler = new CleanUpHandler();
				}
			}
		}
		return handler;
	}

	private CleanUpHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside CleanUpHandler processNode()");

		if(!(node.getType().equals(Constants.CleanUp_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		String localfilepath=null;
		
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		
		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Boolean cdrVerify = false;
		
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(CDR_AT_SERVER))
				{
					String serveradress = fieldElem.getValue(varMap);
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String username = subFieldElems.get(USERNAME).getValue(varMap);
					String password = subFieldElems.get(PASSWORD).getValue(varMap);
					String path = subFieldElems.get(PATH).getValue(varMap);
					String filename = subFieldElems.get(FILENAME).getValue(varMap);
					
					if(!(path.substring(0).equals("/")))
						path = "/".concat(path);
					if(!(path.endsWith("/")))
						path = path.concat("/");
					
					try{
					    //Connection String
					    URL url = new URL("ftp://"+username+":"+password+"@"+serveradress+path+filename+";type=i");
					   
					    URLConnection con = url.openConnection();
					    logger.debug("URL done");
					  
					    BufferedInputStream in = new BufferedInputStream(con.getInputStream());
					 
					    logger.debug("Downloading file.");
					    
					    FileOutputStream out;
					    String s = System.getProperty("os.name");
						String [] val = s.split(" ");
						
						if(val[0].equalsIgnoreCase("windows"))
						{
							localfilepath ="C:\\" + fileName;
							out = new FileOutputStream(localfilepath);
						}else{
							localfilepath="\\" + fileName;
							out = new FileOutputStream(localfilepath);
						}
					 
					    int i = 0;
					    byte[] bytesIn = new byte[1024];
					    while ((i = in.read(bytesIn)) >= 0) {
					        out.write(bytesIn, 0, i);
					    }
					    out.close();
					    in.close();
					 
					    logger.debug("File downloaded.");
					    					
					
						if(localfilepath!=null)
							{
								File f = new File(localfilepath);
								BufferedReader br = new BufferedReader(new FileReader(f));
								String last,temp;
								temp=br.readLine();
								last =temp;
								while(temp!=null)
								{
									last =temp;
									temp =br.readLine();
								}
								String values[] = last.split(",");
								logger.debug("values read from cdr file are: "+values.length);
								int num=1;
								for(String str:values)
								{
									logger.debug("Field"+num+" "+str);
									num++;
								}
											
								//Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
								String cdrdata = subFieldElems.get(CDR_VALUESTOVERIFY).getValue(varMap);
								String cdrtokens[] = cdrdata.split(",");
								int k=0;
								while(k<cdrtokens.length)
								{
									String cdrvalues[] = cdrtokens[k].split(":");
									if(cdrvalues.length==2)
									{
										if(values[Integer.parseInt(cdrvalues[0])-1].equalsIgnoreCase(cdrvalues[1])){
											cdrVerify =true;
											logger.debug("CDR value at location "+cdrvalues[0]+" matched");
											}else{
											cdrVerify =false;
											logger.debug("CDR value at location "+cdrvalues[0]+" doesn't matched");
											break;
											}										
									}else if(cdrvalues.length==3){
										//if(values[Integer.parseInt(cdrvalues[0])].equalsIgnoreCase(cdrvalues[1]))
											// cdrVerify =true;
										Pattern p = Pattern.compile(cdrvalues[1]);
										Matcher m = p.matcher(values[Integer.parseInt(cdrvalues[0])-1]);
										if(m.find()){
											cdrVerify=true;
											logger.debug("CDR value at location "+cdrvalues[0]+" contains substring "+cdrvalues[1]);
										}else{
											cdrVerify =false;
											logger.debug("CDR value at location "+cdrvalues[0]+" doesn't contains substring "+cdrvalues[1]);
											break;
										}
									}else{
										cdrVerify=false;
										logger.debug("Wrong input in cdrvalues subfield");
										break;
									}
									k++;
								}
							}
					}catch(MalformedURLException murlexcptn)
				    {
				    	logger.debug("Malformed URL Exception>>> "+murlexcptn.toString());
				    }catch(IOException io)
				    {
				    	logger.debug("IOEception>>> "+io.toString());
				    }catch(Exception e)
				    {
				    	logger.debug("excptn is "+ e.toString());
				    	e.printStackTrace();
				    }
			}
				
			}//complete if subelem is field
		}//while complete
						
		if(!cdrVerify)
		{
			logger.info("Leaving CleanUpHandler processNode() with false status as cdr values didn't matched");
			return false;
		}
		
		logger.debug("Going to execute system command");
		String command = ((CleanUpNode)node).getCommand();
		int outputVal = -1;
		if(command!=null)
		{
			outputVal = Helper.systemCall(command);
			logger.debug("value returned from system call is "+outputVal);
			if(outputVal!=0)
				return false;
			try{
				long time = InapIsupSimServlet.getInstance().getConfigData().getPublishingTime();
				time = time * 1000;
				Thread.sleep(time);
				
			}catch(InterruptedException e)
			{
				logger.error("Interrupt Exception while thread was sleepin when publishing going "+e);
			}
		}
				
				
		if(logger.isInfoEnabled())
			logger.info("Leaving CleanUpHandler processNode() with true status");
		return true;
	}
	
	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
				
		if(logger.isInfoEnabled())
			logger.info("Leaving CleanUpHandler processRecievedMessage() with false status  as receive mode is not supported");
		//will always work in send mode
		return false;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		//can't be true as will always work in send mode
		if(logger.isInfoEnabled())
			logger.info("Leaving CleanUpHandler validateMessage() with false status  as receive mode is not supported");
		return false;
	}



}
