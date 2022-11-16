package com.baypackets.ase.ftjdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.transaction.UserTransaction;

/**
 * Servlet implementation class for Servlet: DataAccessTestServlet
 *
 */
 public class DataAccessTestServlet extends javax.servlet.sip.SipServlet implements javax.servlet.Servlet {
    /* (non-Java-doc)
	 * @see javax.servlet.sip.SipServlet#SipServlet()
	 */
	public DataAccessTestServlet() {
		super();
	}   	

	public void init(ServletConfig srvletConfigRef) {
		this.srvletConfig = srvletConfigRef;
		// Prepare properties
		Properties props = new Properties();
		Enumeration initParamNames = srvletConfig.getInitParameterNames();
		while (initParamNames.hasMoreElements()) {
			String paramName = (String)initParamNames.nextElement();
			String paramValue = srvletConfig.getInitParameter(paramName);
			props.setProperty(paramName, paramValue);
			System.out.println("Set property ::" + paramName + "=" + paramValue);
		}
		
		DataAccess dataAccessRef = (DataAccess)srvletConfig.getServletContext().
			getAttribute("DATA_ACCESS");
		
		if (dataAccessRef == null) {
			dataAccessRef = DataAccess_Impl.getInstance();
			dataAccessRef.initialize(props);
			srvletConfig.getServletContext().setAttribute("DATA_ACCESS", dataAccessRef);
			System.out.println("DATA_ACCESS reference set to ::" + dataAccessRef.toString());
		}
		else {
			System.out.println(
					"DATA_ACCESS reference already set to ::" + dataAccessRef.toString());
		}
	}
	  	
	  	
	  	
	
	protected void doInvite(SipServletRequest request) throws ServletException, IOException {
		ServletContext srvletContext = srvletConfig.getServletContext();
		
//		Enumeration attribNames = srvletContext.getAttributeNames();
//		while (attribNames.hasMoreElements()) {
//			System.out.println("ServletContext Attribute ::" + attribNames.nextElement());
//		}
		
		DataAccess dataAccess = (DataAccess)srvletContext.getAttribute("DATA_ACCESS");
		
		// Read operation using JDBC Statement
		Connection readConn = dataAccess.getReadConnection();
		Statement stmt = null;
		ResultSet rsQ = null;
		
		try {
			// Read operation
			stmt = readConn.createStatement();
			rsQ = stmt.executeQuery("select count(*) from test1");
			while (rsQ.next()) {
				System.out.println("Count = " + rsQ.getInt(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rsQ.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				readConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Write operation using JDBC Statement
		UserTransaction userTxRef = dataAccess.getUserTransaction();
		Connection writeConn = dataAccess.getWriteConnection();
		Statement wstmt = null;
		
		try {
			// Write operation
			userTxRef.begin();
			wstmt = writeConn.createStatement();
			wstmt.executeUpdate("insert into test1 values('EXP INSERT', sysdate)");
			userTxRef.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				wstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writeConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Read operation using JDB PreparedStatement & Read-Write Connection
		UserTransaction userTxRef1 = dataAccess.getUserTransaction();
		Connection writeConn1 = dataAccess.getWriteConnection();
		PreparedStatement wstmt1 = null;
		ResultSet rs1 = null;
		
		try {
			userTxRef1.begin();
			wstmt1 = writeConn1.prepareStatement("select count(*) from test1 where description = ?");
			wstmt1.setString(1, "EXP INSERT");
			rs1 = wstmt1.executeQuery();
			while (rs1.next()) {
				System.out.println("Count for EXP INSERT = " + rs1.getInt(1));
			}			
			userTxRef1.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs1.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				wstmt1.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writeConn1.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Write operation using JDBC CallableStatement & Read-Write Connection
		UserTransaction userTxRef2 = dataAccess.getUserTransaction();
		Connection writeConn2 = dataAccess.getWriteConnection();
		CallableStatement wstmt2 = null;
		
		try {
			userTxRef2.begin();
			wstmt2 = writeConn2.prepareCall("call test_procedure(?)");
			wstmt2.setString(1, "SP INSERT");
			wstmt2.execute();
			userTxRef2.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				wstmt2.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writeConn2.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String forwardURI="sip:5107432501@192.168.9.29:5060";
		SipFactory sipFac = (SipFactory)srvletContext.getAttribute(SIP_FACTORY);
		SipURI suri = (SipURI)sipFac.createURI(forwardURI);
		Proxy proxyObj = (Proxy)request.getProxy();
		proxyObj.proxyTo(suri);
		
	}
	
	private ServletConfig srvletConfig;  	
}
