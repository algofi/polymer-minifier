package fr.algofi.maven.plugins.polymer.minifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;
import fr.algofi.maven.plugins.polymer.minifier.util.MiniNameProvider;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;

public class PolymerMinifier {

	private Collection<PolymerComponent> dependencies = new ArrayList<>();
	private boolean minifyJavascript;

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

		// minify all blanks
		polymer.setMiniContent(minifyBlanks(polymer.getMinifiedContent()));

		final Map<String, String> minifiedProperties = new LinkedHashMap<>();

		if (!polymer.getProperties().isEmpty()) {
			for (final PolymerProperty property : polymer.getProperties()) {

				final String propertyName = property.getName();
				final String miniPropertyName = getNextMiniPropertyName(shortNameIterator);

				minifiedProperties.put(propertyName, miniPropertyName);
				property.setMiniName(miniPropertyName);

				final String miniContent = minify(polymer, propertyName, miniPropertyName);
				polymer.setMiniContent(miniContent);
			}
		}

		if (minifyJavascript) {
			minifyJavascript(polymer);
		}

	}

	private void minifyJavascript(PolymerComponent polymer) {
		final Document document = Jsoup.parse(polymer.getMinifiedContent());
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		final String miniJavascript = MinifierUtils.minifyJavascript(polymer.getPath(), scriptPart.getBulkScript());
		final String minifiedContent = polymer.getMinifiedContent().replace(scriptPart.getBulkScript(), miniJavascript);
		polymer.setMiniContent(minifiedContent);
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
		// content = minifyBlanks(content);
		content = MinifierUtils.minifyName(content, polymer.getName(), polymer.getMiniName());
		content = minifyDependenciesName(content, dependencies);

		return content;

	}

	private String minifyDependenciesName(String content, final Collection<PolymerComponent> dependencies) {

		for (PolymerComponent dependency : dependencies) {
			content = MinifierUtils.minifyDependency(content, dependency);
		}

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

	public void setDependencies(Collection<PolymerComponent> dependencies) {
		this.dependencies = dependencies;
	}

	public void minifyJavascript(boolean minifyJavascript) {
		this.minifyJavascript = minifyJavascript;
	}
}
