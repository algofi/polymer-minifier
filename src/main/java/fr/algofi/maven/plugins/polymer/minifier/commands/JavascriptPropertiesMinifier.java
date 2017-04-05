package fr.algofi.maven.plugins.polymer.minifier.commands;

import static fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils.find;
import static fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils.findFunction;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.Environment;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;
import fr.algofi.maven.plugins.polymer.minifier.util.JavascriptUtils;
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
public class JavascriptPropertiesMinifier implements Minifier {
	private static final Logger LOGGER = LogManager.getLogger(JavascriptPropertiesMinifier.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(PolymerComponent component, final Collection<PolymerComponent> dependencies)
			throws MinifierException {

		try {
			LOGGER.info("Minifying JS properties for " + component.getPath());

			final ScriptPart scriptPart = getInitialScript(component);

			final String path = component.getPath();
			final Compiler compiler = new Compiler();

			final CompilerOptions options = configureCompiler();

			final Environment env = options.getEnvironment();
			final List<SourceFile> externs = AbstractCommandLineRunner.getBuiltinExterns(env);

			final SourceFile src = SourceFile.fromCode(path, scriptPart.getBulkScript());

			compiler.initOptions(options);

			final Node root = compiler.parse(src);
			final List<Node> createElementNodes = find(root, Token.STRING, "createElement");

			for (Node createElementNode : createElementNodes) {
				minifyCreatedElements(createElementNode, dependencies);
			}

			JavascriptUtils.showNode("ROOT", root, 0);

			String minifiedContent = component.getMinifiedContent();
			minifiedContent = minifiedContent.replace(scriptPart.getBulkScript(), compiler.toSource(root));

			component.setMiniContent(minifiedContent);

		} catch (IOException e) {
			throw new MinifierException("Cannot minimize the script " + component.getPath(), e);
		}

	}

	private void minifyCreatedElements(final Node createElementNode, final Collection<PolymerComponent> dependencies) {

		final Node elementTagNode = createElementNode.getParent().getNext();
		if (Token.STRING.equals(elementTagNode.getToken())) {
			final String originalComponentName = elementTagNode.getString();

			for (PolymerComponent dependency : dependencies) {

				if (originalComponentName.equals(dependency.getName())) {

					minifyCreatedElement(createElementNode, elementTagNode, dependency);
				}
			}
		}

	}

	private void minifyCreatedElement(final Node createElementNode, final Node elementTagNode,
			PolymerComponent dependency) {
		// change createElement( 'my-custom-element' )
		// to createElement( 'x-k' )
		if (dependency.getMiniName() != null) {
			elementTagNode.setString(dependency.getMiniName());
		}
		/*
		 * if the dependency has no properties we don't need to find the
		 * variable that is bound to `document.createElement`
		 */
		if (!dependency.getProperties().isEmpty()) {
			minifyCreatedElementProperties(createElementNode, dependency);
		}
	}

	/**
	 * if the dependency has properties, then we retrieve the variable bound to
	 * `document.createElement` and minify all variable bindings
	 * 
	 * @param createElementNode
	 * @param dependency
	 */
	private void minifyCreatedElementProperties(final Node createElementNode, PolymerComponent dependency) {
		// variable name assigned with the created Element
		final Node variableNameNode = createElementNode.getGrandparent().getParent();
		/*
		 * if the `document.createElement` is not assigned to a variable
		 */
		if (!variableNameNode.getToken().equals(Token.NAME)) {
			return;
		}

		final String variableName = variableNameNode.getString();

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

		// NO OPTIMIZATION

		options.setContinueAfterErrors(true);
		options.setLanguageIn(LanguageMode.ECMASCRIPT_2015);
		options.setStrictModeInput(false);
		options.setLanguageOut(LanguageMode.ECMASCRIPT5);
		options.setExternExports(true);
		// options.setWarningLevel(type, CheckLevel.OFF);
		return options;
	}

}
