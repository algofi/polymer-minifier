package fr.algofi.maven.plugins.polymer.minifier.model;

/**
 * represent a script extracted from HTML
 * 
 * @author cjechoux
 *
 */
public class ScriptPart {

	private String script;
	private String bulkScript;

	public void setScript(String script) {
		this.script = script;
	}

	public String getScript() {
		return script;
	}

	public void setBulkScript(String bulkScript) {
		this.bulkScript = bulkScript;
	}

	public String getBulkScript() {
		return bulkScript;
	}

}
