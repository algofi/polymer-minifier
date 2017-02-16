package fr.algofi.maven.plugins.polymer.minifier;

public class PolymerProperty {

	private String name;
	private String miniName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMiniName(String miniName) {
		this.miniName = miniName;
	}

	public String getMiniName() {
		return miniName;
	}

	public CharSequence getAttribute() {
		return MinifierUtils.propertyToAttribute(name);
	}

	public CharSequence getMiniAttribute() {
		return MinifierUtils.propertyToAttribute(miniName);
	}
}
