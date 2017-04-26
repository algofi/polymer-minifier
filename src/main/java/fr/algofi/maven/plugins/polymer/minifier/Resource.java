package fr.algofi.maven.plugins.polymer.minifier;

import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

public class Resource {

	private List<String> includes;
	private List<String> excludes;


	@Parameter(name = "minifyJavascript", defaultValue = "true")
	private boolean minifyJavascript;

	@Parameter(name = "whiteOnlyJavascript", defaultValue = "true")
	private boolean whiteOnlyJavascript;

	@Parameter(name = "compileJavascript", defaultValue = "true")
	private boolean compileJavascript;

	@Parameter(name = "gzipElements", defaultValue = "true")
	private boolean gzipElements;

	@Parameter(name = "minifyBlanks", defaultValue = "true")
	private boolean minifyBlanks;

	@Parameter(name = "minifyHtmlComments", defaultValue = "true")
	private boolean minifyHtmlComments;

	@Parameter(name = "minifyProperties", defaultValue = "false")
	private boolean minifyProperties;

	@Parameter(name = "minifyPolymerName", defaultValue = "true")
	private boolean minifyPolymerName;

	@Parameter(name = "writeSingleFile", defaultValue = "true")
	private boolean writeSingleFile;

	/**
	 * minify CSS styles includes inside an HTML
	 */
	@Parameter(name = "minifyStyles", defaultValue = "true")
	private boolean minifyStyles;

	/**
	 * @return the includes
	 */
	public List<String> getIncludes() {
		return includes;
	}

	/**
	 * @param includes the includes to set
	 */
	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	/**
	 * @return the excludes
	 */
	public List<String> getExcludes() {
		return excludes;
	}

	/**
	 * @param excludes the excludes to set
	 */
	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	/**
	 * @return the minifyJavascript
	 */
	public boolean isMinifyJavascript() {
		return minifyJavascript;
	}

	/**
	 * @param minifyJavascript the minifyJavascript to set
	 */
	public void setMinifyJavascript(boolean minifyJavascript) {
		this.minifyJavascript = minifyJavascript;
	}

	/**
	 * @return the whiteOnlyJavascript
	 */
	public boolean isWhiteOnlyJavascript() {
		return whiteOnlyJavascript;
	}

	/**
	 * @param whiteOnlyJavascript the whiteOnlyJavascript to set
	 */
	public void setWhiteOnlyJavascript(boolean whiteOnlyJavascript) {
		this.whiteOnlyJavascript = whiteOnlyJavascript;
	}

	/**
	 * @return the compileJavascript
	 */
	public boolean isCompileJavascript() {
		return compileJavascript;
	}

	/**
	 * @param compileJavascript the compileJavascript to set
	 */
	public void setCompileJavascript(boolean compileJavascript) {
		this.compileJavascript = compileJavascript;
	}

	/**
	 * @return the gzipElements
	 */
	public boolean isGzipElements() {
		return gzipElements;
	}

	/**
	 * @param gzipElements the gzipElements to set
	 */
	public void setGzipElements(boolean gzipElements) {
		this.gzipElements = gzipElements;
	}

	/**
	 * @return the minifyBlanks
	 */
	public boolean isMinifyBlanks() {
		return minifyBlanks;
	}

	/**
	 * @param minifyBlanks the minifyBlanks to set
	 */
	public void setMinifyBlanks(boolean minifyBlanks) {
		this.minifyBlanks = minifyBlanks;
	}

	/**
	 * @return the minifyHtmlComments
	 */
	public boolean isMinifyHtmlComments() {
		return minifyHtmlComments;
	}

	/**
	 * @param minifyHtmlComments the minifyHtmlComments to set
	 */
	public void setMinifyHtmlComments(boolean minifyHtmlComments) {
		this.minifyHtmlComments = minifyHtmlComments;
	}

	/**
	 * @return the minifyProperties
	 */
	public boolean isMinifyProperties() {
		return minifyProperties;
	}

	/**
	 * @param minifyProperties the minifyProperties to set
	 */
	public void setMinifyProperties(boolean minifyProperties) {
		this.minifyProperties = minifyProperties;
	}

	/**
	 * @return the minifyPolymerName
	 */
	public boolean isMinifyPolymerName() {
		return minifyPolymerName;
	}

	/**
	 * @param minifyPolymerName the minifyPolymerName to set
	 */
	public void setMinifyPolymerName(boolean minifyPolymerName) {
		this.minifyPolymerName = minifyPolymerName;
	}

	/**
	 * @return the writeSingleFile
	 */
	public boolean isWriteSingleFile() {
		return writeSingleFile;
	}

	/**
	 * @param writeSingleFile the writeSingleFile to set
	 */
	public void setWriteSingleFile(boolean writeSingleFile) {
		this.writeSingleFile = writeSingleFile;
	}

	/**
	 * @return the minifyStyles
	 */
	public boolean isMinifyStyles() {
		return minifyStyles;
	}

	/**
	 * @param minifyStyles the minifyStyles to set
	 */
	public void setMinifyStyles(boolean minifyStyles) {
		this.minifyStyles = minifyStyles;
	}

	
}
