/**
 * Retrieve a server software package available for a given host.
 * Consists of a software stack (dependencies), a software
 * name and a version number. The important comparison capabilities
 * are implemented in `VulnerabilityTuple.compareTo`.
 * 
 * This uniquely identifies some software in version X with dependencies
 * at a given host.
 */
package db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matcher.SoftwareDependency;
import matcher.SoftwareName;
import matcher.VersionNumber;
import vulndb.VulnerabilityTuple;

public class ServerSoftwareTuple implements Comparable<ServerSoftwareTuple> {
	// The hostname where this software is available
	private String hostname;
	// Software identifier
	private SoftwareName name;
	// Software dependencies
	private SoftwareDependency stack;
	// Software version number
	private VersionNumber version_number;

	public ServerSoftwareTuple(String host, SoftwareName id,
			VersionNumber version, SoftwareDependency dep) {
		hostname = host;
		name = id;
		version_number = version;
		stack = dep;
	}

	public String getHostname() {
		return hostname;
	}

	public SoftwareName getSoftware() {
		return name;
	}

	public VersionNumber getVersion() {
		return version_number;
	}

	public SoftwareDependency getDependency() {
		return stack;
	}

	/*
	 * Take some input string and split software name
	 * from software version number. Returns
	 * Array[software name, version number].
	 */ 
	public static String[] splitVersion(String spec) {
		Pattern re1 = Pattern.compile("((\\d+(\\.\\d+)+)([-+]\\w+)*)");
		Pattern re2 = Pattern.compile("^[\\/_\t ]");
		Pattern re3 = Pattern.compile("[\\/_\t ]$");

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

		String[] tuple = { sw, version };
		return tuple;
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

	public String toString() {
		String[] args = new String[] { hostname, name.toString(),
				stack.toString(), version_number.toString() };
		String out = new String();
		for (String str : args) {
			if (str != null && str.length() > 0)
				out += str + ", ";
		}

		if (out.length() > 3)
			return "SoftwareTuple(" + out.substring(0, out.length() - 2) + ")";
		else
			return "SoftwareTuple()";
	}
}
