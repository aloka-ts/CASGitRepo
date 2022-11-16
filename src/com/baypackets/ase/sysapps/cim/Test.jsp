<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.baypackets.ase.sysapps.cim.receiver.CIMSIPServlet" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
<%
		String phoneNumber=request.getParameter("phone");
		String message=request.getParameter("message");
		String messgaeId=request.getParameter("id");
		
		out.println("OK");
		
		CIMSIPServlet cimServlet=new CIMSIPServlet();
		cimServlet.sendMessageToCaller(messgaeId,message);
%>
</body>
</html>