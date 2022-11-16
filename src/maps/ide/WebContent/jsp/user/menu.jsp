<%@ page language="java" extends="org.jboss.portal.core.servlet.jsp.PortalJsp" %>
<%@ taglib uri="/WEB-INF/portal-lib.tld" prefix="n" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page isELIgnored="false" %>
<portlet:defineObjects/>

<table width="100%" cellpadding="2" border="0">

   <n:if ctx="modifiedProfile">
     <p class="portlet-form-label">${n:i18n("MODIFIED_PROFILE")}</p>
   </n:if>

   <n:if ctx="admin">
      <tr>
         <td class="portlet-section-body">
            <form
               action="<portlet:actionURL><portlet:param name="op" value="showListUsers"/></portlet:actionURL>"
               method="post">
               <input type="hidden" name="usersperpage" value="10"/>
               <font class="portlet-form-label">${n:i18n("REGISTER_ADMIN_SEARCH")}</font><br/>
               <input type="text" name="usernamefilter" value="${n:out("usernamefilter")}" size="15"/>
               <input name="Find" type="submit" class="portlet-form-button" id="Find" value="${n:i18n("Search")}"/>
            </form>
            <br/>
         </td>
      </tr>
   </n:if>

	   <n:if ctx="createMode">
	 
		  <tr>
			 <td class="portlet-section-body">
				<a id="register"
				   href="<portlet:renderURL windowState="maximized"><portlet:param name="op" value="showRegister"/></portlet:renderURL>"><img
				   src="<%= renderRequest.getContextPath() %>/images/user/ico_adduser.gif" border="0"
				   align="absmiddle"/></a>&nbsp;<a
				id="register"
				href="<portlet:renderURL windowState="maximized"><portlet:param name="op" value="showRegister"/><portlet:param name="lastView" value="showMenu"/></portlet:renderURL>">${n:i18n("REGISTER_REGISTER_ADMIN_LINK")}</a>
			 </td>
		  </tr>
	   </n:if>

   <tr>
      <td class="portlet-section-body">
         <a id="editprofile"
            href="<portlet:renderURL windowState="maximized"><portlet:param name="op" value="showProfile"/></portlet:renderURL>"><img
            src="<%= renderRequest.getContextPath() %>/images/user/edit_profile.gif" border="0"
            align="absmiddle"/></a>&nbsp;<a id="editprofile"
                                            href="<portlet:renderURL windowState="maximized"><portlet:param name="op" value="showProfile"/></portlet:renderURL>">${n:i18n("MENU_EDITPROFILE")}</a>
      </td>
   </tr>

<%--
   // Removed for 2.6. Its throwing and no point in having logout in two places.
   <tr>
      <td class="portlet-section-body">
         <a id="logout"
            href="<portlet:actionURL windowState="normal"><portlet:param name="op" value="userLogout"/></portlet:actionURL>"><img
            src="<%= renderRequest.getContextPath() %>/images/user/logout.gif" border="0"
            align="absmiddle"/></a>&nbsp;<a id="logout"
                                            href="<portlet:actionURL windowState="normal"><portlet:param name="op" value="userLogout"/></portlet:actionURL>">${n:i18n("MENU_LOGOUT")}</a>
      </td>
   </tr>
--%>

</table>
