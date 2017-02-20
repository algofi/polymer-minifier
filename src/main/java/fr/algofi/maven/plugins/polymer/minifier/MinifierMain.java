package fr.algofi.maven.plugins.polymer.minifier;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import fr.algofi.maven.plugins.polymer.minifier.model.MiniElements;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;

public class MinifierMain {
	private final Path imports;
	private final Path index;
	private final Path build;
	private final ElementsMinifier minifier;

	public MinifierMain(String elementsPath, String indexPath, String buildFolder) {
		imports = Paths.get(elementsPath);
		index = Paths.get(indexPath);
		build = Paths.get(buildFolder);

		if (!build.toFile().exists()) {
			build.toFile().mkdirs();
		}
		minifier = new ElementsMinifier();
	}

	public MiniElements minimize() throws MinifierException {
		//final MiniElements minimized = minifier.minimize(imports, Optional.of(index));

		return null;
	}

	public static void main(String[] args) throws IOException, MinifierException {

		// arguments
		// 0: elements.html file : where to find a list of HTML imports
		// 1: index.html file : where to have the component to load
		// 2: output folder where to create the index file and the elements file
		// name

		if (args.length != 3) {
			System.err.println("Usage: elements.html index.html ../build");
		}

		MinifierMain main = new MinifierMain(args[0], args[1], args[2]);
		MiniElements miniElements = main.minimize();

	}

}
