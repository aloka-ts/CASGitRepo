package com.genband.ase.alc.http ;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;


import com.genband.ase.alc.alcml.ALCServiceInterface.*;

import com.genband.ase.alc.alcml.jaxb.*;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.genband.ase.alc.http.HttpServiceContextProvider;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
 @ALCMLActionClass(
         name="Http Servlet ALC Extensions"
                 )
public class HttpServletALC extends ALCServiceInterfaceImpl
{
        static Logger logger = Logger.getLogger(HttpServletALC.class.getName());
        private final static String Name = "HttpServletALC";

    public String getServiceName() { return Name; }

        @ALCMLActionMethod( name="get-http-parameter", isAtomic=true, help="gets paramter from http request\n", asStatic=true)
        static public void GetParameter(ServiceContext sContext,
        		                   @ALCMLMethodParameter(	name="name",
				                   asAttribute=true,
				                   required=true,
				                   help="name of parameter\n")
					               String name,
                                  @ALCMLMethodParameter(  name="results-in",
                                                                        asAttribute=true,
                                                                        required=true,
                                                                        help="place to store results.\n")
                                                                                String ResultsIn ) throws ServiceActionExecutionException
        {
        	
                HttpServletRequest req = (HttpServletRequest)sContext.getAttribute(HttpServiceContextProvider.Request);
                if (req != null)
                {
                	if (logger.isDebugEnabled()){
                		logger.debug(" Getting http parameter from http request " +req);
                	}
                	
                	if(req.getAttribute(name)!=null){ //for getting parameters from POST request which got already loaded by loadPostedData() by Adaptor
                		
                		if (logger.isDebugEnabled()){
                    		logger.debug(" Getting http parameter from http request Attribute " +name);
                    	}
                		String value = (String) req.getAttribute(name);
                		sContext.setAttribute(ResultsIn, value);
                		
                	}else {
                		
                		
                        String param = req.getParameter(name);
                        
                        if (logger.isDebugEnabled())
                    		logger.debug(" Getting http parameter from http request name " +name+" Value : "+param);
                        sContext.setAttribute(ResultsIn, param);
                        
                	}
                }
        	     
                sContext.ActionCompleted();
        }


        
        @ALCMLActionMethod( name="display-page", isAtomic=true, help="posts the html page e.g on doGet \n", asStatic=true)
        static public void DisplayPage(ServiceContext sContext,
        		                   @ALCMLMethodParameter(	name="path",
				                   asAttribute=true,
				                   required=true,
				                   help="path of the html page to display it can be file/http\n")String url){
        	
      HttpServletResponse res = (HttpServletResponse)sContext.getAttribute(HttpServiceContextProvider.Response);
    		
            if(logger.isDebugEnabled())
                {
                        logger.debug("The file to display is..."+url);
                }
 

	    BufferedInputStream bis = null;
	    ServletOutputStream out = null;

	    try {

	    if(url.startsWith("http")|| url.startsWith("file")){	
                
                URL url1 = new URL (url);  
                InputStream fis = url1.openStream();  
                 bis = new BufferedInputStream(fis);	
	    	 
	    	 res.setContentType("text/xml"); 
	
       	   }else{
	  
               // Load the html file
	        File html = new File(url);
	        FileInputStream fis = new FileInputStream(html);
			
	        bis = new BufferedInputStream(fis);

	        // Let the browser know that XML is coming
	     
	        res.setContentType("text/xml");
	        res.setContentLength((int)html.length());

               }
              out = res.getOutputStream(); 
	        // Output the html file 
	        int readBytes = 0;
	     
				while ((readBytes = bis.read()) != -1) {
				  // output the html
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
                

      
}
