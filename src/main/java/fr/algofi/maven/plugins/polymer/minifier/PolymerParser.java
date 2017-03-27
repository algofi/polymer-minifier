package fr.algofi.maven.plugins.polymer.minifier;

import static fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils.find;
import static fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils.showNode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerParserException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;

/**
 * Extract properties names of a Polymer web component
 * 
 * @author cjechoux
 *
 */
public class PolymerParser {

	private static final Logger LOGGER = LogManager.getLogger(PolymerParser.class);

	private final Map<String, PolymerComponent> components = new HashMap<>();

	public PolymerComponent read(final String path) throws PolymerParserException {

		if (components.containsKey(path)) {
			LOGGER.debug("Get the component from the cache " + path);
			return components.get(path);
		}

		LOGGER.debug("Parsing file: " + path);

		try {

			final String content = readContent(path);
			final Document document = Jsoup.parse(content);

			final ScriptPart script = MinifierUtils.extractScript(document);

			final PolymerComponent polymer = new PolymerComponent();
			polymer.setPath(path);
			// register the component
			components.put(path, polymer);

			// read content
			polymer.setContent(content.trim());

			final String name = extractPolymerName(document);
			polymer.setName(name);

			final boolean isDomModule = document.getElementsByTag("dom-module").size() > 0;

			if (isDomModule) {
				final Map<String, PolymerProperty> properties = extractPolymerProperties(path, script.getBulkScript());
				polymer.setProperties(properties);
			}

			final List<PolymerComponent> imports = extractImports(path, document, polymer);
			polymer.setImports(imports);

			return polymer;

		} catch (IOException e) {
			throw new PolymerParserException("Cannot read the polymer component at " + path, e);
		}

	}

	private List<PolymerComponent> extractImports(final String path, final Document document,
			PolymerComponent component) throws PolymerParserException {
		final List<PolymerComponent> imports = new ArrayList<>();
		String content = component.getContent();

		final Elements links = document.getElementsByTag("link");

		for (Element link : links) {
			final String rel = link.attr("rel");
			if ("import".equals(rel)) {
				final String href = link.attr("href");
				final String importPath = Paths.get(path, "..").normalize().resolve(Paths.get(href)).normalize()
						.toString();

				final PolymerComponent polymer = read(importPath);
				imports.add(polymer);

			}

		}

		final Elements scripts = document.getElementsByTag("script");
		for (Element script : scripts) {
			final String src = script.attr("src");
			if (src != null && src.trim().length() > 0) {
				final String importPath = Paths.get(path, "..").normalize().resolve(Paths.get(src)).normalize()
						.toString();

				// don't parse the component if it was already parsed
				final PolymerComponent polymer = read(importPath);
				imports.add(polymer);
			}
		}

		component.setContent(content);

		return imports;
	}

	private String extractPolymerName(final Document document) {

		final Elements domModules = document.getElementsByTag("dom-module");

		if (domModules.size() == 1) {
			return domModules.get(0).attr("id");
		}

		return null;
	}

	private Map<String, PolymerProperty> extractPolymerProperties(final String path, String script) throws PolymerParserException {
		final Map<String, PolymerProperty> properties = new HashMap<>();

		final Compiler compiler = new Compiler();
		final SourceFile sourceFile = SourceFile.fromCode(path, script);
		final Node root = compiler.parse(sourceFile);
		// showNode("root", root, 0);

		final List <Node> polymerNodes = find(root, Token.NAME, "Polymer", 3);
		// assertEquals(1, polymerNodes.size());
		if (polymerNodes.isEmpty()) {
			return properties;
		}

		// FIXME show errors...

		final List<Node> propertiesNodes = find(polymerNodes.get(0), Token.STRING_KEY, "properties");
		// assertEquals(1, propertiesNodes.size());
		if (propertiesNodes.isEmpty()) {
			return properties;
		}

		showNode("properties", propertiesNodes.get(0), 0);

		final Node propertiesNode = propertiesNodes.get(0);

		System.out.println("Properties found");
		final List<Node> propertiesStringKeyChildsNextsNodes = find(propertiesNode, Token.STRING_KEY, 2);

		final List<PolymerProperty> propertiesList = propertiesStringKeyChildsNextsNodes.stream()
				.filter(n -> n.getParent() != null)
				.filter(n -> n.getParent().getParent().getToken().equals(Token.STRING_KEY))
				.filter(n -> n.getParent().getParent().getString().equals("properties")).map(n -> {
					PolymerProperty property = new PolymerProperty();
					property.setName(n.getString());
					return property;
				}).collect(Collectors.toList());

		for (PolymerProperty property : propertiesList) {
			properties.put(property.getName(), property);
		}

		return properties;

	}

	private static String readContent(final String path) throws IOException {

		try (InputStream inputStream = new FileInputStream(path)) {
			return readContent(inputStream);
		}

	}

	private static String readContent(final InputStream inputStream) throws IOException {
		final StringBuilder builder = new StringBuilder();

		try (Reader streamReader = new InputStreamReader(inputStream)) {
			try (BufferedReader reader = new BufferedReader(streamReader)) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line).append("\n");
				}
			}
		}
		return builder.toString();
	}
}
