package vldemo2.servlets;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// process all requested html files with velocity templating engine
// https://velocity.apache.org/engine/releases/velocity-1.7
// https://velocity.apache.org/engine/releases/velocity-1.7/user-guide.html
@WebServlet(value = "*.html")
public class Velocity extends HttpServlet {

    private VelocityEngine engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        engine = createTemplateEngine(config.getServletContext());
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        VelocityContext context = new VelocityContext();
        // add additional data to the context here
        // it can then be used inside the templates
        getTemplate(req).merge(context, resp.getWriter());
    }

    private Template getTemplate(HttpServletRequest req) {
        return engine.getTemplate(req.getRequestURI(), "UTF-8");
    }

}
