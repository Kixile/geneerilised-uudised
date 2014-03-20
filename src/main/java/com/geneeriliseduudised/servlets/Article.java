package com.geneeriliseduudised.servlets;

public class Article {
	private String header;
	private String text;
	private String[] tags;
	
	public Article(String header, String text, String tags) {
		super();
		this.header = header;
		this.text = text;
		this.tags = tags.replaceAll(" ", "").split(",");
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
	
	
}