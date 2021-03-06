package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class ArticleWriteServlet extends HttpServlet {

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
	public void submit(String header, Timestamp timestamp, String text, int userid, String[] tags){
		try {
			PreparedStatement stmt1 = con.prepareStatement("INSERT INTO artikkel(pealkiri, aeg, sisu, kasutaja_id) VALUES(?, ?, ?, ?);");
			stmt1.setString(1, header);
			stmt1.setTimestamp(2, timestamp);
			stmt1.setString(3, text);
			stmt1.setInt(4, userid);
			stmt1.execute();
			stmt1.close();
			
			int n = 0;
			ResultSet currentTag;
			ResultSet currentArticle;
			PreparedStatement stmt2 = con.prepareStatement("INSERT INTO tag(nimi) VALUES(?)");
			PreparedStatement stmt3 = con.prepareStatement("SELECT tag_id FROM tag WHERE nimi = ?");
			PreparedStatement stmt4 = con.prepareStatement("SELECT artikkel_id FROM artikkel WHERE pealkiri = ? AND aeg = ?");
			PreparedStatement stmt5 = con.prepareStatement("INSERT INTO artikkel_tag(artikkel_id, tag_id) VALUES (?, ?)");
			stmt4.setString(1, header);
			stmt4.setTimestamp(2,  timestamp);
			currentArticle = stmt4.executeQuery();
			currentArticle.next();
			int currentArticleID = currentArticle.getInt("artikkel_id");
			
			while (tags.length > n) {
				tags[n] = tags[n].trim();
				stmt3.setString(1, tags[n]);
				currentTag = stmt3.executeQuery();
				if (!currentTag.next()) {
					stmt2.setString(1, tags[n]);
					stmt2.execute();
					currentTag = stmt3.executeQuery();
					currentTag.next();
				}
				int currentTagID = currentTag.getInt("tag_id");
				stmt5.setInt(1, currentArticleID);
				stmt5.setInt(2, currentTagID);
				stmt5.execute();
				n++;
			}
			stmt2.close();
			stmt3.close();
			stmt4.close();
			stmt5.close();
			con.close();
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
		String [] tags = tag[0].split(",");
		
		//SimpleDateFormat formaat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = new Date();
		Timestamp timestamp = new Timestamp(today.getTime());
		int userid = 1;
		
		//String info = "INSERT INTO artikkel(pealkiri, aeg, sisu, kasutaja_id) "
		//		+ "VALUES('" + head[0] + "', '" + todaystring + "', '" + text[0] + "', " + userid + ");";
		submit(head[0], timestamp, text[0], userid, tags);
		
//		Article a = new Article(head[0], text[0], tag[0]);
//		
//		Gson gson = new Gson();
//		
//		String json = gson.toJson(a);  
//		
//		PrintWriter writer = new PrintWriter("src/main/webapp/json/"+head[0].replaceAll(" ","_")+".json", "UTF-8");
//		writer.println(json);
//		writer.close();
		
		resp.sendRedirect("index.html");
	}
}
