package fr.algofi.maven.plugins.polymer.minifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

	private Executor executor;

	public PolymerMinifier(Minifier minifier, Minifier... minifiers) {
		this.firstMinifier = minifier;

		this.minifiers = new ArrayList<>();
		if (minifiers != null) {
			this.minifiers.addAll(Arrays.asList(minifiers));
		}

		this.executor = Executors.newFixedThreadPool(10);
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

		for (final Minifier minifier : minifiers) {
			// CompletableFuture.runAsync( () -> minifier.minimize(polymer,
			// dependencies) ).ex;
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
