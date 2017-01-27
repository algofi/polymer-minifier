package fr.algofi.maven.plugins.polymer.minifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.MiniPropertyProvider;

public class MiniPropertyProviderTest {

	private MiniPropertyProvider sut;

	@Before
	public void setup() {
		sut = new MiniPropertyProvider();
	}

	@Test
	public void shouldReturnALongList() {

		final List<String> miniPropertyNames = sut.provide();

		// assertions
		assertNotNull(miniPropertyNames);
		assertEquals(26 * 27, miniPropertyNames.size());
		final String[] expectedValues = "abcdefghijklmnopqrstuvwxyz".split("");
		for (int i = 0; i < 26; i++) {
			assertEquals(expectedValues[i], miniPropertyNames.get(i));
		}
		assertEquals("aa", miniPropertyNames.get(26));
		assertEquals("ab", miniPropertyNames.get(27));
		assertEquals("ac", miniPropertyNames.get(28));
		assertEquals("ad", miniPropertyNames.get(29));
		assertEquals("ae", miniPropertyNames.get(30));
	}

}
