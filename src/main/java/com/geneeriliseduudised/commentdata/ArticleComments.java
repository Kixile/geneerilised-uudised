package com.geneeriliseduudised.commentdata;

import java.util.List;

public class ArticleComments {

	public Integer id;
	public String title;
	public String description;
	public List<Comment> bids;

	public ArticleComments() {
	}

	public ArticleComments(Integer id, String title, String description,
			List<Comment> bids) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.bids = bids;
	}

}
