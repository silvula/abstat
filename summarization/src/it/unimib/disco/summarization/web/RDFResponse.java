package it.unimib.disco.summarization.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;

public class RDFResponse implements Response{

	private Api api;

	public RDFResponse(Api api) {
		this.api = api;
	}

	@Override
	public void sendTo(Request base, HttpServletResponse response, RequestParameters parameters) throws Exception {
		response.setContentType("application/rdf+xml");
		response.addHeader("Access-Control-Allow-Origin", "*");
		IOUtils.copy(this.api.get(parameters), response.getOutputStream());
		base.setHandled(true);
	}
}
