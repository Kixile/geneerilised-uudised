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

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

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
		datastore = new MemoryCommentData(0);
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
		
		try{
		datastore = new MemoryCommentData(Integer.parseInt(parts[4]));
		}
		catch (Exception e){
			datastore = new MemoryCommentData(0);
		}
		
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
			Comment comment = gson.fromJson(req.getParameter("json_1"), Comment.class);


			String string = comment.getArticleURL();
			String[] parts = string.split("/");
			System.out.println(req.getParameter("json_3"));
			String a = req.getParameter("json_2").split(":")[1].replaceAll("\"","");
			String b = req.getParameter("json_3").split(":")[1].replaceAll("\"","");
			System.out.println(a+ "   "+ b);

			try{
				ReCaptcha captcha = ReCaptchaFactory.newReCaptcha("6Le37vMSAAAAAKoR2M1Bbg2RTCb-0X5rBdRaHHsk", "6Le37vMSAAAAAKP0O50B7ReJnVZX9v0QwqhQ2pNp", false);
				ReCaptchaResponse response = captcha.checkAnswer(req.getRemoteAddr(), b, a);

				if (response.isValid()) {
					datastore.addComment(comment,req);
				}
				else{
					System.out.println("FuckYou");
				}
			}
			catch(Exception e){

			}


			// echo the same object back for convenience and debugging
			// also it now contains the generated id of the bid
			String commentEcho = gson.toJson(comment);
			resp.setHeader("Content-Type", "application/json");
			resp.getWriter().write(commentEcho);



			// actually this is a bad place to send the broadcast.
			// better: attach sockets as eventlisteners to the datastore
			// even better: use message queues for servlet-datastore events
			CommentSocketController.find(req.getServletContext()).broadcast(commentEcho);
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
