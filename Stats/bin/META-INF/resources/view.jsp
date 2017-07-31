<%@ include file="/init.jsp" %>
<%/* Value of a Render Command */%>
<portlet:renderURL var="viewOutstandingURL">
	<portlet:param name="mvcRenderCommandName"
		value="/stats/outstanding" />
	<portlet:param name="redirect" value="${currentURL}" />
</portlet:renderURL>
<%
	int planned = (int)request.getAttribute("outstanding"); /* Outstanding requests */
	int completed = (int)request.getAttribute("closed");/* Complete Requests */
	int total = (int)request.getAttribute("total"); /* Total Requests */
	String averageTimeDisplay = (String)request.getAttribute("averageTime"); /* Overall average assign time */
	String thisMonthAverageTime = (String)request.getAttribute("thisMonthAverageTime"); /* This months average assign time */

%>
<div>
<%/* Displaying the summary of all the requests */%>
<h2>Overall Summary (<%=total %> requests made)</h2>
<table class="table" style="font-size:40px">
	<tr>
		<th style="font-size:40px">Outstanding</th>
		
		<th style="font-size:40px">Closed</th>
	</tr>
	<tr>
		
			<td>
				<a href="${viewOutstandingURL}"><p><%=planned %></p></a>
			</td>
		
		<td><%=completed %></td>
	</tr>
	
</table>
</div>
<br>
<%/* Display a summary of the average assign times */%>
<h2>Average Assign Times</h2>
<table class="table" style="font-size:20px">
		<tr>
			<th>
				Overall Average Time
			</th>
			<th>
				This Month Average Time
			</th>
		</tr>
		<tr>
			<td>
				<%=averageTimeDisplay %>
			</td>
			<td>
				<%=thisMonthAverageTime %>
			</td>
		</tr>
</table>
