package ch.dragbone.anonsomenauts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper{
	/**
	 * Replaces all occurrences of the first group of {@code regex} with {@code replace} in {@code input}
	 * @param input Input string
	 * @param regex Regular expression to match
	 * @param replace String to replace with
	 * @return String with all occurrences replaced
	 */
	public static String replaceSubMatch(String input, String regex, String replace){
		int lastPos = 0;
		Matcher m = Pattern.compile(regex).matcher(input);
		while(m.find(lastPos) && m.groupCount() > 0){
			int beginIndex = m.start(1);
			int endIndex = m.end(1);
			input = input.substring(0, beginIndex - 1) + replace + input.substring(endIndex + 1);
			m = Pattern.compile(regex).matcher(input);
			lastPos = beginIndex;
		}
		return input;
	}

	/**
	 * Example:<br/>
	 * 	{@code firstSubMatch("a=12, x=23, n=100, n=55","n=(\\d+)") // returns "100"}
	 * @param input Input string
	 * @param regex Regular expression to match
	 * @return First group of first match
	 */
	public static String firstSubMatch(String input, String regex){
		Matcher m = Pattern.compile(regex).matcher(input);
		if(!m.find()){
			return null;
		}
		return m.group(1);
	}

	public static String unEscapeXMLString(String xmlString){
		xmlString = xmlString.replace("&lt;", "<");
		xmlString = xmlString.replace("&gt;", ">");
		xmlString = xmlString.replace("&quot;", "\"");
		xmlString = xmlString.replace("&apos;", "'");
		xmlString = xmlString.replace("&amp;", "&"); // needs to be last
		return xmlString;
	}
}
