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
		
		try{
			PrintWriter writer = new PrintWriter(output_filepath, "UTF-8");
			writer.close();
			
			writer.println("<?XML version=\"1.0\"?>");
			writer.println("<DOCUMENT>");
			for (Match m : matches) {
				writer.println("<ENTRY>"+m+"</ENTRY>");
			}
			writer.println("</DOCUMENT");
		
		} catch(IOException e){
			e.printStackTrace();
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
