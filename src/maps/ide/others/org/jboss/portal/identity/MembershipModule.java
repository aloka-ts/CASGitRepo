/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.identity;


import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public interface MembershipModule
{

   /**
    * Return the set of role objects that a given user has.
    *
    * @param user the user
    * @return the set of roles of the specified user
    */
   Set getRoles(User user) throws IdentityException, IllegalArgumentException;

   /**
    * Returns the set of user objects that a given role has.
    * @param role
    * @return
    * @throws IdentityException
    * @throws IllegalArgumentException
    */
   Set getUsers(Role role) throws IdentityException, IllegalArgumentException;


   /**
    * Creates a relationship beetween a role and set of users. Other roles that have assotiontions with
    * those users remain unaffected.
    *
    * @param role
    * @param users
    * @throws IdentityException
    */
   void assignUsers(Role role, Set users) throws IdentityException, IllegalArgumentException;

   /**
    * Creates a relationship beetween a user and set of roles. This operation will erase any other assotientions
    * beetween the user and roles not specified in the provided set.
    *
    * @param user
    * @param roles
    * @throws IdentityException
    */
   void assignRoles(User user, Set roles) throws IdentityException, IllegalArgumentException;

   /**
    * Returns role members based on rolename - depreciated method ethod here only for compatibility with
    * old RoleModule interface 
    *
    * @param roleName
    * @param offset
    * @param limit
    */
   Set findRoleMembers(String roleName, int offset, int limit, String userNameFilter) throws IdentityException, IllegalArgumentException;

   /**
    * Returns role members based on rolename - depreciated method ethod here only for compatibility with
    * old RoleModule interface 
    *
    * @param roleName
    * @param offset
    * @param limit
    * @param merchantId
    */
   List findRoleMembers(String roleName, int offset, int limit, String userNameFilter , long mechantId) throws IdentityException, IllegalArgumentException;
}
