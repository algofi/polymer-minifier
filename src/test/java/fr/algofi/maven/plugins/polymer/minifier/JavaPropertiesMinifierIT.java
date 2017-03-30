package fr.algofi.maven.plugins.polymer.minifier;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.commands.JavascriptPropertiesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.NoMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerNameMinifier;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerParserException;

public class JavaPropertiesMinifierIT {

	private final Minifier noMinifier = new NoMinifier();
	private final Minifier nameMinifier = new PolymerNameMinifier();
	private final Minifier javascriptPropertiesMinifier = new JavascriptPropertiesMinifier();

	@Test
	public void shouldParseAllPolymerElements() throws IOException {
		final Path start = Paths.get("src", "test", "resources", "polymer-elements");
		final Set<FileVisitOption> options = new HashSet<>();

		final Set<String> fileNamesToIgnore = Arrays.asList("web-animations.html").stream().collect(Collectors.toSet());

		final List<PolymerComponent> components = new ArrayList<>();

		Files.walkFileTree(start, options, 3, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

				if (file.toString().contains("bower_components") && attrs.isRegularFile()) {

					final String filename = file.toFile().getName();

					if (filename.endsWith(".html") && !fileNamesToIgnore.contains(filename)) {
						final PolymerComponent component = assertPolymerElementsFile(file);
						if (component != null) {
							components.add(component);
						}
					}

				}

				return FileVisitResult.CONTINUE;
			}
		});

		components.stream().forEach(component -> {
			try {
				noMinifier.minimize(component, components);
				nameMinifier.minimize(component, components);
				//javascriptPropertiesMinifier.minimize(component, components);
			} catch (Exception e) {
				System.out.println("component = " + component.getPath());
				e.printStackTrace();
				fail(e.getMessage());
			}
		});

	}

	protected PolymerComponent assertPolymerElementsFile(Path file) {

		final PolymerParser parser = new PolymerParser();
		try {
			return parser.read(file.toString());
		} catch (PolymerParserException e) {
			if (e.getCause() instanceof FileNotFoundException) {
				System.err.println("WARNING " + e.getCause().getMessage());
			} else {
				e.printStackTrace();
				fail("Exception Not expected= " + e.getMessage());
			}
		}

		// we assert no exception is thrown
		return null;
	}

}
