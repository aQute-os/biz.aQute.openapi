package aQute.openapi.provider.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.osgi.framework.Bundle;

import aQute.bnd.osgi.FileResource;
import aQute.lib.io.IO;
import aQute.openapi.provider.resources.JUnitFramework.BundleBuilder;
import aQute.openapi.provider.resources.ResourceDomain.ResourceHolder;

public class BundleDomainTest {


	@Test
	public void testSimple() throws Exception {
		BundleDomain domain = new BundleDomain();
		domain.server = new ResourceDomain();
		try (JUnitFramework fw = new JUnitFramework()) {

			domain.activate(fw.context);

			assertEquals(0, domain.server.holders.size());

			BundleBuilder bundle = fw.bundle();
			bundle.setProperty("OpenAPI-Resources",
					"/resourcedomain;max-age=10;uncached=*.not;compressed=*.txt, foobar");
			bundle.addResource("resourcedomain/foo.txt",
					new FileResource(IO.getFile("resources/resourcedomain/foo.txt")));
			bundle.addResource("resourcedomain/foo.not",
					new FileResource(IO.getFile("resources/resourcedomain/foo.not")));
			Bundle install = bundle.install();
			install.start();

			assertEquals(2, domain.server.holders.size());
			ResourceHolder first = domain.server.holders.get(0);

			assertEquals(first.base, "/resourcedomain/");
			assertEquals(first.maxAge, 10);
			assertTrue(first.compress[0].matcher("x.txt").matches());
			assertFalse(first.compress[0].matcher("x.not").matches());
			assertFalse(first.uncached[0].matcher("x.txt").matches());
			assertTrue(first.uncached[0].matcher("x.not").matches());

			ResourceHolder second = domain.server.holders.get(1);
			assertEquals(second.maxAge, 600);
			assertEquals(0, second.compress.length);
			assertEquals(0, second.uncached.length);

			install.stop();

			assertEquals(0, domain.server.holders.size());
		}
	}
}
