package com.liferay.sales.amhhome.portlet.portlet.command;

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
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalServiceUtil;

import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentInstanceLocalServiceUtil;
import com.liferay.sales.amhhome.portlet.portlet.AMHomePortletKeys;

@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + AMHomePortletKeys.PORTLET_NAME,
			"mvc.command.name=/amhomes/viewAM"
	}, service = MVCRenderCommand.class)

public class ViewAMRenderCommand implements MVCRenderCommand{
	
	// Method for rendering the summary of an Account Manager
	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse)
			throws PortletException {

		// Get the user ID of the Account Manager selected
		long amId = ParamUtil.getLong(renderRequest, "AM");
		
		// Create variables for user details
		String username = "";
		String firstname = "";
		// List of DDMContent, all the form data is stored in DDMContents
		List<DDMContent> contentList = DDMContentLocalServiceUtil.getContents();
		
		// All Variables needed to get the necessary information
		ArrayList<String> ratingsArray = new ArrayList<String>(); // ArrayList to hold the ratings of the Account Managers
		ArrayList<String> creators = new ArrayList<String>(); // ArrayList to hold the creators of these ratings
		ArrayList<String> monthAverage = new ArrayList<String>(); // ArrayList to hold this months ratings
		 
		String creator = ""; // String for name of the creator
		String fieldVals = ""; // String to hold the field values of the JSONObject "jo"
		JSONObject jo = null;  // JSON Object to hold information from the results
		JSONArray ja = null; // JSON Array to hold the field values, turns the string "fieldVals" into a JSON Array
		JSONObject cname = null; // First Object of the JSON Array "ja" which contains the field values
		JSONObject amNameJSON = null; // Object from the array which contains the name of the Account Manager that was reviewed
		String amName = ""; // String to hold the Account Managers name
		JSONObject r = null; // JSON Object that is taking from "ja" that contains the rating information
		String rstring = ""; // Gets value from "r" in String format
		JSONObject ratingObject = null; // JSON Object created from the String "rstring" and it contains the rating
		String rating = ""; // String to hold the rating value
		
		
		// Create a date which is one month ago to track this months stats
		Date today = new Date();
		long n = 31;
		Date lastMonth = new Date(today.getTime() - n * 24 * 3600 * 1000 );		
		
		
		// Counters to record number of each section, upc = requested but not yet complete, com = requests that are completed, thisMonth = Requests that have been made this month
		int upc = 0;
		int com = 0;
		int thisMonth = 0;
					
		// Try Catch to get a specific user
		try {
			
			// Get the user by their User ID
			User am = UserLocalServiceUtil.getUser(amId);
			
			// Set the attribute "am" to be used on the JSP
			renderRequest.setAttribute("am", am);
			
			// Update the name variables
			username = am.getFullName();
			firstname = am.getFirstName();
			
			
			// Get all Kaleo Instances which contain the necessary information
			List<KaleoInstance> kals = KaleoInstanceLocalServiceUtil.getKaleoInstances(0, KaleoInstanceLocalServiceUtil.getKaleoInstancesCount());
			
			// Loop through these Kaleo Instances to find the relevant ones
			for(int i = 0; i < kals.size(); i++)
			{
				
				// A Kaleo Instance is relevant is its definintion name is "Manual Workflow", this means they use the custom workflow created
				if(kals.get(i).getKaleoDefinitionName().equals("Manual Workflow") && kals.get(i).getUserName().equals(am.getFullName()))
				{
					
					// Check is the Kaleo Instance is complete
					if(kals.get(i).getCompleted())
					{
						// Increment the complete variable (com) if it is complete
						com++;
					}
					else
					{
						// If its not complete increment the non-complete variable (upc)
						upc++;
					}
					
					// Check if this Kaleo Instance was created in the last month using the lastMonth variable created earlier
					if(kals.get(i).getCreateDate().after(lastMonth))
					{
						// Increment the variable (thisMonth) recording all new requests made this month 
						thisMonth++;
					}
				}
				
			}
			
			
			
			// Loop to loop through the form information which is in the form of DDMContent
			for(int i = 0; i < contentList.size(); i++)
			{
				
			
				jo = JSONFactoryUtil.createJSONObject(contentList.get(i).getData()); // Turn the form data into JSON format
	
				fieldVals = jo.get("fieldValues").toString(); // Turn into String format
				ja = JSONFactoryUtil.createJSONArray(fieldVals); // Create a JSON Array of the data
				
				cname = ja.getJSONObject(0); // Get the first index value to see if the DDMContent is relevant
				
				// Check if the data is relevant by checking the "name" value, if its relevant it should equal "PleaseEnterTheNameOfTheSalesMember"
				if(cname.getString("name").equals("PleaseEnterTheNameOfTheSalesMember")) // This is the first question on the feedback form
				{
					amNameJSON = cname.getJSONObject("value"); // Get a JSON Object which contains the information about the Account Managers name
					amName = amNameJSON.getString("en_US"); // Check it's English version

					// Check that the "username" equals the "amName" meaning that this is the user we are looking for
					if(username.equals(amName))
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
			
			
		
			
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		renderRequest.setAttribute("upc", upc); // Set the attribute for the number of requested and non complete demos
		renderRequest.setAttribute("com", com); // Set the attribute for the number of complete demos
		renderRequest.setAttribute("thisMonth", thisMonth); //  // Set the attribute for the number of requested demos this month

		double a = getAverageRating(ratingsArray); // Get the average rating for all time
		double monthA = getAverageRating(monthAverage); // Get the average rating for this month
		renderRequest.setAttribute("average", a); // Set the overall average rating
		renderRequest.setAttribute("monthAverage", monthA); // Set this months average rating

		renderRequest.setAttribute("ratingsArray", ratingsArray); // Send the ArrayList of ratings to the JSP
		renderRequest.setAttribute("creators", creators); // Send the ArrayList of rating creators to the JSP
		
		// Return the JSP page
		return "/am_profile.jsp";
	}
	
	// Method for getting the average 
	double getAverageRating(ArrayList<String> ratingsArray)
	{
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
