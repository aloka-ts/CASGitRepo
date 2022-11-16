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

import com.genband.m5.maps.common.entity.Organization;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet </a>
 * @version $Revision: 5946 $
 */
public interface UserModule
{
   /**
    * Retrieve a user by its name.
    *
    * @param userName the user name
    * @return the user
    */
   User findUserByUserName(String userName) throws IdentityException, IllegalArgumentException, NoSuchUserException;

   /**
    * Retrieve a user by its id.
    *
    * @param id the user id
    * @return the user
    * @throws IllegalArgumentException if the id is null
    */
   User findUserById(Object id) throws IdentityException, IllegalArgumentException, NoSuchUserException;

   /**
    * Retrieve a user by its id.
    *
    * @param id the user id
    * @return the user
    * @throws IllegalArgumentException if the id is null or not in the good format
    */
   User findUserById(String id) throws IdentityException, IllegalArgumentException, NoSuchUserException;

   /**
    * Creates a new user with the specified name.
    *
    * @param userName
    * @return the user
    */
   User createUser(String userName, String password) throws IdentityException, IllegalArgumentException;

   /**
    * Creates a new user with the specified name.
    *
    * @param userName
    * @param merchantId of organization to which new user will belong to
    * @return the user
    */
   User createUser(String userName, String password , long merchantId ) throws IdentityException, IllegalArgumentException;
   
   User createUser(String userName, String password , Organization organization) throws IdentityException;

   /**
    * Remove a user.
    *
    * @param id the user id
    */
   void removeUser(Object id) throws IdentityException, IllegalArgumentException;

   /**
    * Get a range of users.
    *
    * @param offset the offset of the first result to retrieve
    * @param limit  the maximum number of users to retrieve
    * @return the user set
    */
   Set findUsers(int offset, int limit) throws IdentityException, IllegalArgumentException;

   /**
    * Get a range of users.
    *
    * @param offset the offset of the first result to retrieve
    * @param limit  the maximum number of users to retrieve
    * @param merchantId of organization(of which users have to be searched)
    * @return the user set
    */
   List findUsers(int offset, int limit , long merchantId , int role) throws IdentityException, IllegalArgumentException;

   /**
    * Get a range of users.
    *
    * @param filter a string filter applied to the user name.
    * @param offset the offset of the frist result to retrieve
    * @param limit  the maximum number of users to retrieve
    * @return the user set
    */
   Set findUsersFilteredByUserName(String filter, int offset, int limit) throws IdentityException, IllegalArgumentException;

   /**
    * Get a range of users.
    *
    * @param filter a string filter applied to the user name.
    * @param offset the offset of the frist result to retrieve
    * @param limit  the maximum number of users to retrieve
    * @param merchantId of organization(of which users have to be searched)
    * @return the user set
    */
   List findUsersFilteredByUserName(String filter, int offset, int limit , long merchantId , int role) throws IdentityException, IllegalArgumentException;

   /**
    * Returns the number of users.
    *
    * @return the number of users
    */
   int getUserCount() throws IdentityException, IllegalArgumentException;
   
   /**
    * method to find if a particular user is in a specific role?
    */
   public boolean isUserInRole(User user , String role);

}
