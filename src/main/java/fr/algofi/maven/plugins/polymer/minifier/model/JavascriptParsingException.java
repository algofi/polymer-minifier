package fr.algofi.maven.plugins.polymer.minifier.model;

import com.google.javascript.jscomp.JSError;

public class JavascriptParsingException extends PolymerParserException {

	private static final long serialVersionUID = 1L;

	public JavascriptParsingException(String message, JSError[] errors) {
		super(message, null);
	}

}
