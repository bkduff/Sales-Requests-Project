package com.liferay.sales.trdisplay.portlet.portlet.command;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ProcessAction;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.sales.trdisplay.portlet.portlet.TRDisplayPortletKeys;

@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + TRDisplayPortletKeys.PORTLET_NAME,
			"mvc.command.name=/trdisplay/select"
	},
		service = MVCActionCommand.class)

public class TRDisplayMVCActionCommand extends BaseMVCActionCommand{
	@ProcessAction(name = "selecttr")
	protected void getTR(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		String trName = ParamUtil.getString(actionRequest, "trName");
		System.out.println("tr: " + trName);
		
		actionResponse.setRenderParameter("trName", trName);
	}

	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		getTR(actionRequest,actionResponse);
	}
}
