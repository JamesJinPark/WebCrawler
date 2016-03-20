package edu.upenn.cis455.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.crawler.Channel;
import edu.upenn.cis455.crawler.DatabaseDAO;

public class DisplayChannels extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer htmlBuffer = new StringBuffer();

		File databaseDir = new File(getServletContext().getInitParameter("BDBstore"));
		databaseDir.mkdir();
		DatabaseDAO.setup(databaseDir);
		ArrayList<Channel> allChannels = DatabaseDAO.getAllChannels();
	
		htmlBuffer.append("<html>");
		htmlBuffer.append("<h1>Displaying Channels</h1>");

		if(!allChannels.isEmpty()){
			htmlBuffer.append("<h2>All Channels</h2><br>");
			for(Channel channel : allChannels ){
				htmlBuffer.append(channel.toString());
			}
		} 

		htmlBuffer.append("</html>"); 
		
		
		out.println(htmlBuffer.toString());
		out.flush();
		out.close();
	}
	
	//when you delete channel
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer htmlBuffer = new StringBuffer();
		
		
		out.println(htmlBuffer.toString());
		out.flush();
		out.close();		
	}

}
