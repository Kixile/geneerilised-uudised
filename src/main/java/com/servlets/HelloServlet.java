package main.java.com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String placeArr = req.getParameterValues("header")[0];
		String placeArr2 = req.getParameterValues("text-input")[0];
		String placeArr3 = req.getParameterValues("tags-input")[0];
		
		String[] parts = placeArr3.split(", ");
		
		PrintWriter writer = resp.getWriter();
		writer.println("<html><head><title>Hello</title></head><body>");
		writer.println("<p>Header -  "+ placeArr +"</p>");
		writer.println("<p>posted text: " + placeArr2 + "</p>");
		for(String a : parts){
			writer.println( a );
		}
		writer.println("</body></html>");	
	}
}
