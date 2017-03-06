package fr.algofi.maven.plugins.polymer.minifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.algofi.maven.plugins.polymer.minifier.commands.BlankMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.HTMLCommentMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.JavascriptMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.JavascriptPropertiesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.NoMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerNameMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerPropertiesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.model.MiniElements;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;

@Mojo(name = "minify")
public class MinifierMojo extends AbstractMojo {

	@Parameter(name = "index", required = true)
	private String index;

	@Parameter(name = "outputFolder", defaultValue = "target/polymer-minifier")
	private String outputFolder;

	@Parameter(name = "minifyJavascript", defaultValue = "true")
	private boolean minifyJavascript;

	@Parameter(name = "gzipElements", defaultValue = "true")
	private boolean gzipElements;

	@Parameter(name = "minifyBlanks", defaultValue = "true")
	private boolean minifyBlanks;

	@Parameter(name = "minifyHtmlComments", defaultValue = "true")
	private boolean minifyHtmlComments;

	@Parameter(name = "minifyProperties", defaultValue = "false")
	private boolean minifyProperties;

	@Parameter(name = "minifyPolymerName", defaultValue = "true")
	private boolean minifyPolymerName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		final ElementsMinifier minifier = createElementsMinifier();

		final Path indexPath = Paths.get(index);

		try {
			final MiniElements mini = minifier.minimize(indexPath);

			createTargetFolder();

			writeMini(Paths.get(outputFolder, "index.html"), mini.getIndexContent());
			writeMini(Paths.get(outputFolder, mini.getImportBuildHref()), mini.getContent());

			if (gzipElements) {
				writeMiniGzip(Paths.get(outputFolder, "index.html.gz"), mini.getIndexContent());
				writeMiniGzip(Paths.get(outputFolder, mini.getImportBuildHref() + ".gz"), mini.getContent());
			}

		} catch (IOException | MinifierException e) {
			throw new MojoFailureException("Cannot build the polymer project", e);
		}
	}

	private ElementsMinifier createElementsMinifier() {
		final Minifier no = new NoMinifier();

		final List<Minifier> minifiers = new ArrayList<>();
		if (minifyBlanks) {
			minifiers.add(new BlankMinifier());
		}
		if (minifyHtmlComments) {
			minifiers.add(new HTMLCommentMinifier());
		}
		if (minifyProperties) {
			minifiers.add(new PolymerPropertiesMinifier());
		}
		if (minifyPolymerName) {
			minifiers.add(new PolymerNameMinifier());
		}
		if (minifyJavascript) {
			minifiers.add(new JavascriptPropertiesMinifier());
			minifiers.add(new JavascriptMinifier());
		}

		final ElementsMinifier minifier = new ElementsMinifier(no, minifiers.toArray(new Minifier[minifiers.size()]));
		return minifier;
	}

	/**
	 * write the content into a gzipped file
	 * 
	 * @param path
	 *            path of the archive
	 * @param content
	 *            content to write
	 * @throws IOException
	 */
	private void writeMiniGzip(Path path, String content) throws IOException {

		// we write on a file
		try (final OutputStream fileOutpuStream = new FileOutputStream(path.toFile())) {
			// the file stream is gzipped
			try (final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutpuStream)) {
				// we write this string content
				try (final PrintWriter writer = new PrintWriter(gzipOutputStream)) {
					writer.write(content);
				}
			}
		}

	}

	/**
	 * Write the content into an index file
	 * 
	 * @param path
	 * @param indexContent
	 * @throws FileNotFoundException
	 */
	private void writeMini(Path path, String indexContent) throws FileNotFoundException {

		// if the parent folders does not exist, then create the parents folder
		if (!path.getParent().toFile().exists()) {
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
