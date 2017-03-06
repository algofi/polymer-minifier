package fr.algofi.maven.plugins.polymer.minifier;

import java.lang.reflect.Field;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

public class MinifierMojoIntegrationTest {

	// TODO have a simple Jetty static server for testing purpose

	private MinifierMojo sut;

	@Before
	public void setup() throws Exception {
		sut = new MinifierMojo();
		setFieldValue(sut, "index", "../my-app/index.html");
		setFieldValue(sut, "outputFolder", "../my-app/target/polymer-plugin");
		setFieldValue(sut, "gzipElements", Boolean.FALSE);
		setFieldValue(sut, "minifyBlanks", Boolean.FALSE);
		setFieldValue(sut, "minifyHtmlComments", Boolean.FALSE);
		setFieldValue(sut, "minifyProperties", Boolean.FALSE);
		setFieldValue(sut, "minifyPolymerName", Boolean.FALSE);
		setFieldValue(sut, "minifyPolymerName", Boolean.FALSE);
		setFieldValue(sut, "minifyJavascript", Boolean.FALSE);
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
