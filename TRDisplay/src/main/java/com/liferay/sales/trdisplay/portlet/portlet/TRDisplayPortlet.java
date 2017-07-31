package com.liferay.sales.trdisplay.portlet.portlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=TRDisplay Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class TRDisplayPortlet extends MVCPortlet {
	
	// Method for rendering the portlet
	@Override
	public void doView(
		RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		// This is the role ID number for Technical Resources
		long roleId = 31197;
		
		// Creates a list of all users with the role ID 
		List<User> users = UserLocalServiceUtil.getRoleUsers(roleId);
 		
		// Set the attribute "tr" as the list of Technical Resources to be accessed in the JSP
		renderRequest.setAttribute("tr", users);
		
		super.doView(renderRequest, renderResponse);
	}
}