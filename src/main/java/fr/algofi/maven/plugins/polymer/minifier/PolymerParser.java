package fr.algofi.maven.plugins.polymer.minifier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerParserException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Extract properties names of a Polymer web component
 * 
 * @author cjechoux
 *
 */
public class PolymerParser {

	private static final Logger LOGGER = LogManager.getLogger(PolymerParser.class);

	/**
	 * script engine - to evaluate javascript
	 */
	private final ScriptEngine scriptEngine;
	/**
	 * script prologue. We redefine Polymer function to returns the polymer web
	 * component properties object.
	 */
	private final String PROPERTIES_SCRIPT_PROLOGUE = "function Polymer( o ) { return o.properties ; }\n";
	// private final String COMPONENT_NAME_SCRIPT_PROLOGUE = "function Polymer(o
	// ) { return o.is ; }\n";

	/**
	 * constructor: create the JAVAScript
	 * 
	 * @param scriptEngine
	 *            engine to parse Javascript
	 */
	public PolymerParser(final ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public PolymerComponent read(final String path) throws PolymerParserException {

		try {

			final Document document = Jsoup.parse(new File(path), Charset.defaultCharset().name());

			final ScriptPart script = MinifierUtils.extractScript(document);

			final PolymerComponent polymer = new PolymerComponent();
			String content = Files.readAllLines(Paths.get(path)).stream().collect(Collectors.joining("\n"));
			polymer.setContent(content.trim());

			final List<PolymerComponent> imports = extractImports(path, document, polymer);


			// final Compiler compiler = new Compiler();
			// final SourceFile src = SourceFile.fromCode(path,
			// script.getBulkScript());
			// final Node root = compiler.parse(src);

			// if (compiler.getErrorCount() > 0) {
			// String message =
			// Arrays.asList(compiler.getErrors()).stream().map(err ->
			// err.toString())
			// .collect(Collectors.joining("\n"));
			// LOGGER.error(message);
			// // throw new PolymerParserException(message);
			// }

			LOGGER.debug("Parsing file: " + path);
			// showNode("root", root, 0);

			try {
				final Map<String, PolymerProperty> properties = extractPolymerProperties(script.getBulkScript());
				polymer.setProperties(properties);
				final String name = extractPolymerName(document);
				polymer.setName(name);
			} catch (ScriptException e) {
				// exception ignored
				LOGGER.warn("cannot parse the file : " + path + " . Cause : " + e.getMessage());
			}

			polymer.setPath(path);
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

				content = content.replace(link.outerHtml(), "");
			}

		}

		final Elements scripts = document.getElementsByTag("script");
		for (Element script : scripts) {
			final String src = script.attr("src");
			if (src != null && src.trim().length() > 0) {
				final String importPath = Paths.get(path, "..").normalize().resolve(Paths.get(src)).normalize()
						.toString();

				final PolymerComponent polymer = read(importPath);
				imports.add(polymer);
				content = content.replace(script.outerHtml(), "");
				content = content.replace(script.outerHtml().replace('"', '\''), "");
			}
		}
		
		component.setContent(content);

		return imports;
	}

	private String extractPolymerName(final Document document) throws ScriptException {

		final Elements domModules = document.getElementsByTag("dom-module");

		if (domModules.size() == 1) {
			return domModules.get(0).attr("id");
		}

		return null;
	}

	private Map<String, PolymerProperty> extractPolymerProperties(
			/* Node root */ final String script) throws ScriptException {
		final Map<String, PolymerProperty> properties = new HashMap<>();

		// final List<Node> polymerNodes = find(root, Token.NAME, "Polymer");
		// if (polymerNodes.size() == 1) {
		// final Node polymerNode = polymerNodes.get(0);
		// final List<Node> propertiesNodes = find(polymerNode,
		// Token.STRING_KEY, "properties");
		// if (propertiesNodes.size() == 1) {
		// final List<Node> propertyEntryNodes =
		// find(propertiesNodes.get(0).getFirstChild(), Token.STRING_KEY, 1);
		// propertyEntryNodes.stream().forEach(node -> {
		// final String propertyName = node.getString();
		// final PolymerProperty property = new PolymerProperty();
		// property.setName(propertyName);
		// properties.put(propertyName, property);
		// });
		//
		// }
		// }

		final ScriptObjectMirror mirror = (ScriptObjectMirror) scriptEngine.eval(PROPERTIES_SCRIPT_PROLOGUE + script);
		if (mirror != null) {
			final String[] propertiesName = mirror.getOwnKeys(true);
			final List<String> propertiesFound = Arrays.asList(propertiesName);

			for (final String propertyName : propertiesFound) {
				final PolymerProperty property = new PolymerProperty();
				property.setName(propertyName);
				properties.put(propertyName, property);
			}

		}

		return properties;
	}

}
