package fr.algofi.maven.plugins.polymer.minifier;

import static fr.algofi.maven.plugins.polymer.minifier.MinifierUtils.propertyToAttribute;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

public class MinifierUtilsTest {

	@Test
	public void shouldReturnEmptyScript() {
		// input
		final String content = "<script></script>";
		final Document document = Jsoup.parse(content);
		// call
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		// assertions
		assertEquals("", scriptPart.getScript());
		// assertEquals(8, scriptPart.getStart());
		// assertEquals(8, scriptPart.getEnd());
	}

	@Test
	public void shouldReturnEmptyScriptWhenBlankScriptContent() {
		// input
		final String content = "<script>   </script>";
		final Document document = Jsoup.parse(content);
		// call
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		// assertions
		assertEquals("", scriptPart.getScript());
		// assertEquals(8, scriptPart.getStart());
		// assertEquals(11, scriptPart.getEnd());
	}

	@Test
	public void shouldReturnInlineScriptWhenInlineScriptContent() {
		// input
		final String content = "<script>console.log( 'hello world' );</script>";
		final Document document = Jsoup.parse(content);
		// call
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		// assertions
		assertEquals("console.log( 'hello world' );", scriptPart.getScript());
		// assertEquals(8, scriptPart.getStart());
		// assertEquals(37, scriptPart.getEnd());
	}

	@Test
	public void shouldReturnScriptWhenLongScriptStartTagPresent() {
		// input
		final String content = "<script type='text/javascript'>console.log( 'hello world' );</script>";
		final Document document = Jsoup.parse(content);
		// call
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		// assertions
		assertEquals("console.log( 'hello world' );", scriptPart.getScript());
		// assertEquals(30, scriptPart.getStart());
		// assertEquals(60, scriptPart.getEnd());
	}

	@Test
	public void shouldReturnMultilineScriptWhenMultilineScript() {
		// input
		final String content = "<script type='text/javascript'>\n\n\r\nconsole.log( 'hello world' );</script>";
		final Document document = Jsoup.parse(content);
		// call
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		// assertions
		assertEquals("console.log( 'hello world' );", scriptPart.getScript());
		// assertEquals(30, scriptPart.getStart());
		// assertEquals(64, scriptPart.getEnd());
	}

	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void shouldThrowExceptionIfNoEndTagWhenLongOpeningTag() {
		// input
		final String content = "<script type='text/javascript'>\n\n\r\nconsole.log( 'hello world' );";
		final Document document = Jsoup.parse(content);
		// call
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
	}

	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void shouldThrowExceptionIfNoEndTagWhenShortOpeningTag() {
		// input
		final String content = "<script>\n\n\r\nconsole.log( 'hello world' );";
		final Document document = Jsoup.parse(content);
		// call
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
	}

	@Test
	public void shouldExtractAScriptFromPolymerElements() throws IOException {
		// expected
		final String expectedPath = "src/test/resources/extractor/x-no-properties_script_only.js";
		final String expectedJavascript = Files.readAllLines(Paths.get(expectedPath)).stream()
				.collect(Collectors.joining("\n"));
		// input
		final String path = "src/test/resources/extractor/x-no-properties.html";
		final String content = Files.readAllLines(Paths.get(path)).stream().collect(Collectors.joining("\n"));
		final Document document = Jsoup.parse(content);
		// call
		final ScriptPart scriptPart = MinifierUtils.extractScript(document);
		// assertions
		assertEquals(expectedJavascript, scriptPart.getScript());
	}

	@Test
	public void shouldConvertAPropertyToAnAttributeName() {
		assertEquals("a", propertyToAttribute("a"));
		assertEquals("sessionid", propertyToAttribute("sessionid"));
		assertEquals("session-id", propertyToAttribute("sessionId"));
		assertEquals("session-id", propertyToAttribute("SessionId"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionIfNameIsNull() {
		propertyToAttribute(null);
	}

}
