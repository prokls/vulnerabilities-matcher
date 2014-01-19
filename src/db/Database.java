/**
 * The database connection implementation.
 * Retrieves ServerSoftwareTuple instances from the DB dataset. 
 */
package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		} catch (Exception e) {
			System.err.println("Error with config.xml");
		}
		
		return config;
	}
	
	private String buildConnectionString(HashMap<String, String> config)
	{
		String connectionString = new String();
		connectionString = MessageFormat.format(
				"jdbc:mysql://{0}/{1}?user={2}&password={3}",
				config.get("host"), config.get("name"),
				config.get("username"), config.get("password"));
		
		return connectionString;
	}
	
	public void connect() {
		HashMap<String, String> config = readConfig();
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(buildConnectionString(config));
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		
	}
	
	public static String[] splitVersion(String spec) {
		Pattern re1 = Pattern.compile("((\\d+(\\.\\d+)+)([-+]\\w+)*)");
		Pattern re2 = Pattern.compile("^[\\/_\t ]");
		Pattern re3 = Pattern.compile( "[\\/_\t ]$");

		Matcher m = re1.matcher(spec);
		String version = null;
		int v_pos = spec.length();

		// retrieve version
		if (m.find()) {
			version = m.group(1);
			v_pos = m.start();
		}
		
		// retrieve software
		String sw = spec.substring(0, v_pos);
		while (re2.matcher(sw).find())
			sw = sw.substring(1);
		while (re3.matcher(sw).find())
			sw = sw.substring(0, sw.length() - 1);

		String[] tuple = {sw, version};
		return tuple;
	}
	
	public Set<ServerSoftwareTuple> getTuples() {
		Statement sql = null;
		HashSet<ServerSoftwareTuple> tuples = new HashSet<ServerSoftwareTuple>();
		try {
			sql = conn.createStatement();
			ResultSet result = sql.executeQuery(
					"SELECT * FROM Header INNER JOIN PageInfo " +
					"ON Header.PAGE_ID = PageInfo.id " + 
					"WHERE Header.name = 'X-Powered-By'");
			
			while (result.next()) {
				String powered[] = splitVersion(result.getString("Header.value"));

				String host = result.getString("PageInfo.url");
				String software = powered[0];
				String version = powered[1];

				tuples.add(new ServerSoftwareTuple(host, software, version, ""));
			}
			
			if (sql != null)
				sql.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tuples;
	}
	
	public void disconnect() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
