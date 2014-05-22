package com.geneeriliseduudised.servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class AuthorityHandler {
	private Connection con = null;
	private ResultSet rs = null;
	private int kas_id = 0;

	public AuthorityHandler() {
	}
	
	public void connect() {
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isLegit(HttpServletRequest req){
		Statement stmt = null;
		Cookie[] cookies = req.getCookies();
		String[] aa = new String[2];
		for (int i = 0; i < cookies.length; i++) {
			aa[i] = cookies[i].getValue();
		}
		
		connect();
		try {
			String query = "SELECT kasutaja_id FROM sessioonid WHERE sessioon_id = '"+ aa[1] +"';";
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				kas_id = rs.getInt("kasutaja_id");
				return true;
			}
			stmt.close();
			rs.close();
			con.close();
		} catch (SQLException e3) {
			e3.printStackTrace();
			try {
				stmt.close();
				rs.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean isEditor(){
		Statement stmt = null;
		
		connect();
		try {
			String query = "SELECT * FROM kasutaja WHERE autor = 'true' and  kasutaja_id = '" + kas_id + "';";
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				return true;
			}
			stmt.close();
			rs.close();
			con.close();
		} catch (SQLException e3) {
			e3.printStackTrace();
			try {
				stmt.close();
				rs.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
}
