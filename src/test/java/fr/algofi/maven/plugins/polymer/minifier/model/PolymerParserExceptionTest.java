package fr.algofi.maven.plugins.polymer.minifier.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class PolymerParserExceptionTest {

	@Test
	public void shouldSetMessageAndCause() {

		// input
		final String message = "foo";
		final Throwable cause = mock(Throwable.class);

		// call
		PolymerParserException exception = new PolymerParserException(message, cause);

		// assertions
		assertEquals("foo", exception.getMessage());
		assertEquals(cause, exception.getCause());

	}

}
