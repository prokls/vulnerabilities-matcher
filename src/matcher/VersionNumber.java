package matcher;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class VersionNumber implements Comparable<VersionNumber> {
	ArrayList<Integer> number;

	public VersionNumber(String version) {
		number = new ArrayList<Integer>();
		String[] ver = version.split("\\.");
		for (String v : ver)
		{
			try{
				number.add(Integer.parseInt(v));
			}catch(NumberFormatException e){}
		}
	}

	public static boolean isValid(String version) {
		if (version == null || version.equals(""))
			return false;
		
		// matching via Regex
		Pattern re = Pattern.compile("((\\d+(\\.\\d+)+)([-+]\\w+)*)");
		Matcher m = re.matcher(version);
		
		return m.find();
	}

	public Integer[] toArray() {
		return (Integer[]) number.toArray();
	}

	public String toString() {
		String out = new String();
		for (Integer i : number)
			out += String.format("%d", i) + ".";
		if (number.size() == 0)
			return "<no version number>";
		else
			return out.substring(0, out.length() - 2);
	}

	public int compareTo(VersionNumber other) {
		if (number.size() == 0 && other.number.size() == 0)
			return 0;
		if (number.size() == 0)
			return -1;
		if (other.number.size() == 0)
			return 1;

		// compare value by value
		for (int i = 0; i < number.size(); i++) {
			if (other.number.size() >= i)
				break;
			int cmp = Integer.compare(number.get(i), other.number.get(i));
			if (cmp != 0)
				return cmp;
		}

		return 0;
	}
}
