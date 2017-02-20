package fr.algofi.maven.plugins.polymer.minifier.commands;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class NoMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component) {
		component.setMiniContent(component.getContent());
	}

}
