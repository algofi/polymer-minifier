package fr.algofi.maven.plugins.polymer.minifier;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MinifierUtilsTest {

	@Test
	public void shouldReturnEmptyScript() {
		// input
		final String content = "<script></script>";
		// call
		final String actualScript = MinifierUtils.extractScript(content);
		// assertions
		assertEquals("", actualScript);
	}

	@Test
	public void shouldReturnEmptyScriptWhenBlankScriptContent() {
		// input
		final String content = "<script>   </script>";
		// call
		final String actualScript = MinifierUtils.extractScript(content);
		// assertions
		assertEquals("", actualScript);
	}

	@Test
	public void shouldReturnInlineScriptWhenInlineScriptContent() {
		// input
		final String content = "<script>console.log( 'hello world' );</script>";
		// call
		final String actualScript = MinifierUtils.extractScript(content);
		// assertions
		assertEquals("console.log( 'hello world' );", actualScript);
	}

	@Test
	public void shouldReturnScriptWhenLongScriptStartTagPresent() {
		// input
		final String content = "<script type='text/javascript'>console.log( 'hello world' );</script>";
		// call
		final String actualScript = MinifierUtils.extractScript(content);
		// assertions
		assertEquals("console.log( 'hello world' );", actualScript);
	}

	@Test
	public void shouldReturnMultilineScriptWhenMultilineScript() {
		// input
		final String content = "<script type='text/javascript'>\n\n\r\nconsole.log( 'hello world' );</script>";
		// call
		final String actualScript = MinifierUtils.extractScript(content);
		// assertions
		assertEquals("console.log( 'hello world' );", actualScript);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNoEndTagWhenLongOpeningTag() {
		// input
		final String content = "<script type='text/javascript'>\n\n\r\nconsole.log( 'hello world' );";
		// call
		MinifierUtils.extractScript(content);
		
	}
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNoEndTagWhenShortOpeningTag() {
		// input
		final String content = "<script>\n\n\r\nconsole.log( 'hello world' );";
		// call
		MinifierUtils.extractScript(content);
		
	}

}
