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
package org.jboss.portal.core.ui.portlet.role;

import org.jboss.logging.Logger;
import org.jboss.portal.core.ui.portlet.PortletHelper;
import org.jboss.portal.core.ui.portlet.user.UserPortletConstants;
import org.jboss.portal.core.servlet.jsp.PortalJsp;
import org.jboss.portal.core.servlet.jsp.taglib.context.DelegateContext;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portlet.JBossActionRequest;
import org.jboss.portlet.JBossActionResponse;
import org.jboss.portlet.JBossPortlet;
import org.jboss.portlet.JBossRenderRequest;
import org.jboss.portlet.JBossRenderResponse;
import org.jboss.security.SecurityAssociation;

import com.genband.m5.maps.common.entity.Organization;
import com.genband.m5.maps.identity.GBUserPrincipal;

import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;

/**
 * This portlet aims at managing roles of users.
 *
 * @author <a href="mailto:noel.rocher@free.fr">Noel Rocher</a>
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @author Roy Russo : roy at jboss dot org
 * @version $Revision: 7343 $
 */
public class RolePortlet
   extends JBossPortlet
{
   /** the class logger */
   public static final Logger log = Logger.getLogger(JBossPortlet.class);

   private PortletHelper portletHelper;

   // Render op
   public static final String OP_SHOWSUMMARY = "showSummary";

   public static final String OP_SHOWCREATEROLE = "showCreateRole";

   public static final String OP_SHOWEDITROLE = "showEditRole";

   /** Main Edit role page where admins can filter */
   public static final String OP_SHOWMAINROLEMEMBERS = "showMainEditRoleMembers";

   /** Edit role page where admins can edit the members individually. */
   public static final String OP_SHOWLISTROLEMEMBERS = "showListEditRoleMembers";

   /** Render operation to show the screen to assign roles to a user */
   public static final String OP_SHOWADDROLESTOUSER = "showAddRolesToUser";

   private RoleModule roleModule;

   private UserModule userModule;

   private MembershipModule membershipModule;

   private UserProfileModule userProfileModule;

   private static final String ADMIN_ROLE = "NPM";

   public void init() throws PortletException
   {
      super.init();
      userModule = (UserModule)getPortletContext().getAttribute("UserModule");
      roleModule = (RoleModule)getPortletContext().getAttribute("RoleModule");
      portletHelper = new PortletHelper(this);
      membershipModule = (MembershipModule)getPortletContext().getAttribute("MembershipModule");
      userProfileModule = (UserProfileModule)getPortletContext().getAttribute("UserProfileModule");

      //
      if (userModule == null)
      {
         throw new PortletException("No user module");
      }
      if (roleModule == null)
      {
         throw new PortletException("No role module");
      }
      if (membershipModule == null)
      {
         throw new PortletException("No membership module");
      }
      if (userProfileModule == null)
      {
         throw new PortletException("No user profile module");
      }


   }

   public void destroy()
   {
      super.destroy();

      //
      userModule = null;
      roleModule = null;
      portletHelper = null;
   }

   public String getDefaultOperation()
   {
      return OP_SHOWSUMMARY;
   }

   protected void doView(JBossRenderRequest req, JBossRenderResponse resp)
      throws PortletException, PortletSecurityException, IOException
   {

	  //if (req.isUserInRole(ADMIN_ROLE))
      if (req.getUser().getUserName().equals("root"))
      {
         resp.setContentType("text/html");
         PrintWriter writer = resp.getWriter();

         String op;
         if (req.getWindowState() != WindowState.MAXIMIZED)
         {
            op = getDefaultOperation();
         }
         else
         {
            op = req.getParameters().get(getOperationName(),
               getDefaultOperation());
         }

         Locale locale = req.getLocale();
         ResourceBundle bundle = getResourceBundle(locale);
         if (OP_SHOWSUMMARY.equals(op))
         {
            //if (req.isUserInRole(ADMIN_ROLE))
             if (req.getUser().getUserName().equals("root"))
            {
               DelegateContext ctx = new DelegateContext();
               ctx.put("nbRoles", getNbRolesString(bundle));

               DelegateContext roleCtx = null;
               try
               {
                  Set roles = roleModule.findRoles();
                  Iterator i = roles.iterator();
                  while (i.hasNext())
                  {
                     Role role = (Role)i.next();
                     roleCtx = ctx.next("role");
                     String roleId = role.getId().toString();
                     roleCtx.put("id", roleId);
                     roleCtx.put("displayname", role.getDisplayName());
                     DelegateContext showmembers ;
                     if(role.getName().equals("NPA") || role.getName().equals("NPM")){
                         roleCtx.next("showMembers");
                     }
                  }
               }
               catch (IdentityException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }

               req.setAttribute(PortalJsp.CTX_REQUEST, ctx);
               PortletRequestDispatcher rd = getPortletContext()
                  .getRequestDispatcher("/WEB-INF/jsp/role/menu.jsp");
               rd.include(req, resp);
            }
         }
         else if (OP_SHOWCREATEROLE.equals(op))
         {
            //if (req.isUserInRole(ADMIN_ROLE))
             if (req.getUser().getUserName().equals("root"))
             {
               PortletRequestDispatcher rd = getPortletContext()
                  .getRequestDispatcher("/WEB-INF/jsp/role/createRole.jsp");
               rd.include(req, resp);
            }
         }
         else if (OP_SHOWEDITROLE.equals(op))
         {
            //if (req.isUserInRole(ADMIN_ROLE))
            if (req.getUser().getUserName().equals("root"))
            {
               try
               {
                  Set roles = roleModule.findRoles();
                  DelegateContext ctx = new DelegateContext();
                  ctx.put("editroleid", req.getParameter("roleid"));
                  ctx.put("editroledisplayname", req.getParameter("roledisplayname"));
                  Iterator i = roles.iterator();
                  DelegateContext roleCtx = null;
                  while (i.hasNext())
                  {
                     Role role = (Role)i.next();
                     roleCtx = ctx.next("role");
                     String roleId = role.getId().toString();
                     roleCtx.put("id", roleId);
                     roleCtx.put("displayname", role.getDisplayName());
                     String selectedRoleId = req.getParameter("roleid");
                     if (roleId.equals(selectedRoleId))
                     {
                        roleCtx.put("selected", "selected");
                     }
                  }
                  req.setAttribute(PortalJsp.CTX_REQUEST, ctx);
               }
               catch (IdentityException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               PortletRequestDispatcher rd = getPortletContext()
                  .getRequestDispatcher("/WEB-INF/jsp/role/editRole.jsp");
               rd.include(req, resp);
            }
         }
         else if (OP_SHOWMAINROLEMEMBERS.equals(op))
         {
            //if (req.isUserInRole(ADMIN_ROLE))
            if (req.getUser().getUserName().equals("root"))
            {
               try
               {
                  Set roles = roleModule.findRoles();
                  DelegateContext ctx = new DelegateContext();
                  Iterator i = roles.iterator();
                  DelegateContext roleCtx = null;
                  while (i.hasNext())
                  {
                     Role role = (Role)i.next();
                     roleCtx = ctx.next("role");
                     String roleId = role.getId().toString();
                     roleCtx.put("id", roleId);
                     roleCtx.put("displayname", role.getDisplayName());
                     String selectedRoleId = req.getParameter("roleid");
                     if (roleId.equals(selectedRoleId))
                     {
                        roleCtx.put("selected", "selected");
                     }
                  }
                  req.setAttribute(PortalJsp.CTX_REQUEST, ctx);
               }
               catch (IdentityException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               PortletRequestDispatcher rd = getPortletContext()
                  .getRequestDispatcher("/WEB-INF/jsp/role/editRoleMembers.jsp");
               rd.include(req, resp);
            }
         }
         else if (OP_SHOWLISTROLEMEMBERS.equals(op))
         {
            //if (req.isUserInRole(ADMIN_ROLE))
            if (req.getUser().getUserName().equals("root"))
            {
               try
               {
                  String roleID = req.getParameters().getParameter("roleid");
                  Role role = roleModule.findRoleById(roleID);

                  DelegateContext ctx = new DelegateContext();
                  DelegateContext roleCtx = null;
                  roleCtx = ctx.next("role");
                  ctx.put("displayname", role.getDisplayName());

                  int offset = req.getParameters().getInt("offset", 0);
                  int usersPerPage = req.getParameters().getInt("usersperpage", UserPortletConstants.DEFAULT_USERSPERPAGE);
                  String usernameFilter = req.getParameters().get("usernamefilter", "");
                  ctx.put("usernameFilter", usernameFilter);

                  List users = null;
                  users = membershipModule.findRoleMembers(role.getName(), offset, usersPerPage + 1, usernameFilter.trim(), getMerchantId());

                  User[] usersArray = new User[users.size()];
                  usersArray = (User[])users.toArray(usersArray);

                  DelegateContext rowCtx = null;
                  for (int i = 0; i < Math.min(usersArray.length, usersPerPage); i++)
                  {
                     User user = usersArray[i];
                     rowCtx = ctx.next("row");
                     rowCtx.put("fullname", getFullName(bundle, user));
                     rowCtx.put("username", user.getUserName());

                     //
                     Iterator itRoles = membershipModule.getRoles(user).iterator();
                     while (itRoles.hasNext())
                     {
                        DelegateContext rolesCtx = rowCtx.next("roles");
                        rolesCtx.put("name", ((Role)itRoles.next()).getDisplayName());
                     }

                     PortletURL editURL = resp.createRenderURL();
                     editURL.setParameter(getOperationName(), OP_SHOWADDROLESTOUSER);
                     editURL.setParameter("userid", "" + user.getId());
                     editURL.setParameter("roleid", "" + role.getId());
                     editURL.setParameter("usernamefilter", usernameFilter);
                     editURL.setParameter("offset", "" + offset);
                     editURL.setParameter("usersperpage", "" + usersPerPage);

                     rowCtx.put("editURL", editURL.toString());
                  }

                  if (offset != 0)
                  {
                     PortletURL previousPageLink = resp.createRenderURL();
                     previousPageLink.setParameter(getOperationName(), OP_SHOWLISTROLEMEMBERS);
                     previousPageLink.setParameter("offset", "" + Math.max(0, offset - usersPerPage));
                     previousPageLink.setParameter("usersperpage", "" + usersPerPage);
                     previousPageLink.setParameter("usernamefilter", usernameFilter);
                     previousPageLink.setParameter("roleid", "" + roleID);
                     DelegateContext previousCtx = ctx.next("previouspage");
                     previousCtx.put("link", previousPageLink.toString());
                  }

                  if (usersArray.length > usersPerPage)
                  {
                     PortletURL nextPageLink = resp.createRenderURL();
                     nextPageLink.setParameter(getOperationName(), OP_SHOWLISTROLEMEMBERS);
                     nextPageLink.setParameter("offset", "" + (offset + usersPerPage));
                     nextPageLink.setParameter("usersperpage", "" + usersPerPage);
                     nextPageLink.setParameter("usernamefilter", usernameFilter);
                     nextPageLink.setParameter("roleid", "" + roleID);
                     DelegateContext nextCtx = ctx.next("nextpage");
                     nextCtx.put("link", nextPageLink.toString());
                  }

                  try
                  {
                     Set roles = roleModule.findRoles();
                     Iterator i = roles.iterator();
                     DelegateContext roleCtxs = null;
                     while (i.hasNext())
                     {
                        Role arole = (Role)i.next();
                        roleCtxs = ctx.next("rolelist");
                        String aroleId = arole.getId().toString();
                        roleCtxs.put("id", aroleId);
                        roleCtxs.put("displayname", arole.getDisplayName());
                        if (aroleId.equals(roleID))
                        {
                           roleCtxs.put("selected", "selected");
                        }
                     }
                  }
                  catch (IdentityException e)
                  {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }

                  ctx.put("usernamefilter", usernameFilter);
                  req.setAttribute(PortalJsp.CTX_REQUEST, ctx);
               }
               catch (IdentityException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               PortletRequestDispatcher rd = getPortletContext()
                  .getRequestDispatcher("/WEB-INF/jsp/role/editListRoleMembers.jsp");
               rd.include(req, resp);
            }
         }
         else if (OP_SHOWADDROLESTOUSER.equals(op))
         {
            //if (req.isUserInRole(ADMIN_ROLE))
            if (req.getUser().getUserName().equals("root"))
            {
               DelegateContext ctx = new DelegateContext();

               try
               {
                  ctx.put("usernamefilter", req.getParameter("usernamefilter"));
                  ctx.put("offset", req.getParameter("offset"));
                  ctx.put("usersperpage", req.getParameter("usersperpage"));
                  ctx.put("roleid", req.getParameter("roleid"));


                  User user = userModule.findUserById(req.getParameter("userid"));

                  ctx.put("userid", user.getId().toString());
                  ctx.put("username", user.getUserName());
                  ctx.put("userfullname", getFullName(bundle, user));
                  Set userRoles = membershipModule.getRoles(user);

                  //old stuff
                  Set roles = roleModule.findRoles();
                  Role[] rolesArray = new Role[roles.size()];
                  //rolesArray = (Role[])roles.toArray(rolesArray);
                  Role role = null;

                  DelegateContext allRolesCtx = null;
                  for (Iterator it = roles.iterator(); it.hasNext();)
                  {
                     role = (Role)it.next();
                     allRolesCtx = ctx.next("allRoles");

                     allRolesCtx.put("name", role.getName());
                     allRolesCtx.put("displayname", role.getDisplayName());
                  }

                  DelegateContext userRolesCtx = null;

                  if (!req.getParameterMap().keySet().contains("selectedRoles"))
                  {
                     for (Iterator it = userRoles.iterator(); it.hasNext();)
                     {
                        role = (Role)it.next();
                        userRolesCtx = ctx.next("userRoles");

                        userRolesCtx.put("name", role.getName());
                        userRolesCtx.put("displayname", role.getDisplayName());
                     }
                  }
                  else
                  {
                     String[] selectedRoles = req.getParameterValues("selectedRoles");
                     for (int i = 0; i < selectedRoles.length; i++)
                     {
                        String selectedRole = selectedRoles[i];
                        role = roleModule.findRoleByName(selectedRole);

                        userRolesCtx = ctx.next("userRoles");

                        userRolesCtx.put("name", role.getName());
                        userRolesCtx.put("displayname", role.getDisplayName());
                     }
                  }

               }
               catch (IllegalArgumentException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               catch (IdentityException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }

               req.setAttribute(PortalJsp.CTX_REQUEST, ctx);
               PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/role/editUserRoles.jsp");
               rd.include(req, resp);
            }
         }
         else
         {
            log.error("This operation does not exist when user is logged in:"
               + op);
         }
         writer.close();
      }
      else
      {
         forbidden(req, resp);
      }
   }

   public void showListEditRoleMembers(JBossActionRequest req, JBossActionResponse resp) throws WindowStateException
   {
      resp.setRenderParameter(getOperationName(), OP_SHOWLISTROLEMEMBERS);
      resp.setRenderParameter("roleid", req.getParameter("roleid"));
      resp.setRenderParameter("usersperpage", req.getParameter("usersperpage"));
      resp.setRenderParameter("usernamefilter", req.getParameter("usernamefilter"));
      resp.setWindowState(WindowState.MAXIMIZED);
   }

   /**
    * Handles permissions errors on doView.
    *
    * @param rReq
    * @param rRes
    * @throws javax.portlet.PortletException
    * @throws IOException
    */
   private void forbidden(javax.portlet.RenderRequest rReq, javax.portlet.RenderResponse rRes) throws javax.portlet.PortletException, IOException
   {
      rRes.setContentType("text/html");
      javax.portlet.PortletRequestDispatcher prd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/permission/forbidden.jsp");
      prd.include(rReq, rRes);
   }

   public void createRole(JBossActionRequest req, JBossActionResponse resp)
   {
      //if (req.isUserInRole(ADMIN_ROLE))
	   if (req.getUser().getUserName().equals("root"))
	   {

         String roleName = req.getParameter("rolename");
         String roleDisplayName = req.getParameter("roledisplayname");
         if ((roleName != null) && (roleDisplayName != null)
            && (roleName.length() != 0) && (roleDisplayName.length() != 0))
         {
            if (roleByDisplayNameAvailable(roleDisplayName)
               && roleByNameAvailable(roleName))
            {
               try
               {
                  roleModule.createRole(roleName, roleDisplayName);
/*
                  try
                  {
*/
                  resp.setRenderParameter(getOperationName(),
                     getDefaultOperation());
/*
                     resp.setWindowState(WindowState.NORMAL);
                  }
                  catch (WindowStateException e1)
                  {
                     log.error("Couldn't put the window in normal state");
                  }
*/
               }
               catch (IllegalArgumentException e)
               {
                  log.error("Cannot create role, rolename is null");
                  e.printStackTrace();
               }
               catch (IdentityException e)
               {
                  log.error("Cannot create role \"" + roleName
                     + "\", unexpected error");
                  e.printStackTrace();
               }
            }
            else
            {
               if (!roleByDisplayNameAvailable(roleDisplayName))
               {
                  // Role with that display name already exists !
                  portletHelper.setRenderParameter(resp, "rolename", roleName);
                  portletHelper.setRenderParameter(resp, "roledisplayname",
                     roleDisplayName);
                  resp.setRenderParameter("roledisplayname_error",
                     "ROLE_ERROR_DISPLAYNAMEALREADYEXISTS");
                  resp.setRenderParameter(getOperationName(),
                     getDefaultOperation());
               }
               else if (!roleByNameAvailable(roleName))
               {
                  // Role with that name already exists !
                  portletHelper.setRenderParameter(resp, "rolename", roleName);
                  portletHelper.setRenderParameter(resp, "roledisplayname",
                     roleDisplayName);
                  resp.setRenderParameter("rolename_error",
                     "ROLE_ERROR_NAMEALREADYEXISTS");
                  resp.setRenderParameter(getOperationName(),
                     getDefaultOperation());
               }
            }
         }
         else
         {
            // Role name or displayname empty
            portletHelper.setRenderParameter(resp, "rolename", roleName);
            portletHelper.setRenderParameter(resp, "roledisplayname",
               roleDisplayName);
            if (roleName == null || roleName.length() == 0)
            {
               resp
                  .setRenderParameter("rolename_error",
                     "ROLE_ERROR_NAMEEMPTY");
            }
            if (roleDisplayName == null || roleDisplayName.length() == 0)
            {
               resp.setRenderParameter("roledisplayname_error",
                  "ROLE_ERROR_DISPLAYNAMEEMPTY");
            }
            resp.setRenderParameter(getOperationName(),
               getDefaultOperation());
         }
      }
   }

   /**
    * Action to edit a role
    *
    * @param req
    * @param resp
    */
   public void editRole(JBossActionRequest req, JBossActionResponse resp) throws WindowStateException
   {
      //if (req.isUserInRole(ADMIN_ROLE))
      if (req.getUser().getUserName().equals("root"))

      {
         String roleID = req.getParameters().getParameter("roleid");
         String newDisplayName = req.getParameter("roledisplayname");
         Role role = null;
         try
         {
            role = roleModule.findRoleById(roleID);
            // Check that the new display name is available
            if (!"".equals(newDisplayName))
            {
               if (roleByDisplayNameAvailable(newDisplayName))
               {
                  role.setDisplayName(newDisplayName);
               }
               else
               // The display name is not available, go back to the prefilled form
               {
                  portletHelper.setRenderParameter(resp, "roledisplayname",
                     newDisplayName);
                  portletHelper.setRenderParameter(resp, "roleid", roleID);
                  resp.setRenderParameter("roledisplayname_error",
                     "ROLE_ERROR_DISPLAYNAMEALREADYEXISTS");
                  resp.setRenderParameter(getOperationName(), OP_SHOWEDITROLE);
               }
            }
            else
            {
               portletHelper.setRenderParameter(resp, "roledisplayname",
                  newDisplayName);
               portletHelper.setRenderParameter(resp, "roleid", roleID);
               resp.setRenderParameter("roledisplayname_error",
                  "ROLE_ERROR_NAMEEMPTY");
               resp.setRenderParameter(getOperationName(), OP_SHOWEDITROLE);
            }
         }
         catch (IllegalArgumentException e)
         {
            log.error("Cannot update role: roleID is null");
            e.printStackTrace();
         }
         catch (IdentityException e)
         {
            log.error("Cannot update role, unexpected error");
            e.printStackTrace();
         }
      }
   }

   /**
    * Adds or Removes roles from a user.
    *
    * @param request
    * @param response
    */
   public void addRolesToUser(JBossActionRequest request, JBossActionResponse response)
   {

      //if (request.isUserInRole(ADMIN_ROLE))
      if (request.getUser().getUserName().equals("root"))
      {
         String userId = request.getParameters().getParameter("userid");

         response.setRenderParameter("usernamefilter", request.getParameter("usernamefilter"));
         response.setRenderParameter("offset", request.getParameter("offset"));
         response.setRenderParameter("roleid", request.getParameter("roleid"));
         response.setRenderParameter("usersperpage", request.getParameter("usersperpage"));

         if (request.getParameterMap().keySet().contains("addRoles"))
         {
            String[] selectedRoles = request.getParameterValues("assignedRoles");
            String[] rolesToAdd = request.getParameterValues("rolesToAdd");

            if (selectedRoles == null)
            {
               selectedRoles = new String[]{};
            }
            if (rolesToAdd == null)
            {

               if (selectedRoles.length != 0)
               {
                  response.setRenderParameter("selectedRoles", selectedRoles);
               }

               response.setRenderParameter("op", OP_SHOWADDROLESTOUSER );
               response.setRenderParameter("userid",  userId);
               return;
            }


            Set roles = new HashSet();
            List l1 = Arrays.asList(selectedRoles);
            List l2 = Arrays.asList(rolesToAdd);
            roles.addAll(l1);
            roles.addAll(l2);

            String[] roleNames = new String[roles.size()];
            roleNames = (String[])roles.toArray(roleNames);

            response.setRenderParameter("selectedRoles", roleNames);
            response.setRenderParameter("op", OP_SHOWADDROLESTOUSER );
            response.setRenderParameter("userid",  userId);
         }
         else if (request.getParameterMap().keySet().contains("removeRoles"))
         {

            String[] selectedRoles = request.getParameterValues("assignedRoles");
            String[] rolesToRemove = request.getParameterValues("selectedRoles");

            if (selectedRoles == null)
            {
               selectedRoles = new String[]{};
            }

            if (rolesToRemove == null)
            {
               if (selectedRoles.length != 0)
               {
                  response.setRenderParameter("selectedRoles", selectedRoles);
               }

               response.setRenderParameter("op", OP_SHOWADDROLESTOUSER );
               response.setRenderParameter("userid",  userId);
               return;
            }
            
            List toRemove = Arrays.asList(rolesToRemove);

            Set roles = new HashSet();

            for (int i = 0; i < selectedRoles.length; i++)
            {
               String selectedRole = selectedRoles[i];

               if (!toRemove.contains(selectedRole))
               {
                  roles.add(selectedRole);
               }
            }

            String[] roleNames = new String[roles.size()];
            roleNames = (String[])roles.toArray(roleNames);

            if (roleNames.length != 0)
            {
               response.setRenderParameter("selectedRoles", roleNames);
            }

            response.setRenderParameter("op", OP_SHOWADDROLESTOUSER );
            response.setRenderParameter("userid",  userId);
         }
         else
         {
            
            String[] selectedRoles = request.getParameterValues("assignedRoles");

            try
            {
               User user = userModule.findUserById(userId);
               Set roles = roleModule.findRolesByNames(selectedRoles);
               membershipModule.assignRoles(user, roles);

               response.setRenderParameter("op", OP_SHOWLISTROLEMEMBERS );
               response.setRenderParameter("userid",  userId);
            }
            catch (IllegalArgumentException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            catch (IdentityException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }
   }

   /**
    * Action to delete a role
    *
    * @param req
    * @param resp
    */
   public void removeRole(JBossActionRequest req, JBossActionResponse resp)
   {


      //if (req.isUserInRole(ADMIN_ROLE))
      if (req.getUser().getUserName().equals("root"))
      {
         String roleId = req.getParameters().getParameter("roleid");
         try
         {
            Role role = roleModule.findRoleById(roleId);
            roleModule.removeRole(role.getId());
            resp.setRenderParameter("roledelete_error", "ROLE_DELETED");
            resp.setRenderParameter(getOperationName(), OP_SHOWEDITROLE);
         }
         catch (IdentityException e)
         {
            log.error("Cannot remove role, unexpected error", e);
            portletHelper.setRenderParameter(resp, "roleid", roleId);
            resp.setRenderParameter("roledelete_error", "ROLE_ERROR_DELETE_FAILED");
            resp.setRenderParameter(getOperationName(), OP_SHOWEDITROLE);
         }
      }
   }

   /**
    * Returns true if the role with display name in argument is available
    *
    * @param name Display name of the role to check
    * @return true if the display name is available
    */
   private boolean roleByDisplayNameAvailable(String name)
   {
      try
      {
         for (Iterator i = roleModule.findRoles().iterator(); i.hasNext();)
         {
            Role role = (Role)i.next();
            if (role.getDisplayName() != null && role.getDisplayName().equals(name))
            {
               return false;
            }
         }
      }
      catch (IdentityException e)
      {
         return false;
      }
      return true;
   }

   /** Returns true if the role with name in argument is available */
   private boolean roleByNameAvailable(String name)
   {
      Role role = null;
      try
      {
         role = roleModule.findRoleByName(name);
         //in case that exception was not thrown
         if (role == null)
         {
            return true;
         }
      }
      catch (IllegalArgumentException e)
      {
         e.printStackTrace();
      }
      catch (IdentityException e)
      {
         return true;
      }
      return false;
   }

   /** Return a localized sentence stating the number of existing roles */
   private String getNbRolesString(ResourceBundle bundle)
   {
      StringBuffer buffer = new StringBuffer();
      int nbRoles = 0;
      try
      {
         nbRoles = roleModule.getRolesCount();
      }
      catch (IllegalArgumentException e)
      {
         e.printStackTrace();
      }
      catch (IdentityException e)
      {
         e.printStackTrace();
      }
      if (nbRoles <= 1)
      {
         buffer.append(bundle.getString("ROLE_THEREIS")).append(" ");
         if (nbRoles == 0)
         {
            buffer.append("no");
         }
         else
         {
            buffer.append("1");
         }
         buffer.append(" ").append(bundle.getString("ROLE_ROLEDEFINED"));
      }
      else
      {
         buffer.append(bundle.getString("ROLE_THEREARE")).append(" ");
         buffer.append(nbRoles);
         buffer.append(" ").append(bundle.getString("ROLE_ROLESDEFINED"));
      }
      return buffer.toString();
   }

   /**
    * This is a helper method extracted from the userPortlet that returns the user's fullname.
    *
    * @param bundle
    * @param user
    */
   private String getFullName(ResourceBundle bundle,
                              User user)
   {
      String givenName = null;
      String familyName = null;

      try
      {
         givenName = (String)userProfileModule.getProperty(user, User.INFO_USER_NAME_GIVEN);
         familyName = (String)userProfileModule.getProperty(user, User.INFO_USER_NAME_FAMILY);
      }
      catch (IdentityException e)
      {
         log.error("cannot obtain user profile information: ", e);
      }

      if ((givenName != null) && (givenName.trim().length() != 0))
      {
         if ((familyName != null) && (familyName.trim().length() != 0))
         {
            return givenName + " " + familyName;
         }
         else
         {
            return givenName.trim();
         }
      }
      else
      {
         if ((familyName != null) && (familyName.trim().length() != 0))
         {
            return familyName.trim();
         }
         else
         {
            return bundle.getString("NAMENOTAVAILABLE");
         }
      }
   }
   private long getMerchantId(){
	   	Set<Principal> s = SecurityAssociation.getSubject().getPrincipals();
		
		for (Principal principal : s) {
			log.debug ("sub principal: " + principal.getClass().getName());
			if (principal instanceof GBUserPrincipal) {
				log.debug ("p: " + principal);
				
				Organization enterprise = ((GBUserPrincipal) principal).getMerchantAccount();
				long enterpriseId = enterprise.getOrganizationId();
				log.debug("enterpriseId of user who has logged in is : = " + enterpriseId);
				return enterpriseId;
			}
	
		}
		return UserPortletConstants.INVALID_MERCHANT_ID;
 }

}