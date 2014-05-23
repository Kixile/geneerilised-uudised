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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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

@WebServlet(name = "Single article servlet", urlPatterns = { "/article/*", })
public class MainArticleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private VelocityEngine engine = new VelocityEngine();

	public Connection con = null;
	public ResultSet rs = null;

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
		String uri = req.getRequestURI();
		init();
		connect();
		Statement stmt = null;

		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e3) {

		}

		String[] uriSplit = uri.split("/");

		if (isInteger(uriSplit[2])) {
			String sql = "SELECT * FROM artikkel_create where artikkel_id = "
					+ uriSplit[2];

			try {
				rs = stmt.executeQuery(sql);
			} catch (SQLException e3) {
				e3.printStackTrace();
			}

			Article article = new Article();

			try {
				while (rs.next()) {
					article = new Article(rs.getString("pealkiri"),
							rs.getString("sisu"), "helo", rs.getString("aeg"),
							rs.getString("kasutajanimi"),
							rs.getString("lyhisisu"),
							rs.getInt("artikkel_id"), rs.getString("pilt"));
				}
			} catch (SQLException e1) {
				close();
			}
			
			close();
			
			VelocityContext context = new VelocityContext();
			
			AuthorityHandler auth = new AuthorityHandler();
			
			boolean isauth = false;
			boolean isedit = false;
			
			
			try {
				isauth = auth.isLegit(req);
				isedit = auth.isEditor();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			context.put("isAuth", isauth);
			context.put("isEdit", isedit);

			context.put("uri", uriSplit[2]);
			context.put("urisplit", uriSplit);
			context.put("art", article);
			Template template = null;

			try {
				template = Velocity
						.getTemplate(
								"./src/main/webapp/templates/template-velocity-article.html",
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
			StringWriter sw = new StringWriter();

			template.merge(context, sw);
			resp.setCharacterEncoding("UTF-8");
			PrintWriter writer = resp.getWriter();
			writer.println(sw);
		}

		else resp.sendRedirect("#");
		
		close();
	}
}
