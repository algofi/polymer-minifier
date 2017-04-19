package fr.algofi.maven.plugins.polymer.minifier.commands;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.PolymerParser;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerParserException;

public class CssMinifierTest {

	private PolymerParser parser;
	private Minifier sut;

	@Before
	public void initParser() {
		parser = new PolymerParser();
	}

	@Test
	public void shouldNotMinifyAnHtmlPageWithNoStyle() throws PolymerParserException, MinifierException {
		// input
		final Path path = Paths.get("src", "test", "resources", "css", "x-page-no-style.html");
		final PolymerComponent component = parser.read(path.toString());
		// the default minified content is the original content itself
		component.setMiniContent(component.getContent());

		// call
		sut = new CssMinifier( );
		sut.minimize(component, Arrays.asList(component));

		// assertions
		assertEquals(component.getContent(), component.getMinifiedContent());
	}
	

	@Test
	public void shouldCompileCss() throws PolymerParserException, MinifierException, IOException {
		// input
		final Path path = Paths.get("src", "test", "resources", "css", "x-page-empty-style.html");
		final PolymerComponent component = parser.read(path.toString());
		// the default minified content is the original content itself
		component.setMiniContent(component.getContent());
		
		// expected
		final Path expectedPathpath = Paths.get("src", "test", "resources", "css", "x-page-empty-style_compiled_expected.html");
		final String expectedContent = Files.readAllLines(expectedPathpath).stream().collect(Collectors.joining("\n"));
		
		// call
		sut = new CssMinifier( );
		sut.minimize(component, Arrays.asList(component));
		
		// assertions
		assertEquals(expectedContent, component.getMinifiedContent());
	}

}
