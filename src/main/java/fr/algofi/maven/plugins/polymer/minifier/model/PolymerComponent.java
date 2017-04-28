package fr.algofi.maven.plugins.polymer.minifier.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolymerComponent {

	private String name;
	private String path;
	private String content;
	private String miniContent;

	private Map<String, PolymerProperty> properties = new HashMap<>();

	private List<PolymerComponent> imports = new ArrayList<>();
	private String miniName;

	public void setProperties(Map<String, PolymerProperty> properties) {
		this.properties = properties;
	}

	public Map<String, PolymerProperty> getProperties() {
		return properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * path of the polymer component
	 * 
	 * @param path
	 *            path of the component
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * set the content of the polymer element
	 * 
	 * @param content
	 *            original content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getMinifiedContent() {
		return miniContent;
	}

	public synchronized void setMiniContent(String miniContent) {
		this.miniContent = miniContent;
	}

	public List<PolymerComponent> getImports() {
		return imports;
	}

	public void setImports(List<PolymerComponent> imports) {
		this.imports = imports;
	}

	public String getPath() {
		return path;
	}

	public void setMiniName(String miniName) {
		this.miniName = miniName;
	}

	public String getMiniName() {
		return miniName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PolymerComponent [name=" + name + "]";
	}

}
