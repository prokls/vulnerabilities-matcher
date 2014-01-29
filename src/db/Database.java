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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import matcher.SoftwareDependency;
import matcher.SoftwareName;
import matcher.VersionNumber;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Database {
	public Connection conn = null;

	public Database() {
	}

	private HashMap<String, String> readConfig() {
		HashMap<String, String> config = new HashMap<String, String>();
		try {
			File fXmlFile = new File("config.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			NodeList nList = doc.getElementsByTagName("database");
			Node nNode = nList.item(0);
			Element eElement = (Element) nNode;

			config.put("name", eElement.getElementsByTagName("name").item(0)
					.getTextContent());
			config.put("username", eElement.getElementsByTagName("username")
					.item(0).getTextContent());
			config.put("password", eElement.getElementsByTagName("password")
					.item(0).getTextContent());
			config.put("host", eElement.getElementsByTagName("host").item(0)
					.getTextContent());
		} catch (Exception e) {
			System.err.println("Error with config.xml");
		}

		return config;
	}

	private String buildConnectionString(HashMap<String, String> config) {
		String connectionString = new String();
		connectionString = MessageFormat.format(
				"jdbc:mysql://{0}/{1}?user={2}&password={3}",
				config.get("host"), config.get("name"), config.get("username"),
				config.get("password"));

		return connectionString;
	}

	public void connect() {
		HashMap<String, String> config = readConfig();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(buildConnectionString(config));
		} catch (Exception e) {
			System.err.println(e.toString());
		}

	}

	public Set<ServerSoftwareTuple> getTuples() {
		Statement sql = null;
		HashSet<ServerSoftwareTuple> tuples = new HashSet<ServerSoftwareTuple>();
		try {
			sql = conn.createStatement();
			ResultSet result = sql
					.executeQuery("SELECT * FROM Header INNER JOIN PageInfo "
							+ "ON Header.PAGE_ID = PageInfo.id "
							+ "WHERE (Header.name = 'X-Powered-By' OR "
							+ "Header.name = 'META/Generator')");

			while (result.next()) {
				String powered[] = ServerSoftwareTuple.splitVersion(result
						.getString("Header.value"));

				// TODO: test isValid before and only create object, if enough data is valid
				String host = result.getString("PageInfo.url");
				SoftwareName software = new SoftwareName(powered[0]);
				VersionNumber version = new VersionNumber(powered[1]);
				SoftwareDependency dep = new SoftwareDependency("");

				ServerSoftwareTuple sst = new ServerSoftwareTuple(host,
						software, version, dep);
				tuples.add(sst);
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
