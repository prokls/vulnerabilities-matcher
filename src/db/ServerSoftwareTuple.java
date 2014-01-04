/**
 * Retrieve a server software package available for a given host.
 * Typically consists of a software stack (dependencies), a software
 * identifier (name) and a version number. The important comparison
 * capabilities are implemented in `VulnerabilityTuple.compareTo`.
 * 
 * This uniquely identifies some software in version X with dependencies
 * at a given host.
 */
package db;

import vulndb.VulnerabilityTuple;

public class ServerSoftwareTuple implements Comparable<ServerSoftwareTuple> {
	// The hostname where this software is available
	private String hostname;
	// Software identifier 
	private String name;
	// Software dependencies
	private String stack; // TODO: has to be "defined more precisely" or "normalized" 
	// Software version number
	private String version_number; 

	public ServerSoftwareTuple(String host, String id, String version, String dep) {
		hostname = host;

		name = id;
		version_number = version;
		stack = dep;
	}

	public String getHostname() {
		return hostname;
	}
	
	public String getSoftware() {
		return name;
	}
	
	public String getVersion() {
		return version_number;
	}
	
	public String getDependency() {
		return stack;
	}
	
	@Override
	public int compareTo(ServerSoftwareTuple o) {
		int result = hostname.compareTo(o.getHostname());
		if (result != 0)
			return result;
		result = name.compareTo(o.getSoftware());
		if (result != 0)
			return result;
		result = version_number.compareTo(o.getVersion());
		if (result != 0)
			return result;
		result = stack.compareTo(o.getDependency());
		if (result != 0)
			return result;

		return 0;
	}

	public int compareTo(VulnerabilityTuple o) {
		return -o.compareTo(this);
	}
}
