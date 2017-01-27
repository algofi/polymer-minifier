package fr.algofi.maven.plugins.polymer.minifier;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

	public static String readContent(final String path) throws IOException {
		final byte[] bytes = Files.readAllBytes(Paths.get(path));
		return new String(bytes, Charset.defaultCharset());
	}
}
