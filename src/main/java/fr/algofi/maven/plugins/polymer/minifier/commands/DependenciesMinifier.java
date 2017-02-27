package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Collection;
import java.util.List;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;

public class DependenciesMinifier implements Minifier {
	
	private final Collection<PolymerComponent> dependencies;

	public DependenciesMinifier(Collection<PolymerComponent> dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component) {
		String miniContent = component.getMinifiedContent();

		for (PolymerComponent dependency : dependencies) {
			miniContent = minifyDependency(miniContent, dependency);
		}

		component.setMiniContent(miniContent);

	}
	
	private String minifyDependency(String content, PolymerComponent dependency) {
		final String dependencyName = dependency.getName();
		final String dependencyMiniName = dependency.getMiniName();
		// we replace the tag name everywhere :
		// opening and closing HTML tags, style CSS selector, javasacript CSS
		// selector,
		// document.createElement, and so on
		content = content.replaceAll(dependencyName, dependencyMiniName);
		// replace custom element attributes

		// replace all mini tags
		final List<String> tags = MinifierUtils.findHtmlTags(dependencyMiniName, content);

		for (String tag : tags) {
			String miniTag = tag;
			for (PolymerProperty property : dependency.getProperties()) {
				miniTag = miniTag.replace(property.getAttribute() + "=", property.getMiniAttribute() + "=");
				miniTag = miniTag.replace(property.getAttribute() + "$=", property.getMiniAttribute() + "$=");
			}
			miniTag = miniTag.replaceAll("\\v+", " ");
			miniTag = miniTag.replaceAll("\t+", "");
			content = content.replace(tag, miniTag);
		}

		return content;
	}

}
