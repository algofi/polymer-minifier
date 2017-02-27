package fr.algofi.maven.plugins.polymer.minifier.commands;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

/**
 * this is the interface to all minifiers. Minifiers can remove blanks space,
 * minify javascript, minify component tag names, and so on.
 * 
 * @author cjechoux
 *
 */
public interface Minifier {

	/**
	 * minimize a polymer component
	 * 
	 * @param component
	 *            web component to minify.
	 * @throws MinifierException
	 *             thrown if the minify operation failed
	 */
	public void minimize(PolymerComponent component) throws MinifierException;

}
