package fr.algofi.maven.plugins.polymer.minifier.commands;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;
import fr.algofi.maven.plugins.polymer.minifier.model.PolymerComponent;

public class ResourceMinifierTest {

	private ResourceMinifier sut;

	@Test
	public void shouldNotMinifyInNullIncludesList() throws MinifierException {

		// requirements
		PolymerComponent component = mock(PolymerComponent.class);
		when(component.getPath()).thenReturn("src/index.html");
		Collection<PolymerComponent> dependencies = Collections.emptyList();

		final Minifier minifierMock = mock(Minifier.class);

		// inputs
		List<PathMatcher> includes = null;
		List<PathMatcher> excludes = null;
		sut = new ResourceMinifier(minifierMock, includes, excludes);

		// call
		sut.minimize(component, dependencies);

		// assertions
		verify(minifierMock, times(0)).minimize(component, dependencies);

	}

	@Test
	public void shouldNotMinifyInEmptyIncludesList() throws MinifierException {

		// requirements
		PolymerComponent component = mock(PolymerComponent.class);
		when(component.getPath()).thenReturn("src/index.html");
		Collection<PolymerComponent> dependencies = Collections.emptyList();

		final Minifier minifierMock = mock(Minifier.class);

		// inputs
		List<PathMatcher> includes = Collections.emptyList();
		List<PathMatcher> excludes = null;
		sut = new ResourceMinifier(minifierMock, includes, excludes);

		// call
		sut.minimize(component, dependencies);

		// assertions
		verify(minifierMock, times(0)).minimize(component, dependencies);

	}

	@Test
	public void shouldNotMinifyInExcludedElement() throws MinifierException {

		// requirements
		PolymerComponent component = mock(PolymerComponent.class);
		when(component.getPath()).thenReturn("src/index.html");
		Collection<PolymerComponent> dependencies = Collections.emptyList();

		final Minifier minifierMock = mock(Minifier.class);

		// inputs
		List<PathMatcher> includes = Arrays.asList(FileSystems.getDefault().getPathMatcher("glob:**/cp*.html"));
		List<PathMatcher> excludes = Arrays.asList(FileSystems.getDefault().getPathMatcher("glob:**/i*.html"));
		sut = new ResourceMinifier(minifierMock, includes, excludes);

		// call
		sut.minimize(component, dependencies);

		// assertions
		verify(minifierMock, times(0)).minimize(component, dependencies);

		component = mock(PolymerComponent.class);
		when(component.getPath()).thenReturn("src/cp-23.html");

		// call
		sut.minimize(component, dependencies);

		// assertions
		verify(minifierMock, times(1)).minimize(component, dependencies);

	}

}
