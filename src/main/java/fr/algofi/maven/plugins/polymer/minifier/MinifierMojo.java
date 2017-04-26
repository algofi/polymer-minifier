package fr.algofi.maven.plugins.polymer.minifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
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
import fr.algofi.maven.plugins.polymer.minifier.commands.ResourceMinifier;
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

	@Parameter(name = "gzipElements", defaultValue = "true")
	private boolean gzipElements;

	@Parameter(name = "resources")
	private List<Resource> resources;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		final Path indexPath = Paths.get(index);
		createTargetFolder();

		try {

			final ElementsMinifier minifier = createElementsMinifier(resources);
			final MiniElements mini = minifier.minimize(indexPath);

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

	private ElementsMinifier createElementsMinifier(List<Resource> resources) throws MinifierException {

		final List<Minifier> elementsMinifier = new ArrayList<>();

		for (Resource resource : resources) {

			final Minifier no = new NoMinifier();

			final List<PathMatcher> includes = new ArrayList<>(); // FIXME
			final List<PathMatcher> excludes = new ArrayList<>(); // FIXME

			final List<Minifier> minifiers = new ArrayList<>();
			
			minifiers.add(no);
			
			
			if (resource.isMinifyBlanks()) {
				minifiers.add(new ResourceMinifier(new BlankMinifier(), includes, excludes));
			}

			if (resource.isMinifyStyles()) {
				minifiers.add(new ResourceMinifier(new CssMinifier(), includes, excludes));
			}

			if (resource.isMinifyHtmlComments()) {
				minifiers.add(new ResourceMinifier(new HTMLCommentMinifier(), includes, excludes));
			}
			if (resource.isMinifyProperties()) {
				minifiers.add(new ResourceMinifier(new PolymerPropertiesMinifier(), includes, excludes));
			}
			if (resource.isMinifyPolymerName()) {
				minifiers.add(new ResourceMinifier(new PolymerNameMinifier(), includes, excludes));
			}
			if (resource.isMinifyJavascript()) {
				minifiers.add(new ResourceMinifier(new JavascriptPropertiesMinifier(), includes, excludes));
				// minifiers.add(new JavascriptMinifier(), includes, excludes
				// ));
			}
			if (resource.isWhiteOnlyJavascript()) {
				minifiers.add(new ResourceMinifier(new WhiteOnlyJavascriptMinifier(), includes, excludes));
			} else if (resource.isCompileJavascript()) {
				minifiers.add(new ResourceMinifier(new JavascriptCompilerMinifier(), includes, excludes));
			}

			if (resource.isWriteSingleFile()) {
				final Path srcParentFolder = Paths.get(index).getParent();
				final Path target = Paths.get(outputFolder);
				final Minifier writeSingleFileMinifier = new WriteSingleFileMinifier(srcParentFolder, target,
						resource.isMinifyPolymerName(), resource.isGzipElements());
				minifiers.add(writeSingleFileMinifier);
			}
			
			
			final Minifier listMinifier = new ListMinifier(minifiers);
			
			elementsMinifier.add(listMinifier);
			
		}


		final ElementsMinifier minifier = new ElementsMinifier(elementsMinifier);
		return minifier;
	}

	private void createTargetFolder() {
		File target = new File(outputFolder);
		if (!target.exists()) {
			target.mkdirs();
		}
	}

}
