package com.geneeriliseduudised.commentdata;

public class Comment {

	public Integer id;
	public Integer itemId;
	public String bidder;
	public String amount;
	public String articleURL;

	public Comment() {
	}

	public Comment(Integer id, Integer itemId, String bidder, String amount, String articleURL) {
		this.id = id;
		this.itemId = itemId;
		this.bidder = bidder;
		this.amount = amount;
		this.articleURL = articleURL;
	}

	public String getArticleURL() {
		return articleURL;
	}

	public void setArticleURL(String articleURL) {
		this.articleURL = articleURL;
	}
	
	
	

}
