package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.yahoo.platform.yui.compressor.CssCompressor;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class CssMinifier implements Minifier {
	
	private static final String STYLE_END_TAG = "</style>";

	@Override
	public void minimize(PolymerComponent component, Collection<PolymerComponent> dependencies)
			throws MinifierException {

		String minifiedContent = component.getMinifiedContent();

		minifiedContent = minifiedContent.replaceAll("<style>\\p{Blank}*</style>", "");

		final List<Style> styles = getStyles(minifiedContent);
		for (Style style : styles) {
			if (style.bulkCss.trim().length() == 0) {
				minifiedContent = minifiedContent.replaceFirst(style.outerHtml, "");
			} else {

				final String compiledCss = compileCss(component, style.bulkCss);

				final String compiledStyle;
				if (compiledCss.trim().length() == 0) {
					compiledStyle = "";
				} else {
					compiledStyle = style.startTag + compiledCss + STYLE_END_TAG;
				}
				minifiedContent = minifiedContent.replace(style.outerHtml, compiledStyle);
			}
		}

		component.setMiniContent(minifiedContent);
	}

	private String compileCss(PolymerComponent component, String bulkCss) throws MinifierException {

		try {
			final Reader in = new StringReader(bulkCss);
			final CssCompressor compressor = new CssCompressor(in);

			final StringWriter writer = new StringWriter();
			final int linebreakpos = -1;

			compressor.compress(writer, linebreakpos);
			return writer.toString();
		} catch (IOException e) {
			throw new MinifierException("Faial to compile the style for the path : " + component.getPath(), e);
		}

	}

	/**
	 * return the list of all styles found
	 *
	 * @param component
	 * @return
	 */
	private List<Style> getStyles(String minifiedContent) {

		final List<Style> styles = new ArrayList<>();

		final Document document = Jsoup.parse(minifiedContent);
		final Elements styleElements = document.getElementsByTag("style");

		for (Element styleElement : styleElements) {
			final String outerHtml = styleElement.outerHtml();
			String bulkCss = outerHtml;
			final int indexOfClosingStartTag = outerHtml.indexOf(">");
			final String startTag = outerHtml.substring(0, indexOfClosingStartTag + 1);
			bulkCss = bulkCss.substring(indexOfClosingStartTag + 1);
			bulkCss = bulkCss.replace("</style>", "");

			styles.add(new Style(startTag, bulkCss, outerHtml));
		}

		return styles;
	}

	private class Style {

		public String startTag;

		public String bulkCss;
		public String outerHtml;

		public Style(String startTag, String bulkCss, String outerHtml) {
			this.startTag = startTag;
			this.bulkCss = bulkCss;
			this.outerHtml = outerHtml;
		}

	}

}
