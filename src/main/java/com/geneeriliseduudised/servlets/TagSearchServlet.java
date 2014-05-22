package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "Tag article shit", urlPatterns = { "/tag/*"})
public class TagSearchServlet extends HttpServlet{

	String tag = "";

	Connection con = null;
	ResultSet rs = null;

	public void connect() {
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) { /* ignore */
			}
			rs = null;
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) { /* ignore */
			}
			con = null;
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String[] uriChop = req.getRequestURL().toString().split("/");


		if (uriChop.length < 5){
			resp.sendRedirect("/");
		}
		else{
			System.out.println(uriChop[4]);
			tag = uriChop[4];

		}
	}
}
