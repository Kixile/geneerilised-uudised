package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.anakia.OutputWrapper;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class ArticleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private VelocityEngine engine = new VelocityEngine();

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
			stmt.close();
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
        Velocity.setProperty( Velocity.INPUT_ENCODING, "UTF-8" );
        Velocity.setProperty( Velocity.OUTPUT_ENCODING, "UTF-8" );
        connect();
    }
    
    
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {

		
		//for buttons and better article scrolling
		String uri = /*req.getScheme() + "://" +
	             req.getServerName() + */
	            /* ("http".equals(req.getScheme()) && req.getServerPort() == 80 || "https".equals(req.getScheme()) && req.getServerPort() == 443 ? "" : ":" + req.getServerPort() ) +
	             */req.getRequestURI() /*+
	            (req.getQueryString() != null ? "?" + req.getQueryString() : "")*/;
		
		
		init();
		
		Statement stmt = null;
		
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			//e3.printStackTrace();
		}

		
		String sql = "SELECT * FROM artikkel_create";
		ResultSet rs = null;
		
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		int rowAmmount = 0;

		List<Article> articlesList = new ArrayList<Article>();
		
		try {
			while(rs.next()){
				articlesList.add(new Article(rs.getString("pealkiri"), 
						rs.getString("sisu"), "helo", rs.getString("aeg"),
						rs.getString("kasutajanimi"), rs.getString("lyhisisu"), rs.getString("artikkel_id")));
				rowAmmount ++;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		VelocityContext context = new VelocityContext();
		
		String[] uriSplit = uri.split("/");
		int pageIndex = 0;
		if (uriSplit.length < 3){
			articlesList = articlesList.subList(0, 5);
		}
		else if ((WordUtils.uncapitalize(uriSplit[2].substring(0,4))).equals("page")){
			pageIndex = Integer.parseInt(uriSplit[2].replaceAll("\\D+",""));
			if (pageIndex*5 > rowAmmount){
				articlesList = articlesList.subList((pageIndex-1)*5, rowAmmount);
			}
			else{
				articlesList = articlesList.subList((pageIndex-1)*5, (pageIndex)*5);
			}
			
		}
		else{
			articlesList = articlesList.subList(0, 1);
		}
		
		context.put("next", "/article/page" + (pageIndex + 1));
		context.put("previous", "/article/page" + (pageIndex - 1));
		context.put("nextPagecheck", (pageIndex*5 > rowAmmount));
		context.put("uri", uri);
		context.put("uri2", uriSplit.length);
		//context.put("uri3", uriSplit[2]);
		//context.put("debug1", (WordUtils.uncapitalize(uriSplit[2].substring(0,4))).equals("page"));
		context.put("index", rowAmmount);
		context.put("artList", articlesList);
		Template template = null;
		
		try
		{
		   template = Velocity.getTemplate("./src/main/webapp/templates/template-velocity.html", "UTF-8");
		   
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
		{
			
		}
		//Writer sw = new PrintWriter(new PrintStream(resp.getOutputStream(), true, encoding));
		StringWriter sw = new StringWriter();

		template.merge( context, sw );
		
		PrintWriter writer = resp.getWriter();
		writer.println(sw);
		
	}
}
