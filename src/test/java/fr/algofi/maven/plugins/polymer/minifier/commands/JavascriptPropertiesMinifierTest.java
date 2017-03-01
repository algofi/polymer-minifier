package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class JavascriptPropertiesMinifierTest {
	
	private Minifier sut;
	
	@Before
	public void setup() {
		sut = new JavascriptPropertiesMinifier();
	}

	@Test
	public void shouldMinifyThePolymerContent() throws IOException, MinifierException {
		final String path = "src/test/resources/minifier/createElementExample.js";
		final String content = Files.readAllLines(Paths.get(path)).stream().collect(Collectors.joining("\n"));

		
		// input
		PolymerComponent component = new PolymerComponent();
		component.setPath(path);
		component.setContent(content);
		component.setMiniContent(content);

		
		// call
		sut.minimize(component);
		
	}
	
}
