<%@ include file="/init.jsp" %>

<%/* Render Parameter to get to the next JSP  */ %>
<portlet:renderURL var="viewTRURL">
	<portlet:param name="mvcRenderCommandName"
		value="/homes/viewTR" />
	<portlet:param name="redirect" value="${currentURL}" />
</portlet:renderURL>


</div>
<h1>Technical Resources</h1>
<%
	/* List of users that are Technical resources using an attribute from the render command */
	List<User> users = (List<User>) request.getAttribute("TRs");
	/* Display the list of TR's */
%>
	<h3>Please select the Technical Resource you would like to view</h3>
     <table class="table" border="1px">
		<liferay-ui:search-container emptyResultsMessage="No TR's">
			<liferay-ui:search-container-results results="<%=users %>" />
				<liferay-ui:search-container-row className="com.liferay.portal.kernel.model.User" modelVar="u">
					<portlet:renderURL var="viewTR">
						<portlet:param name="mvcRenderCommandName" value="/homes/viewTR" />
						<portlet:param name="redirect" value="${currentURL}" />
						<portlet:param name="TR" value="${u.getUserId()}" />
					</portlet:renderURL>
					<tr>
						<td>
						<%/* Provide a link for the user to select a TR */%>
							<a href="${viewTR}"><p class="truncate-text flex-item-center">${u.getFullName()}</p></a> 
    					</td>
    				</tr>
    			</liferay-ui:search-container-row>
		</liferay-ui:search-container>
	</table>