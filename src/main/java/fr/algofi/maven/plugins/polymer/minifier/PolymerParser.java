package fr.algofi.maven.plugins.polymer.minifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Extract properties names of a Polymer web component
 * 
 * @author cjechoux
 *
 */
public class PolymerParser {

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

	public PolymerComponent read(String path) throws IOException {
		final byte[] bytes = Files.readAllBytes(Paths.get(path));
		final String content = new String(bytes, Charset.defaultCharset());

		final String script = MinifierUtils.extractScript(content);

		final PolymerComponent polymer = new PolymerComponent();

		try {
			final List<String> properties = extractPolymerProperties(script);
			polymer.setProperties(properties);
			final String name = extractPolymerName(script);
			polymer.setName(name);
		} catch (ScriptException e) {
			// exception ignored
			System.out.println("[WARN] cannot parse the file : " + path + " . Cause : " + e.getMessage());
		}

		final List<PolymerComponent> imports = extractImports(path, content);

		polymer.setPath(path);
		polymer.setImports(imports);

		return polymer;
	}

	private List<PolymerComponent> extractImports(String path, String content) throws IOException {
		final List<PolymerComponent> imports = new ArrayList<>();

		final Pattern pattern = Pattern.compile("<link rel=\"import\" href=\"([^\"]+)\"");
		final Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			final String href = matcher.group(1);
			// TODO import relative to the current path
			final String importPath = Paths.get(path, "..").normalize().resolve(Paths.get(href)).normalize().toString();

			final PolymerComponent polymer = read(importPath);
			imports.add(polymer);
		}

		return imports;
	}

	private String extractPolymerName(String script) throws ScriptException {
		return (String) scriptEngine.eval(COMPONENT_NAME_SCRIPT_PROLOGUE + script);
	}

	private List<String> extractPolymerProperties(String script) throws ScriptException {
		final List<String> properties = new ArrayList<>();

		final ScriptObjectMirror mirror = (ScriptObjectMirror) scriptEngine.eval(PROPERTIES_SCRIPT_PROLOGUE + script);
		if (mirror != null) {
			final String[] propertiesName = mirror.getOwnKeys(true);
			final List<String> propertiesFound = Arrays.asList(propertiesName);
			properties.addAll(propertiesFound);
		}

		return properties;
	}

}
