package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DbConn", urlPatterns = { "/articledump" })
public class DbConn extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4929054734671868212L;
	Connection con = null;

	@Override
	public void init() {
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}