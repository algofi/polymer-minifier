package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
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
public class JavascriptPropertiesMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(PolymerComponent component) throws MinifierException {

		final String path = component.getPath();

		final Document document = Jsoup.parse(component.getMinifiedContent());
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);

		final Compiler compiler = new Compiler();

		final CompilerOptions options = new CompilerOptions();
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

		final List<SourceFile> externs = Collections.emptyList();
		final List<SourceFile> inputs = new ArrayList<>();
		SourceFile src = SourceFile.fromCode(path, scriptPart.getBulkScript());
		inputs.add(src);

		compiler.compile(externs, inputs, options);

		// System.out.println(compiler.getAstDotGraph());

		final Node root = compiler.parse(src);
		System.out.println("Show nodes :");
		showNode("root ", root, 0);

		// System.out.println("-----------------");
		// System.out.println(compiler.toSource().trim());

		System.out.println("-----------------");
		// FIXME loop over the list
		final List<Node> createElementNodes = find(root, Token.STRING, "createElement");
		System.out.println("found length " + createElementNodes.size());
		final Node createElementNode = createElementNodes.get(0);
		// System.out.println( "element : " + createElementNode );
		// System.out.println( "getprop : " + createElementNode.getParent() );
		// System.out.println( "previous : " + createElementNode.getPrevious()
		// );
		// System.out.println( "next : " + createElementNode.getNext() );
		// System.out.println( "parent.next : " +
		// createElementNode.getParent().getNext() );
		// System.out.println( "call : " + createElementNode.getGrandparent() );
		// System.out.println( "name : " +
		// createElementNode.getGrandparent().getParent() );

		final String variableName = createElementNode.getGrandparent().getParent().getString();

		// get the function scope
		final Node functionNode = findFunction(createElementNode);

		// check for name element
		final List<Node> variableNodes = find(functionNode, Token.NAME, variableName);
		System.out.println("Variables");
		for (Node variableNode : variableNodes) {
			System.out.println(" first child  " + variableNode.getFirstChild());
			System.out.println("  next        " + variableNode.getNext());
			System.out.println("  parent      " + variableNode.getParent());
			System.out.println("  parent      " + variableNode.getGrandparent());
			System.out.println("-------------------");
			//new Node(nodeType, child, lineno, charno)
		}
	}

	private Node findFunction(Node node) {

		if (node == null) {
			return null;
		} else if (node.getToken() == Token.FUNCTION) {
			return node;
		}

		return findFunction(node.getParent());
	}

	private List<Node> find(final Node node, final Token token, final String expression) {
		final List<Node> nodes = new ArrayList<>();

		if (node != null) {

			if (node.getToken() == token) {
				if (expression.equals(node.getString())) {
					nodes.add(node);
				}
			}

			nodes.addAll(find(node.getFirstChild(), token, expression));
			nodes.addAll(find(node.getNext(), token, expression));
		}

		return nodes;
	}

	private void showNode(final String type, final Node node, final int offset) {
		if (node != null) {
			System.out.println(type.toUpperCase() + " - " + offset(offset) + node);
			showNode("child", node.getFirstChild(), offset + 1);
			showNode("next ", node.getNext(), offset + 1);
		}
	}

	private static String offset(int offset) {
		final StringBuilder builder = new StringBuilder();
		IntStream.range(0, offset).forEach(i -> builder.append(" "));
		return builder.toString();

	}

}
