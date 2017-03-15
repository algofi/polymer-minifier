package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;
import fr.algofi.maven.plugins.polymer.minifier.util.FilesUtils;

/**
 * <p>
 * write the polymer component into a single file.
 * <p>
 * The component output path is taken from the component's path relative to the
 * source folder. The component root path is <tt>target</tt> in the constructor.
 * <p>
 * The elements may be gzipped as well.
 * <p>
 * Out of any compression, the component is written twice : once with the
 * standard file name, once with its mini component name as a file name. In the
 * component source, the standard import link contains its mini name. However,
 * sometimes, the import may use polymer <tt>importHref</tt> with a dynamically
 * built URL. To handle this use case, we will also save the component in with
 * it standard file name.
 * 
 * @author cjechoux
 *
 */
public class WriteSingleFileMinifier implements Minifier {

	private static final Logger LOGGER = LogManager.getLogger(WriteSingleFileMinifier.class);

	private Path srcParentFolder;
	private Path target;
	private boolean gzipElements;
	private boolean minifyPolymerName;

	public WriteSingleFileMinifier(final Path srcParentFolder, final Path target, final boolean minifyPolymerName,
			final boolean gzipElements) {
		this.srcParentFolder = srcParentFolder;
		this.target = target;
		this.gzipElements = gzipElements;
		this.minifyPolymerName = minifyPolymerName;
	}

	@Override
	public void minimize(PolymerComponent component, Collection<PolymerComponent> dependencies)
			throws MinifierException {
		try {

			// 1/ standard filename
			String componentFileName = getStandardFileName(component, false);
			Path path = buildOutputFilePath(component, componentFileName);
			LOGGER.debug("Writing " + path);
			FilesUtils.write(path, component.getMinifiedContent());

			// 2/ standard filename gzipped
			if (gzipElements) {
				componentFileName = getStandardFileName(component, true);
				path = buildOutputFilePath(component, componentFileName);
				LOGGER.debug("Writing " + path);
				FilesUtils.writeGzip(path, component.getMinifiedContent());
			}

			if (minifyPolymerName) {
				// 3/ mini file name
				Optional<String> miniFileName = getMiniFileName(component, false);
				if (miniFileName.isPresent()) {
					path = buildOutputFilePath(component, miniFileName.get());
					LOGGER.debug("Writing " + path);
					FilesUtils.write(path, component.getMinifiedContent());
				}
				// 4/ mini file name gzipped
				if (gzipElements) {
					miniFileName = getMiniFileName(component, true);
					if (miniFileName.isPresent()) {
						path = buildOutputFilePath(component, miniFileName.get());
						LOGGER.debug("Writing " + path);
						FilesUtils.writeGzip(path, component.getMinifiedContent());
					}
				}
			}

		} catch (IOException e) {
			throw new MinifierException(
					"Cannot write the component " + component.getName() + " from " + component.getPath(), e);
		}

	}

	private Path buildOutputFilePath(final PolymerComponent component, final String componentFileName) {

		final Path rootComponentPath = Paths.get(".", component.getPath().replace(srcParentFolder.toString(), ""));

		Path outputFilePath = Paths.get(target.toString(), rootComponentPath.toString(), "..", componentFileName)
				.normalize();

		return outputFilePath;
	}

	/**
	 * return a standard file name
	 * 
	 * @param component
	 * @param gzip
	 * @return
	 */
	private String getStandardFileName(PolymerComponent component, boolean gzip) {
		String componentFileName = Paths.get(component.getPath()).toFile().getName();
		if (gzip) {
			componentFileName += ".gz";
		}

		return componentFileName;
	}

	/**
	 * return a minifed file name
	 * 
	 * @param component
	 * @param gzip
	 * @return
	 */
	private Optional<String> getMiniFileName(PolymerComponent component, boolean gzip) {

		if (component.getMiniName() == null) {
			return Optional.empty();
		}

		final String extension = component.getPath().substring(component.getPath().lastIndexOf("."));
		String componentFileName = component.getMiniName() + extension;
		if (gzip) {
			componentFileName += ".gz";
		}

		return Optional.of(componentFileName);
	}

}
