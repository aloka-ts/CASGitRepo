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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.portal.common.util.Tools;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.service.MembershipModuleService;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class HibernateMembershipModuleImpl extends MembershipModuleService
{
   /** . */
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(HibernateMembershipModuleImpl.class);
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

//   public SessionFactory getSessionFactory()
//   {
//      return sessionFactory;
//   }

   public String getSessionFactoryJNDIName()
   {
      return sessionFactoryJNDIName;
   }

   public void setSessionFactoryJNDIName(String sessionFactoryJNDIName)
   {
      this.sessionFactoryJNDIName = sessionFactoryJNDIName;
   }

   public Set getRoles(User user) throws IdentityException
   {
      //throw new UnsupportedOperationException("Not yet implemented");
      if (!(user instanceof HibernateUserImpl))
      {
         throw new IllegalArgumentException("User is not a HibernateUserImpl user");
      }

      // We return an immutable set to avoid modifications
      HibernateUserImpl ui = (HibernateUserImpl)user;
      Set roles = ui.getRoles();
      Set copy = new HashSet();
      for (Iterator iterator = roles.iterator(); iterator.hasNext();)
      {
         HibernateRoleImpl role = (HibernateRoleImpl)iterator.next();
         copy.add(role);
      }

      return Collections.unmodifiableSet(copy);
   }

   public Set getUsers(Role role) throws IdentityException
   {
      if (!(role instanceof HibernateRoleImpl))
      {
         throw new IllegalArgumentException("User is not a HibernateRoleImpl user");
      }

      // We return an immutable set to avoid modifications
      HibernateRoleImpl ri = (HibernateRoleImpl)role;
      Set users = ri.getUsers();
      Set copy = new HashSet();
      for (Iterator iterator = users.iterator(); iterator.hasNext();)
      {
         HibernateUserImpl user = (HibernateUserImpl)iterator.next();
         copy.add(user);
      }

      return Collections.unmodifiableSet(copy);
   }

   public void assignUsers(Role role, Set users) throws IdentityException
   {
      //throw new UnsupportedOperationException("Not yet implemented");
      if (!(role instanceof HibernateRoleImpl))
      {
         throw new IllegalArgumentException("User is not a HibernateRoleImpl user");
      }

      for (Iterator i = users.iterator(); i.hasNext();)
      {
         Object o = i.next();
         if (o instanceof HibernateUserImpl)
         {
            HibernateUserImpl user = (HibernateUserImpl)o;
            user.getRoles().add(role);
         }
         else
         {
            throw new IllegalArgumentException("Only HibernateUserImpl roles can be accepted");
         }
      }

   }

   public void assignRoles(User user, Set roles) throws IdentityException
   {
      //throw new UnsupportedOperationException("Not yet implemented");
      if (!(user instanceof HibernateUserImpl))
      {
         throw new IllegalArgumentException("User is not a HibernateUserImpl user");
      }

      // We make a defensive copy with unwrapped maps and update with a new set
      Set copy = new HashSet();
      for (Iterator i = roles.iterator(); i.hasNext();)
      {
         Object o = i.next();
         if (o instanceof HibernateRoleImpl)
         {
            copy.add(o);
         }
         else
         {
            throw new IllegalArgumentException("Only HibernateRoleImpl roles can be accepted");
         }
      }

      // Assign new roles
      HibernateUserImpl ui = (HibernateUserImpl)user;
      ui.setRoles(copy);
   }

   //TODO:
   public Set findRoleMembers(String roleName, int offset, int limit, String userNameFilter) throws IdentityException
   {
      if (roleName != null)
      {
         try
         {
            Session session = getCurrentSession();

            Query query;
            if (userNameFilter.trim().length() != 0)
            {
               //
               userNameFilter = "%" + userNameFilter.replaceAll("%", "") + "%";

               //
               query = session.createQuery("from HibernateUserImpl as user left join user.roles role where role.name=:name" + " AND user.userName LIKE :filter");
               query.setString("filter", userNameFilter);
            }
            else
            {
               query = session.createQuery("from HibernateUserImpl as user left join user.roles role where role.name=:name");
            }
            query.setString("name", roleName);
            query.setFirstResult(offset);
            query.setMaxResults(limit);

            Iterator iterator = query.iterate();
            Set result = Tools.toSet(iterator);

            Set newResult = new HashSet();
            Iterator cleaner = result.iterator();
            while (cleaner.hasNext())
            {
               Object[] oArr = (Object[])cleaner.next();
               newResult.add(oArr[0]);
            }

            return newResult;
         }
         catch (HibernateException e)
         {
            String message = "Cannot find role  " + roleName;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("id cannot be null");
      }
   }

   public List findRoleMembers(String roleName, int offset, int limit, String userNameFilter ,long merchantId ) throws IdentityException
   {
      if (roleName != null)
      {
         try
         {
            Session session = getCurrentSession();

            Query query;
            if (userNameFilter.trim().length() != 0)
            {
               //
               userNameFilter = "%" + userNameFilter.replaceAll("%", "") + "%";

               //
               query = session.createQuery("from HibernateUserImpl as user left join user.roles role left join user.merchantAccount org where role.name=:name" + " AND user.userName LIKE :filter AND org.organizationId=:merchantId");
               query.setString("filter", userNameFilter);
            }
            else
            {
               query = session.createQuery("from HibernateUserImpl as user left join user.roles role left join user.merchantAccount org where role.name=:name AND org.organizationId=:merchantId");
            }
            query.setString("name", roleName);
            Long mId = merchantId;
            query.setString("merchantId", mId.toString());
            query.setFirstResult(offset);
            query.setMaxResults(limit);

            Iterator iterator = query.iterate();
            Set result = Tools.toSet(iterator);

            List newResult = new ArrayList();
            Iterator cleaner = result.iterator();
            while (cleaner.hasNext())
            {
               Object[] oArr = (Object[])cleaner.next();
               newResult.add(oArr[0]);
            }

            return newResult;
         }
         catch (HibernateException e)
         {
            String message = "Cannot find role  " + roleName;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("id cannot be null");
      }
   }

   /**
    * Process Set of Map objects and returns a Set of HibernateRoleImpl objects
    * @param maps
    * @return
    * @throws Exception
    */

   /** Can be subclasses to provide testing in a non JTA environement. */
   protected Session getCurrentSession()
   {
      if (sessionFactory == null)
      {
         throw new IllegalStateException("No session factory");
      }
      return sessionFactory.getCurrentSession();
   }
}
