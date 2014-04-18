package com.geneeriliseduudised.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CookieReadServlet", urlPatterns = { "/cookie" })
public class CookieReadServlet extends HttpServlet {

	/**
	 * Test servlet for reading cookies
	 * 
	 */
	private static final long serialVersionUID = -4602380048998493062L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Cookie[] cookies = req.getCookies();

		String sessionId = null;
		for (Cookie cookie : cookies) {
			if ("SESSIONID".equals(cookie.getName())) {
				sessionId = cookie.getValue();
			}
		}
		resp.getWriter().println("Session id is: " + sessionId);

	}

}
