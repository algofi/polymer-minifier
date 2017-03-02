package fr.algofi.maven.plugins.polymer.minifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.commands.BlankMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.DependenciesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.HTMLCommentMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.NoMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerNameMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerPropertiesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.model.MiniElements;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;

public class ElementsMinifierTest {

	private ElementsMinifier sut;

	@Before
	public void setup() {
		final Minifier no = new NoMinifier();
		final Minifier blank = new BlankMinifier();
		final Minifier htmlComments = new HTMLCommentMinifier();
		final Minifier properties = new PolymerPropertiesMinifier();
		final Minifier polymerName = new PolymerNameMinifier();
		final Minifier dep = new DependenciesMinifier();

		sut = new ElementsMinifier(no, blank, htmlComments, properties, polymerName, dep);
	}

	@Test
	public void shouldReturnAListOfImports() throws IOException, MinifierException {

		// expected content :
		final Path expectedContentPath = Paths.get("src", "test", "resources", "minifier-all", "target",
				"elements.build.html");
		final String expectedContent = Files.readAllLines(expectedContentPath).stream()
				.collect(Collectors.joining("\n"));
		final Path expectedIndexPath = Paths.get("src", "test", "resources", "minifier-all", "target",
				"index.build.html");
		final String expectedIndex = Files.readAllLines(expectedIndexPath).stream().collect(Collectors.joining("\n"));

		// input
		// main entry point for the imports above
		final Path index = Paths.get("src", "test", "resources", "minifier-all", "source", "index.html");

		// call
		final MiniElements minimized = sut.minimize(index);

		// asasertions
		assertNotNull(minimized);
		assertEquals(expectedContent, minimized.getContent());
		assertEquals(expectedIndex, minimized.getIndexContent());
		assertEquals("elements.build.html", minimized.getImportBuildHref());

	}
}
