package com.geneeriliseduudised.datastore;

import com.geneeriliseduudised.commentdata.ArticleComments;
import com.geneeriliseduudised.commentdata.Comment;
import com.geneeriliseduudised.servlets.Article;
import com.geneeriliseduudised.servlets.AuthorityHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class MemoryCommentData implements CommentDataProvider {

	public Map<Integer, ArticleComments> items;
	private int commentCounter = 0;
	Connection con = null;
	
	public MemoryCommentData(int id) {
		commentCounter = 0;
		List<Comment> firstArticleComment = new ArrayList<Comment>();
		
		init();
		
		String sql = "SELECT * FROM kommentaar where artikkel_id =" + id;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e3) {

		}
		
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		
		try {
			while (rs.next()) {
				firstArticleComment.add(new Comment(1,1, rs.getString("kasutaja_id"),rs.getString("sisu") , "","",""));
				commentCounter ++;
			}
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		items = new HashMap<Integer, ArticleComments>();
		items.put(1, new ArticleComments(1, "Click to load comments.", "",firstArticleComment));
	}


	public ArticleComments findItemById(int id) {
		return items.get(id);
	}

	public List<ArticleComments> findAllItems() {
		return new ArrayList<ArticleComments>(items.values());
	}

	public void addComment(Comment comment, HttpServletRequest req) {
		items.get(comment.itemId).bids.add(comment);
		init();
		
		AuthorityHandler auth = new AuthorityHandler();

		int userID = auth.getUserId(req);
		
		try{
		String string = comment.getArticleURL();
		String[] parts = string.split("/");
		
		Date today = new Date();
		Timestamp timestamp = new Timestamp(today.getTime());
		
		PreparedStatement stmt1 = con.prepareStatement("INSERT INTO kommentaar(kasutaja_id, artikkel_id, aeg, sisu) VALUES(?, ?, ?, ?);");
		stmt1.setInt(1, 1);
		stmt1.setInt(2, Integer.parseInt(parts[4]));
		stmt1.setTimestamp(3, timestamp);
		stmt1.setString(4, comment.amount);
		stmt1.execute();
		stmt1.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		comment.id = ++commentCounter; // concurrency bug

	}
	
	public void init() {
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}

	
}


