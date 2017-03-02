package fr.algofi.maven.plugins.polymer.minifier.util;

import static fr.algofi.maven.plugins.polymer.minifier.util.StringUtils.offset;

import java.util.ArrayList;
import java.util.List;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

public class JavascriptUtils {
	private JavascriptUtils() {
	}

	/**
	 * find the function enclosing the child node
	 * 
	 * @param childNode
	 *            child node of the JS function to find
	 * @return the
	 */
	public static Node findFunction(Node childNode) {

		if (childNode == null) {
			return null;
		} else if (childNode.getToken() == Token.FUNCTION) {
			return childNode;
		}

		return findFunction(childNode.getParent());
	}

	/**
	 * Returns a list of node (child node of the parent node) that match the
	 * given <tt>token</tt> and whose <tt>string</tt> match the given expression
	 * 
	 * @param parentNode
	 *            starting point for the search
	 * @param token
	 *            type of the node to fetch
	 * @param expression
	 *            matching <tt>string</tt>
	 * @return found nodes or empty list
	 */
	public static List<Node> find(final Node parentNode, final Token token, final String expression) {
		final List<Node> nodes = new ArrayList<>();

		if (parentNode != null) {

			// check this node
			if (parentNode.getToken() == token) {
				if (expression.equals(parentNode.getString())) {
					nodes.add(parentNode);
				}
			}

			// check the 1st child
			nodes.addAll(find(parentNode.getFirstChild(), token, expression));
			// check next node
			nodes.addAll(find(parentNode.getNext(), token, expression));
		}

		return nodes;
	}

	/**
	 * print a tree of nodes
	 * 
	 * @param type
	 *            any given type. Can be set to root. For 1st child, it is set
	 *            to <tt>child</tt> for next child it is set to <tt>next</tt>
	 * @param node
	 *            starting browsing point
	 * @param offset
	 *            left white space offset for printing
	 */
	public static void showNode(final String type, final Node node, final int offset) {
		if (node != null) {
			System.out.println(type.toUpperCase() + " - " + offset(offset) + node);
			showNode("child", node.getFirstChild(), offset + 1);
			showNode("next ", node.getNext(), offset + 1);
		}
	}
}
