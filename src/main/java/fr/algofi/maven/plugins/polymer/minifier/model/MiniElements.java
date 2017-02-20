package fr.algofi.maven.plugins.polymer.minifier.model;

public class MiniElements {

	private String content;
	private String indexContent;
	private String importBuildHref;

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getIndexContent() {
		return indexContent;
	}

	public void setIndexContent(String indexContent) {
		this.indexContent = indexContent;
	}

	public String getImportBuildHref() {
		return importBuildHref;
	}
	
	public void setImportBuildHref(String importBuildHref) {
		this.importBuildHref = importBuildHref;
	}
}
