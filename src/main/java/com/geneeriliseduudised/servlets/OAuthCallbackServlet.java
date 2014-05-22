package com.geneeriliseduudised.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.geneeriliseduudised.commentdata.Comment;
import com.google.common.collect.ImmutableMap;

@WebServlet(name = "OAuthCallbackServlet", urlPatterns = { "/oauth2callback" })
public class OAuthCallbackServlet extends HttpServlet {

	Connection con = null;

	/**
	 * Handles the token request callback from Google, inited by
	 * OAuthSigninServlet, and requests data using the token.
	 * 
	 */
	private static final long serialVersionUID = 1055758581569443293L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// if error validating
		if (req.getParameter("error") != null) {
			resp.getWriter().println(
					req.getParameter("Error in callback validation"));
			return;
		}
		// code to turn in for access token
		String code = req.getParameter("code");

		// post access request, save answer to string
		String body = post(
				"https://accounts.google.com/o/oauth2/token",
				ImmutableMap
				.<String, String> builder()
				.put("code", code)
				.put("client_id", OAuthSigninServlet.OAUTH_CLIENT_ID)
				.put("client_secret",
						OAuthSigninServlet.OAUTH_CLIENT_SECRET)
						.put("redirect_uri",
								OAuthSigninServlet.OAUTH_REDIRECT_URI)
								.put("grant_type", "authorization_code").build());

		JSONObject jsonObject = null;

		// get json from string
		jsonObject = new JSONObject(body);

		// retrieve token
		String accessToken = (String) jsonObject.get("access_token");
		// store token in session for further use
		req.getSession().setAttribute("access_token", accessToken);
		// use token to get user info from Google
		String userDataString = get(new StringBuilder(
				"https://www.googleapis.com/oauth2/v2/userinfo?access_token=")
		.append(accessToken).toString());

		JSONObject userDataJsonObject = new JSONObject(userDataString);
		String userId = userDataJsonObject.getString("email");

		// get session id, save session id to cookie
		String state = null;
		try {
			state = req.getSession().getAttribute("state").toString();
		} finally {
		}
		Cookie sessionIdCookie = new Cookie("SESSIONID", state);
		resp.addCookie(sessionIdCookie);

		// print userinfo to browser
		resp.getWriter().println("userid:" + userId + "\nsessionid:" + state);

	}

	public String get(String url) throws ClientProtocolException, IOException {
		return execute(new HttpGet(url));
	}

	// makes a POST request to url with form parameters and returns body as a
	// string
	public String post(String url, Map<String, String> formParameters)
			throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : formParameters.keySet()) {
			nvps.add(new BasicNameValuePair(key, formParameters.get(key)));
		}

		request.setEntity(new UrlEncodedFormEntity(nvps));

		return execute(request);
	}

	// makes request and checks response code for 200
	private String execute(HttpRequestBase request)
			throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);

		HttpEntity entity = response.getEntity();
		String body = EntityUtils.toString(entity);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Expected 200 but got "
					+ response.getStatusLine().getStatusCode() + ", with body "
					+ body);
		}

		return body;
	}

	public void connect() {
		try {
			con = DriverManager
					.getConnection("jdbc:postgresql://ec2-54-246-101-204.eu-west-1.compute.amazonaws.com:5432/dc09hcdktafoks?user=ahheansgceypsj&password=gVrdggxl82Anv12TSTDqxlrDaG&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendSession(String email, String id) {
		connect();
		
		String sql = "SELECT kasutaja_id FROM kasutaja WHERE email = '"+ email +"';";
		Statement stmt = null;
		ResultSet rs = null;
		int kas_id = 0;
		
		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e3) {

		}
		
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				kas_id = rs.getInt("kasutaja_id");
			}
			rs.close();
			stmt.close();
			} catch (SQLException e3) {
				try {
					rs.close();
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}
		
		
		try {
			PreparedStatement stmt1 = con
					.prepareStatement("INSERT INTO sessioonid(kasutaja_id, 	sessioon_id) VALUES(?, ?);");
			stmt1.setInt(1, kas_id);
			stmt1.setString(2, id);
			stmt1.executeUpdate();
			stmt1.close();
			}
		catch (SQLException e) {
			try {
				con.close();
			} catch (SQLException e1) {
			}
		e.printStackTrace();
	}
		
	}
}
