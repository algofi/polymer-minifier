package fr.algofi.maven.plugins.polymer.minifier.util;

import java.io.File;
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

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;

public class MinifierUtils {

	/**
	 * extract the 1st script tag met
	 * 
	 * @param document
	 * @return
	 */
	public static ScriptPart extractScript(Document document) {

		final ScriptPart scriptPart = new ScriptPart();

		final Elements scripts = document.getElementsByTag("script");

		final Element script = scripts.get(0);
		scriptPart.setScript(script.html());

		return scriptPart;

		// int start = document.indexOf("<script>");
		//
		// if (start >= 0) {
		// int end = document.indexOf("</script>", start);
		// start = start + 8;
		// if (end >= start) {
		// final String script = document.substring(start, end).trim();
		// scriptPart.setScript(script);
		// scriptPart.setStart(start);
		// scriptPart.setEnd(end);
		// return scriptPart;
		// } else {
		// throw new IllegalArgumentException("No closing script tag found");
		// }
		// }
		//
		// // get the opening tag
		// start = document.indexOf("<script ");
		// // get the closing position
		// start = document.indexOf(">", start + 8);
		// int end = document.indexOf("</script>", start);
		// if (end >= start) {
		// final String script = document.substring(start + 1, end).trim();
		// scriptPart.setScript(script);
		// scriptPart.setStart(start);
		// scriptPart.setEnd(end);
		// return scriptPart;
		// } else {
		// throw new IllegalArgumentException("No closing script tag found");
		// }

	}

	public static void minifyJavascript(final String path, final String script) {
		final Compiler compiler = new Compiler();

		final CompilerOptions options = new CompilerOptions();
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

		final List<SourceFile> externs = Collections.emptyList();
		final List<SourceFile> inputs = new ArrayList<>();
		inputs.add(SourceFile.fromFile(new File(path + ".js")));

		/* final Result result = */compiler.compile(externs, inputs, options);

		System.out.println(compiler.toSource());
	}

	/**
	 * convert a property name to an attribute
	 * 
	 * @param name
	 * @return
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

	public static String minifyName(String content, String name, String miniName) {

		if (miniName != null) {

			// minify polymer component name

			// 1) replace <dom-module > id attribute
			content = content.replaceAll("<dom-module\\p{Blank}+id=\"" + name + "\"",
					"<dom-module id=\"" + miniName + "\"");

			// 2) minify javascript polymer component
			content = content.replaceAll("is:\\p{Blank}+['\"]" + name + "['\"]", "is:'" + miniName + "'");

		}

		return content;
	}

	public static String minifyDependency(String content, PolymerComponent dependency) {
		final String dependencyName = dependency.getName();
		final String dependencyMiniName = dependency.getMiniName();
		// replace HTML opening tags
		content = content.replaceAll("<" + dependencyName, "\\<" + dependencyMiniName);
		// replace HTML closing tags
		content = content.replaceAll("</" + dependencyName + ">", "</" + dependencyMiniName + ">");
		// replace custom element attributes

		// final Pattern pattern = Pattern.compile("(<" + dependencyMiniName
		// + "([a-z\\- ]+\\$?=['\"][^'\"]*['\"])*>.*</" + dependencyMiniName
		// + ">)");
		final Pattern pattern = Pattern
				.compile("(<" + dependencyMiniName + ".*>[\n\\r.]*</" + dependencyMiniName + ">)");
		final Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			final String find = matcher.group(1);
			System.out.println("GROUP   = " + find);

			String miniTag = find;
			for (PolymerProperty property : dependency.getProperties()) {
				miniTag = miniTag.replace(property.getAttribute() + "=", property.getMiniAttribute() + "=");
				miniTag = miniTag.replace(property.getAttribute() + "$=", property.getMiniAttribute() + "$=");
			}

			content = content.replace(find, miniTag);
		}


		content = content.replaceAll("document.createElement(\\p{Blank}*['\"]" + dependencyName + "['\"]\\p{Blank}*)", "document.createElement(\\p{Blank}*['\"]" + dependencyMiniName + "['\"]\\p{Blank}*)");

		return content;
	}

}
