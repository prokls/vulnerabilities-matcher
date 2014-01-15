/**
 * The database connection implementation.
 * Retrieves ServerSoftwareTuple instances from the DB dataset. 
 */
package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Database {
	public Connection conn = null;
	public Database() {}
	
	private HashMap<String, String> readConfig()
	{
		HashMap<String, String> config = new HashMap<String, String>();
		try
		{
			File fXmlFile = new File("config.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			NodeList nList = doc.getElementsByTagName("database");
			Node nNode = nList.item(0);
			Element eElement = (Element) nNode;
			
			config.put("name", eElement.getElementsByTagName("name").item(0).getTextContent());
			config.put("username", eElement.getElementsByTagName("username").item(0).getTextContent());
			config.put("password", eElement.getElementsByTagName("password").item(0).getTextContent());
			config.put("host", eElement.getElementsByTagName("host").item(0).getTextContent());
		}catch (Exception e)
		{
			System.err.println("Error with config.xml");
		}
		
		return config;
	}
	
	private String buildConnectionString(HashMap<String, String> config)
	{
		String connectionString = new String();
		connectionString = "jdbc:mysql://" + config.get("host")+ "/" + config.get("name") + "?user=" + config.get("username") + "&password=" + config.get("password");
		
		return connectionString;
	}
	
	public void connect() {
		// TODO: retrieve credentials from configuration file
		//       (define config file yourself)
		// TODO: connect to database and store connection object in object member
		
		HashMap<String, String> config = readConfig();
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(buildConnectionString(config));
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		
	}
	
	public Set<ServerSoftwareTuple> getTuples() {
		// TODO: create new instances of ServerSoftwareTuple
		//       for each entry of software package per hostname
		return new HashSet<ServerSoftwareTuple>();
	}
	
	public void disconnect() {
		// TODO: disconnect from database
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
