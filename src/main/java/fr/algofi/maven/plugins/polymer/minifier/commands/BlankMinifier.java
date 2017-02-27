package fr.algofi.maven.plugins.polymer.minifier.commands;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class BlankMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component) {
		String content = component.getMinifiedContent();
		
		// remove empty lines
		content = content.replaceAll("^\\p{Blank}*[\n\r]*", "");

		// remove spaces between tags
		// spaces between > </ is only available in HTML code
		content = content.replaceAll(">\\p{Blank}*[\n\r]*\\p{Blank}*[\n\r]*\\p{Blank}*<", "><");
		
		component.setMiniContent(content);
	}

}
