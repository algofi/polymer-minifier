package fr.algofi.maven.plugins.polymer.minifier;

public class MinifierUtils {

	public static String extractScript(String content, String scriptPrologue) {

		final StringBuilder scriptBuilder = new StringBuilder(scriptPrologue);
		final String[] lines = content.split("\n|\r|\n\r|\r\n");
		boolean append = false;
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains("Polymer")) {
				append = true;
			}
			if (append && lines[i].contains("</script>")) {
				append = false;
			}
			if (append) {
				scriptBuilder.append(lines[i]).append("\n");
			}
		}

		return scriptBuilder.toString().trim();
	}

}
