package ch.adoray.scotty.integrationtest.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class ResourceLoader {
	final static String RES_FOLDER_SKIPPING_PACKAGES = "ch.adoray.scotty.integrationtest.";

	public static String loadTestData() throws IOException {
		String path = determineResPath(Thread.currentThread().getStackTrace());
		path += ".txt";
		InputStream stream = ResourceLoader.class.getClassLoader()
				.getResourceAsStream(path);
		if(stream == null){
			throw new IllegalStateException("A resource is expected at " + path);
		}
		StringWriter writer = new StringWriter();
		IOUtils.copy(stream, writer);
		return writer.toString();

	}

	private static String determineResPath(StackTraceElement[] st) {
		StackTraceElement traceElement = st[2];
		String path = traceElement.getClassName();
		path = path.replace(RES_FOLDER_SKIPPING_PACKAGES, "").replace(".", "/");
		path += "_" + traceElement.getMethodName();
		return path;
	}
}
