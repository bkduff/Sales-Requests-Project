<%@ include file="/init.jsp" %>
<%
	/* Get all the attributes needed from the render command */
	User am = (User)request.getAttribute("am"); /* Get all the attributes needed from the render command */
	int outstanding = (int)request.getAttribute("upc"); /* Get the number of outstanding requests for this AM */
	int closed = (int)request.getAttribute("com"); /* Get the number of completed requests for this AM */
	int thisMonth = (int)request.getAttribute("thisMonth"); /* Get the number of requests submitted this month for this AM */

	ArrayList<String> creators = (ArrayList<String>)request.getAttribute("creators"); /* Get the list of creators that rated this AM */
	ArrayList<String> ratings = (ArrayList<String>)request.getAttribute("ratingsArray"); /* Get the ratings for this AM */
	double a = (double)request.getAttribute("average"); /* Get the average rating for this AM */
	double monthA = (double)request.getAttribute("monthAverage"); /* Get this months average rating for this AM */

	
	DecimalFormat df = new DecimalFormat("#.##"); /* Round the ratings to 2 decimal places */
%>


<% /* Table to display a summary of the number of request made and completed by/for this AM */ %>
<h1>Feedback for <%=am.getFullName() %></h1>
<h3>Overview of Demos requested by <%=am.getFirstName() %></h3>
<table border="1" class="table">
	<tr>
		<th>Requested</th>
		<th>Closed</th>
		<th>Requested this month</th>
	</tr>
	<tr>
		<td><%=outstanding %></td>
		<td><%=closed %></td>
		<td><%=thisMonth %></td>
	</tr>
</table>

<div border="1" >
<% /* Table to display a summary of the ratings for this AM */ %>
<h3>Average ratings for <%=am.getFirstName() %></h3>
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

<% /* Table to display all the ratings for this AM */ %>
<h3>List of ratings for <%=am.getFirstName() %></h3>
<table border="1" class="table">
	<tr>
		<th>Technical Resource</th>
		<th>Rating</th>
	</tr>
<% 
/* Loop through the list of ratings */
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

<% /* JavaScript to return to previous page for ease */ %>
<a href="javascript: history.go(-1);">&laquo; <liferay-ui:message key="back" /></a>