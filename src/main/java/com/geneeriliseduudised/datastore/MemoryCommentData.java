package com.geneeriliseduudised.datastore;

import com.geneeriliseduudised.commentdata.ArticleComments;
import com.geneeriliseduudised.commentdata.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryCommentData implements CommentDataProvider {

	public Map<Integer, ArticleComments> items;
	private int commentCounter;

	public MemoryCommentData() {
		List<Comment> firstArticleComment = new ArrayList<Comment>();

		items = new HashMap<Integer, ArticleComments>();
		items.put(1, new ArticleComments(1, "Click to load comments.", "",
				firstArticleComment));
		commentCounter = 2;
	}

	public ArticleComments findItemById(int id) {
		return items.get(id);
	}

	public List<ArticleComments> findAllItems() {
		return new ArrayList<ArticleComments>(items.values());
	}

	public void addComment(Comment comment) {
		items.get(comment.itemId).bids.add(comment);
		comment.id = ++commentCounter; // concurrency bug
	}

}
