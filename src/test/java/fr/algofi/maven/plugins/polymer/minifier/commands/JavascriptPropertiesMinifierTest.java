package fr.algofi.maven.plugins.polymer.minifier.commands;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;

public class JavascriptPropertiesMinifierTest {

	private Minifier sut;

	@Before
	public void setup() {
		sut = new JavascriptPropertiesMinifier();
	}

	@Test
	public void shouldMinifyThePolymerContent() throws IOException, MinifierException {
		final String path = "src/test/resources/minifier/createElementExample.html";
		final String content = Files.readAllLines(Paths.get(path)).stream().collect(Collectors.joining("\n"));

		final String pathExpected = "src/test/resources/minifier/createElementExample_expected.html";
		final String contentExpected = Files.readAllLines(Paths.get(pathExpected)).stream()
				.collect(Collectors.joining("\n"));

		// input
		final PolymerComponent component = new PolymerComponent();
		component.setPath(path);
		component.setContent(content);
		component.setMiniContent(content);

		PolymerComponent dep = new PolymerComponent();
		dep.setName("paper-checkbox");
		dep.setMiniName("x-k");

		final Map<String, PolymerProperty> properties = new HashMap<>();
		final PolymerProperty checkedDepProperty = new PolymerProperty();
		checkedDepProperty.setName("checked");
		checkedDepProperty.setMiniName("k");
		properties.put(checkedDepProperty.getName(), checkedDepProperty);
		dep.setProperties(properties);

		// call
		sut.minimize(component, Arrays.asList(dep));

		// assertions
		assertEquals(contentExpected, component.getMinifiedContent());

	}

}
