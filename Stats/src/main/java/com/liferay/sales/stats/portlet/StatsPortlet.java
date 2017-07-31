package com.liferay.sales.stats.portlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalServiceUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalServiceUtil;


@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Stats Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class StatsPortlet extends MVCPortlet {
	
	// Method for rendering this portlet which displays an overall summary of all the requests
	@Override
	public void doView(
		RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		// Get all the Kaleo Instances which indicate if a request is complete or not
		List<KaleoInstance> ki = KaleoInstanceLocalServiceUtil.getKaleoInstances(0, KaleoInstanceLocalServiceUtil.getKaleoInstancesCount());
		
		int outstanding = 0; // Variable to count non-complete requests
		int closed = 0; // Variable to count complete requests
		
		// Loop through all the Kaleo Instances to find all relevant Kaleo Instances
		for(int i = 0; i < ki.size(); i++)
		{
			// A Kaleo Instance is relevant if its Definition Name is "Manual Workflow"
			if(ki.get(i).getKaleoDefinitionName().equals("Manual Workflow"))
			{
				// Check if the request is complete
				if(ki.get(i).isCompleted())
				{
					closed++; // Increment the complete variable if it's complete
				}
				else
				{
					outstanding++; // Increment the non-complete variable if it's not complete
				}
			}
			
		}
		
		// Get the total number of requests submitted by adding complete and non-complete together
		int total = closed + outstanding;
		
		// Getting the average assign time
		// Kaleo Task Instance Tokens contain information on what task the request is on and has completed
		// Get all Kaleo Task Instance Tokens to find relevant ones
		List<KaleoTaskInstanceToken> allAssigns = KaleoTaskInstanceTokenLocalServiceUtil.getKaleoTaskInstanceTokens(0, KaleoTaskInstanceTokenLocalServiceUtil.getKaleoTaskInstanceTokensCount());
		ArrayList<KaleoTaskInstanceToken> starts = new ArrayList<KaleoTaskInstanceToken>(); // Tasks found which are relevant and are the starting task
		ArrayList<KaleoTaskInstanceToken> assigned = new ArrayList<KaleoTaskInstanceToken>(); // Tasks there are found which include the task where the request is assigned to a TR
		
		long roleId = 31197; // Role ID of Technical Resources
		List<User> users = UserLocalServiceUtil.getRoleUsers(roleId); // Get all users that have this role ID (TR's)
		
		// Loop through the list of all the tasks (Kaleo Task Instance Tokens) to find specific ones
		for(int i = 0; i < allAssigns.size(); i++)
		{
			// Check if this task is the starting task of the workflow
			if(allAssigns.get(i).getKaleoTaskName().equals("Admin"))
			{
				starts.add(allAssigns.get(i)); // Add to the start task list
			}
			else
			{
				// Loop through a list of TR's 
				for(int j = 0; j < users.size(); j++)
				{
					// Check if the task name matches a TR name which are the names of these tasks
					if(allAssigns.get(i).getKaleoTaskName().equals(users.get(j).getFirstName()))
					{
						assigned.add(allAssigns.get(i)); // Add to the list of assigned TR tasks
						break; // Break the loop
					}
				}
			}
			
		}
		// Calculate last month
		Date today = new Date(); // Date for today 
		long n = 31; // Number of days
		Date lastMonth = new Date(today.getTime() - n * 24 * 3600 * 1000 );		
		
		// Calculate the average time
		long time = 0;  // Time variable which will be used in miliseconds
		int timeCount = 0; // Counter of number of times
		int thisMonthTimeCount = 0; // Counter of number of times this month
		long thisMonthTime = 0; // Time variable which will be used in miliseconds for this month
		
		// Loop through the list of all starting tasks and TR tasks to match them 
		// Loop through starting tasks
		for(int i = 0; i < starts.size(); i++)
		{
			// Loop through TR tasks
			for(int j = 0; j < assigned.size(); j++)
			{
				// Check if the start task has the same Instance ID as the TR task
				if(starts.get(i).getKaleoInstanceId() == assigned.get(j).getKaleoInstanceId())
				{
					// Update total time variable (milliseconds)
					time += (assigned.get(j).getCreateDate().getTime() - starts.get(i).getCreateDate().getTime()); // Convert date difference to milliseconds
					timeCount++; // Increment the number of times counted
					
					// Check if it was created this month
					if(assigned.get(j).getCreateDate().after(lastMonth))
					{
						// Update this months total time variable (milliseconds)
						thisMonthTime += (assigned.get(j).getCreateDate().getTime() - starts.get(i).getCreateDate().getTime()); // Convert date difference to milliseconds
						thisMonthTimeCount++; // Increment the number of times counted this month
					}
				}
				
			}
			
		}
		
		// All time
		// Get the average time
		time = time / timeCount;
		// Turn the average milliseconds into days, hours and minutes
		long days = TimeUnit.MILLISECONDS.toDays(time);
		time -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.HOURS.toMillis(hours);
		long mins = TimeUnit.MILLISECONDS.toMinutes(time);
		time -= TimeUnit.MINUTES.toMillis(mins);
		long secs = TimeUnit.MILLISECONDS.toSeconds(time);
		String averageTime = "Days: " + days + ", hours: " + hours + ", minutes: " + mins;

		//This Month
		// Get the average time this month
		String thisMonthAverageTime = "Days: 0, hours: 0, minutes: 0";
		if(thisMonthTimeCount != 0)
		{
			thisMonthTime = thisMonthTime / thisMonthTimeCount;
			// Turn the average milliseconds into days, hours and minutes
			long thisMonthDays = TimeUnit.MILLISECONDS.toDays(thisMonthTime);
			thisMonthTime -= TimeUnit.DAYS.toMillis(thisMonthDays);
			long thisMonthHours = TimeUnit.MILLISECONDS.toHours(thisMonthTime);
			thisMonthTime -= TimeUnit.HOURS.toMillis(thisMonthHours);
			long thisMonthMins = TimeUnit.MILLISECONDS.toMinutes(thisMonthTime);
			thisMonthTime -= TimeUnit.MINUTES.toMillis(thisMonthMins);
			long thisMonthSecs = TimeUnit.MILLISECONDS.toSeconds(thisMonthTime);
			thisMonthAverageTime = "Days: " + thisMonthDays+ ", hours: " + thisMonthHours + ", minutes: " + thisMonthMins ;
		}
		// Set all relevant attributes to be used on the JSP
		renderRequest.setAttribute("outstanding", outstanding);
		renderRequest.setAttribute("closed", closed);
		renderRequest.setAttribute("total", total);
		renderRequest.setAttribute("averageTime", averageTime);
		renderRequest.setAttribute("thisMonthAverageTime", thisMonthAverageTime);


		super.doView(renderRequest, renderResponse);
	}
}