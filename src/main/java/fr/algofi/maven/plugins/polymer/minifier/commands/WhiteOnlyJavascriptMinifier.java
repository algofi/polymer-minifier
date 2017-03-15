package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;

/**
 * trim each line of the script. Remove all line break
 * 
 * @author cjechoux
 *
 */
public class WhiteOnlyJavascriptMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(PolymerComponent component, Collection<PolymerComponent> dependencies)
			throws MinifierException {

		final ScriptPart scriptPart = extractJavascript(component);
		final String miniJavascript = minifyJavascript(scriptPart);
		updateJavascript(component, scriptPart, miniJavascript);

	}

	/**
	 * update the javascript into the component
	 * 
	 * @param component
	 * @param scriptPart
	 * @param miniJavascript
	 */
	private void updateJavascript(PolymerComponent component, final ScriptPart scriptPart,
			final String miniJavascript) {
		final String minifiedContent = component.getMinifiedContent().replace(scriptPart.getBulkScript(),
				miniJavascript);
		component.setMiniContent(minifiedContent);
	}

	/**
	 * minify Javascript : trim each line of the script
	 * 
	 * @param scriptPart
	 *            script part to read
	 * @return minified script
	 */
	private String minifyJavascript(final ScriptPart scriptPart) {
		String miniJavascript = scriptPart.getBulkScript();

		// remove blanks between punct and word or numbers except within
		// strings...
		miniJavascript = removeSpacesBetweenPunctAndWordsExceptWithinStrings(miniJavascript);

		// all blank line are reduced to ''
		miniJavascript = miniJavascript.replaceAll("(?m)^\\p{Blank}*$", "");
		// remove starting blanks
		miniJavascript = miniJavascript.replaceAll("(?m)^\\p{Blank}*", "");
		// remove trailing blanks
		miniJavascript = miniJavascript.replaceAll("(?m)\\p{Blank}*$", "");
		// remove line comments first
		miniJavascript = miniJavascript.replaceAll("(?m)^//(\\p{Print}| |\t)*", "");
		// remove empty lines
		miniJavascript = miniJavascript.replaceAll("\\R*", "");
		// remove multi line commments
		miniJavascript = miniJavascript.replaceAll("/\\*\\p{Print}*\\*/", "");

		// remove blanks between punctuations
		miniJavascript = removeBlanksBetweenPunctuations(miniJavascript);

		return miniJavascript;
	}

	private String removeBlanksBetweenPunctuations(String miniJavascript) {
		return removeBlanksBetweenPattern(miniJavascript, "\\p{Punct}", "\\p{Punct}", Optional.empty());
	}

	private String removeBlanksBetweenPattern(String miniJavascript, final String startRegEx, final String endRegEx,
			Optional<Predicate<String>> predicate) {
		final Pattern pattern = Pattern.compile("(" + startRegEx + ")\\p{Blank}+(" + endRegEx + ")");
		final Matcher matcher = pattern.matcher(miniJavascript);
		while (matcher.find()) {
			final String startMatch = matcher.group(1);
			final String endMatch = matcher.group(2);
			final String found = matcher.group();
			if (!predicate.isPresent()) {
				miniJavascript = miniJavascript.replace(found, startMatch + endMatch);
			} else if (predicate.get().test(found)) {
				miniJavascript = miniJavascript.replace(found, startMatch + endMatch);
			}
		}
		return miniJavascript;
	}

	private String removeSpacesBetweenPunctAndWordsExceptWithinStrings(String miniJavascript) {

		final Set<String> strings = new HashSet<>();

		// find all string
		Pattern pattern = Pattern.compile("\"[^\"]\"");
		Matcher matcher = pattern.matcher(miniJavascript);
		while (matcher.find()) {
			strings.add(matcher.group());
		}
		pattern = Pattern.compile("'[^']'");
		matcher = pattern.matcher(miniJavascript);
		while (matcher.find()) {
			strings.add(matcher.group());
		}

		miniJavascript = removeBlanksBetweenPattern(miniJavascript, "\\p{Alnum}+", "\\p{Punct}",
				Optional.of(found -> !strings.contains(found)));
		miniJavascript = removeBlanksBetweenPattern(miniJavascript, "\\p{Punct}", "\\p{Alnum}+",
				Optional.of(found -> !strings.contains(found)));

		return miniJavascript;
	}

	/**
	 * extract the Javascript part
	 * 
	 * @param component
	 *            component that contains javascript
	 * @return Script extracted
	 */
	private ScriptPart extractJavascript(PolymerComponent component) {
		final Document document = Jsoup.parse(component.getMinifiedContent());
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		return scriptPart;
	}

}
