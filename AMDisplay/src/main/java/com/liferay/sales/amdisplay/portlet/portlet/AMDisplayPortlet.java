package com.liferay.sales.amdisplay.portlet.portlet;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import java.io.IOException;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=AMDisplay Portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class AMDisplayPortlet extends MVCPortlet {
	// Method for rendering the portlet
	@Override
	public void doView(
		RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		// This is the role ID number for Account Managers
		long roleId = 45002;
		
		// Creates a list of all users with the roleId number
		List<User> users = UserLocalServiceUtil.getRoleUsers(roleId);
		
 		// Set the attribute "am" as the list of Account Managers to be accessed in the JSP
		renderRequest.setAttribute("am", users);
		
		super.doView(renderRequest, renderResponse);
	}
}