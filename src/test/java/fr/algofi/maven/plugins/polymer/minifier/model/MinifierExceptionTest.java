package fr.algofi.maven.plugins.polymer.minifier.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class MinifierExceptionTest {

	@Test
	public void shouldSetMessageAndCause() {

		// input
		final String message = "foo";
		final Throwable cause = mock(Throwable.class);

		// call
		MinifierException exception = new MinifierException(message, cause);

		// assertions
		assertEquals("foo", exception.getMessage());
		assertEquals(cause, exception.getCause());

	}

	@Test
	public void shouldSetMessage() {

		// input
		final String message = "foo";

		// call
		MinifierException exception = new MinifierException(message);

		// assertions
		assertEquals("foo", exception.getMessage());

	}

}
