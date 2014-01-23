/**
 * Represents a match of a ServerSoftwareTuple with a VulnerabilityTuple.
 * Identified by a hostname (via ServerSoftwareTuple) and a
 * CVE (via VulnerabilityTuple).
 */
package matcher;

import java.text.MessageFormat;

public class Match {
	private String hostname;
	private String vul_id;

	public Match(String host, String cve) {
		hostname = host;
		vul_id = cve;
	}

	/**
	 * Retrieve some data about this vulnerability. Return the value
	 * corresponding to `key`. Uses the XML SAX parser for data retrieval.
	 * 
	 * @param vul_db_filepath
	 *            Filepath to vulnerability database (xml)
	 * @param key
	 *            An attribute of the vulnerability (eg. summary)
	 * @return String the value corresponding to the key for this vul.
	 */
	public String retrieve(String vul_db_filepath, String cve, String key) {
		// TODO: open `vul_db_filepath` database with XML SAX parser
		// TODO: search for vulnerability with CVE `cve`
		// TODO: close XML parser
		// TODO: return value corresponding to `key` for the given vulnerability
		// `cve`
		return "TODO";
	}

	public String toString() {
		return MessageFormat.format("Match<{0}, {1}>", hostname, vul_id);
	}
}
