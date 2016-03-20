package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserPortal extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String loginCredentials = (String) request.getSession().getAttribute("Username");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer htmlBuffer = new StringBuffer();

		if(loginCredentials != null){

			htmlBuffer.append("<html><head>"); 
			htmlBuffer.append("<title>User Portal</title>"); 
			htmlBuffer.append("</head><body>"); 
			htmlBuffer.append("<h1>Welcome " + loginCredentials + "!</h1></br>");
			htmlBuffer.append("<h2>Create New Channel</h2>");
			htmlBuffer.append("<form method=\"POST\" action=\"/servlet/xpath\">");
			htmlBuffer.append("<h3>Channel Name</h3>");
			htmlBuffer.append("<input type=\"text\" name=\"ChannelName\"><br>");		
			htmlBuffer.append("<h3>Enter XPath Expressions</h3>");
			htmlBuffer.append("Separate multiple XPaths using two colons with a space before and after the colons.<br>");
			htmlBuffer.append("(e.g.: /example/foo :: /moreExample/bar :: /evenMore[@foo=\"bar\"])<br><br>");
			htmlBuffer.append("<input type=\"text\" name=\"XPath\" ><br>");
			htmlBuffer.append("<h3>URl of XSL Style Sheet</h3>");
			htmlBuffer.append("<input type=\"text\" name=\"URL\" ><br>");
			htmlBuffer.append("<input type=\"submit\" value=\"Submit Form\"/><br>");
			htmlBuffer.append("</form>");

			//log out.  Delete session and go back to log in page
			htmlBuffer.append("<button onclick=\"location.href='/servlet/LogOut'\">Log Out</button>");
			
			htmlBuffer.append("</body></html>"); 

		} else {
			
			htmlBuffer.append("<html>"); 
			htmlBuffer.append("<h1>Please log in first!</h1></br>");
			htmlBuffer.append("Please enter your login information or create new user first.");
			htmlBuffer.append("</body></br></br>");	
			htmlBuffer.append("<button onclick=\"location.href='/servlet/xpath'\">Back</button>");
			htmlBuffer.append("</body></html>"); 
		}
		out.println(htmlBuffer.toString());
		out.flush();
		out.close();

		
	}	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer htmlBuffer = new StringBuffer();
		
		String channelName = request.getParameter("ChannelName");
		String url = request.getParameter("URL");
		String XPath = request.getParameter("XPath");

		htmlBuffer.append("Channel name: " + channelName); 
		htmlBuffer.append("Channel XPath: " + XPath); 
		htmlBuffer.append("Channel URL: " + url); 				

		out.println(htmlBuffer.toString());
		out.flush();
		out.close();
	}
	
}
