package fr.algofi.maven.plugins.polymer.minifier.commands;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.algofi.maven.plugins.polymer.minifier.ListMinifier;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

/**
 * This is a minifier adapater.
 * <p>
 * The minifier adapted will be executed in the case the component is not
 * excluded by the path matcher list <tt>excludes</tt> and is included by the
 * path matcher list <tt>includes</tt>
 * 
 * @author cjechoux
 *
 */
public class ResourceMinifier implements Minifier {

	private static final Logger LOGGER = LogManager.getLogger(ListMinifier.class);

	private final Minifier minifier;
	private final List<PathMatcher> includes;
	private final List<PathMatcher> excludes;

	/**
	 * Constructor
	 * 
	 * @param minifier
	 *            minifier to execute
	 * @param includes
	 *            list of path matcher to include a resource in case of match
	 * @param excludes
	 *            list of path matcher to exclude a resource in case of match
	 */
	public ResourceMinifier(final Minifier minifier, final List<PathMatcher> includes,
			final List<PathMatcher> excludes) {
		this.minifier = minifier;
		this.includes = includes == null ? Collections.emptyList() : includes;
		this.excludes = excludes == null ? Collections.emptyList() : excludes;
	}

	@Override
	public void minimize(PolymerComponent component, Collection<PolymerComponent> dependencies)
			throws MinifierException {

		final String path = component.getPath();

		if (!exclude(path) && include(path)) {
			minifier.minimize(component, dependencies);
		}

	}

	/**
	 * return true in the case one <tt>includes</tt> path matcher is matching.
	 * 
	 * @param pathName
	 * @return true if including the resource
	 */
	private boolean include(String pathName) {

		final Path path = Paths.get(pathName);

		for (PathMatcher pathMatcher : includes) {
			if (pathMatcher.matches(path)) {
				return true;
			}
		}
		// if empty list or none match, return false
		LOGGER.info("No resource match the path : " + pathName + " toward includes : " + includes);
		return false;
	}

	/**
	 * return true in the case one <tt>excludes</tt> path matcher is matching.
	 * 
	 * @param pathName
	 * @return true if excluding the resource
	 */
	private boolean exclude(String pathName) {
		
		final Path path = Paths.get(pathName);
		
		for (PathMatcher pathMatcher : excludes) {
			if (pathMatcher.matches(path)) {
				return true;
			}
		}
		// if empty list or none match, return false
		LOGGER.info("No resource match the path : " + pathName + " toward includes : " + includes);
		return false;
	}

}

