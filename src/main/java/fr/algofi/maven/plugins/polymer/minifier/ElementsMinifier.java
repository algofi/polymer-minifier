package fr.algofi.maven.plugins.polymer.minifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.algofi.maven.plugins.polymer.minifier.model.MiniElements;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.util.MiniNameProvider;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;

public class ElementsMinifier {
	private static final Logger LOGGER = LogManager.getLogger(ElementsMinifier.class);
	private PolymerMinifier minifier = new PolymerMinifier();
	private PolymerParser parser;
	private Iterator<String> componentNameIterator;
	private Map<String, PolymerComponent> components;
	private Document indexDocument;
	private Path importPath;
	private String importHref;
	private String buildHref;
	private String importHtml;

	public ElementsMinifier() {
		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		parser = new PolymerParser(scriptEngine);

		final MiniNameProvider provider = new MiniNameProvider();
		final List<String> componentNames = provider.provide().stream()
				.collect(Collectors.mapping(name -> "x-" + name, Collectors.toList()));
		componentNameIterator = componentNames.iterator();
	}

	/**
	 * minimize all elements. To replacement in entry point if present
	 * 
	 * @param path
	 *            path to all elements
	 * @param index
	 *            main entry point. Optional
	 * @return
	 * @throws IOException
	 * @throws MinifierException
	 */
	public MiniElements minimize(final Path index) throws IOException, MinifierException {

		final String indexContent = Files.readAllLines(index).stream().collect(Collectors.joining("\n"));
		final Path path = getImportPath(index, indexContent);

		final String minifiedContent = minifyElements(path);
		String miniIndex = changeImportLink(indexContent);
		miniIndex = minifyDependencies(miniIndex);

		final MiniElements minimized = new MiniElements();
		minimized.setContent(minifiedContent);
		minimized.setIndexContent(miniIndex);
		minimized.setImportBuildHref(buildHref);

		return minimized;
	}

	private String minifyDependencies(String indexContent) {

		for (PolymerComponent component : components.values()) {
			final String tagName = component.getName();
			// tag present ?
			boolean isComponentPresent = tagName != null && tagName.trim().length() > 0
					&& indexDocument.getElementsByTag(tagName).size() > 0;
			if (isComponentPresent) {
				indexContent = MinifierUtils.minifyDependency(indexContent, component);
			}
		}

		return indexContent;
	}

	private String changeImportLink(String indexContent) {

		buildHref = importHref.replaceFirst(".html", ".build.html");
		//indexContent = indexContent.replaceFirst(importHtml.substring(1, importHtml.length() - 1), "link rel=\"import\" href=\"" + buildHref + "\"");
		indexContent = indexContent.replaceFirst(importHtml, "<link rel=\"import\" href=\"" + buildHref + "\">");

		return indexContent;
	}

	private String minifyElements(final Path path) throws IOException, MinifierException {
		// component already appended
		components = getAndOrderAllComponents(path);

		// Dependencies that only create new element
		final Collection<PolymerComponent> dependencyElements = components.values().stream()
				.filter(c -> c.getName() != null).collect(Collectors.toList());

		// give to the minifier a collection of all dependencies
		minifier.setDependencies(dependencyElements);

		final StringBuilder builder = new StringBuilder("<html><head></head><body><div hidden=\"\">\n");
		appendAllComponents(components, builder);
		builder.append("</div></body></html>");
		return builder.toString();
	}

	private Path getImportPath(final Path index, final String indexContent) throws MinifierException {

		indexDocument = Jsoup.parse(indexContent);
		final Elements links = indexDocument.getElementsByTag("link");

		final List<Path> imports = new ArrayList<>();

		for (Element link : links) {
			final String rel = link.attr("rel");
			if ("import".equals(rel)) {
				importHref = link.attr("href").trim();
				importHtml = link.outerHtml();
				importPath = index.getParent().normalize().resolve(Paths.get(importHref)).normalize();
				imports.add(importPath);
			}
		}

		if (imports.isEmpty() || imports.size() > 1) {
			throw new MinifierException("Only one Import tag is supported");
		} else {
			return imports.get(0);
		}

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
				LOGGER.debug("path = " + importPath);
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
					LOGGER.debug(component.getName() + " -> " + component.getMiniName());
				}
			}
			// minify component name
			components.put(component.getPath(), component);
		}

	}

}
