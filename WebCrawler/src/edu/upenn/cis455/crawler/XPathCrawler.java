package edu.upenn.cis455.crawler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import edu.upenn.cis455.servlet.HttpClient;

public class XPathCrawler {
	int maxSize;
	int numFiles;
	String dbLocation;
	
	public XPathCrawler(){}

	public static void main(String args[]) {
		if(args.length != 3 && args.length != 4){
			System.out.println("Name: James Park");
			System.out.println("SEAS login: jamespj");
		} else {

			XPathCrawler crawler = new XPathCrawler();
		
			Queue<String> serverQueue = new LinkedList<String>(); //one queue for servers
			
			String startingURL = args[0]; // UIRL of the web page to start crawling
			crawler.dbLocation = args[1]; //directory containing the Berkeley DB database environment that holds store
			crawler.maxSize = Integer.valueOf(args[2]); //max size of file to retrieve in MB
			crawler.numFiles = -1;
			if(args.length > 3){ //optional argument of number of files to retrieve
				crawler.numFiles = Integer.valueOf(args[3]);
			}
			
			File databaseDir = new File(crawler.dbLocation); //create file 			
			boolean success = databaseDir.mkdir();
			if(success){
				System.out.println("Database directory at " + crawler.dbLocation + " created");
			}
			DatabaseDAO.setup(databaseDir);

			try {
				crawl(startingURL, serverQueue, crawler.maxSize, crawler.numFiles);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			DatabaseDAO.shutdown();
		}		
	}
	
	public static void crawl(String URL, Queue<String> serverQueue, int maxSize, int maxNumFiles) throws IOException, ParseException{
		int count = maxNumFiles;
		//create hash table of <host, URL>
		HashMap<String, Queue<String>> hostMapper = new HashMap<>();
		
		//create client and parse URL
		HttpClient client = new HttpClient(URL); 
		client.parseURL(URL);
		
		//create URL queue for the host/server
		Queue<String> URLQueue = new LinkedList<String>(); //one queue per host
		URLQueue.add(URL);
		String URLhost = client.getHost();
		hostMapper.put(URLhost, URLQueue);
		
		// initialize queue with host 
		serverQueue.add(URLhost); //String should be host name
		
		while(!serverQueue.isEmpty() && count != 0){  //while queue is not empty
			String currentHost = serverQueue.poll();
			Queue<String> currentQueue = hostMapper.get(currentHost);
			String url = currentQueue.poll();
			String headResponse;
			
			//do HEAD request
			Map<String, String> headers= client.makeHeadRequest(url); //opens connection to server that hosts HTML or XML doc
			String contentType = headers.get("Content-Type");
			boolean isXML = false;
			boolean isHTML = false;
			if(contentType.substring(contentType.length() - 3, contentType.length()).equals("xml")){
				isXML = true;
				System.out.println("Found XML!"); //REMOVE
			}
			if(contentType.equals("text/html")){
				isHTML = true;
				System.out.println("Found HTML!"); //REMOVE!
			}

			
			//download robots.txt
			//parse robots.txt
			
			boolean isModified = client.isModified(headers.get("Last-Modified"), new Date());

			if(DatabaseDAO.pageExists(url) && !isModified){
					System.out.println(url + ": Not modified"); // page is not downloaded because not changed
			}	
			String urlResponse = null;
			if(!DatabaseDAO.pageExists(url) || isModified){
				//do GET request
				System.out.println(url + ": Downloading"); // download corresponding page
				urlResponse = client.downloadDocFromUrl(url); //opens connection to server that hosts HTML or XML doc
				System.out.println(urlResponse); // download corresponding page
				
				if((urlResponse.getBytes().length * 1048576) < maxSize){ //check to make sure that URL content is not too big
					Webpage newPage = new Webpage(url, urlResponse, new Date());
					DatabaseDAO.addPage(newPage);
				}
			}
			
			//Extract links from HTML page
			if(isHTML && urlResponse != null){
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				XPathExpression xPathExpression;
				ArrayList<String> xPathExpressions = null;
				try {
					xPathExpression = xPath.compile("//a/@href");
					xPathExpressions = client.getXPathMatches(xPathExpression, urlResponse);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}
				for(String expressions : xPathExpressions){
					System.out.println(expressions);				// extract URLs

				}
				
				// Append to queue the URLs extracted
			}
			count -= 1;
		}
	}	
}