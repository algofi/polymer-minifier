package fr.algofi.maven.plugins.polymer.minifier.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

import fr.algofi.maven.plugins.polymer.minifier.model.ScriptPart;

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
		MinifierUtils.extractScript(document);
	}

	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void shouldThrowExceptionIfNoEndTagWhenShortOpeningTag() {
		// input
		final String content = "<script>\n\n\r\nconsole.log( 'hello world' );";
		final Document document = Jsoup.parse(content);
		// call
		MinifierUtils.extractScript(document);
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
		assertEquals("a", MinifierUtils.propertyToAttribute("a"));
		assertEquals("sessionid", MinifierUtils.propertyToAttribute("sessionid"));
		assertEquals("session-id", MinifierUtils.propertyToAttribute("sessionId"));
		assertEquals("session-id", MinifierUtils.propertyToAttribute("SessionId"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionIfNameIsNull() {
		MinifierUtils.propertyToAttribute(null);
	}

	@Test
	public void shouldFindMultiLineHtmlTag() throws IOException {
		final String path = "src/test/resources/minifier/x-dep-properties.html";
		final String content = Files.readAllLines(Paths.get(path)).stream().collect(Collectors.joining("\n"));

		// input tag :
		final String tagName = "x-five-properties";

		// call
		final List<String> tags = MinifierUtils.findHtmlTags(tagName, content);

		assertNotNull(tags);
		assertFalse(tags.isEmpty());
		assertEquals(
				"<x-five-properties session-id=\"[[sessionId]]\"\n" + "\t\t\tuser-id=\"[[userId]]\"\n"
						+ "\t\t\tposts=\"{{posts}}\">\n" + "\t\t\t\n" + "\t\t\t</x-five-properties>",
				tags.iterator().next());
	}

	@Test
	public void shouldRemovePolymerBehaviorArray() {
		// input
		final List<String> contentLines = new ArrayList<>();
		contentLines.add("Polymer( {                       ");
		contentLines.add("	is: 'x-main',                  ");
		contentLines.add("	properties: {                  ");
		contentLines.add("		userId: String,            ");
		contentLines.add("		friends: {                 ");
		contentLines.add("			type: Array,           ");
		contentLines.add("			value: [               ");
		contentLines.add("			    { name: 'Alice'},  ");
		contentLines.add("			    { name: 'Bob'},    ");
		contentLines.add("			    { name: 'Charles'} ");
		contentLines.add("			]                      ");
		contentLines.add("		}                          ");
		contentLines.add("	},                             ");
		contentLines.add("	behaviors: [                   ");
		contentLines.add("	    MyCustomBehavior           ");
		contentLines.add("	],                             ");
		contentLines.add("	ready: function () {           ");
		contentLines.add("		this.debug();              ");
		contentLines.add("	}                              ");
		contentLines.add("} );                             ");

		final String content = contentLines.stream().collect(Collectors.joining("\n"));

		// call
		final String actualContent = MinifierUtils.removePolymerBehaviors(content);

		// expected
		final List<String> expectedContentLines = new ArrayList<>();
		expectedContentLines.add("Polymer( {                       ");
		expectedContentLines.add("	is: 'x-main',                  ");
		expectedContentLines.add("	properties: {                  ");
		expectedContentLines.add("		userId: String,            ");
		expectedContentLines.add("		friends: {                 ");
		expectedContentLines.add("			type: Array,           ");
		expectedContentLines.add("			value: [               ");
		expectedContentLines.add("			    { name: 'Alice'},  ");
		expectedContentLines.add("			    { name: 'Bob'},    ");
		expectedContentLines.add("			    { name: 'Charles'} ");
		expectedContentLines.add("			]                      ");
		expectedContentLines.add("		}                          ");
		expectedContentLines.add("	},                             ");
		expectedContentLines.add("	                             ");
		expectedContentLines.add("	ready: function () {           ");
		expectedContentLines.add("		this.debug();              ");
		expectedContentLines.add("	}                              ");
		expectedContentLines.add("} );                             ");

		final String expectedContent = expectedContentLines.stream().collect(Collectors.joining("\n"));

		// assertions
		assertNotNull(actualContent);
		assertEquals(expectedContent, actualContent);

	}

	@Test
	public void testPaths() {
		final Path src = Paths.get("src", "main", "webapp");
		final Path target = Paths.get("target", "polymer-minimizer");
		
		final Path componentPath = Paths.get("src", "main", "webapp", "my-element", "my-element.html"); 
		
		final Path actual = src.relativize(target).resolve(src.relativize(componentPath));
		
		System.out.println( actual );
	}

}
