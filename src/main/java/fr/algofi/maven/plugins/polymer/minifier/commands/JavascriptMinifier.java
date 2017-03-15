package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.Collection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;
import fr.algofi.maven.plugins.polymer.minifier.util.MinifierUtils;

/**
 * @deprecated use JavascriptPropertiesMinifier instead
 * @author cjechoux
 *
 */
@Deprecated
public class JavascriptMinifier implements Minifier {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimize(final PolymerComponent component, final Collection<PolymerComponent> dependencies) {
		
		final Document document = Jsoup.parse(component.getMinifiedContent());
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		final String miniJavascript = MinifierUtils.minifyJavascript(component.getPath(), scriptPart.getBulkScript());
		final String minifiedContent = component.getMinifiedContent().replace(scriptPart.getBulkScript(), miniJavascript);
		component.setMiniContent(minifiedContent);
		
	}

}
