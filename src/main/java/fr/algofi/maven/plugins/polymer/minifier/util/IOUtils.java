package fr.algofi.maven.plugins.polymer.minifier.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fr.algofi.maven.plugins.polymer.minifier.model.MinifierException;

public class IOUtils {

	public static String hash(InputStream inputStream, final String algorithm) throws MinifierException {

		final byte[] buffer = new byte[1024];

		try {
			final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			int numread = 0;
			do {
				numread = inputStream.read(buffer);
				if (numread > 0) {
					messageDigest.update(buffer, 0, numread);
				}
			} while (numread != -1);

			final byte[] hash = messageDigest.digest();

			// create hash String
			final StringBuffer hashBuilder = new StringBuffer();
			for (byte b : hash) {
				hashBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}

			return hashBuilder.toString();

		} catch (IOException | NoSuchAlgorithmException e) {
			throw new MinifierException("Cannot compute HASH", e);
		}

	}

	public static String md5Sum(InputStream inputStream) throws MinifierException {
		return hash(inputStream, "MD5");
	}

}
