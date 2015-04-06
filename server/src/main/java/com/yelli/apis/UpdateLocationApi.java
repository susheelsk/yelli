package com.yelli.apis;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import com.yelli.actions.OnAction;
import com.yelli.actions.UpdateTrackerAction;

public class UpdateLocationApi extends HttpHandler{

	@Override
	public void service(Request request, Response response) throws Exception {
		String message = request.getParameter("data");
		OnAction onAction = new UpdateTrackerAction();
		onAction.onMessage(message, null);
		response.getWriter().append("OK");
	}

}
