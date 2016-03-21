package edu.upenn.cis455.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import com.sun.org.apache.xpath.internal.NodeSet;

/**
 * Client for XPath servlet
 * Called by the servlet's doPost method
 * @author James Park
 *
 */
public class HttpClient {
	String url;
	int port;
	String host;
	String path; 
	String response;
	Map<String, String> headers;
		
	/**
	 * Constructor for HttpClient
	 * @param url
	 */
	public HttpClient(String url){
		this.url = url;
		this.port = 80;
		this.host = "localhost:80";
		this.headers = new HashMap<String, String>();
	}
		
	public void setURL(String URL){
		this.url = URL;
	}

	public String getURL(){
		return this.url;
	}
	
	public String getHost(){
		return this.host;
	}
	
	/**
	 * Grabs the IP address of the URL
	 * @param url
	 * @return
	 * @throws UnknownHostException
	 * @throws MalformedURLException
	 */
	public String convertUrlToIP(String url) throws UnknownHostException, MalformedURLException{
		InetAddress address = InetAddress.getByName(new URL(url).getHost());
		return address.getHostAddress();
	}
	
	/**
	 * Parses url to get the host, IP, and request path
	 * @param url
	 */
	public void parseURL(String url){
    	url = url.replace("http://", ""); //replace the http:// in the url if it has any
    	String[] urlParts = url.split(":");
    	
    	if(urlParts.length > 1){ //if port number was specified in URL
	    	//host
	    	this.host = urlParts[0];

	    	//path
	    	String[] urlParts2 = urlParts[1].split("/", 2);
	    	this.port = Integer.valueOf(urlParts2[0]);
	    	this.path = "/" + urlParts2[1];    
    	} else { //if no port number is specified
	    	String[] urlParts2 = urlParts[0].split("/", 2);
	    	
	    	//host
	    	this.host = urlParts2[0];
	    	
	    	//path
	    	this.path = "/" + urlParts2[1];    
    	}
	}
	
	public boolean isModified(String modifiedTime, Date timestamp){
		DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		Date modifiedDate = null;
		try {
			modifiedDate = formatter.parse(modifiedTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//timestamp = formatter.parse("Thu, 14 Mar 2016 18:36:04 GMT");
		//System.out.println(timestamp);
		return modifiedDate.after(timestamp);
	}
	
	
	/**
	 * Makes HEAD request to the URL 
	 * @param String URL 
	 * @return Map headers
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Map<String, String> makeHeadRequest(String url) throws IOException, ParseException{
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String ipAddress = null;
		this.path = null;
		
		//parse url
		parseURL(url);
		
		//convert url to string and open connection
		String robustURL = "http://" + this.host + this.path; 
		try{
			ipAddress = convertUrlToIP(robustURL);
			socket = new Socket(ipAddress, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(Exception e){
			System.err.println("Couldn't connect to: " + robustURL);
			return null;
		}
		
		System.out.println("Client connected to host : " + ipAddress + " port: " + port);
        
		//send GET request
		out.println("GET " + robustURL + " HTTP/1.1");
		out.println("Host: " + this.host);
		out.println("Connection: keep-alive");
		out.println("User-Agent: cis455crawler");
		out.println("\r\n");
		out.println("\r\n");
		
		//read response
		StringBuffer headerBuffer = new StringBuffer();
		String line = null;

		//get headers
		line = in.readLine(); //e.g.: HTTP/1.1 200 OK 
		headerBuffer.append(line);		
		headerBuffer.append("\r\n");
		while(true) {
			line = in.readLine();
			if (line == null || line.isEmpty()) break;
			headerBuffer.append(line);
			headerBuffer.append("\r\n");
			String key = null;
			String value = null;
			try{
				int separator = line.indexOf(":");
				key = line.substring(0, separator).trim();
				value = line.substring(separator + 1).trim();
				headers.put(key, value);
			}catch(Exception e){
				System.err.println("Could not parse header: " + line + " " + e);
			}
		}
		socket.close();
		return this.headers;		
	}
	
	/**
	 * Connects to outside server and downloads requested XML page
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String downloadDocFromUrl(String url) throws IOException{
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String ipAddress = null;
		String response = null;
		this.path = null;
		
		//parse url
		parseURL(url);
		
		//convert url to string and open connection
		String robustURL = "http://" + this.host + this.path; 
		try{
			ipAddress = convertUrlToIP(robustURL);
			socket = new Socket(ipAddress, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(Exception e){
			System.err.println("Couldn't connect to: " + robustURL);
			return "-1";
		}
		
		System.out.println("Client connected to host : " + ipAddress + " port: " + port);
        
		//send GET request
		out.println("GET " + robustURL + " HTTP/1.1");
		out.println("Host: " + this.host);
		out.println("Connection: keep-alive");
		out.println("User-Agent: cis455crawler");
		out.println("\r\n");
		out.println("\r\n");
		
		//read response
		StringBuffer headerBuffer = new StringBuffer();
		StringBuffer responseBuffer = new StringBuffer();
		String line = null;

		//get headers
		line = in.readLine(); //e.g.: HTTP/1.1 200 OK 
		headerBuffer.append(line);		
		headerBuffer.append("\r\n");
		while(true) {
			line = in.readLine();
			if (line == null || line.isEmpty()) break;
			headerBuffer.append(line);
			headerBuffer.append("\r\n");
			String key = null;
			String value = null;
			try{
				int separator = line.indexOf(":");
				key = line.substring(0, separator).trim();
				value = line.substring(separator + 1).trim();
				headers.put(key, value);
			}catch(Exception e){
				System.err.println("Could not parse header: " + line + " " + e);
			}
		}
		String length = headers.get("Content-Length");
		
		//get actual response
		int count = 0;
		char[] buf = new char[Integer.valueOf(length)];
		if(Integer.valueOf(length) > 0 ){
			count = in.read(buf, 0, Integer.valueOf(length));
			responseBuffer.append(String.valueOf(buf).trim());
//			responseBuffer.append("\r\n");
		}
		while(count != Integer.valueOf(length)){
//			while(true) {
//				line = in.readLine();
//				if (line == null || line.isEmpty()) break;
//				responseBuffer.append(line);
//				responseBuffer.append("\r\n");
//			}
			char[] newbuf = new char[Integer.valueOf(length) - count];
			int newcount = in.read(newbuf, 0, (Integer.valueOf(length) - count));
			responseBuffer.append(String.valueOf(newbuf).trim());
//			responseBuffer.append("\r\n");
			count = count + newcount;
			responseBuffer.trimToSize();
		}
		if(responseBuffer.length() != 0){
			response = responseBuffer.toString();			
		}else {
			response = "-2";
		}
		this.response = response;
		
		//clean up
		out.close();
		in.close();
		socket.close();
		return response;
	}
	
	/**
	 * Uses JTidy to convert XML string into a Document DOM
	 * @param xmlDoc
	 * @return
	 */
	public Document convertDom(String xmlDoc){
		Tidy tidy = new Tidy();
		tidy.setXmlTags(true);
		InputStream xmlStream = new ByteArrayInputStream(xmlDoc.getBytes(StandardCharsets.UTF_8));
		return tidy.parseDOM(xmlStream, null);
	}
	
	public Document convertHTMLDom(String htmlDoc){
		Tidy tidy = new Tidy();
		InputStream xmlStream = new ByteArrayInputStream(htmlDoc.getBytes(StandardCharsets.UTF_8));
		return tidy.parseDOM(xmlStream, null);		
	}
	
	public ArrayList<String> getXPathMatches(XPathExpression xPathExpression, String htmlPage) throws XPathExpressionException{
		//for webparser, this will get links from the XPath //link/*
		//should eventually return ArrayList<String> 
		ArrayList<String> xPathExpressions = new ArrayList<String>();
		Document DOMdoc = convertHTMLDom(htmlPage);
		DTMNodeList nodeSet = (DTMNodeList) xPathExpression.evaluate(DOMdoc, XPathConstants.NODESET);
		for(int i = 0; i< nodeSet.getLength(); i++){
			xPathExpressions.add(nodeSet.item(i).getNodeValue());
		}
		return xPathExpressions;
	}
}
