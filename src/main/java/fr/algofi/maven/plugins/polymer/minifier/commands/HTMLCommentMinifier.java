package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class HTMLCommentMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component, final Collection<PolymerComponent> dependencies) {
		String minifiedContent = component.getMinifiedContent();
		
		final Pattern pattern = Pattern.compile("(<!--[^'].*[^']-->)", Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(minifiedContent);
		while (matcher.find()) {
			final String comment = matcher.group(1);
			minifiedContent = minifiedContent.replace(comment, "");
		}
		
		component.setMiniContent(minifiedContent);
	}

}
