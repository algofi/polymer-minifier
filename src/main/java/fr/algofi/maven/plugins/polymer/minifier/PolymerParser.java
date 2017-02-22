package fr.algofi.maven.plugins.polymer.minifier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	private final String COMPONENT_NAME_SCRIPT_PROLOGUE = "function Polymer( o ) { return o.is ; }\n";

	/**
	 * constructor: create the JAVAScript
	 */
	public PolymerParser(final ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public PolymerComponent read(final String path) throws PolymerParserException  {

		try {
			
			final Document document = Jsoup.parse(new File(path), Charset.defaultCharset().name());
			
			final ScriptPart script = MinifierUtils.extractScript(document);
			
			final PolymerComponent polymer = new PolymerComponent();
			
			String content = Files.readAllLines(Paths.get(path)).stream().collect(Collectors.joining("\n"));
			content = content.replaceAll("<link\\p{Blank}+rel=\"import\"[^>]+>", "");
			polymer.setContent(content.trim());
			
			try {
				final List<PolymerProperty> properties = extractPolymerProperties(script.getScript());
				polymer.setProperties(properties);
				final String name = extractPolymerName(script.getScript());
				polymer.setName(name);
			} catch (ScriptException e) {
				// exception ignored
				LOGGER.warn("cannot parse the file : " + path + " . Cause : " + e.getMessage());
			}
			
			final List<PolymerComponent> imports = extractImports(path, document);
			
			polymer.setPath(path);
			polymer.setImports(imports);
			
			return polymer;
			
		} catch( IOException e ) {
			throw new PolymerParserException("Cannot read the polymer component at " + path, e);
		}
		
	}

	private List<PolymerComponent> extractImports(final String path, final Document document) throws PolymerParserException  {
		final List<PolymerComponent> imports = new ArrayList<>();

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

		return imports;
	}

	private String extractPolymerName(String script) throws ScriptException {
		return (String) scriptEngine.eval(COMPONENT_NAME_SCRIPT_PROLOGUE + script);
	}

	private List<PolymerProperty> extractPolymerProperties(String script) throws ScriptException {
		final List<PolymerProperty> properties = new ArrayList<>();

		final ScriptObjectMirror mirror = (ScriptObjectMirror) scriptEngine.eval(PROPERTIES_SCRIPT_PROLOGUE + script);
		if (mirror != null) {
			final String[] propertiesName = mirror.getOwnKeys(true);
			final List<String> propertiesFound = Arrays.asList(propertiesName);
			
			for( final String propertyName : propertiesFound ) {
				final PolymerProperty property = new PolymerProperty();
				property.setName( propertyName );
				properties.add(property);
			}
			
		}

		return properties;
	}

}
