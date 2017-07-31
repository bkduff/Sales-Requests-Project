<%@ include file="/init.jsp" %>



</div>
<h1>Account Managers</h1>
<%
	/* Get the List attribute (ams) from the render command */
	List<User> users = (List<User>) request.getAttribute("ams");
%>
	<h3>Please select the Technical Resource you would like to view</h3>
     <table class="table" border="1px">
		<%/* Cycle through the list of Account Managers */ %>
		<liferay-ui:search-container emptyResultsMessage="No AM's">
			<liferay-ui:search-container-results results="<%=users %>" />
				<liferay-ui:search-container-row className="com.liferay.portal.kernel.model.User" modelVar="u">
					<%/* Set paramaters that will be used in the next page */ %>
					<portlet:renderURL var="viewAM">
						<portlet:param name="mvcRenderCommandName" value="/amhomes/viewAM" />
						<portlet:param name="redirect" value="${currentURL}" />
						<portlet:param name="AM" value="${u.getUserId()}" />
					</portlet:renderURL>
					<tr>
						<td>
							<%/* Provide a link to be clicked to bring the user to this AM's page */ %>
							<a href="${viewAM}"><p class="truncate-text flex-item-center">${u.getFullName()}</p></a>
    					</td>
    				</tr>
    			</liferay-ui:search-container-row>
		</liferay-ui:search-container>
	</table>