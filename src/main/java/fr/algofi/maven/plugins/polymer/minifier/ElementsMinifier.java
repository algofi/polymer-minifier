package fr.algofi.maven.plugins.polymer.minifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.util.MiniNameProvider;

public class ElementsMinifier {

	private PolymerMinifier minifier = new PolymerMinifier();
	private PolymerParser parser;
	private Iterator<String> componentNameIterator;


	public ElementsMinifier() {
		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		parser = new PolymerParser(scriptEngine);

		final MiniNameProvider provider = new MiniNameProvider();
		final List<String> componentNames = provider.provide().stream()
				.collect(Collectors.mapping(name -> "x-" + name, Collectors.toList()));
		componentNameIterator = componentNames.iterator();
	}

	public String minimize(final Path path) throws IOException, MinifierException {
		final StringBuilder builder = new StringBuilder("<html><head></head><body><div hidden=\"\">\n");

		// component already appended
		final Map<String, PolymerComponent> components = getAndOrderAllComponents(path);
		
		// Dependencies that only create new element
		final Collection<PolymerComponent> dependencyElements = components.values().stream().filter( c -> c.getName() != null ).collect(Collectors.toList()); 
		
		// give to the minifier a collection of all dependencies
		minifier.setDependencies( dependencyElements );

		appendAllComponents(components, builder);

		builder.append("</div></body></html>");

		return builder.toString();
	}

	private void appendAllComponents(final Map<String, PolymerComponent> components, final StringBuilder builder)
			throws MinifierException {

		for (String key : components.keySet()) {
			final PolymerComponent component = components.get(key);

			minifier.minify(component);

			builder.append(component.getMinifiedContent());
		}

	}

	private Map<String, PolymerComponent> getAndOrderAllComponents(final Path path) throws IOException {
		final Map<String, PolymerComponent> components = new LinkedHashMap<>();
		final Document document = Jsoup.parse(path.toFile(), Charset.defaultCharset().name());

		final Elements links = document.getElementsByTag("link");
		for (Element link : links) {
			final String rel = link.attr("rel");
			if ("import".equals(rel)) {
				final String href = link.attr("href");
				final String importPath = path.getParent().normalize().resolve(Paths.get(href)).normalize().toString();
				System.out.println("path = " + importPath);
				final PolymerComponent component = parser.read(importPath);
				appendComponent(component, components);

			}
		}

		return components;
	}

	private void appendComponent(final PolymerComponent component, Map<String, PolymerComponent> components) {

		// append import, and then the components itself
		for (final PolymerComponent importedComponent : component.getImports()) {
			appendComponent(importedComponent, components);
		}

		// append myself
		if (!components.containsKey(component.getPath())) {

			if (component.getName() != null) {
				if (componentNameIterator.hasNext()) {
					component.setMiniName(componentNameIterator.next());
					System.out.println(component.getName() + " -> " + component.getMiniName());
				}
			}
			// minify component name
			components.put(component.getPath(), component);
		}

	}

	
}


