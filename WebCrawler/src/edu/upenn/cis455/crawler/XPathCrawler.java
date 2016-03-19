package edu.upenn.cis455.crawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

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
		
			Queue<String> queue = new LinkedList<String>();
			
			String startingURL = args[0]; // UIRL of the web page to start crawling
			crawler.dbLocation = args[1]; //directory containing the Berkeley DB database environment that holds store
			crawler.maxSize = Integer.valueOf(args[2]); //max size of file to retrieve in MB
			crawler.numFiles = Integer.valueOf(args[3]); //optional argument of number of files to retrieve
			
			File databaseDir = new File(crawler.dbLocation); //create file 			
			boolean success = databaseDir.mkdir();
			if(success){
				System.out.println("Database directory at " + crawler.dbLocation + " created");
			}
			crawl(startingURL, queue);

			DatabaseDAO.setup(databaseDir);
		
			DatabaseDAO.shutdown();
		}		
	}
	
	public static void crawl(String URL, Queue<String> queue) throws IOException{

		// initialize queue with URL
		HttpClient client = new HttpClient(); 
		queue.add(URL);
				
		while(!queue.isEmpty()){//while queue is not empty
			client.setURL(queue.poll());
			String url = client.getURL();
			String urlResponse = client.downloadDocFromUrl(url); //opens connection to server that hosts HTML or XML doc
			System.out.println(urlResponse); // download corresponding page



			//do HEAD request 
			//download robots.txt
			//parse robots.txt

			//use JTidy to create a tree
			//use JTidy's xpath engine
		// extract URLs
		// Append to queue the URLs extracted
		// loop
		}
	}	
	
}
