package fr.algofi.maven.plugins.polymer.minifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class PolymerParsedTest {

	private PolymerParser sut;

	@Before
	public void setup() {
		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		sut = new PolymerParser(scriptEngine);
	}

	@Test
	public void shouldReturnAnEmptyListWhenPolymerElementHasNoProperties() throws IOException, ScriptException {
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
	public void shouldReturnAListWith1PropertyWhenPolymerElementHasOneProperty() throws IOException, ScriptException {
		// input
		final String path = "src/test/resources/extractor/x-one-properties.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getProperties());
		assertFalse(polymer.getProperties().isEmpty());
		
		assertEquals(1, polymer.getProperties().size());
		assertEquals("sessionId", polymer.getProperties().get(0).getName() );
		
		assertEquals("x-one-properties", polymer.getName());
	}

	@Test
	public void shouldReturnAListWith5PropertyWhenPolymerElementHasFiveProperty() throws IOException, ScriptException {
		// input
		final String path = "src/test/resources/extractor/x-five-properties.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getProperties());
		assertFalse(polymer.getProperties().isEmpty());
		assertEquals(5, polymer.getProperties().size());
		assertEquals("sessionId", polymer.getProperties().get(0).getName());
		assertEquals("userId", polymer.getProperties().get(1).getName());
		assertEquals("habilitation", polymer.getProperties().get(2).getName());
		assertEquals("friends", polymer.getProperties().get(3).getName());
		assertEquals("posts", polymer.getProperties().get(4).getName());

		assertEquals("x-five-properties", polymer.getName());
	}

	@Test
	public void shouldReturnAnSingleListOfImportsDependenciesWhenOnlyPolymerImport() throws IOException, ScriptException {
		// input
		final String path = "src/test/resources/minifier-all/source/x-premier.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getImports());
		assertEquals(1, polymer.getImports().size());

	}

	@Test
	public void shouldReturnTheGoodImportsDependenciesWhenSomeProvided() throws IOException, ScriptException {
		// input
		final String path = "src/test/resources/minifier-all/source/x-main.html";
		// call
		final PolymerComponent polymer = sut.read(path);
		// assertions
		assertNotNull(polymer.getImports());
		assertEquals(3, polymer.getImports().size());

		final PolymerComponent premier = polymer.getImports().get(1);
		assertEquals("src/test/resources/minifier-all/source/x-premier.html".replace('/', '\\'), premier.getPath());
		assertNotNull(premier.getProperties());
		assertEquals(1, premier.getProperties().size());
		assertEquals("userId", premier.getProperties().get(0).getName());
		assertEquals("x-premier", premier.getName());
		assertEquals(1, premier.getImports().size());

		final PolymerComponent second = polymer.getImports().get(2);
		assertEquals("src/test/resources/minifier-all/source/x-second.html".replace('/', '\\'), second.getPath());
		assertNotNull(second.getProperties());
		assertEquals(1, second.getProperties().size());
		assertEquals("friends", second.getProperties().get(0).getName());
		assertEquals("x-second", second.getName());
		assertEquals(1, second.getImports().size());

	}

}
