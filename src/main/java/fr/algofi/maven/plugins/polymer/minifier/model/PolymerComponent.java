package fr.algofi.maven.plugins.polymer.minifier.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PolymerComponent {

	private String name;
	private String path;
	private String content;
	private String miniContent;
	
	private List<PolymerProperty> properties = new ArrayList<>();
//	private Map<String, String> minifiedProperties;
	
	private List<PolymerComponent> imports = new ArrayList<>();
	private String miniName;

	public void setProperties(List<PolymerProperty> properties) {
		this.properties = properties;
	}

	public List<PolymerProperty> getProperties() {
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
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * set the content of the polymer element
	 * 
	 * @param content
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

	public void setMiniContent(String miniContent) {
		this.miniContent = miniContent;
	}

//	public Map<String, String> getMiniedProperties() {
//		return minifiedProperties;
//	}
//
//	public void setMinifiedProperties(Map<String, String> minifiedProperties) {
//		this.minifiedProperties = minifiedProperties;
//	}

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PolymerComponent [name=" + name + "]";
	}

	
	
}
