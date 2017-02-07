package fr.algofi.maven.plugins.polymer.minifier;

import java.util.ArrayList;
import java.util.List;

/**
 * build a list of string from <tt>"a"</tt> to <tt>"zz"</tt>
 * 
 * @author cjechoux
 *
 */
public class MiniNameProvider {

	/**
	 * initial value to build the list of strings
	 */
	private static final Character INITIAL_VALUE = 'a';

	/**
	 * provide the list of string
	 * 
	 * @return
	 */
	public List<String> provide() {

		final List<String> shortNames = new ArrayList<>(27 * 26);

		for (int j = 0; j < 27; j++) {
			final String left = buildLeftPart(j);

			for (int i = 0; i < 26; i++) {
				final String shortName = buildShortName(left, i);
				shortNames.add(shortName);
			}
		}

		return shortNames;
	}

	/**
	 * build a chain with a `left`part and any letter (lowercase) from "a" to
	 * "z"
	 */
	private String buildShortName(final String left, int i) {
		final Character c = generateCharacter(i);
		final String shortName = left + c;
		return shortName;
	}

	private Character generateCharacter(int i) {
		final Character c = new Character((char) (INITIAL_VALUE + i));
		return c;
	}

	/**
	 * build the `left`part from "" to "z"
	 * 
	 * @param j
	 *            the offset between the letter <tt>'a'</tt>
	 *            <strong>minus</strong> <tt>1</tt>. <tt>j</tt> equals to 0 will
	 *            return an empty string, j equals to <tt>1</tt> will returns
	 *            <tt>"a"</tt>
	 */
	private String buildLeftPart(int j) {
		final String left;
		if (j == 0) {
			left = "";
		} else {
			left = generateCharacter(j - 1).toString();
		}
		return left;
	}

}
