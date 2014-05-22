package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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

@WebServlet(name = "WriteArticle", urlPatterns = { "/write", })
public class ArticleWritePageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private VelocityEngine engine = new VelocityEngine();

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		engine.init();
		Velocity.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		Velocity.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		AuthorityHandler auth = new AuthorityHandler();

		boolean isauth = auth.isLegit(req);
		boolean isedit = auth.isEditor();

		if(isauth && isedit){

		init();
		Template template = null;
		VelocityContext context = new VelocityContext();

		try {
			template = Velocity.getTemplate(
					"./src/main/webapp/templates/template-post-form.html",
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

		System.out.println(template);

		StringWriter sw = new StringWriter();

		template.merge(context, sw);
		resp.setCharacterEncoding("UTF-8");
		PrintWriter writer = resp.getWriter();
		writer.println(sw);
		
		}
		else{
			resp.sendRedirect("/");
		}

	}
}
