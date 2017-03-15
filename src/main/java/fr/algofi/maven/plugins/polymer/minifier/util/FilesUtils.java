package fr.algofi.maven.plugins.polymer.minifier.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public class FilesUtils {
	private FilesUtils() {
	}

	/**
	 * write the content into a gzipped file
	 * 
	 * @param path
	 *            path of the archive
	 * @param content
	 *            content to write
	 * @throws IOException
	 *             if any error occurs
	 */
	public static void writeGzip(Path path, String content) throws IOException {

		// we write on a file
		try (final OutputStream fileOutpuStream = new FileOutputStream(path.toFile())) {
			// the file stream is gzipped
			try (final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutpuStream)) {
				// we write this string content
				try (final PrintWriter writer = new PrintWriter(gzipOutputStream)) {
					writer.write(content);
				}
			}
		}

	}

	/**
	 * Write the content into an index file
	 * 
	 * @param path
	 *            file path
	 * @param content
	 *            content to write into the file
	 * @throws FileNotFoundException
	 */
	public static void write(final Path path, final String content) throws FileNotFoundException {

		// if the parent folders does not exist, then create the parents folder
		if (!path.getParent().toFile().exists()) {
			path.getParent().toFile().mkdirs();
		}

		try (final PrintWriter writer = new PrintWriter(path.toFile())) {
			writer.print(content);
		}
	}
}
