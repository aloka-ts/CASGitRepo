<%@ page language="java" extends="org.jboss.portal.core.servlet.jsp.PortalJsp" %>
<%@ taglib uri="/WEB-INF/portal-lib.tld" prefix="n" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page isELIgnored="false" %>
<portlet:defineObjects/>
<div class="portlet-section-body">
   <p>
      ${n:i18n("ROLE_TEXT_1")}&nbsp;${n:i18n("ROLE_TEXT_2")}
   <hr/>
   </p>
   <table width="100%" border="0" cellspacing="0" class="portlet-table-body">
      <tr>
         <td class="portlet-table-text" colspan="3"><strong>${n:out("nbRoles")}</strong></td>
      </tr>
      <n:iterate ctx="role">
         <tr>
            <td width="100%" class="portlet-table-text"><a href="<portlet:renderURL windowState="maximized">
            <portlet:param name="op" value="showEditRole" />
            <portlet:param name="roleid" value="${n:out('role.id')}"/>
            <portlet:param name="roledisplayname" value="${n:out('role.displayname')}"/>
            </portlet:renderURL>">${n:out("role.displayname")}</a></td>
            <td nowrap class="portlet-table-text">
		<n:if ctx="showMembers">
			<a href="<portlet:actionURL>
            <portlet:param name="op" value="showListEditRoleMembers"/>
            <portlet:param name="roleid" value="${n:out('role.id')}"/>
            <portlet:param name="usersperpage" value="10"/>
            <portlet:param name="usernamefilter" value=""/>
            
			</portlet:actionURL>">Members</a></td>
		</n:if>

            <td nowrap class="portlet-table-text"><a href="<portlet:actionURL>
            <portlet:param name="op" value="removeRole"/>
            <portlet:param name="roleid" value="${n:out('role.id')}"/>
            </portlet:actionURL>"
            onClick="javascript:return confirm('${n:i18n("ROLE_CONFIRM_DELETE")}')">Delete</a></td>
         </tr>
      </n:iterate>
   </table>
   <p>

   <form class="addicon">
      <input type="button"
             class="portlet-form-button"
             value="${n:i18n("MENU_CREATEROLE")}"
             onClick="hideShow('showall');">
   </form>

   <!--a onclick="hideShow('showall');" href="#" class="addicon">${n:i18n("MENU_CREATEROLE")}</a--></p>
   <n:error key="rolename_error"/>
   <br/>
   <n:error key="roledisplayname_error"/>
</div>
<div id="showall" class="hidden">
   <div>
      <hr/>

      <h6>${n:i18n("ROLE_CREATE")}</h6>

      <form name="createRole"
            action="<portlet:actionURL><portlet:param name="op" value="createRole"/></portlet:actionURL>" method="post">
         <p>
            <label class="portlet-form-label">${n:i18n("ROLE_NAME")}</label>
            <br/>
            <input name="rolename" type="text"/>
         </p>

         <p>
            <label class="portlet-form-label">${n:i18n("ROLE_DISPLAYNAME")}</label>

            <br/>
            <input name="roledisplayname" type="text"/></p>

         <p><input name="Save" type="submit" class="portlet-form-button" id="Save"
                   value="${n:i18n("SaveChanges")}"/>
         </p>
      </form>
   </div>
</div>
