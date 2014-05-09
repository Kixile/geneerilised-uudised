package com.geneeriliseduudised.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import com.geneeriliseduudised.commentdata.Comment;
import com.geneeriliseduudised.commentdata.ArticleComments;
import com.geneeriliseduudised.servlets.Article;
import com.geneeriliseduudised.servlets.ArticleWriteServlet;
import com.geneeriliseduudised.datastore.MemoryCommentData;


public class UnitTests {
	
	private Article a1;
	private Comment c1;
	private Comment c2;
	private Comment c3;
	private ArticleComments ac1;
	private ArticleWriteServlet aws1;
	private MemoryCommentData mcd1;
	private List<Comment> model1 = new ArrayList<Comment>();
	
    @Before
    public void init() {
    	c1 = new Comment(1,1, "Comm1", "Test1");
    	c2 = new Comment(2,1, "Comm2", "Test2");
    	c3 = new Comment(3,1, "Comm3", "Test3");
    	mcd1.addComment(c1);
    	mcd1.addComment(c2);
    	mcd1.addComment(c3);
    	model1.add(c1);
    	model1.add(c2);
    	model1.add(c3);
    	ac1 = new ArticleComments(1, "Title", "Desc", model1);
    }
    
	@Test
	public void findByID(){
		Assert.assertEquals(c2,mcd1.findItemById(2));
	}
    
	@Test
    public void testPostComment() {
    	Assert.assertEquals(c1,mcd1.findItemById(1));
    }
	
}
