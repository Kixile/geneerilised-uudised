package com.geneeriliseduudised.commentdata;

public class Comment {

	public Integer id;
	public Integer itemId;
	public String bidder;
	public String amount;

	public Comment() {
	}

	public Comment(Integer id, Integer itemId, String bidder, String amount) {
		this.id = id;
		this.itemId = itemId;
		this.bidder = bidder;
		this.amount = amount;
	}

}
