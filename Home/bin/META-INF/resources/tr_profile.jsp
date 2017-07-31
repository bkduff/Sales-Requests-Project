<%@ include file="/init.jsp" %>
<%

	User tr = (User)request.getAttribute("tr"); /* Get the specific Technical Resource from the Render Command */

	ArrayList<String> creators = (ArrayList<String>)request.getAttribute("creators"); /* Get the list of creators who provided ratings */
	ArrayList<String> ratings = (ArrayList<String>)request.getAttribute("ratingsArray"); /* Get the list of ratings for this TR */
	double a = (double)request.getAttribute("average"); /* Get the average rating for this TR */
	double monthA = (double)request.getAttribute("monthAverage"); /* Get this months average rating */

	int outstanding = (int)request.getAttribute("outstanding"); /* Get the number of outstanding requests for this TR */
	int closed = (int)request.getAttribute("closed"); /* Get the number of closed requests for this TR */
	int thisMonth = (int)request.getAttribute("thisMonth"); /* Get the number of requests assigned to this TR this month */
	DecimalFormat df = new DecimalFormat("#.##"); /* Get the format to round numbers to 2 decimal places */

%>

<%/* Display a summary of this TR's requests */%>
<h1>Feedback for <%=tr.getFullName() %></h1>
<h3>Overview of Demos assigned to <%=tr.getFirstName() %></h3>
<table border="1" class="table">
	<tr>
		<th>Outstanding</th>
		<th>Closed</th>
		<th>Assigned this month</th>
	</tr>
	<tr>
		<td><%=outstanding %></td>
		<td><%=closed %></td>
		<td><%=thisMonth %></td>
	</tr>
</table>

 
 
<div border="1" >
<%/* Display a summary of this TR's ratings */%>
<h3>Average ratings for <%=tr.getFirstName() %></h3>
<table border="1" class="table">
	<tr>
		<th>Overall Ratings</th>
		<th>Ratings this month</th>
	</tr>
	<tr>
		<td><%=df.format(a) %></td>
		<td><%=df.format(monthA) %></td>
	</tr>
</table>

<%/* List all the ratings for this TR */%>
<h3>List of ratings for <%=tr.getFirstName() %></h3>
<table border="1" class="table">
	<tr>
		<th>Account Manager</th>
		<th>Rating</th>
	</tr>
<% 
for(int i = 0; i < ratings.size(); i++)
{
	
%> 
	<tr>
		<td>
			<b><%=creators.get(i) %></b>
		</td>
		<td>
			<%=ratings.get(i) %>
		</td>
	</tr>

<%
}

%>
</table>
</div>
<%/* JavaScript for a simple back button */%>
<a href="javascript: history.go(-1);">&laquo; <liferay-ui:message key="back" /></a>