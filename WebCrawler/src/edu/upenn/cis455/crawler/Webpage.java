package edu.upenn.cis455.crawler;

import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class Webpage {
	private String content;
	private Date timestamp;

	@PrimaryKey
	private String URL;

	public Webpage(){
		//default constructor		
	}
	
	public Webpage(String URL, String content, Date timestamp){
		this.content = content;
		this.timestamp = timestamp;
		this.URL = URL;
	}
	
	public String getContent(){
		return content;
	}
	
	public boolean checkTimeStamp(Date currentTime){
		return currentTime.after(timestamp);
	}

}
