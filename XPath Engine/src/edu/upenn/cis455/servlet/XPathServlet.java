package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;

import org.w3c.dom.Document;

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
		htmlForm.append("<h2>Enter XPaths</h2>");
		htmlForm.append("Separate multiple XPaths using two colons with a space before and after the colons.<br>");
		htmlForm.append("(e.g.: /example/foo :: /moreExample/bar :: /evenMore[@foo=\"bar\"])<br><br>");
		htmlForm.append("<form method=\"POST\" action=\"/servlet/xpath\">");
		htmlForm.append("<input type=\"text\" name=\"XPath\" id=\"XPath\">");
		htmlForm.append("<h2>Enter URL for XML document</h2>");
		htmlForm.append("<input type=\"text\" name=\"URL\" id=\"URL\"><br><br>");
		htmlForm.append("<input type=\"submit\" value=\"Submit Form\"/>");
		htmlForm.append("</form>");
		htmlForm.append("</html>");
		
		out.println(htmlForm.toString());
		out.flush();
		out.close();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer htmlBuffer = new StringBuffer();
		boolean validInputs = true; 
		response.setContentType("text/html");
				
		String url = request.getParameter("URL");
		String XPath = request.getParameter("XPath");
		if(url == null || url == ""){
			response.setStatus(404);
			htmlBuffer.append("<!DOCTYPE HTML PUBLIC>"); 
			htmlBuffer.append("<html><head>"); 
			htmlBuffer.append("<title>404 Not Found</title>"); 
			htmlBuffer.append("</head><body>"); 
			htmlBuffer.append("<h1>404 URL Not Found</h1>"); 
			htmlBuffer.append("Error with URL input."); 
			htmlBuffer.append("</body></html>"); 
			try { //hack-y solution to prevent system exiting too fast 
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			validInputs = false;
		} else if (XPath == null || XPath == ""){
			response.setStatus(400);
			htmlBuffer.append("<!DOCTYPE HTML PUBLIC>"); 
			htmlBuffer.append("<html><head>"); 
			htmlBuffer.append("<title>400 Malformed Request</title>");
			htmlBuffer.append("</head><body>"); 
			htmlBuffer.append("<h1>400 Malformed Request</h1>");
			htmlBuffer.append("Error with XPath input."); 
			htmlBuffer.append("</body></html>"); 
			try { //hack-y solution to prevent system exiting too fast 
				Thread.sleep(1000); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			validInputs = false;
		}
		
		if(validInputs){
			
			//call HttpClient
			HttpClient client = new HttpClient(url); 
			String urlResponse = client.downloadDocFromUrl(url); //opens connection to server that hosts HTML or XML doc

			if(urlResponse.equals("-1")){
				response.setStatus(404);
				htmlBuffer.append("<!DOCTYPE HTML PUBLIC>"); 
				htmlBuffer.append("<html><head>"); 
				htmlBuffer.append("<title>404 Not Found</title>"); 
				htmlBuffer.append("</head><body>"); 
				htmlBuffer.append("<h1>404 URL Not Found</h1>"); 
				htmlBuffer.append("Error with URL input."); 
				htmlBuffer.append("Could not connect to: " + url); 
				htmlBuffer.append("</body></html>"); 
				try { //hack-y solution to prevent system exiting too fast 
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}else if(urlResponse.equals("-2")){
				response.setStatus(502);
				htmlBuffer.append("<!DOCTYPE HTML PUBLIC>"); 
				htmlBuffer.append("<html><head>"); 
				htmlBuffer.append("<title>502 Bad Gateway</title>"); 
				htmlBuffer.append("</head><body>"); 
				htmlBuffer.append("<h1>502 Bad Gateway</h1>"); 
				htmlBuffer.append("No response from upstream server!"); 
				htmlBuffer.append("</body></html>"); 
				try { //hack-y solution to prevent system exiting too fast 
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			} else {			
				//Convert the response to Document DOM
				Document xmlDoc = client.convertDom(urlResponse);
				
				//call XPathEngine
				XPathEngineImpl engine = new XPathEngineImpl();
				
				//multiple xpaths
				String[] xpaths = XPath.split(" :: ");
				engine.setXPaths(xpaths);
				
				//check whether document in URL matches XPath
				boolean[] urlMatches = engine.evaluate(xmlDoc);
				
				htmlBuffer.append("<html><head>"); 
				htmlBuffer.append("<style>table, th, td {border: 1px solid black;"); 
				htmlBuffer.append("border-collapse: collapse;}th, td {padding: 24px;}</style>");
				htmlBuffer.append("<title>XPath Engine</title>"); 
				htmlBuffer.append("</head><body>"); 
				htmlBuffer.append("<h1>XPath Engine</h1>"); 
				htmlBuffer.append("<h2>XPaths that match the XML document</h2>"); 
				htmlBuffer.append("<table border=\"1\" style=\"width:60%\">");
				for(int i = 0; i < xpaths.length; i++){
					htmlBuffer.append("<tr>"); 
					htmlBuffer.append("<td>" + xpaths[i] + "</td>");
					htmlBuffer.append("<td>" + urlMatches[i] + "</td>");
					htmlBuffer.append("</tr>");
				}
				htmlBuffer.append("</table>");
				htmlBuffer.append("</body></html>"); 
			}
		}
		out.println(htmlBuffer.toString());
		out.flush();
		out.close();
	}
}