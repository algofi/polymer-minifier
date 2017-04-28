package fr.algofi.maven.plugins.polymer.minifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.algofi.maven.plugins.polymer.minifier.commands.BlankMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.CssMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.HTMLCommentMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.JavascriptCompilerMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.JavascriptPropertiesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.NoMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerNameMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.PolymerPropertiesMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.WhiteOnlyJavascriptMinifier;
import fr.algofi.maven.plugins.polymer.minifier.commands.WriteSingleFileMinifier;
import fr.algofi.maven.plugins.polymer.minifier.model.MiniElements;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.util.FilesUtils;

@Mojo(name = "minify")
public class MinifierMojo extends AbstractMojo {

	private static final Logger LOGGER = LogManager.getLogger(MinifierMojo.class);

	@Parameter(name = "index", required = true)
	private String index;

	@Parameter(name = "outputFolder", defaultValue = "target/polymer-minifier")
	private String outputFolder;

	@Parameter(name = "minifyJavascript", defaultValue = "true")
	private boolean minifyJavascript;

	@Parameter(name = "whiteOnlyJavascript", defaultValue = "true")
	private boolean whiteOnlyJavascript;

	@Parameter(name = "compileJavascript", defaultValue = "true")
	private boolean compileJavascript;

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

	@Parameter(name = "writeSingleFile", defaultValue = "true")
	private boolean writeSingleFile;

	/**
	 * minify CSS styles includes inside an HTML
	 */
	@Parameter(name = "minifyStyles", defaultValue = "true")
	private boolean minifyStyles;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		final Path indexPath = Paths.get(index);

		try {
			final ElementsMinifier minifier = createElementsMinifier();
			final MiniElements mini = minifier.minimize(indexPath);

			createTargetFolder();

			// todo standard index

			FilesUtils.write(Paths.get(outputFolder, "index.build.html"), mini.getBuildIndexContent());
			FilesUtils.write(Paths.get(outputFolder, "index.html"), mini.getMiniIndexContent());
			FilesUtils.write(Paths.get(outputFolder, mini.getBuildFileName()), mini.getBuilldContent());

			if (gzipElements) {
				FilesUtils.write(Paths.get(outputFolder, "index.build.html.gz"), mini.getBuildIndexContent());
				FilesUtils.writeGzip(Paths.get(outputFolder, "index.html.gz"), mini.getMiniIndexContent());
				FilesUtils.writeGzip(Paths.get(outputFolder, mini.getBuildFileName() + ".gz"), mini.getBuilldContent());
			}

		} catch (IOException | MinifierException e) {
			LOGGER.error(e);
			throw new MojoFailureException("Cannot build the polymer project", e);
		}
	}

	private ElementsMinifier createElementsMinifier() throws MinifierException {
		final Minifier no = new NoMinifier();

		final List<Minifier> minifiers = new ArrayList<>();
		if (minifyBlanks) {
			minifiers.add(new BlankMinifier());
		}

		if (minifyStyles) {
			minifiers.add(new CssMinifier());
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
			// minifiers.add(new JavascriptMinifier());
		}
		if (whiteOnlyJavascript) {
			minifiers.add(new WhiteOnlyJavascriptMinifier());
		} else if (compileJavascript) {
			minifiers.add(new JavascriptCompilerMinifier());
		}

		if (writeSingleFile) {
			final Path srcParentFolder = Paths.get(index).getParent();
			final Path target = Paths.get(outputFolder);
			final Minifier writeSingleFileMinifier = new WriteSingleFileMinifier(srcParentFolder, target,
					minifyPolymerName, gzipElements);
			minifiers.add(writeSingleFileMinifier);
		}

		final ElementsMinifier minifier = new ElementsMinifier(no, minifiers.toArray(new Minifier[minifiers.size()]));
		return minifier;
	}

	private void createTargetFolder() {
		File target = new File(outputFolder);
		if (!target.exists()) {
			target.mkdirs();
		}
	}

}
