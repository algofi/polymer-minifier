package fr.algofi.maven.plugins.polymer.minifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.algofi.maven.plugins.polymer.minifier.model.MiniElements;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;

@Mojo(name = "minify")
public class MinifierMojo extends AbstractMojo {

	@Parameter(name = "index", required = true)
	private String index;

	@Parameter(name = "outputFolder", defaultValue = "target/polymer-minifier")
	private String outputFolder;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		final ElementsMinifier minifier = new ElementsMinifier();

		final Path indexPath = Paths.get(index);

		try {
			final MiniElements mini = minifier.minimize(indexPath);

			createTargetFolder();
			writeMini(Paths.get(outputFolder, "index.html"), mini.getIndexContent());
			writeMini(Paths.get(outputFolder, mini.getImportBuildHref()), mini.getContent());

		} catch (IOException | MinifierException e) {
			throw new MojoFailureException("Cannot build the polymer project", e);
		}
	}

	private void writeMini(Path path, String indexContent) throws FileNotFoundException {

		// if the parent folders does not exist, then create the parents folder
		if ( !path.getParent().toFile().exists() ) {
			path.getParent().toFile().mkdirs();
		}
		
		try (final PrintWriter writer = new PrintWriter(path.toFile())) {
			writer.print(indexContent);
		}
	}

	private void createTargetFolder() {
		File target = new File(outputFolder);
		if (!target.exists()) {
			target.mkdirs();
		}
	}

}
