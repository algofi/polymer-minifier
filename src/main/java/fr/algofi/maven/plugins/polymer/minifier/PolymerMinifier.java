package fr.algofi.maven.plugins.polymer.minifier;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolymerMinifier {

	/**
	 * minify a polymer component
	 * 
	 * @param polymer
	 * @throws MinifierException
	 */
	public void minify(final PolymerComponent polymer) throws MinifierException {

		final List<String> shortNames = new MiniNameProvider().provide();
		final Iterator<String> shortNameIterator = shortNames.iterator();

		// initial value
		polymer.setMiniContent(polymer.getContent());

		final Map<String, String> minifiedProperties = new LinkedHashMap<>();

		if (!polymer.getProperties().isEmpty()) {
			for (final String propertyName : polymer.getProperties()) {

				final String miniPropertyName = getNextMiniPropertyName(shortNameIterator);

				minifiedProperties.put(propertyName, miniPropertyName);

				final String miniContent = minify(polymer, propertyName, miniPropertyName);
				polymer.setMiniContent(miniContent);
			}
		}

		polymer.setMinifiedProperties(minifiedProperties);
	}

	private String getNextMiniPropertyName(final Iterator<String> iterator) throws MinifierException {

		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			throw new MinifierException("Too much property in the web component");
		}

	}

	private String minify(PolymerComponent polymer, String propertyName, String miniPropertyName) {

		String content = polymer.getMinifiedContent();

		content = minifyProperties(content, propertyName, miniPropertyName);
		content = minifyBlanks(content);
		content = minifyName(content, polymer.getName(), polymer.getMiniName());

		return content;

	}

	private String minifyName(String content, String name, String miniName) {

		// replace <dom-module > id attribute
		content = content.replaceAll("<dom-module\\p{Blank}+id=\"" + name + "\"",
				"<dom-module id=\"" + miniName + "\"");

		content = content.replaceAll("is:\\p{Blank}+['\"]" + name + "['\"]", "is:'" + miniName + "'");

		content = content.replaceAll("<" + name, "\\<" + miniName);
		content = content.replaceAll("</" + name + ">", "</" + miniName + ">");

		return content;
	}

	private String minifyBlanks(String content) {
		// remove empty lines
		content = content.replaceAll("^\\p{Blank}*[\n\r]*", "");

		// remove spaces between tags
		// spaces between > </ is only available in HTML code
		content = content.replaceAll(">\\p{Blank}*[\n\r]*\\p{Blank}*[\n\r]*\\p{Blank}*<", "><");

		return content;
	}

	private String minifyProperties(String content, String propertyName, String miniPropertyName) {
		// replace properties declaration
		content = content.replaceAll(propertyName + ":", miniPropertyName + ":");

		// replace property use in HTML
		content = content.replaceAll("\\[\\[" + propertyName, "[[" + miniPropertyName);
		content = content.replaceAll("\\{\\{" + propertyName, "{{" + miniPropertyName);

		// replace property use in JS
		content = content.replaceAll("this." + propertyName, "this." + miniPropertyName);

		// final Pattern thisAssignementPattern =
		// Pattern.compile("(var|let)\\p{Blank}+([a-zA-Z_$]+)\\p{Blank}+=\\p{Blank}+this\\p{Blank}+;?");
		final Pattern thisAssignementPattern = Pattern
				.compile("(var|let)\\p{Blank}+([a-zA-Z_$]+)\\p{Blank}*=\\p{Blank}*this\\p{Blank}*;?");

		// FIXME : do this only in the scope of a function
		final Matcher matcher = thisAssignementPattern.matcher(content);
		while (matcher.find()) {
			final String found = matcher.group(2);
			content = content.replaceAll(found + "." + propertyName, found + "." + miniPropertyName);
		}
		return content;
	}

}
