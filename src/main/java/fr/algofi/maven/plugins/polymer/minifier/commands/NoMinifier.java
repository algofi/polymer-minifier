package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Collection;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class NoMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component, final Collection<PolymerComponent> dependencies) {
		component.setMiniContent(component.getContent());
	}

}
