package edu.upenn.cis455.crawler;

import java.util.ArrayList;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Channel {
	ArrayList<String> XPathExpressions;
	ArrayList<String> URLS;
	User owner;
	
	@PrimaryKey
	String channelName;
	
	public Channel(){
		//default constructor
	}

	public Channel(ArrayList<String> XPathExpressions, ArrayList<String> URLS, User owner, String name){
		this.XPathExpressions = XPathExpressions;
		this.URLS = URLS;
		this.owner = owner;
		this.channelName = name;
	}
}
