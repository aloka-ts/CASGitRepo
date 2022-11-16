package com.baypackets.ase.sbb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.io.InputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload; 

import com.baypackets.ase.util.AseStrings;

import java.util.Iterator;
import java.util.List;

public class WebEvent extends SBBEvent {

	private ServletOutputStream stream;
	private HttpServletRequest req;
	private HttpServletResponse res;
        private ServletContext sContext ; 
    private final String TEXT_XML_CONTENT="text/xml";
        private static Logger logger = Logger.getLogger(WebEvent.class); 
	
	public WebEvent(HttpServletRequest req ,HttpServletResponse res,ServletContext ctx) {
		this.req =req;
		this.res =res;
                this.sContext =ctx; 
	}

	public Map getParameterMap() {
		return req.getParameterMap();
	}
	
       public HttpServletRequest getServletRequest(){
                return req; 
         } 
	
	public String getParameter(String name){
		return this.req.getParameter(name);
	}

        public  Enumeration getParameterNames() {
		return this.req.getParameterNames();
	}

	
	public  String[] getParameterValues(String name) {
		return this.req.getParameterValues(name);
	}	
	
	public Object getAttribute(String name){
		return this.req.getAttribute(name);
	}
	
	
	public void setAttribute(String name,Object value){
		 this.req.setAttribute(name,value);
	}

       public Enumeration  getAttributeNames(){
		
		return this.req.getAttributeNames();
	}
	 

       public InputStream getInputStream(){
		
		InputStream stream =null;
		try {
			stream= this.req.getInputStream();
		} catch (IOException e) {
	         logger.error(e.getMessage(), e);	
                 }
		return stream;
	}
	
	
	public int getContentLength(){
		return this.req.getContentLength();
	}
	
	
	public String getContentType(){
		return this.req.getContentType();
	}
	
	public HttpSession getSession(){
		return this.req.getSession();
	} 	
	
	public void postVxmlFile(String url){
		
            if(logger.isDebugEnabled())
                {
                        logger.debug("<SBB> The vxml file url is..."+url);
                }
 

	    BufferedInputStream bis = null;
	    ServletOutputStream out = null;

	    try {

	    if(url.startsWith(AseStrings.PROTOCOL_HTTP)|| url.startsWith(AseStrings.PROTOCOL_FILE)){	
                
                URL url1 = new URL (url);  
	    	//HttpURLConnection urlCon = (HttpURLConnection) url1.openConnection ();  
	    	//InputStream fis = urlCon.getInputStream();
	    	
                InputStream fis = url1.openStream();  
                 bis = new BufferedInputStream(fis);	
	    	 
	    	 res.setContentType(TEXT_XML_CONTENT); 
	
       	   }else{
	  
               // Load the VXML file
	        File vxml = new File(url);
	        FileInputStream fis = new FileInputStream(vxml);
			
	        bis = new BufferedInputStream(fis);

	        // Let the browser know that XML is coming
	     
	        res.setContentType(TEXT_XML_CONTENT);
	        res.setContentLength((int)vxml.length());

               }
              out = res.getOutputStream(); 
	        // Output the VXML file 
	        int readBytes = 0;
	     
				while ((readBytes = bis.read()) != -1) {
				  // output the VXML
				  out.write(readBytes);
				}
	      		
	      }catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
		             logger.error(e.getMessage(), e);	
                         }finally {
	        if (out != null)
				try {
					out.close();
					if (bis != null) bis.close();
				} catch (IOException e) {
			logger.error(e.getMessage(), e);	
                                }
	        
	      }
	    }
                

           public  void loadData(){
	
               HttpServletRequest req= this.req; 	
		org.apache.commons.fileupload.servlet.ServletFileUpload sfu = new ServletFileUpload();
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		
		if (!isMultipart) {
       
              if(logger.isDebugEnabled())
                {
                        logger.debug("<SBB> No File to upload ...");
                }
	
		} else {
		
			File savedFile =null;
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items = null;
	
			try {
			items = upload.parseRequest(req);
	            //  System.out.println("items: "+items);
			} catch (FileUploadException e) {
	              logger.error(e.getMessage(), e); 		
                       }
			
			Iterator itr = items.iterator();
		
			
			while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
					
			     if (item.isFormField()){
                          
                              if(logger.isDebugEnabled())
                           {
                                logger.debug("<SBB> No File to upload ...");
                           }
			

			               	        String name = item.getFieldName();
					//	System.out.println("name: "+name);
						String value = item.getString();
						
						req.setAttribute(name, value);
					//	System.out.println("value: "+value);
					} else {
						
						  if(logger.isDebugEnabled())
                                                 {
                                                  logger.debug("<SBB>  File  upload  from post data ...");
                                                  }
							try {
							
							String itemName = item.getName();
							String name = item.getFieldName();

                                                        if(logger.isDebugEnabled())
                                                        {
                                                         logger.debug("<SBB>  Data file name ."+name + " Path is " +itemName);
                                                        }  
							
							String ext  = itemName.substring(itemName.lastIndexOf(AseStrings.PERIOD)+1);
							
						 if(logger.isDebugEnabled())
                                                        {
                                                         logger.debug("<SBB>  File EXT is "+ ext );
                                                        }	
							byte[] b =item.get();
							
		                                        
                                                        String webInf = this.sContext.getResource("/WEB-INF").getPath();
                                                      
                                                       if(logger.isDebugEnabled())
                                                        {
                                                         logger.debug("<SBB> WEB-INF path is " +webInf);
                                                        } 
                                                        savedFile = new File(webInf,"recordings");
                                                        savedFile.mkdir();
                                                        savedFile =new File(savedFile,name+"."+ext);
  					
							
							FileOutputStream fos = new FileOutputStream(savedFile);
							
							fos.write(b);
							 
						        req.setAttribute(name, savedFile.getAbsolutePath());	
						
                                                     if(logger.isDebugEnabled())
                                                        {
                                                         logger.debug("<SBB>  File uploaded "+ savedFile );
                                                        }
					
						}catch(Exception e ){
						logger.error(e.getMessage(), e);	
						}
					}
			   }//while end
			
		 } //else end
	} 
  
             

}

