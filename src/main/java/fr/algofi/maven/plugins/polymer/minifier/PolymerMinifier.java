package fr.algofi.maven.plugins.polymer.minifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.algofi.maven.plugins.polymer.minifier.commands.Minifier;
import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class PolymerMinifier {

	private static final Logger LOGGER = LogManager.getLogger(PolymerMinifier.class);

	// private Collection<PolymerComponent> dependencies = new ArrayList<>();
	private final List<Minifier> minifiers;

	private Minifier firstMinifier;

	public PolymerMinifier(Minifier minifier, Minifier... minifiers) {
		this.firstMinifier = minifier;

		this.minifiers = new ArrayList<>();
		if (minifiers != null) {
			this.minifiers.addAll(Arrays.asList(minifiers));
		}
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
	public void minify(final PolymerComponent polymer, final Collection<PolymerComponent> dependencies)
			throws MinifierException {

		LOGGER.info("Minimizing: " + polymer.getPath());

		firstMinifier.minimize(polymer, dependencies);

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
