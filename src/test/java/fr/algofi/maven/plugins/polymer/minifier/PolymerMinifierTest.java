package fr.algofi.maven.plugins.polymer.minifier;

import static fr.algofi.maven.plugins.polymer.minifier.Utils.readComponent;
import static fr.algofi.maven.plugins.polymer.minifier.Utils.readContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;

public class PolymerMinifierTest {

	private PolymerMinifier sut;

	@Before
	public void setup() {
		sut = new PolymerMinifier();
	}

	@Test
	public void shouldNotChangeThePolymerWebComponentCodeIfNoPropertyGiven() throws IOException, MinifierException {
		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-no-properties.html");

		// call
		sut.minify(polymer);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(polymer.getContent(), polymer.getMinifiedContent());
		assertNotNull(polymer.getMiniedProperties());
		assertEquals(0, polymer.getMiniedProperties().size());
	}

	@Test
	public void shouldMinifyOnePropertyThePolymerWebComponentCodeIfOnePropertyGiven()
			throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-one-properties_expected.html");

		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-one-properties.html");
		polymer.setProperties(  Arrays.asList("sessionId") );

		// call
		sut.minify(polymer);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
		assertNotNull(polymer.getMiniedProperties());
		assertEquals(1, polymer.getMiniedProperties().size());
		assertEquals("a", polymer.getMiniedProperties().get("sessionId"));
	}

	@Test
	public void shouldMinifyFivePropertyThePolymerWebComponentCodeIfFivePropertyGiven()
			throws IOException, MinifierException {
		final String contentExpected = readContent("src/test/resources/minifier/x-five-properties_expected.html");

		// input
		final PolymerComponent polymer = readComponent("src/test/resources/minifier/x-five-properties.html");
		polymer.setProperties(  Arrays.asList("sessionId", "userId", "habilitation", "friends", "posts") );

		// call
		sut.minify(polymer);

		// assertions
		assertNotNull(polymer.getMinifiedContent());
		assertEquals(contentExpected, polymer.getMinifiedContent());
		// assert minifed properties
		assertNotNull(polymer.getMiniedProperties());
		assertEquals(5, polymer.getMiniedProperties().size());
		assertEquals("a", polymer.getMiniedProperties().get("sessionId"));
		assertEquals("b", polymer.getMiniedProperties().get("userId"));
		assertEquals("c", polymer.getMiniedProperties().get("habilitation"));
		assertEquals("d", polymer.getMiniedProperties().get("friends"));
		assertEquals("e", polymer.getMiniedProperties().get("posts"));
	}

	@Test
	public void jsScriptShouldCompile() {
		final Compiler compiler = new Compiler();

		final CompilerOptions options = new CompilerOptions();
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

		final List<SourceFile> externs = Collections.emptyList();
		final List<SourceFile> inputs = new ArrayList<>();
		inputs.add(SourceFile.fromFile(new File("src/test/resources/minifier/hello.js")));

		final Result result = compiler.compile(externs, inputs, options);

		System.out.println(compiler.toSource());

	}

}
