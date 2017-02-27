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

import fr.algofi.maven.plugins.polymer.minifier.commands.BlankMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.DependenciesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.HTMLCommentMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.JavascriptMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.NoMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerNameMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerPropertiesMinifier;
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
	private final PolymerMinifier minifier;
	private PolymerParser parser;
	private Iterator<String> componentNameIterator;
	private Map<String, PolymerComponent> components;
	private Document indexDocument;
	private Path importPath;
	private String importHref;
	private String buildHref;
	private String importHtml;

	private Minifier javascriptMinifier = new JavascriptMinifier();

	public ElementsMinifier() {

		final Minifier no = new NoMinifier();
		final Minifier blank = new BlankMinifier();
		final Minifier htmlComments = new HTMLCommentMinifier();
		final Minifier properties = new PolymerPropertiesMinifier();
		final Minifier polymerName = new PolymerNameMinifier();

		minifier = new PolymerMinifier(no, blank, htmlComments, properties, polymerName);

		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		parser = new PolymerParser(scriptEngine);

		final MiniNameProvider provider = new MiniNameProvider();
		final List<String> componentNames = provider.provide().stream()
				.collect(Collectors.mapping(name -> "x-" + name, Collectors.toList()));
		componentNameIterator = componentNames.iterator();
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
			final Path path = getImportPath(index, indexContent);

			final String minifiedContent = minifyElements(path);
			String miniIndex = changeImportLink(indexContent);
			miniIndex = minifyDependencies(miniIndex);

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
	private String minifyDependencies(String indexContent) throws MinifierException {

		for (PolymerComponent component : components.values()) {
			final String tagName = component.getName();
			// tag present ?
			boolean isComponentPresent = tagName != null && tagName.trim().length() > 0
					&& indexDocument.getElementsByTag(tagName).size() > 0;
			if (isComponentPresent) {

				Minifier dependencyMinifier = new DependenciesMinifier(Arrays.asList(component));

				final PolymerComponent indexComponent = new PolymerComponent();
				indexComponent.setContent(indexContent);
				indexComponent.setMiniContent(indexContent);

				dependencyMinifier.minimize(indexComponent);

				indexContent = indexComponent.getMinifiedContent();
			}
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

		// Dependencies that only create new element
		final Collection<PolymerComponent> dependencyElements = components.values().stream()
				.filter(c -> c.getName() != null).collect(Collectors.toList());

		// give to the minifier a collection of all dependencies
		// minifier.addMinifier(dependencyElements);
		minifier.addMinifier(new DependenciesMinifier(dependencyElements));

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

		for (String key : components.keySet()) {
			final PolymerComponent component = components.get(key);

			minifier.minify(component);

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

			final Elements links = document.getElementsByTag("link");
			for (Element link : links) {
				final String rel = link.attr("rel");
				if ("import".equals(rel)) {
					final String href = link.attr("href");
					final Path importPath = path.getParent().normalize().resolve(Paths.get(href)).normalize();
					LOGGER.debug("path = " + importPath);

					try {
						final PolymerComponent component = parser.read(importPath.toString());
						appendComponent(component, components);
					} catch (PolymerParserException e) {
						throw new MinifierException("Cannot read a dependency" + importPath, e);
					}

				}
			}

			return components;
		} catch (IOException e) {
			throw new MinifierException("Cannot parse the web page " + path, e);
		}
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

	/**
	 * enable JS minification
	 * 
	 * @param minifyJavascript true to minify JS
	 */
	public void setMinifyJavascript(boolean minifyJavascript) {
		if (minifyJavascript) {
			minifier.addMinifier(javascriptMinifier);
		} else {
			minifier.removeMinifier(javascriptMinifier);
		}
	}

}
