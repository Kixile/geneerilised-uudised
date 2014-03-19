package main.java.com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class HelloServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String[] head = req.getParameterValues("header");
		String[] text = req.getParameterValues("text-input");
		String[] tag = req.getParameterValues("tags-input");
		
		Article a = new Article(head[0], text[0], tag[0]);
		
		Gson gson = new Gson();
		
		String json = gson.toJson(a);  
		
		PrintWriter writer = new PrintWriter("src/main/webapp/json/"+head[0].replaceAll(" ","_")+".json", "UTF-8");
		writer.println(json);
		writer.close();
		
		resp.sendRedirect("index.html");
	}
}
