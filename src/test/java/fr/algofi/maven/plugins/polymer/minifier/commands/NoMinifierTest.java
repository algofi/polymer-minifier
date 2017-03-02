package fr.algofi.maven.plugins.polymer.minifier.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class NoMinifierTest {

	private Minifier sut;

	@Before
	public void setup() {
		sut = new NoMinifier();
	}

	@Test
	public void shouldNotMinimizeThePolymerContent() throws MinifierException {
		// input
		final PolymerComponent component = new PolymerComponent();
		component.setContent("<p>Hello World</p>");

		// call
		sut.minimize(component, null);

		// assertions
		assertEquals("<p>Hello World</p>", component.getMinifiedContent());
	}
}
