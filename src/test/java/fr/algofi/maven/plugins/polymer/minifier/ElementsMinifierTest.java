package fr.algofi.maven.plugins.polymer.minifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class ElementsMinifierTest {

	private ElementsMinifier sut;

	@Before
	public void setup() {
		sut = new ElementsMinifier();
	}

	@Test
	public void shouldReturnAListOfImports() throws IOException, MinifierException {

		// expected content :
		final Path expectedPath = Paths.get("src", "test", "resources", "minifier-all", "target",
				"elements.build.html");
		final String expectedContent = Files.readAllLines(expectedPath).stream().collect(Collectors.joining("\n"));

		// input main imports
		final Path path = Paths.get("src", "test", "resources", "minifier-all", "source", "elements.html");

		// call
		// href imports
		final String minimized = sut.minimize(path);

		// asasertions
		assertNotNull(minimized);
		assertEquals( expectedContent, minimized );

		// TODO should concateneate all imports

		// TODO should minify all of them

	}
}
