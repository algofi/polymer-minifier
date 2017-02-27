package fr.algofi.maven.plugins.polymer.minifier.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;

import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;

public class MinifierUtils {

	/**
	 * extract the 1st script tag met
	 * 
	 * @param document
	 *            HTML document to parse
	 * @return script element
	 */
	public static ScriptPart extractScript(Document document) {

		final ScriptPart scriptPart = new ScriptPart();

		final Elements scripts = document.getElementsByTag("script");

		final Element script = scripts.get(0);
		scriptPart.setScript(script.html());
		String bulkScript = script.outerHtml();
		bulkScript = bulkScript.substring(bulkScript.indexOf(">") + 1);
		bulkScript = bulkScript.replace("</script>", "");
		scriptPart.setBulkScript(bulkScript);

		return scriptPart;
	}

	/**
	 * Minimize javascript
	 * 
	 * @param path
	 *            file path to minimize
	 * @param script
	 *            script content to minimize
	 * @return the minimized script
	 */
	public static String minifyJavascript(final String path, final String script) {
		final Compiler compiler = new Compiler();

		final CompilerOptions options = new CompilerOptions();
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

		final List<SourceFile> externs = Collections.emptyList();
		final List<SourceFile> inputs = new ArrayList<>();
		// inputs.add(SourceFile.fromFile(new File(path + ".js")));
		SourceFile src = SourceFile.fromCode(path, script);
		inputs.add(src);

		/* final Result result = */compiler.compile(externs, inputs, options);

		return compiler.toSource().trim();
	}

	/**
	 * convert a property name (ie sessionId) to an attribute (ie session-id)
	 * 
	 * @param name
	 *            property name
	 * @return HTML attibute name
	 */
	public static String propertyToAttribute(String name) {

		if (name == null) {
			throw new IllegalArgumentException("property name should not be null");
		}

		final StringBuilder builder = new StringBuilder();

		final char[] chars = name.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			final Character c = chars[i];
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					builder.append('-');
				}
				builder.append(Character.toLowerCase(c));
			} else {
				builder.append(c);
			}
		}

		return builder.toString();
	}

	/**
	 * remove all content that match the given pattern
	 * 
	 * @param input
	 *            text to read
	 * @param pattern
	 *            pattern that must be replaced with empty string
	 * @return new content where pattern were replaced by empty string
	 */
	public static String removePattern(final String input, final Pattern pattern) {
		final Matcher matcher = pattern.matcher(input);

		final List<Integer> starts = new ArrayList<>();
		final List<Integer> ends = new ArrayList<>();
		while (matcher.find()) {
			starts.add(matcher.start());
			ends.add(matcher.end());
		}

		if (starts.isEmpty() || ends.isEmpty()) {
			return input;
		}

		final StringBuilder builder = new StringBuilder();

		builder.append(input.substring(0, starts.get(0)));
		for (int i = 0; i < starts.size() - 1; i++) {
			int start = ends.get(i);
			int end = starts.get(i + 1) - 1;
			if (end < start) {
				end = start;
			}
			builder.append(input.substring(start, end));
		}
		builder.append(input.substring(ends.get(ends.size() - 1)));

		return builder.toString();
	}

	/**
	 * find a list HTML code that involves the HTML tag
	 * 
	 * @param tagName
	 *            name of the tag to find
	 * @param content
	 *            HTML content to read
	 * @return list of all tags content
	 */
	public static List<String> findHtmlTags(final String tagName, final String content) {
		final List<String> tags = new ArrayList<>();

		int start = 0;
		int end = 0;

		while ((start = content.indexOf("<" + tagName, end)) != -1
				&& (end = content.indexOf("</" + tagName + ">", start)) != -1) {
			final String html = content.substring(start, end + tagName.length() + 3);
			tags.add(html);
		}

		return tags;
	}

}
