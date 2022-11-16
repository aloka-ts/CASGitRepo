/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/
package com.baypackets.ase.sysapps.cim.receiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.cim.util.Configuration;
import com.baypackets.ase.sysapps.cim.util.Constants;

/**
 * This class extends HttpServlet and provide support for file upload download and delete HTTP API in CIM application.
 * @author Amit Baxi 
 */
public class CIMHTTPServlet  extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 71236783125465346L;
	
	// CIMHTTPServlet Constants
	private static final String[] SUPPORTED_FILE_TYPES={"image","audio","video","rawfile"};
	private static final String PART_ACONYX_USERNAME="AconyxUserName";
	private static final String PART_FILE_TYPE="FileType";
	private static final String PART_FILE_EXT="FileExtension";
	private static final String PART_FILE_CONTENT="FileContent";
	private static final String PARAM_FILE_TYPE="fileType";
	private static final String PARAM_FILE_NAME="fileName";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
	private static Logger logger=Logger.getLogger(CIMHTTPServlet.class);
	private static Configuration config=Configuration.getInstance();
	

	

	@Override
	public void init(ServletConfig servletConfig)throws ServletException{
		if(logger.isDebugEnabled()){
			logger.debug("[CIM] init(ServletConfig): enter");
		}
		super.init(servletConfig);
		String baseDirectory=config.getParamValue(Constants.PROP_BASE_UPLOAD_DIR);
		for(String fileType:SUPPORTED_FILE_TYPES){
			String dirPath=baseDirectory+File.separator+fileType;
			File dir=new File(dirPath);
			if(!dir.exists()){
				if(logger.isInfoEnabled()){
					logger.info("[CIM] Creating directory"+dirPath);
				}
				try{
					boolean result=dir.mkdir();
					if(!result){
						logger.error("[CIM] Failed to create directory"+dirPath);
					}
				}catch (SecurityException e) {
					logger.error("[CIM] Exception in mkdir():"+dirPath+":",e);
				}
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug("[CIM] init(ServletConfig): exit");
		}
	}
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if(logger.isInfoEnabled()){
			logger.info("[CIM] doGet():enter");
		}
		String requestURI=req.getRequestURI();
		if(requestURI.contains("/upload")){
			resp.sendError(405, "Use POST method for uploading files.");
		}
		else if(requestURI.contains("/download")){
			handleDownloadRequest(req,resp);
			return;
		}
		else if(requestURI.contains("/delete")){
			handleDeleteRequest(req,resp);
		}
		if(logger.isInfoEnabled()){
			logger.info("[CIM] doGet():exit");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if(logger.isInfoEnabled()){
			logger.info("[CIM] doPost():enter");
		}
		if(req.getRequestURI().contains("/download")){
			resp.sendError(405, "POST method can be used for uploading files only. Use GET method for downloading files.");
		}
		if(req.getRequestURI().contains("/delete")){
			resp.sendError(405, "POST method can be used for uploading files only. Use GET method for deleting files.");
		}
		else if(!isMultipartContent(req)){
			resp.sendError(500, "The request doesn't contain a multipart/form-data or multipart/mixed stream");
		}
		else{
			handleUploadRequest(req,resp);
		}
		if(logger.isInfoEnabled()){
			logger.info("[CIM] doPost():exit");
		}
	}

	/**
	 * This method will handle HTTP requests for file upload.
	 * <p>POST method will be used for uploading file. </p>
	 * 	Request Part:- AconyxUserName(String),FileType(String),FileExtension(String),FileContent(bytes)
	 * 	<pre>URL: http://ip:port/CIM/upload</pre>
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleUploadRequest(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		if(logger.isInfoEnabled()){
			logger.info("[CIM] handleUploadRequest():enter");
		}
		String aconyxUserName=null;
		String fileType=null;
		String fileExtension=null;
		String fileName=null;
		String downloadUrl=null;
		String deleteUrl=null;
		try{
		if(req.getPart(PART_ACONYX_USERNAME)!=null)
		{
			aconyxUserName=this.convertToString(req.getPart(PART_ACONYX_USERNAME).getInputStream());
		}else{
			resp.sendError(400, "Part "+PART_ACONYX_USERNAME+" is mandatory");
			return;
		}
		if(req.getPart(PART_FILE_TYPE)!=null)
		{
			fileType=this.convertToString(req.getPart(PART_FILE_TYPE).getInputStream());
			fileType=fileType.trim().toLowerCase();
			if(!isSupportedFileType(fileType)){
				resp.sendError(400, "FileType "+fileType+" not supported. Supported types are:"+Arrays.toString( SUPPORTED_FILE_TYPES ));
				return;			
			}
		}else{
			resp.sendError(400, "Part "+PART_FILE_TYPE+" is mandatory");
			return;
		}
		if(req.getPart(PART_FILE_EXT)!=null)
		{
			fileExtension=this.convertToString(req.getPart(PART_FILE_EXT).getInputStream());
		}else{
			resp.sendError(400, "Part "+PART_FILE_EXT+" is mandatory");
			return;
		}

		String path=null;
		if(req.getPart(PART_FILE_CONTENT)!=null){
			path = null;	
			fileName = aconyxUserName+"_"+ sdf.format(new Date()) + "."+fileExtension;
			String baseDirectory=config.getParamValue(Constants.PROP_BASE_UPLOAD_DIR);
			path = baseDirectory +File.separator+fileType+File.separator+fileName;
			req.getPart(PART_FILE_CONTENT).write(path);
			if(logger.isInfoEnabled()){
				logger.info("[CIM] Uploaded file:["+fileName+"] AconyxUserName:["+aconyxUserName+"] FileType :["+fileType+"]");
			}
			resp.setContentType("text/xml");
			StringBuffer buffer=req.getRequestURL();		
			int index=buffer.lastIndexOf("/");		
			downloadUrl=buffer.substring(0, index)+"/download?"+PARAM_FILE_TYPE+"="+fileType+"&"+PARAM_FILE_NAME+"="+fileName;	
			deleteUrl=buffer.substring(0, index)+"/delete?"+PARAM_FILE_TYPE+"="+fileType+"&"+PARAM_FILE_NAME+"="+fileName;	
			resp.getWriter().println("<Response><FileName>"+fileName+"</FileName><Operation>Upload</Operation><Status>Success</Status><DownloadURL>"+downloadUrl+"</DownloadURL><DeleteURL>"+deleteUrl+"</DeleteURL></Response>");
		}else{
			resp.sendError(400, "Part "+PART_FILE_CONTENT+" is mandatory");
			return;
		}
		}catch(IllegalStateException e){
			logger.error("[CIM] IllegalStateException in handleUploadRequest():",e);
			String message=e.getCause().getMessage();
			if(message==null){
				message=e.getMessage();
			}
			resp.sendError(500,message);
		}
	}

	/**
	 * This method will handle HTTP requests for file download.
	 * <p>GET method will be used for downloading file.</p>
	 * For Download
	 * <pre>URL: http://ip:port/CIM/download/image?fileName=...jpg</pre>
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleDownloadRequest(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		if(logger.isInfoEnabled()){
			logger.info("[CIM] handleDownloadRequest():enter");
		}
		String fileType=req.getParameter(PARAM_FILE_TYPE);
		if(fileType==null){
			if(logger.isDebugEnabled()){
				logger.debug("[CIM] Sending 400 response "+PARAM_FILE_TYPE+" parameter missing in request");
			}
			resp.sendError(400, "Parameter "+PARAM_FILE_TYPE+" is mandatory");
			return;
		}
		String fileName=req.getParameter(PARAM_FILE_NAME);
		if(fileName==null){
			if(logger.isDebugEnabled()){
				logger.debug("[CIM] Sending 400 response "+PARAM_FILE_NAME+" parameter missing in request");
			}
			resp.sendError(400, "Parameter "+PARAM_FILE_NAME+" is mandatory");
			return;
		}
		if(!isSupportedFileType(fileType)){
			if(logger.isDebugEnabled()){
				logger.debug("[CIM] Sending 404 response filename not found");
			}
			resp.sendError(404, "File: "+fileName+" not found on server");
			return;			
		}
		String baseDirectory=config.getParamValue(Constants.PROP_BASE_UPLOAD_DIR);
		String filePath=baseDirectory+File.separator+fileType+File.separator+fileName.trim();
		File file=new File(filePath);
		if(file.exists()){
			FileInputStream fileToDownload=null;
			ServletOutputStream responseStream=resp.getOutputStream();
			try{
				resp.setContentType("application/x-download");
				resp.setHeader("Content-Disposition", "attachment; filename="+fileName);
				fileToDownload = new FileInputStream(file);
				resp.setContentLength(fileToDownload.available());

				byte[] bufferData = new byte[1024];
				int read=0;
				while((read = fileToDownload.read(bufferData))!= -1){
					responseStream.write(bufferData, 0, read);
				}
				responseStream.flush();
			}catch (Exception e) {
				logger.error("[CIM] Exception in doGet():",e);
				resp.sendError(500, "Internal Server Errror");
			}finally{
				responseStream.close();
				fileToDownload.close();
			}    

		}else{
			if(logger.isDebugEnabled()){
				logger.debug("[CIM] Sending 404 response file not exists:"+fileName);
			}
			resp.sendError(404, "File Not Exists:"+fileName);
			return;	
		}

	}

	/**
	 * This method will handle HTTP requests for file deletion
	 * <p>GET method will be used for deleting file.</p>
	 *  For Delete
	 * <pre>URL: http://ip:port/CIM/delete/image?fileName=...jpg</pre>
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleDeleteRequest(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		if(logger.isInfoEnabled()){
			logger.info("[CIM] handleDeleteRequest():enter");
		}
		String fileType=req.getParameter(PARAM_FILE_TYPE);
		if(fileType==null){
			if(logger.isDebugEnabled()){
				logger.debug("[CIM] Sending 400 response "+PARAM_FILE_TYPE+" parameter missing in request");
			}
			resp.sendError(400, "Parameter "+PARAM_FILE_TYPE+" is mandatory");
			return;
		}
		String fileName=req.getParameter(PARAM_FILE_NAME);
		if(fileName==null){
			if(logger.isDebugEnabled()){
				logger.debug("[CIM] Sending 400 response "+PARAM_FILE_NAME+" parameter missing in request");
			}
			resp.sendError(400, "Parameter "+PARAM_FILE_NAME+" is mandatory");
			return;
		}
		if(!isSupportedFileType(fileType)){
			if(logger.isDebugEnabled()){
				logger.debug("[CIM] Sending 404 response filename not found");
			}
			resp.sendError(404, "File: "+fileName+" not found on server");
			return;			
		}
		String baseDirectory=config.getParamValue(Constants.PROP_BASE_UPLOAD_DIR);
		String filePath=baseDirectory+File.separator+fileType+File.separator+fileName.trim();
		File file=new File(filePath);
		if(file.exists()){
			if(logger.isInfoEnabled()){
				logger.info("[CIM] Deleting file:"+filePath);
			}
			try{
				boolean result=file.delete();
				if(!result){
					logger.error("[CIM] Failed to delete file"+filePath);
					resp.sendError(500, "Error occured while deleting file:"+fileName);
				}else{
					resp.setContentType("text/xml");
					resp.getWriter().println("<Response><FileName>"+fileName+"</FileName><Operation>Delete</Operation><Status>Success</Status></Response>");
				}

			}catch (SecurityException e) {
				logger.error("[CIM] Exception in delete():"+filePath+":",e);
				resp.sendError(500, "Error occured while deleting file:"+fileName);
			}
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("[CIM] Sending 404 response file not exists:"+fileName);
			}
			resp.sendError(404, "File Not Exists:"+fileName);
		}
		if(logger.isInfoEnabled()){
			logger.info("[CIM] handleDeleteRequest():exit");
		}
	}

	/**
	 * This method will convert a input stream in to string.
	 * @param inputStream stream for which method is called.
	 * @return
	 */
	private String convertToString(InputStream inputStream){
		Scanner s =new Scanner(inputStream);
		StringBuilder builder=new StringBuilder();
		try {
			//first use a Scanner to get each line
			while ( s.hasNextLine() ){
				builder.append(s.nextLine());
			}
		} finally{
			s.close();
		}
		return builder.toString();
	}

	/**
	 * This method will be used to check weather request is a multi part request or not.
	 * @param request request for which method is called.
	 * @return
	 */
	private boolean isMultipartContent(HttpServletRequest request)
	{
		String contentType =request.getContentType();
		if (contentType == null) {
			return false;
		}
		return contentType.toLowerCase().startsWith("multipart/");
	}

	private boolean isSupportedFileType(String fileType){
		boolean supported=false;
		if(fileType!=null){
			for(String type:SUPPORTED_FILE_TYPES){
				if(type.equals(fileType)){
					supported=true;
					break;
				}					
			}
		}
		return supported;
	}
}
