package fr.algofi.maven.plugins.polymer.minifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerParserException;

public class PolymerParserTest {

	private PolymerParser sut;

	@Before
	public void setup() throws PolymerParserException {
		// final ScriptEngine scriptEngine = new
		// ScriptEngineManager().getEngineByName("nashorn");
		sut = new PolymerParser();
	}

	@Test
	public void shouldReturnAnEmptyListWhenPolymerElementHasNoProperties() throws PolymerParserException {
		// input
		final String path = "src/test/resources/extractor/x-no-properties.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getProperties());
		assertTrue(polymer.getProperties().isEmpty());
		assertEquals("x-no-properties", polymer.getName());
	}

	@Test
	public void shouldReturnAListWith1PropertyWhenPolymerElementHasOneProperty() throws PolymerParserException {
		// input
		final String path = "src/test/resources/extractor/x-one-properties.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getProperties());
		assertFalse(polymer.getProperties().isEmpty());

		assertEquals(1, polymer.getProperties().size());
		assertEquals("sessionId", polymer.getProperties().get("sessionId").getName());

		assertEquals("x-one-properties", polymer.getName());
	}

	@Test
	public void shouldReturnAListWith1FilePropertyWhenPolymerElementHasOneProperty() throws PolymerParserException {
		// input
		final String path = "src/test/resources/extractor/x-one-file-properties.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getProperties());
		assertFalse(polymer.getProperties().isEmpty());

		assertEquals(1, polymer.getProperties().size());
		assertEquals("file", polymer.getProperties().get("file").getName());

		assertEquals("x-one-file-properties", polymer.getName());
	}

	@Test
	public void shouldReturnAListWith5PropertyWhenPolymerElementHasFiveProperty() throws PolymerParserException {
		// input
		final String path = "src/test/resources/extractor/x-five-properties.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getProperties());
		assertFalse(polymer.getProperties().isEmpty());
		assertEquals(5, polymer.getProperties().size());
		assertEquals("sessionId", polymer.getProperties().get("sessionId").getName());
		assertEquals("userId", polymer.getProperties().get("userId").getName());
		assertEquals("habilitation", polymer.getProperties().get("habilitation").getName());
		assertEquals("friends", polymer.getProperties().get("friends").getName());
		assertEquals("posts", polymer.getProperties().get("posts").getName());

		assertEquals("x-five-properties", polymer.getName());
	}

	@Test
	public void shouldReturnAnSingleListOfImportsDependenciesWhenOnlyPolymerImport() throws PolymerParserException {
		// input
		final String path = "src/test/resources/minifier-all/source/x-premier.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getImports());
		assertEquals(1, polymer.getImports().size());

	}

	@Test
	public void shouldReturnTheGoodImportsDependenciesWhenSomeProvided() throws PolymerParserException {
		// input
		final String path = "src/test/resources/minifier-all/source/x-main.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getImports());
		assertEquals(4, polymer.getImports().size());

		final PolymerComponent premier = polymer.getImports().get(1);
		assertEquals("src/test/resources/minifier-all/source/x-premier.html".replace('/', File.separatorChar),
				premier.getPath());
		assertNotNull(premier.getProperties());
		assertEquals(1, premier.getProperties().size());
		assertEquals("userId", premier.getProperties().get("userId").getName());
		assertEquals("x-premier", premier.getName());
		assertEquals(1, premier.getImports().size());

		final PolymerComponent second = polymer.getImports().get(2);
		assertEquals("src/test/resources/minifier-all/source/x-second.html".replace('/', File.separatorChar),
				second.getPath());
		assertNotNull(second.getProperties());
		assertEquals(1, second.getProperties().size());
		assertEquals("friends", second.getProperties().get("friends").getName());
		assertEquals("x-second", second.getName());
		assertEquals(1, second.getImports().size());

		final PolymerComponent myCustomBehavior = polymer.getImports().get(3);
		assertEquals("src/test/resources/minifier-all/source/my-custom-behavior.html".replace('/', File.separatorChar),
				myCustomBehavior.getPath());
		assertNull(myCustomBehavior.getName());
	}
}
