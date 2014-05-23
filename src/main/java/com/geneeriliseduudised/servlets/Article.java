package com.geneeriliseduudised.servlets;

public class Article {
	private int id;
	private String header;
	private String text;
	private String[] tags;
	private String date;
	private String username;
	private String summary;
	private String image;
	private int writerId;

	public Article(String header, String text, String tags, String date,
			String username, String summary, int id, String image) {
		super();
		this.id = id;
		this.header = header;
		this.text = text;
		this.tags = tags.split(",");
		this.date = date;
		this.username = username;
		this.summary = summary;
		this.image = image;
	}

	
	
	public int getWriterId() {
		return writerId;
	}



	public void setWriterId(int writerId) {
		this.writerId = writerId;
	}



	public Article() {
		// TODO Auto-generated constructor stub
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}