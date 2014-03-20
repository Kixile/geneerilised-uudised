package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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

public class ArticleServlte extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private VelocityEngine engine = new VelocityEngine();;
	
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
    }
    private Template getTemplate(HttpServletRequest req) {
        return engine.getTemplate(req.getRequestURI(), "UTF-8");
    }
    
    private VelocityEngine createTemplateEngine(ServletContext context) {
        // velocity must know where /src/main/webapp is deployed
        // details in the developer guide (Configuring resource loaders)
        String templatePath = context.getRealPath("/");

        VelocityEngine engine = new VelocityEngine();
        engine.addProperty("file.resource.loader.path", templatePath);
        engine.init();
        return engine;
    }
    
    
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		Article[] articles = new Article[5];
		articles[0] = new Article("header1", "texttexttexttexttexttexttexttext", "tag1, tag2");
		articles[1] = new Article("header2", "texttexttexttexttexttexttexttext", "tag1, tag2");
		articles[2] = new Article("header3", "texttexttexttexttexttexttexttext", "tag1, tag2");
		articles[3] = new Article("header4", "texttexttexttexttexttexttexttext", "tag1, tag2");
		articles[4] = new Article("header5", "texttexttexttexttexttexttexttext", "tag1, tag2");
		
		
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
