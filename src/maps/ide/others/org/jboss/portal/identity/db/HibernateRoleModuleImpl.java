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

import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.db.HibernateRoleImpl;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.db.HibernateUserImpl;
import org.jboss.portal.common.util.Tools;
import org.jboss.portal.core.ui.portlet.user.UserPortletConstants;
import org.jboss.portal.identity.service.RoleModuleService;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import javax.naming.InitialContext;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet </a>
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @author Roy Russo : roy at jboss dot org
 * @version $Revision: 5448 $
 * @portal.core
 */
public class HibernateRoleModuleImpl extends RoleModuleService
{

   /** . */
      private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(HibernateRoleModuleImpl.class);
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


   public Role findRoleByName(String name) throws IdentityException
   {
      if (name != null)
      {
         try
         {
            Session session = getCurrentSession();
            Criteria criteria = session.createCriteria(HibernateRoleImpl.class);
            criteria.add(Restrictions.naturalId().set("name", name));
            criteria.setCacheable(true);
            HibernateRoleImpl role = (HibernateRoleImpl)criteria.uniqueResult();
            if (role == null)
            {
               throw new IdentityException("No such role " + name);
            }
            return role;
         }
         catch (HibernateException e)
         {
            String message = "Cannot find role by name " + name;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("name cannot be null");
      }
   }

   public Set findRolesByNames(String[] names) throws IdentityException
   {
      if (names != null)
      {
         try
         {
            Session session = getCurrentSession();
            StringBuffer queryString = new StringBuffer("from HibernateRoleImpl as g where g.name=?");
            for (int i = 1; i < names.length; i++)
            {
               queryString.append(" or g.name=?");
            }
            Query query = session.createQuery(queryString.toString());
            for (int i = 0; i < names.length; i++)
            {
               query.setString(i, names[i]);
            }
            Iterator iterator = query.iterate();
            return Tools.toSet(iterator);
         }
         catch (HibernateException e)
         {
            String message = "Cannot find roles";
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("name cannot be null");
      }
   }

   public Role findRoleById(String id) throws IllegalArgumentException, IdentityException
   {
      if (id == null)
      {
         throw new IllegalArgumentException("The id is null");
      }
      try
      {
         return findRoleById(new Long(id));
      }
      catch (NumberFormatException e)
      {
         throw new IllegalArgumentException("Cannot parse id into an long " + id);
      }
   }

   public Role findRoleById(Object id) throws IdentityException
   {
      if (id instanceof Long)
      {
         try
         {
            Session session = getCurrentSession();
            HibernateRoleImpl role = (HibernateRoleImpl)session.get(HibernateRoleImpl.class, (Long)id);
            if (role == null)
            {
               throw new IdentityException("No role found for " + id);
            }
            return role;
         }
         catch (HibernateException e)
         {
            String message = "Cannot find role by id " + id;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("The id is not an long : " + id);
      }
   }

   public Role createRole(String name, String displayName) throws IdentityException
   {
      if (name != null)
      {
         try
         {
            HibernateRoleImpl role = new HibernateRoleImpl(name, displayName);
            Session session = getCurrentSession();
            session.save(role);
            return role;
         }
         catch (HibernateException e)
         {
            String message = "Cannot create role " + name;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("name cannot be null");
      }
   }

   public void removeRole(Object id) throws IdentityException
   {
      if (id instanceof Long)
      {
         try
         {
            Session session = getCurrentSession();
            HibernateRoleImpl role = (HibernateRoleImpl)session.load(HibernateRoleImpl.class, (Long)id);
            Iterator users = role.getUsers().iterator();
            while (users.hasNext())
            {
               HibernateUserImpl user = (HibernateUserImpl)users.next();
               user.getRoles().remove(role);
            }
            session.delete(role);
            session.flush();
         }
         catch (HibernateException e)
         {
            String message = "Cannot remove role  " + id;
            log.error(message, e);
            throw new IdentityException(message, e);
         }
      }
      else
      {
         throw new IllegalArgumentException("The id is not an long : " + id);
      }
   }

   public int getRolesCount() throws IdentityException
   {
      try
      {
         Session session = getCurrentSession();
         Query query = session.createQuery("select count(g.id) from HibernateRoleImpl as g");
         return ((Number)query.uniqueResult()).intValue();
      }
      catch (HibernateException e)
      {
         String message = "Cannot count roles";
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }

   public Set findRoles() throws IdentityException
   {
      try
      {
         Session session = getCurrentSession();
         Query query = session.createQuery("from HibernateRoleImpl");
         Iterator iterator = query.iterate();
         return Tools.toSet(iterator);
      }
      catch (HibernateException e)
      {
         String message = "Cannot find roles";
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }

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

   public void setRoles(User user, Set roles) throws IdentityException
   {
      if (!(user instanceof HibernateUserImpl))
      {
         throw new IllegalArgumentException("User is not a db user");
      }

      // We make a defensive copy and update with a new set
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
            throw new IllegalArgumentException("Only db roles can be accepted");
         }
      }

      // Assign new roles
      HibernateUserImpl ui = (HibernateUserImpl)user;
      ui.setRoles(copy);
   }

   public Set getRoles(User user) throws IdentityException
   {
      if (!(user instanceof HibernateUserImpl))
      {
         throw new IllegalArgumentException("User is not a db user");
      }

      // We return an immutable set to avoid modifications
      HibernateUserImpl ui = (HibernateUserImpl)user;
      return Collections.unmodifiableSet(ui.getRoles());
   }

   /** Can be subclasses to provide testing in a non JTA environement. */
   protected Session getCurrentSession()
   {
      if (sessionFactory == null)
      {
         throw new IllegalStateException("No session factory");
      }
      return sessionFactory.getCurrentSession();
   }
   
   public Set findRoles(int role ) throws IdentityException
   {
      try
      {
         Session session = getCurrentSession();
         String strQuery = "";
         if ( UserPortletConstants.ROOT == role ) {
        	 strQuery = "from HibernateRoleImpl r where r.name LIKE \'NPA\' OR r.name LIKE \'NPM\'";
         }else if ( UserPortletConstants.SPA == role ) {
        	 strQuery = "from HibernateRoleImpl r where r.name NOT LIKE \'NPA\' AND r.name NOT LIKE \'NPM\'";
         }else if ( UserPortletConstants.NON_STANDARD_ROLE == role ){
        	 strQuery = "from HibernateRoleImpl r where r.name NOT LIKE \'NPA\' AND r.name NOT LIKE \'NPM\' " +
        	 		" AND r.name NOT LIKE \'SPA\'";
         }
         Query query = session.createQuery(strQuery);
         Iterator iterator = query.iterate();
         return Tools.toSet(iterator);
      }
      catch (HibernateException e)
      {
         String message = "Cannot find roles";
         log.error(message, e);
         throw new IdentityException(message, e);
      }
   }

}
