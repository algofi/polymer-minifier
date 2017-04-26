package fr.algofi.maven.plugins.polymer.minifier;

import java.lang.reflect.Field;
import java.util.Arrays;

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
		
		final Resource resource = new Resource();
		resource.setIncludes(Arrays.asList("**/*"));
		resource.setMinifyBlanks(true);
		resource.setMinifyHtmlComments(true);
		resource.setMinifyProperties(true);
		resource.setMinifyPolymerName(true);
		resource.setMinifyJavascript(true);
		resource.setWhiteOnlyJavascript(false);
		resource.setCompileJavascript(true);
		resource.setWriteSingleFile(true);
		
		setFieldValue(sut, "resources", Arrays.asList(resource));
		
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
