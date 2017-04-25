package fr.algofi.maven.plugins.polymer.minifier.util;

import static org.junit.Assert.assertTrue;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.junit.Test;

public class PathUtilsTest {

	@Test
	public void shouldMatchPath() {

		// path matcher glob syntax :
		// https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob

		// regex syntax
		// https://docs.oracle.com/javase/tutorial/essential/regex/index.html

		String syntaxAndPattern = "glob:**/*";
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(syntaxAndPattern);

		boolean match = pathMatcher.matches(Paths.get("src", "main", "webapp", "index.html"));

		assertTrue(match);

	}
}
