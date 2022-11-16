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
package org.jboss.portal.core.ui.portlet.user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.jboss.logging.Logger;
import org.jboss.portal.api.node.PortalNode;
import org.jboss.portal.api.node.PortalNodeURL;
import org.jboss.portal.common.i18n.LocaleFormat;
import org.jboss.portal.common.i18n.LocaleManager;
import org.jboss.portal.common.net.URLTools;
import org.jboss.portal.common.p3p.P3PConstants;
import org.jboss.portal.common.util.ConversionException;
import org.jboss.portal.common.util.Tools;
import org.jboss.portal.core.aspects.controller.node.Navigation;
import org.jboss.portal.core.modules.MailModule;
import org.jboss.portal.core.servlet.jsp.PortalJsp;
import org.jboss.portal.core.servlet.jsp.taglib.context.DelegateContext;
import org.jboss.portal.core.ui.portlet.PortletHelper;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.db.HibernateRoleImpl;
import org.jboss.portal.identity.db.HibernateUserImpl;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeInfo;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portlet.JBossActionRequest;
import org.jboss.portlet.JBossActionResponse;
import org.jboss.portlet.JBossPortlet;
import org.jboss.portlet.JBossRenderRequest;
import org.jboss.portlet.JBossRenderResponse;
import org.jboss.security.SecurityAssociation;

import com.genband.m5.maps.common.entity.Organization;
import com.genband.m5.maps.identity.GBUserPrincipal;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This portlet aims at managing users
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @author <a href="mailto:mageshbk@jboss.com">Magesh Kumar Bojan</a>
 * @version $Revision: 7379 $
 */
public class UserPortlet
   extends JBossPortlet
{
	private static Logger LOG = Logger.getLogger(UserPortlet.class);

   /** The class logger. */
   public static final Logger log = Logger.getLogger(JBossPortlet.class);

   /** Render operation to show the login screen. */
   public static final String OP_SHOWLOGIN = "showLogin";

   /** Render operation to show the register screen. */
   public static final String OP_SHOWREGISTER = "showRegister";

   /** Render operation to show the register thankyou screen. */
   public static final String OP_SHOWREGISTER_TY = "showRegisterty";

   /** Render operation to show the user menu screen. */
   public static final String OP_SHOWMENU = "showMenu";

   /** Render operation to show the user profile to edit. */
   public static final String OP_SHOWPROFILE = "showProfile";

   /** Render operation to show the screen to assign roles to a user. */
   public static final String OP_SHOWADDROLESTOUSER = "showAddRolesToUser";

   /** Render operation to show the list of users. */
   public static final String OP_SHOWLISTUSERS = "showListUsers";

   /** Logout the user. */
   public static final String OP_USERLOGOUT = "userLogout";

   /** Deletes the user */
   public static final String OP_DELETEUSER = "deleteUser";

   /** Activate the user via email link */
   public static final String OP_ACTIVATEUSER = "activate";

   private UserModule userModule;
   private RoleModule roleModule;
   private MembershipModule membershipModule;
   private UserProfileModule userProfileModule;
   private MailModule mailModule;
   private PortletHelper portletHelper;
   public static final short UNDEFINED_TIMEZONE = (short)0;
   private long n= 0 ;
   private long prevn[] = new long[20] ;
   private int pageNo =0 ;
   

   /**
    * init method of the portlet, Setting up the diffrent modules used.
    *
    * @throws PortletException If a module cannot be looked up.
    */
   public void init()
      throws PortletException
   {
      super.init();

      //
      userModule = (UserModule)getPortletContext().getAttribute("UserModule");
      roleModule = (RoleModule)getPortletContext().getAttribute("RoleModule");
      membershipModule = (MembershipModule)getPortletContext().getAttribute("MembershipModule");
      userProfileModule = (UserProfileModule)getPortletContext().getAttribute("UserProfileModule");
      mailModule = (MailModule)getPortletContext().getAttribute("MailModule");
      portletHelper = new PortletHelper(this);

      //
      if (userModule == null)
      {
         throw new PortletException("No user module");
      }
      if (roleModule == null)
      {
         throw new PortletException("No role module");
      }
      if (mailModule == null)
      {
         throw new PortletException("No mail module");
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
      mailModule = null;
      portletHelper = null;
   }

   public String getDefaultOperation()
   {
      return OP_SHOWLOGIN;
   }

   /**
    * doView method
    *
    * @param req  Render request
    * @param resp render response
    * @throws PortletException         DOCUMENT_ME
    * @throws PortletSecurityException DOCUMENT_ME
    * @throws IOException              DOCUMENT_ME
    */
   protected void doView(JBossRenderRequest req, JBossRenderResponse resp)
      throws PortletException,
      PortletSecurityException,
      IOException
   {
      String op;
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();
/*
      if (req.getRemoteUser() == null)
      {
         req.getPortletSession().invalidate();
         PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/login.jsp");
         PortalNode currentNode = req.getPortalNode();
         PortalNodeURL url = resp.createRenderURL(currentNode);
         url.setAuthenticated(Boolean.TRUE);
         if ("1".equals(getPortletConfig().getInitParameter("useSSL")))
         {
            url.setSecure(Boolean.TRUE);
            req.setAttribute("secureURL", url.toString());
         }
         url.setSecure(null);
         req.setAttribute("URL", url.toString());
         rd.include(req, resp);
         return;
      }
*/
      if (req.getRemoteUser() != null)
      {
         if (req.getWindowState() != WindowState.MAXIMIZED)
         {
            op = OP_SHOWMENU;
         }
         else
         {
            op = req.getParameters().get(getOperationName(),
               OP_SHOWMENU);
            // Because of the automatic redirection
            if (op.equals(OP_SHOWLOGIN))
            {
               op = OP_SHOWMENU;
            }
         }

         Locale requestLocale = req.getLocale();
         ResourceBundle bundle = getResourceBundle(requestLocale);
         if (OP_SHOWPROFILE.equals(op))
         {
            String userid = req.getParameters().getParameter("userid");
            User user = null;
            if (userid != null)
            {
               try
               {
                  user = userModule.findUserById(userid);
               }
               catch (Exception e)
               {
                  log.error("Cannot retrive user", e);
               }
            }
            else
            {
               user = req.getUser();
               try
               {
                  user = userModule.findUserById(user.getId());
               }
               catch (IdentityException e)
               {
                  log.error("Cannot retrive user", e);
               }
            }

            // Validate we have a user object
            if (user == null)
            {
               throw new PortletException("Not user object found");
            }

            //
            DelegateContext ctx = new DelegateContext();

            //
            ctx.put("userid", user.getId().toString());
            fillContextWithUserProfile(user, ctx);

            //
            //String selectedTimeZone = (String)user.getProfile().get(User.INFO_USER_TIME_ZONE_OFFSET);
            String selectedTimeZone = (String)getProperty(user, User.INFO_USER_TIME_ZONE_OFFSET);
            for (int i = 0; i < UserPortletConstants.TIME_ZONE_OFFSETS.length; i++)
            {
               if (UserPortletConstants.TIME_ZONE_OFFSETS[i] != null)
               {
                  DelegateContext timeZoneCtx = ctx.next("timezone");
                  timeZoneCtx.put("name", UserPortletConstants.TIME_ZONE_OFFSETS[i]);
                  timeZoneCtx.put("id", "" + i);
                  if (selectedTimeZone != null && selectedTimeZone.equals("" + i))
                  {
                     timeZoneCtx.put("selected", "selected");
                  }
               }
            }

            //
            //String selectedLocale = (String)user.getProfile().get(User.INFO_USER_LOCALE);
            String selectedLocale = (String)getProperty(user, User.INFO_USER_LOCALE);
            
            ArrayList locales = new ArrayList(LocaleManager.getLocales());
            Collections.sort(locales, new LocaleComparator());
            
            for (Iterator i = locales.iterator(); i.hasNext();)
            {
               Locale locale = (Locale)i.next();
               DelegateContext localeCtx = ctx.next("locale");
               localeCtx.put("name", locale.getDisplayName(requestLocale));
               String localeString = locale.toString();
               localeCtx.put("id", localeString);
               if (selectedLocale != null && selectedLocale.equals(localeString))
               {
                  localeCtx.put("selected", "selected");
               }
            }

            //
            //String selectedTheme = (String)user.getProfile().get(User.INFO_USER_THEME);
            String selectedTheme = (String)getProperty(user,User.INFO_USER_THEME);
            ThemeService themeService = (ThemeService)getPortletContext().getAttribute("ThemeService");
            for (Iterator i = themeService.getThemes().iterator(); i.hasNext();)
            {
               PortalTheme theme = (PortalTheme)i.next();
               ThemeInfo info = theme.getThemeInfo();
               DelegateContext themeCtx = ctx.next("theme");
               themeCtx.put("name", info.getAppId() + "." + info.getName());
               themeCtx.put("id", info.getRegistrationId().toString());
               if ((selectedTheme != null) && selectedTheme.equals(info.getRegistrationId().toString()))
               {
                  themeCtx.put("selected", "selected");
               }
            }

            //
            req.setAttribute(PortalJsp.CTX_REQUEST, ctx);
            resp.setTitle(bundle.getString("REGISTER_PERSONALINFO"));
            PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/editProfile.jsp");
            rd.include(req, resp);
         }
         else if (OP_SHOWMENU.equals(op))
         {
	       	  log.debug("before showing menu user is : " + req.getUser().getUserName());
	    	  log.debug( "user in role NPA ? " + req.isUserInRole("NPA"));
	    	  log.debug( "user in role Admin ? " + req.isUserInRole("Admin"));
	    	  log.debug( "user in role NPM ? " + req.isUserInRole("NPM"));
	    	  log.debug( "user in role User ? " + req.isUserInRole("User"));
	    	  log.debug( "user in role SPA ? " + req.isUserInRole("SPA"));
	
            DelegateContext ctx = new DelegateContext();
            if ( req.isUserInRole("NPM") || req.isUserInRole("SPA") || req.getUser().getUserName().equals("root"))
            {
               ctx.next("admin");
               
            }
            if ( req.isUserInRole("SPA") || req.getUser().getUserName().equals("root")){
            	DelegateContext createCtx = ctx.next("createMode");
            }
            if (req.getParameter("modifiedProfile") != null)
            {
                ctx.next("modifiedProfile");
            }
            req.setAttribute(PortalJsp.CTX_REQUEST, ctx);
            PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/menu.jsp");
            rd.include(req, resp);
            //PortletContext c = req.ge;

         }
         
         
         else if (OP_SHOWLISTUSERS.equals(op))
         {
             if (req.isUserInRole("NPM") || req.isUserInRole("SPA") || req.getUser().getUserName().equals("root"))
            {
               try
               {
                  int offset = req.getParameters().getInt("offset", 0);
                  int usersPerPage = req.getParameters().getInt("usersperpage", UserPortletConstants.DEFAULT_USERSPERPAGE);
                  String usernameFilter = req.getParameters().get("usernamefilter", "");
                  // We get usersPerPage + 1 entries so we can know if there is
                  // a next page
                  // It's tricky but it avoids to make 2 queries.
                  List users;
            	  int role = UserPortletConstants.INVALID_ROLE;
            	  if(req.getUser().getUserName().equals("root")){
            		  role = UserPortletConstants.ROOT;
            	  }else if(req.isUserInRole("SPA")){
            		  role = UserPortletConstants.SPA;
            	  }else if(req.isUserInRole("NPM")){
            		  role = UserPortletConstants.NPM;
            	  }
                  
                  if (usernameFilter.trim().length() == 0)
                  {
                	  //users = userModule.findUsers(offset, usersPerPage + 1);
                	  /* PR 51046
                	   * changed to get users related to a particular organization only
                	   */
                	  log.debug("user is : " + req.getUser().getUserName());
                	  log.debug( "user in role NPA ? " + req.isUserInRole("NPA"));
                	  log.debug( "user in role Admin ? " + req.isUserInRole("Admin"));
                	  log.debug( "user in role NPM ? " + req.isUserInRole("NPM"));
                	  log.debug( "user in role User ? " + req.isUserInRole("User"));
                	  log.debug( "user in role SPA ? " + req.isUserInRole("SPA"));
                	  
                	  users = userModule.findUsers(offset, usersPerPage + 1 , getMerchantId() ,role ) ;
                  }
                  else
                  {
                	  /* PR 51046
                	   * changed to get users related to a particular organization only
                	   */
                	  //users = userModule.findUsersFilteredByUserName(usernameFilter, offset, usersPerPage + 1);

                	  log.debug( "user in role NPM ? " + req.isUserInRole("NPM"));
                	  log.debug( "user in role NPA ? " + req.isUserInRole("NPA"));
                	  log.debug( "user in role User ? " + req.isUserInRole("User"));
                	  log.debug( "user in role SPA ? " + req.isUserInRole("SPA"));
                	  
                	  //users = userModule.findUsers(offset, usersPerPage + 1 , getMerchantId() ,role ) ;
                        
                	  users = userModule.findUsersFilteredByUserName(usernameFilter, offset, usersPerPage + 1 , getMerchantId(),role);
                  }
                  log.debug("no. of users retreived : " + users.size());
                  //log.debug("returned class is : " + users.iterator().next().getClass().getName());
                  
                  Iterator it = users.iterator();
                  //int n = 0;
                  //int prevn = n;
                  prevn[pageNo] = n;
                  n =0 ;
                  
                  int index = 0;
                  while(it.hasNext() && index < usersPerPage){
                	  HibernateUserImpl u = (HibernateUserImpl)it.next();
                	  //n++;
                	  index++;
                	  if(u.getRoles().size() != 0){
                		  n = n + u.getRoles().size() ;
                	  }else{
                		  n++;
                	  }
                  }
                  
                  User[] usersArray = new User[users.size()];
                  usersArray = (User[])users.toArray(usersArray);
                  DelegateContext ctx = new DelegateContext();
                  DelegateContext rowCtx = null;
                  for (int i = 0; i < Math.min(usersArray.length, usersPerPage); i++)
                  {
                	  //HibernateUserImpl user = (HibernateUserImpl)users.iterator().next();
                     //User user = (User)huser;
                     User user = (User)usersArray[i];
                	 rowCtx = ctx.next("row");
                     rowCtx.put("firstname", getFirstName(bundle, user));
                     rowCtx.put("lastname", getLastName(bundle, user));
                     rowCtx.put("username", user.getUserName());
                     if (i % 2 == 0)
                     {
                        rowCtx.put("css-class", "portlet-table-text");
                     }
                     else
                     {
                        rowCtx.put("cssClass", "portlet-table-alternate");
                     }
                     
                     //
                     Iterator itRoles = membershipModule.getRoles(user).iterator();
                     while (itRoles.hasNext())
                     {
                        DelegateContext rolesCtx = rowCtx.next("roles");
                        rolesCtx.put("name", ((Role)itRoles.next()).getDisplayName());
                     }

                     PortletURL editURL = resp.createRenderURL();
                     editURL.setParameter(getOperationName(), OP_SHOWPROFILE);
                     editURL.setParameter("userid", "" + user.getId());

                     PortletURL rolesURL = resp.createRenderURL();
                     rolesURL.setParameter(getOperationName(), OP_SHOWADDROLESTOUSER);
                     rolesURL.setParameter("userid", "" + user.getId());
                     rolesURL.setParameter("usernamefilter", usernameFilter);
                     rolesURL.setParameter("offset", "" + offset);
                     rolesURL.setParameter("usersperpage", "" + usersPerPage);
                     if(req.isUserInRole("SPA") || req.getUser().getUserName().equals("root")) {
                    	 
	                     DelegateContext editCtx = rowCtx.next("editMode");
	                     
	                     editCtx.put("editURL", editURL.toString());
	                     editCtx.put("rolesURL", rolesURL.toString());
	                     if(!user.getUserName().equals(req.getUser().getUserName()) && !user.getUserName().equals("root")){
	                    	 PortletURL deleteUrl = resp.createActionURL();
	                         deleteUrl.setParameter(getOperationName(), OP_DELETEUSER);
	                         deleteUrl.setParameter("userid", "" + user.getId());
	                         DelegateContext deleteCtx = editCtx.next("deleteUser");
	                         deleteCtx.put("deleteURL", deleteUrl.toString());
	                     }
                     }

                  }

                  if (offset != 0)
                  {
                     PortletURL previousPageLink = resp.createRenderURL();
                     previousPageLink.setParameter(getOperationName(), OP_SHOWLISTUSERS);
                     previousPageLink.setParameter("offset", "" + Math.max(0, offset - usersPerPage));
                     previousPageLink.setParameter("usersperpage", "" + usersPerPage);
                     previousPageLink.setParameter("usernamefilter", usernameFilter);
                     DelegateContext previousCtx = ctx.next("previouspage");
                     previousCtx.put("link", previousPageLink.toString());
                  }

                  if (usersArray.length > usersPerPage)
                  {
                     PortletURL nextPageLink = resp.createRenderURL();
                     nextPageLink.setParameter(getOperationName(), OP_SHOWLISTUSERS);
                     nextPageLink.setParameter("offset", "" + (offset + usersPerPage));
                     nextPageLink.setParameter("usersperpage", "" + usersPerPage);
                     nextPageLink.setParameter("usernamefilter", usernameFilter);
                     DelegateContext nextCtx = ctx.next("nextpage");
                     nextCtx.put("link", nextPageLink.toString());
                  }

                  ctx.put("usernamefilter", usernameFilter);
                  ctx.put("results", usersArray.length + "");
                  req.setAttribute(PortalJsp.CTX_REQUEST, ctx);
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

               PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/listUsers.jsp");
               rd.include(req, resp);
            }
         }
         else if (OP_SHOWADDROLESTOUSER.equals(op))
         {
             if (req.isUserInRole("NPM") || req.isUserInRole("SPA") || req.getUser().getUserName().equals("root"))
            {
               DelegateContext ctx = new DelegateContext();

               ctx.put("usernamefilter", req.getParameter("usernamefilter"));
               ctx.put("offset", req.getParameter("offset"));
               ctx.put("usersperpage", req.getParameter("usersperpage"));
               
               try
               {
                  User user = userModule.findUserById(req.getParameter("userid"));
                  ctx.put("userid", user.getId().toString());
                  ctx.put("username", user.getUserName());
                  ctx.put("userfullname", getFullName(bundle, user));
                  Set userRoles = membershipModule.getRoles(user);

                  //Set roles = roleModule.findRoles();
                  Set roles = null;
                  if(req.getUser().getUserName().equals("root")){
                	  roles = roleModule.findRoles(UserPortletConstants.ROOT);
                	 
                  } else if(req.isUserInRole("SPA")){
                	  roles = roleModule.findRoles(UserPortletConstants.SPA);
                  }

                  //roles = roleModule.findRoles();

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
               PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/editUserRoles.jsp");
               rd.include(req, resp);
            }
         }
         else if (OP_SHOWREGISTER.equals(op))
         {
             if (req.isUserInRole("NPM") || req.isUserInRole("SPA") || req.getUser().getUserName().equals("root"))
            {
               DelegateContext ctx = new DelegateContext();
               ctx.put("lastView", req.getParameters().get("lastView", "showMenu"));
               req.setAttribute(PortalJsp.CTX_REQUEST, ctx);

               PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/registerUser.jsp");
               rd.include(req, resp);
            }
         }
         else if (OP_SHOWREGISTER_TY.equals(op))
         {
             if (req.isUserInRole("NPM") || req.isUserInRole("SPA") || req.getUser().getUserName().equals("root"))
            {
               PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/registerUser_admin.jsp");
               rd.include(req, resp);
            }
         }
         else
         {
            log.error("This operation does not exist when user is logged in:" + op);
         }
      }
      else
      {
         // User is not logged in
         op = req.getParameters().get(getOperationName(), OP_SHOWLOGIN);

         if (req.getWindowState() != WindowState.MAXIMIZED && ! OP_SHOWREGISTER_TY.equals(op) && ! OP_ACTIVATEUSER.equals(op))
         {
            op = OP_SHOWLOGIN;
         }

         if (OP_SHOWREGISTER.equals(op))
         {
            PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/register.jsp");
            rd.include(req, resp);
         }
         else if (OP_SHOWREGISTER_TY.equals(op))
         {
            PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/register_ty.jsp");
            rd.include(req, resp);
         }
         else if (OP_SHOWLOGIN.equals(op))
         {
            PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/login.jsp");
            rd.include(req, resp);
         }
         else if (OP_ACTIVATEUSER.equals(op))
         {
            String hash = req.getParameter(UserPortletConstants.HASH);
            String userId = req.getParameters().getParameter(UserPortletConstants.USERID);

            User user;
            PortletRequestDispatcher rd;
            try
            {
               user = userModule.findUserById(userId);
               String hexCompare = Tools.md5AsHexString(user.getUserName() + getProperty(user,User.INFO_USER_REGISTRATION_DATE).toString() + UserPortletConstants.SALT);
               if (hash.equals(hexCompare))
               {
                  setProperty(user, User.INFO_USER_ENABLED, Boolean.TRUE);
                  rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/register_ty.jsp");
                  PortalNode currentNode = Navigation.getCurrentNode();
                  PortalNodeURL url = resp.createRenderURL(currentNode);
                  url.setAuthenticated(Boolean.TRUE);
                  if ("1".equals(getPortletConfig().getInitParameter("useSSL")))
                  {
                     url.setSecure(Boolean.TRUE);
                     req.setAttribute("secureURL", url.toString());
                  }
                  url.setSecure(null);
                  req.setAttribute("URL", url.toString());
               }
               else
               {
                  rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/login.jsp");
               }
            }
            catch (Exception e)
            {
               rd = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/user/login.jsp");
            }
            rd.include(req, resp);
         }
         else
         {
            log.error("This operation does not exist when user is not logged in:" + op);
         }
      }

      writer.close();
   }


   /**
    *
    */
   public void showListUsers(JBossActionRequest actionRequest, JBossActionResponse actionResponse) throws IOException, WindowStateException
   {
       if (actionRequest.isUserInRole("NPM") || actionRequest.isUserInRole("SPA") || actionRequest.getUser().getUserName().equals("root"))
      {
         actionResponse.setRenderParameter("usernamefilter", actionRequest.getParameter("usernamefilter"));
         actionResponse.setRenderParameter("usersperpage", actionRequest.getParameter("usersperpage"));
         actionResponse.setRenderParameter("op", OP_SHOWLISTUSERS);
         actionResponse.setWindowState(WindowState.MAXIMIZED);
      }
   }


   /** Performs a log out. */
   public void userLogout(JBossActionRequest req, JBossActionResponse resp) throws IOException
   {
      String locationURL = req.getParameter("locationURL");
      if (locationURL != null)
      {
         resp.signOut(locationURL);
      }
      else
      {
         resp.signOut();
      }
   }

   /**
    * Action when a user register
    *
    * @param req  JBoss action request
    * @param resp JBoss action response
    */
   public void userRegister(JBossActionRequest req, JBossActionResponse resp)
   {
      // TODO: Check that a bot is not creating many accounts

      if (req.getParameter("Cancel") ==  null)
      {
      int nbErrors = 0;

      String uname = req.getParameter("uname");
      if ((uname == null) || (uname.length() == 0))
      {
         nbErrors++;
         resp.setRenderParameter("uname_error", "REGISTER_ERROR_INVALIDUSERNAME");
      }
      else
      {
         try
         {
            User user = userModule.findUserByUserName(uname);
            if (user != null)
            {
               nbErrors++;
               resp.setRenderParameter("uname_error", "REGISTER_ERROR_EXISTINGUSERNAME");
            }
         }
         catch (IllegalArgumentException e)
         {
            log.error("", e);
         }
         catch (IdentityException e)
         {
            // Ok the user does not exist yet
         }
      }

      String pass1 = req.getParameter("pass1");
      if ((pass1 == null) || (pass1.length() == 0))
      {
         nbErrors++;
         resp.setRenderParameter("pass1_error", "REGISTER_ERROR_INVALIDPASSWORD1");
      }

      String pass2 = req.getParameter("pass2");
      if ((pass2 == null) || (pass2.length() == 0))
      {
         nbErrors++;
         resp.setRenderParameter("pass2_error", "REGISTER_ERROR_INVALIDPASSWORD2");
      }
      else if (!pass1.equals(pass2))
      {
         nbErrors++;
         resp.setRenderParameter("pass2_error", "REGISTER_ERROR_PASSWORDMISMATCH");
      }
      String realEmail = req.getParameter("realemail");
      if (!URLTools.isEmailValid(realEmail))
      {
         nbErrors++;
         resp.setRenderParameter("realemail_error", "REGISTER_ERROR_INVALIDREALEMAIL");
      }

      String fakeEmail = req.getParameter("fakeemail");
      String question = req.getParameter("question");
      String answer = req.getParameter("answer");
      User user;
      if (nbErrors == 0)
      {
         try
         {

             
             /* PR 51046
        	  * changed to create user related to a particular organization only
        	  */
        	 //user = userModule.createUser(uname, pass1);
        	 user = userModule.createUser(uname, pass1 , getMerchantId());
            
            setProperty(user, P3PConstants.INFO_USER_BUSINESS_INFO_ONLINE_EMAIL, realEmail);
            //user.setFakeEmail(fakeEmail);
            setProperty(user,User.INFO_USER_EMAIL_FAKE, fakeEmail);

            //TODO: set registration date

            String type = userProfileModule.getProfileInfo().getPropertyInfo(User.INFO_USER_REGISTRATION_DATE).getType();
            if (type.equals("java.util.Date"))
            {
               putNonEmptyProperty(user, User.INFO_USER_REGISTRATION_DATE, new Date());
            }
            else if (type.equals("java.lang.String"))
            {
               putNonEmptyProperty(user, User.INFO_USER_REGISTRATION_DATE, new Date().toString());
            }
            else
            {
               log.warn(User.INFO_USER_REGISTRATION_DATE + " property is mapped in not supported type: " + type);
            }

            String subscriptionMode = getPortletConfig().getInitParameter(UserPortletConstants.SUBSCRIPTIONMODE);
            if (subscriptionMode == null)
            {
               subscriptionMode = UserPortletConstants.SUBSCRIPTIONMODE_AUTOMATIC;
            }

            setProperty(user, User.INFO_USER_LOCALE, req.getLocale().toString());
            
            
            
            //if (UserPortletConstants.SUBSCRIPTIONMODE_AUTOMATIC.equals(subscriptionMode) || req.isUserInRole("Admin"))
            if (UserPortletConstants.SUBSCRIPTIONMODE_AUTOMATIC.equals(subscriptionMode) || req.isUserInRole("NPM") || req.isUserInRole("SPA") || req.getUser().getUserName().equals("root"))
            {
               setProperty(user, User.INFO_USER_ENABLED, Boolean.TRUE);
            }
            else if (UserPortletConstants.SUBSCRIPTIONMODE_EMAILVERIFICATION.equals(subscriptionMode))
            {
               setProperty(user, User.INFO_USER_ENABLED, Boolean.FALSE);
               String emailText = generateValidationEmail(req, resp, user, pass1);
               String from = getPortletConfig().getInitParameter(UserPortletConstants.EMAILFROM);
               Locale locale = req.getLocale();
               ResourceBundle bundle = getResourceBundle(locale);
               String subject = bundle.getString("REGISTER_CONFIRMATIONEMAIL");
               mailModule.send(from, (String)getProperty(user,P3PConstants.INFO_USER_BUSINESS_INFO_ONLINE_EMAIL), subject, emailText);
            }

            /*
            * Have to define the link to /login Have to redirect after the
            * login try { resp.sendRedirect("/portal/login?username=" + uname +
            * "&password=" + pass1); } catch (IOException e1) { // TODO
            * Auto-generated catch block e1.printStackTrace(); }
            */

            // Add the user to the default user role
            /*String defaultRole = getPortletConfig().getInitParameter(UserPortletConstants.DEFAULT_ROLE);
            if (defaultRole != null)
            {
               Set roleSet = new HashSet();
               Role role = roleModule.findRoleByName(defaultRole);
               if (role != null)
               {
                  roleSet.add(role);
                  membershipModule.assignRoles(user, roleSet);
               }
               else
               {
                  log.error("The role you specified as default role does not exist, check your portlet configuration");
               }
            }
            else
            {
               log.info("You didn't specify a default role in the portlet init configuration, please refer to the documentation");
            }*/
            //resp.setRenderParameter("op", OP_SHOWREGISTER_TY);
            if (req.getParameters().get("lastView",OP_SHOWMENU).equals(OP_SHOWLISTUSERS))
            {
               resp.setWindowState(WindowState.MAXIMIZED);
            }
            else
            {
               resp.setWindowState(WindowState.NORMAL);
            }
         }
         catch (IllegalArgumentException e)
         {
            log.error("Cannot create user " + uname, e);
         }
         catch (IdentityException e)
         {
            log.error("Cannot create user " + uname, e);
         }
         catch (WindowStateException e)
         {
            log.error("Normal window state not supported...");
         }
      }
      else
      {
         portletHelper.setRenderParameter(resp, "USERNAME", uname);
         portletHelper.setRenderParameter(resp, "REALEMAIL", realEmail);
         portletHelper.setRenderParameter(resp, "FAKEEMAIL", fakeEmail);
         portletHelper.setRenderParameter(resp, "QUESTION", question);
         portletHelper.setRenderParameter(resp, "ANSWER", answer);
         portletHelper.setRenderParameter(resp, getOperationName(), OP_SHOWREGISTER);
      }
      }
      else
      {
          resp.setRenderParameter("op", req.getParameters().get("lastView",OP_SHOWMENU));
          try {
			resp.setWindowState(WindowState.NORMAL);
		} catch (WindowStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
   }

   private void fillContextWithUserProfile(User user, DelegateContext ctx)
   {
      ctx.put("GIVENNAME", (String)getProperty(user, P3PConstants.INFO_USER_NAME_GIVEN));
      ctx.put("FAMILYNAME", (String)getProperty(user, P3PConstants.INFO_USER_NAME_FAMILY));
      ctx.put("REALEMAIL", (String)getProperty(user, P3PConstants.INFO_USER_BUSINESS_INFO_ONLINE_EMAIL));
      ctx.put("FAKEEMAIL", (String)getProperty(user, User.INFO_USER_EMAIL_FAKE));
      ctx.put("THEME", (String)getProperty(user, User.INFO_USER_THEME));
      ctx.put("VIEWREALEMAIL", (getProperty(user, User.INFO_USER_VIEW_EMAIL_VIEW_REAL)).equals("true") ? "checked=\"checked\"" : "");
      ctx.put("HOMEPAGE", (String)getProperty(user,User.INFO_USER_HOMEPAGE));
      ctx.put("ICQ", (String)getProperty(user,User.INFO_USER_IM_ICQ));
      ctx.put("AIM", (String)getProperty(user,User.INFO_USER_IM_AIM));
      ctx.put("YIM", (String)getProperty(user,User.INFO_USER_IM_YIM));
      ctx.put("MSNM", (String)getProperty(user,User.INFO_USER_IM_MSNM));
      ctx.put("SKYPE", (String)getProperty(user,User.INFO_USER_IM_SKYPE));
      ctx.put("SIGNATURE", (String)getProperty(user,User.INFO_USER_SIGNATURE));
      ctx.put("LOCATION", (String)getProperty(user,User.INFO_USER_LOCATION));
      ctx.put("OCCUPATION", (String)getProperty(user,User.INFO_USER_OCCUPATION));
      ctx.put("INTERESTS", (String)getProperty(user,User.INFO_USER_INTERESTS));
      ctx.put("EXTRA", (String)getProperty(user,User.INFO_USER_EXTRA));
      ctx.put("QUESTION", (String)getProperty(user,User.INFO_USER_SECURITY_QUESTION));
      ctx.put("ANSWER", (String)getProperty(user,User.INFO_USER_SECURITY_ANSWER));
   }

   /**
    * DOCUMENT_ME
    *
    * @param req  DOCUMENT_ME
    * @param resp DOCUMENT_ME
    */
   public void storeProfile(JBossActionRequest req, JBossActionResponse resp) throws PortletException
   {
      User currentUser = req.getUser();
      try
      {
         currentUser = userModule.findUserById(currentUser.getId());
      }
      catch (IdentityException e)
      {
         log.error("Cannot retrive user", e);
      }

      if (currentUser == null)
      {
         throw new PortletException("No user");
      }

      // Get the user
      User user = null;
      try
      {
         String userid = req.getParameters().getParameter("userid");
         user = userModule.findUserById(userid);
      }
      catch (Exception e)
      {
         throw new PortletException("blah", e);
      }

      // Are we editing ourself ?
      boolean self = currentUser.getId().equals(user.getId());
      if (!self && !(req.isUserInRole("NPM") || req.isUserInRole("SPA") || req.getUser().getUserName().equals("root")))
      {
         throw new PortletException();
      }


      int nbErrors = 0;
      boolean changePassword = false;
      String givenName = req.getParameter("givenname");
      String familyName = req.getParameter("familyname");
      String pass1 = req.getParameter("pass1");
      String pass2 = req.getParameter("pass2");
      if (!(((pass2 == null) || (pass2.length() == 0)) && ((pass1 == null) || (pass1.length() == 0))))
      {
         changePassword = true;
         if ((pass1 == null) || (pass1.length() == 0))
         {
            nbErrors++;
            resp.setRenderParameter("pass1_error", "REGISTER_ERROR_INVALIDPASSWORD1");
         }

         if ((pass2 == null) || (pass2.length() == 0))
         {
            nbErrors++;
            resp.setRenderParameter("pass2_error", "REGISTER_ERROR_INVALIDPASSWORD2");
         }
         else if (!pass1.equals(pass2))
         {
            nbErrors++;
            resp.setRenderParameter("pass2_error", "REGISTER_ERROR_PASSWORDMISMATCH");
         }
      }

      String realEmail = req.getParameter("realemail");
      if (!URLTools.isEmailValid(realEmail))
      {
         nbErrors++;
         resp.setRenderParameter("realemail_error", "REGISTER_ERROR_INVALIDREALEMAIL");
      }

      String fakeEmail = req.getParameter("fakeemail");
      boolean viewRealEmail = req.getParameters().getBoolean("viewrealemail", false);
      String homepage = req.getParameter("homepage");
      Short timezoneoffset = req.getParameters().getShortObject("timezoneoffset", UNDEFINED_TIMEZONE);
      String question = req.getParameter("question");
      String answer = req.getParameter("answer");
      String icq = req.getParameter("icq");
      String msnm = req.getParameter("msnm");
      String yim = req.getParameter("yim");
      String aim = req.getParameter("aim");
      String skype = req.getParameter("skype");
      String location = req.getParameter("location");
      String occupation = req.getParameter("occupation");
      String interests = req.getParameter("interests");
      String signature = req.getParameter("signature");
      String extra = req.getParameter("extra");
      String localeParam = req.getParameter("locale");
      String theme = req.getParameter("theme");

      if (nbErrors == 0)
      {
         //
         if (givenName.trim().length() != 0)
         {
            setProperty(user, P3PConstants.INFO_USER_NAME_GIVEN, givenName);
         }

         if (familyName.trim().length() != 0)
         {
            setProperty(user, P3PConstants.INFO_USER_NAME_FAMILY, familyName);
         }

         if (realEmail.trim().length() != 0)
         {
            setProperty(user, P3PConstants.INFO_USER_BUSINESS_INFO_ONLINE_EMAIL, realEmail);
         }

         if (fakeEmail.trim().length() != 0)
         {
            setProperty(user, User.INFO_USER_EMAIL_FAKE, fakeEmail);
         }

         setProperty(user, User.INFO_USER_VIEW_EMAIL_VIEW_REAL, Boolean.valueOf(viewRealEmail));

         if (changePassword)
         {
            user.updatePassword(pass1);
         }

         if (timezoneoffset.shortValue() != UNDEFINED_TIMEZONE)
         {
            setProperty(user, User.INFO_USER_TIME_ZONE_OFFSET, timezoneoffset.toString());
         }

         try
         {
            Locale locale = LocaleFormat.DEFAULT.getLocale(localeParam);
            //setProperty(user, User.INFO_USER_LOCALE, localeInfo.getLocale());
            setProperty(user, User.INFO_USER_LOCALE, LocaleFormat.DEFAULT.toString(locale));
         }
         catch (ConversionException e)
         {
            log.error("Cannot convert locale format", e);
         }

         // It means we want to erase the current theme choice and use the default provided by the site
         if (theme != null && theme.length() == 0)
         {
            try
            {
               userProfileModule.setProperty(user, User.INFO_USER_THEME, null);
            }
            catch (IdentityException e)
            {
               log.error("Cannot update theme property", e);
            }
         }
         else
         {
            putNonEmptyProperty(user, User.INFO_USER_THEME, theme);
         }

         //
         putNonEmptyProperty(user, User.INFO_USER_HOMEPAGE, homepage);
         putNonEmptyProperty(user, User.INFO_USER_SECURITY_QUESTION, question);
         putNonEmptyProperty(user, User.INFO_USER_SECURITY_ANSWER, answer);
         putNonEmptyProperty(user, User.INFO_USER_IM_ICQ, icq);
         putNonEmptyProperty(user, User.INFO_USER_IM_MSNM, msnm);
         putNonEmptyProperty(user, User.INFO_USER_IM_YIM, yim);
         putNonEmptyProperty(user, User.INFO_USER_IM_AIM, aim);
         putNonEmptyProperty(user, User.INFO_USER_IM_SKYPE, skype);
         putNonEmptyProperty(user, User.INFO_USER_SIGNATURE, signature);
         putNonEmptyProperty(user, User.INFO_USER_LOCATION, location);
         putNonEmptyProperty(user, User.INFO_USER_OCCUPATION, occupation);
         putNonEmptyProperty(user, User.INFO_USER_INTERESTS, interests);
         putNonEmptyProperty(user, User.INFO_USER_EXTRA, extra);

         // Set back to normal window state
         resp.setWindowState(WindowState.NORMAL);
         portletHelper.setRenderParameter(resp, "modifiedProfile", "true");
         portletHelper.setRenderParameter(resp, getOperationName(), OP_SHOWMENU);
      }
      else
      {
         portletHelper.setRenderParameter(resp, "GIVENNAME", givenName);
         portletHelper.setRenderParameter(resp, "FAMILYNAME", familyName);
         portletHelper.setRenderParameter(resp, "REALEMAIL", realEmail);
         portletHelper.setRenderParameter(resp, "FAKEEMAIL", fakeEmail);
         if (getProperty(user, User.INFO_USER_VIEW_EMAIL_VIEW_REAL).toString().equals("true"))
         {
            portletHelper.setRenderParameter(resp, "VIEWREALEMAIL", "checked=\"checked\"");
         }
         else
         {
            portletHelper.setRenderParameter(resp, "VIEWREALEMAIL", "");
         }

         portletHelper.setRenderParameter(resp, "HOMEPAGE", homepage);
         portletHelper.setRenderParameter(resp, "SELECTEDTIMEZONE", "" + timezoneoffset);
         portletHelper.setRenderParameter(resp, "SELECTEDLOCALE", localeParam);
         portletHelper.setRenderParameter(resp, "THEME", theme);
         portletHelper.setRenderParameter(resp, "ICQ", icq);
         portletHelper.setRenderParameter(resp, "AIM", aim);
         portletHelper.setRenderParameter(resp, "YIM", yim);
         portletHelper.setRenderParameter(resp, "MSNM", msnm);
         portletHelper.setRenderParameter(resp, "SKYPE", skype);
         portletHelper.setRenderParameter(resp, "SIGNATURE", signature);
         portletHelper.setRenderParameter(resp, "LOCATION", location);
         portletHelper.setRenderParameter(resp, "OCCUPATION", occupation);
         portletHelper.setRenderParameter(resp, "INTERESTS", interests);
         portletHelper.setRenderParameter(resp, "EXTRA", extra);
         portletHelper.setRenderParameter(resp, getOperationName(), OP_SHOWPROFILE);
      }
      
   }

   /**
    * DOCUMENT_ME
    *
    * @param request  DOCUMENT_ME
    * @param response DOCUMENT_ME
    */
   public void addRolesToUser(JBossActionRequest request, JBossActionResponse response)
   {
      if ( request.isUserInRole("SPA") || request.getUser().getUserName().equals("root"))
      {

         String userId = request.getParameters().getParameter("userid");
         response.setRenderParameter("usernamefilter", request.getParameter("usernamefilter"));
         response.setRenderParameter("offset", request.getParameter("offset"));
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
            try
            {
               String[] selectedRoles = request.getParameterValues("assignedRoles");
               User user = userModule.findUserById(userId);
               Set roles = roleModule.findRolesByNames(selectedRoles);
               membershipModule.assignRoles(user, roles);
               response.setRenderParameter(getOperationName(), OP_SHOWLISTUSERS);
               response.setRenderParameter("usernamefilter", request.getParameter("usernamefilter"));
               response.setRenderParameter("offset", request.getParameter("offset"));
               response.setRenderParameter("usersperpage", request.getParameter("usersperpage"));
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

   /*
   * TODO: The email verification needs an email service. This is not done yet.
   * It also needs a way to generate portlet URL or some kind of URL. Once all
   * of this made, it will be quick to implement.
   */
   /*
   * * Generates the body of the email sent for user activation.
   *
   * @param user user @param password unencrypted password @return email body
   *
   */
   private String generateValidationEmail(JBossActionRequest req,
                                          JBossActionResponse resp,
                                          User user,
                                          String clearPassword)
   {
      // gen link using username, encrypted pw, and a salt.
      String hash = Tools.md5AsHexString(user.getUserName() + getProperty(user, User.INFO_USER_REGISTRATION_DATE).toString() + UserPortletConstants.SALT);
      
      PortalNode node = req.getPortalNode();
      
      //PortalNodeURL link = resp.createActionURL(node);
      PortalNodeURL link = resp.createRenderURL(node);
      link.setParameter("op", OP_ACTIVATEUSER);
      link.setParameter(UserPortletConstants.USERID, "" + user.getId());
      link.setParameter(UserPortletConstants.HASH, hash);
      link.setRelative(false);

      // Fill data to share with the template
      Map modelRoot = new HashMap();
      modelRoot.put("emailDomain", getPortletConfig().getInitParameter("emailDomain"));
      modelRoot.put("id", user.getId());
      modelRoot.put("username", user.getUserName());
      modelRoot.put("password", clearPassword);
      modelRoot.put("activationLink", link);
      
      ClassLoader tcl = Thread.currentThread().getContextClassLoader();
      String message = null;
      try
      {
         Locale locale = req.getLocale();
         URL config = tcl.getResource("templates/user/emailTemplate_" + locale.getLanguage() + "_" + locale.getCountry()+ ".tpl");
         if (config == null)
         {
            config = tcl.getResource("templates/user/emailTemplate_" + locale.getLanguage() + ".tpl");
         }
         if (config == null)
         {
            config = tcl.getResource("templates/user/emailTemplate.tpl");
         }
         if (config == null)
         {
            throw new FileNotFoundException("Cannot load a suitable emailTemplate.tpl in templates/user");
         }
         InputStream in = config.openStream();
         Template tpl = new Template("emailTemplate", new InputStreamReader(in), new Configuration());
         StringWriter out = new StringWriter();
         tpl.process(modelRoot, out);
         out.close();
         message = out.toString();
      }
      catch (IOException e1)
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      catch (TemplateException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return message;
   }

   private String getFirstName(ResourceBundle bundle, User user)
   {
      String givenName = null;

      try
      {
         givenName = (String)userProfileModule.getProperty(user, P3PConstants.INFO_USER_NAME_GIVEN);
      }
      catch (IdentityException e)
      {
         log.error("cannot obtain user profile information: ", e);
      }

      if ((givenName != null) && (givenName.trim().length() != 0))
      {
         return givenName.trim();
      }
      else
      {
         return bundle.getString("NAMENOTAVAILABLE");
      }
   }

   private String getLastName(ResourceBundle bundle, User user)
   {
      String familyName = null;

      try
      {
         familyName = (String)userProfileModule.getProperty(user, P3PConstants.INFO_USER_NAME_FAMILY);
      }
      catch (IdentityException e)
      {
         log.error("cannot obtain user profile information: ", e);
      }


      if ((familyName != null) && (familyName.trim().length() != 0))
      {
         return familyName.trim();
      }
      else
      {
         return bundle.getString("NAMENOTAVAILABLE");
      }
   }

   private String getFullName(ResourceBundle bundle,
                              User user)
   {
      String givenName = null;
      String familyName = null;

      try
      {
         givenName = (String)userProfileModule.getProperty(user, P3PConstants.INFO_USER_NAME_GIVEN);
         familyName = (String)userProfileModule.getProperty(user, P3PConstants.INFO_USER_NAME_FAMILY);
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

   private void putNonEmptyProperty(User user, String key, Object value)
   {
      if (value != null)
      {
         if (value instanceof String && !(((String)value).trim().length() != 0) )
         {
            return;
         }
         //user.getProfile().put(key, value);
         try
         {
            userProfileModule.setProperty(user, key, value);
         }
         catch (IdentityException e)
         {
            //TODO: change to error
            log.info("Cannot set profile property: ", e);
         }
      }
   }

   public void deleteUser(JBossActionRequest request, JBossActionResponse response)
   {
      if ( request.isUserInRole("SPA") || request.getUser().getUserName().equals("root"))
      {
         try
         {
            log.debug("delteteUser() called");
        	 log.debug("request uid " + request.getParameter("userid"));
            User user = userModule.findUserById(request.getParameter("userid"));
            log.debug("usrid : " + user.getId());
            log.debug("username : " + user.getUserName());
            if(!(user.getUserName().equals("root") || user.getId().equals(request.getUser().getId()))){
                userModule.removeUser(user.getId());
            }
          	 log.debug("deleteUser() exiting ");
         }
         catch (IdentityException e)
         {
        	 log.debug("exception caught deleteUser() exiting ");
            e.printStackTrace();
         }
      }
      portletHelper.setRenderParameter(response, getOperationName(), OP_SHOWLISTUSERS);
   }

   private void setProperty(User user, String key, Object value)
   {
      try
      {
         userProfileModule.setProperty(user, key, value);
      }
      catch (IdentityException e)
      {
         //TODO: change to error
         log.info("Cannot set profile property: ", e);
      }
   }

   //Some temp solution to make this portlet not break with new stuff
   private Object getProperty(User user, String key)
   {
      try
      {
         Object o = userProfileModule.getProperty(user, key);
         if (o == null)
         {
            return "";
         }
         else
         {
            return o.toString();
         }
      }
      catch (IdentityException e)
      {
         log.error("Cannot get profile property: ", e);
      }
      return null;
   }

   private class LocaleComparator implements Comparator
   {

      public int compare(Object arg0, Object arg1)
      {
         Locale locale1 = (Locale)arg0;
         Locale locale2 = (Locale)arg1;
         int compare = locale1.getDisplayLanguage().compareTo(locale2.getDisplayLanguage());
         if (compare == 0)
         {
            compare = locale1.getDisplayCountry().compareTo(locale2.getDisplayCountry());
         }
         return compare;
      }
      
   }
   
   private long getMerchantId(){
	   	Set<Principal> s = SecurityAssociation.getSubject().getPrincipals();
		
		for (Principal principal : s) {
			LOG.debug ("sub principal: " + principal.getClass().getName());
			if (principal instanceof GBUserPrincipal) {
				LOG.debug ("p: " + principal);
				
				Organization enterprise = ((GBUserPrincipal) principal).getMerchantAccount();
				long enterpriseId = enterprise.getOrganizationId();
				LOG.debug("enterpriseId of user who has logged in is : = " + enterpriseId);
				return enterpriseId;
			}
	
		}
		return UserPortletConstants.INVALID_MERCHANT_ID;
  }
   
   private Organization getMerchantAccount(){
	   	Set<Principal> s = SecurityAssociation.getSubject().getPrincipals();
		
		for (Principal principal : s) {
			LOG.debug ("sub principal: " + principal.getClass().getName());
			if (principal instanceof GBUserPrincipal) {
				LOG.debug ("p: " + principal);
				
				Organization enterprise = ((GBUserPrincipal) principal).getMerchantAccount();
				long enterpriseId = enterprise.getOrganizationId();
				LOG.debug("enterpriseId of user who has logged in is : = " + enterpriseId);
				return enterprise;
			}
	
		}
		return null;
  }
   
   /*
   * private String getTimezoneOffsetString(short timezoneOffset) {
   * StringBuffer timeZone = new StringBuffer(); for (int i = 0; i <
   * UserPortletConstants.TIME_ZONE_OFFSETS.length; i++) { if
   * (UserPortletConstants.TIME_ZONE_OFFSETS[i] != null) { timeZone.append("
   * <option value=\"").append(i); if (timezoneOffset == i) {
   * timeZone.append("\" selected>"); } else { timeZone.append("\">"); }
   * timeZone.append(UserPortletConstants.TIME_ZONE_OFFSETS[i]).append("
   * </option>"); } } return timeZone.toString(); }
   *
   * private String getTimezoneOffsetString() { return
   * getTimezoneOffsetString((short)0); }
   *
   * private String getTimezoneOffsetString(String timezoneOffset) { short
   * offset = Short.parseShort(timezoneOffset); return
   * getTimezoneOffsetString(offset); }
   */
}
