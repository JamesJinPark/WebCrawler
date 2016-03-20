package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateUser extends HttpServlet{
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer htmlBuffer = new StringBuffer();
		
		htmlBuffer.append("<html><head>"); 
		htmlBuffer.append("<title>Create New User</title>"); 
		htmlBuffer.append("</head><body>"); 

		htmlBuffer.append("<h2>Create New User</h2>");
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

		
		htmlBuffer.append("</body></html>"); 
		
		
		out.println(htmlBuffer.toString());
		out.flush();
		out.close();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer htmlBuffer = new StringBuffer();
		
		
		out.println(htmlBuffer.toString());
		out.flush();
		out.close();		
	}

}
