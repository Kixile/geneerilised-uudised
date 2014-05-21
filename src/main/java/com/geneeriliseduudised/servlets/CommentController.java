package com.geneeriliseduudised.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.geneeriliseduudised.commentdata.Comment;
import com.geneeriliseduudised.commentdata.ArticleComments;
import com.geneeriliseduudised.datastore.CommentDataProvider;
import com.geneeriliseduudised.datastore.MemoryCommentData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(value = "/comments/*")
public class CommentController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Gson gson;
	private CommentDataProvider datastore;

	@Override
	public void init() throws ServletException {
		super.init();
		gson = new Gson();
		datastore = new MemoryCommentData(1);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Content-Type", "application/json");
		
		String userId = req.getParameter("userId");
		System.out.println(userId);
		
		StringBuffer string = req.getRequestURL();
		String str = string.toString();
		String[] parts = str.split("/");

		datastore = new MemoryCommentData(Integer.parseInt(parts[4]));
		
		String idString = req.getParameter("id");
		
		if (idString != null) {
			replyWithSingleItem(resp, idString);
		} else {
			replyWithAllItems(resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			Comment comment = gson.fromJson(req.getReader(), Comment.class);
			
			
			String string = comment.getArticleURL();
			String[] parts = string.split("/");
			System.out.println(parts[4]);

			
			datastore.addComment(comment); // bid should be validated carefully
			
			System.out.println(comment.getArticleURL());
			
			
			
			// echo the same object back for convenience and debugging
			// also it now contains the generated id of the bid
			String commentEcho = gson.toJson(comment);
			resp.setHeader("Content-Type", "application/json");
			resp.getWriter().write(commentEcho);
			
			

			// actually this is a bad place to send the broadcast.
			// better: attach sockets as eventlisteners to the datastore
			// even better: use message queues for servlet-datastore events
			CommentSocketController.find(req.getServletContext()).broadcast(
					commentEcho);
		} catch (JsonParseException ex) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		}
	}

	private void replyWithAllItems(HttpServletResponse resp) throws IOException {
		List<ArticleComments> allContent = datastore.findAllItems();
		resp.getWriter().write(gson.toJson(allContent));
	}

	private void replyWithSingleItem(HttpServletResponse resp, String idString)
			throws IOException {
		int id = Integer.parseInt(idString);
		ArticleComments item = datastore.findItemById(id);
		resp.getWriter().write(gson.toJson(item));
	}

}
