package fr.algofi.maven.plugins.polymer.minifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Extract properties names of a Polymer web component
 * 
 * @author cjechoux
 *
 */
public class PropertiesExtractor {

	/**
	 * script engine - to evaluate javascript
	 */
	private final ScriptEngine scriptEngine;
	/**
	 * script prologue. We redefine Polymer function to returns the polymer web
	 * component properties object.
	 */
	private final String SCRIPT_PROLOGUE = "function Polymer( o ) { return o.properties ; }\n";

	/**
	 * constructor: create the JAVAScript
	 */
	public PropertiesExtractor(final ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public List<String> extractProperties(String content) throws ScriptException {
		final List<String> properties = new ArrayList<>();

		final String script = MinifierUtils.extractScript(content, SCRIPT_PROLOGUE);
		final ScriptObjectMirror mirror = (ScriptObjectMirror) scriptEngine.eval(script);

		if (mirror != null) {
			final String[] propertiesName = mirror.getOwnKeys(true);
			final List<String> propertiesFound = Arrays.asList(propertiesName);
			properties.addAll(propertiesFound);
		}

		return properties;
	}

}
