package fr.algofi.maven.plugins.polymer.minifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;

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

}
