package fr.algofi.maven.plugins.polymer.minifier.commands;

import static fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils.find;
import static fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils.findFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.Environment;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;

/**
 * <p>
 * Replace polymer component properties within javascript code.
 * <p>
 * Example
 * 
 * <pre>
 * Polymer( { is: 'custom-toolbar'
 *   properties: {
 *     userId: String
 *   },
 *   ready: function () {
 *   	this.userId = 'john doe';
 *   }
 * });
 * </pre>
 * 
 * will be minimized to :
 * 
 * <pre>
 * Polymer( { is: 'x-a'
 *   properties: {
 *     a: String
 *   },
 *   ready: function () {
 *   	this.a = 'john doe';
 *   }
 * });
 * </pre>
 * 
 * @author cjechoux
 *
 */
public class JavascriptCompilerMinifier implements Minifier {
	private static final Logger LOGGER = LogManager.getLogger(JavascriptCompilerMinifier.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(PolymerComponent component, final Collection<PolymerComponent> dependencies)
			throws MinifierException {

		try {



			final CompilerOptions options = configureCompiler();

			final Environment env = options.getEnvironment();
			final List<SourceFile> externs = AbstractCommandLineRunner.getBuiltinExterns(env);

			final List<SourceFile> inputs = new ArrayList<>();
			final String path = component.getPath();
			final ScriptPart scriptPart = getInitialScript(component);
			final SourceFile src = SourceFile.fromCode(path, scriptPart.getBulkScript());
			inputs.add(src);


			final Compiler compiler = new Compiler();
			compiler.compile(externs, inputs, options);

			final JSError[] compilerErrors = compiler.getErrors();

			// we update the minified content only when there is no errors.
			if (compilerErrors.length == 0) {
				final String minifiedContent = component.getMinifiedContent().replace(scriptPart.getBulkScript(),
						compiler.toSource());
				component.setMiniContent(minifiedContent);
			} else {

//				if (component.getPath().contains("bower_componentFAKE")) {
//					for (JSError compilerError : compilerErrors) {
//						LOGGER.warn(compilerError.toString());
//					}
//				} else {
					final StringBuilder compilerErrorBuilder = new StringBuilder();
					for (JSError compilerError : compilerErrors) {
						compilerErrorBuilder.append(compilerError.toString()).append("\n");
					}
					throw new MinifierException("Cannot optimize " + component.getPath()
							+ " because errors were found:\n" + compilerErrorBuilder.toString());
//				}
			}
		} catch (IOException e) {

		}
	}

	private ScriptPart getInitialScript(final PolymerComponent component) {
		final String minifiedContent = component.getMinifiedContent();
		final Document document = Jsoup.parse(minifiedContent);
		return MinifierUtils.extractScript(document);
	}

	/**
	 * build the option object for the closure compiler
	 * 
	 * @return
	 */
	private CompilerOptions configureCompiler() {
		final CompilerOptions options = new CompilerOptions();
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
//		CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
		options.setContinueAfterErrors(true);
		options.setLanguageIn(LanguageMode.ECMASCRIPT_2015);
		options.setStrictModeInput(false);
		options.setLanguageOut(LanguageMode.ECMASCRIPT5);
		options.setExternExports(true);
		// options.setWarningLevel(type, CheckLevel.OFF);
		return options;
	}

	

}
