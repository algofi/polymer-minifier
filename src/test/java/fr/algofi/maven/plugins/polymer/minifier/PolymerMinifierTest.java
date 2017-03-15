package fr.algofi.maven.plugins.polymer.minifier;

import static fr.algofi.maven.plugins.polymer.minifier.Utils.readComponent;
import static fr.algofi.maven.plugins.polymer.minifier.Utils.readContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.commands.BlankMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.DependenciesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.HTMLCommentMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.JavascriptMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.NoMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerNameMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerPropertiesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.WhiteOnlyJavascriptMinifier;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerProperty;

public class PolymerMinifierTest {

	private PolymerMinifier sut;

	@Before
	public void setup() {
		final Minifier no = new NoMinifier();
		final Minifier blank = new BlankMinifier();
		final Minifier htmlComments = new HTMLCommentMinifier();
		final Minifier properties = new PolymerPropertiesMinifier();
		final Minifier polymerName = new PolymerNameMinifier();
//		final Minifier whiteOnly = new WhiteOnlyJavascriptMinifier();
		sut = new PolymerMinifier(no, blank, htmlComments, properties, polymerName);
	}

	@Test
	public void shouldNotChangeThePolymerWebComponentCodeIfNoPropertyGiven() throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-no-properties_expected.html");
		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-no-properties.html");

		// call
		sut.minify(polymer, null);

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
		sut.addMinifier(new JavascriptMinifier());
		sut.minify(polymer, null);

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
		final Map<String, PolymerProperty> properties = new HashMap<>();
		properties.put(sessionIdProperty.getName(), sessionIdProperty);
		polymer.setProperties(properties);

		// call
		sut.minify(polymer, null);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
		assertNotNull(polymer.getProperties());
		assertEquals(1, polymer.getProperties().size());
		assertEquals("a", polymer.getProperties().get("sessionId").getMiniName());
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

		final Map<String, PolymerProperty> properties = new LinkedHashMap<>();
		properties.put(sessionIdProperty.getName(), sessionIdProperty);
		properties.put(userIdProperty.getName(), userIdProperty);
		properties.put(habilitationProperty.getName(), habilitationProperty);
		properties.put(friendsProperty.getName(), friendsProperty);
		properties.put(postsProperty.getName(), postsProperty);
		polymer.setProperties(properties);

		// call
		sut.minify(polymer, null);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
		// assert minifed properties
		assertNotNull(polymer.getProperties());
		assertEquals(5, polymer.getProperties().size());
		assertEquals("a", polymer.getProperties().get("sessionId").getMiniName());
		assertEquals("b", polymer.getProperties().get("userId").getMiniName());
		assertEquals("c", polymer.getProperties().get("habilitation").getMiniName());
		assertEquals("d", polymer.getProperties().get("friends").getMiniName());
		assertEquals("e", polymer.getProperties().get("posts").getMiniName());
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

		final Map<String, PolymerProperty> properties = new HashMap<>();
		properties.put(sessionIdProperty.getName(), sessionIdProperty);
		properties.put(userIdProperty.getName(), userIdProperty);
		properties.put(postsProperty.getName(), postsProperty);
		polymer.setProperties(properties);

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

		final Map<String, PolymerProperty> depProperties = new HashMap<>();
		depProperties.put(sessionIdDepProperty.getName(), sessionIdDepProperty);
		depProperties.put(userIdDepProperty.getName(), userIdDepProperty);
		depProperties.put(habilitationDepProperty.getName(), habilitationDepProperty);
		depProperties.put(friendsDepProperty.getName(), friendsDepProperty);
		depProperties.put(postsDepProperty.getName(), postsDepProperty);
		dep.setProperties(depProperties);

		sut.addMinifier(new DependenciesMinifier());

		// call
		sut.minify(polymer, Arrays.asList(dep));

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
	}

}
