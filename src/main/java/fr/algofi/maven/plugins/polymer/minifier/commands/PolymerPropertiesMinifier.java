package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;
import fr.algofi.maven.plugins.polymer.minifier.util.MiniNameProvider;

/**
 * minify polymer properties. Given a web component with properties, all its web
 * component properties are minified : properties name are replaces by smaller
 * name : 'a', 'b', 'c'
 * 
 * @author cjechoux
 *
 */
public class PolymerPropertiesMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component) throws MinifierException {
		String content = component.getMinifiedContent();

		final List<String> shortNames = new MiniNameProvider().provide();
		final Iterator<String> shortNameIterator = shortNames.iterator();

		if (!component.getProperties().isEmpty()) {
			for (final PolymerProperty property : component.getProperties()) {

				final String propertyName = property.getName();
				final String miniPropertyName = getNextMiniPropertyName(shortNameIterator);

				property.setMiniName(miniPropertyName);

				content = minifyProperties(content, propertyName, miniPropertyName);

			}
		}

		component.setMiniContent(content);
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

	private String getNextMiniPropertyName(final Iterator<String> iterator) throws MinifierException {

		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			throw new MinifierException("Too much property in the web component");
		}

	}

}
