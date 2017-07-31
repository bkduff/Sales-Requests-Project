package com.liferay.sales.portlet.command;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentInstanceLocalServiceUtil;
import com.liferay.sales.portlet.HomePortletKeys;

@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + HomePortletKeys.PORTLET_NAME,
			"mvc.command.name=/homes/viewTR"
	}, service = MVCRenderCommand.class)

public class ViewTRRenderCommand implements MVCRenderCommand{
	
	// Method for rendering the summary page of a Technical Resource
	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse)
			throws PortletException {

		// Variables to be used 
		long trId = ParamUtil.getLong(renderRequest, "TR"); // Get the TR ID from the previous JSP that is the specified Technical Resource
		String username = ""; // String to hold the TR username
		String firstname = ""; // String to hold the TR first name
		User tr = null; // Variable to hold all the TR
		Date today = new Date(); // Todays date that will be used to get last month
		long n = 31; // 31 days to get last months date
		Date lastMonth = lastMonth = new Date(today.getTime() - n * 24 * 3600 * 1000 );  //Date variable to hold last month
		List<DDMContent> contentList = null; // List of DDMContent which contains necessary of feedback forms
		ArrayList<String> ratingsArray = null; // List to hold all the ratings for this Technical Resource
		ArrayList<String> creators = null; // List to hold all the creators of the ratings
		ArrayList<String> monthAverage = null; // List to hold ratings only created this month
		String creator = ""; // String to temporarily hold the creator name
		JSONObject jo = null; // JSON Object to hold information from the results
		String fieldVals = ""; // String to hold the field values of the JSONObject "jo"
		JSONArray ja = null; // JSON Array to hold the field values, turns the string "fieldVals" into a JSON Array
		JSONObject cname = null; // First Object of the JSON Array "ja" which contains the field values
		JSONObject trNameJSON = null; // Object from the array which contains the name of the Technical Resource that was reviewed
		String trName = ""; // String to hold Technical Resources name
		JSONObject r = null; // JSON Object that is taking from "ja" that contains the rating information
		String rstring = ""; // Gets value from "r" in String format
		JSONObject ratingObject = null; // JSON Object created from the String "rstring" and it contains the rating
		String rating = ""; // String to hold the rating value
		List<KaleoTaskAssignmentInstance> kals = null; // List to hold Kaleo Task Assignment Instances to get finished and unfinished 
		ArrayList<KaleoTaskAssignmentInstance> nameMatch = null; // List of Kaleo Task Assignment Instances that match this TR's task
		ArrayList<KaleoTaskAssignmentInstance> ending = null; // List of Kaleo Task Assignment Instances that are Feedback Forms
		int thisMonth = 0; // Count of requests received this month
		int uc = 0; // Count of upcoming/uncomplete requests for this TR
		int compl = 0; // Count of requests completed by this TR
		
		
		try {
			tr = UserLocalServiceUtil.getUser(trId); // Get the specified user (TR)
			renderRequest.setAttribute("tr", tr); // Set the attribute "tr" to use in the JSP 
			username = tr.getFullName(); // Get the TR's full name to match with Form Feedback
			firstname = tr.getFirstName(); // Get the first name of the TR to match with Manual Workflow tasks
			
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			
				
			contentList = DDMContentLocalServiceUtil.getContents(); // Get all the DDMContents which contain relevant form data
			
			ratingsArray = new ArrayList<String>(); // Ratings array list
			creators = new ArrayList<String>(); // Creators array list
			monthAverage = new ArrayList<String>(); // This months ratings array list
			
			// Loop through the list of DDMContents to get relevant forms
			for(int i = 0; i < contentList.size(); i++)
			{

				jo = JSONFactoryUtil.createJSONObject(contentList.get(i).getData()); // Create JSON Object of this DDMContents metadata
	
				fieldVals = jo.get("fieldValues").toString(); // Get field values from the JSON Object (jo) in String form
				ja = JSONFactoryUtil.createJSONArray(fieldVals); // Create a JSON Array from the String of field values 
				
				cname = ja.getJSONObject(0); // Get the first index value to see if the DDMContent is relevant
				
				// Check if the data is relevant by checking the "name" value, if its relevant it should equal "PleaseEnterTheNameOfTheTR"
				if(cname.getString("name").equals("PleaseEnterTheNameOfTheTR")) // This is the first question on the feedback form
				{
					trNameJSON = cname.getJSONObject("value"); // Get a JSON Object which contains the information about the Technical Resources name
					trName = trNameJSON.getString("en_US"); // Check it's English version

					// Check that the "username" equals the "trName" meaning that this is the user we are looking for
					if(username.equals(trName))
					{
						// Get the rating
						r = ja.getJSONObject(1); // Get the second index value (JSON Object) from the "ja" JSON Array
						rstring = r.get("value").toString(); // Get the data in String format
						ratingObject = JSONFactoryUtil.createJSONObject(rstring); // Create a JSON Object from this String value
			
						// Get the rating value from the JSON data
						rating = ratingObject.getString("en_US").substring(ratingObject.getString("en_US").indexOf('"') +1, ratingObject.getString("en_US").lastIndexOf('"'));
						
						creator = contentList.get(i).getUserName(); // Get the creator of the feedback 
						creators.add(creator); // Add this creator to the "creators" list
						ratingsArray.add(rating); // Add the rating to the ratings list
						
						// Check if this feedback was given in the last month
						if(contentList.get(i).getCreateDate().after(lastMonth))
						{
							monthAverage.add(rating); // Add to the month ratings list (monthAverage) if it was
						}				
					}
				}
			}
			
			
			
					
			// Get all the Kaleo Task Assignment Instances to see how many are upcoming and complete
			kals = KaleoTaskAssignmentInstanceLocalServiceUtil.getKaleoTaskAssignmentInstances(0, KaleoTaskAssignmentInstanceLocalServiceUtil.getKaleoTaskAssignmentInstancesCount());
			nameMatch = new ArrayList<KaleoTaskAssignmentInstance>(); // Kaleo Task Assignment Instances that are assigned to this TR
			ending = new ArrayList<KaleoTaskAssignmentInstance>(); // Kaleo Task Assignment Instances that ending tasks meaning the process is complete
			thisMonth = 0;    // Counting for this month
			
			// Loop through all the Kaleo Task Assignment Instances
			for(int i = 0; i < kals.size(); i++)
			{
				if(kals.get(i).getKaleoTaskName().equals(firstname)) // If the TR was assigned this request (The task name matches their first name)
				{
					if(kals.get(i).getCreateDate().after(lastMonth)) // Check if it was created in the last month
					{
						thisMonth++; // Increment to track this months request count
					}
					
					nameMatch.add(kals.get(i)); // Add to this request to this TR's list of requests because the task name matches their first name
				}
				else
				{
					// If the task name is past the TR task section add it to the 
					if(kals.get(i).getKaleoTaskName().equals("Form Feedback"))
					{
						ending.add(kals.get(i)); // Add it to the list of these tasks
					}
				}
			}
		
			uc = nameMatch.size(); // Update the number of "upcoming" requests which will be decrement to the right number soon
			compl = 0; // The number of complete starts as 0 and will be incremented 
			
			// Loop through this TR's tasks and the ending tasks to match them
			for(int i = 0; i < nameMatch.size(); i++)
			{
				for(int j = 0; j < ending.size(); j++)
				{
					// If this TR's tasks has the same Kaleo Instance ID as the ending task, then the request must be complete
					if(nameMatch.get(i).getKaleoInstanceId() == ending.get(j).getKaleoInstanceId())
					{
						uc--; // Decrement the upcoming request variable because this request is complete
						compl++;  // Increment the complete request variable because this request is complete
						ending.remove(j); // Remove from the list because it's been used already					
						break; // Break this loop because the requests ending task has been found
					}
				}
			}	

		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Set the attributes for the number of requests completed and upcoming, as well as the number of request set to this TR this month
		renderRequest.setAttribute("outstanding", uc);
		renderRequest.setAttribute("closed", compl);
		renderRequest.setAttribute("thisMonth", thisMonth);
		
		// Get the all time and this months average ratings and set them as attributes
		double a = getAverageRating(ratingsArray);
		double monthA = getAverageRating(monthAverage);
		renderRequest.setAttribute("average", a);
		renderRequest.setAttribute("monthAverage", monthA);

		// Set the attributes for the list of ratings and creators to be displayed on the JSP
		renderRequest.setAttribute("ratingsArray", ratingsArray);
		renderRequest.setAttribute("creators", creators);
		
		// Return the JSP name
		return "/tr_profile.jsp";
	}
	
	// Method to get the average rating from a list of Strings
	double getAverageRating(ArrayList<String> ratingsArray)
	{
		// Check to see if the size is 0 to prevent dividing by zero
		if(ratingsArray.size() == 0)
		{
			return 0;
		}
		else
		{
			int average = 0;
			for(int i = 0; i < ratingsArray.size(); i++)
			{
				average += Integer.valueOf(ratingsArray.get(i));
			}
			double a = (double)average;
			a = a / ratingsArray.size();
			return a;
		}
	}
	
}

 