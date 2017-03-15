package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;

public class HTMLCommentMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component, final Collection<PolymerComponent> dependencies) {
		String minifiedContent = component.getMinifiedContent();
		
//		final Pattern pattern = Pattern.compile("(<!--[^'].*[^']-->)", Pattern.DOTALL);
		final Pattern pattern = Pattern.compile("(<!--.*?-->)", Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(minifiedContent);
		
		final Document document = Jsoup.parse(minifiedContent);
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		
		while (matcher.find()) {
			final String comment = matcher.group(1);
			// we don't replace if the comment found is within script tag
			if ( !scriptPart.getBulkScript().contains(comment) ) {
				minifiedContent = minifiedContent.replace(comment, "");
			}
		}
		
		component.setMiniContent(minifiedContent.trim());
	}

}
