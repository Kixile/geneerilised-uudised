package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

@WebServlet(name = "Image Servlet", urlPatterns = { "/image/*"})
public class ImageServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 517782719731754231L;

	Connection con = null;
	ResultSet rs = null;
	byte[] imgBytes;

	public void connect() {
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet request(String sql) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
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
		close();
		
		connect();


		StringBuffer string = req.getRequestURL();
		String str = string.toString();
		String[] parts = str.split("/");
		String filename = parts[parts.length-1];
		String[] parts2 = filename.split("\\.");

		try {

			PreparedStatement ps = con.prepareStatement("SELECT fail FROM pilt WHERE nimi = ?");
			ps.setString(1, filename);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					imgBytes = rs.getBytes(1);
					// use the data in some way here
				}
				rs.close();
			}
			ps.close();
			resp.setHeader("Content-Type", "image/"+parts2[1]);
			resp.setContentLength(imgBytes.length);
			resp.getOutputStream().write(imgBytes);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			close();
			//e.printStackTrace();
		}

		close();

	}

}
