package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionServlet extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		  
		
		String state = null;
		try{
			state= req.getSession().getAttribute("state").toString();
		}catch (Exception e){
			//nothing
		}
		// Create a state token to prevent request forgery.
		  // Store it in the session for later validation.
		if (state == null){
		  state = new BigInteger(130, new SecureRandom()).toString(32);
		  req.getSession().setAttribute("state", state);
		}
		  // Read index.html into memory, and set the client ID,
		  // token state, and application name in the HTML before serving it.
//		  return new Scanner(new File("index.html"), "UTF-8")
//		      .useDelimiter("\\A").next()
//		      .replaceAll("[{]{2}\\s*CLIENT_ID\\s*[}]{2}", CLIENT_ID)
//		      .replaceAll("[{]{2}\\s*STATE\\s*[}]{2}", state)
//		      .replaceAll("[{]{2}\\s*APPLICATION_NAME\\s*[}]{2}",
//		                  APPLICATION_NAME);

		  resp.sendRedirect("/velocity");
		  
		  
	}
}
