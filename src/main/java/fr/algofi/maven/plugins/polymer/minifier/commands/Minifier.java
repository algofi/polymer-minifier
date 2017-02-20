package fr.algofi.maven.plugins.polymer.minifier.commands;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

/**
 * this is the interface to all minifiers
 * @author cjechoux
 *
 */
public interface Minifier {

	/**
	 * minimize a polymer component
	 * @param component
	 */
	public void minimize(PolymerComponent component);

}
