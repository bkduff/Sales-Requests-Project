<%@ include file="/init.jsp" %>

<table class="table">

<%
	/* Get the list of Account Managers from the Attribute "am" */
	List<User> am = (List<User>)request.getAttribute("am");
	
	/* Loop to loop through the Account Managers list to print out Account Managers names */
	for(int i = 0; i < am.size(); i++)
	{
%>
	<tr>
		<td id="p<%=i %>"><%=am.get(i).getFullName() %></td>
		<td><button onclick="copyName('#p<%=i %>')">Copy</button></td>
	</tr>
<% }%>

</table>
<script>
<%/* Javascript to copy the Account Managers names */ %>
function copyName(element) {
	  var $temp = $("<input>");
	  $("body").append($temp);
	  $temp.val($(element).text()).select();
	  document.execCommand("copy");
	  $temp.remove();
	}

</script>

<%/* CSS for the page */ %>
<style type="text/css">
td
{
    padding:5px 15px 5px 15px;
}
button {
    background-color: #ADD8E6; 
    border: 2px solid;
    border-radius: 15px;
    color: white;
    padding: 4px 4px;
    text-align: center;
   
    display: inline-block;
    font-size: 16px;
}
</style>