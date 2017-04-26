
package fr.algofi.maven.plugins.polymer.minifier;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

/**
 * it runs a sublist of minifier
 * @author cjechoux
 *
 */
public class ListMinifier implements Minifier {

	private static final Logger LOGGER = LogManager.getLogger(ListMinifier.class);

	private final List<Minifier> minifiers;


	public ListMinifier( List<Minifier> minifiers) {
		this.minifiers = minifiers;
	}

	/**
	 * minify a polymer component
	 * 
	 * @param polymer
	 *            component to minify
	 * @param dependencies
	 *            dependencies for this component
	 * @throws MinifierException
	 *             thrown if we cannot minify a component
	 */
	@Override
	public void minimize(final PolymerComponent polymer, final Collection<PolymerComponent> dependencies)
			throws MinifierException {

		LOGGER.info("Minimizing: " + polymer.getPath());

		for (Minifier minifier : minifiers) {
			minifier.minimize(polymer, dependencies);
		}

	}

	public void addMinifier(final Minifier minifier) {
		this.minifiers.add(minifier);
	}

	public void removeMinifier(final Minifier minifier) {
		this.minifiers.remove(minifier);
	}


}
