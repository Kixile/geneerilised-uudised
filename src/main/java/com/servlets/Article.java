package main.java.com.servlets;

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
}