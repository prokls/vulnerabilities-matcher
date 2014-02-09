/**
 * Main program
 */
package matcher;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
		vuls = reader.getVulns();

		LOG.info(MessageFormat.format("{0} vulnerability tuples created.",
				vuls.size()));
	}

	public void matchServerData() {
		// establish database connection
		Database db = new Database();
		db.connect();
		Set<ServerSoftwareTuple> tuples = db.getTuples();

		// the major task: matching server and vulnerability data
		// match each with each one: O(n^2)
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

	public void writeResultFile(String output_filepath, HashSet<ResultTuple> results) {

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root element
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("document");
			doc.appendChild(rootElement);

			for (ResultTuple res : results) {

				// entry element
				Element entry = doc.createElement("entry");
				rootElement.appendChild(entry);
				
				Element hostnames = doc.createElement("hostnames");
				for(String host: res.getHostname()){
					Element hostname = doc.createElement("hostname");
					hostname.appendChild(doc.createTextNode(host));
					hostnames.appendChild(hostname);
				}
				entry.appendChild(hostnames);
				
				Element score = doc.createElement("score");
				score.appendChild(doc.createTextNode(res.getScore()));
				entry.appendChild(score);
				
				Element accessCompl = doc.createElement("accessComplexity");
				accessCompl.appendChild(doc.createTextNode(res.getAccessComplexity()));
				entry.appendChild(accessCompl);
				
				Element summary = doc.createElement("summary");
				summary.appendChild(doc.createTextNode(res.getSummary()));
				entry.appendChild(summary);
				
				Element references = doc.createElement("references");
				for(String ref: res.getReference()){
					Element reference = doc.createElement("reference");
					reference.appendChild(doc.createTextNode(ref));
					references.appendChild(reference);
				}
				entry.appendChild(references);
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(
					new File(output_filepath));

			transformer.transform(source, result);
			System.out.println("File saved.");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}
	
	public HashSet<ResultTuple> WriteValues(String vuln_db){
		SAXParser parser;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		MatcherSaxHandler handler = new MatcherSaxHandler(matches);
		try {
			parser = factory.newSAXParser();			
			parser.parse(vuln_db, handler);			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return handler.getTuple();
		
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

		LOG.setLevel(Level.WARNING);

		Matcher mat = new Matcher();
		mat.readVulnerabilities(args[0]);
		mat.matchServerData();
		mat.writeResultFile("matching_result.xml",mat.WriteValues(args[0]));
		
		System.out.println("Done.");
	}
}
