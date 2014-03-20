package com.geneeriliseduudised.servlets;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DbConn extends HttpServlet {
	
	
	Connection con = null;
	@Override 
	public void init() {
		try {
			con = DriverManager.getConnection(
					"jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
					);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override 
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String sql = "SELECT * FROM artikkel;";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String pealkiri = rs.getString("pealkiri");
				resp.getWriter().println(pealkiri);
			}
			rs.close();
			stmt.close();
		}	catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

	
//	public static void main(String[] argv) throws SQLException {
//		 
//		System.out.println("-------- PostgreSQL "
//				+ "JDBC Connection Testing ------------");
// 
//		try {
// 
//			Class.forName("org.postgresql.Driver");
// 
//		} catch (ClassNotFoundException e) {
// 
//			System.out.println("Where is your PostgreSQL JDBC Driver? "
//					+ "Include in your library path!");
//			e.printStackTrace();
//			return;
// 
//		}
// 
//		System.out.println("PostgreSQL JDBC Driver Registered!");
// 
//		Connection connection = null;
//		Statement stmt = null;
// 
//		try {
// 
//			connection = DriverManager.getConnection(
//					"jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
//					);
// 
//		} catch (SQLException e) {
// 
//			System.out.println("Connection Failed! Check output console");
//			e.printStackTrace();
//			return;
// 
//		}
// 
//		if (connection != null) {
//			System.out.println("You made it, take control your database now!");
//			stmt = connection.createStatement();
//			String sql = "SELECT * FROM artikkel;";
//			ResultSet rs = stmt.executeQuery(sql);
//			while (rs.next()) {
//				String pealkiri = rs.getString("artikkel_id");
//				System.out.println("Pealkiri: " + pealkiri);
//			}
//			rs.close();
//			stmt.close();
//			connection.close();
//		} else {
//			System.out.println("Failed to make connection!");
//		}
//	}
//}