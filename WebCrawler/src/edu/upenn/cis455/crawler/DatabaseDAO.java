package edu.upenn.cis455.crawler;

import java.io.File;
import java.util.ArrayList;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class DatabaseDAO {
	//The EntityStore for User, Channel, and Webpage databases
	private static EntityStore userStore;

	private static EntityStore channelStore;

	private static EntityStore webpageStore;

	//Environment for EntityStore
	private static Environment env;
	
	//PrimaryIndex accessor for users
	private static PrimaryIndex<String, User> registeredUsers;

	//PrimaryIndex accessor for channels
	private static PrimaryIndex<String, Channel> channels;

	//PrimaryIndex accessor for webpages
	private static PrimaryIndex<String, Webpage> webpages;
		
	public static void setup(File databaseDir){
		EnvironmentConfig envConfig = new EnvironmentConfig();
		StoreConfig storeConfig = new StoreConfig();
		envConfig.setAllowCreate(true);
		storeConfig.setAllowCreate(true);
		envConfig.setTransactional(true);
		storeConfig.setTransactional(true);
		env = new Environment(databaseDir, envConfig);
		userStore = new EntityStore(env, "UserEntityStore", storeConfig);
		channelStore = new EntityStore(env, "ChannelEntityStore", storeConfig);
		webpageStore = new EntityStore(env, "WebpageEntityStore", storeConfig);
		registeredUsers = userStore.getPrimaryIndex(String.class, User.class);
		channels = channelStore.getPrimaryIndex(String.class, Channel.class);
		webpages = webpageStore.getPrimaryIndex(String.class, Webpage.class);
	}
		
	public static void registerUser(User user){
		registeredUsers.put(user);
	}
	
	public static User getUser(String username){
		return registeredUsers.get(username);
	}
	
	public static boolean userExists(String username){
		return registeredUsers.contains(username);
	}
	
	public static void deleteUser(String username){
		registeredUsers.delete(username);
	}
	
	public static void addChannel(Channel channel){
		channels.put(channel);
	}
	
	public static Channel getChannel(String channelName){
		return channels.get(channelName);
	}
		
	public static ArrayList<Channel> getAllChannels(){
		ArrayList<Channel> tempList = new ArrayList<Channel>(); 
		for(String key : channels.keys()){
			tempList.add(channels.get(key));
		}
		return tempList;
	}
	
	public static void deleteChannel(String channelName){
		channels.delete(channelName);
	}
	
	public static void addPage(Webpage webpage){
		webpages.put(webpage);
	}
	
	public static Webpage getPage(String URL){
		return webpages.get(URL);
	}

	public static boolean pageExists(String URL){
		if(webpages.get(URL) == null){
			return false;
		}
		return true;
//		return webpages.contains(URL);
	}

	
	public static void deletePage(String URL){
		webpages.delete(URL);
	}
	
	public static void shutdown(){
		userStore.close();
		channelStore.close();
		webpageStore.close();

		env.cleanLog();
		env.close();
	}
}
