package compiler;

import static com.google.javascript.jscomp.SourceFile.fromCode;
import static com.google.javascript.jscomp.SourceFile.fromInputStream;
import static java.nio.charset.Charset.defaultCharset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.Environment;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;

public class CompilerMain {

	public static void main(String[] args) throws URISyntaxException, IOException {

		final String script = readScript();

		final Compiler compiler = new Compiler();
		final CompilerOptions options = new CompilerOptions();
		// CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
		CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
		options.setContinueAfterErrors(true);
		options.setLanguageIn(LanguageMode.ECMASCRIPT6_STRICT);
		options.setLanguageOut(LanguageMode.ECMASCRIPT5);
		options.setExternExports(true);

		System.out.println("Compiling:\n" + script);
		System.out.println("------------------------------");

		final Environment env = options.getEnvironment();
		final List<SourceFile> externs = AbstractCommandLineRunner.getBuiltinExterns(env);

		final List<SourceFile> inputs = new ArrayList<>();
		final SourceFile src = fromCode("stdin.txt", script);
		inputs.add(src);
		final Result result = compiler.compile(externs, inputs, options);

		System.out.println("------------------------------");

		final JSError[] errors = result.errors;
		if (errors.length > 0) {
			for (JSError error : errors) {
				System.err.println(error.toString());
			}
		} else {
			System.out.println(compiler.toSource());
		}

	}

	private static SourceFile externSourceFileES6(final String filename) throws IOException {

		final InputStream inputstream = CompilerMain.class
				.getResourceAsStream("/com/google/javascript/jscomp/js/" + filename);

		return fromInputStream(filename, inputstream, defaultCharset());
	}

	private static String readScript() throws URISyntaxException, IOException {
		final URI uri = CompilerMain.class.getResource("/compiler/example1.js").toURI();
		final Path path = Paths.get(uri);
		return Files.readAllLines(path).stream().collect(Collectors.joining("\n"));
	}

}
