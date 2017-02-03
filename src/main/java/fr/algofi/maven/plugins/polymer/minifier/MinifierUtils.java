package fr.algofi.maven.plugins.polymer.minifier;

public class MinifierUtils {

	public static String extractScript(String content) {

		int start = content.indexOf("<script>");

		if (start >= 0) {
			int end = content.indexOf("</script>", start);
			start = start + 8;
			if ( end >= start ) {
				return content.substring(start, end).trim();
			} else {
				throw new IllegalArgumentException("No closing script tag found");
			}
		}

		// get the opening tag
		start = content.indexOf("<script ");
		// get the closing position
		start = content.indexOf(">", start + 8);
		int end = content.indexOf("</script>", start);
		if (end >= start) {
			return content.substring(start + 1, end).trim();
		} else {
			throw new IllegalArgumentException("No closing script tag found");
		}

	}

}
