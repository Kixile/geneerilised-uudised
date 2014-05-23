package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

@WebServlet(name = "Edit article", urlPatterns = { "/edit/*", })
public class ArticleEditServlet extends HttpServlet {
	
	public Connection connect() {
		Connection con = null;
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return con;
	}

	public void close(Connection con , ResultSet rs) {
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
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		
		AuthorityHandler auth = new AuthorityHandler();
		Connection con = connect();
		boolean isauth = false;
		boolean isedit = false;
		
		
		try {
			isauth = auth.isLegit(req);
			isedit = auth.isEditor();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		if(isauth && isedit){
			String[] uriChop = req.getRequestURL().toString().split("/");
			if (uriChop.length < 5){
				resp.sendRedirect("/404.html");
			}
			else{
				int id = Integer.parseInt(uriChop[4]);
				
				
				ResultSet rs = null;
				
				Article editable = new Article();
				
				try {
					PreparedStatement ps = con.prepareStatement("select * from artikkel_edit where artikkel_edit.artikkel_id = ? AND on_kustutatud = false");
					ps.setInt(1, id);
					
					
					rs = ps.executeQuery();
					
					if(rs.next()){
						editable = new Article(rs.getString("pealkiri"), rs.getString("sisu"), 
								rs.getString("string_agg"), rs.getString("aeg"), rs.getString("kasutajanimi"), 
								rs.getString("lyhisisu"), rs.getInt("artikkel_id"), rs.getString("pilt"));
					}
					else{
						String lastpageurl = (String) req.getSession().getAttribute("lastpageurl");
						resp.sendRedirect(lastpageurl);
					}
					ps.close();
					
				} catch (SQLException e) {
					close(con, rs);
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				
				Template template = null;
				VelocityContext context = new VelocityContext();
				
				try {
					template = Velocity.getTemplate(
							"./src/main/webapp/templates/template-edit-form.html",
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
				
				close(con, rs);
				
				context.put("article", editable);
				context.put("isAuth", isauth);
				context.put("isEdit", isedit);
				
				StringWriter sw = new StringWriter();
				template.merge(context, sw);
				resp.setCharacterEncoding("UTF-8");
				PrintWriter writer = resp.getWriter();
				writer.println(sw);
				
			}
		}
	}
	

}
