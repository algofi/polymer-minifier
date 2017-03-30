package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class BlankMinifier implements Minifier {

	private static final Logger LOGGER = LogManager.getLogger(BlankMinifier.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component, final Collection<PolymerComponent> dependencies) {

		LOGGER.info("Minifying HTML blanks for " + component.getPath() );
		String content = component.getMinifiedContent();
		
		// remove empty lines
		content = content.replaceAll("^\\p{Blank}*[\n\r]*", "");

		// remove spaces between tags
		// spaces between > </ is only available in HTML code
		content = content.replaceAll(">\\p{Blank}*[\n\r]*\\p{Blank}*[\n\r]*\\p{Blank}*<", "><");
		
		component.setMiniContent(content);
	}

}
