package com.geneeriliseduudised.servlets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet(name = "ArticleWriteServlet", urlPatterns = { "/postarticle" })
@MultipartConfig
public class ArticleWriteServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Connection con = null;

	public void connect() {
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void submit(String header, Timestamp timestamp, String text,
			int userid, String[] tags) {
		try {
			PreparedStatement stmt1 = con
					.prepareStatement("INSERT INTO artikkel(pealkiri, aeg, sisu, kasutaja_id) VALUES(?, ?, ?, ?);");
			stmt1.setString(1, header);
			stmt1.setTimestamp(2, timestamp);
			stmt1.setString(3, text);
			stmt1.setInt(4, userid);
			stmt1.execute();
			stmt1.close();

			int n = 0;
			ResultSet currentTag;
			ResultSet currentArticle;
			PreparedStatement stmt2 = con
					.prepareStatement("INSERT INTO tag(nimi) VALUES(?)");
			PreparedStatement stmt3 = con
					.prepareStatement("SELECT tag_id FROM tag WHERE nimi = ?");
			PreparedStatement stmt4 = con
					.prepareStatement("SELECT artikkel_id FROM artikkel WHERE pealkiri = ? AND aeg = ?");
			PreparedStatement stmt5 = con
					.prepareStatement("INSERT INTO artikkel_tag(artikkel_id, tag_id) VALUES (?, ?)");
			stmt4.setString(1, header);
			stmt4.setTimestamp(2, timestamp);
			currentArticle = stmt4.executeQuery();
			currentArticle.next();
			int currentArticleID = currentArticle.getInt("artikkel_id");

			while (tags.length > n) {
				tags[n] = tags[n].trim();
				stmt3.setString(1, tags[n]);
				currentTag = stmt3.executeQuery();
				if (!currentTag.next()) {
					stmt2.setString(1, tags[n]);
					stmt2.execute();
					currentTag = stmt3.executeQuery();
					currentTag.next();
				}
				int currentTagID = currentTag.getInt("tag_id");
				stmt5.setInt(1, currentArticleID);
				stmt5.setInt(2, currentTagID);
				stmt5.execute();
				n++;
			}
			stmt2.close();
			stmt3.close();
			stmt4.close();
			stmt5.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		connect();
		
        Part filePart = req.getPart("userfile1");
        System.out.println(req.getParameter("image_name"));
        
        System.out.println(req.getPart("userfile1").getContentType().split("/")[1]);
        
        InputStream filecontent = null;
        OutputStream out = null;
        
        out = new FileOutputStream(new File("./src/main/webapp/" + File.separator
                + req.getParameter("image_name")+"." +req.getPart("userfile1").getContentType().split("/")[1]));
        
        
        filecontent = filePart.getInputStream();
        
        
        System.out.println((int)filePart.getSize());
        
        
        int read = 0;
        final byte[] bytes = new byte[1024];

        while ((read = filecontent.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        out.close();
        filecontent.close();
        String name =  req.getParameter("image_name")+"." +req.getPart("userfile1").getContentType().split("/")[1];
        try{
        	PreparedStatement ps = con.prepareStatement("INSERT INTO pilt(nimi,fail) VALUES (?,?);");
            ps.setString(1, name);
            ps.setBytes(2, bytes);
            ps.execute();
            ps.close();
            
            con.close();
        }catch (SQLException e) {
			e.printStackTrace();
			try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
        
	}
	
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        doGet( req, resp );
    }
    
    
 
}
