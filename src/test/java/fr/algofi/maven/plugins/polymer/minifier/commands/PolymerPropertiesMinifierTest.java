package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;

public class PolymerPropertiesMinifierTest {

	private Minifier sut;

	@Before
	public void setup() {
		sut = new PolymerPropertiesMinifier();
	}

	@Test(expected = MinifierException.class)
	public void shouldThrowMinifierExceptionWhenNoMiniNameAvailable() throws MinifierException {

		// input
		final PolymerComponent component = new PolymerComponent();
		component.setMiniContent("");
		final Map<String, PolymerProperty> properties = new LinkedHashMap<>();

		for (int i = 0; i < 10_000; i++) {
			final PolymerProperty property = new PolymerProperty();
			final String name = "_prop" + i;
			property.setName(name);
			properties.put(name, property);
		}
		component.setProperties(properties);

		// call
		sut.minimize(component, null);

	}

}
