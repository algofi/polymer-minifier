package fr.algofi.maven.plugins.polymer.minifier.model;

public class MiniElements {

	private String buildContent;
	private String buildFileName;
	private String buildIndexContent;
	private String miniIndexContent;

	public void setBuildContent(String buildContent) {
		this.buildContent = buildContent;
	}

	public String getBuilldContent() {
		return buildContent;
	}

	public void setBuildFileName(String buildFileName) {
		this.buildFileName = buildFileName;
	}

	public String getBuildFileName() {
		return buildFileName;
	}

	public void setBuildIndexContent(String buildIndexContent) {
		this.buildIndexContent = buildIndexContent;
	}

	public String getBuildIndexContent() {
		return buildIndexContent;
	}

	public String getMiniIndexContent() {
		return miniIndexContent;
	}
	
	public void setMiniIndexContent(String miniIndexContent) {
		this.miniIndexContent = miniIndexContent;
	}
}
