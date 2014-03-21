package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class ArticleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private VelocityEngine engine = new VelocityEngine();;
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
	
	public ResultSet request(String sql){
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
//			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}	
	
	
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        connect();
    }
    
    
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String sql = "SELECT * FROM artikkel_create";
		ResultSet rs = request(sql);
		Article[] articles = new Article[5];
		try {
			int n = 0;
			while(rs.next() && n < 5){
				articles[n] = new Article(rs.getString("pealkiri"), 
						rs.getString("sisu"), "helo", rs.getString("aeg"),
						rs.getString("kasutajanimi"), rs.getString("lyhisisu"));
				n++;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
//		try {
//			rs.close();
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}

//		articles[0] = new Article("header333", "texttexttexttexttexttexttexttext", "tag1, tag2");
//		articles[1] = new Article("header2", "texttexttexttexttexttexttexttext", "tag1, tag2");
//		articles[2] = new Article("header3", "texttexttexttexttexttexttexttext", "tag1, tag2");
//		articles[3] = new Article("header4", "texttexttexttexttexttexttexttext", "tag1, tag2");
//		articles[4] = new Article("header5", "texttexttexttexttexttexttexttext", "tag1, tag2");
		
		
		VelocityContext context = new VelocityContext();
		context.put( "name", new String("Velocity") );
		context.put("test", articles);
		Template template = null;
		
		
		
		try
		{
		   template = Velocity.getTemplate("./src/main/webapp/templates/template-velocity.html");
		}
		catch( ResourceNotFoundException rnfe )
		{
		   // couldn't find the template
		}
		catch( ParseErrorException pee )
		{
		  // syntax error: problem parsing the template
		}
		catch( MethodInvocationException mie )
		{
		  // something invoked in the template
		  // threw an exception
		}
		catch( Exception e )
		{}
		
		StringWriter sw = new StringWriter();

		template.merge( context, sw );
		
		PrintWriter writer = resp.getWriter();
		writer.println(sw);

	}
}
