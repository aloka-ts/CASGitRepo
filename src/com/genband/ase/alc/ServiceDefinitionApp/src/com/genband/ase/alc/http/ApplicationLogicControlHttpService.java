package com.genband.ase.alc.http ;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.servlet.sip.SipApplicationSession;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.http.HttpServiceContextProvider;
import com.genband.sip.ServiceDefinitionApp.SipServletALC;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class ApplicationLogicControlHttpService extends javax.servlet.http.HttpServlet 
{
	private static final long serialVersionUID = -4629937833502893124L;
	private Logger logger =  Logger.getLogger(ApplicationLogicControlHttpService.class.getName());
        private static String OP_TYPE="OP_TYPE";
	

	protected void doPost(HttpServletRequest req,HttpServletResponse res)
	{
		try
		{
		int opType =0;	
                  if(req.getAttribute(OP_TYPE)!=null)
		         	opType=1;
		          else
			      opType=2;

                  ServiceDefinition sd =null;
                  if(opType==1) 
                       sd = ServiceDefinition.getServiceDefinition("__" + getALCNameSpace(), "do-get" );
                  else if(opType==2)
                      sd = ServiceDefinition.getServiceDefinition("__" + getALCNameSpace(), "do-post" );
                  

  
			if (sd == null)
			{
				logger.log(Level.DEBUG, "No service found for " + getALCNameSpace() + ":: do-get"  );
				super.doGet(req,res);
				return;
			}
                ServiceContext  sdContext = new ServiceContext();
                
                if(opType == 2)
                	loadPostedData(req);
              
               if( logger.isDebugEnabled())
                logger.log(Level.DEBUG, "The HTTP REQ is  " + req );
               
               
               if( logger.isDebugEnabled())
                   logger.log(Level.DEBUG, "The HTTP REQ param is  " + req.getParameterMap() );
               
                
				HttpServiceContextProvider sscp = new HttpServiceContextProvider(getServletContext(), req, res ,sdContext);
				sdContext.addServiceContextProvider(sscp);
				sd.execute(sdContext);
			
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Exception while processing request for namespace " + getALCNameSpace() + "::" + req.getMethod());
		}
	}

	protected void  doGet(HttpServletRequest req,HttpServletResponse res)
	{
               req.setAttribute(OP_TYPE, "1"); 
               doPost(req,res); 	
        }


	public String getALCNameSpace()
	{
		if (namespace == null)
			namespace = getServletContext().getInitParameter("ALCNameSpace");
		return namespace;
	}
	
	
	  public  void loadPostedData(HttpServletRequest req ){
      	
      	if(logger.isDebugEnabled())
          {
                  logger.debug("loading posted parameters ...");
          }
	
		org.apache.commons.fileupload.servlet.ServletFileUpload sfu = new ServletFileUpload();
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		
		if (!isMultipart) {
     
            if(logger.isDebugEnabled())
              {
                      logger.debug("No data to upload ...");
              }
	
		} else {
		
			File savedFile =null;
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items = null;
	
			try {
			items = upload.parseRequest(req);
	           
			} catch (FileUploadException e) {
	              logger.error(e.getMessage(), e); 		
                     }
			
			Iterator itr = items.iterator();
		
			
			while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
					
			     if (item.isFormField()){
                        
                       String name = item.getFieldName();
				
						String value = item.getString();
						
						req.setAttribute(name, value);
					
						
						if(logger.isDebugEnabled())
                      {
                           logger.debug("<SBB> loading parameters : name : "+ name +" Value : "+value);
                      } 
					} 
			   }//while end
			
		 } //else end
	} 


	private String namespace = null;
}


