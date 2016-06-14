package it.unimib.disco.summarization.web;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;

public class HttpParameters implements RequestParameters {

	private HttpServletRequest request;

	public HttpParameters(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String get(String name) {
		//stratagemma pper evitare che venga codificato in utf-8 dato che valori contenenti caratteri speciali devono rimanere tali.
		String req = request.toString();
		String[] parameters = req.substring(req.indexOf("?")+1, req.lastIndexOf(")")).split("&");
		String value = null;
		for(String parameter : parameters){
			String campo = parameter.substring(0, parameter.indexOf("="));
			if(campo.equals(name)){
				value = parameter.substring(parameter.indexOf("=")+1);
				if(value.contains("%23"))
					value = value.replace("%23", "#");
			}
		}
		
		return value;
	}

}
