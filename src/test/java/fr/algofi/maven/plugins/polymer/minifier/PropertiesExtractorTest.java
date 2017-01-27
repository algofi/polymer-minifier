package fr.algofi.maven.plugins.polymer.minifier;
import static fr.algofi.maven.plugins.polymer.minifier.Utils.readContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.PropertiesExtractor;

public class PropertiesExtractorTest {

	private PropertiesExtractor sut;

	@Before
	public void setup() {
		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		sut = new PropertiesExtractor(scriptEngine);
	}

	@Test
	public void shouldReturnAnEmptyListWhenPolymerElementHasNoProperties() throws IOException, ScriptException {
		// input
		final String content = readContent("src/test/resources/extractor/x-no-properties.html");
		// call
		final List<String> properties = sut.extractProperties(content);
		// assertions
		assertNotNull(properties);
		assertTrue(properties.isEmpty());
	}

	@Test
	public void shouldReturnAListWith1PropertyWhenPolymerElementHasOneProperty() throws IOException, ScriptException {
		// input
		final String content = readContent("src/test/resources/extractor/x-one-properties.html");
		// call
		final List<String> properties = sut.extractProperties(content);
		// assertions
		assertNotNull(properties);
		assertFalse(properties.isEmpty());
		assertEquals(1, properties.size());
		assertEquals("sessionId", properties.get(0));
	}

	@Test
	public void shouldReturnAListWith5PropertyWhenPolymerElementHasFiveProperty() throws IOException, ScriptException {
		// input
		final String content = readContent("src/test/resources/extractor/x-five-properties.html");
		// call
		final List<String> properties = sut.extractProperties(content);
		// assertions
		assertNotNull(properties);
		assertFalse(properties.isEmpty());
		assertEquals(5, properties.size());
		assertEquals("sessionId", properties.get(0));
		assertEquals("userId", properties.get(1));
		assertEquals("habilitation", properties.get(2));
		assertEquals("friends", properties.get(3));
		assertEquals("posts", properties.get(4));
	}


}
