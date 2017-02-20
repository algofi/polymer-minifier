package fr.algofi.maven.plugins.polymer.minifier.model;

public class MinifierException extends Exception {

	private static final long serialVersionUID = 1L;

	public MinifierException(String message) {
		super(message);
	}

	public MinifierException(String message, Throwable cause) {
		super(message, cause);
	}

}
