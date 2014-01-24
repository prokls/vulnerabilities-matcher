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

		String str1 = name.substring(0, matcher1.start());
		String str2 = other.toString().substring(0, matcher2.start());

		return str1.compareToIgnoreCase(str2);
	}
}
