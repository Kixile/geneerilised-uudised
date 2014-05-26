package com.geneeriliseduudised.servlets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.imgscalr.Scalr;

@WebServlet(name = "ArticleEditor", urlPatterns = { "/editarticle/*" })
@MultipartConfig
public class ArticleEditSubmitServlet extends HttpServlet {


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

	public String nameGenerator(int size){
		Random r = new Random();

		String output = "";

		String alphabet = "abcdefghijklmnopqrtsuvwxyz1234567890";
		for (int i = 0; i < size; i++) {
			output = output + alphabet.charAt(r.nextInt(alphabet.length()));
		}

		return output;
	}

	public void submit(int id, String header, Timestamp timestamp, String text,
			int userid, String[] tags, String pilt,boolean delete, Connection con) {
		
		
		try {
			PreparedStatement stmt1 = con
					//.prepareStatement("INSERT INTO artikkel(pealkiri, aeg, sisu, kasutaja_id,pilt) VALUES(?, ?, ?, ?, ?);");
					.prepareStatement("UPDATE artikkel SET pealkiri = ?, aeg = ?,sisu  = ?,kasutaja_id = ?,pilt = ?, on_kustutatud = ? WHERE  artikkel_id = ?;");
			stmt1.setString(1, header);
			stmt1.setTimestamp(2, timestamp);
			stmt1.setString(3, text);
			stmt1.setInt(4, 7);
			stmt1.setString(5, pilt);
			stmt1.setBoolean(6, delete);
			stmt1.setInt(7, id);
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
					.prepareStatement("INSERT INTO artikkel_tag(artikkel_id, tag_id) VALUES (?, ?) NOT EXISTS ( SELECT * FROM artikkel_tag WHERE artikkel_id = ? AND tag_id = ?);");
			stmt4.setString(1, header);
			stmt4.setTimestamp(2, timestamp);
			stmt4.setString(3, header);
			stmt4.setTimestamp(4, timestamp);
			currentArticle = stmt4.executeQuery();
			int currentArticleID = id;
			while(currentArticle.next()){
				currentArticleID = currentArticle.getInt("artikkel_id");
			}
			
			currentArticleID = id;
			
			System.out.println(currentArticleID);
			
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
			try {
				con.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Connection con = connect();
		AuthorityHandler auth = new AuthorityHandler();
		
		String uri = req.getRequestURI();
		String[] uriSplit = uri.split("/");
		int id = Integer.parseInt(uriSplit[2]);
		
		boolean delete = false;
		
		if(req.getParameter("delete") != null){
			delete = true;
		}

		boolean isauth = false;
		boolean isedit = false;
		int id_here = -1;
		try {
			isauth = auth.isLegit(req);
			isedit = auth.isEditor();
			id_here = auth.getUserId(req);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if(isauth && isedit){
			
			String head = req.getParameter("header");
			String text = req.getParameter("text-input");
			String tag = req.getParameter("tags-input");
			String[] tags = tag.split(",");
			// SimpleDateFormat formaat = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date today = new Date();
			Timestamp timestamp = new Timestamp(today.getTime());
			int userid = 0;
			if(id_here != 0 )userid = id_here;

			connect();

			Part filePart = req.getPart("userfile1");

			System.out.println(req.getPart("userfile1").getContentType().split("/")[1]);

			InputStream filecontent = null;
			OutputStream out = null;


			filecontent = filePart.getInputStream();

			BufferedImage imBuff = ImageIO.read(filecontent);
			
			String pilt = "";
			
			if(imBuff != null){
				BufferedImage thumbnail = Scalr.resize(imBuff, 500);
				System.out.println(req.getPart("userfile1").getContentType().split("/")[1]);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write( thumbnail, req.getPart("userfile1").getContentType().split("/")[1] , baos);
				baos.flush();

				int read = 0;
				final byte[] bytes = baos.toByteArray();

				filecontent.close();
				String name =  nameGenerator(20) +"." +req.getPart("userfile1").getContentType().split("/")[1];
				try{
					PreparedStatement ps = con.prepareStatement("INSERT INTO pilt(nimi,fail) VALUES (?,?);");
					ps.setString(1, name);
					ps.setBytes(2, bytes);
					ps.execute();
					ps.close();

					pilt = "/image/"+ name;

					submit(id, head, timestamp, text, userid, tags,pilt,delete, con);
					
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
				resp.sendRedirect("/");
			}


			

			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
		else{
			resp.sendRedirect("/");
		}

	}
}