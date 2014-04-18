package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionServlet extends HttpServlet {
	/**
	 * Generates session state, if none exist.
	 * 
	 */
	private static final long serialVersionUID = 7805279714147643724L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String state = null;
		try {
			state = req.getSession().getAttribute("state").toString();
		} finally {

		}
		// Create a state token to prevent request forgery.
		// Store it in the session for later validation.
		if (state == null) {
			state = new BigInteger(130, new SecureRandom()).toString(32);
			req.getSession().setAttribute("state", state);
		}
		resp.sendRedirect("/velocity");

	}
}
