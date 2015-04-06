package com.yelli.apis;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class TestServelet extends HttpHandler{

	@Override
	public void service(Request request, Response response) throws Exception {
		response.getWriter().append("<html><head></head><body><h1>Yelli : Test Class</h1><p>This is a sample test page</p></body></html>");
	}

}