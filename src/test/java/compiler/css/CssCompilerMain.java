package compiler.css;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import com.yahoo.platform.yui.compressor.CssCompressor;

public class CssCompilerMain {

	public static void main(String[] args) throws IOException {

		final StringBuilder cssCode = new StringBuilder();
		cssCode.append("body {").append("\n");
		cssCode.append("    color: red;").append("\n");
		cssCode.append("    @apply(--foo-bar-cc);").append("\n");
		cssCode.append("    background-color: --paper-pink-500;").append("\n");
		cssCode.append("}").append("\n");

		final Reader in = new StringReader(cssCode.toString());
		final CssCompressor compressor = new CssCompressor(in);

		final StringWriter writer = new StringWriter();
		final int linebreakpos = -1;

		compressor.compress(writer, linebreakpos);

		System.out.println(writer.toString());
	}
}
