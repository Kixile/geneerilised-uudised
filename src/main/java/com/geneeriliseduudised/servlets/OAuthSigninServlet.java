package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.lang.System;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "OAuthSigninServlet", urlPatterns = { "/login" })
public class OAuthSigninServlet extends HttpServlet {

	/**
	 * Handles creating and sending OAuth token requests to Google.
	 * 
	 */

	// environment variables have to be set!
	// example: heroku config:set GITHUB_USERNAME=joesmith

	private static final long serialVersionUID = -7761963272295233213L;
	public static String OAUTH_CLIENT_ID = null;
	public static String OAUTH_CLIENT_SECRET = null;
	public static String OAUTH_REDIRECT_URI = null;

	public void init() {
		OAUTH_CLIENT_ID = System.getenv("OAUTH_CLIENT_ID");
		OAUTH_CLIENT_SECRET = System.getenv("OAUTH_CLIENT_SECRET");
		OAUTH_REDIRECT_URI = System.getenv("OAUTH_REDIRECT_URI");
		if (OAUTH_CLIENT_ID == null | OAUTH_CLIENT_SECRET == null
				| OAUTH_REDIRECT_URI == null) {
			throw new NullPointerException(
					"Invalid OAuth ID / SECRET / REDIRECT URI");
		}

	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String state = null;
		try {
			state = req.getSession().getAttribute("state").toString();
		} catch (Exception e) {
			state = new BigInteger(130, new SecureRandom()).toString(32);
			req.getSession().setAttribute("state", state);
		}
		
		String lastpageurl = req.getHeader("Referer");
		req.getSession().setAttribute("lastpageurl", lastpageurl);

		
		StringBuilder oauthUrl = new StringBuilder()
				.append("https://accounts.google.com/o/oauth2/auth")
				.append("?client_id=").append(OAUTH_CLIENT_ID)
				// the client id from the api console registration
				.append("&response_type=code")
				.append("&scope=https://www.googleapis.com/auth/userinfo.email") // scope
																					// is
																					// the
																					// api
																					// permissions
																					// we
																					// are
																					// requesting
				.append("&redirect_uri=").append(OAUTH_REDIRECT_URI) // the
																		// servlet
																		// that
																		// google
																		// redirects
																		// to
																		// after
																		// authorization
				.append("&state=").append(state).append("&access_type=offline") // here
																				// we
																				// are
																				// asking
																				// to
																				// access
																				// to
																				// user's
																				// data
																				// while
																				// they
																				// are
																				// not
																				// signed
																				// in
				.append("&approval_prompt=force"); // this requires them to
													// verify which account to
													// use, if they are already
													// signed in
		resp.sendRedirect(oauthUrl.toString());
	}
}