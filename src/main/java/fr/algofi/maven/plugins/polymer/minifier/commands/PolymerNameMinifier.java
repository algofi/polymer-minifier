package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Collection;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class PolymerNameMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component, final Collection<PolymerComponent> dependencies) {
		String content = component.getMinifiedContent();
		
		final String name = component.getName();
		final String miniName = component.getMiniName();
		
		if (miniName != null) {

			// minify polymer component name

			// 1) replace <dom-module > id attribute
			content = content.replaceAll("<dom-module\\p{Blank}+id=\"" + name + "\"",
					"<dom-module id=\"" + miniName + "\"");

			// 2) minify javascript polymer component
			content = content.replaceAll("is:\\p{Blank}+['\"]" + name + "['\"]", "is:'" + miniName + "'");

		}
		
		component.setMiniContent(content);
	}

}
