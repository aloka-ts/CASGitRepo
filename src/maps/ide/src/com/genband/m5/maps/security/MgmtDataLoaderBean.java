package com.genband.m5.maps.security;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.LocalBinding;

import com.genband.m5.maps.common.entity.DeployedApp;
import com.genband.m5.maps.common.entity.Organization;
import com.genband.m5.maps.common.entity.OrganizationAddress;

@Stateless
@Local (ICPFDataLoader.class)
@LocalBinding (jndiBinding="maps/LocalMgmtDataLoader")
public class MgmtDataLoaderBean {

	private static Logger logger = Logger.getLogger(MgmtDataLoaderBean.class);
	
	@PersistenceContext (unitName="mgmt")
	private EntityManager em;
	

	//Get file name of security data
	//@Resource (name="maps/aclURL")
	private String aclURLStr = "http://localhost:8080/method_permissions.csv"; //if no entry defined
	
	public void uploadSecurityData () {
		logger.debug ("Loading data for security permissions. Requested data size: " + securityData.length);
		logger.debug("URL String is : " + aclURLStr);

		try {
			//Query deleteAll = em.createQuery("DELETE FROM PERMISSION");
			//int rowsDeleted = deleteAll.executeUpdate();
			//logger.info ("Deleted security data. number of rows affected - " + rowsDeleted);

			processAclData ();

		} catch (Exception e) {
			logger.error ("Exception got in loading data for security", e);
		}
	}
	
	public void uploadSecurityData (String csvUrl,DeployedApp deployedApp) {
		logger.debug ("Loading data for security permissions. Requested data size: " + securityData.length);
		logger.debug("URL String is : " + csvUrl);

		try {
				processAclData (csvUrl,deployedApp);

		} catch (Exception e) {
			logger.error ("Exception got in loading data for security", e);
		}
	}
	
	/*public void uploadOrganizationData_old () {
		logger.debug ("Loading data for organization entries. Requested data size: " + organizationData.length);
		int i = 0;
		try {
			Query q = em.createQuery("SELECT u FROM User AS u WHERE u.userId = :uname").setParameter("uname", "root");
			List result = q.getResultList();
			for (Object object : result) {
				logger.debug ("Got user. Data - " + object);
				User u = (User) object;
				logger.debug ("user name: " + u.getUserId() + ", password: " + u.getPassword());
			}
			int rowsDeleted = em.createQuery("DELETE FROM User").executeUpdate();
			logger.info ("Deleted user data. number of rows affected - " + rowsDeleted);
			rowsDeleted = em.createQuery("DELETE FROM Role").executeUpdate();
			logger.info ("Deleted role data. number of rows affected - " + rowsDeleted);
			rowsDeleted = em.createQuery("DELETE FROM Organization").executeUpdate();
			logger.info ("Deleted organization data. number of rows affected - " + rowsDeleted);
			Organization org = null;
			OrganizationAddress add = null;
			for (i = 0; i < organizationData.length; i++) {
				org = new Organization ();
				org.setName(organizationData[i][0]);
				org.setDomainName(organizationData[i][1]);
				org.setActivationDate(new java.sql.Date(new SimpleDateFormat("mm-dd-yyyy").parse(organizationData[i][2]).getTime()));
				org.setExpirationDate(new java.sql.Date(new SimpleDateFormat("mm-dd-yyyy").parse(organizationData[i][3]).getTime()));
				add = new OrganizationAddress ();
				add.setCity(organizationData[i][4]);
				add.setState(organizationData[i][5]);
				add.setZip(organizationData[i][6]);
				List<OrganizationAddress> addList = new ArrayList<OrganizationAddress>();
				addList.add (add);
//				org.setAddress(add);
				
				User u = new User ();
				u.setUserId("root");
				u.setPassword("1234");
				u.setMerchantAccount(org);
				
				Role r = new Role ();
				r.setName("Admin");
								
				Set<Role> roles = new HashSet<Role> ();
				roles.add (r);
				u.setRoles(roles);
				
				em.persist(org);
				em.persist (u);
				em.persist (r);
				result = q.getResultList();
				logger.debug ("result list size: " + result.size());
				for (Object object : result) {
					logger.debug ("Got user. Data - " + object);
					u = (User) object;
					logger.debug ("user name: " + u.getUserId() + ", password: " + u.getPassword());
				}
			}
			logger.debug ("Done loading data for organizations. Data size: " + i);
		} catch (Exception e) {
			logger.error ("Exception got in loading data for organizations", e);
			logger.debug ("Partial loading of organizations. Data loaded: " + i);
		}
	}*/
	
	public void uploadOrganizationData () {
		logger.debug ("Loading data for organization entries. Requested data size: " + organizationData.length);
		int i = 0;
		try {
			/*Query q = em.createQuery("SELECT u FROM User AS u WHERE u.userName = :uname").setParameter("uname", "root");
			List result = q.getResultList();
			for (Object object : result) {
				logger.debug ("Got user. Data - " + object);
				UserImpl u = (UserImpl) object;
				logger.debug ("user name: " + u.getUserName() + ", password: " + u.getPassword());
			}
			int rowsDeleted = em.createQuery("DELETE FROM User").executeUpdate();
			logger.info ("Deleted user data. number of rows affected - " + rowsDeleted);
			rowsDeleted = em.createQuery("DELETE FROM Role").executeUpdate();
			logger.info ("Deleted role data. number of rows affected - " + rowsDeleted);*/
			//int rowsDeleted = em.createQuery("DELETE FROM Organization").executeUpdate();
			//logger.info ("Deleted organization data. number of rows affected - " + rowsDeleted);
			Organization org = null;
			OrganizationAddress add = null;
			for (i = 0; i < organizationData.length; i++) {
				org = new Organization ();
				org.setName(organizationData[i][0]);
				org.setDomainName(organizationData[i][1]);
				org.setActivationDate(new java.sql.Date(new SimpleDateFormat("mm-dd-yyyy").parse(organizationData[i][2]).getTime()));
				org.setExpirationDate(new java.sql.Date(new SimpleDateFormat("mm-dd-yyyy").parse(organizationData[i][3]).getTime()));
				add = new OrganizationAddress ();
				add.setCity(organizationData[i][4]);
				add.setState(organizationData[i][5]);
				add.setZip(organizationData[i][6]);
				
				org.setAddress1(add);
				
				/*UserImpl u = new UserImpl ("root");
				u.setPassword("1234");
				u.updatePassword("1234");
				u.setRealEmail("root@genband.com");
				u.setMerchantAccount(org);
				u.setViewRealEmail(true);
				u.setEnabled(true);				

				RoleImpl r = new RoleImpl("Admin", "Administrators");

				u.getRoles().add(r);
				r.getUsers().add(u);*/
								
				em.persist(org);
				/*em.persist (u);
				em.persist (r);
				
				result = q.getResultList();
				logger.debug ("result list size: " + result.size());
				for (Object object : result) {
					logger.debug ("Got user. Data - " + object);
					u = (UserImpl) object;
					logger.debug ("user name: " + u.getUserName() + ", password: " + u.getPassword());
				}*/
			}
			logger.debug ("Done loading data for organizations. Data size: " + i);
		} catch (Exception e) {
			logger.error ("Exception got in loading data for organizations", e);
			logger.debug ("Partial loading of organizations. Data loaded: " + i);
		}
	}
	
	private static final String[][] securityData = new String[][] {
		{"100", "Admin", "com.genband.m5.maps.common.entity.Organization", "LIST",
			"Organization.organizationId, name, merchantAccount"
		},
		{"101", "Admin", "com.genband.m5.maps.common.entity.Organization", "VIEW",
			"organizationId, name, activationDate, merchantAccount"
		},
		{"102", "Admin", "com.genband.m5.maps.common.entity.Organization", "MODIFY",
			"name"
		},
		{"103", "Admin", "com.genband.m5.maps.common.entity.Organization", "DELETE",
			null
		},
		{"104", "Admin", "com.genband.m5.maps.common.entity.Organization", "CREATE",
			null
		}
	};
	
	private void processAclData () {
		URLConnection con = null;
		BufferedInputStream bis = null;
		BufferedReader bis1 = null;
		try {
			logger.info ("reading data from: " + aclURLStr);
			URL aclURL = new URL (aclURLStr);
			con = aclURL.openConnection();
			logger.info ("opened connection: " + con);
			con.setUseCaches(false);
			long since = con.getIfModifiedSince();
			logger.debug ("content modified since: " + since);
			logger.debug ("Content type: " + con.getContentType() + ", length: " + con.getContentLength());

			/*if (! "text/plain".equals (con.getContentType().trim())) {
				logger.info ("only accept text/plain... quitting.");
				return;
			}*/
			
			String data = aclURL.toExternalForm();
			logger.debug ("got data: " + data);
			
			bis = new BufferedInputStream (aclURL.openStream());
			
			bis1 = new BufferedReader (new InputStreamReader(con.getInputStream()));
			
			String temp = null;
			temp = bis1.readLine();
			while((temp = bis1.readLine()) != null) {
				logger.info("line is : " + temp);
				String[] temp1 = temp.split(",", 6);
				uploadMethodPermission(temp1);
			}
			
			/*byte[] b = new byte[1024];
			while (bis.read (b) != -1) {
				System.out.print(new String (b));
			}*/
			
//			int begin = data.indexOf ("~~~~~~~~~~~~"); //12~ or more is marker
			
			/*if (begin == -1) {
				logger.info ("nothing useful found...");
				return;
			}*/
			
			/*StringBuffer sb = new StringBuffer (8192);
			sb.append(data.substring(begin + 12));
			int index = 0;
			logger.info("index is : Prev " + sb.toString() + " " + index);
			while (sb.charAt(index++) != '\n')
				;
			logger.info("index is : " + index);
			//delete initial junk data
			sb.delete(0, index-1);
			index = 0; //reset
			int count = 0; //total read
			int len = sb.length();
			logger.debug("length is : " + len);
			String[] str;
			while (count < len) {
				while (index < sb.length() && sb.charAt(index++) != '\n')
					; //look for newline
				//got a line
				count += index;
				if (index == 0)
					continue;
				str = new String (sb.substring(0, index)).split(",", 5);
				logger.info("rec is : " + str.toString());
				uploadMethodPermission (str);
				
				sb.delete (0, index);
				index = 0; //reset
			}*/

		} catch (Exception e) {
			logger.info("EXCEPTION...");
			logger.error(e);
			// TODO: handle exception
		}
		finally {
			try {
				if (bis != null)
					bis.close ();
				if(bis1 != null)
					bis1.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
private void processAclData (String csvUrl,DeployedApp deployedApp) {
		
		URLConnection con = null;
		BufferedInputStream bis = null;
		BufferedReader bis1 = null;
		try {
			logger.info ("reading data from: " + aclURLStr);
			URL aclURL = new URL (csvUrl);
			con = aclURL.openConnection();
			logger.info ("opened connection: " + con);
			con.setUseCaches(false);
			long since = con.getIfModifiedSince();
			logger.debug ("content modified since: " + since);
			logger.debug ("Content type: " + con.getContentType() + ", length: " + con.getContentLength());
			String data = aclURL.toExternalForm();
			logger.debug ("got data: " + data);
			
			bis = new BufferedInputStream (aclURL.openStream());
			bis1 = new BufferedReader (new InputStreamReader(con.getInputStream()));
			String temp = null;
			temp = bis1.readLine();
			
			while((temp = bis1.readLine()) != null) {
				logger.info("line is : " + temp);
				String[] temp1 = temp.split(",", 6);
				uploadMethodPermission(temp1,deployedApp);
			}
			
			
		} catch (Exception e) {
			logger.info("EXCEPTION...");
			logger.error(e);
			// TODO: handle exception
		}
		finally {
			try {
				if (bis != null)
					bis.close ();
				if(bis1 != null)
					bis1.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

private void uploadMethodPermission (String[] securityData,DeployedApp deployedApp) {
	logger.debug ("Loading data for security permissions. Requested data size: " + securityData.length);
	try {
		MethodPermission mp = null;
		mp = new MethodPermission ();
		mp.setPortletId(Integer.parseInt(securityData[0]));
		mp.setOpId (Integer.parseInt (securityData[1]));
		mp.setRole (securityData[2]);
		mp.setRootEntity (securityData[3]);
		mp.setOpType (securityData[4]);
				
		//As file contains codes arround this data value has to remove codes before entering into data base
		if(securityData[5].contains("\"")) {
			securityData[5] = securityData[5].substring(1,securityData[5].lastIndexOf("\""));
		}
		mp.setAttributes (securityData[5]);
		logger.info("Deployed App Object Details recieved as follows.");
		logger.info("app_id :"+deployedApp.getAppId());
		logger.info("app Description :"+deployedApp.getAppDescription());
		logger.info("app_deployer :"+deployedApp.getAppDeployer());
		logger.info("Deploy Date :"+deployedApp.getDeployDate());
		mp.setDeployedApp(deployedApp);
		em.persist(mp);
		
		logger.debug ("loaded data for security permissions.");
	} catch (Exception e) {
		logger.error ("Exception got in loading data for security", e);
	}
}
	
	private void uploadMethodPermission (String[] securityData) {
		logger.debug ("Loading data for security permissions. Requested data size: " + securityData.length);
		try {
			MethodPermission mp = null;
			mp = new MethodPermission ();
			mp.setPortletId(Integer.parseInt(securityData[0]));
			mp.setOpId (Integer.parseInt (securityData[1]));
			mp.setRole (securityData[2]);
			mp.setRootEntity (securityData[3]);
			mp.setOpType (securityData[4]);
			//As file contains codes arround this data value has to remove codes before entering into data base
			if(securityData[5].contains("\"")) {
				securityData[5] = securityData[5].substring(1,securityData[5].lastIndexOf("\""));
			}
			mp.setAttributes (securityData[5]);
			
			em.persist(mp);
			
			logger.debug ("loaded data for security permissions.");
		} catch (Exception e) {
			logger.error ("Exception got in loading data for security", e);
		}
	}
	
	private static final String[][] organizationData = new String[][] {
		{"TheNPA", "www.thenpa.com", "01-01-2008", "12-31-2099", "New Delhi", "Delhi", "110001"}
	};
	
}

