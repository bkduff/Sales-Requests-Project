package com.liferay.sales.stats.portlet.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.sales.stats.portlet.StatsPortletKeys;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalServiceUtil;

@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + StatsPortletKeys.PORTLET_NAME,
			"mvc.command.name=/stats/outstanding"
	}, service = MVCRenderCommand.class)

public class OutstandingRenderCommand implements MVCRenderCommand{
	
	// Method to render JSP which will display all outstanding request
	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse)
			throws PortletException {
		
		// Get all the Kaleo Instances to find the relevant ones
		List<KaleoInstance> ki = KaleoInstanceLocalServiceUtil.getKaleoInstances(0, KaleoInstanceLocalServiceUtil.getKaleoInstancesCount());
		ArrayList<KaleoInstance> al = new ArrayList<KaleoInstance>(); // ArrayList to hold all relevant Kaleo Instances
		ArrayList<String> companyNames = new ArrayList<String>(); // Array List for names of the request client
		ArrayList<String> demoDates = new ArrayList<String>(); // Array List for the dates of the requests demo
		ArrayList<String> ratingCreators = new ArrayList<String>(); // Array List for names of the request creators
		
		// Loop through the list of all Kaleo Instances
		for(int i = 0; i < ki.size(); i++)
		{
			// Check if the Kaleo Instnace has the correct workflow and isn't complete
			if(ki.get(i).getKaleoDefinitionName().equals("Manual Workflow") && !ki.get(i).getCompleted())
			{
			 	al.add(ki.get(i)); // Add it to the array list for all relevant requests
				String wc = ki.get(i).getWorkflowContext(); // Get the metadata
				try {
					// Turn the data into JSON format
					JSONObject jo = JSONFactoryUtil.createJSONObject(wc);
					String cn = "CustomerName"; // String to identify value
					String demoDate = "$Date$"; // String to identify value
					// Create a JSON Object of the specific value
					JSONObject vals = jo.getJSONObject("map").getJSONObject("serviceContext").getJSONObject("serializable").getJSONObject("attributes").getJSONObject("map");
					Iterator<String> a = vals.keys();
					// Iterate through the values
					while(a.hasNext())
					{
						String key = a.next();
						// Check if the key contains the correct value
						if(key.contains(cn))
						{
							companyNames.add(vals.get(key).toString()); // Add it to the company names if it does
							ratingCreators.add(ki.get(i).getUserName()); // Add the creator to the list of Account Managers
						}
						// Check if the key contains the correct date string
						else if(key.contains(demoDate))
						{
							// Check if there's a pre-selected date
							if(vals.get(key).toString().equals(""))
							{
								demoDates.add("No pre-selected date"); // Add default value if there's no pre-selected date
							}
							else
							{
								demoDates.add(vals.get(key).toString()); // Else add the pre-selected date
							}
						}
						
						
					}
					
					// Set all the relevant attribute values
					renderRequest.setAttribute("ratingCreators", ratingCreators);
					renderRequest.setAttribute("demoDates", demoDates);
					renderRequest.setAttribute("companyNames", companyNames);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		renderRequest.setAttribute("outstandings", al);
		
		// Return the value of the next JSP 
		return "/outstanding.jsp";
	
	}
}
