package com.geneeriliseduudised.commentdata;

public class Comment {

	public Integer id;
	public Integer itemId;
	public String bidder;
	public String amount;
	public String articleURL;
	public String captchaPart1;
	public String captchaPart2;

	public Comment() {
	}

	public Comment(Integer id, Integer itemId, String bidder, String amount, String articleURL, String captchaPart1, String captchaPart2) {
		this.id = id;
		this.itemId = itemId;
		this.bidder = bidder;
		this.amount = amount;
		this.articleURL = articleURL;
		this.captchaPart1 = captchaPart1;
		this.captchaPart2 = captchaPart2;
	}

	public String getArticleURL() {
		return articleURL;
	}

	public void setArticleURL(String articleURL) {
		this.articleURL = articleURL;
	}
	
	
	

}
