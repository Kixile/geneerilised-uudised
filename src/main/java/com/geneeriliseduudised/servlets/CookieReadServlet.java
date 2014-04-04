package com.geneeriliseduudised.servlets;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieReadServlet extends HttpServlet{
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Cookie[] cookies = req.getCookies();

		String genericNewsUserId = null;
		for(Cookie cookie : cookies){
		    if("genericNewsUserId".equals(cookie.getName())){
		    	genericNewsUserId = cookie.getValue();
		    }
		}
		resp.getWriter().println("Session is: "+ genericNewsUserId);
		
	}

}
