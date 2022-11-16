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

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;

import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet </a>
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @author Roy Russo : roy at jboss dot org
 * @version $Revision: 5885 $
 */
public interface RoleModule
{
   /**
    * Retrieves a role by its name
    *
    * @param name the role name
    * @return the role
    */
   Role findRoleByName(String name) throws IdentityException, IllegalArgumentException;

   /**
    * Retrieve a collection of role from the role names.
    *
    * @param names the role names
    * @return a collection of roles
    * @throws IllegalArgumentException
    */
   Set findRolesByNames(String[] names) throws IdentityException, IllegalArgumentException;

   /**
    * Retrieves a role by its id.
    *
    * @param id the role id
    * @return the role
    */
   Role findRoleById(Object id) throws IdentityException, IllegalArgumentException;

   /**
    * Retrieves a role by its id.
    *
    * @param id the role id
    * @return the role
    */
   Role findRoleById(String id) throws IdentityException, IllegalArgumentException;

   /**
    * Create a new role with the specified name.
    *
    * @param name        the role name
    * @param displayName the role display name
    * @return the role
    */
   Role createRole(String name, String displayName) throws IdentityException, IllegalArgumentException;

   /**
    * Remove a role.
    *
    * @param id the role id
    */
   void removeRole(Object id) throws IdentityException, IllegalArgumentException;

   /**
    * Returns the number of roles.
    *
    * @return the number of roles
    */
   int getRolesCount() throws IdentityException;

   /**
    * Get all the roles
    *
    * @return the roles
    */
   Set findRoles() throws IdentityException;

   /**
    * Get all the roles
    * @param roles of the user who has logged in 
    * @return the roles
    */
   Set findRoles(int role) throws IdentityException;


}
