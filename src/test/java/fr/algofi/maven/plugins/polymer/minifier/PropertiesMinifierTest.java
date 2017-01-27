package fr.algofi.maven.plugins.polymer.minifier;

import static fr.algofi.maven.plugins.polymer.minifier.Utils.readContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.PropertiesMinifier;

public class PropertiesMinifierTest {

	private PropertiesMinifier sut;

	@Before
	public void setup() {
		sut = new PropertiesMinifier();
	}

	@Test
	public void shouldNotChangeThePolymerWebComponentCodeIfNoPropertyGiven() throws IOException, MinifierException {
		// input
		final String content = readContent("src/test/resources/minifier/x-no-properties.html");
		final List<String> properties = new ArrayList<>();

		// call
		final String minifiedContent = sut.minify(content, properties);

		// assertions
		assertNotNull(minifiedContent);
		assertEquals(content, minifiedContent);
	}

	@Test
	public void shouldMinifyOnePropertyThePolymerWebComponentCodeIfOnePropertyGiven()
			throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-one-properties_expected.html");

		// input
		final String content = readContent("src/test/resources/minifier/x-one-properties.html");
		final List<String> properties = Arrays.asList("sessionId");

		// call
		final String minifiedContent = sut.minify(content, properties);

		// assertions
		assertNotNull(minifiedContent);
		assertEquals(contentExpected, minifiedContent);
	}

	@Test
	public void shouldMinifyFivePropertyThePolymerWebComponentCodeIfFivePropertyGiven()
			throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-five-properties_expected.html");

		// input
		final String content = readContent("src/test/resources/minifier/x-five-properties.html");
		final List<String> properties = Arrays.asList("sessionId", "userId", "habilitation", "friends", "posts");

		// call
		final String minifiedContent = sut.minify(content, properties);

		// assertions
		assertNotNull(minifiedContent);
		assertEquals(contentExpected, minifiedContent);
	}
}
