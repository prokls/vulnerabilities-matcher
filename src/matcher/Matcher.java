/**
 * Main program
 */
package matcher;

import java.io.*;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import vulndb.VulnerabilityReader;
import vulndb.VulnerabilityTuple;
import db.Database;
import db.ServerSoftwareTuple;

public class Matcher {

	// Tuples of vulnerabilities (retrieved from vulnerabilities database) 
	private Set<VulnerabilityTuple> vuls;
	// Matches of vulnerabilities with server software 
	private Set<Match> matches;
	// Logging instance to use
	private final static Logger LOG = Logger.getLogger("VulMatcher");

	public Matcher() {
		vuls = new HashSet<VulnerabilityTuple>();
		matches = new HashSet<Match>();
	}
	
	public void readVulnerabilities(String vuln_db_filepath) {
		VulnerabilityReader reader = new VulnerabilityReader(vuln_db_filepath);
		for (VulnerabilityTuple vt : reader) {
			vuls.add(vt);
		}

		LOG.info(MessageFormat.format("{0} vulnerability tuples created.", vuls.size()));
	}
	
	public void matchServerData() {
		// establish database connection
		Database db = new Database();
		db.connect();
		Set<ServerSoftwareTuple> tuples = db.getTuples();

		// the major task: matching server and vulnerability data
		//     match each with each one: O(n^2)
		for (VulnerabilityTuple vt : vuls) {
			for (ServerSoftwareTuple sst : tuples) {
				if (vt.compareTo(sst) == 0) {
					String cve = vt.getCVE();
					String hostname = sst.getHostname();
					
					matches.add(new Match(hostname, cve));
				}
			}
		}
		
		// teardown
		db.disconnect();
		LOG.info(MessageFormat.format("{0} matches found.", matches.size()));
	}
	
	public void writeResultFile(String output_filepath) {
		// TODO: take `matches` members and write them as XML file to `output_filepath`
		
		int id_num = 0;
		
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// root element
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("document");
			doc.appendChild(rootElement);
			
			for(Match m : matches){
				id_num += 1;
				
				// entry element
				Element entry = doc.createElement("entry");
				entry.appendChild(doc.createTextNode(m.toString()));
				entry.setAttribute("id", String.valueOf(id_num));
				rootElement.appendChild(entry);
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(output_filepath));
				
				transformer.transform(source, result);
				System.out.println("File saved.");
			}
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
			
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("usage: ./Matcher <vulnerabilites.xml>");
			System.err.println("Furthermore the MySQL server must be running");
			System.exit(1);
		}

		// TODO: only during development (set to Level.WARNING in production)
		LOG.setLevel(Level.FINEST);
		
		Matcher mat = new Matcher();
		mat.readVulnerabilities(args[0]);
		mat.matchServerData();
		mat.writeResultFile("matching_result.xml");
		
		System.out.println("Done.");
	}
}
