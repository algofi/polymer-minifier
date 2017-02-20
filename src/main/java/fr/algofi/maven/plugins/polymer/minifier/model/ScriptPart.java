package fr.algofi.maven.plugins.polymer.minifier.model;

/**
 * represent a script extracted from HTML
 * 
 * @author cjechoux
 *
 */
public class ScriptPart {

	private String script;
	/**
	 * start position of the script (inclusive)
	 */
	private int start;
	/**
	 * end position of the script (exclusive)
	 */
	private int end;

	public void setScript(String script) {
		this.script = script;
	}

	public String getScript() {
		return script;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStart() {
		return start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getEnd() {
		return end;
	}
}
