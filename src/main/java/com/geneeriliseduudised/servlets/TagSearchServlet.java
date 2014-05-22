package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

		close();
		if (uriChop.length < 5){
			resp.sendRedirect("/");
		}
		else{
			System.out.println(uriChop[4]);
			tag = uriChop[4].replace("%20", " ");
			connect();
			ResultSet rs = null;
			List<Integer> articles = new ArrayList<Integer>();
			List<Article> art = new ArrayList<Article>();
			
			try {
				PreparedStatement ps = con.prepareStatement("SELECT artikkel_tag.artikkel_id from tag, artikkel_tag "
						+ "where tag.tag_id = artikkel_tag.tag_id AND tag.nimi = ?");
				ps.setString(1, tag);
				
				
				rs = ps.executeQuery();
				
				while(rs.next()){
					articles.add(rs.getInt("artikkel_id"));
				}
				ps.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			close();
			
			connect();
			
			try {
				System.out.println(tag);
				Integer[] array = new Integer[articles.size()];
				articles.toArray(array);
				PreparedStatement ps = con.prepareStatement("SELECT * FROM tagid WHERE "
						+ "artikkel_id = ANY (?)");
				ps.setArray(1, con.createArrayOf("int", array));
				
				rs = ps.executeQuery();
				
				while(rs.next()){
					articles.add(rs.getInt("artikkel_id"));
					System.out.println(rs.getInt("artikkel_id"));
					art.add(new Article(rs.getString("pealkiri"), rs
						.getString("sisu"), rs.getString("string_agg"), rs.getString("aeg"), rs
						.getString("kasutajanimi"), rs.getString("lyhisisu"), rs
						.getString("artikkel_id"), rs.getString("pilt")));
				}
				ps.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			close();
			
			VelocityContext context = new VelocityContext();

			context.put("articles", art);
			
			Template template = null;
			
			System.out.println(art.size());
			
			try {
				template = Velocity.getTemplate(
						"./src/main/webapp/templates/template-velocity-search.html",
						"UTF-8");

			} catch (ResourceNotFoundException rnfe) {
				// couldn't find the template
			} catch (ParseErrorException pee) {
				// syntax error: problem parsing the template
			} catch (MethodInvocationException mie) {
				// something invoked in the template
				// threw an exception
			} catch (Exception e) {

			}
			// Writer sw = new PrintWriter(new PrintStream(resp.getOutputStream(),
			// true, encoding));
			StringWriter sw = new StringWriter();

			template.merge(context, sw);
			resp.setCharacterEncoding("UTF-8");
			PrintWriter writer = resp.getWriter();
			writer.println(sw);
			
		}
	}
	
	protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
	{
		doGet( req, resp );
	}
}
