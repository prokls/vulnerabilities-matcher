/*
 * Represents the name of the name of a software package.
 */
package matcher;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SoftwareName implements Comparable<SoftwareName> {
	private String name;

	public SoftwareName(String software) {
		name = software.trim();
	}

	public String toString() {
		return name;
	}

	public int compareTo(SoftwareName other) {
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher1 = pattern.matcher(name);
		Matcher matcher2 = pattern.matcher(other.toString());
		String str1, str2;

		try {
			str1 = name.substring(0, matcher1.start());
		} catch (IllegalStateException e) {
			str1 = name;
		}
		try {
			str2 = other.toString().substring(0, matcher2.start());
		} catch (IllegalStateException e) {
			str2 = other.toString();
		}

		return str1.compareToIgnoreCase(str2);
	}
}
