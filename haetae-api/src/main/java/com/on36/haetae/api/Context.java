package com.on36.haetae.api;

import java.util.Set;

import com.on36.haetae.api.http.Session;

public interface Context {

	String getPath();
	
	String getRequestParameter(String param);
	
	String getHeaderValue(String param);
	
	Set<String> getRequestParameterNames();
	
	Session getSession();

}