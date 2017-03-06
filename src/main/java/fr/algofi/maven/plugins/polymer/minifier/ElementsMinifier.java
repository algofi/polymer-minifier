package fr.algofi.maven.plugins.polymer.minifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

import fr.algofi.maven.plugins.polymer.minifier.commands.DependenciesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.JavascriptMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.model.MiniElements;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerParserException;
import fr.algofi.maven.plugins.polymer.minifier.util.MiniNameProvider;

/**
 * minify a set of web component
 * 
 * @author cjechoux
 *
 */
public class ElementsMinifier {
	private static final Logger LOGGER = LogManager.getLogger(ElementsMinifier.class);
	private final PolymerMinifier polymerMinifier;
	private PolymerParser parser;
	private Iterator<String> componentNameIterator;
	private Map<String, PolymerComponent> components;
	private Document indexDocument;
	private Path importPath;
	private String importHref;
	private String buildHref;
	private String importHtml;

	private Minifier dependenciesMinifier;

	private Minifier javascriptMinifier = new JavascriptMinifier();

	public ElementsMinifier(Minifier minifier, Minifier... minifiers) {

		polymerMinifier = new PolymerMinifier(minifier, minifiers);

		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		parser = new PolymerParser(scriptEngine);

		final MiniNameProvider provider = new MiniNameProvider();
		final List<String> componentNames = provider.provide().stream()
				.collect(Collectors.mapping(name -> "x-" + name, Collectors.toList()));
		componentNameIterator = componentNames.iterator();

		setDependenciesMinifier(minifier, minifiers);

	}

	private void setDependenciesMinifier(Minifier minifier, Minifier... minifiers) {
		if (minifier instanceof DependenciesMinifier) {
			dependenciesMinifier = minifier;
		}
		for (Minifier mini : minifiers) {
			if (mini instanceof DependenciesMinifier) {
				dependenciesMinifier = mini;
				break;
			}
		}
	}

	/**
	 * minimize all elements found into all link@import of the entry point
	 * 
	 * @param index
	 *            main entry point
	 * @return a summary of all minified elements
	 * @throws MinifierException
	 *             thrown if we cannot minify a web component
	 */
	public MiniElements minimize(final Path index) throws MinifierException {

		try {
			final String indexContent = Files.readAllLines(index).stream().collect(Collectors.joining("\n"));
			final Path path = getEntryPointImportPath(index, indexContent);

			final String minifiedContent = minifyElements(path);
			String miniIndex = changeImportLink(indexContent);
			miniIndex = minifyIndexEntryPointDependencies(miniIndex);

			final MiniElements minimized = new MiniElements();
			minimized.setContent(minifiedContent);
			minimized.setIndexContent(miniIndex);
			minimized.setImportBuildHref(buildHref);

			return minimized;
		} catch (IOException e) {
			throw new MinifierException("Cannot read input files", e);
		}
	}

	/**
	 * minify dependencies of the entry point
	 * 
	 * @param indexContent
	 *            content of the entry point (index.html)
	 * @return the single minified content having all dependencies concatenated.
	 * @throws MinifierException
	 *             thrown in case we cannot minimize
	 */
	private String minifyIndexEntryPointDependencies(String indexContent) throws MinifierException {

		if (dependenciesMinifier != null) {
			for (PolymerComponent component : components.values()) {
				final String tagName = component.getName();
				// tag present ?
				boolean isComponentPresent = tagName != null && tagName.trim().length() > 0
						&& indexDocument.getElementsByTag(tagName).size() > 0;
				if (isComponentPresent) {

					final PolymerComponent indexComponent = new PolymerComponent();
					indexComponent.setContent(indexContent);
					indexComponent.setMiniContent(indexContent);

					Collection<PolymerComponent> dependencies = Arrays.asList(component);
					dependenciesMinifier.minimize(indexComponent, dependencies);

					indexContent = indexComponent.getMinifiedContent();
				}
			}

		} else {
			LOGGER.warn("Cannot minify dependency for index entry point: dependency minifier null.");
		}
		return indexContent;
	}

	/**
	 * change the import link with the built import link
	 * 
	 * @param indexContent
	 *            index content to change
	 * @return changed index content
	 */
	private String changeImportLink(String indexContent) {

		buildHref = importHref.replaceFirst(".html", ".build.html");
		indexContent = indexContent.replaceFirst(importHtml, "<link rel=\"import\" href=\"" + buildHref + "\">");

		return indexContent;
	}

	/**
	 * Minify all elements found in the import set
	 * 
	 * @param path
	 *            path of the file that contains only imports
	 * @return content of all concatenated web component minified
	 * @throws MinifierException
	 *             thrown in the case we cannot minify
	 */
	private String minifyElements(final Path path) throws MinifierException {
		// component already appended
		components = getAndOrderAllComponents(path);

		// give to the minifier a collection of all dependencies
		// minifier.addMinifier(dependencyElements);
		// polymerMinifier.addMinifier(new
		// DependenciesMinifier(dependencyElements));

		final StringBuilder builder = new StringBuilder("<html><head></head><body><div hidden=\"\">\n");
		appendAllComponents(components, builder);
		builder.append("</div></body></html>");
		return builder.toString();
	}

	/**
	 * get the 1st import from the index content
	 * 
	 * @param index
	 *            path of the index content
	 * @param indexContent
	 *            index file content
	 * @return path of the 1st import
	 * @throws MinifierException
	 *             thrown in the case no import if found
	 */
	private Path getEntryPointImportPath(final Path index, final String indexContent) throws MinifierException {

		indexDocument = Jsoup.parse(indexContent);
		final Elements links = indexDocument.getElementsByTag("link");

		final List<Path> imports = new ArrayList<>();
		Path parent = index.getParent();

		for (Element link : links) {
			final String rel = link.attr("rel");
			if ("import".equals(rel)) {
				importHref = link.attr("href").trim();
				importHtml = link.outerHtml();
				if (parent == null) {
					if (importHref.startsWith("/")) {
						importHref = "." + importHref;
					}
					importPath = Paths.get(importHref).normalize();
				} else {
					importPath = parent.normalize().resolve(Paths.get(importHref)).normalize();
				}
				imports.add(importPath);
			}
		}

		if (imports.isEmpty() || imports.size() > 1) {
			throw new MinifierException("Only one Import tag is supported");
		} else {
			return imports.get(0);
		}

	}

	/**
	 * minify and append all minified content together
	 * 
	 * @param components
	 *            web component to browse
	 * @param builder
	 *            string builder to concat all web component
	 * @throws MinifierException
	 *             thown in the case we cananot minify
	 */
	private void appendAllComponents(final Map<String, PolymerComponent> components, final StringBuilder builder)
			throws MinifierException {

		// Dependencies that only create new element
		final Collection<PolymerComponent> dependencyElements = components.values().stream()
				.filter(c -> c.getName() != null).collect(Collectors.toList());

		for (String key : components.keySet()) {
			final PolymerComponent component = components.get(key);

			polymerMinifier.minify(component, dependencyElements);

			LOGGER.info("Appending file: " + component.getPath());

			builder.append("<!--");
			builder.append(component.getPath());
			builder.append("-->");
			builder.append(component.getMinifiedContent());
		}

	}

	/**
	 * get a map of all web components : key component path, value
	 * PolymerComponent
	 * 
	 * @param path
	 *            path of the file to read all imports
	 * @return all polymer component parsed within a map
	 * @throws MinifierException
	 *             in the case we cannot read the given page or we cannot parse
	 *             the polymer component
	 */
	private Map<String, PolymerComponent> getAndOrderAllComponents(final Path path) throws MinifierException {
		try {

			final Map<String, PolymerComponent> components = new LinkedHashMap<>();
			final Document document = Jsoup.parse(path.toFile(), Charset.defaultCharset().name());

			Path parent = path.getParent();

			/*
			 * read HTML imports
			 */

			final Elements links = document.getElementsByTag("link");
			for (Element link : links) {
				final String rel = link.attr("rel");
				if ("import".equals(rel)) {
					final String href = link.attr("href");
					final PolymerComponent dependency = readDependency(parent, href);
					appendComponent(dependency, components);

				}
			}

			/*
			 * read script inclusion : <script src="foo.js"></script>
			 */

			final Elements scripts = document.getElementsByTag("script");
			for (Element script : scripts) {
				final String src = script.attr("src");
				if (src != null && src.trim().length() > 0) {
					final PolymerComponent dependency = readDependency(parent, src);
					appendComponent(dependency, components);
				}
			}

			return components;
		} catch (IOException e) {
			throw new MinifierException("Cannot parse the web page " + path, e);
		}
	}

	private PolymerComponent readDependency(final Path parent, final String href) throws MinifierException {
		final Path importPath = getImportPath(parent, href);

		try {
			return parser.read(importPath.toString());
		} catch (PolymerParserException e) {
			throw new MinifierException("Cannot read a dependency" + importPath, e);
		}
	}

	private Path getImportPath(Path parent, final String href) {
		final Path importPath;
		if (parent == null) {
			if (importHref.startsWith("/")) {
				importHref = "." + importHref;
			}
			importPath = Paths.get(importHref).normalize();
		} else {
			importPath = parent.normalize().resolve(Paths.get(href)).normalize();
		}
		// final Path importPath =
		// path.getParent().normalize().resolve(Paths.get(href)).normalize();
		LOGGER.debug("path = " + importPath);
		return importPath;
	}

	/**
	 * concat all web components all together
	 * 
	 * @param component
	 *            the current component
	 * @param components
	 *            component dependencies
	 */
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
