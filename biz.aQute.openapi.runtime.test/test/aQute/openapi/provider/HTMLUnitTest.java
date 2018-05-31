package aQute.openapi.provider;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import aQute.lib.io.IO;
import aQute.openapi.provider.cors.CORSImplementation;
import gen.cors.CorsBase;

public class HTMLUnitTest {

	@Rule
	public OpenAPIServerTestRule	rule	= new OpenAPIServerTestRule();

	Logger							l		= LoggerFactory.getLogger("Test");

	class X extends CorsBase {

		@Override
		protected void a_b_get() throws Exception {
			System.out.println("a_b_get");
		}

		@Override
		protected void a_put() throws Exception {
			System.out.println("a_put");

		}

		@Override
		protected void a_b_post() throws Exception {
			System.out.println("a_b_post");

		}

		@Override
		protected String a_get(String foo) throws Exception {
			System.out.println("a_get");
			return "a_get";
		}

		@Override
		protected String a_post() throws Exception {
			System.out.println("a_post");
			return "a_post";
		}

	}

	//@Test
	public void testSimple() throws Exception {
		System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "debug");
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

		rule.add(new X());
		rule.runtime.cors = new CORSImplementation(l, new String[] {}, new String[] {}, new String[] {}, true, -1);
		URL uri = rule.resolve("/routes/a?foo=ABC");
		File html = IO.getFile("resources/htmlunit/simple.html");
		File js = IO.getFile("resources/htmlunit/simple.js");

		String jsContent = IO.collect(js);
		jsContent = jsContent.replace("${url}", uri.toString());

		try (final WebClient webClient = new WebClient()) {
			webClient.getOptions().setUseInsecureSSL(true);

			final HtmlPage page = webClient.getPage(html.toURI().toURL());

			page.executeJavaScript(jsContent);

			while (true) {
				Object v = page.executeJavaScript("data").getJavaScriptResult();

				System.out.println(v);
				if (v instanceof Number) {
					assertEquals(((Number) v).intValue(), 1);
					return;
				}
				Thread.sleep(1000);
			}

		}

	}
}
