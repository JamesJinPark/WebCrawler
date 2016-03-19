package test.edu.upenn.cis455;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import edu.upenn.cis455.crawler.Channel;
import edu.upenn.cis455.crawler.DatabaseDAO;
import edu.upenn.cis455.crawler.User;
import edu.upenn.cis455.crawler.Webpage;
import junit.framework.TestCase;

public class DatabaseDAOTest extends TestCase {
	File databaseDir;
	
	public void setupTest(){
		databaseDir = new File("database"); //create file 	
		databaseDir.mkdir();
		DatabaseDAO.setup(databaseDir);		
	}

	public void testDatabaseUser(){
		setupTest();
		User user1 = new User("James Park", "Password");
		User user2 = new User("James Jin Park", "12345");
		DatabaseDAO.registerUser(user1);
		DatabaseDAO.registerUser(user2);
		User userA = DatabaseDAO.getUser("James Park");
		User userB = DatabaseDAO.getUser("James Jin Park");
		assertEquals(userA.getUsername(), user1.getUsername());
		assertEquals(userB.getUsername(), user2.getUsername());
		DatabaseDAO.deleteUser("James Park");
		DatabaseDAO.deleteUser("James Jin Park");
		assertNull(DatabaseDAO.getUser("James Park"));
		assertNull(DatabaseDAO.getUser("James Jin Park"));
		DatabaseDAO.shutdown();		
	}
	
	public void testDatabaseChannel(){
		//setupTest();
	}
	
	public void testDatabaseWebpage(){
		setupTest();
		Date oldDate = new Date();
		DatabaseDAO.addPage(new Webpage("www.test.edu", "Testing Content", new Date()));
		Webpage webpage = DatabaseDAO.getPage("www.test.edu");
		assertTrue(webpage.checkTimeStamp(new Date()));
		assertFalse(webpage.checkTimeStamp(oldDate));
		DatabaseDAO.deletePage("www.test.edu");
		assertNull(DatabaseDAO.getPage("www.test.edu"));
		DatabaseDAO.shutdown();		
	}
	
	public void testCleanup(){
		setupTest();
		System.out.println(databaseDir.toPath());
		System.out.println(databaseDir.toString());		
		System.out.println(databaseDir.delete());
		try {
			Files.deleteIfExists(databaseDir.toPath());
		} catch (IOException e) {
			System.err.println("Cannot find directory to delete.");
		}
	}

}
