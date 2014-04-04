package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuthSigninServlet extends HttpServlet{
	
	//XXX: localhost:8080 only!
	//TODO: move out from source code
	// heroku config:set GITHUB_USERNAME=joesmith
	// System.getEnv("envar")
	public static final String OAUTH_CLIENT_ID = "101097860686-9i8l9hr9n8nd9slb8njk001t6qcf6iop.apps.googleusercontent.com";
	public static final String OAUTH_CLIENT_SECRET = "PmhD1dBIhnrD3ekvPaJHl61-";
	public static final String OAUTH_REDIRECT_URI = "http://localhost:8080/oauth2callback";
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String state = null;
		try{		
			state = req.getSession().getAttribute("state").toString();
		}catch (Exception e){
			  state = new BigInteger(130, new SecureRandom()).toString(32);
			  req.getSession().setAttribute("state", state);
		}
		StringBuilder oauthUrl = new StringBuilder().append("https://accounts.google.com/o/oauth2/auth")
			   .append("?client_id=").append(OAUTH_CLIENT_ID) // the client id from the api console registration
			   .append("&response_type=code")
			   .append("&scope=https://www.googleapis.com/auth/userinfo.email") // scope is the api permissions we are requesting
			   .append("&redirect_uri=").append(OAUTH_REDIRECT_URI) // the servlet that google redirects to after authorization
			   .append("&state=").append(state)
			   .append("&access_type=offline") // here we are asking to access to user's data while they are not signed in
			   .append("&approval_prompt=force"); // this requires them to verify which account to use, if they are already signed in
		resp.sendRedirect(oauthUrl.toString());
	}
}