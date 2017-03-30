package fr.algofi.maven.plugins.polymer.minifier;

import java.lang.reflect.Field;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

public class MinifierMojoIntegrationTest {

	private MinifierMojo sut;

	@Before
	public void setup() throws Exception {
		sut = new MinifierMojo();
		setFieldValue(sut, "index", "src/test/resources/my-app/index.html");
		setFieldValue(sut, "outputFolder", "src/test/resources/my-app/target/polymer-minifier-integration-test-mini");
		setFieldValue(sut, "gzipElements", Boolean.TRUE);
		setFieldValue(sut, "minifyBlanks", Boolean.TRUE);
		setFieldValue(sut, "minifyHtmlComments", Boolean.TRUE);
		setFieldValue(sut, "minifyProperties", Boolean.TRUE);
		setFieldValue(sut, "minifyPolymerName", Boolean.TRUE);
		setFieldValue(sut, "minifyJavascript", Boolean.TRUE);
		
		setFieldValue(sut, "whiteOnlyJavascript", Boolean.FALSE);
		setFieldValue(sut, "compileJavascript", Boolean.TRUE);
		
		setFieldValue(sut, "writeSingleFile", Boolean.TRUE);
	}

	private static void setFieldValue(MinifierMojo mojo, final String fieldName, final Object fieldValue)
			throws Exception {
		final Field field = mojo.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(mojo, fieldValue);
	}

	@Test
	public void shouldBuildAllElementsTogehterToProvideAnErrorLessWebPage() throws MojoExecutionException, MojoFailureException {

		
		// cal
		sut.execute();
		
	}

}
