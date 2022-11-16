/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.portal.identity.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.logging.Logger;
import org.jboss.portal.common.util.Tools;
import org.jboss.portal.core.ui.portlet.user.UserPortletConstants;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.service.UserModuleService;

import com.genband.m5.maps.common.entity.Organization;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet </a>
 * @version $Revision: 5448 $
 * @portal.core
 */
public class HibernateUserModuleImpl extends UserModuleService
{

   /** . */
   //private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(HibernateUserModuleImpl.class);
   public static final Logger log = Logger.getLogger(HibernateUserModuleImpl.class);

   /** . */
   protected SessionFactory sessionFactory;

   /** . */
   protected String sessionFactoryJNDIName;

   protected void startService() throws Exception
   {
      //
      sessionFactory = (SessionFactory)new InitialContext().lookup(sessionFactoryJNDIName);

      super.startService();
   }

   protected void stopService() throws Exception
   {

      //
      sessionFactory = null;

      super.stopService();
   }

   public String getSessionFactoryJNDIName()
   {
      return sessionFactoryJNDIName;
   }

   public void setSessionFactoryJNDIName(String sessionFactoryJNDIName)
   {
      this.sessionFactoryJNDIName = sessionFactoryJNDIName;
   }

   public User findUserByUserName(String userName) throws IdentityException
   {
      if (userName != null)
      {
         try
         {
            Session session = getCurrentSession();
            Query query = session.createQuery("from HibernateUserImpl where userName=:userName");
            query.setParameter("userName", userName);
            query.setCacheable(true);
            HibernateUserImpl user = (HibernateUserImpl)query.uniqueResult();
            if (user == null)
            {
               throw new NoSuchUserException("No such user " + userName);
            }
            return user;
         }
         catch (HibernateException e)
         {
            String message = "Cannot find user by name " + userName;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("user name cannot be null");
      }
   }

   public User findUserById(String id) throws IllegalArgumentException, IdentityException, NoSuchUserException
   {
      if (id == null)
      {
         throw new IllegalArgumentException("The id is null");
      }
      try
      {
         return findUserById(new Long(id));
      }
      catch (NumberFormatException e)
      {
         throw new IllegalArgumentException("Cannot parse id into an long " + id);
      }
   }

   public User findUserById(Object id) throws IllegalArgumentException, IdentityException, NoSuchUserException
   {
      if (id instanceof Long)
      {
         try
         {
            Session session = getCurrentSession();
            HibernateUserImpl user = (HibernateUserImpl)session.get(HibernateUserImpl.class, (Long)id);
            if (user == null)
            {
               throw new NoSuchUserException("No user found for " + id);
            }
            return user;
         }
         catch (HibernateException e)
         {
            String message = "Cannot find user by id " + id;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("The id is not an long : " + id);
      }
   }

   public User createUser(String userName, String password) throws IdentityException
   {
      if (userName != null)
      {
         try
         {
            HibernateUserImpl user = new HibernateUserImpl(userName);
            user.updatePassword(password);
            //user.setRealEmail(realEmail);
            Session session = getCurrentSession();
            session.save(user);

            //fire events
            fireUserCreatedEvent(user.getId(), user.getUserName());

            return user;
         }
         catch (HibernateException e)
         {
            String message = "Cannot create user " + userName;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("name cannot be null");
      }
   }


   public void removeUser(Object id) throws IdentityException
   {
      if (id instanceof Long)
      {
         try
         {
            Session session = getCurrentSession();
            HibernateUserImpl user = (HibernateUserImpl)session.load(HibernateUserImpl.class, (Serializable)id);

            String userName = user.getUserName();
            if (user == null)
            {
               throw new NoSuchUserException("No such user " + id);
            }
            session.delete(user);
            session.flush();

            //fire events
            fireUserDestroyedEvent(id, userName);
         }
         catch (HibernateException e)
         {
            String message = "Cannot remove user " + id;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("The id is not an long : " + id);
      }
   }

   public Set findUsers(int offset, int limit) throws IdentityException
   {
      try
      {
         Session session = getCurrentSession();
         Query query = session.createQuery("from HibernateUserImpl");
         query.setFirstResult(offset);
         query.setMaxResults(limit);
         Iterator iterator = query.iterate();
         return Tools.toSet(iterator);
      }
      catch (HibernateException e)
      {
         String message = "Cannot find user range [" + offset + "," + limit + "]";
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }


   public Set findUsersFilteredByUserName(String filter, int offset, int limit) throws IdentityException
   {
      try
      {
         // Remove all occurences of % and add ours
         filter = "%" + filter.replaceAll("%", "") + "%";

         //
         Session session = getCurrentSession();
         Query query = session.createQuery("from HibernateUserImpl as u where u.userName like :filter");
         query.setString("filter", filter);
         query.setFirstResult(offset);
         query.setMaxResults(limit);
         Iterator iterator = query.iterate();
         return Tools.toSet(iterator);
      }
      catch (HibernateException e)
      {
         String message = "Cannot find user range [" + offset + "," + limit + "]";
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }


   public int getUserCount() throws IdentityException
   {
      try
      {
         Session session = getCurrentSession();
         Query query = session.createQuery("select count(u.key) from HibernateUserImpl as u");
         return ((Number)query.uniqueResult()).intValue();
      }
      catch (HibernateException e)
      {
         String message = "Cannot count users";
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }

   /**
    * Can be subclasses to provide testing in a non JTA environement.
    *
    * @throws IllegalStateException if no session factory is present
    */
   protected Session getCurrentSession() throws IllegalStateException
   {
      if (sessionFactory == null)
      {
         throw new IllegalStateException("No session factory");
      }
      
      //return sessionFactory.getCurrentSession();
      try{
    	  return sessionFactory.getCurrentSession();
      }catch(Exception e){
    	  log.debug("Exception caught while getting CurrentSession " ,e);
    	  return sessionFactory.openSession();
    	  
      }
   }

   	/* --------------- APIs related to PR 51046  --------------starts-------
	 */

   public User createUser(String userName, String password , long merchantid) throws IdentityException
   {
	   log.debug("customized createUser(" + userName + ", password," + merchantid + ") called ");
      if (userName != null)
      {
         try
         {
            HibernateUserImpl user = new HibernateUserImpl(userName);
            user.updatePassword(password);
            Organization merchantAccount = new Organization();
            merchantAccount.setOrganizationId(merchantid);
            user.setMerchantAccount(merchantAccount);
            //user.setRealEmail(realEmail);
            Session session = getCurrentSession();
            session.save(user);

            //fire events
            fireUserCreatedEvent(user.getId(), user.getUserName());
     	   log.debug("customized createUser() going to exit  ");

            return user;
         }
         catch (HibernateException e)
         {
            String message = "Cannot create user " + userName;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("name cannot be null");
      }
   }

   public User createUser(String userName, String password , Organization organization) throws IdentityException
   {
	   log.debug("customized createUser(" + userName + ", password," + organization.getCustomerId() + ") called ");
      if (userName != null)
      {
         try
         {
            HibernateUserImpl user = new HibernateUserImpl(userName);
            user.updatePassword(password);
          /*  Organization merchantAccount = new Organization();
            merchantAccount.setOrganizationId(merchantid);*/
            user.setMerchantAccount(organization);
            //user.setRealEmail(realEmail);
            Session session = getCurrentSession();
            Set roles = findSPARole();
            user.setEnabled(true);
            user.setRoles(roles);
            session.save(user);

            //fire events
            fireUserCreatedEvent(user.getId(), user.getUserName());
     	   log.debug("customized createUser() going to exit  ");

            return user;
         }
         catch (HibernateException e)
         {
            String message = "Cannot create user " + userName;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("name cannot be null");
      }
   }
   


   public List findUsersFilteredByUserName(String filter, int offset, int limit , long merchantId , int role) throws IdentityException
   {
	   log.debug("customized findUsersFilteredByUserName(" + filter + "," + offset + "," + limit + "," + merchantId +"," + role + ") called ");
	  	 List result = new ArrayList();
         filter = "%" + filter.replaceAll("%", "") + "%";
		  
	      try
	      {
	    	  if( UserPortletConstants.INVALID_ROLE == role){
	         	 return result;
	          }
	         Session session = getCurrentSession();
	         String queryStr =	"select DISTINCT u  " 
	        	 + " from HibernateUserImpl as u left join u.roles r left join u.merchantAccount org " +
	 			" where org.organizationId=:merchantId AND u.userName like :filter" ;
	         if ( UserPortletConstants.SPA == role){
	        	 queryStr = queryStr + " AND (r.name NOT like \'NPA\' AND r.name NOT like \'NPM\' OR r.name is null) ";
	         }
	         queryStr = queryStr + " order By u.key";
	         Query query = session.createQuery(queryStr);
	         
	         Long mId = merchantId;
	         query.setString("merchantId", mId.toString());
	         query.setString("filter", filter);

	         query.setFirstResult(offset);
	         query.setMaxResults(limit);
	         log.debug("-----------------query to be executted : -------------\n" + queryStr);
	         log.debug("offset : " + offset);
	         log.debug("max results :" + limit);
	         Iterator iterator = query.iterate();
	         while(iterator.hasNext()){
	        	 HibernateUserImpl u =  (HibernateUserImpl)iterator.next();
	        	 if ( false == result.contains(u) ) {
	        		 result.add(u);
	        	 }
	        	 log.debug("****userName : " + u.getUserName() + "*****");
	        	 log.debug("uid : " + u.getKey());
	        	 log.debug("roles : " + u.getRoles().size());
	        	 log.debug("******************************************");
	         }
	         log.debug("query results completed ");
	         log.debug("customized findUsers() going to exit ");
	         return result;
	      } catch (HibernateException e)
	      {
	         String message = "Cannot find user range [" + offset + "," + limit + "] having merchantId = " + merchantId;
	         log.error(message, e);
	         throw new IdentityException(message, e);
	      }
   }

   public Set findSPARole() throws IdentityException
   {
	   log.debug("findSPARole called ");
	   Set result = new HashSet();

	   Session session = getCurrentSession();
         String queryStr =	" from HibernateRoleImpl as r where r.name LIKE \'SPA\' " ;

         Query query = session.createQuery(queryStr);
         Iterator iterator = query.iterate();
         result = Tools.toSet(iterator);
         return result;
   }

   
   public Set findUsers1(int offset, int limit , long merchantId , int role ) throws IdentityException
   {
	   log.debug("customized findUsers(," + offset + "," + limit + "," + merchantId +") called ");
	   Set result = new HashSet();
	   try
      {
    	  if( UserPortletConstants.INVALID_ROLE == role){
         	 return result;
          }
    	 
         Session session = getCurrentSession();
         String queryStr =	"select u  " 
        	 + " from HibernateUserImpl as u left join u.roles r left join u.merchantAccount org " +
 			" where org.organizationId=:merchantId " ;
         if ( UserPortletConstants.SPA == role){
        	 queryStr = queryStr + " AND (r.name NOT like \'NPA\' AND r.name NOT like \'NPM\' OR r.name is null) ";
         }
         //queryStr = queryStr + " order By u.key";
         Query query = session.createQuery(queryStr);
         
         Long mId = merchantId;
         query.setString("merchantId", mId.toString());

         query.setFirstResult(offset);
         query.setMaxResults(limit);
         
         Iterator iterator = query.iterate();
         
         result = Tools.toSet(iterator);
         log.debug("offset :" +offset + "limit : " + limit);
         log.debug("users found : " + result.size());
         Iterator cleaner = result.iterator();
         while (cleaner.hasNext())
         {
        	 HibernateUserImpl u = (HibernateUserImpl)cleaner.next();
        	 
        	 log.debug("***** username : " +  u.getUserName() + " ******");
        	 //newResult.add(u);
        	 Set roles = u.getRoles();
        	 Iterator it = roles.iterator();
        	 while ( it.hasNext() ) {
        		 HibernateRoleImpl role1 = (HibernateRoleImpl)it.next();
        		 log.debug("role : " + role1.getName());
        	 }
         }
         
         log.debug("customized findUsers() going to exit ");
         
  	   	 return result;
      }
      catch (HibernateException e)
      {
         String message = "Cannot find user range [" + offset + "," + limit + "] having merchantId = " + merchantId ;
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }

   
   
   public List findUsers2(int offset, int limit , long merchantId , int role ) throws IdentityException
   {
	   log.debug("customized findUsers(" + offset + "," + limit + "," + merchantId +") called ");
	   int tmpOffset = -1;
	   int tmpLimit = limit;
	   boolean noMoreTuples = false;
	   List<HibernateUserImpl> newResult = new ArrayList();
	  
      try
      {
    	  if( UserPortletConstants.INVALID_ROLE == role){
         	 return newResult;
          }
    	 boolean newUser = true;
    	 List result = new ArrayList();
         Session session = getCurrentSession();
         String queryStr = "select u.key , u.userName," +
         	" u.givenName, u.familyName, u.password, u.realEmail, u.fakeEmail," +
 			" u.registrationDate, u.viewRealEmail, u.enabled , r.name ,r.displayName " + 
 			" from HibernateUserImpl as u left join u.roles r left join u.merchantAccount org " +
 			" where org.organizationId=:merchantId " ;

         queryStr =	"select DISTINCT u  " 
        	 + " from HibernateUserImpl as u left join u.roles r left join u.merchantAccount org " +
 			" where org.organizationId=:merchantId " ;
         if ( UserPortletConstants.SPA == role){
        	 queryStr = queryStr + " AND (r.name NOT like \'NPA\' AND r.name NOT like \'NPM\' OR r.name is null) ";
         }
         queryStr = queryStr + " order By u.key";
         Query query = session.createQuery(queryStr);
         
         Long mId = merchantId;
         query.setString("merchantId", mId.toString());
         
         while ( (newResult.size() < limit) && (false == noMoreTuples) )
	     {
        	 if ( -1 == tmpOffset ) {
        		 tmpOffset = offset;
        	 } else {
        		 tmpOffset = tmpOffset + tmpLimit ;
        	 }
        	 tmpLimit = 2 *tmpLimit ;
	         query.setFirstResult(tmpOffset);
	         query.setMaxResults(tmpLimit);
	         log.debug("-----------------query to be executted : -------------\n" + queryStr);
	         log.debug("offset : " + tmpOffset);
	         log.debug("max results :" + tmpLimit);
	         Iterator iterator = query.iterate();
	         if(iterator.hasNext()){
	        	 noMoreTuples = false;
	         }else{
	        	 noMoreTuples = true;
	         }
	         while(iterator.hasNext()){
	        	 noMoreTuples = false;
	        	 HibernateUserImpl u =  (HibernateUserImpl)iterator.next();
	        	 if ( false == result.contains(u) ) {
	        		 result.add(u);
	        	 }
	        	 log.debug("****userName : " + u.getUserName() + "*****");
	        	 log.debug("uid : " + u.getKey());
	        	 log.debug("roles : " + u.getRoles().size());
	        	 log.debug("******************************************");
	         }
	         log.debug("query results completed ");
	         
	         //Set result = Tools.toSet(iterator);
	         /*if(result.size() == 0){
	        	 noMoreTuples = true;
	         }else{
	        	 noMoreTuples = false;
	         }*/
	         
	         Iterator cleaner = result.iterator();
	         while (cleaner.hasNext())
	         {
	        	 HibernateUserImpl u = (HibernateUserImpl)cleaner.next();
	        	 
	        	 log.debug("username : " +  u.getUserName());
	        	 if(newResult.size() < limit && false == newResult.contains(u)){
	        		 newResult.add(u);
	        	 }
	        	 Set roles = u.getRoles();
	        	 Iterator it = roles.iterator();
	        	 while ( it.hasNext() ) {
	        		 HibernateRoleImpl role1 = (HibernateRoleImpl)it.next();
	        		 log.debug("role : " + role1.getName());
	        	 }
	        	 /*
	            Object[] oArr = (Object[])cleaner.next();
	            Iterator iterator4NewResult = newResult.iterator();
	            HibernateUserImpl alreadyAddedUser = null ;
	            while (iterator4NewResult.hasNext()){
	            	alreadyAddedUser = (HibernateUserImpl)iterator4NewResult.next();
	            	
	            	if ( alreadyAddedUser.getKey().equals(oArr[0])) {
	            		newUser = false;
	            		break;
	            	}else{
	            		newUser = true;
	            	}
	            }
	            log.debug("\n\n\n**********user from db : " + oArr[0] + " username : " + oArr[1] + "********");
	            if ( true == newUser ) {
	            	if(newResult.size() >= limit) {
	            		break;
	            	}
		            HibernateUserImpl tmp = new HibernateUserImpl();
		            log.debug("newResult size is : " +newResult.size());
		            log.debug("new user found: ");
		            if(null != oArr[0]){
		            	tmp.setKey((Long)oArr[0]);
		            	log.debug("key is :" + oArr[0]);
		            }
		            if(null != oArr[1]){
		            	tmp.setUserName(oArr[1].toString());
		            	log.debug("useName is  :" + oArr[1]);
		            }
		            if(null != oArr[2]){
		                tmp.setGivenName(oArr[2].toString());
		            }
		            if(null != oArr[3]){
		                tmp.setFamilyName(oArr[3].toString());
		            }
		            if(null != oArr[4]) {
		                tmp.setPassword(oArr[4].toString());
		            }
		            if(null != oArr[5]){
		                tmp.setRealEmail(oArr[5].toString());
		            }
		            if(null != oArr[6]){
		                tmp.setFakeEmail(oArr[6].toString());
		            }
		            if(null != oArr[7]){
		                tmp.setRegistrationDate((Date)oArr[7]);
		            }
		            if(null != oArr[8]){
		                tmp.setViewRealEmail((Boolean)oArr[8]);
		            }
		            if(null != oArr[9]){
		                tmp.setEnabled((Boolean)oArr[9]);
		            }
		            Set<HibernateRoleImpl> roles = new HashSet();
		            HibernateRoleImpl tmpRole = new HibernateRoleImpl();
		            if(null != oArr[10]){
		            	tmpRole.setName(oArr[10].toString());
		            	log.debug("role name :" + tmpRole.getName());
		            }
		            if(null != oArr[11]){
		            	tmpRole.setDisplayName(oArr[11].toString());
		            }
		            roles.add(tmpRole);
		            tmp.setRoles(roles);
		            
		            newResult.add(tmp);
	            } else { // user is already added in newResult
	            	log.debug("user is already added..");
	            	Set<HibernateRoleImpl> roles = alreadyAddedUser.getRoles();
		            HibernateRoleImpl tmpRole = new HibernateRoleImpl();
		            log.debug("new role found : " + oArr[10]);
		            if(null != oArr[10])
		            	tmpRole.setName(oArr[10].toString());
		            if(null != oArr[11])
		            	tmpRole.setDisplayName(oArr[11].toString());
		            roles.add(tmpRole);
		            alreadyAddedUser.setRoles(roles);
	            }
	         */}
	     }
         log.debug("customized findUsers() going to exit ");
         
  	   	 return newResult;
      }
      catch (HibernateException e)
      {
         String message = "Cannot find user range [" + offset + "," + limit + "] having merchantId = " + merchantId ;
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }
   public List findUsers(int offset, int limit , long merchantId , int role ) throws IdentityException
   {
	   log.debug("customized findUsers(" + offset + "," + limit + "," + merchantId +") called ");
  	 List result = new ArrayList();
	  
      try
      {
    	  if( UserPortletConstants.INVALID_ROLE == role){
         	 return result;
          }
         Session session = getCurrentSession();
         String queryStr =	"select DISTINCT u  " 
        	 + " from HibernateUserImpl as u left join u.roles r left join u.merchantAccount org " +
 			" where org.organizationId=:merchantId " ;
         if ( UserPortletConstants.SPA == role){
        	 queryStr = queryStr + " AND (r.name NOT like \'NPA\' AND r.name NOT like \'NPM\' OR r.name is null) ";
         }
         queryStr = queryStr + " order By u.key";
         Query query = session.createQuery(queryStr);
         
         Long mId = merchantId;
         query.setString("merchantId", mId.toString());
         query.setFirstResult(offset);
         query.setMaxResults(limit);
         log.debug("-----------------query to be executted : -------------\n" + queryStr);
         log.debug("offset : " + offset);
         log.debug("max results :" + limit);
         Iterator iterator = query.iterate();
         while(iterator.hasNext()){
        	 HibernateUserImpl u =  (HibernateUserImpl)iterator.next();
        	 if ( false == result.contains(u) ) {
        		 result.add(u);
        	 }
        	 log.debug("****userName : " + u.getUserName() + "*****");
        	 log.debug("uid : " + u.getKey());
        	 log.debug("roles : " + u.getRoles().size());
        	 log.debug("******************************************");
         }
         log.debug("query results completed ");
         log.debug("customized findUsers() going to exit ");
         return result;
         
         
      }
      catch (HibernateException e)
      {
         String message = "Cannot find user range [" + offset + "," + limit + "] having merchantId = " + merchantId ;
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }
   
   public boolean isUserInRole(User user , String role)
   {
      try
      {
         Session session = getCurrentSession();
         Query query = session.createQuery("from HibernateUserImpl user left join user.roles role where user.key = :uid AND role.name=:roleName");
         query.setString("roleName",role);
         query.setString("uid",user.getId().toString());
         Iterator iterator = query.iterate();
         
         Set s = Tools.toSet(iterator);
         if(s.size() > 0) {
        	 return true ;
         } else { 
        	 return false ;
         }
      }
      catch (HibernateException e)
      {
         String message = "Cannot find - is " + user.getUserName() + " in role " + role + "?";
         log.error(message, e);
         return false;
         //throw new IdentityException(message, e);
      }
   }

   
  	/* --------------- APIs related to PR 51046  --------------ends-------
	 */


}
