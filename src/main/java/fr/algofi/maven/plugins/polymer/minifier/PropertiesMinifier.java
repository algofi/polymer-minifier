package fr.algofi.maven.plugins.polymer.minifier;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesMinifier {

	public String minify(String content, final List<String> properties) throws MinifierException {

		final List<String> shortNames = new MiniPropertyProvider().provide();
		final Iterator<String> shortNameIterator = shortNames.iterator();

		if (!properties.isEmpty()) {
			for (final String propertyName : properties) {

				final String miniPropertyName = getNextMiniPropertyName(shortNameIterator);

				content = minify(content, propertyName, miniPropertyName);
			}
		}
		return content;
	}

	private String getNextMiniPropertyName(final Iterator<String> iterator) throws MinifierException {

		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			throw new MinifierException("Too much property in the web component");
		}

	}

	private String minify(String content, String propertyName, String miniPropertyName) {

		// replace properties declaration
		content = content.replaceAll(propertyName + ":", miniPropertyName + ":");

		// replace property use in HTML
		content = content.replaceAll("\\[\\[" + propertyName, "[[" + miniPropertyName);
		content = content.replaceAll("\\{\\{" + propertyName, "{{" + miniPropertyName);
		
		// replace property use in JS
		content = content.replaceAll("this." + propertyName, "this." + miniPropertyName);
		
		//final Pattern thisAssignementPattern = Pattern.compile("(var|let)\\p{Blank}+([a-zA-Z_$]+)\\p{Blank}+=\\p{Blank}+this\\p{Blank}+;?");
		final Pattern thisAssignementPattern = Pattern.compile("(var|let)\\p{Blank}+([a-zA-Z_$]+)\\p{Blank}*=\\p{Blank}*this\\p{Blank}*;?");
		
		// FIXME : do this only in the scope of a function
		final Matcher matcher = thisAssignementPattern.matcher(content);
		while ( matcher.find() ) {
			final String found = matcher.group(2);
			content = content.replaceAll( found + "." + propertyName, found + "." + miniPropertyName);
		}
		
		
		return content;

	}

}
