package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
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

@WebServlet(name = "Display Servlet", urlPatterns = { "/page/*",
"index.html" })
public class ArticleDisplayServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private VelocityEngine engine = new VelocityEngine();

	private Connection con = null;
	private ResultSet rs = null;
	private ResultSet rs2 = null;

	public void connect() {
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		engine.init();
		Velocity.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		Velocity.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
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

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// for buttons and better article scrolling
		close();
		String uri = req.getRequestURI() ;
		init();
		connect();
		Statement stmt = null;

		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e3) {
			System.out.println("hmmm...");
		}

		String[] uriSplit = uri.split("/");
		int pageIndex = 0;
		System.out.println("URI LEN ----------" + uriSplit.length);
		if(uriSplit.length < 3){
			pageIndex = 0;
		}
		else{
			System.out.println(uriSplit[0]+" "+ uriSplit[1] +" "+ uriSplit[2]);
			int index = 0;
			try{
				index = Integer.parseInt(uriSplit[2]);
			}catch(Exception e){
				
			}
			pageIndex = index;
		}
		
		


		String sql = "SELECT * FROM artikkel_create LIMIT 5 OFFSET "+ pageIndex * 5 +";";


		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		int rowAmmount = 0;

		List<Article> articlesList = new ArrayList<Article>();
		Statement stmt2 = null;
		ResultSet rs2 = null;

		String sql2 = "SELECT artikkel_tag.artikkel_id, string_agg(tag.nimi, ', ') "
				+ "FROM tag, artikkel_tag where tag.tag_id = artikkel_tag.tag_id "
				+ "GROUP BY artikkel_tag.artikkel_id ORDER BY artikkel_tag.artikkel_id "
				+ "DESC LIMIT 5 OFFSET "+ 0 +";";


		try {
			stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e3) {

		}

		try {
			rs2 = stmt2.executeQuery(sql2);
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		List<String> names = new ArrayList<String>();
		
		try {
			while (rs.next() && rs2.next()) {
				String tagstring = "";

				tagstring = rs2.getString("string_agg");

				if(tagstring == null) tagstring = "";

				Article art = new Article(rs.getString("pealkiri"), rs
						.getString("sisu"), tagstring, rs.getString("aeg"), rs
						.getString("kasutajanimi"), rs.getString("lyhisisu"), rs
						.getString("artikkel_id"), rs.getString("pilt"));

				articlesList.add(art);
				names.add(rs.getString("kasutajanimi"));
				rowAmmount++;
			}
			stmt.close();
			stmt2.close();
			rs.close();
			rs2.close();
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		VelocityContext context = new VelocityContext();
		System.out.println(articlesList.size());


		context.put("next", "/page/" + (pageIndex + 1));
		context.put("previous", "/page/" + (pageIndex - 1));
		context.put("uri", uri);
		context.put("pagein", pageIndex);
		context.put("index", rowAmmount);
		context.put("artList", articlesList);

		AuthorityHandler auth = new AuthorityHandler();

		boolean isauth = auth.isLegit(req);
		boolean isedit = auth.isEditor();
		context.put("isAuth", isauth);
		context.put("isEdit", isedit);

		Template template = null;

		try {
			template = Velocity.getTemplate(
					"./src/main/webapp/templates/template-velocity.html",
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

		close();
	}
}
