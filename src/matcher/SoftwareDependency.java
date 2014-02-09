/*
 * A software dependency. Allows comparison whether a system
 * (represented as a SoftwareDependency) matches a given
 * software stack (also as a SoftwareDependency).
 * 
 * Is not actually used, because read data from vulnerability DB
 * and web crawler data does only create empty SoftwareDependency.
 */
package matcher;

public class SoftwareDependency implements Comparable<SoftwareDependency> {
	private String[] dep;

	public SoftwareDependency(String dep_stack) {
		dep = dep_stack.split("/");
	}

	public String toString() {
		String out = new String();
		for (String d : dep) {
			out += d + "/";
		}
		if (dep.length == 0)
			return "<no dependency>";
		else
			return out.substring(0, out.length() - 1);
	}

	public int compareTo(SoftwareDependency other) {
		for (int d = 0; d < dep.length; d++) {
			if (d >= other.dep.length)
				return 1;
			int result = dep[d].compareTo(other.dep[d]);
			if (result != 0)
				return result;
		}
		if (dep.length < other.dep.length)
			return -1;
		return 0;
	}
}
