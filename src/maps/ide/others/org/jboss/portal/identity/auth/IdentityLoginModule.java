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
package org.jboss.portal.identity.auth;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.jboss.portal.common.transaction.Transactions;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.UserStatus;
import org.jboss.security.SimpleGroup;

import com.genband.m5.maps.common.entity.Organization;
import com.genband.m5.maps.identity.GBUserPrincipal;
import com.genband.m5.maps.security.CPFUsernamePasswordLoginModule;

/**
 * A login module that uses the user module.
 * 
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision: 6803 $
 */
public class IdentityLoginModule extends CPFUsernamePasswordLoginModule
{

	private static final Logger log = Logger.getLogger (IdentityLoginModule.class);

   protected String userModuleJNDIName;

   protected String roleModuleJNDIName;

   protected String userProfileModuleJNDIName;

   protected String membershipModuleJNDIName;

   protected String additionalRole;

   protected String havingRole;

   public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options)
   {
      super.initialize(subject, callbackHandler, sharedState, options);

      // Get data
      userModuleJNDIName = (String) options.get("userModuleJNDIName");
      roleModuleJNDIName = (String) options.get("roleModuleJNDIName");
      userProfileModuleJNDIName = (String) options
            .get("userProfileModuleJNDIName");
      membershipModuleJNDIName = (String) options
            .get("membershipModuleJNDIName");
      additionalRole = (String) options.get("additionalRole");
      havingRole = (String) options.get("havingRole");

      // Some info
      log.trace("userModuleJNDIName = " + userModuleJNDIName);
      log.trace("roleModuleJNDIName = " + roleModuleJNDIName);
      log.trace("userProfileModuleJNDIName = " + userProfileModuleJNDIName);
      log.trace("membershipModuleJNDIName = " + membershipModuleJNDIName);
      log.trace("additionalRole = " + additionalRole);
      log.trace("havingRole = " + havingRole);
   }

   private UserModule userModule;

   private RoleModule roleModule;

   private UserProfileModule userProfileModule;

   private MembershipModule membershipModule;

   protected UserModule getUserModule() throws NamingException
   {
      if (userModule == null)
      {
         userModule = (UserModule)new InitialContext().lookup(userModuleJNDIName);
      }
      return userModule;
   }

   protected RoleModule getRoleModule() throws NamingException
   {

      if (roleModule == null)
      {
         roleModule = (RoleModule)new InitialContext().lookup(roleModuleJNDIName);
      }
      return roleModule;
   }

   protected UserProfileModule getUserProfileModule() throws NamingException
   {

      if (userProfileModule == null) {
         userProfileModule = (UserProfileModule) new InitialContext()
               .lookup(userProfileModuleJNDIName);
      }
      return userProfileModule;
   }

   protected MembershipModule getMembershipModule() throws NamingException
   {

      if (membershipModule == null)
      {
         membershipModule = (MembershipModule)new InitialContext().lookup(membershipModuleJNDIName);
      }
      return membershipModule;
   }

   protected String getUsersPassword() throws LoginException
   {
      return "";
   }

   protected boolean validatePassword(final String inputPassword, String expectedPassword)
   {
      if (inputPassword != null)
      {
         try
         {
            try
            {
               HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");

               UserStatus userStatus = getUserStatus(inputPassword);
               
               if (userStatus == UserStatus.DISABLE)
               {
                  request.setAttribute("org.jboss.portal.loginError", "Your account is disabled");
                  return false;
               }
               else if (userStatus == UserStatus.NOTASSIGNEDTOROLE)
               {
                  request.setAttribute("org.jboss.portal.loginError", "The user doesn't have the correct role");
                  return false;
               }
               else if ((userStatus == UserStatus.UNEXISTING) || userStatus == UserStatus.WRONGPASSWORD)
               {
                  request.setAttribute("org.jboss.portal.loginError", "The user doesn't exist or the password is incorrect");
                  return false;
               }
               else if (userStatus == UserStatus.OK)
               {
                  return true;
               }
               else
               {
                  log.error("Unexpected error while logging in");
                  return false;
               }
            }
            catch (Exception e)
            {
               log.error("Error when validating password", e);
            }
         }
         catch (Exception e)
         {
            log.debug("Failed to validate password", e);
         }
      }
      return false;
   }

   protected UserStatus getUserStatus(final String inputPassword)
   {
      UserStatus result = UserStatus.OK;

      try {
         TransactionManager tm = (TransactionManager)new InitialContext().lookup("java:/TransactionManager");
         UserStatus tmp = (UserStatus)Transactions.required(tm, new Transactions.Runnable()
         {
            public Object run() throws Exception
            {
               try
               {
                  User user = getUserModule().findUserByUserName(getUsername());
                  // in case module implementation doesn't throw proper
                  // exception...
                  if (user == null)
                  {
                     throw new NoSuchUserException("UserModule returned null user object");
                  }
                  boolean enabled = false;
                  try {
                     Object enabledS;
                     enabledS = getUserProfileModule().getProperty(user,
                           User.INFO_USER_ENABLED);
                     if (enabledS != null && (enabledS instanceof Boolean)) {
                        enabled = ((Boolean)enabledS).booleanValue();
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
                  if (!enabled) {
                     return UserStatus.DISABLE;
                  }
                  if (havingRole != null)
                  {
                     boolean hasTheRole = false;
                     Set roles = getMembershipModule().getRoles(user);
                     for (Iterator i = roles.iterator(); i.hasNext();)
                     {
                        Role role = (Role)i.next();
                        if (havingRole.equals(role.getName()))
                        {
                           hasTheRole = true;
                           break;
                        }
                     }
                     if (!hasTheRole)
                     {
                        return UserStatus.NOTASSIGNEDTOROLE;
                     }
                  }
                  if (!user.validatePassword(inputPassword))
                  {
                     return UserStatus.WRONGPASSWORD;
                  }
               }
               catch (NoSuchUserException e1)
               {
                  return UserStatus.UNEXISTING;
               }
               catch (Exception e)
               {
                  throw new LoginException(e.toString());
               }
               return null;
            }
         });
         if (tmp != null)
         {
            result = tmp;
         }
      } catch (NamingException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      return result;
   }

   protected Group[] getRoleSets() throws LoginException
   {
      try {
         TransactionManager tm = (TransactionManager) new InitialContext()
               .lookup("java:/TransactionManager");
         return (Group[]) Transactions.required(tm, new Transactions.Runnable()
         {
            public Object run() throws Exception
            {
               try {
                  User user = getUserModule().findUserByUserName(getUsername());
                  Set roles = getMembershipModule().getRoles(user);

                  //
                  Group rolesGroup = new SimpleGroup("Roles");

                  //
                  if (additionalRole != null) {
                     rolesGroup.addMember(createIdentity(additionalRole));
                  }

                  //
                  for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
                     Role role = (Role) iterator.next();
                     String roleName = role.getName();
                     try {
                        Principal p = createIdentity(roleName);
                        rolesGroup.addMember(p);
                     } catch (Exception e) {
                        log.debug("Failed to create principal " + roleName, e);
                     }
                  }

                  //
                  return new Group[] { rolesGroup };
               } catch (Exception e) {
                  throw new LoginException(e.toString());
               }
            }
         });
      } catch (Exception e) {
         Throwable cause = e.getCause();
         throw new LoginException(cause.toString());
      }
   }

   /** Subclass to use the PortalPrincipal to make the username easier to retrieve by the portal. */
   protected Principal createIdentity(String username) throws Exception
   {
	   log.debug ("create identity for " + username);
      return new UserPrincipal(username);
   }
   
   protected Principal createIdentity(final String username, boolean hasOrganization) throws Exception {

	   log.debug ("create identity for " + username + ", with organization ? " + hasOrganization);
	   
	   if (!hasOrganization)
			return createIdentity(username);

	   GBUserPrincipal result = null;
		try {
			TransactionManager tm = (TransactionManager) new InitialContext()
					.lookup("java:/TransactionManager");
			GBUserPrincipal tmp = (GBUserPrincipal) Transactions.required(tm,
					new Transactions.Runnable() {
						public Object run() throws Exception {

							GBUserPrincipal principal = null;
							try {
								User user = getUserModule().findUserByUserName(
										getUsername());
								// in case module implementation doesn't throw
								// proper
								// exception...
								if (user == null) {
									throw new NoSuchUserException(
											"UserModule returned null user object");
								}
								principal = new GBUserPrincipal(user);
								HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
								HttpSession session = request.getSession (true);
								session.setAttribute("User", principal);
								log.debug ("session principal: " + session.getAttribute ("User"));
								log.debug("Session tree: " + session.getAttributeNames());

							} catch (NoSuchUserException e1) {
								log
										.error("Could not find user for an organization: "
												+ username);
							} catch (Exception e) {
								log
										.error("Could not find user for an organization: "
												+ username);
							}
							if (principal == null)
								return null;
							else
								return principal;
						}
					});
			
			if (tmp != null) {
				result = tmp;
			}
		} catch (NamingException e1) {
			log.error ("got naming exception for TM..." + e1);
		}
		return result;
   }
   
   protected Organization getPrincipalOrganization (Principal identity) {
	   
	   
	   if (identity instanceof User
			   && identity != null) {
		   return ((User) identity).getMerchantAccount();
	   }
	   return null;
   }
   
}
