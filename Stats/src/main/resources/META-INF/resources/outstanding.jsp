<%@ include file="/init.jsp" %>
<%
	ArrayList<String> companyNames = (ArrayList<String>)request.getAttribute("companyNames"); /* List of the company names */
	ArrayList<String> demoDates = (ArrayList<String>)request.getAttribute("demoDates"); /* List of the pre-selected requested dates */
	ArrayList<String> ratingCreators = (ArrayList<String>)request.getAttribute("ratingCreators"); /* List of the Account Managers who created the requests */

	
%>
<%/* Displaying the outstanding requests */%>
<h3>Outstanding page</h3>
	<table class="table">
		<tr>
			<th><u>Company:</u></th>
			<th><u>Planned Demo Date</u></th>
			<th><u>Account Manager</u></th>
		</tr>
<% for(int i = 0; i < companyNames.size(); i++) { %>
		<tr>
			<td><%=companyNames.get(i) %></td>
			<td><%=demoDates.get(i) %></td>
			<td><%=ratingCreators.get(i) %></td>
		</tr>
<%}%>
	</table>
	
