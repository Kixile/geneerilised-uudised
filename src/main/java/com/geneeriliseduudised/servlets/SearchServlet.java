package com.geneeriliseduudised.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Search, cause it will not work, lol", urlPatterns = { "/search"})
public class SearchServlet extends HttpServlet {


	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
	{
		doGet( req, resp );
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendRedirect("/tag/" + req.getParameter("tftextinput"));
	}
}