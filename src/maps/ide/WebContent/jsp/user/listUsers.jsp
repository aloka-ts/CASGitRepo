<%@ page language="java" extends="org.jboss.portal.core.servlet.jsp.PortalJsp" %>
<%@ taglib uri="/WEB-INF/portal-lib.tld" prefix="n" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page isELIgnored="false" %>
<portlet:defineObjects/>

<p>${n:i18n("LIST_SEARCHRESULTS")} "${n:out("usernamefilter")}" (${n:out("results")} ${n:i18n("LIST_MATCHING")})</p>

<hr/>

<p></p>
<table width="100%" border="0" cellspacing="0" class="portlet-table-body">
   <tr>
      <td nowrap="nowrap" class="portlet-table-header">${n:i18n("LIST_USERNAME")}</td>
      <td nowrap="nowrap" class="portlet-table-header">${n:i18n("LIST_FIRSTNAME")}</td>
      <td nowrap="nowrap" class="portlet-table-header">${n:i18n("LIST_LASTNAME")}</td>
      <td nowrap="nowrap" class="portlet-table-header">${n:i18n("LIST_ROLES")}</td>
  	  <n:if ctx="row">
		  <n:if ctx="editMode">
			<td nowrap="nowrap" class="portlet-table-header">${n:i18n("LIST_ACTIONS")}</td>
		  </n:if>
	  </n:if>
   </tr>
   <n:iterate ctx="row">
      <tr>
         <td nowrap="nowrap" class="${n:out("row.cssClass")}">${n:out("row.username")}</td>
		

         <td nowrap="nowrap" class="${n:out("row.cssClass")}">${n:out("row.firstname")}</td>
         <td nowrap="nowrap" class="${n:out("row.cssClass")}">${n:out("row.lastname")}</td>
         <td nowrap="nowrap" class="${n:out("row.cssClass")}">
            <n:iterate ctx="roles">${n:out("row.roles.name")}<br/></n:iterate>
         </td>
		 <n:if ctx="editMode">
			 <td nowrap="nowrap" class="${n:out("row.cssClass")}"><a
				href="${n:out("row.editMode.editURL")}">${n:i18n("LIST_ACTIONSSHOWPROFILE")}</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a
				href="${n:out("row.editMode.rolesURL")}">${n:i18n("LIST_ACTIONADDROLESTOUSER")}</a>&nbsp;&nbsp;
				<n:if ctx="deleteUser">|&nbsp;&nbsp;
					<a href="${n:out("row.editMode.deleteUser.deleteURL")}"
					onClick="javascript:return confirm('${n:i18n("LIST_CONFIRMDELETEUSER")}')"
					>${n:i18n("LIST_ACTIONDELETEUSER")}</a>
			   </n:if>
		    </td>
    	</n:if>
	  </tr>
   </n:iterate>
</table>



<div align="center">
   <table width="0%" border="0" cellspacing="0" cellpadding="0" class="pagination">
      <tr>
         <td nowrap>
            <ul>
               <n:if ctx="previouspage">
                  <li class="previouspage">
                     <a href="${n:out("previouspage.link")}">${n:i18n("PREVIOUSPAGE")}</a>
                  </li>
               </n:if>
               <n:if ctx="nextpage">
                  <li class="nextpage">
                     <a href="${n:out("nextpage.link")}">${n:i18n("NEXTPAGE")}</a>
                  </li>
               </n:if>
            </ul>
         </td>
      </tr>
   </table>
</div>

<br/>
<form
   id="<portlet:namespace/>searchUsers"
   action="<portlet:actionURL><portlet:param name="op" value="showListUsers"/></portlet:actionURL>"
   method="post">
   <font class="portlet-form-label">${n:i18n("REGISTER_ADMIN_SEARCH")}</font><br/>
   <input type="text" name="usernamefilter" value="${n:out("usernamefilter")}" size="15"/>
   <select name="usersperpage">
      <option value="10">10</option>
      <option value="20">20</option>
      <option value="30">30</option>
      <option value="50">50</option>
      <option value="75">75</option>
      <option value="100">100</option>
   </select>
   <br/>
</form>
<input name="Save" class="portlet-form-button" id="Save" type="submit"
          onclick="document.forms['<portlet:namespace/>searchUsers'].submit();"
          value="${n:i18n("Search")}"/>
   &nbsp;
   <input name="Cancel" class="portlet-form-button" id="Cancel" type="submit"
          onclick="document.forms['<portlet:namespace/>cancel'].submit();"
          value="${n:i18n("Cancel")}"/>
<form
   name="<portlet:namespace/>cancel"
   action="<portlet:renderURL windowState="normal"><portlet:param name="op" value="showMenu"/></portlet:renderURL>"
   method="post"/>


<br/>
  <n:if ctx="row">
    <n:if ctx="editMode">
		<a id="register"
		   href="<portlet:renderURL windowState="maximized"><portlet:param name="op" value="showRegister"/></portlet:renderURL>"><img
		   src="<%= renderRequest.getContextPath() %>/images/user/ico_adduser.gif" border="0"
		   align="absmiddle"/></a>&nbsp;<a
		id="register"
		href="
		<portlet:renderURL windowState="maximized">
		   <portlet:param name="op" value="showRegister"/>
		   <portlet:param name="lastView" value="showListUsers"/>
		</portlet:renderURL>
		">${n:i18n("REGISTER_REGISTER_ADMIN_LINK")}</a>
	</n:if>
</n:if>