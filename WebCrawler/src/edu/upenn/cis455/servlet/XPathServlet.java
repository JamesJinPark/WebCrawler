package edu.upenn.cis455.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

import org.w3c.dom.Document;

import edu.upenn.cis455.crawler.Channel;
import edu.upenn.cis455.crawler.DatabaseDAO;
import edu.upenn.cis455.crawler.User;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

/**
 * Servlent for XPath Engine
 * @author James Park
 *
 */
@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		StringBuffer htmlForm = new StringBuffer();
		
		htmlForm.append("<html>");
		htmlForm.append("<h1>CIS 555 XPathServlet</h1>");
		htmlForm.append("<p>Full Name: James Jin Park</p>");
		htmlForm.append("<p>SEAS Login Name: jamespj</p>");
		htmlForm.append("<h2>Log In</h2>");
		htmlForm.append("<form method=\"POST\" action=\"/servlet/xpath\">");
		htmlForm.append("Username ");
		htmlForm.append("<input type=\"text\" name=\"UserName\" ><br>");
		htmlForm.append("Password ");
		htmlForm.append("<input type=\"text\" name=\"Password\" ><br>");
		htmlForm.append("<input type=\"submit\" value=\"Submit\"/>");
		htmlForm.append("</form>");

		htmlForm.append("<h2>Create New Account</h2>");
		htmlForm.append("<form method=\"POST\" action=\"/servlet/xpath\">");
		htmlForm.append("New Username ");
		htmlForm.append("<input type=\"text\" name=\"NewUserName\"><br>");
		htmlForm.append("New Password ");
		htmlForm.append("<input type=\"text\" name=\"NewPassword\"><br>");
		htmlForm.append("<input type=\"submit\" value=\"Submit\"/>");
		htmlForm.append("</form>");
			
		out.println(htmlForm.toString());
		out.flush();
		out.close();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		File databaseDir = new File(getServletContext().getInitParameter("BDBstore"));
		databaseDir.mkdir();
		DatabaseDAO.setup(databaseDir);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer htmlBuffer = new StringBuffer();

		String loginUsername = request.getParameter("UserName");
		String loginPassword = request.getParameter("Password");

		String newAccount = request.getParameter("NewUserName");
		String newAccountPassword = request.getParameter("NewPassword");
		
		int choice = 0; //default 
		if(loginUsername != null && loginPassword != null){
			choice = 1; //user entered login in info
		} 
		if(newAccount != null && newAccountPassword != null){
			choice = 2; //user tried to create new account
		}

		
		switch(choice){				
		case 1:			
			boolean userExists = DatabaseDAO.userExists(loginUsername);
			userExists = true;

			if(userExists){
				if(loginPassword.equals(DatabaseDAO.getUser(loginUsername).getPassword())){
					request.getSession().setAttribute("Username", loginUsername);
					htmlBuffer.append("<html><head>"); 
					htmlBuffer.append("<meta http-equiv=\"refresh\" "); 		
					htmlBuffer.append("content=\"0;URL=\'"); 
					htmlBuffer.append("/servlet/UserPortal\'\"" + "></meta>"); //redirect url 
					htmlBuffer.append("</head><body>");
					htmlBuffer.append("</body></html>"); 

				} else{
					
					htmlBuffer.append("<html>"); 
					htmlBuffer.append("<h1>Wrong password!</h1></br>");
					htmlBuffer.append("<body>User with the name \"" + loginUsername + "\" has different " +
							"password than " + loginPassword + "!</br>");
					htmlBuffer.append("Please re-enter your login information or create new user first.");
					htmlBuffer.append("</body></br></br>");	
					htmlBuffer.append("<button onclick=\"location.href='/servlet/xpath'\">Back</button>");
					htmlBuffer.append("</body></html>"); 
					
				}
			} else { //user does not exist
				
				htmlBuffer.append("<html>"); 
				htmlBuffer.append("<h1>User does not exist!</h1></br>");
				htmlBuffer.append("<body>User with the name \"" + loginUsername + "\" does not exist!</br>");
				htmlBuffer.append("Please re-enter your login information or create new user first.");
				htmlBuffer.append("</body></br></br>");	
				htmlBuffer.append("<button onclick=\"location.href='/servlet/xpath'\">Back</button>");
				htmlBuffer.append("</body></html>"); 

			}
			
			break;
			
		case 2:
			boolean userAlreadyExists = DatabaseDAO.userExists(newAccount);
			if(!userAlreadyExists){
				User user = new User(newAccount, newAccountPassword);
				DatabaseDAO.registerUser(user);
				
				htmlBuffer.append("<html><head>"); 
				htmlBuffer.append("<meta http-equiv=\"refresh\" "); 		
				htmlBuffer.append("content=\"5;URL=\'"); 
				htmlBuffer.append("/servlet/xpath\'\"" + "></meta>"); //redirect url 
				htmlBuffer.append("</head><body>");
				htmlBuffer.append("<p>New user created!" + "</br>");
				htmlBuffer.append("New username: " + newAccount + "</br>"); 											
				htmlBuffer.append("New password: " + newAccountPassword + "</br>"); 										
				htmlBuffer.append("You'll be redirected to the log in page in 5 seconds.</p>"); 											
				htmlBuffer.append("</body></html>"); 

			} else {
				
				htmlBuffer.append("<html>"); 
				htmlBuffer.append("<h1>User already exists</h1></br>");
				htmlBuffer.append("<body>User with the name \"" + newAccount + "\" already exists.</br>");
				htmlBuffer.append("Please choose another username.");
				htmlBuffer.append("</body></br></br>");	
				htmlBuffer.append("<button onclick=\"location.href='/servlet/xpath'\">Back</button>");
				htmlBuffer.append("</body></html>"); 
			}
			
			break;
			
		case 0: //directly go to default
			
		default: //shouldn't get here! 
			response.setStatus(500);
			htmlBuffer.append("<!DOCTYPE HTML PUBLIC>"); 
			htmlBuffer.append("<html><head>"); 
			htmlBuffer.append("<title>500 Internal Error</title>"); 
			htmlBuffer.append("</head><body>"); 
			htmlBuffer.append("<h1>500 Internal Server Error</h1>"); 
			htmlBuffer.append("Error with URL input."); 
			htmlBuffer.append("</body></html>"); 
			try { //hack-y solution to prevent system exiting too fast 
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}	
		out.println(htmlBuffer.toString());
		out.flush();
		out.close();
		DatabaseDAO.shutdown();
	}
}



//if(url == null || url == ""){
//response.setStatus(404);
//htmlBuffer.append("<!DOCTYPE HTML PUBLIC>"); 
//htmlBuffer.append("<html><head>"); 
//htmlBuffer.append("<title>404 Not Found</title>"); 
//htmlBuffer.append("</head><body>"); 
//htmlBuffer.append("<h1>404 URL Not Found</h1>"); 
//htmlBuffer.append("Error with URL input."); 
//htmlBuffer.append("</body></html>"); 
//try { //hack-y solution to prevent system exiting too fast 
//	Thread.sleep(1000);
//} catch (InterruptedException e) {
//	e.printStackTrace();
//}
//validInputs = false;
//} else if (XPath == null || XPath == ""){
//response.setStatus(400);
//htmlBuffer.append("<!DOCTYPE HTML PUBLIC>"); 
//htmlBuffer.append("<html><head>"); 
//htmlBuffer.append("<title>400 Malformed Request</title>");
//htmlBuffer.append("</head><body>"); 
//htmlBuffer.append("<h1>400 Malformed Request</h1>");
//htmlBuffer.append("Error with XPath input."); 
//htmlBuffer.append("</body></html>"); 
//try { //hack-y solution to prevent system exiting too fast 
//	Thread.sleep(1000); 
//} catch (InterruptedException e) {
//	e.printStackTrace();
//}
//validInputs = false;
//}

