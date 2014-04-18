package com.geneeriliseduudised.datastore;

import com.geneeriliseduudised.commentdata.Comment;
import com.geneeriliseduudised.commentdata.ArticleComments;

import java.util.List;

public interface CommentDataProvider {

	public ArticleComments findItemById(int id);

	public List<ArticleComments> findAllItems();

	public void addComment(Comment bid);
}
