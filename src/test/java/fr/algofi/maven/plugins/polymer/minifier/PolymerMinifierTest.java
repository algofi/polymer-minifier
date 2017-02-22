package fr.algofi.maven.plugins.polymer.minifier;

import static fr.algofi.maven.plugins.polymer.minifier.Utils.readComponent;
import static fr.algofi.maven.plugins.polymer.minifier.Utils.readContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;

public class PolymerMinifierTest {

	private PolymerMinifier sut;

	@Before
	public void setup() {
		sut = new PolymerMinifier();
	}

	@Test
	public void shouldNotChangeThePolymerWebComponentCodeIfNoPropertyGiven() throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-no-properties_expected.html");
		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-no-properties.html");

		// call
		sut.minify(polymer);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
		assertNotNull(polymer.getProperties());
		assertEquals(0, polymer.getProperties().size());
	}

	@Test
	public void shouldMinifyJavascript() throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-no-properties_expected.min.html");
		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-no-properties.html");
		
		// call
		sut.minifyJavascript( true );
		sut.minify(polymer);
		
		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
		assertNotNull(polymer.getProperties());
		assertEquals(0, polymer.getProperties().size());
	}

	@Test
	public void shouldMinifyOnePropertyThePolymerWebComponentCodeIfOnePropertyGiven()
			throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-one-properties_expected.html");

		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-one-properties.html");
		final PolymerProperty sessionIdProperty = new PolymerProperty();
		sessionIdProperty.setName("sessionId");
		polymer.setProperties(Arrays.asList(sessionIdProperty));

		// call
		sut.minify(polymer);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
		assertNotNull(polymer.getProperties());
		assertEquals(1, polymer.getProperties().size());
		assertEquals("a", polymer.getProperties().get(0).getMiniName());
	}

	@Test
	public void shouldMinifyFivePropertyThePolymerWebComponentCodeIfFivePropertyGiven()
			throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-five-properties_expected.html");

		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-five-properties.html");

		final PolymerProperty sessionIdProperty = new PolymerProperty();
		sessionIdProperty.setName("sessionId");

		final PolymerProperty userIdProperty = new PolymerProperty();
		userIdProperty.setName("userId");

		final PolymerProperty habilitationProperty = new PolymerProperty();
		habilitationProperty.setName("habilitation");

		final PolymerProperty friendsProperty = new PolymerProperty();
		friendsProperty.setName("friends");

		final PolymerProperty postsProperty = new PolymerProperty();
		postsProperty.setName("posts");

		polymer.setProperties(
				Arrays.asList(sessionIdProperty, userIdProperty, habilitationProperty, friendsProperty, postsProperty));

		// call
		sut.minify(polymer);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
		// assert minifed properties
		assertNotNull(polymer.getProperties());
		assertEquals(5, polymer.getProperties().size());
		assertEquals("a", polymer.getProperties().get(0).getMiniName());
		assertEquals("b", polymer.getProperties().get(1).getMiniName());
		assertEquals("c", polymer.getProperties().get(2).getMiniName());
		assertEquals("d", polymer.getProperties().get(3).getMiniName());
		assertEquals("e", polymer.getProperties().get(4).getMiniName());
	}

	@Test
	public void shouldMinifyDependencies() throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-dep-properties_expected.html");

		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-dep-properties.html");
		polymer.setName("x-dep-properties");

		final PolymerProperty sessionIdProperty = new PolymerProperty();
		sessionIdProperty.setName("sessionId");

		final PolymerProperty userIdProperty = new PolymerProperty();
		userIdProperty.setName("userId");

		final PolymerProperty postsProperty = new PolymerProperty();
		postsProperty.setName("posts");

		polymer.setProperties(Arrays.asList(sessionIdProperty, userIdProperty, postsProperty));

		// dependency
		PolymerComponent dep = new PolymerComponent();
		dep.setName("x-five-properties");
		dep.setMiniName("x-a");
		final PolymerProperty sessionIdDepProperty = new PolymerProperty();
		sessionIdDepProperty.setName("sessionId");
		sessionIdDepProperty.setMiniName("a");

		final PolymerProperty userIdDepProperty = new PolymerProperty();
		userIdDepProperty.setName("userId");
		userIdDepProperty.setMiniName("b");

		final PolymerProperty habilitationDepProperty = new PolymerProperty();
		habilitationDepProperty.setName("habilitation");
		habilitationDepProperty.setMiniName("c");

		final PolymerProperty friendsDepProperty = new PolymerProperty();
		friendsDepProperty.setName("friends");
		friendsDepProperty.setMiniName("d");

		final PolymerProperty postsDepProperty = new PolymerProperty();
		postsDepProperty.setName("posts");
		postsDepProperty.setMiniName("e");

		dep.setProperties(Arrays.asList(sessionIdDepProperty, userIdDepProperty, habilitationDepProperty,
				friendsDepProperty, postsDepProperty));

		sut.setDependencies(Arrays.asList(dep));

		// call
		sut.minify(polymer);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
	}

}
