package edu.upenn.cis455.crawler;

import java.util.ArrayList;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * @author James Park
 * Provides a record of registered users
 */
@Entity
public class User {
	@SuppressWarnings("unused")
	private String password;
	private ArrayList<Integer> channels;
	
	@PrimaryKey
	private String username;
	
	public User(){
		//default constructor
	}
	
	public User(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public String getUsername(){
		return this.username;
	}
		
	public ArrayList<Integer> getChannels(){
		return channels;
	}
	
	public void addChannel(int channel){
		channels.add(channel);
	}
}
