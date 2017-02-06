package fr.algofi.maven.plugins.polymer.minifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ElementsMinifier {

	private PolymerParser parser;

	public ElementsMinifier() {
		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		parser = new PolymerParser(scriptEngine);
	}

	public String minimize(final Path path) throws IOException {
		final StringBuilder builder = new StringBuilder("<html><head></head><body><div hidden=\"\">\n");

		// component already appended
		final Set<String> appended = new HashSet<>();

		final Document document = Jsoup.parse(path.toFile(), Charset.defaultCharset().name());
		final Elements links = document.getElementsByTag("link");
		for (Element link : links) {
			final String rel = link.attr("rel");
			if ("import".equals(rel)) {
				final String href = link.attr("href");
				final String importPath = path.getParent().normalize().resolve(Paths.get(href)).normalize().toString();

				final PolymerComponent component = parser.read(importPath);

				appendComponent(component, builder, appended);

			}
		}

		builder.append("</div></body></html>");

		return builder.toString();
	}

	private void appendComponent(final PolymerComponent component, final StringBuilder builder,
			final Set<String> appended) {

		// append import, and then the components itself
		for (final PolymerComponent importedComponent : component.getImports()) {
			appendComponent(importedComponent, builder, appended);
		}

		// append myself
		if (!appended.contains(component.getPath())) {

			builder.append(component.getContent() );

			appended.add(component.getPath());
		}

	}

}
