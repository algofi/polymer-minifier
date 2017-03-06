package fr.algofi.maven.plugins.polymer.minifier.util;

import java.util.stream.IntStream;

public class StringUtils {

	private StringUtils() {
	}

	/**
	 * build a string containing <tt>offset</tt>white spaces.
	 * 
	 * @param offset number of spaces to append
	 * @return string with only <tt>offset</tt> spaces
	 */
	public static String offset(int offset) {
		final StringBuilder builder = new StringBuilder();
		IntStream.range(0, offset).forEach(i -> builder.append(" "));
		return builder.toString();

	}
}
