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

	public boolean isLegit(HttpServletRequest req){ //kaput with dB
		Statement stmt = null;
		Cookie[] cookies = req.getCookies();
		String[] aa = new String[2];
		try{
			for (int i = 0; i < cookies.length; i++) {
				aa[i] = cookies[i].getValue();
			}
		}
		catch (Exception e){
			return false;
		}
		close();
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
			close();
		} catch (SQLException e3) {
			close();
		}

		close();

		return false;
	}

	public boolean isEditor(){ //kaput with DB
		Statement stmt = null;
		close();
		connect();
		try {
			String query = "SELECT * FROM kasutaja WHERE autor = 'true' and  kasutaja_id = '" + kas_id + "';";
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				return true;
			}
			stmt.close();
			close();
		} catch (SQLException e3) {
			close();
		}

		close();

		return false;
	}

	public void logout(HttpServletRequest req){
		close();
		connect();

		Statement stmt = null;
		Cookie[] cookies = req.getCookies();
		String[] aa = new String[2];
		try{
			for (int i = 0; i < cookies.length; i++) {
				aa[i] = cookies[i].getValue();
			}
		}
		catch (Exception e){
		}

		try {
			PreparedStatement stmt1 = con.prepareStatement("UPDATE sessioonid SET sessioon_id = ?  WHERE  sessioon_id = ?;");
			stmt1.setString(1, "");
			stmt1.setString(2, aa[1]);
			stmt1.executeUpdate();
			stmt1.close();
			close();
		} catch (SQLException e) {
			close();
		}
		
		close();
	}

	public String getName(HttpServletRequest req){
		String id = getSessionId(req);
		Statement stmt = null;
		close();
		connect();

		try {
			String query = "SELECT kasutajanimi FROM kasutaja_sessioon WHERE sessioon_id = '"+ id +"';";
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				return rs.getString("kasutajanimi");
			}
			stmt.close();
			rs.close();
			close();
		} catch (Exception e3) {
			close();
		}

		close();
		
		return null;
	}

	public String getSessionId(HttpServletRequest req){
		Statement stmt = null;
		Cookie[] cookies = req.getCookies();
		String[] aa = new String[2];
		try{
			for (int i = 0; i < cookies.length; i++) {
				aa[i] = cookies[i].getValue();
			}
		}
		catch (Exception e){
			return null;
		}
		return aa[1];
	}

	public int getUserId(HttpServletRequest req){
		String id = getSessionId(req);
		Statement stmt = null;
		close();
		connect();
		
		try {
			String query = "SELECT kasutaja.kasutaja_id FROM kasutaja, sessioonid where sessioonid.sessioon_id = '"+ id +"' "
					+ "AND sessioonid.kasutaja_id = kasutaja.kasutaja_id;";
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				return rs.getInt("kasutaja_id");
			}
			stmt.close();
			rs.close();
			close();
		} catch (Exception e3) {
			close();
		}
		
		close();
		return 0;
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

}
