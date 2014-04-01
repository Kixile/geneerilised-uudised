package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Connection con = null;
	
	public void connect(){
		try {
			con = DriverManager.getConnection(
					"jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
					);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void submit(String sql){
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeQuery(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		connect();
		String[] head = req.getParameterValues("header");
		String[] text = req.getParameterValues("text-input");
		String[] tag = req.getParameterValues("tags-input");
		DateFormat formaat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = new Date();
		String todaystring = formaat.format(today);
		String userid = "1";
		
		String info = "INSERT INTO artikkel(pealkiri, aeg, sisu, kasutaja_id) "
				+ "VALUES('" + head[0] + "', '" + todaystring + "', '" + text[0] + "', " + userid + ");";
		submit(info);
		
//		Article a = new Article(head[0], text[0], tag[0]);
//		
//		Gson gson = new Gson();
//		
//		String json = gson.toJson(a);  
//		
//		PrintWriter writer = new PrintWriter("src/main/webapp/json/"+head[0].replaceAll(" ","_")+".json", "UTF-8");
//		writer.println(json);
//		writer.close();
		
		resp.sendRedirect("/");
	}
}
