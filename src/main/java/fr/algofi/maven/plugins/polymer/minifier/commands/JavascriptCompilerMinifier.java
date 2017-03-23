package fr.algofi.maven.plugins.polymer.minifier.commands;

import static fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils.find;
import static fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils.findFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
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

		final String path = component.getPath();

		String minifiedContent = component.getMinifiedContent();
		final Document document = Jsoup.parse(minifiedContent);
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);

		final Compiler compiler = new Compiler();

		final CompilerOptions options = new CompilerOptions();
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
		options.setContinueAfterErrors(true);
//		options.setWarningLevel(type, CheckLevel.OFF);
		
		
		final List<SourceFile> externs = Collections.emptyList();
		final List<SourceFile> inputs = new ArrayList<>();
		SourceFile src = SourceFile.fromCode(path, scriptPart.getBulkScript());
		// inputs.add(src);

		final Node root = compiler.parse(src);
		final List<Node> createElementNodes = find(root, Token.STRING, "createElement");

		for (Node createElementNode : createElementNodes) {
			minifyCreatedElements(createElementNode, dependencies);
		}

		compiler.compile(externs, inputs, options);

		final JSError[] compilerErrors = compiler.getErrors();

		// we update the minified content only when there is no errors.
		if (compilerErrors.length == 0) {
			minifiedContent = minifiedContent.replace(scriptPart.getBulkScript(), compiler.toSource());
			component.setMiniContent(minifiedContent);
		} else {

			if (component.getPath().contains("bower_component")) {
				for (JSError compilerError : compilerErrors) {
					LOGGER.warn(compilerError.toString());
				}
			} else {
				final StringBuilder compilerErrorBuilder = new StringBuilder();
				for (JSError compilerError : compilerErrors) {
					compilerErrorBuilder.append(compilerError.toString()).append("\n");
				}
				throw new MinifierException("Cannot optimize " + component.getPath() + " because errors were found:\n"
						+ compilerErrorBuilder.toString());

			}

		}
	}

	private void minifyCreatedElements(final Node createElementNode, final Collection<PolymerComponent> dependencies) {

		final Node elementTagNode = createElementNode.getParent().getNext();
		final String originalComponentName = elementTagNode.getString();

		for (PolymerComponent dependency : dependencies) {

			if (originalComponentName.equals(dependency.getName())) {

				minifyCreatedElement(createElementNode, elementTagNode, dependency);
			}
		}

	}

	private void minifyCreatedElement(final Node createElementNode, final Node elementTagNode,
			PolymerComponent dependency) {
		// change createElement( 'my-custom-element' )
		// to createElement( 'x-k' )
		elementTagNode.setString(dependency.getMiniName());
		// variable name assigned with the created Element
		final String variableName = createElementNode.getGrandparent().getParent().getString();

		// get the function scope where this variable where created
		final Node functionNode = findFunction(createElementNode);

		// check for name element
		final List<Node> variableNodes = find(functionNode, Token.NAME, variableName);

		for (Node variableNode : variableNodes) {

			final Node propertyNode = variableNode.getNext();
			if (propertyNode != null && propertyNode.getGrandparent() != null
					&& propertyNode.getGrandparent().getToken() == Token.ASSIGN) {

				final String propertyName = propertyNode.getString();
				final PolymerProperty property = dependency.getProperties().get(propertyName);

				final String miniPropertyName = property.getMiniName();
				propertyNode.setString(miniPropertyName);

			}
		}
	}

}
