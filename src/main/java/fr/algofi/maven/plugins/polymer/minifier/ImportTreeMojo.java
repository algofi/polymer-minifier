package fr.algofi.maven.plugins.polymer.minifier;
import static java.nio.file.FileVisitResult.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "import-tree")
public class ImportTreeMojo extends AbstractMojo {

	private static final Logger LOGGER = LogManager.getLogger(ImportTreeMojo.class);

	@Parameter(name = "root", required = true)
	private String root;

	@Parameter(name = "includes", required = true)
	private List<String> includes;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		final Path path = Paths.get(root);

		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes fileAttributes) throws IOException {
					return _visitFile(path, fileAttributes);
				}
			});
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot visite the directory " + root, e);
		}

	}

	protected FileVisitResult _visitFile(final Path path, final BasicFileAttributes fileAttributes) {

		
		
		
		return CONTINUE;
	}

}
