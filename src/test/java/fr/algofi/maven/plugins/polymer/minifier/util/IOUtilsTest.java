package fr.algofi.maven.plugins.polymer.minifier.util;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;

public class IOUtilsTest {

	@Test
	public void shouldComputeTheChecksumOfAnInputStream() throws MinifierException {
		// input
		final InputStream inputStream = getClass().getResourceAsStream("/hello-world.txt");

		// call
		final String actualMd5Hash = IOUtils.md5Sum(inputStream);

		// assertions
		final String expectedMd5Hash = "ed076287532e86365e841e92bfc50d8c";
		assertEquals(expectedMd5Hash, actualMd5Hash);

	}

}
